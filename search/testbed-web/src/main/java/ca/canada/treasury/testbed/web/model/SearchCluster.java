package ca.canada.treasury.testbed.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Search cluster.
 * @author Pascal Essiembre
 */
public class SearchCluster {

    private String label;
    private int percent;
    private final List<String> docIds = new ArrayList<>();
    private boolean otherTopics;
    private double score;

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public int getPercent() {
        return percent;
    }
    public void setPercent(int percent) {
        this.percent = percent;
    }

    public List<String> getDocIds() {
        return docIds;
    }
    public void setDocIds(List<String> docIds) {
        this.docIds.clear();
        if (docIds != null) {
            this.docIds.addAll(docIds);
        }
    }

    public boolean isOtherTopics() {
        return otherTopics;
    }
    public void setOtherTopics(boolean otherTopics) {
        this.otherTopics = otherTopics;
    }

    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
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
