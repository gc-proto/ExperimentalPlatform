package ca.canada.treasury.testbed.web.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Service;

import ca.canada.treasury.testbed.web.TestbedRuntimeException;
import ca.canada.treasury.testbed.web.model.Recall;
import ca.canada.treasury.testbed.web.model.SearchFacet;
import ca.canada.treasury.testbed.web.model.SearchFacetValue;
import ca.canada.treasury.testbed.web.model.SearchRequest;
import ca.canada.treasury.testbed.web.model.SearchResponse;
import ca.canada.treasury.testbed.web.model.SearchResults;
import ca.canada.treasury.testbed.web.service.ISearchService;

/**
 * Search service implementation.
 */
@Service
public class SolrSearchServiceImpl implements ISearchService {

    private static final Logger LOG =
            LoggerFactory.getLogger(SolrSearchServiceImpl.class);

    @Value("${dataset.modified}")
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate datasetModified;

    //TODO make configurable on request so we can play with boosting dynamically
    // for testing
    private static final Map<String, Integer> SEARCH_FIELDS =
            MapUtils.putAll(new HashMap<>(), new Object[] {
        "recall_title",     25,
        "recall_desc",      20,
        "_text_",            1,

        // Those below are of type "string", so only exact match will match them
        // Is it worth giving them weight?
//        "recall_dept",       15,
//        "recall_audiences",  15,
//        "recall_categories", 15,
//        "vhcl_system_type",  15,
//        "vhcl_notif_type",   15,
//        "vhcl_model",        10,
//        "vhcl_make",          5,
    });

    @Autowired
    private SolrClient solr;

    //TODO have an argument in SearchRequest whether to highlight or not.
    @SuppressWarnings("unchecked")
    @Override
    public SearchResponse<Recall> searchRecalls(SearchRequest searchRequest)
            throws IOException {
//        if (searchRequest.isEmpty()) {
//            return new SearchResponse();
//        }
        SolrQuery q = toSolrQuery(searchRequest);
        QueryResponse qr = query(SolrUtil.COLLECTION_RECALLS, q);

        // Results
        SearchResults<Recall> results = new SearchResults<>(
                SolrUtil.toRecallList(qr, qr.getResults()),
                qr.getResults().getNumFound(),
                qr.getResults().getStart());
        SearchResponse<Recall> sr = new SearchResponse<>();
        sr.setResults(results);

        // Facets
        List<SearchFacet> facets = new ArrayList<>();
        for (FacetField ff : qr.getFacetFields()) {
            SearchFacet facet = new SearchFacet();
            facet.setName(ff.getName());
            facet.setValueCount(ff.getValueCount());
            List<SearchFacetValue> facetValues = new ArrayList<>();
            for (FacetField.Count cnt : ff.getValues()) {
                SearchFacetValue value = new SearchFacetValue();
                value.setField(ff.getName());
                value.setCount(cnt.getCount());
                // hack since srping splits on comma
                value.setValue(cnt.getName().replace(",", "___"));
                String[] parts = cnt.getName().split("\\|");
                int level = parts.length - 1;
                if ("recall_cat_hier".equals(ff.getName())) {
                    // do not consider recall type in level for categories
                    value.setLevel(level -1);
                } else {
                    value.setLevel(level);
                }
                value.setLabel(parts[level]);
                facetValues.add(value);
            }
//            if ("vhcl_make".equals(facet.getName())) {
//                // sort alphabetically
//                facetValues.sort((fv1, fv2) -> {
//                    return fv1.getName().compareTo(fv2.getName());
//                });
//            }

            facet.setValues(facetValues);
            facets.add(facet);
        }

        // Facet Ranges
        for (RangeFacet<Integer, Integer> fr : qr.getFacetRanges()) {
            SearchFacet facet = new SearchFacet();
            facet.setName(fr.getName());
            List<SearchFacetValue> facetValues = new ArrayList<>();
            for (RangeFacet.Count cnt : fr.getCounts()) {
                SearchFacetValue value = new SearchFacetValue();
                value.setField(fr.getName());
                value.setLabel(cnt.getValue() + " - " +
                        (Integer.valueOf(cnt.getValue()) + fr.getGap() -1));
                value.setValue(value.getLabel());
                value.setCount(cnt.getCount());
                value.setLevel(0);
                facetValues.add(value);
            }
            Collections.reverse(facetValues);
            facet.setValues(facetValues);
            facets.add(facet);
        }

        sr.setFacets(facets);
        sr.setSpellCheck(SolrUtil.toSuggestion(qr.getSpellCheckResponse()));

        return sr;
    }

