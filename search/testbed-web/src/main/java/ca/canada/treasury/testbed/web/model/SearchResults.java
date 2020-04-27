package ca.canada.treasury.testbed.web.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Search results.
 * @param <T> result type
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public class SearchResults<T>// extends Serializable>
        extends AbstractList<T> {//implements Serializable {

    //private static final long serialVersionUID = 1L;

    private long numFound;
    private long start;

    @JsonProperty
    private final List<T> results = new ArrayList<>();

    public SearchResults() {
        super();
    }
    public SearchResults(
            Collection<? extends T> c, long numFound, long start) {
        this.results.addAll(c);
        this.numFound = numFound;
        this.start = start;
    }


    public long getNumFound() {
        return numFound;
    }
    public void setNumFound(long numFound) {
        this.numFound = numFound;
    }

    public long getStart() {
        return start;
    }
    public void setStart(long start) {
        this.start = start;
    }

    @Override
    public T get(int index) {
        return results.get(index);
    }
    @Override
    public int size() {
        return results.size();
    }

    @Override
    public T set(int index, T element) {
        return results.set(index, element);
    }
    @Override
    public void add(int index, T element) {
        results.add(index, element);
    }
    @Override
    public T remove(int index) {
        return results.remove(index);
    }

//    @Override
//    public Iterator<T> iterator() {
//        return results.iterator();
//    }

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
