package ca.canada.treasury.testbed.indexer.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.iterators.PeekingIterator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.text.WordUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.canada.treasury.testbed.indexer.Categorizer;
import ca.canada.treasury.testbed.indexer.Categorizer.CategoryMapping;
import ca.canada.treasury.testbed.indexer.IndexerUtil;
import ca.canada.treasury.testbed.indexer.RecallField;

/**
 * <p>
 * Index Transport Canada Vehicle Recall data from obtained from Open Data:
 * https://open.canada.ca/data/en/dataset/1ec92326-47ef-4110-b7ca-959fab03f96d
 * </p>
 * <p>
 * The data file format is expected to be CSV, with the columns
 * matching the order of {@link Column} elements.
 * </p>
 * <p>
 * Not all fields are indexed.
 * </p>
 * <p>
 * The data file can optionally be compressed (GZip).
 * </p>
 *
 * @author Pascal Essiembre
 */
public class OpenDataVehicleRecallsIndexer {

    private static final Logger LOG =
            LoggerFactory.getLogger(OpenDataVehicleRecallsIndexer.class);

    public static final int DEFAULT_BATCH_SIZE = 1000;
    public static final int DEFAULT_MAX = -1;
    public static final int COMMIT_WITHIN_MS = 5 * 60 * 1000;
    public static final String ARG_SOLR = "solr";
    public static final String ARG_FILE = "file";
    public static final String ARG_BATCH = "batch";
    public static final String ARG_MAX = "max";
    public static final String ARG_SKIP_COMMIT = "skipCommit";


    // Using ordinal position for CSV order.
    public enum Column {
        RECALL_NUMBER_NUM("recall_no"),
        YEAR("vhcl_years"),
        MANUFACTURER_RECALL_NO_TXT("N/A"),
        CATEGORY_ETXT("recall_categories"),
        CATEGORY_FTXT("N/A"),
        MAKE_NAME_NM("vhcl_make"),
        MODEL_NAME_NM("vhcl_model"),
        UNIT_AFFECTED_NBR("vhcl_nbr_affected"),
        SYSTEM_TYPE_ETXT("vhcl_system_type"),
        SYSTEM_TYPE_FTXT("N/A"),
        NOTIFICATION_TYPE_ETXT("vhcl_notif_type"),
        NOTIFICATION_TYPE_FTXT("N/A"),
        COMMENT_ETXT("recall_desc"),
        COMMENT_FTXT("N/A"),
        RECALL_DATE_DTE("recall_date");

        private final String solrField;
        private Column(String solrField) {
            this.solrField = solrField;
        }
        @Override
        public String toString() {
            return solrField;
        }
    }

    private final Config config;
    private final Map<String, MutableInt> idCounts = new HashMap<>();

    public OpenDataVehicleRecallsIndexer(Config config) {
        super();
        this.config = config;
    }

    public static void main(String[] args)
            throws IOException, SolrServerException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(Option.builder(ARG_SOLR).desc(
                "Solr URL (e.g. http://localhost:8983/solr)")
                .hasArg().required().build());
        options.addOption(Option.builder(ARG_FILE).desc(
                "path to sample data file").hasArg().required().build());
        options.addOption(Option.builder(ARG_BATCH).desc(
                "document batch size for indexing (default "
                        + DEFAULT_BATCH_SIZE + ")")
                .hasArg().build());
        options.addOption(Option.builder(ARG_MAX).desc(
                "maximum number of documents to send for indexing "
              + "(default: unlimited).")
                .hasArg().build());
        options.addOption(Option.builder(ARG_SKIP_COMMIT).desc(
                "Skip hard commit to Solr.").build());

