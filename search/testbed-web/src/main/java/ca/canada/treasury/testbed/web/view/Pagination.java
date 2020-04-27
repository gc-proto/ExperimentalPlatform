package ca.canada.treasury.testbed.web.view;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.canada.treasury.testbed.web.model.SearchRequest;
import ca.canada.treasury.testbed.web.model.SearchResponse;

/**
 * A class facilitating results pagination.
 * @author Pascal Essiembre
 */
public class Pagination implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int currentPage;
    private final int pageCount;
    private final int maxPagesLinks;
    private final long firstDocIndex;
    private final long lastDocIndex;
    private final long numFound;

    public Pagination(SearchRequest request,
            SearchResponse<?> response, int maxPagesLinks) {
        super();
        Objects.requireNonNull(request, "'request' must not be null");
        Objects.requireNonNull(response, "'response' must not be null");
        this.numFound = response.getResults().getNumFound();
        this.maxPagesLinks = maxPagesLinks;
        this.currentPage = Math.max(request.getPageIndex(), 1);
        if (numFound > request.getDocsPerPage()) {
            this.pageCount = (int) Math.ceil(
                    (double) numFound / request.getDocsPerPage());
        } else {
            this.pageCount = 1;
        }
        this.firstDocIndex = response.getResults().getStart() + 1;
        this.lastDocIndex = response.getResults().getStart()
                + response.getResults().size();
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }
    public long getFirstDocIndex() {
        return firstDocIndex;
    }
    public long getLastDocIndex() {
        return lastDocIndex;
    }
    public boolean isCurrentPageFirst() {
        return currentPage == 1;
    }
    public boolean isCurrentPageLast() {
        return currentPage == getPageCount();
    }
    public Integer getPreviousPage() {
        if (!isCurrentPageFirst()) {
            return currentPage - 1;
        }
        return null;
    }
    public Integer getNextPage() {
        if (!isCurrentPageLast()) {
            return currentPage + 1;
        }
        return null;
    }
    public int getMaxPagesLinks() {
        return maxPagesLinks;
    }

    public int[] getPageNumbers() {
        if (pageCount < 1) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }

        int mod = maxPagesLinks % 2;
        int delta = maxPagesLinks / 2;
        int left = currentPage - delta + 1 - mod;
        int right = currentPage + delta;

        int leftOverflow = 0;
        if (left < 1) {
            leftOverflow = 1 - left;
            left = 1;
        }
        int rightOverflow = 0;
        if (right > pageCount) {
            rightOverflow = right - pageCount;
            right = pageCount;
        }

        // Add left/right overflow to right/left (if we can)
        right = Math.min(right + leftOverflow, pageCount);
        left = Math.max(left - rightOverflow, 1);

        int[] pages = new int[right - left + 1];
        for (int i = 0; i < pages.length; i++) {
            pages[i] = left + i;
        }
        return pages;
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
