package ca.canada.treasury.testbed.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Search facet field.
 * @author Pascal Essiembre
 */
public class SearchFacet {

    private String name;
    private long valueCount;
    private final List<SearchFacetValue> values = new ArrayList<>();

    public boolean isEmpty() {
        return valueCount == 0;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getValueCount() {
        return valueCount;
    }
    public void setValueCount(long valueCount) {
        this.valueCount = valueCount;
    }

    public List<SearchFacetValue> getValues() {
        return values;
    }
    public void setValues(List<SearchFacetValue> values) {
        this.values.clear();
        this.values.addAll(values);
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
