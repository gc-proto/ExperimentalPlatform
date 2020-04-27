package ca.canada.treasury.testbed.indexer.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.canada.treasury.testbed.indexer.IndexerException;
import ca.canada.treasury.testbed.indexer.IndexerUtil;
import ca.canada.treasury.testbed.indexer.RecallField;

/**
 * <p>
 * Index a CSV file maintained manually that adds records to Solr.
 * </p>
 * <p>
 * The data file format is expected to be CSV, with the columns
 * matching the order of {@link Column} elements.
 * </p>
 * <p>
 * The data file can optionally be compressed (GZip).
 * </p>
 *
 * @author Pascal Essiembre
 */
public class ManualAdditionsRecallsIndexer {

    private static final Logger LOG =
            LoggerFactory.getLogger(ManualAdditionsRecallsIndexer.class);

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
        ID("id"),
        RECALL_NO("recall_no"),
        RECALL_TYPES("recall_types"),
        TITLE("recall_title"),
        DESC("recall_desc"),
        DESC_FIELDS("recall_desc_fields"),
        SUMMARY("recall_summary"),
        DATE("recall_date"),
        YEAR("recall_year"),
        DEPT("recall_dept"),
        BARCODE("recall_barcode"),
        BARCODE_TYPE("recall_barcode_type"),
        AUDIENCES("recall_audiences"),
        BRAND("recall_brand"),
        URL("recall_url"),
        ALERT_TYPE("recall_alert_type"),
        CAT_HIER("recall_cat_hier");

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

    public ManualAdditionsRecallsIndexer(Config config) {
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

            new ManualAdditionsRecallsIndexer(cfg).start();
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

        for (CSVRecord rec :
                CSVFormat.RFC4180.withHeader().parse(
                        IndexerUtil.toReader(config.file))) {
            try {
                if (isRecordValid(rec)) {
                    docList.add(toSolrDoc(rec));
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
            } catch (IOException | SolrServerException e) {
                throw new IndexerException(
                        "Problem indexing one or more records.", e);
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

    private SolrInputDocument toSolrDoc(CSVRecord rec) throws IOException {
        SolrInputDocument doc = new SolrInputDocument();


        for (Column col : Column.values()) {
            String colValue = getCsvValue(col, rec);

            // if recall_no is empty, use id.
            if (col == Column.RECALL_NO && StringUtils.isBlank(colValue)) {
                colValue = getCsvValue(Column.ID, rec);
            } else if (col == Column.CAT_HIER) {
                String[] parts = colValue.split("\\|");
                String cat = null;
                String subcat = null;
                if (parts.length >= 2) {
                    cat = parts[1];
                    addField(doc, "recall_categories", cat);
                    addField(doc, "recall_categories_orig", cat);
                    addField(doc, RecallField.SUGGEST.getName(),
                            cat + " []"
                          + "recallTypes:" + parts[0]
                          + "^categories:" + colValue);
                }
                if (parts.length >= 3) {
                    subcat = parts[2];
                    addField(doc, "recall_subcats", subcat);
                    addField(doc, RecallField.SUGGEST.getName(),
                            subcat + " []"
                          + "recallTypes:" + parts[0]
                          + "^categories:" + colValue);
                }
            }
            addField(doc, col, colValue);
        }


        // merge all fields
        List<String> suggs = new ArrayList<>();
        List<String> allFields = new ArrayList<>();
        final StringBuilder recallType = new StringBuilder();
        doc.forEach((k, v) -> {
            allFields.add(k + "=" + v.getValue());
            if (StringUtils.equalsAny(
                    k, "recall_title", "recall_brand", "recall_desc")) {
                suggs.add(Objects.toString(v.getValue(), ""));
            } else if ("recall_types".equals(k)) {
                recallType.append(Objects.toString(v.getValue(), ""));
            }
        });
        addField(doc, "_text_", allFields);

        doc.addField(recallType + "_suggest", suggs);


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
