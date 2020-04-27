package ca.canada.treasury.testbed.web.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Recall implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_VEHICLES = "vehicles";
    public static final String TYPE_HEALTH = "health";
    public static final String TYPE_CONSUMER = "consumer";
    public static final String TYPE_FOOD = "food";

    private String id;
    private String recallNo;
    private LocalDate date;
    private String title;
    private String description;
    private String department;
    private String url;
    private String alertType;
    private final List<String> types = new ArrayList<>();
    private final List<String> audiences = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private String summary;
    private Float score;
    private String explain;

    // Next few are for giving context only (would normally not exist).
    private final List<String> descriptionFields = new ArrayList<>();
    private final List<String> catchAllFields = new ArrayList<>();

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecallNo() {
        return recallNo;
    }
    public void setRecallNo(String recallNo) {
        this.recallNo = recallNo;
    }

    public List<String> getTypes() {
        return types;
    }
    public void setTypes(List<String> types) {
        this.types.clear();
        if (types != null) {
            this.types.addAll(types);
        }
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate recallDate) {
        this.date = recallDate;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String comment) {
        this.description = comment;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getCatchAllFields() {
        return catchAllFields;
    }
    public void setCatchAllFields(List<String> catchAllFields) {
        this.catchAllFields.clear();
        if (catchAllFields != null) {
            this.catchAllFields.addAll(catchAllFields);
        }
    }

    public List<String> getDescriptionFields() {
        return descriptionFields;
    }
    public void setDescriptionFields(List<String> descriptionFields) {
        this.descriptionFields.clear();
        if (descriptionFields != null) {
            this.descriptionFields.addAll(descriptionFields);
        }
    }

    public List<String> getAudiences() {
        return audiences;
    }
    public void setAudiences(List<String> audiences) {
        this.audiences.clear();
        if (audiences != null) {
            this.audiences.addAll(audiences);
        }
    }

    public List<String> getCategories() {
        return categories;
    }
    public void setCategories(List<String> categories) {
        this.categories.clear();
        if (categories != null) {
            this.categories.addAll(categories);
        }
    }

    public String getAlertType() {
        return alertType;
    }
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public Float getScore() {
        return score;
    }
    public void setScore(Float score) {
        this.score = score;
    }

    public String getExplain() {
        return explain;
    }
    public void setExplain(String explain) {
        this.explain = explain;
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
