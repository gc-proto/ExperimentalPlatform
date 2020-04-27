package ca.canada.treasury.testbed.solr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionExporter {

    private static final Logger LOG =
            LoggerFactory.getLogger(CollectionExporter.class);

    public static final String ARG_SOLR = "solr";
    public static final String ARG_OUTFILE = "outfile";
    public static final String ARG_MAX = "max";
    public static final String ARG_FIELDS = "fields";
    public static final String ARG_INDENT = "indent";
    public static final String ARG_QUERY = "query";

    private String solrURL;
    private File outFile;
    private long max = -1;
    private String[] fields;
    private boolean indent;
    private String query;

    public String getSolrURL() {
        return solrURL;
    }
    public void setSolrURL(String solrURL) {
        this.solrURL = solrURL;
    }

    public File getOutFile() {
        return outFile;
    }
    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public long getMax() {
        return max;
    }
    public void setMax(long max) {
        this.max = max;
    }

    public String[] getFields() {
        return fields;
    }
    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public boolean isIndent() {
        return indent;
    }
    public void setIndent(boolean indent) {
        this.indent = indent;
    }

    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }

    public void exportData() throws UnsupportedOperationException, IOException {
        LOG.info("Exporting from {} to {}", solrURL, outFile.getAbsolutePath());
        try (CloseableHttpClient http = HttpClientBuilder.create().build()) {
            String exportURL = StringUtils.appendIfMissing(solrURL, "/")
                    + "select?wt=json&omitHeader=true";
            if (ArrayUtils.isNotEmpty(fields)) {
                exportURL += "&fl=" + StringUtils.join(fields, "+");
            } else {
                exportURL += "&fl=*";
            }
            exportURL += "&q=" + (StringUtils.isBlank(query) ? "*:*" : query);
            exportURL += "&rows=" + (max < 0? Integer.MAX_VALUE: max);
            exportURL += "&indent=" + (indent ? "on" : "off");

            LOG.debug("Solr export URL: " + exportURL);

            HttpGet updateRequest = new HttpGet(exportURL);
            HttpResponse response = http.execute(updateRequest);
            try (InputStream is = response.getEntity().getContent()) {
                responseToFile(is, outFile);
                if (response.getStatusLine().getStatusCode()
                        != HttpStatus.SC_OK) {
                    throw new RuntimeException("Invalid HTTP response when "
                            + "trying to export JSON data from Solr: \""
                            + response.getStatusLine() + "\".  Check logs.");
                }
            }
        }
    }

    private void responseToFile(InputStream is, File outFile)
            throws IOException {
        InputStream input = is;
        if (!input.markSupported()) {
            input = new BufferedInputStream(input);
        }
        input.mark(2);
        char c;
        while ((c = (char) input.read()) != -1) {
            if (c == '[') {
                input.reset();
                break;
            }
            input.mark(2);
        }
        try (FileOutputStream os = new FileOutputStream(outFile)) {
            IOUtils.copy(input, os);
            os.getChannel().truncate(os.getChannel().size() - 3);
        }
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(Option.builder(ARG_SOLR).desc(
                "(Required) Solr URL including collection name.")
                .hasArg().required().build());
        options.addOption(Option.builder(ARG_OUTFILE).desc(
                "(Required) Where to store the exported file.")
                .hasArg().required().build());
        options.addOption(Option.builder(ARG_MAX).desc(
                "(Optional) Max records to export (defaults to all).")
                .hasArg().build());
        options.addOption(Option.builder(ARG_FIELDS).desc(
                "(Optional) Comma-separated list of fields to export. "
                        + "Defaults to all fields.")
                .hasArg().build());
        options.addOption(Option.builder(ARG_INDENT).desc(
                "(Optional) Whether to indent the exported data.")
                .build());
        options.addOption(Option.builder(ARG_QUERY).desc(
                "(Optional) Solr query to control what data is exported.")
                .hasArg().build());

        try {
            CommandLine cmd = parser.parse(options, args);
            CollectionExporter export = new CollectionExporter();
            export.setSolrURL(cmd.getOptionValue(ARG_SOLR));
            export.setOutFile(new File(cmd.getOptionValue(ARG_OUTFILE)));
            export.setFields(
                    StringUtils.split(cmd.getOptionValue(ARG_FIELDS), ", "));
            if (cmd.hasOption(ARG_MAX)) {
                export.setMax(Long.parseLong(cmd.getOptionValue(ARG_MAX)));
            }
            export.setIndent(cmd.hasOption(ARG_INDENT));
            export.setQuery(cmd.getOptionValue(ARG_QUERY));
            export.exportData();
        } catch (ParseException exp) {
            System.out.println("Error: " + exp.getMessage());
            new HelpFormatter().printHelp(
                    CollectionExporter.class.getSimpleName(), options);
        }
    }
}
