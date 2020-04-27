package ca.canada.treasury.testbed.web.config;

import java.net.URL;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfig {

    @Value("${solr.url}")
    private URL solrURL;

    @Bean
    public SolrClient solr() {
        return new HttpSolrClient.Builder(solrURL.toExternalForm()).build();
    }
}
