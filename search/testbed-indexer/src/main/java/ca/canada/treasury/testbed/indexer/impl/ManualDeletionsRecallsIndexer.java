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
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.canada.treasury.testbed.indexer.IndexerException;
import ca.canada.treasury.testbed.indexer.IndexerUtil;

/**
 * <p>
 * Delete records with matching IDs from Solr.
 * </p>
 * <p>
 * The data file format is expected to be plain text, with one ID per line
 * and nothing else.
 * </p>
 * <p>
 * The data file can optionally be compressed (GZip).
 * </p>
 *
 * @author Pascal Essiembre
 */
public class ManualDeletionsRecallsIndexer {

    private static final Logger LOG =
            LoggerFactory.getLogger(ManualDeletionsRecallsIndexer.class);

    public static final int DEFAULT_BATCH_SIZE = 50;
    public static final int DEFAULT_MAX = -1;
    public static final int COMMIT_WITHIN_MS = 5 * 60 * 1000;
    public static final String ARG_SOLR = "solr";
    public static final String ARG_FILE = "file";
    public static final String ARG_BATCH = "batch";
    public static final String ARG_SKIP_COMMIT = "skipCommit";

    private final Config config;

    public ManualDeletionsRecallsIndexer(Config config) {
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
                "path to file").hasArg().required().build());
        options.addOption(Option.builder(ARG_BATCH).desc(
                "ID batch size for deleting (default "
                        + DEFAULT_BATCH_SIZE + ")")
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
            cfg.skipCommit = line.hasOption(ARG_SKIP_COMMIT);

            LOG.info("Deleting IDs in \"{}\" from \"{}\"", cfg.file, cfg.solrURL);

            new ManualDeletionsRecallsIndexer(cfg).start();
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

        List<String> idList = new ArrayList<>();
        MutableLong cnt = new MutableLong();

        for (String id: IOUtils.readLines(IndexerUtil.toReader(config.file))) {
            try {
                if (StringUtils.isBlank(id)) {
                    continue;
                }
                idList.add(id);
                cnt.increment();
                if (cnt.intValue() % config.batchSize == 0) {
                    deleteBatch(solr, idList);
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
                        "Problem deleting one or more records.", e);
            }
        }

        if (!idList.isEmpty()) {
            deleteBatch(solr, idList);
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
        LOG.info("{} ids sent for deletions. Elapsed time: {}.",
                cnt, timer);
    }

    private void deleteBatch(
            SolrClient solr, List<String> idList)
                    throws SolrServerException, IOException {
        solr.deleteById(idList, COMMIT_WITHIN_MS);
        idList.clear();
    }

    public static class Config {
        private File file;
        private String solrURL;
        private int batchSize;
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
