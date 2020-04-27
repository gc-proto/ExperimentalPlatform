package ca.canada.treasury.testbed.web.service.impl;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.response.Cluster;
import org.apache.solr.client.solrj.response.ClusteringResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Correction;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.SimpleOrderedMap;

import ca.canada.treasury.testbed.web.model.Recall;
import ca.canada.treasury.testbed.web.model.SearchCluster;
import ca.canada.treasury.testbed.web.model.SearchSuggestion;
import ca.canada.treasury.testbed.web.model.VehicleRecall;

/**
 * Solr utility methods.
 */
public final class SolrUtil {

    //TODO make a configurable mapping of alias to actual collection names.
    public static final String COLLECTION_RECALLS = "recalls";
    public static final String COLLECTION_NAICS = "naics";
    public static final String COLLECTION_COVID19 = "covid19";

    private SolrUtil() {
        super();
    }

    public static int wordCount(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        return text.split("\\s+").length;
    }

    public static SortClause toSortClause(String sort) {
        String s = StringUtils.trimToNull(sort);
        if (s == null) {
            return SortClause.desc("score");
        }
        ORDER order = ORDER.desc;
        String field = StringUtils.substringBefore(s, " ");
        if ("asc".equalsIgnoreCase(StringUtils.substringAfter(s, " "))) {
            order = ORDER.asc;
        }
        return SortClause.create(field, order);
    }

    // To years back, from dataset last updated date.
    public static void addExcludeArchivedFilter(SolrQuery q, LocalDate now) {
        q.addFilterQuery(
                "NOT ((recall_types:food OR recall_types:health) "
              + "AND recall_date:[* TO NOW-2YEARS])");
        if (now != null) {
            q.add("NOW", Long.toString(now.plusDays(1).atStartOfDay().toInstant(
                    ZoneOffset.UTC).toEpochMilli()));
        }
    }

    public static void addFilterQuery(
            SolrQuery q, String fieldName, Object value) {
        String str = StringUtils.trimToNull(Objects.toString(value, null));
        if (str != null) {
            q.addFilterQuery(fieldName
                    + ":\"" + ClientUtils.escapeQueryChars(str) + "\"");
        }
    }
    public static void addFieldFilterQuery(
            SolrQuery q, String fieldName, Collection<?> values) {
        addFieldFilterQuery(q, fieldName, values, null);
    }
    public static void addFieldFilterQuery(
            SolrQuery q, String fieldName, Collection<?> values, String tag) {
        if (CollectionUtils.isNotEmpty(values)) {
            boolean first = true;
            StringBuilder b = new StringBuilder();
            for (Object v : values) {
                String value = Objects.toString(v);
                if (StringUtils.isNotBlank(value)) {
                    if (first) {
                        if (StringUtils.isNotBlank(tag)) {
                            b.append(tag);
                        }
                        b.append(fieldName + ":(");
                    } else {
                        b.append(" OR ");
                    }
                    b.append('"');
                    b.append(ClientUtils.escapeQueryChars(value.replace("___", ","))); // <-- Hack to prevent spring from splitting commas
                    b.append('"');
                    first = false;
                }
            }
            b.append(')');
            q.addFilterQuery(b.toString());
        }
    }
    public static void addFieldNumRangeFilterQuery(
            SolrQuery q, String fieldName, Collection<?> values) {
        addFieldNumRangeFilterQuery(q, fieldName, values, null);
    }
    public static void addFieldNumRangeFilterQuery(
            SolrQuery q, String fieldName, Collection<?> values, String tag) {
        if (CollectionUtils.isNotEmpty(values)) {
            boolean first = true;
            StringBuilder b = new StringBuilder();
            for (Object v : values) {
                String value = Objects.toString(v);
                if (StringUtils.isNotBlank(value)) {
                    if (!first) {
                        b.append(" OR ");
                    }
                    String f = fieldName.replaceFirst("^\\{.*\\}",  "");

                    b.append(f + ":");
                    b.append(value.replaceFirst(
                            "(\\d+).+?(\\d+)", "[$1 TO $2]"));
                    first = false;
                }
            }
            if (b.length() > 0) {
                b.insert(0, '(');
                b.append(')');
                if (StringUtils.isNotBlank(tag)) {
                    b.insert(0, tag);
                }
            }
            q.addFilterQuery(b.toString());
        }
    }

    public static void addFacetRange(
            SolrQuery q, String field, int start, int end, int gap) {
        q.add(FacetParams.FACET_RANGE, field);
        String untaggedField = field.replaceFirst("^\\{.*\\}",  "");
        q.set(fieldParam(untaggedField, FacetParams.FACET_RANGE_START), start);
        q.set(fieldParam(untaggedField, FacetParams.FACET_RANGE_END), end);
        q.set(fieldParam(untaggedField, FacetParams.FACET_RANGE_GAP), gap);
    }

    public static SearchSuggestion toSuggestion(SpellCheckResponse scr) {
        SearchSuggestion sug = new SearchSuggestion();
        if (scr == null || scr.isCorrectlySpelled()
                || scr.getCollatedResults().isEmpty()) {
            return sug;
        };
        Collation collation = scr.getCollatedResults().get(0);
        String query = collation.getCollationQueryString();
        String markup = query;
        for (Correction cor : collation.getMisspellingsAndCorrections()) {
            if (!Objects.equals(cor.getCorrection(), cor.getOriginal())) {
                markup = markup.replaceAll(
                        "\\b(" + Pattern.quote(cor.getCorrection()) + ")\\b",
                        "<b>$1</b>");
            }
        }
        sug.setQuery(query);
        sug.setMarkup(markup);
        return sug;
    }

