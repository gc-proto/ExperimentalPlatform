package ca.canada.treasury.testbed.web.model;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RecallGroup extends VehicleRecall {
    private static final long serialVersionUID = 1L;
    private Set<Recall> recalls = new TreeSet<>((r1, r2) -> {
        VehicleRecall v1 = (VehicleRecall) r1;
        VehicleRecall v2 = (VehicleRecall) r2;
        return new CompareToBuilder()
                .append(v1.getDate(), v2.getDate())
                .append(v1.getMake(), v2.getMake())
                .append(v1.getModel(), v2.getModel())
                .build();
    });
    public Set<Recall> getRecalls() {
        return recalls;
    }
    public void setRecalls(Set<Recall> recalls) {
        this.recalls = recalls;
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