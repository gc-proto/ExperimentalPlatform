package ca.canada.treasury.testbed.indexer.impl;

import static ca.canada.treasury.testbed.indexer.RecallField.ALERT_TYPE;
import static ca.canada.treasury.testbed.indexer.RecallField.AUDIENCES;
import static ca.canada.treasury.testbed.indexer.RecallField.BARCODE;
import static ca.canada.treasury.testbed.indexer.RecallField.BARCODE_TYPE;
import static ca.canada.treasury.testbed.indexer.RecallField.BRAND;
import static ca.canada.treasury.testbed.indexer.RecallField.CATCH_ALL;
import static ca.canada.treasury.testbed.indexer.RecallField.CATEGORIES;
import static ca.canada.treasury.testbed.indexer.RecallField.CATEG_ORIG;
import static ca.canada.treasury.testbed.indexer.RecallField.CAT_HIER;
import static ca.canada.treasury.testbed.indexer.RecallField.CIE_NAME;
import static ca.canada.treasury.testbed.indexer.RecallField.COMMON_NAME;
import static ca.canada.treasury.testbed.indexer.RecallField.CREATED_DATE;
import static ca.canada.treasury.testbed.indexer.RecallField.DEPT_ACRONYM;
import static ca.canada.treasury.testbed.indexer.RecallField.DESC;
import static ca.canada.treasury.testbed.indexer.RecallField.DESC_FIELDS;
import static ca.canada.treasury.testbed.indexer.RecallField.DRUG_DIN;
import static ca.canada.treasury.testbed.indexer.RecallField.INFO_ISSUE;
import static ca.canada.treasury.testbed.indexer.RecallField.INFO_PRODUCT;
import static ca.canada.treasury.testbed.indexer.RecallField.INFO_TODO;
import static ca.canada.treasury.testbed.indexer.RecallField.MAKE;
import static ca.canada.treasury.testbed.indexer.RecallField.PRODUCT_SKU;
import static ca.canada.treasury.testbed.indexer.RecallField.PRODUCT_UPC;
import static ca.canada.treasury.testbed.indexer.RecallField.PROVIDER_ACRONYM;
import static ca.canada.treasury.testbed.indexer.RecallField.RECALL_DATE;
import static ca.canada.treasury.testbed.indexer.RecallField.RECALL_ID;
import static ca.canada.treasury.testbed.indexer.RecallField.RECALL_TYPES;
import static ca.canada.treasury.testbed.indexer.RecallField.RECALL_YEAR;
import static ca.canada.treasury.testbed.indexer.RecallField.SUB_CATS;
import static ca.canada.treasury.testbed.indexer.RecallField.SUGGEST;
import static ca.canada.treasury.testbed.indexer.RecallField.SUMMARY;
import static ca.canada.treasury.testbed.indexer.RecallField.TITLE;
import static ca.canada.treasury.testbed.indexer.RecallField.UPDATED_DATE;
import static ca.canada.treasury.testbed.indexer.RecallField.URL;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.canada.treasury.testbed.indexer.Categorizer;
import ca.canada.treasury.testbed.indexer.Categorizer.CategoryMapping;
import ca.canada.treasury.testbed.indexer.IndexerUtil;
import ca.canada.treasury.testbed.indexer.RecallField;
import ca.canada.treasury.testbed.indexer.RecallField.P;

/**
 * <p>
 * Index Health Canada Recall data from Health Canada Recalls database.
 * <b>IMPORTANT:</b> Does not index vehicles data given it is out-of-date
 * in the recall database.  We use {@link OpenDataVehicleRecallsIndexer} for
 * vehicles.
 * </p>
 *
 * @author Pascal Essiembre
 */
public class DBHealthRecallsIndexer {

    private static final Logger LOG =
            LoggerFactory.getLogger(DBHealthRecallsIndexer.class);

    public static final int DEFAULT_BATCH_SIZE = 1000;
    public static final int DEFAULT_MAX = -1;
    public static final int COMMIT_WITHIN_MS = 5 * 60 * 1000;
    public static final String ARG_SOLR = "solr";
    public static final String ARG_BATCH = "batch";
    public static final String ARG_MAX = "max";
    public static final String ARG_DBURL = "dburl";
    public static final String ARG_DBUSER = "dbuser";
    public static final String ARG_DBPASS = "dbpass";
    public static final String ARG_SKIP_COMMIT = "skipCommit";
    public static final String TWO_YEARS_AGO =
            LocalDateTime.now().minusYears(2).toString();