    /**
     * Hacky way to break good design but get things done for the
     * testbed.  Do not repeat at home.  The idea is to start with this
     * and make it proper down the road.
     * @param query a {@link SolrQuery}
     * @return {@link QueryResponse}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <R, Q> R badIdeaNativeSearch(Q query) throws IOException {
        Objects.requireNonNull(query, "'query' must not be null.");
        if (!(query instanceof SolrQuery)) {
            throw new IllegalArgumentException(
                    "'query' must be of type SolrQuery");
        }
        try {
            return (R) solr.query((SolrQuery) query);
        } catch (SolrServerException e) {
            throw new TestbedRuntimeException("Could not query Solr.", e);
        }
    }

    private SolrQuery toSolrQuery(SearchRequest req) {

        int yearRangeStart = Year.now().getValue() - 49;

        SolrQuery q = new SolrQuery();

        String terms = req.getTerms();
        if (StringUtils.isBlank(terms)) {
            terms = "*:*";
        } else {
//            terms = "\"" + terms + "\"~10";
//                || StringUtils.countMatches(terms, '"') % 2 != 0) {
//            // Number of quote is odddoes not contain quotes, so make sure they are close to
//            // each other
//            terms = "\"" + ClientUtils.escapeQueryChars(terms) + "\"~10";
        }
        q.setQuery(terms);
//        q.set("pf", "recall_title");
//        q.set("ps", 1);
//      q.set("qs",30);


        // No need for score when not sorting by relevance
        // To not return description and rely on highlighting only:
        //     "recall_desc:[value v=\"\"]"
        q.setFields("*");
        if (StringUtils.startsWith(req.getSort(), "score")) {
            q.addField("score");
        }

//      q.setFields("*,score,[explain style=html]");
        q.setRows(req.getDocsPerPage());
        q.setStart(Math.max(0, req.getPageIndex() - 1) * req.getDocsPerPage());
        q.setSort(SolrUtil.toSortClause(req.getSort()));

        //--- dismax params ---
        q.set("defType", "edismax");

        // set fields we want to query, applying different boost
        q.set("qf", buildSearchFields(null, null, true));

        //--- facets ---
        q.setFacet(true);

        // get matching facets
        q.addFacetField("{!ex=typ}recall_types");
        SolrUtil.addFacetRange(q, "{!ex=year}recall_year",
                yearRangeStart, Year.now().getValue(), 5);

        // set facet filters
        SolrUtil.addFieldFilterQuery(
                q, "recall_types", req.getRecallTypes(), "{!tag=typ}");
        SolrUtil.addFieldNumRangeFilterQuery(q, "recall_year",
                req.getRecallYearRanges(), "{!tag=year}");

        if (!req.getRecallTypes().isEmpty()) {
            q.addFacetField("{!ex=cat}recall_cat_hier");
            q.addFacetField("{!ex=alert}recall_alert_type");
            SolrUtil.addFieldFilterQuery(
                    q, "recall_cat_hier", req.getCategories(), "{!tag=cat}");
            SolrUtil.addFieldFilterQuery(q, "recall_alert_type",
                    req.getAlertTypes(), "{!tag=alert}");
        }

        if (req.getRecallTypes().contains(Recall.TYPE_VEHICLES)) {
            // get matching facets
            q.addFacetField("{!ex=mak}vhcl_make");
            SolrUtil.addFacetRange(q, "{!ex=vyear}vhcl_years",
                    yearRangeStart, Year.now().getValue(), 5);
            // set facet filters
            SolrUtil.addFieldFilterQuery(
                    q, "vhcl_make", req.getVehicleMakes(), "{!tag=mak}");
            SolrUtil.addFieldNumRangeFilterQuery(
                    q, "vhcl_years", req.getVehicleYearRanges(), "{!tag=vyear}");
        } else if (!req.getRecallTypes().isEmpty()) {
            q.addFacetField("{!ex=aud}recall_audiences");
            SolrUtil.addFieldFilterQuery(
                    q, "recall_audiences", req.getAudiences(), "{!tag=aud}");
        }

        // Filter on most recent "food" and "health" by default.
        if (!req.isIncludeArchived()) {
            SolrUtil.addExcludeArchivedFilter(q, datasetModified);
//            q.addFilterQuery(
//                    "NOT ((recall_types:food OR recall_types:health) "
//                  + "AND recall_date:[* TO NOW-2YEARS])");
//
//          + "AND recall_date:[* TO " + datasetModified + "-2YEARS])");

            //fq=recall_date:[NOW-2YEARS%20TO%20*]
//           q.
        }

  //      NOW+6MONTHS

        // facets to filter on
//        SolrUtil.addFieldFilterQuery(
//                q, "recall_categories", req.getCategories(), "{!tag=cat}");
//        SolrUtil.addFieldFilterQuery(
//                q, "recall_dept", req.getRecallDepartments(), "{!tag=dep}");



//      q.add("f.filedate.facet.range.hardend", "true");
        q.setFacetMinCount(0);
        q.setFacetSort("index"); // count or index
        q.setFacetLimit(200); // just to be safe


      //--- spelcheck params ---
      q.add("spellcheck", "true");
      q.add("spellcheck.dictionary", "default");
      q.add("spellcheck.extendedResults", "true");
      q.add("spellcheck.count", "10");
      q.add("spellcheck.alternativeTermCount", "5");
      q.add("spellcheck.maxResultsForSuggest", "5");
      q.add("spellcheck.collate", "true");
      q.add("spellcheck.collateExtendedResults", "true");
      q.add("spellcheck.maxCollationTries", "10");
      q.add("spellcheck.maxCollations", "1");
      q.add("spellcheck.onlyMorePopular", "true");


      //--- highlight params ---

//TODO highlight fragments for recall_desc but full for recall_catchall

      q.setHighlight(true);
      // No spaces after commas:
      // https://issues.apache.org/jira/browse/SOLR-11334
      q.setHighlightSimplePre("<mark>");
      q.setHighlightSimplePost("</mark>");
      q.set("hl.requireFieldMatch", "false");
      q.set("hl.fl", "recall_desc,recall_title,_text_,"
              + "vhcl_system_type,vhcl_notif_type,vhcl_model,vhcl_make");
      q.set("hl.method", "unified");
      q.set("hl.defaultSummary", "true");
      q.set("hl.fragsize", 0);

      q.set("f.recall_desc.hl.snippets", 2);
      q.set("f.recall_desc.hl.fragsize", 100);
      q.set("f.recall_desc.hl.method", "unified");


//
////      q.set("hl.defaultSummary", "false");
////      q.set("hl.encoder", "html");
////      q.set("hl.fragsize", "50");
////      q.set("hl.snippets", "1");
//
//      //--- debug params ---
////      q.set("debugQuery", "true");
////      q.set("debug.explain.structured", "true");
//
//

        //--- other params ---
        q.set("omitHeader", true);

        LOG.info("Solr {} query: {}", SolrUtil.COLLECTION_RECALLS, q);
        return q;
    }

    private String[] buildSearchFields(
            String filterField, String suffix, boolean applyBoost) {
        if (StringUtils.isNotBlank(filterField)) {
            return new String[] {applyBoost(filterField, suffix,
                    (applyBoost ? SEARCH_FIELDS.get(filterField) : null))};
        }
        String[] fields = new String[SEARCH_FIELDS.size()];
        int i = 0;
        for (Entry<String, Integer> e : SEARCH_FIELDS.entrySet()) {
            fields[i++] = applyBoost(
                    e.getKey(), suffix, (applyBoost ? e.getValue() : null));
        }
        return fields;
    }
    private String applyBoost(String field, String suffix, Integer boost) {
        String f = field;
        if (StringUtils.isNotBlank(suffix)) {
            f += suffix;
        }
        if (boost != null && boost != 0) {
            f += "^" + boost;
        }
        return f;
    }
    public QueryResponse query(String collection, SolrQuery q) {
        try {
            return solr.query(collection, q);
        } catch (SolrServerException | IOException e) {
            throw new TestbedRuntimeException(
                    "Solr query failed: " + q.toString(), e);
        }
    }
}
