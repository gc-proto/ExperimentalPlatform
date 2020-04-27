package ca.canada.treasury.testbed.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Search response.
 * @param <T> type of results
 */
public class SearchResponse<T> {// extends Serializable> implements Serializable {

    //private static final long serialVersionUID = 1L;

    private final SearchResults<T> results = new SearchResults<>();
    private final ListOrderedMap<String, SearchFacet> facets =
            new ListOrderedMap<>();
    private final List<SearchCluster> clusters = new ArrayList<>();

    private SearchSuggestion spellCheck = new SearchSuggestion();

    public SearchResponse() {
        super();
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    public SearchResults<T> getResults() {
        return results;
    }
    public void setResults(SearchResults<T> documents) {
        this.results.clear();
        this.results.addAll(documents);
        this.results.setNumFound(documents.getNumFound());
        this.results.setStart(documents.getStart());
    }

    public List<SearchFacet> getFacets() {
        return facets.valueList();
    }
    public void setFacets(List<SearchFacet> facets) {
        this.facets.clear();
        for (SearchFacet facet : facets) {
            this.facets.put(facet.getName(), facet);
        }
    }
    public SearchFacet getFacet(String name) {
        return facets.get(name);
    }
    public boolean isFacetsEmpty() {
        for (SearchFacet f : facets.valueList()) {
            if (!f.isEmpty()) {
                return false;
            }
        }
        return true;
    }


    public List<SearchCluster> getClusters() {
        return clusters;
    }
    public void setClusters(List<SearchCluster> clusters) {
        this.clusters.clear();
        if (clusters != null) {
            this.clusters.addAll(clusters);
        }
    }


    public SearchSuggestion getSpellCheck() {
        return spellCheck;
    }
    public void setSpellCheck(SearchSuggestion spellCheck) {
        this.spellCheck = spellCheck;
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
