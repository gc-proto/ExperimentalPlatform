package ca.canada.treasury.testbed.web.controller;

import static ca.canada.treasury.testbed.web.service.impl.SolrUtil.COLLECTION_RECALLS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.canada.treasury.testbed.web.model.RecallGroup;
import ca.canada.treasury.testbed.web.model.VehicleRecall;
import ca.canada.treasury.testbed.web.service.ISearchService;
import ca.canada.treasury.testbed.web.service.impl.SolrUtil;
import ca.canada.treasury.testbed.web.view.UITools;

@Controller
public class ExampleResultCollapsingController {

    private static final Logger LOG =
            LoggerFactory.getLogger(ExampleResultCollapsingController.class);

    @Autowired
    private ISearchService searchService;

    @GetMapping(value="/example/resultcollapsing")
    public String search(
            Model model,
            @RequestParam(name = "terms", required = false)
            String terms) throws IOException {

        SolrQuery q = new SolrQuery();
        q.setRequestHandler("/" + COLLECTION_RECALLS + "/select");

        q.setQuery(StringUtils.defaultIfBlank(terms, "*:*"));
        q.setFields(
                "id", "vhcl_groupid", "recall_no", "recall_types",
                "recall_date", "recall_year", "recall_title", "recall_desc",
                "recall_dept", "recall_url", "vhcl_years", "vhcl_make",
                "vhcl_model", "vhcl_nbr_affected", "vhcl_system_type",
                "vhcl_notif_type",
                "score");
        q.setRows(20);
        q.setSort("recall_date", ORDER.desc);

        q.set("defType", "edismax");
        //q.set("qf", "_text_");

        q.setFilterQueries(
            "{!collapse field=vhcl_groupid}",
            "recall_types:\"vehicles\""
        );

//        q.set("fq", "{!collapse field=recall_no}");
        q.set("expand", true);
        q.set("expand.rows", 500);

        LOG.debug("Solr {} query: {}", SolrUtil.COLLECTION_RECALLS, q);


        QueryResponse qr = searchService.badIdeaNativeSearch(q);

        List<RecallGroup> results = new ArrayList<>();
        SolrUtil.toRecallList(qr, qr.getResults()).forEach(r -> {
            RecallGroup group = new RecallGroup();
            BeanUtils.copyProperties(r, group);
            // push main one in children as well.
            group.getRecalls().add(r);
            group.getRecalls().addAll(SolrUtil.toRecallList(qr,
                    qr.getExpandedResults().get(
                            ((VehicleRecall) r).getGroupKey())));
            results.add(group);
        });

        model.addAttribute("terms", terms);
        model.addAttribute("searchResults", results);
        model.addAttribute("ui", UITools.instance());
        return "example-resultcollapsing";
    }
}