package ca.canada.treasury.testbed.solr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionImporter {

    private static final Logger LOG =
            LoggerFactory.getLogger(CollectionImporter.class);

    public static final String ARG_SOLR = "solr";
    public static final String ARG_DATA = "data";

    private String solrURL;
    private String[] importData;

    public String getSolrURL() {
        return solrURL;
    }
    public void setSolrURL(String solrURL) {
        this.solrURL = solrURL;
    }

    public String[] getImportData() {
        return importData;
    }
    public void setImportData(String... importData) {
        this.importData = importData;
    }

    public void execute() throws IOException {

        if (importData == null) {
            LOG.debug("Nothing to import into Solr.");
            return;
        }
        Pattern p = Pattern.compile("(.*?)\\:(.*?)\\:(.*?)(?:$|\\?)(.*)");
        LOG.info("POPULATING Solr: {}", solrURL);
        List<String> collectionsToCommit = new ArrayList<>();
        for (String data : importData) {
            LOG.info("Data: {}", data);
            Matcher m = p.matcher(data);
            if (!m.matches()) {
                throw new IllegalArgumentException(
                        "Invalid data argument: " + data);
            }
            String format = m.group(1);
            String collection = m.group(2);
            File file = new File(m.group(3));
            String params = m.group(4);
            executeUpdate(format, collection, file, params);
            collectionsToCommit.add(collection);
        }

        for (String collection : collectionsToCommit) {
            executeCommit(collection);
        }

        LOG.info("POPULATING Solr: DONE!");
    }

    private void executeCommit(String collection) throws IOException {
        String commitURL = StringUtils.appendIfMissing(solrURL, "/")
                + collection + "/update?commit=true";
        LOG.info("Committing \"{}\": {}", collection, commitURL);
        httpRequest(new HttpGet(commitURL));
    }

    private void executeUpdate(String format, String collection,
            File file, String params) throws IOException {

        LOG.info("Importing into \"{}\" -> [{}] {} (params: {})",
                collection, format, file.getAbsolutePath(),
                ObjectUtils.defaultIfNull(params, "[none]"));
        String importURL = StringUtils.appendIfMissing(solrURL, "/")
                + collection + "/update/" + format;
        if (StringUtils.isNotBlank(params)) {
            importURL += "?" + params;
        }
        LOG.info("Solr import URL: {}", importURL);

        HttpPost updateRequest = new HttpPost(importURL);

        try (InputStream is = toInputStream(file)) {
            HttpEntity inEntity = new InputStreamEntity(toInputStream(file));
            updateRequest.setEntity(inEntity);
            httpRequest(updateRequest);
        }
    }

    private void httpRequest(HttpUriRequest req) throws IOException {
        try (CloseableHttpClient httpClient =
                HttpClientBuilder.create().build()) {
            HttpResponse response = httpClient.execute(req);
            try (InputStream is = response.getEntity().getContent()) {
                String responseText = IOUtils.toString(is,
                        StandardCharsets.UTF_8);
                LOG.info("Solr import response:\n{}", responseText);
                if (response.getStatusLine().getStatusCode()
                        != HttpStatus.SC_OK) {
                    throw new IOException("Invalid HTTP response from Solr: "
                            + response.getStatusLine() + "\".  Check logs.");
                }
            }
        }
    }

    private InputStream toInputStream(File inFile) throws IOException {
        InputStream in = FileUtils.openInputStream(inFile);
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        in.mark(2);
        int magic = 0;
        try {
            magic = in.read() & 0xff | ((in.read() << 8) & 0xff00);
            in.reset();
        } catch (IOException e) {
            LOG.error("Could not detect if Gzip, assumign a normal file.", e);
            in.reset();
            return in;
        }
        if (magic == GZIPInputStream.GZIP_MAGIC) {
            LOG.info("Gzip file detected.");
            return new GZIPInputStream(in);
        }
        LOG.info("Plain-text file detected.");
        return in;
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(Option.builder(ARG_SOLR).desc(
                "(Required) Solr URL (without collection name).")
                .hasArg().required().build());
        options.addOption(Option.builder(ARG_DATA).desc(
                "(Required) Data to load into Solr, using this format: "
                        + "\"format:collection:filepath?optionalSolrParams "
                        + "(e.g., \"csv:company:c:\\data\\company.csv"
                        + "?keepEmpty=true\"). Repeat this argument "
                        + "to index multiple files. Path can be .gz file.")
                .hasArg().required().build());

        try {
            CommandLine cmd = parser.parse(options, args);
            CollectionImporter importer = new CollectionImporter();
            importer.setSolrURL(cmd.getOptionValue(ARG_SOLR));
            importer.setImportData(cmd.getOptionValues(ARG_DATA));
            importer.execute();
        } catch (ParseException exp) {
            LOG.error("Error: {}", exp.getMessage());
            new HelpFormatter().printHelp(
                    CollectionImporter.class.getSimpleName(), options);
        }
    }
}
