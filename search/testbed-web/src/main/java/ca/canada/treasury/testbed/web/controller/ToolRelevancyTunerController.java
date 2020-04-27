package ca.canada.treasury.testbed.web.controller;

import static ca.canada.treasury.testbed.web.service.impl.SolrUtil.COLLECTION_RECALLS;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.NoOpResponseParser;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.canada.treasury.testbed.web.model.Recall;
import ca.canada.treasury.testbed.web.model.SearchResponse;
import ca.canada.treasury.testbed.web.model.SearchResults;
import ca.canada.treasury.testbed.web.service.ISearchService;
import ca.canada.treasury.testbed.web.service.impl.SolrUtil;
import ca.canada.treasury.testbed.web.view.UITools;


@Controller
public class ToolRelevancyTunerController {

    private static final Logger LOG =
            LoggerFactory.getLogger(ToolRelevancyTunerController.class);

    @Autowired
    private ISearchService searchService;

    @Autowired
    private SolrClient solr;

    @Value("${dataset.modified}")
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate datasetModified;

    @GetMapping("/tool/relevancytuner")
    public String search(Model model, RelevancyTuningRequest searchRequest)
            throws IOException {

        SolrQuery q = new SolrQuery();
        q.setRequestHandler("/" + COLLECTION_RECALLS + "/select");

        String terms = searchRequest.getTerms();
        if (StringUtils.isBlank(terms)) {
            terms = "*:*";
        } else if (searchRequest.proximity != null) {
            q.set("qs", searchRequest.proximity);
        }

        if (StringUtils.isNotBlank(searchRequest.getMinMustMatch())) {
            q.set("mm", searchRequest.getMinMustMatch());
            q.set("defType", "dismax");
        } else {
            q.set("defType", "edismax");
        }

        q.setQuery(terms);

        q.setFields("*,score,[explain style=html]");
        q.setRows(searchRequest.getMaxResults());


        q.set("qf",
                "recall_title" + boost(searchRequest.getTitleBoost()),
                "recall_desc" + boost(searchRequest.getDescriptionBoost()),
                "_text_" + boost(searchRequest.getFulltextBoost()));
        // boost more recent dates.
        q.set("bf", "recip(ms(NOW/HOUR,recall_date),3.16e-11,1,1)"
                + boost(searchRequest.getDateBoost()));

        // boost desired terms
        String tb = searchRequest.getTermBoost();
        if (StringUtils.isNotBlank(tb)) {
            q.set("bq", tb.replaceAll("\\s+", " "));
        }

        if (searchRequest.getTieBreaker() != null) {
            q.set("tie", Float.toString(searchRequest.getTieBreaker()));
        }

        // Filter on most recent "food" and "health" by default.
        if (!searchRequest.isIncludeArchived()) {
            SolrUtil.addExcludeArchivedFilter(q, datasetModified);

//            q.addFilterQuery(
//                    "NOT ((recall_types:food OR recall_types:health) "
//                  + "AND recall_date:[* TO " + datasetModified + "-2YEARS])");
////                  + "AND recall_date:[* TO NOW-2YEARS])");
        }

        //--- highlight params ---
        q.setHighlight(true);
        // No spaces after commas:
        // https://issues.apache.org/jira/browse/SOLR-11334
        q.setHighlightSimplePre("<mark>");
        q.setHighlightSimplePost("</mark>");
        q.set("hl.requireFieldMatch", "false");
        q.set("hl.fl", "recall_desc,recall_title,_text_");
        q.set("hl.method", "unified");
        q.set("hl.defaultSummary", "true");
        q.set("hl.fragsize", 0);

        q.set("f.recall_desc.hl.snippets", 2);
        q.set("f.recall_desc.hl.fragsize", 100);
        q.set("f.recall_desc.hl.method", "unified");

        q.set("lowercaseOperators", false);
        q.set("sow", false);

        //--- debug params ---
        q.set("debugQuery", "true");
        q.set("debug.explain.structured", "false");
        q.set("indent", "off");

        //--- other params ---
        q.set("omitHeader", true);

        LOG.debug("Solr {} query: {}", SolrUtil.COLLECTION_RECALLS, q);

        QueryResponse qr = searchService.badIdeaNativeSearch(q);

        // clean query a bit for display:
        q.remove("qt");
        q.remove("debugQuery");
        q.remove("debug.explain.structured");
        q.remove("indent");
        q.remove("omitHeader");

        //--- search response ---
        SearchResponse<Recall> searchResponse = new SearchResponse<>();
        searchResponse.setResults(new SearchResults<>(
                SolrUtil.toRecallList(qr, qr.getResults()),
                qr.getResults().getNumFound(),
                qr.getResults().getStart()));

        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("searchResponse", searchResponse);
        model.addAttribute("ui", UITools.instance());
        model.addAttribute("solrQuery", q.toString());
        return "tool-relevancytuner";
    }

