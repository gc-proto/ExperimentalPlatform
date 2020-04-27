package ca.canada.treasury.testbed.indexer.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;
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

/**
 * <p>
 * NAICS data obtained from StatsCan:
 *
 * https://www.statcan.gc.ca/eng/subjects/standard/naics/2017/v3/index
 * </p>
 * <p>
 * Used only to have sufficiently good volume of short and optimal data to test
 * various suggesters offered by Solr.
 * </p>
 * <p>
 * The data file format is expected to be CSV, with the columns
 * matching the order of {@link Column} elements.
 * </p>
 * <p>
 * Not all fields may be indexed.
 * </p>
 * <p>
 * The data file can optionally be compressed (GZip).
 * </p>
 *
 * @author Pascal Essiembre
 */
public class NaicsIndexer {

    private static final Logger LOG =
            LoggerFactory.getLogger(NaicsIndexer.class);

    public static final int DEFAULT_BATCH_SIZE = 1000;
    public static final int DEFAULT_MAX = -1;
    public static final int COMMIT_WITHIN_MS = 5 * 60 * 1000;
    public static final String ARG_SOLR = "solr";
    public static final String ARG_FILE = "file";
    public static final String ARG_BATCH = "batch";
    public static final String ARG_MAX = "max";
    public static final String ARG_LEVELS = "levels";
    public static final String ARG_SKIP_COMMIT = "skipCommit";

    // Using ordinal position for CSV order.  Column name matches Solr field.
    public enum Column {
        LEVEL,
        HIER_STRUCT,
        CODE,
        CLASS_TITLE,
        SUPERSCRIPT,
        CLASS_DESC
    }

    private final Config config;

    public NaicsIndexer(Config config) {
        super();
        this.config = config;
    }

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(Option.builder(ARG_SOLR).desc(
                "Solr URL (e.g. http://localhost:8983/solr).")
                .hasArg().required().build());
        options.addOption(Option.builder(ARG_FILE).desc(
                "Path to local data file.").hasArg().required().build());
        options.addOption(Option.builder(ARG_BATCH).desc(
                "Document batch size for indexing. "
              + "Default is " + DEFAULT_BATCH_SIZE + ".")
                .hasArg().build());
        options.addOption(Option.builder(ARG_MAX).desc(
                "Maximum number of documents to send for indexing. "
              + "Default is unlimited.")
                .hasArg().build());
        options.addOption(Option.builder(ARG_LEVELS).desc(
                "NAICS hierarchy levels to index, from 1 to 5. "
              + "Defaults to all levels.")
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
            cfg.levels = line.getOptionValues(ARG_LEVELS);
            cfg.skipCommit = line.hasOption(ARG_SKIP_COMMIT);

            new NaicsIndexer(cfg).start();

        } catch (Exception exp) {
            LOG.error(exp.getMessage());
            new HelpFormatter().printHelp("<this_app>", options);
            System.exit(-1);
        }
    }

    public void start() throws Exception {
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
                    cnt.increment();

                    if (cnt.intValue() == config.max) {
                        LOG.info("Maximum number of records reached: {}.",
                                config.max);
                        break;
                    }

                    if (cnt.intValue() % config.batchSize == 0) {
                        indexBatch(solr, docList);
                        LOG.info("  {} documents sent for indexing.", cnt);
                        if (!config.skipCommit
                                && cnt.intValue() == config.batchSize) {
                            LOG.info("Committing 1st batch now so you can "
                                    + "start playing...");
                            solr.commit();
                        }
                    }
                }
            } catch (IOException | SolrServerException e) {
                throw new IndexerException(
                        "Problem indexing one or more records.", e);
            }
        }

        if (!docList.isEmpty()) {
            indexBatch(solr, docList);
            LOG.info("Indexed {} documents.", cnt);
            if (!config.skipCommit) {
                LOG.info("Committing...");
                solr.commit();
                solr.optimize();
            }
            timer.stop();
            LOG.info("DONE. Elapsed time: {}", timer);
        }
    }

    private boolean isRecordValid(CSVRecord rec) {
        if (!rec.isConsistent()) {
            LOG.error("Record has invalid number of fields: {}", rec);
            return false;
        }
        // if not of requested level, do not index
        if (ArrayUtils.isNotEmpty(config.levels) && !ArrayUtils.contains(
                config.levels, getCsvValue(Column.LEVEL, rec))) {
            return false;
        }

        return true;
    }

    private SolrInputDocument toSolrDoc(CSVRecord rec) {
        // Some suggesters are case-sensitive.
        String naicsClassTitle =
                StringUtils.lowerCase(getCsvValue(Column.CLASS_TITLE, rec));

        SolrInputDocument doc = new SolrInputDocument();
        addField(doc, "id", "naics-" + getCsvValue(Column.CODE, rec));
        addCsvField(doc, Column.LEVEL, rec);
        addCsvField(doc, Column.HIER_STRUCT, rec);
        addCsvField(doc, Column.CODE, rec);
        addField(doc, Column.CLASS_TITLE, naicsClassTitle);
        addCsvField(doc, Column.CLASS_DESC, rec);
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
            doc.addField(field.toLowerCase(), value);
        }
    }

    public static class Config {
        private File file;
        private String solrURL;
        private int batchSize;
        private int max;
        private String[] levels;
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
