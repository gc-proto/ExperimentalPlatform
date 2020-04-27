package ca.canada.treasury.testbed.web.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Search suggestion.
 */
public class SearchSuggestion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String query;
    private String markup;

    public SearchSuggestion() {
        super();
    }

    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }

    public String getMarkup() {
        return markup;
    }
    public void setMarkup(String markup) {
        this.markup = markup;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(query);
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