    public String boost(Integer value) {
        if (value == null) {
            return "";
        }
        return "^" + value;
    }

    @CrossOrigin
    @GetMapping(
            value="/tool/relevancytuner/analyze",
            produces = "application/json")
    public @ResponseBody String analyze(
            @RequestParam(name = "field", required = true) String field,
            @RequestParam(name = "terms", required = true) String terms)
            throws IOException, SolrServerException {

        SolrQuery q = new SolrQuery(terms);
        q.setRequestHandler("/" + COLLECTION_RECALLS + "/analysis/field");
        q.set("analysis.fieldvalue", terms);
        q.set("fl", "* score");
        q.set("analysis.fieldname", field);
        q.set("verbose_output", 1);

        q.set("analysis.showmatch", true);
        q.set("wt", "json");
        q.set("indent", true);

        LOG.debug("Solr {} query: {}", SolrUtil.COLLECTION_RECALLS, q);

        QueryRequest req = new QueryRequest(q);
        NoOpResponseParser rawJsonResponseParser = new NoOpResponseParser();
        rawJsonResponseParser.setWriterType("json");
        req.setResponseParser(rawJsonResponseParser);
        return (String) solr.request(req).get("response");
    }

    public static class RelevancyTuningRequest {
        private String terms;
        private String minMustMatch;
        private Integer proximity;
        private int maxResults = 50;
        private Integer titleBoost = 25;
        private Integer descriptionBoost = 20;
        private Integer fulltextBoost = 1;
        private Integer dateBoost = 100;
        private boolean includeArchived;
        private String termBoost;
        private Float tieBreaker;

        public String getTerms() {
            return terms;
        }
        public void setTerms(String terms) {
            this.terms = terms;
        }
        public String getMinMustMatch() {
            return minMustMatch;
        }
        public void setMinMustMatch(String minMustMatch) {
            this.minMustMatch = minMustMatch;
        }
        public Integer getProximity() {
            return proximity;
        }
        public void setProximity(Integer proximity) {
            this.proximity = proximity;
        }
        public Integer getMaxResults() {
            return maxResults;
        }
        public void setMaxResults(Integer maxResults) {
            this.maxResults = maxResults;
        }
        public Integer getTitleBoost() {
            return titleBoost;
        }
        public void setTitleBoost(Integer titleBoost) {
            this.titleBoost = titleBoost;
        }
        public Integer getDescriptionBoost() {
            return descriptionBoost;
        }
        public void setDescriptionBoost(Integer descriptionBoost) {
            this.descriptionBoost = descriptionBoost;
        }
        public Integer getFulltextBoost() {
            return fulltextBoost;
        }
        public void setFulltextBoost(Integer fulltextBoost) {
            this.fulltextBoost = fulltextBoost;
        }
        public Integer getDateBoost() {
            return dateBoost;
        }
        public void setDateBoost(Integer dateBoost) {
            this.dateBoost = dateBoost;
        }
        public boolean isIncludeArchived() {
            return includeArchived;
        }
        public void setIncludeArchived(boolean includeArchived) {
            this.includeArchived = includeArchived;
        }
        @Override
        public boolean equals(final Object other) {
            return EqualsBuilder.reflectionEquals(this, other);
        }
        public String getTermBoost() {
            return termBoost;
        }
        public void setTermBoost(String termBoost) {
            this.termBoost = termBoost;
        }
        public Float getTieBreaker() {
            return tieBreaker;
        }
        public void setTieBreaker(Float tieBreaker) {
            this.tieBreaker = tieBreaker;
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