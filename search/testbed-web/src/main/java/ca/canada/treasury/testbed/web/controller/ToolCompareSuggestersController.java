package ca.canada.treasury.testbed.web.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.canada.treasury.testbed.web.service.ISearchService;


@Controller
public class ToolCompareSuggestersController {

    private static final Logger LOG =
            LoggerFactory.getLogger(ToolCompareSuggestersController.class);

    private static final String[] LOOKUP_IMPLS = new String[] {
            "AnalyzingInfixLookupFactory",
            "AnalyzingLookupFactory",
            "BlendedInfixLookupFactory",
            "FreeTextLookupFactory",
            "FSTLookupFactory",
            "FuzzyLookupFactory",
            "JaspellLookupFactory",
            "TSTLookupFactory",
            "WFSTLookupFactory"
    };

    @Autowired
    private ISearchService searchService;

    @GetMapping("/tool/suggestercomparator")
    public String search(Model model) {
        model.addAttribute("lookupImpls", LOOKUP_IMPLS);
        return "tool-suggestercomparator";
    }

    @CrossOrigin
    @GetMapping(
            value="/tool/suggestercomparator/rest",
            produces = "application/json")
    public @ResponseBody Map<String, List<String>> suggest(
            @RequestParam("term") String terms)
            throws IOException {

        SolrQuery q = new SolrQuery(terms);
        q.setRequestHandler("/naics/suggest");
        q.add("suggest.dictionary", LOOKUP_IMPLS);

        q.set("omitHeader", "true");
        q.set("suggest.q", StringUtils.lowerCase(terms));

        LOG.debug("Solr query: {}", q);

        QueryResponse response = searchService.badIdeaNativeSearch(q);
        return response.getSuggesterResponse().getSuggestedTerms();
    }
}