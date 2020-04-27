package ca.canada.treasury.testbed.web.controller;

import static ca.canada.treasury.testbed.web.service.impl.SolrUtil.COLLECTION_RECALLS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.canada.treasury.testbed.web.model.Recall;
import ca.canada.treasury.testbed.web.service.ISearchService;
import ca.canada.treasury.testbed.web.service.impl.SolrUtil;
import ca.canada.treasury.testbed.web.view.UITools;

@Controller
public class ExampleResultGroupingController {

    private static final Logger LOG =
            LoggerFactory.getLogger(ExampleResultGroupingController.class);

    @Autowired
    private ISearchService searchService;

    @GetMapping(value="/example/resultgrouping")
    public String search(
            Model model,
            @RequestParam(name = "terms", required = false)
            String terms) throws IOException {

        SolrQuery q = new SolrQuery();
        q.setRequestHandler("/" + COLLECTION_RECALLS + "/select");

        q.setQuery(StringUtils.defaultIfBlank(terms, "*:*"));
        q.setFields(
                "id", "recall_no", "recall_types",
                "recall_date", "recall_title",
                "recall_url", "score");
        q.setRows(20);
        q.setSort("recall_date", ORDER.desc);

        q.set("defType", "edismax");
        //q.set("qf", "_text_");

        q.set("group", true);
        q.set("group.field", "recall_brand");
        q.set("group.limit", 10);
//        q.set("group.cache.percent", 0);
        q.set("group.ngroups", true);

        LOG.debug("Solr {} query: {}", SolrUtil.COLLECTION_RECALLS, q);

        QueryResponse qr = searchService.badIdeaNativeSearch(q);

        List<ResultGroup> results = new ArrayList<>();
        List<GroupCommand> gcs = qr.getGroupResponse().getValues();
        if (!gcs.isEmpty()) {
            // we only requested one field.
            GroupCommand gc = gcs.get(0);
            model.addAttribute("matches", gc.getMatches());
            model.addAttribute("ngroups", gc.getNGroups());
            for (Group g: gc.getValues()) {
                List<Recall> recalls = new ArrayList<>();
                Set<String> uniqueTitles = new HashSet<>();
                for (Recall recall : SolrUtil.toRecallList(qr, g.getResult())) {
                    if (uniqueTitles.add(recall.getTitle())) {
                        recalls.add(recall);
                    }
                }
                results.add(new ResultGroup(
                        g.getGroupValue(),
                        g.getResult().getNumFound(),
                        recalls));
            }
        }

//TODO eliminate duplicate titles before returning.


        model.addAttribute("terms", terms);
        model.addAttribute("searchResults", results);
        model.addAttribute("ui", UITools.instance());
        return "example-resultgrouping";
    }

    public static class ResultGroup {
        private final String name;
        private final long numFound;
        private final List<Recall> recalls;
        public ResultGroup(String name, long numFound, List<Recall> recalls) {
            super();
            this.name = name;
            this.numFound = numFound;
            this.recalls = recalls;
        }
        public String getName() {
            return name;
        }
        public long getNumFound() {
            return numFound;
        }
        public long getNumRemaining() {
            return numFound - recalls.size();
        }
        public List<Recall> getRecalls() {
            return recalls;
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