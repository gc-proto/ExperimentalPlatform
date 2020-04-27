package ca.canada.treasury.testbed.web.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_DOCS_PER_PAGE = 10;
    public static final String DEFAULT_SORT = "score desc, recall_date desc";

    private String terms;
    private String sort = DEFAULT_SORT;
    private Integer docsPerPage = DEFAULT_DOCS_PER_PAGE;
    private int pageIndex = 1;
    private boolean includeArchived;

    private Set<String> recallTypes = new HashSet<>();
    private Set<String> recallYearRanges = new HashSet<>();
    private Set<String> audiences = new HashSet<>();
    private Set<String> categories = new HashSet<>();
    private Set<String> alertTypes = new HashSet<>();

    private Set<String> vehicleMakes = new HashSet<>();
    private Set<String> vehicleYearRanges = new HashSet<>();

    public String getTerms() {
        return terms;
    }
    public void setTerms(String terms) {
        this.terms = terms;
    }

    public int getPageIndex() {
        return pageIndex;
    }
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getSort() {
        return sort;
    }
    public void setSort(String sort) {
        this.sort = sort;
    }

    public boolean isIncludeArchived() {
        return includeArchived;
    }
    public void setIncludeArchived(boolean includeArchived) {
        this.includeArchived = includeArchived;
    }

    public Integer getDocsPerPage() {
        return docsPerPage;
    }
    public void setDocsPerPage(Integer docsPerPage) {
        this.docsPerPage = docsPerPage;
    }

    public Set<String> getRecallTypes() {
        return recallTypes;
    }
    public void setRecallTypes(Set<String> recallTypes) {
        this.recallTypes = recallTypes;
    }

    public Set<String> getRecallYearRanges() {
        return recallYearRanges;
    }
    public void setRecallYearRanges(Set<String> recallYearRanges) {
        this.recallYearRanges = recallYearRanges;
    }

    public Set<String> getAlertTypes() {
        return alertTypes;
    }
    public void setAlertTypes(Set<String> alertTypes) {
        this.alertTypes = alertTypes;
    }

    public Set<String> getVehicleMakes() {
        return vehicleMakes;
    }
    public void setVehicleMakes(Set<String> makes) {
        this.vehicleMakes = makes;
    }

    public Set<String> getVehicleYearRanges() {
        return vehicleYearRanges;
    }
    public void setVehicleYearRanges(Set<String> yearRanges) {
        this.vehicleYearRanges = yearRanges;
    }

    public Set<String> getAudiences() {
        return audiences;
    }
    public void setAudiences(Set<String> audiences) {
        this.audiences = audiences;
    }

    public Set<String> getCategories() {
        return categories;
    }
    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return StringUtils.isAllBlank(terms/*, sort, fileType*/);
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
