package ca.canada.treasury.testbed.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class VehicleRecall extends Recall {

    private static final long serialVersionUID = 1L;

    private final List<Integer> years = new ArrayList<>();
    private String make;
    private String model;
    private Integer numberAffected;
    private String systemType;
    private String notificationType;
    private String groupKey;

    public List<Integer> getYears() {
        return years;
    }
    public void setYears(List<Integer> years) {
        this.years.clear();
        if (years != null) {
            this.years.addAll(years);
        }
    }

    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public String getGroupKey() {
        return groupKey;
    }
    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public Integer getNumberAffected() {
        return numberAffected;
    }
    public void setNumberAffected(Integer numberAffected) {
        this.numberAffected = numberAffected;
    }

    public String getSystemType() {
        return systemType;
    }
    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getNotificationType() {
        return notificationType;
    }
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
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
