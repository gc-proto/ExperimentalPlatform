package ca.canada.treasury.testbed.web.controller;

import static ca.canada.treasury.testbed.web.service.impl.SolrUtil.COLLECTION_RECALLS;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ca.canada.treasury.testbed.web.model.Recall;
import ca.canada.treasury.testbed.web.model.SearchResponse;
import ca.canada.treasury.testbed.web.model.SearchResults;
import ca.canada.treasury.testbed.web.service.ISearchService;
import ca.canada.treasury.testbed.web.service.impl.SolrUtil;
import ca.canada.treasury.testbed.web.view.UITools;

@Controller
public class ExampleResultClusteringController {

    private static final Logger LOG =
            LoggerFactory.getLogger(ExampleResultClusteringController.class);

    @Autowired
    private ISearchService searchService;

    @GetMapping(value="/example/resultclustering")
    public String search(Model model, ResultClusteringRequest searchRequest)
            throws IOException {

        SearchResponse<Recall> searchResponse = new SearchResponse<>();
        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("searchResponse", searchResponse);
        model.addAttribute("ui", UITools.instance());

        if (!searchRequest.isEmpty()) {
            doSearch(searchRequest, searchResponse);
        }
        return "example-resultclustering";
    }

    private void doSearch(
            ResultClusteringRequest searchRequest,
            SearchResponse<Recall> searchResponse) throws IOException {
        SolrQuery sq = new SolrQuery(
                "\"" + ClientUtils.escapeQueryChars(searchRequest.getTerms())
                + "\"~10");
        sq.setRequestHandler("/" + COLLECTION_RECALLS + "/clustering");
        sq.set("clustering.engine", searchRequest.getClusterEngine());
        sq.setFields("*");
        sq.setRows(searchRequest.getMaxResults());

        //--- dismax params ---
        sq.set("defType", "edismax");
        sq.set("qf", "recall_title^10", "recall_desc^5", "_text_^1");
//        sq.addFilterQuery("(*:* NOT recall_types:vehicles)");

        //--- highlight params ---
        sq.setHighlight(true);
        sq.set("hl.fl", "recall_desc,recall_title");
        sq.setHighlightSimplePre("<mark>");
        sq.setHighlightSimplePost("</mark>");
        sq.set("hl.requireFieldMatch", "false");
        sq.set("hl.method", "unified");
        sq.set("hl.fragsize", 100);

     //   sq.set("LingoClusteringAlgorithm.desiredClusterCountBase", 20);

        // true by default: sq.set("carrot.produceSummary", true);


        LOG.debug("Solr {} query: {}", SolrUtil.COLLECTION_RECALLS, sq);

        QueryResponse qr = searchService.badIdeaNativeSearch(sq);

        //--- search response ---
        searchResponse.setResults(new SearchResults<>(
                SolrUtil.toRecallList(qr, qr.getResults()),
                qr.getResults().getNumFound(),
                qr.getResults().getStart()));
        searchResponse.setClusters(
                SolrUtil.toClusterList(qr.getClusteringResponse(), Math.min(
                        sq.getRows(),
                        searchResponse.getResults().getNumFound())));
    }

    public static class ResultClusteringRequest {
        private String terms;
        private int maxResults = 50;
        private String clusterEngine;
        public String getTerms() {
            return terms;
        }
        public void setTerms(String terms) {
            this.terms = terms;
        }
        public int getMaxResults() {
            return maxResults;
        }
        public void setMaxResults(int qty) {
            this.maxResults = qty;
        }
        public String getClusterEngine() {
            return clusterEngine;
        }
        public void setClusterEngine(String clusterEngine) {
            this.clusterEngine = clusterEngine;
        }
        public boolean isEmpty() {
            return StringUtils.isBlank(terms);
        }
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