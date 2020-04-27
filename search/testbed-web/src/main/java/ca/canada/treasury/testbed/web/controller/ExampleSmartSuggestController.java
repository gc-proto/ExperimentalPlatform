package ca.canada.treasury.testbed.web.controller;

import static ca.canada.treasury.testbed.web.service.impl.SolrUtil.COLLECTION_RECALLS;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.canada.treasury.testbed.web.service.ISearchService;
import ca.canada.treasury.testbed.web.service.impl.SolrUtil;

@Controller
public class ExampleSmartSuggestController {

    private static final Logger LOG =
            LoggerFactory.getLogger(ExampleSmartSuggestController.class);

//    private static final Map<String, String> FIELD_MAPPINGS =
//            MapUtils.putAll(new HashMap<>(), new Object[] {
//        "make", "vhcl_make",
//        "model", "vhcl_model",
//        "system", "vhcl_system-type"
//    });

    @Autowired
    private ISearchService searchService;

    private static final Comparator<Suggestion> SUG_LENGTH_COMPARATOR =
            (s1, s2) -> s1.suggestQuery.length() - s2.suggestQuery.length();
    private static final Comparator<String> WORD_COUNT_COMPARATOR =
            (s1, s2) -> SolrUtil.wordCount(s2) - SolrUtil.wordCount(s1);
    private static final Comparator<FacetField.Count> FACET_COUNT_COMPARATOR =
            (f1, f2) -> {
        // devalue vehicle (too many)
        long c1 = f1.getName().startsWith("vehicles")
                ? f1.getCount() / 6 : f1.getCount();
        long c2 = f2.getName().startsWith("vehicles")
                ? f2.getCount() / 6 : f2.getCount();
        return (int) (c2 - c1);
    };

    @GetMapping(value="/example/smartsuggest")
    public String search() throws IOException {
        return "example-smartsuggest";
    }

    @CrossOrigin
    @GetMapping(
            value="/example/smartsuggest/rest",
            produces = "application/json")
    public @ResponseBody Set<Suggestion> suggest(
            @RequestParam(name = "term", required = true) String terms,
            @RequestParam(name = "maxPerType", defaultValue = "5") int max)
            throws IOException {
        Set<Suggestion> suggestions = new ListOrderedSet<>();
        fromSolrSuggesters(suggestions, terms, max);
        fromPattern(suggestions, terms);
        fromFulltextSearch(suggestions, terms, max);
//LOG.info("suggestQuery: {}", suggestions);
        return suggestions;
    }

    @CrossOrigin
    @GetMapping(
            value="/example/smartsuggest/restrefinements",
            produces = "application/json")
    public @ResponseBody Set<Suggestion> suggestRefinements(
            @RequestParam(name = "term", required = true) String terms,
            @RequestParam(name = "max", defaultValue = "5") int max)
            throws IOException {
        Set<Suggestion> suggestions = new ListOrderedSet<>();


        fromFacets(suggestions, terms, max);


        return suggestions;
    }

    private void fromFacets(
            Set<Suggestion> suggestions, String terms, int max)
                    throws IOException {

        //--- From Facets ---
        SolrQuery q = new SolrQuery(terms);
        q.setRequestHandler("/" + COLLECTION_RECALLS + "/select");

        q.set("omitHeader", "true");
        q.set("defType", "edismax");
        q.setRows(0);

        q.setFacet(true);
        q.addFacetField("recall_cat_hier");//, "vhcl_make");
//                "{!ex=mak}vhcl_make"
        q.setFacetMinCount(10);
        q.setFacetSort("count"); // count or index
        q.setFacetLimit(max * 4); // double to give some room for cleaning
        //q.setFacetLimit(max);
        LOG.debug("Solr query: {}", q);

        QueryResponse response = searchService.badIdeaNativeSearch(q);


        // Facets
        List<FacetField.Count> facetValues = new ArrayList<>();
        for (FacetField ff : response.getFacetFields()) {
            for (FacetField.Count cnt : ff.getValues()) {
                facetValues.add(cnt);
            }
        }
        facetValues.sort(FACET_COUNT_COMPARATOR);

        List<Suggestion> sugList = new ArrayList<>();
        for (FacetField.Count fv : facetValues) {
            String facetName = fv.getFacetField().getName();
            // categories
            if ("recall_cat_hier".equals(facetName)) {
                String[] parts = fv.getName().split("\\|");
                if (parts.length > 0) {
                    Suggestion sug = new Suggestion(parts[0],
                            StringUtils.substringAfter(
                                    fv.getName(), "|").toLowerCase(),
                            parts[parts.length - 1]);
                    if (/*StringUtils.isNotBlank(sug.subType)
                            &&*/ !"uncategorized".equals(sug.suggestLabel)
                            && !"other".equals(sug.suggestLabel)) {
                        sugList.add(sug);
                    }
                }
            // other
            } else if ("vhcl_make".equals(facetName)) {
                sugList.add(new Suggestion("vehicles", "make", fv.getName()));
            }
            if (sugList.size() >= max) {
                break;
            }
        }
        suggestions.addAll(sugList);
    }