    private static final Map<String, String> RECALL_TYPE_MAPPINGS =
            MapUtils.putAll(new HashMap<>(), new String[] {
        "CFIA-SB", "food",
        "TC-SSG",  "vehicles",
        "HPFB",    "health",
        "MHP",     "health",
        "SCOMMS",  "health",
        "CPS",     "consumer",
    });


    private final Config config;

    public DBHealthRecallsIndexer(Config config) {
        super();
        this.config = config;
    }

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(Option.builder(ARG_SOLR).desc(
                "Solr URL (e.g. http://localhost:8983/solr). Optional with "
              + "'download' flag.")
                .hasArg().required().build());
        options.addOption(Option.builder(ARG_BATCH).desc(
                "Document batch size for indexing. "
              + "Default is " + DEFAULT_BATCH_SIZE + ".")
                .hasArg().build());
        options.addOption(Option.builder(ARG_MAX).desc(
                "Maximum number of documents to send for indexing. "
              + "Default is unlimited.")
                .hasArg().build());
        options.addOption(Option.builder(ARG_DBURL).desc(
                "Database JDBC connection URL").hasArg().required().build());
        options.addOption(Option.builder(ARG_DBUSER).desc(
                "Database user name.").hasArg().build());
        options.addOption(Option.builder(ARG_DBPASS).desc(
                "Database password.").hasArg().build());
        options.addOption(Option.builder(ARG_SKIP_COMMIT).desc(
                "Skip hard commit to Solr.").build());

