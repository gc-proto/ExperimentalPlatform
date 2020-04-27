package ca.canada.treasury.testbed.web.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CovidRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String search;
    private String sort;
    private String order;
    private int offset;
    private int limit;

    private String language;
    private String content;
    private Set<String> h2 = new HashSet<>();
    private Set<String> breadcrumb2 = new HashSet<>();

    public String getSearch() {
        return search;
    }
    public void setSearch(String search) {
        this.search = search;
    }
    public String getSort() {
        return sort;
    }
    public void setSort(String sort) {
        this.sort = sort;
    }
    public String getOrder() {
        return order;
    }
    public void setOrder(String order) {
        this.order = order;
    }
    public int getOffset() {
        return offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Set<String> getH2() {
        return h2;
    }
    public void setH2(Set<String> h2) {
        this.h2 = h2;
    }
    public Set<String> getBreadcrumb2() {
        return breadcrumb2;
    }
    public void setBreadcrumb2(Set<String> breadcrumb2) {
        this.breadcrumb2 = breadcrumb2;
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
                this, ToStringStyle.SHORT_PREFIX_STYLE)
                .toString();
    }
}
