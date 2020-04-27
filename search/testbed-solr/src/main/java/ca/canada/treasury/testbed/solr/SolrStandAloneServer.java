package ca.canada.treasury.testbed.solr;

import java.io.File;
import java.net.URISyntaxException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.JettySolrRunner;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrStandAloneServer {

    private static final Logger LOG =
            LoggerFactory.getLogger(SolrStandAloneServer.class);

    private final String home;
    private final int port;
    private JettySolrRunner solrServer;

    // Start Sorl using a random/available port.
    public SolrStandAloneServer(String home) {
        this(home, 0);
    }

    public SolrStandAloneServer(String home, int port) {
        super();
        this.home = home;
        this.port = port;
    }

    public String getHome() {
        return home;
    }

    public void start() throws Exception {
        LOG.info("Starting Solr...");
        if (solrServer != null && solrServer.isRunning()) {
            throw new IllegalStateException(
                    "Solr already running on port " + port);
        }
        String log = new File(home, "solr-test.log").getAbsolutePath();
        System.setProperty("solr.log.dir", log);
        this.solrServer = new JettySolrRunner(home, "/solr", port);
        solrServer.start();

        int seconds = 0;
        for (; seconds < 30; seconds++) {
            if (solrServer.isRunning()) {
                break;
            }
            LOG.info("Waiting for Solr to start...");
            Thread.sleep(1000);
        }
        if (seconds >= 30) {
            LOG.warn("Looks like Solr is not starting on port {}. "
                   + "Please investigate.", solrServer.getLocalPort());

        } else {
            LOG.info("Started on port {}", solrServer.getLocalPort());
        }
    }

    public void stop() throws Exception {
        solrServer.stop();
        LOG.info("Stopped");
    }

    public int getPort() {
        if (solrServer == null) {
            throw new IllegalStateException("Cannot get Solr port "
                    + "because no Solr Server was found.");
        }
        return solrServer.getLocalPort();
    }

    public String getBaseURL() throws URISyntaxException {
        if (solrServer == null) {
            throw new IllegalStateException("Cannot get Solr base URL "
                    + "because no Solr Server was found.");
        }
        return "http://localhost:" + getPort()
                + solrServer.getBaseUrl().getPath();
    }

    public SolrClient newSolrClient(String collection) {
        if (solrServer == null) {
            throw new IllegalStateException("Cannot create new SolrClient "
                    + "because no Solr Server was found.");
        }
        // solrServer.newClient() does not work for some reason, host is null
        return new HttpSolrClient.Builder(
               "http://localhost:" + getPort() + "/solr/" + collection).build();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            LOG.error("Usage: <this_app> solr_home port");
            System.exit(-1);
        }
        String home = args[0];
        int port = Integer.parseInt(args[1]);
        new SolrStandAloneServer(home, port).start();
    }
}