        try {
            CommandLine line = parser.parse(options, args);
            Config cfg = new Config();
            cfg.solrURL = line.getOptionValue(ARG_SOLR);
            cfg.batchSize = NumberUtils.toInt(
                    line.getOptionValue(ARG_BATCH), DEFAULT_BATCH_SIZE);
            cfg.max = NumberUtils.toInt(
                    line.getOptionValue(ARG_MAX), DEFAULT_MAX);
            cfg.dbURL = line.getOptionValue(ARG_DBURL);
            cfg.dbUsername = line.getOptionValue(ARG_DBUSER);
            cfg.dbPassword = line.getOptionValue(ARG_DBPASS);
            cfg.skipCommit = line.hasOption(ARG_SKIP_COMMIT);

            new DBHealthRecallsIndexer(cfg).start();

        } catch (Exception exp) {
            LOG.error("OMG! What did you do?", exp);
            new HelpFormatter().printHelp("<this_app>", options);
            System.exit(-1);
        }
    }

    public void start() throws Exception {
        StopWatch timer = new StopWatch();
        timer.start();

        LOG.info("Indexing from \"{}\" into \"{}\"",
                config.dbURL, config.solrURL);
        index();
        LOG.info("Indexing completed.");
        timer.stop();
        LOG.info("DONE. Elapsed time: {}", timer);
    }

    public void index() throws IOException, SolrServerException, SQLException {

        HttpSolrClient solr =
                new HttpSolrClient.Builder(config.solrURL).build();
        List<SolrInputDocument> docList = new ArrayList<>();
        String sql = IOUtils.toString(getClass().getResourceAsStream(
                "/recalls.sql"), StandardCharsets.UTF_8);
        long cnt = 0;
        try (Connection connection = DriverManager.getConnection(
                config.dbURL, config.dbUsername, config.dbPassword);
             Statement st = connection.createStatement()) {
            if (config.max > -1) {
                st.setMaxRows(config.max);
            }
            try (ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    if (isRecordValid(rs)) {
                        docList.add(toSolrDoc(rs));
                        cnt++;
                        if (cnt % config.batchSize == 0) {
                            indexBatch(solr, docList);
                            LOG.info("  {} documents sent for indexing.", cnt);
                            if (!config.skipCommit && cnt == config.batchSize) {
                                LOG.info("Committing 1st batch now so you can "
                                        + "start playing...");
                                solr.commit();
                            }
                        }
                    }
                }
            }
        }

        if (!docList.isEmpty()) {
            indexBatch(solr, docList);
            LOG.info("Indexed {} documents.", cnt);
            if (!config.skipCommit) {
                LOG.info("Committing...");
                solr.commit();
                solr.optimize();
            }
        }
    }

    private boolean isRecordValid(ResultSet rs) throws SQLException {
        //TODO: for now, vehicle data from Transport Canada is outdated
        // in the health recalls database, so we reject them and rely
        // on Open Data for now.
        return !"TC-SSG".equals(rs.getString("provider_acronym"));
    }


    @SuppressWarnings("unchecked")
    private SolrInputDocument toSolrDoc(ResultSet rs)
            throws SQLException, IOException {
        Map<String, String> allFields = new ListOrderedMap<>();
        List<String> descriptions = new ArrayList<>();
        List<String> descFields = new ArrayList<>();
        Map<RecallField, Object> solrFields = new EnumMap<>(RecallField.class);

        //--- Set "Simple" fields: ---

        ResultSetMetaData m = rs.getMetaData();
        for (int i = 1; i <= m.getColumnCount(); i++) {
            String dbCol = m.getColumnLabel(i);
            Object dbVal = rs.getObject(i);
            if (IndexerUtil.isNotEmpty(dbVal)) {
                String strVal = IndexerUtil.toString(dbVal);
                allFields.put(dbCol, strVal);
                RecallField f = RecallField.of(dbCol);
                if (f == null) {
                    continue;
                }
                if (f.is(P.MULTI)) {
                    // db val is never null here, we check earlier.
                    dbVal = new TreeSet<>(
                            Arrays.asList(StringUtils.split(strVal, '|')));
                }
                if (f.is(P.DESC)) {
                    descFields.add(dbCol);
                    descriptions.add(IndexerUtil.stripHtml(strVal));
                }
                if (f.is(P.DATETIME)) {
                    dbVal = strVal;
                }
                if (f.is(P.SCHEMA)) {
                    solrFields.put(f, dbVal);
                }
            }
        }


        //--- Compute new or transform existing fields: ---

        // recall date/year
        String recallDate = StringUtils.firstNonBlank(
                get(allFields, RECALL_DATE),
                get(allFields, UPDATED_DATE),
                get(allFields, CREATED_DATE));
        String recallYear = StringUtils.substringBefore(recallDate, "-");
        solrFields.put(RECALL_DATE, recallDate);
        solrFields.put(RECALL_YEAR, recallYear);
        set(allFields, RECALL_YEAR, recallYear);

        // recall type
        String recallType = RECALL_TYPE_MAPPINGS.get(
                get(allFields, PROVIDER_ACRONYM));
        solrFields.put(RECALL_TYPES, recallType);
        set(allFields, RECALL_TYPES, recallType);

        // recall URL
        String url = createURL(allFields);
        solrFields.put(URL, url);
        set(allFields, URL, url);

        // description
        if (!descFields.isEmpty()) {
            solrFields.put(DESC, descriptions);
            set(allFields, DESC, descriptions);
            solrFields.put(DESC_FIELDS, descFields);
            set(allFields, DESC_FIELDS, descFields);
        }

        // barcodes
        String upc = IndexerUtil.stripHtml(get(allFields, PRODUCT_UPC));
        String din = IndexerUtil.stripHtml(get(allFields, DRUG_DIN));
        String sku = IndexerUtil.stripHtml(get(allFields, PRODUCT_SKU));
        if (StringUtils.isNotBlank(upc)) {
            solrFields.put(BARCODE, upc);
            solrFields.put(BARCODE_TYPE, "upc");
        } else if (StringUtils.isNotBlank(din)) {
            solrFields.put(BARCODE, din);
            solrFields.put(BARCODE_TYPE, "din");
        } else if (StringUtils.isNotBlank(sku)) {
            solrFields.put(BARCODE, sku);
            solrFields.put(BARCODE_TYPE, "sku");
        }

        // brand (if multiple found, only store one).
        String cieName = cleanBrand(get(allFields, CIE_NAME));
        String make = cleanBrand(get(allFields, MAKE));
        String commonName = cleanBrand(get(allFields, COMMON_NAME));
        String brand = StringUtils.firstNonBlank(commonName, make, cieName);
        if (StringUtils.isNotBlank(brand)) {
            solrFields.put(BRAND, brand);
        }

        // Clean the data.
        cleanRecall(solrFields);
        set(allFields, CATEG_ORIG, solrFields.get(CATEG_ORIG));

        // store "allFields" to "catch all"
        solrFields.put(CATCH_ALL, allFields.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList()));

        //--- Convert solr fields to Solr doc: ---
        SolrInputDocument doc = new SolrInputDocument();
        solrFields.entrySet().forEach(e -> {
            addField(doc, e.getKey(), e.getValue());
        });

        // consumer product static summary
        if ("consumer".equals(recallType)) {
            StringBuilder b = new StringBuilder();
            addToSummary(b, "Product", get(allFields, INFO_PRODUCT));
            addToSummary(b, "Issue", get(allFields, INFO_ISSUE));
            addToSummary(b, "What to do", get(allFields, INFO_TODO));
            if (b.length() > 0) {
                doc.addField(SUMMARY.getName(), "<ul>" + b + "</ul>");
            }
        }

        // store those we want for recall-specific freetext suggestions:
        // use brand, desc, and title
        List<String> suggs = new ArrayList<>();
        suggs.add(get(allFields, TITLE));
        suggs.add(get(allFields, BRAND));
        if (!descFields.isEmpty()) {
            suggs.add(get(allFields, DESC));
        }
        for (String rt : (Set<String>) solrFields.get(RECALL_TYPES)) {
            if (!StringUtils.equalsAny(rt, "food", "health")
                    ||  TWO_YEARS_AGO.compareTo(recallDate) <= 0) {
                doc.addField(rt + "_suggest", suggs);
            }
        }

        return doc;
    }

    private String cleanBrand(String brand) {
        if (brand == null) {
            return null;
        }
        String b = brand.trim();

        // strip html and remove duplicate spaces
        b = IndexerUtil.stripHtml(b);
        b = StringUtils.normalizeSpace(b);

        // strip parenthesis content
        b = b.replaceFirst("^(.*?)\\(.*$", "$1").trim();

        // remove brands that could be a numbered company
        b = b.replaceFirst("^\\d{5,}\\s.*$", "");
        b = b.replaceFirst("^\\d{4,}-\\d{4,}\\s.*$", "");

        // remove brands only containing non-alphanumeric characters
        b = b.replaceFirst("^[^a-zA-Z\\d]+$", "");

        // strip surrounding quotes
        b = b.replaceFirst("^[\"'\\s]+(.*)[\"'\\s]+$", "$1");

        // replace/strip some values
        b = b.replace("&amp;", "&");
        b = b.replace("®", "");
        b = b.replaceFirst("(?i)(.*)\\s+("
                + "Ltd|Limited|Lté|Inc|Corp|ULC|LLC"
                + ")\\.?$", "$1");
        b = b.replaceFirst("(.*)\\,+$", "$1");

        // remove brands with more than 5 words.
        b = StringUtils.countMatches(b, ' ') < 6 ? b : "";

        return b;
    }


    private void addToSummary(StringBuilder b, String heading, String value) {
        if (StringUtils.isNotBlank(value)) {
            b.append("<li><strong>")
             .append(heading)
             .append(":</strong> ")
             .append(IndexerUtil.stripHtml(value))
             .append("</li>\n");
        }
    }

    @SuppressWarnings("unchecked")
    private void cleanRecall(Map<RecallField, Object> solrFields)
            throws IOException {
        Set<String> categories = (Set<String>) solrFields.computeIfAbsent(
                CATEGORIES, k -> new TreeSet<>());
        Set<String> categoriesOrig = (Set<String>) solrFields.computeIfAbsent(
                CATEG_ORIG, k -> new TreeSet<>());
        Set<String> audiences = (Set<String>) solrFields.computeIfAbsent(
                AUDIENCES, k -> new TreeSet<>());

        // move maternity entry to audience
        String aud = "Affects children, pregnant or breast feeding women";
        if (categories.remove(aud)) {
            audiences.add(aud);
        }

        // Add an entry to audience if Children's Products
        String childrenProducts = "Children's Products";
        if (categories.contains(childrenProducts)) {
            audiences.add(aud);
        }

        // If no category, create a default one.
        if (categories.isEmpty()) {
            categories.add("Uncategorized");
        }

        // Capitalize audience
        List<String> auds = audiences.stream().map(
                IndexerUtil::capitalizeFully).collect(Collectors.toList());
        audiences.clear();
        audiences.addAll(auds);

        // Re-categorize
        String origType = (String) solrFields.get(RECALL_TYPES);
        Set<String> recallTypes = new TreeSet<>(Arrays.asList(origType));
        Set<String> newCats = new TreeSet<>();
        Set<String> newSubCats = new TreeSet<>();
        Set<String> newPaths = new TreeSet<>();
        Set<String> smartSuggests = new TreeSet<>();
        for (String c : categories) {
            List<CategoryMapping> cms =
                    Categorizer.get().getMappings(origType, c);
            // if no mapping, simply add recall type to value
            if (cms.isEmpty()) {
                newCats.add(c);
                newPaths.add(origType + "|" + c);
                LOG.debug("No category mapping for {}: \"{}\"", origType, c);
            // if mapping, simply add recall type to value
            } else {
                for (CategoryMapping cm : cms) {
                    newCats.add(cm.getCategory());
                    newSubCats.add(cm.getSubCategory());
                    newPaths.add(cm.getPath());
                    recallTypes.add(cm.getType());
                    smartSuggests.add(cm.getCategory()
                            + " []recallTypes:" + cm.getType()
                            + "^categories:" + cm.getPath());
                    if (StringUtils.isNotBlank(cm.getSubCategory())) {
                        smartSuggests.add(cm.getSubCategory()
                                + " []recallTypes:" + cm.getType()
                                + "^categories:" + cm.getPath());
                    }
                }
            }
        }
        solrFields.put(CATEGORIES, newCats);
        solrFields.put(SUB_CATS, newSubCats);
        solrFields.put(CAT_HIER, newPaths);
        solrFields.put(SUGGEST, smartSuggests);
        solrFields.put(CATEG_ORIG, categoriesOrig);
        solrFields.put(RECALL_TYPES, recallTypes);
    }

    //XXX This is flacky at best.  Formal URLs (or their patterns)
    //XXX should be provided by the client.
    private String createURL(Map<String, String> allFields) {
        String recallId = get(allFields, RECALL_ID);
        String year = get(allFields, RECALL_YEAR);
        String deptAcronym = get(allFields, DEPT_ACRONYM);
        String alertType = get(allFields, ALERT_TYPE);

        String path = null;
        // One of Advisory or Recall:
        String typeSuffix = StringUtils.left(alertType, 1).toLowerCase();

        if (StringUtils.isBlank(deptAcronym)) {
            LOG.error("MUST FIX: No department acronym for recall_id: {}",
                    recallId);
            return "MUST_FIX_NO_DEPT_ACRONYM_FOR_RECALL_" + recallId;
        }
        switch (deptAcronym) {
        case "TC":
            // we are not indexing TC from the DB
            LOG.error("MUST FIX: Got a 'TC' "
                    + "department_acronym for recall_id '{}'.", recallId);
            break;
        case "HC":
            path = "hc-sc";
            break;
        case "CFIA":
            path = "inspection";
            break;
        default:
            break;
        }

        if (path == null) {
            LOG.error("MUST FIX: Could not figure out department_acronym '{}' "
                    + "for recall_id '{}'.", deptAcronym, recallId);
            return "MUST_FIX_UNKNOWN_URL_PATH_FOR_DEPT_ACRONYM_" + deptAcronym;
        }
        return "https://www.healthycanadians.gc.ca/"
                + "recall-alert-rappel-avis/" + path
                + "/" + year
                + "/" + recallId + typeSuffix + "-eng.php";
    }

    private void indexBatch(SolrClient solr, List<SolrInputDocument> docList)
            throws SolrServerException, IOException {
        solr.add(docList, COMMIT_WITHIN_MS);
        docList.clear();
    }

    private String get(Map<String, String> allFields, RecallField field) {
        return allFields.get(field.getName());
    }
    private void set(
            Map<String, String> allFields, RecallField field, Object value) {
        allFields.put(field.getName(),
                IndexerUtil.stripHtml(IndexerUtil.toString(value)));
    }

    private void addField(
            SolrInputDocument doc, RecallField field, Object value) {
        if (!IndexerUtil.isEmpty(value)) {
            doc.addField(field.getName(), IndexerUtil.stripHtml(value));
        }
    }

    public static class Config {
        private String solrURL;
        private int batchSize;
        private int max;
        private String dbURL;
        private String dbUsername;
        private String dbPassword;
        private boolean skipCommit;
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
