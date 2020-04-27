package ca.canada.treasury.testbed.indexer;

import org.apache.commons.lang3.ArrayUtils;

/* Fields we are dealing with and how to handle them.
 * Only fields we care about are here.  Otherwise other ones
 * are only used to go in the "catch all" Solr field as reference only.
 */
public enum RecallField {
    // Usually direct Solr fields mapping:
    RECALL_ID     ("id",                        P.SCHEMA),
    RECALL_NO     ("recall_no",                 P.SCHEMA),
    RECALL_DATE   ("recall_date",               P.SCHEMA, P.DATETIME),
    TITLE         ("recall_title",              P.SCHEMA),
    DEPTARTMENT   ("recall_dept",               P.SCHEMA),
    AUDIENCES     ("recall_audiences",          P.SCHEMA, P.MULTI),
    CATEGORIES    ("recall_categories",         P.SCHEMA, P.MULTI),
    CATEG_ORIG    ("recall_categories_orig",    P.SCHEMA, P.MULTI),
    ALERT_TYPE    ("recall_alert_type",         P.SCHEMA),

    // Usually computed fields for Solr:
    RECALL_YEAR   ("recall_year",               P.SCHEMA),
    RECALL_TYPES   ("recall_types",               P.SCHEMA),
    URL           ("recall_url",                P.SCHEMA),
    CATCH_ALL     ("_text_",                    P.SCHEMA),
    DESC          ("recall_desc",               P.SCHEMA),
    DESC_FIELDS   ("recall_desc_fields",        P.SCHEMA),
    CAT_HIER      ("recall_cat_hier",           P.SCHEMA),
    SUB_CATS      ("recall_subcats",            P.SCHEMA),
    BARCODE       ("recall_barcode",            P.SCHEMA),
    BARCODE_TYPE  ("recall_barcode_type",       P.SCHEMA),
    BRAND         ("recall_brand",              P.SCHEMA, P.MULTI),

    // Description/summary fields:
    INFO_PRODUCT  ("info_summary_product_name", P.DESC),
    INFO_ISSUE    ("info_summary_issue",        P.DESC),
    INFO_TODO     ("info_summary_what_to_do",   P.DESC),
    INFO_ACTION   ("info_consumer_action",      P.DESC),
    INFO_SUBTITLE ("info_subtitle",             P.DESC),
    INFO_INTRO    ("info_intro_text",           P.DESC),
    INFO_REASON   ("info_awr_reason",           P.DESC),
    INFO_TEXT     ("info_full_awr_text",        P.DESC),
    INFO_TRD_NAME ("info_trade_name",           P.DESC),
    INFO_COM_NAME ("info_common_name",          P.DESC),
    INFO_ADD_NAME ("info_additional_name",      P.DESC),
    INFO_PROB_ISSUE ("info_problem_issue",      P.DESC),

    // For a static summary (consumer only for now):
    SUMMARY       ("recall_summary",            P.SCHEMA),

    // For suggester
    SUGGEST       ("smart_suggest",             P.SCHEMA),

    // Other fields (e.g., used to compute Solr fields):
    PROVIDER_ACRONYM ("provider_acronym"),
    DEPT_ACRONYM  ("department_acronym"),
    PRODUCT_UPC   ("product_upc"),
    DRUG_DIN      ("drug_din"),
    PRODUCT_SKU   ("info_sku"),
    COMMON_NAME   ("common_name"),
    TRADE_NAME    ("trade_name"),
    CIE_NAME      ("cie_name"),
    MAKE          ("cie_make"),
    UPDATED_DATE  ("updated_date"),
    CREATED_DATE  ("created_date"),
    ;

    // Field "poperties"
    public enum P {
        SCHEMA,  // A dedicated field in Solr schema.
        MULTI,   // Is a multi-value field.
        DESC,    // Is a description field (for summary generation)
        DATETIME // Is a date-time field.
    }

    String name;
    private P[] features;
    private RecallField(String name, P... features) {
        this.name = name;
        this.features = features;
    }
    public static RecallField of(String name) {
        for (RecallField f: values()) {
            if (f.name.equals(name)) { return f; }
        }
        return null;
    }
    public boolean is(P feature) {
        return ArrayUtils.contains(features, feature);
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return name;
    }
}