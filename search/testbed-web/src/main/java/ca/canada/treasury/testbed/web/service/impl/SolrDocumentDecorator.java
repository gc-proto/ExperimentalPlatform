package ca.canada.treasury.testbed.web.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;


/**
 * Adds util methods to a solr document.  Can be reused safely within
 * same thread by calling {@link #setDocument(SolrDocument)}
 * @author Pascal Essiembre
 */
public class SolrDocumentDecorator {

    private SolrDocument doc;
    private final QueryResponse qr;
    public SolrDocumentDecorator(QueryResponse qr) {
        super();
        this.qr = qr;
    }
    public SolrDocumentDecorator setDocument(SolrDocument doc) {
        this.doc = doc;
        return this;
    }
    public SolrDocument getDocument() {
        return doc;
    }

    public List<Float> getFloats(String fieldName) {
        return Optional.ofNullable(doc.getFieldValues(fieldName)).orElse(
                Collections.emptyList()).stream().map(i ->  (Float) i)
                        .collect(Collectors.toList());
    }
    public Float getFloat(String fieldName) {
        return (Float) doc.getFieldValue(fieldName);
    }

    public List<Double> getDoubles(String fieldName) {
        return Optional.ofNullable(doc.getFieldValues(fieldName)).orElse(
                Collections.emptyList()).stream().map(i ->  (Double) i)
                        .collect(Collectors.toList());
    }
    public Double getDouble(String fieldName) {
        return (Double) doc.getFieldValue(fieldName);
    }

    public List<Integer> getIntegers(String fieldName) {
        return Optional.ofNullable(doc.getFieldValues(fieldName)).orElse(
                Collections.emptyList()).stream().map(i ->  (Integer) i)
                        .collect(Collectors.toList());
    }
    public Integer getInteger(String fieldName) {
        return (Integer) doc.getFieldValue(fieldName);
    }
    public List<String> getStrings(String fieldName) {
        return Optional.ofNullable(doc.getFieldValues(fieldName)).orElse(
                Collections.emptyList()).stream().map(i ->  (String) i)
                        .collect(Collectors.toList());
    }
    public String getString(String fieldName) {
        return getString(fieldName, null);
    }
    public String getString(String fieldName, String defaultValue) {
        return toString(getFieldValue(fieldName), defaultValue);
    }
    public LocalDate getDate(String fieldName) {
        LocalDateTime date = getDateTime(fieldName);
        if (date == null) {
            return null;
        }
        return date.toLocalDate();
    }
    public LocalDateTime getDateTime(String fieldName) {
        Date date = (Date) getFieldValue(fieldName);
        if (date == null) {
            return null;
        }
        return date.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime();
    }

    public String getHighlighted(String fieldName) {
        String hl = null;
        Map<String, Map<String, List<String>>> allHL = qr.getHighlighting();
        if (allHL != null) {
            Map<String, List<String>> docHL =
                    allHL.get(doc.getFieldValue("id"));
            if (docHL != null) {
                hl = StringUtils.join(docHL.get(fieldName), " … ");
                if (hl != null) {
                    // To join contiguous highlights
                    hl = hl.replaceAll("</mark>\\s*<mark>", " ");
                }
            }
        }
        return hl;
    }
    // HTML-encode regular string when no highlight for consistency.
    public String getHighlightedOrString(String fieldName) {
        String hl = getHighlighted(fieldName);
        if (StringUtils.isBlank(hl)) {
            return StringEscapeUtils.escapeXml11(getString(fieldName));
        }
        return hl;
    }

    public Object getFieldValue(String fieldName) {
        Object obj = doc.getFieldValue(fieldName);
        if (obj instanceof Collection) {
            Collection<?> col = (Collection<?>) obj;
            if (!col.isEmpty()) {
                obj = col.iterator().next();
            } else {
                obj = null;
            }
        }
        return obj;
    }

    private String toString(Object obj, Object defaultValue) {
        return Objects.toString(obj, Objects.toString(defaultValue, null));
    }
}