    @CrossOrigin
    @GetMapping(
            value="/example/smartsuggest/restcompletions",
            produces = "application/json")
    public @ResponseBody List<String> suggestCompletions(
            @RequestParam(name = "term", required = true) String terms,
            @RequestParam(name = "max", defaultValue = "5") int max)
            throws IOException {
        List<String> suggestions =
                SetUniqueList.setUniqueList(new ArrayList<>());

        SolrQuery q = new SolrQuery(terms);
        q.setRequestHandler("/" + COLLECTION_RECALLS + "/suggest");
        // vehicle make and system type are already in title, but add model?
        q.add("suggest.dictionary", "term_completion");
        q.set("omitHeader", "true");
        q.set("suggest.q", StringUtils.lowerCase(terms));
        q.set("suggest.count", max * 2); // get more so we can sort the top ones

        LOG.debug("Solr query: {}", q);

        QueryResponse response = searchService.badIdeaNativeSearch(q);


        for (List<String> suggs :
                response.getSuggesterResponse().getSuggestedTerms().values()) {
            suggestions.addAll(suggs);
        }

        suggestions = suggestions.stream()
                .map(s-> s.replace('\u001e', ' ')).collect(Collectors.toList());

        // order by most words first
        suggestions.sort(WORD_COUNT_COMPARATOR);

        int wordCount = SolrUtil.wordCount(terms);
        suggestions.removeIf(s -> SolrUtil.wordCount(s) < wordCount);

        suggestions = suggestions.subList(0, Math.min(max, suggestions.size()));

        LOG.debug("suggestCompletionQuery: {}", suggestions);
        return suggestions;
    }


    private void fromFulltextSearch(
            Set<Suggestion> suggestions, String terms, int max)
                    throws IOException {
        String t = terms.trim();
        if (t.length() > 3 && !t.endsWith("*")) {
            t += "*";
        }

        SolrQuery q = new SolrQuery(t);
        q.setRequestHandler("/" + COLLECTION_RECALLS + "/select");
        q.setFields("id,recall_types,recall_title:[value v=\"\"]");
        q.setRows(max);
        q.set("omitHeader", true);

        //--- dismax params ---
        q.set("defType", "edismax");
        q.set("qf", "recall_title");

        //--- highlight params ---
        q.setHighlight(true);
        q.setHighlightSimplePre("<b>");
        q.setHighlightSimplePost("</b>");
        q.set("hl.requireFieldMatch", "true");
        q.set("hl.fl", "recall_title");
        q.set("hl.method", "unified");
        q.set("hl.fragsize", 50);
        q.set("hl.bs.type", "WORD");

        q.set("group", "true");
        q.set("group.field", "recall_title");
        q.set("group.main", "true");

        LOG.debug("Solr suggestQuery: {}", q);

        QueryResponse response = searchService.badIdeaNativeSearch(q);
        List<Suggestion> suggs =
                new ArrayList<>(response.getResults().size());
        response.getResults().forEach(doc -> {
            Object id = doc.getFirstValue("id");
            String recallType = (String) doc.getFirstValue("recall_types");
            if (!"undefined".equals(recallType)) {
                suggs.add(new Suggestion(recallType, "fulltext",
                        response.getHighlighting().get(id).get(
                                "recall_title").iterator().next()));
            }
        });
        // order by shortest suggestions first.
        suggs.sort(SUG_LENGTH_COMPARATOR);
        // if first one is multi word, add a first one single word
        //TODO do it for each type?
        if (!suggs.isEmpty()) {
            Suggestion sug = suggs.get(0);
            if (sug.suggestQuery.contains(" ")) {
                suggs.add(0, new Suggestion(sug.type, sug.subType,
                        sug.suggestLabel.replaceFirst("(.*?) .*", "$1")));
            }
        }
        suggestions.addAll(suggs);
    }

    private void fromSolrSuggesters(
            Set<Suggestion> suggestions, String terms, int max)
            throws IOException {

        SolrQuery q = new SolrQuery(terms);
        q.setRequestHandler("/" + COLLECTION_RECALLS + "/suggest");
        q.add("suggest.dictionary", "make", "model", "system_type");

        q.set("omitHeader", "true");
        q.set("suggest.q", StringUtils.lowerCase(terms));
        q.set("suggest.count", max);

        LOG.debug("Solr suggestQuery: {}", q);

        QueryResponse response = searchService.badIdeaNativeSearch(q);
        List<Suggestion> suggs = new ArrayList<>(
                response.getSuggesterResponse().getSuggestions().size());
        response.getSuggesterResponse().getSuggestions().forEach((k, v) -> {
            if (!v.isEmpty()) {
                v.forEach(sug -> suggs.add(
                        new Suggestion("vehicles", k, sug.getTerm())));
            }
        });
        suggs.sort(SUG_LENGTH_COMPARATOR);
        suggestions.addAll(suggs);
    }

    private void fromPattern(Set<Suggestion> suggestions, String terms) {
        String lastTerm = terms.replaceFirst(".*\\s(.*)", "$1").trim();

        // Year?
        if (NumberUtils.isDigits(lastTerm)) {
            int val = NumberUtils.toInt(lastTerm);
            // check if between 1950 and a couple years from now
            // (car model year could be set in future).
            if (val > 1950 && val <= Year.now().getValue() + 2) {
                suggestions.add(new Suggestion("any", "recall_year", lastTerm));
                suggestions.add(
                        new Suggestion("vehicles", "vhcl_years", lastTerm));
            }
        }
    }

    public static class Suggestion {
        private final String type;
        private final String subType;
        private final String suggestQuery;
        private final String suggestLabel;
        public Suggestion(String type, String subType, String suggestion) {
            super();
            this.type = type;
            this.subType = subType;
            this.suggestLabel = suggestion.toLowerCase(Locale.FRENCH);
            this.suggestQuery = suggestLabel.replaceAll("<.*?>", "");
        }
        public String getType() {
            return type;
        }
        public String getSubType() {
            return subType;
        }
        public String getSuggestQuery() {
            return suggestQuery;
        }
        public String getSuggestLabel() {
            return suggestLabel;
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