        try {
            CommandLine line = parser.parse(options, args);
            Config cfg = new Config();
            cfg.file = new File(line.getOptionValue(ARG_FILE));
            cfg.solrURL = line.getOptionValue(ARG_SOLR);
            cfg.batchSize = NumberUtils.toInt(
                    line.getOptionValue(ARG_BATCH), DEFAULT_BATCH_SIZE);
            cfg.max = NumberUtils.toInt(
                    line.getOptionValue(ARG_MAX), DEFAULT_MAX);
            cfg.skipCommit = line.hasOption(ARG_SKIP_COMMIT);

            LOG.info("Indexing \"{}\" into \"{}\"", cfg.file, cfg.solrURL);

            new OpenDataVehicleRecallsIndexer(cfg).start();
        } catch (ParseException exp) {
            LOG.error(exp.getMessage());
            new HelpFormatter().printHelp("<this_app>", options);
        }
    }

    public void start() throws IOException, SolrServerException {

        StopWatch timer = new StopWatch();
        timer.start();

        HttpSolrClient solr =
                new HttpSolrClient.Builder(config.solrURL).build();

        List<SolrInputDocument> docList = new ArrayList<>();
        MutableLong cnt = new MutableLong();

        PeekingIterator<CSVRecord> it = (PeekingIterator<CSVRecord>)
                IteratorUtils.peekingIterator(
                        CSVFormat.RFC4180.withHeader().parse(
                                IndexerUtil.toReader(config.file)).iterator());
        while (it.hasNext()) {
            CSVRecord rec = it.next();
            Set<String> cats = new TreeSet<>();
            mergeCategoriesForDuplicates(it, rec, cats);

            if (isRecordValid(rec)) {
                docList.add(toSolrDoc(rec, cats));
            }
            cnt.increment();

            if (cnt.intValue() == config.max) {
                LOG.info("Maximum number of records reached: {}.",
                        config.max);
                break;
            }

            if (cnt.intValue() % config.batchSize == 0) {
                indexBatch(solr, docList);
                logProgress(cnt, timer);
                if (!config.skipCommit
                        && cnt.intValue() == config.batchSize) {
                    LOG.info("Committing 1st batch now so you can start "
                            + "playing...");
                    solr.commit();
                }
            }
        }

        if (!docList.isEmpty()) {
            indexBatch(solr, docList);
            logProgress(cnt, timer);
            if (!config.skipCommit) {
                LOG.info("Committing...");
                solr.commit();
                solr.optimize();
            }
            timer.stop();
            LOG.info("DONE. Elapsed time: {}", timer);
        }
    }

    private void mergeCategoriesForDuplicates(
            PeekingIterator<CSVRecord> it, CSVRecord master, Set<String> cats) {
        cats.add(getCsvValue(Column.CATEGORY_ETXT, master));
        CSVRecord nextOne = it.peek();
        if (nextOne != null
                && isEqual(master, nextOne, Column.RECALL_NUMBER_NUM)
                && isEqual(master, nextOne, Column.MAKE_NAME_NM)
                && isEqual(master, nextOne, Column.MODEL_NAME_NM)
                && isEqual(master, nextOne, Column.YEAR)
                && isEqual(master, nextOne, Column.SYSTEM_TYPE_ETXT)) {
            cats.add(getCsvValue(Column.CATEGORY_ETXT, nextOne));
            it.next();
            mergeCategoriesForDuplicates(it, master, cats);
        }
    }
    private boolean isEqual(
            CSVRecord master, CSVRecord nextOne, Column col) {
        return Objects.equals(
                getCsvValue(col, master), getCsvValue(col, nextOne));
    }


    private void logProgress(MutableLong cnt, StopWatch timer) {
        LOG.info("{} documents sent for indexing. Elapsed time: {}.",
                cnt, timer);
    }

    private boolean isRecordValid(CSVRecord rec) {
        if (!rec.isConsistent()) {
            LOG.error("Record has invalid number of fields: {}", rec);
            return false;
        }
        return true;
    }

    private SolrInputDocument toSolrDoc(CSVRecord rec, Set<String> cats)
            throws IOException {
        SolrInputDocument doc = new SolrInputDocument();

        String recallNo = getCsvValue(Column.RECALL_NUMBER_NUM, rec);
        String recallDate =
                getCsvValue(Column.RECALL_DATE_DTE, rec) + "T00:00:00Z";
        String make = WordUtils.capitalizeFully(
                getCsvValue(Column.MAKE_NAME_NM, rec));
        String modelYear = getCsvValue(Column.YEAR, rec);

        // make sure make does not appear in model
        String model = getCsvValue(Column.MODEL_NAME_NM, rec).toLowerCase();
        model = StringUtils.removeStart(model, make.toLowerCase()).trim();
        model = WordUtils.capitalizeFully(model);

        String systemType = getCsvValue(Column.SYSTEM_TYPE_ETXT, rec);
        String recallDesc = getCsvValue(Column.COMMENT_ETXT, rec);

        String recallTitle = make + " "
                + model + " " + modelYear + " Recall - " + systemType;

        addField(doc, "id", "vehicles-" + recallNo + "-"
                + idCounts.computeIfAbsent(recallNo,
                        k -> new MutableInt()).incrementAndGet());

        addField(doc, "vhcl_groupid", make + "-" + recallNo + "-" + systemType);

        addField(doc, "recall_dept", "Transport Canada");
        addField(doc, "recall_date", recallDate);
        addField(doc, "recall_year",
                StringUtils.substringBefore(recallDate, "-"));
        addField(doc, "recall_title", recallTitle);
        addField(doc, "vhcl_system_type", systemType);
        addField(doc, "vhcl_make", make);
        addField(doc, "recall_brand", make);
        addField(doc, "vhcl_model", model);
        addCsvField(doc, Column.RECALL_NUMBER_NUM, rec);

        // Re-categorize
        Set<String> recallTypes = new TreeSet<>(Arrays.asList("vehicles"));
        Set<String> newCats = new TreeSet<>();
        Set<String> newSubCats = new TreeSet<>();
        Set<String> newPaths = new TreeSet<>();
        for (String cat : cats) {
            List<CategoryMapping> cms =
                    Categorizer.get().getMappings("vehicles", cat);
            // if no mapping, simply add recall type to value
            if (cms.isEmpty()) {
                newCats.add(cat);
                newPaths.add("vehicles|" + cat);
                LOG.debug("No category mapping for vehicles: \"{}\"", cat);
            // if mapping, simply add recall type to value
            } else {
                for (CategoryMapping cm : cms) {
                    newCats.add(cm.getCategory());
                    newSubCats.add(cm.getSubCategory());
                    newPaths.add(cm.getPath());
                    recallTypes.add(cm.getType());
                }
            }
            addField(doc, "recall_categories", newCats);
            addField(doc, "recall_subcats", newSubCats);
            addField(doc, "recall_categories_orig", cat);
            addField(doc, "recall_cat_hier", newPaths);
        }

        // if we duplicate the first level suggestion with something in between,
        // it will be given higher scoring, which is what we want.
        //TODO move this logic to SolrUtil?
        addField(doc, RecallField.SUGGEST.getName(),
                make + " [boost_this " + make + " ]"
              + "recallTypes:vehicles^vehicleMakes:" + make);
        addField(doc, RecallField.SUGGEST.getName(),
                make + " " + model + " []"
              + "recallTypes:vehicles^vehicleMakes:" + make
              + "^vehicleModels:" + model);

        addField(doc, "recall_types", recallTypes);

        addCsvField(doc, Column.YEAR, rec);
        addCsvField(doc, Column.UNIT_AFFECTED_NBR, rec, v -> {
            return StringUtils.remove(StringUtils.removeEnd(v, ".00"), ',');
        });
        addCsvField(doc, Column.NOTIFICATION_TYPE_ETXT, rec);
        addField(doc, "recall_desc", recallDesc);

        // Recall URL was derived.  Should be established more reliably.
        addField(doc, "recall_url", "https://wwwapps.tc.gc.ca/Saf-Sec-Sur/7/"
                + "VRDB-BDRV/search-recherche/detail.aspx?lang=eng&rn="
                + recallNo);

        // merge all fields
        List<String> allFields = new ArrayList<>();
        doc.forEach((k, v) -> {
            allFields.add(k + "=" + v.getValue());
        });
        addField(doc, "_text_", allFields);

        addField(doc, "recall_desc_fields", "comment_etxt");

//        // add useful entries for term completion
//        addField(doc, "term_completion", make + " " + model);

        // store those we want for recall-specific freetext suggestions:
        // use brand, desc, and title
        List<String> suggs = new ArrayList<>();
        suggs.add(recallTitle);
        suggs.add(make);
        suggs.add(recallDesc);
        //Set<String> recallTypes
        for (String rt : recallTypes) {
            doc.addField(rt + "_suggest", suggs);
        }

        return doc;
    }

    private void indexBatch(
            SolrClient solr, List<SolrInputDocument> docList)
                    throws SolrServerException, IOException {
        solr.add(docList, COMMIT_WITHIN_MS);
        docList.clear();
    }

    private String getCsvValue(Column col, CSVRecord rec) {
        return rec.get(col.ordinal());
    }
    private void addCsvField(
            SolrInputDocument doc, Column col, CSVRecord rec,
            Function<String, Object> f) {
        addField(doc, col, f.apply(rec.get(col.ordinal())));
    }
    private void addCsvField(
            SolrInputDocument doc, Column col, CSVRecord rec) {
        addField(doc, col, rec.get(col.ordinal()));
    }
    private void addField(
            SolrInputDocument doc, Column col, Object value) {
        addField(doc, col.toString(), value);
    }
    private void addField(
            SolrInputDocument doc, String field, Object value) {
        if (!IndexerUtil.isEmpty(value)) {
            doc.addField(field, IndexerUtil.stripHtml(value));
        }
    }

    public static class Config {
        private File file;
        private String solrURL;
        private int batchSize;
        private int max;
        private boolean skipCommit;
        @Override
        public boolean equals(final Object other) {
            return EqualsBuilder.reflectionEquals(this, other);
        }
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
        @Override
        public String toString() {
            return new ReflectionToStringBuilder(
                    this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
        }
    }
}