    @SuppressWarnings("unchecked")
    public static List<Recall> toRecallList(
            QueryResponse qr, SolrDocumentList docList) {
        if (docList == null || docList.isEmpty()) {
            return Collections.emptyList();
        }
        SolrDocumentDecorator doc = new SolrDocumentDecorator(qr);
        return docList.stream().map(d -> {
            doc.setDocument(d);

            List<String> recallTypes = doc.getStrings("recall_types");
            // if we start having quite a few specifics, use a factory
            Recall r = recallTypes.contains("vehicles")
                    ? new VehicleRecall() : new Recall();

            r.setId(doc.getString("id"));
            r.setRecallNo(doc.getString("recall_no"));
            r.setTypes(doc.getStrings("recall_types"));
            r.setDate(doc.getDate("recall_date"));
            r.setUrl(doc.getString("recall_url"));
            r.setTitle(doc.getHighlightedOrString("recall_title"));
            r.setDescription(doc.getHighlightedOrString("recall_desc"));
            r.setDepartment(doc.getHighlightedOrString("recall_dept"));
            r.setAlertType(doc.getString("recall_alert_type"));
            r.setAudiences(doc.getStrings("recall_audiences"));
            r.setCategories(doc.getStrings("recall_categories"));
            r.setSummary(doc.getString("recall_summary"));
            r.setScore(doc.getFloat("score"));

//            r.setMiscContent(doc.getHighlightedOrString("recall_catchall"));
            r.setCatchAllFields(doc.getStrings("_text_"));
            r.setDescriptionFields(doc.getStrings("recall_desc_fields"));

            // Grab explain if requested
            Map<String, Object> debugMap = qr.getDebugMap();
            if (debugMap != null) {
                SimpleOrderedMap<String> explainMap =
                        (SimpleOrderedMap<String>) debugMap.get("explain");
                if (explainMap != null) {
                    r.setExplain(explainMap.get(r.getId()));
                }
            }

            // Vehicle-specific:
            if (r instanceof VehicleRecall) {
                VehicleRecall vr = (VehicleRecall) r;
                vr.setYears(doc.getIntegers("vhcl_years"));
                vr.setMake(doc.getHighlightedOrString("vhcl_make"));
                vr.setModel(doc.getHighlightedOrString("vhcl_model"));
                vr.setNumberAffected(doc.getInteger("vhcl_nbr_affected"));
                vr.setSystemType(
                        doc.getHighlightedOrString("vhcl_system_type"));
                vr.setNotificationType(
                        doc.getHighlightedOrString("vhcl_notif_type"));
                vr.setGroupKey(doc.getString("vhcl_groupid"));
            }
            return r;
        }).collect(Collectors.toList());
    }
/*
    public static List<CovidDoc> toCovidDocList(
            QueryResponse qr, SolrDocumentList docList) {
        if (docList == null || docList.isEmpty()) {
            return Collections.emptyList();
        }
        SolrDocumentDecorator doc = new SolrDocumentDecorator(qr);
        return docList.stream().map(solrDoc -> {
            doc.setDocument(solrDoc);
            CovidDoc d = new CovidDoc();
            d.setId(doc.getString(CovidDoc.ID));
            d.setAudience(doc.getString(CovidDoc.AUDIENCE));
            d.setAuthor(doc.getString(CovidDoc.AUTHOR));
            d.setCategory(doc.getString(CovidDoc.CATEGORY));
            d.setContent(doc.getString(CovidDoc.CONTENT));
            d.setDesc(doc.getString(CovidDoc.DESC));
            d.setDomain(doc.getString(CovidDoc.DOMAIN));
            d.setFiletype(doc.getString(CovidDoc.FILE_TYPE));
            d.setKeywords(doc.getString(CovidDoc.KEYWORDS));
            d.setLanguage(doc.getString(CovidDoc.LANGUAGE));
            d.setLastcrawled(doc.getString(CovidDoc.LAST_CRAWLED));
            d.setLastmodified(doc.getString(CovidDoc.LAST_MODIFIED));
            d.setLinktitle(doc.getString(CovidDoc.LINK_TITLE));
            d.setSubject(doc.getString(CovidDoc.SUBJECT));
            d.setTitle(doc.getString(CovidDoc.TITLE));
            return d;
        }).collect(Collectors.toList());
    }
*/
    public static List<SearchCluster> toClusterList(
            ClusteringResponse cr, long totalForPercent) {
        if (cr == null || cr.getClusters().isEmpty()) {
            return Collections.emptyList();
        }
        List<SearchCluster> list = new ArrayList<>();

//        int cnt = 0;
        for (Cluster cluster: cr.getClusters()) {
            for (String label : cluster.getLabels()) {
                SearchCluster sc = new SearchCluster();
                sc.setLabel(StringUtils.lowerCase(label, Locale.CANADA_FRENCH));
                //w.name("docIdsIndex"); w.value(cnt);
                sc.setDocIds(cluster.getDocs());
//                w.name("docCount"); w.value(cluster.getDocs().size());
                sc.setPercent((int)
                        (100 * cluster.getDocs().size() / totalForPercent));
                sc.setScore(cluster.getScore());
                sc.setOtherTopics(cluster.isOtherTopics());
                list.add(sc);
            }
//            cnt++;
        }


        return list;
    }


    private static String fieldParam(String field, String param) {
        return String.format(Locale.ROOT, "f.%s.%s", field, param);
    }
}
