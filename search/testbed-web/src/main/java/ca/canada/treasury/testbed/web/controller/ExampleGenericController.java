package ca.canada.treasury.testbed.web.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ca.canada.treasury.testbed.web.model.Recall;
import ca.canada.treasury.testbed.web.model.SearchRequest;
import ca.canada.treasury.testbed.web.model.SearchResponse;
import ca.canada.treasury.testbed.web.service.ISearchService;
import ca.canada.treasury.testbed.web.view.Pagination;
import ca.canada.treasury.testbed.web.view.UITools;

@Controller
public class ExampleGenericController {

    private static final int MAX_PAGINATION_LINKS = 7;

    @Autowired
    private ISearchService searchService;

    @GetMapping(value="/example/generic")
    public String search(Model model, SearchRequest searchRequest)
            throws IOException {

        // If form is true, the form has been submitted by the user.
        // set some defaults.
//        if (!form) {
//            // set default sort
//            searchRequest.setSort("score desc, recall_date desc");
//            // set default filters
//            int yearEnd = Year.now().getValue();
//            int yearStart = yearEnd - 4;
//            searchRequest.setRecallYearRanges(
//                    SetUtils.hashSet(yearStart + " - " + yearEnd));
//        }

        // Clone search request first to keep input data as-is?
        SearchResponse<Recall> searchResponse =
                searchService.searchRecalls(searchRequest);
        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("searchResponse", searchResponse);
        model.addAttribute("pagination", new Pagination(
                searchRequest, searchResponse, MAX_PAGINATION_LINKS));
        model.addAttribute("ui", UITools.instance());

        return "example-generic";
    }
}