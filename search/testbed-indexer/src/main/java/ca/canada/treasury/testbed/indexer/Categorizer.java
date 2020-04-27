package ca.canada.treasury.testbed.indexer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Creates proper categorization for a recall.
 * @author Pascal Essiembre
 */
public final class Categorizer {

    private static final Categorizer INSTANCE = new Categorizer();

    private static final MultiKeyMap<String, CategoryMapping> CATEGORIES =
            new MultiKeyMap<>();
    private static final MultiKeyMap<String, CategoryMapping>
            ADDITIONAL_CATEGORIES = new MultiKeyMap<>();

    // Using ordinal position for CSV order.
    public enum Column {
        RECALL_TYPE,
        SOURCE_CATEGORY,
        TARGET_CATEGORY,
        TARGET_SUBCATEGORY,
        ADDITIONAL_RECALL_TYPE,
        ADDITIONAL_CATEGORY,
        ADDITIONAL_SUBCATEGORY
    }


    private Categorizer() {
        super();
    }

    public List<CategoryMapping> getMappings(String type, String sourceCat) {
        List<CategoryMapping> mappings = new ArrayList<>(1);
        CategoryMapping mapping = CATEGORIES.get(type, sourceCat);
        if (mapping != null) {
            mappings.add(mapping);
            CategoryMapping plusMapping =
                    ADDITIONAL_CATEGORIES.get(type, sourceCat);
            if (plusMapping != null) {
                mappings.add(plusMapping);
            }
        }
        return mappings;
    }

//    public CategoryMapping getMapping(String type, String sourceCat) {
//        return CATEGORIES.get(type, sourceCat);
//    }

    public static synchronized Categorizer get() throws IOException {
        if (!CATEGORIES.isEmpty()) {
            return INSTANCE;
        }
        for (CSVRecord rec : CSVFormat.RFC4180.withHeader().parse(
                new InputStreamReader(Categorizer.class.getResourceAsStream(
                        "/categories.csv")))) {
            String type = rec.get(Column.RECALL_TYPE.ordinal()).toLowerCase();
            String sourceCat = rec.get(Column.SOURCE_CATEGORY.ordinal());
            String cat = rec.get(Column.TARGET_CATEGORY.ordinal());
            String sub = rec.get(Column.TARGET_SUBCATEGORY.ordinal());
            String plusType = rec.get(
                    Column.ADDITIONAL_RECALL_TYPE.ordinal()).toLowerCase();
            String plusCat = rec.get(Column.ADDITIONAL_CATEGORY.ordinal());
            String plusSub = rec.get(Column.ADDITIONAL_SUBCATEGORY.ordinal());
            if (StringUtils.isBlank(cat)) {
                cat = sourceCat;
            }
            CATEGORIES.put(type, sourceCat, new CategoryMapping(type, cat, sub));
            if (StringUtils.isNotBlank(plusType)) {
                ADDITIONAL_CATEGORIES.put(type, sourceCat,
                        new CategoryMapping(plusType, plusCat, plusSub));
            }
        }
        return INSTANCE;
    }

    public static class CategoryMapping {
        private final String type;
        private final String category;
        private final String subCategory;
        private final String path;
        public CategoryMapping(
                String type, String category, String subCategory) {
            super();
            this.type = type;
            this.category = category;
            this.subCategory = subCategory;
            String p = type + "|" + category;
            if (StringUtils.isNotBlank(subCategory)) {
                p += "|" + subCategory;
            }
            this.path = p;
        }
        public String getPath() {
            return path;
        }
        public String getType() {
            return type;
        }
        public String getCategory() {
            return category;
        }
        public String getSubCategory() {
            return subCategory;
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
}
