package ca.canada.treasury.testbed.web.controller;

import static ca.canada.treasury.testbed.web.service.impl.SolrUtil.COLLECTION_COVID19;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.InputStreamResponseParser;
import org.apache.solr.client.solrj.impl.NoOpResponseParser;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.Cluster;
import org.apache.solr.client.solrj.response.ClusteringResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.util.NamedList;
import org.noggit.CharArr;
import org.noggit.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.canada.treasury.testbed.web.model.CovidDoc;
import ca.canada.treasury.testbed.web.model.CovidRequest;
import ca.canada.treasury.testbed.web.service.ISearchService;
import ca.canada.treasury.testbed.web.service.impl.SolrUtil;
import ca.canada.treasury.testbed.web.view.UITools;

@Controller
public class Covid19MonitorController {

	private static final Logger LOG = LoggerFactory.getLogger(Covid19MonitorController.class);
	
	

	@Autowired
	private ISearchService searchService;

	// alias name + solr name
	private static final Map<String, String> CSV_FLD_ALIASES = new TreeMap<>();
	static {
		CSV_FLD_ALIASES.put(CovidDoc.ID, "URL");
		CSV_FLD_ALIASES.put(CovidDoc.LANGUAGE, "Language");
		CSV_FLD_ALIASES.put(CovidDoc.TITLE, "Title");
		CSV_FLD_ALIASES.put(CovidDoc.HAS_ALERT, "Has Alert");
		CSV_FLD_ALIASES.put(CovidDoc.H2, "H2");
		CSV_FLD_ALIASES.put(CovidDoc.BC2_NAME, "Breadcrumb LVL2 Name");
		CSV_FLD_ALIASES.put(CovidDoc.BC2_URL, "Breadcrumb LVL2 URL");
		CSV_FLD_ALIASES.put(CovidDoc.BC3_NAME, "Breadcrumb LVL3 Name");
		CSV_FLD_ALIASES.put(CovidDoc.BC3_URL, "Breadcrumb LVL3 URL");
		CSV_FLD_ALIASES.put(CovidDoc.BC4_NAME, "Breadcrumb LVL4 Name");
		CSV_FLD_ALIASES.put(CovidDoc.BC4_URL, "Breadcrumb LVL4 URL");
		CSV_FLD_ALIASES.put(CovidDoc.LAST_MODIFIED, "Last Modified");
		CSV_FLD_ALIASES.put(CovidDoc.LAST_CRAWLED, "Last Crawled");
		CSV_FLD_ALIASES.put(CovidDoc.AUTHOR, "Author");
		CSV_FLD_ALIASES.put(CovidDoc.SUBJECT, "dcterms.subject");
		CSV_FLD_ALIASES.put(CovidDoc.AUDIENCE, "dcterms.audience");
		CSV_FLD_ALIASES.put(CovidDoc.CATEGORY, "dcterms.type");
		CSV_FLD_ALIASES.put(CovidDoc.DESC, "desc");
	}
	private static final Map<String, Col> XLSX_COLS = new ListOrderedMap<>();
	static {
		// Max width is 255 (characters)
		XLSX_COLS.put(CovidDoc.ID, new Col("URL", 60));
		XLSX_COLS.put(CovidDoc.LANGUAGE, new Col("Language", 5));
		XLSX_COLS.put(CovidDoc.TITLE, new Col("Title", 50));
		XLSX_COLS.put(CovidDoc.HAS_ALERT, new Col("Has Alert", 5));
		XLSX_COLS.put(CovidDoc.H2, new Col("H2", 30));
		XLSX_COLS.put(CovidDoc.BC2_NAME, new Col("Breadcrumb LVL2 Name", 30));
		XLSX_COLS.put(CovidDoc.BC2_URL, new Col("Breadcrumb LVL2 URL", 40));
		XLSX_COLS.put(CovidDoc.BC3_NAME, new Col("Breadcrumb LVL3 Name", 30));
		XLSX_COLS.put(CovidDoc.BC3_URL, new Col("Breadcrumb LVL3 URL", 40));
		XLSX_COLS.put(CovidDoc.BC4_NAME, new Col("Breadcrumb LVL4 Name", 30));
		XLSX_COLS.put(CovidDoc.BC4_URL, new Col("Breadcrumb LVL4 URL", 40));
		XLSX_COLS.put(CovidDoc.LAST_MODIFIED, new Col("Last Modified", 15));
		XLSX_COLS.put(CovidDoc.LAST_CRAWLED, new Col("Last Crawled", 15));
		XLSX_COLS.put(CovidDoc.AUTHOR, new Col("Author", 25));
		XLSX_COLS.put(CovidDoc.SUBJECT, new Col("dcterms.subject", 25));
		XLSX_COLS.put(CovidDoc.AUDIENCE, new Col("dcterms.audience", 25));
		XLSX_COLS.put(CovidDoc.CATEGORY, new Col("dcterms.type", 30));
		XLSX_COLS.put(CovidDoc.DESC, new Col("desc", 100));
	}

	private static class Col {
		String name;
		int width;

		public Col(String name, int width) {
			super();
			this.name = name;
			this.width = width;
		}
	}

	@Autowired
	private SolrClient solr;

	@GetMapping(value = "/covid19/rest/csv", produces = "text/csv")
	public @ResponseBody void restCSV(@RequestParam(required = false) String lang,
			@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "100") int rows, HttpServletResponse response)
			throws IOException, SolrServerException {
		SolrQuery q = new SolrQuery();
		q.setRequestHandler("/" + COLLECTION_COVID19 + "/select");

		q.set("wt", "csv");
		q.set("omitHeader", "true");
		
		q.setQuery("*:*");
		String fields = "*";
		q.setFields(fields);
		

		String fileLang = "";
		if (StringUtils.isNotBlank(lang)) {
			q.set("fq", "language:" + lang);
			fileLang = "_" + lang;
		} else {
			q.set("qf", "id");
		}

		q.setStart(start);
		q.setRows(rows);
		q.setSort(CovidDoc.LAST_MODIFIED, ORDER.desc);

		q.set("defType", "edismax");

		response.setHeader("Content-Disposition",
				"attachment; filename=covid19-" + LocalDate.now().toString() + fileLang + ".csv");

		LOG.debug("Solr {} query: {}", SolrUtil.COLLECTION_COVID19, q);
		QueryRequest solrReq = new QueryRequest(q);
		NoOpResponseParser responseParser = new NoOpResponseParser();
		responseParser.setWriterType("csv");
		solrReq.setResponseParser(responseParser);
		String csv = (String) solr.request(solrReq).get("response");
		Reader reader = new StringReader(csv);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase().withEscape('\\').withTrim());
                
        Writer writer = response.getWriter();

        List<String> outputHeader = new ArrayList<String>();
        for (String key : XLSX_COLS.keySet()) {
        	outputHeader.add(XLSX_COLS.get(key).name);
        }
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL
                .withHeader(outputHeader.toArray(new String[outputHeader.size()])));
        for (CSVRecord csvRecord : csvParser) {
           List<String> record = new ArrayList<String>();
           for (String key : XLSX_COLS.keySet()) {
           	record.add(csvRecord.get(key));
           }
           csvPrinter.printRecord(record);
        }
        csvParser.close();
        csvPrinter.close();
		writer.close();
	}

	@CrossOrigin(origins = "*")
	@GetMapping(value = "/covid19/rest/xlsx",
			// produces = "application/vnd.ms-excel")
			produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public void restURLs(@RequestParam(required = false) String lang,
			@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "100") int rows, HttpServletResponse response)
			throws IOException, SolrServerException {
		SolrQuery q = new SolrQuery();
		q.setRequestHandler("/" + COLLECTION_COVID19 + "/select");

		q.set("wt", "xlsx");
		q.set("omitHeader", "true");
		q.setQuery("*:*");

		for (Entry<String, Col> en : XLSX_COLS.entrySet()) {
			String solrField = en.getKey();
			Col col = en.getValue();
			q.addField(solrField);
			q.set("colwidth." + solrField, col.width);
			q.set("colname." + solrField, col.name);
		}

		String fileLang = "";
		if (StringUtils.isNotBlank(lang)) {
			q.set("fq", "language:" + lang);
			fileLang = "_" + lang;
		} else {
			q.set("qf", "id");
		}

		q.setStart(start);
		q.setRows(rows);
		q.setSort(CovidDoc.LAST_MODIFIED, ORDER.desc);

		q.set("defType", "edismax");

		response.setHeader("Content-Disposition",
				"attachment; filename=covid19-" + LocalDate.now().toString() + fileLang + ".xlsx");

		LOG.debug("Solr {} query: {}", SolrUtil.COLLECTION_COVID19, q);
		QueryRequest solrReq = new QueryRequest(q);
		solrReq.setResponseParser(new InputStreamResponseParser("xlsx"));

		NamedList<Object> resp = solr.request(solrReq);
		InputStream xlsxStream = (InputStream) resp.get("stream");
		IOUtils.copy(xlsxStream, response.getOutputStream());
	}

	@CrossOrigin
	@GetMapping(value = "/covid19/rest/cluster", produces = "application/json")
	public @ResponseBody String restCluster(CovidRequest req, String clusterEngine)
			throws IOException, SolrServerException {

		SolrQuery q = new SolrQuery();

		q.setRequestHandler("/" + COLLECTION_COVID19 + "/clustering");
		q.set("clustering.engine", StringUtils.defaultIfBlank(clusterEngine, "lingo"));
		q.setFields("id");
		q.setRows(500);

		// --- dismax params ---
		q.set("defType", "edismax");

		addFilterQueries(q, req);

		LOG.debug("Solr {} query: {}", COLLECTION_COVID19, q);

		QueryResponse qr = searchService.badIdeaNativeSearch(q);
		ClusteringResponse cr = qr.getClusteringResponse();
		if (cr == null) {
			return "[]";
		}
		CharArr out = new CharArr();
		JSONWriter json = new JSONWriter(out);
		json.startArray();
		boolean writeSep = false;
		for (Cluster cluster : cr.getClusters()) {
			if (!cluster.isOtherTopics()) {
				for (String label : cluster.getLabels()) {
					if (writeSep) {
						json.writeValueSeparator();
					}
					json.startObject();

					json.writeString("text");
					json.writeNameSeparator();
					json.writeString(label);

					json.writeValueSeparator();

					json.writeString("weight");
					json.writeNameSeparator();
					BigDecimal bd = BigDecimal.valueOf(cluster.getScore());
					bd = bd.setScale(2, RoundingMode.HALF_UP);
					json.write(bd.floatValue());

					json.writeValueSeparator();

					json.writeString("link");
					json.writeNameSeparator();
					json.writeString("#");

					json.endObject();
					writeSep = true;
				}
			}
		}
		json.endArray();
		return out.toString();
	}

	@CrossOrigin
	@GetMapping(value = "/covid19/monitor/rest", produces = "application/json")
	public @ResponseBody String restSearch(CovidRequest req) throws IOException, SolrServerException {

		LOG.debug("COVID request: {}", req);

		SolrQuery q = new SolrQuery();
		q.setRequestHandler("/" + COLLECTION_COVID19 + "/select");

		q.set("wt", "json");
		q.set("indent", true);
		q.set("omitHeader", "true");

		q.setFields(CovidDoc.ID, CovidDoc.LANGUAGE, CovidDoc.LAST_MODIFIED, CovidDoc.LAST_CRAWLED, CovidDoc.HAS_ALERT,
				CovidDoc.H2, CovidDoc.BC2_NAME, CovidDoc.BC2_URL, CovidDoc.BC3_NAME, CovidDoc.BC3_URL,
				CovidDoc.BC4_NAME, CovidDoc.BC4_URL, CovidDoc.AUTHOR, CovidDoc.SUBJECT, CovidDoc.AUDIENCE,
				CovidDoc.CATEGORY, CovidDoc.TITLE, CovidDoc.LINK_TITLE, "score");

		q.setStart(req.getOffset());
		q.setRows(req.getLimit());

		String sortField = StringUtils.defaultIfBlank(req.getSort(), CovidDoc.LAST_MODIFIED);
		String sortOrder = StringUtils.defaultIfBlank(req.getOrder(), "desc");
		q.setSort(SortClause.create(sortField, sortOrder));

		q.set("defType", "edismax");

		// filters
		q.setFacet(true);
		q.setFacetMinCount(1);
		q.setFacetLimit(20);
		q.addFacetField("{!ex=h2}h2");
		q.addFacetField("{!ex=bc2name}bc2_name");

		// filters
		addFilterQueries(q, req);

		LOG.debug("Solr {} query: {}", SolrUtil.COLLECTION_COVID19, q);

		QueryRequest solrReq = new QueryRequest(q);
		NoOpResponseParser rawJsonResponseParser = new NoOpResponseParser();
		rawJsonResponseParser.setWriterType("json");
		solrReq.setResponseParser(rawJsonResponseParser);
		return (String) solr.request(solrReq).get("response");
	}

	private void addFilterQueries(SolrQuery q, CovidRequest req) {
		q.set("qf", "content title^10");
		String terms = req.getSearch();
		terms = StringUtils.strip(terms, " \"");
		if (StringUtils.isBlank(terms)) {
			terms = "*:*";
		} else {
			terms = StringUtils.wrap(ClientUtils.escapeQueryChars(terms), '"');
		}
		q.setQuery(terms);

		// SolrUtil.addFilterQuery(q, CovidDoc.CONTENT, req.getContent());
		SolrUtil.addFilterQuery(q, CovidDoc.LANGUAGE, req.getLanguage());
		SolrUtil.addFieldFilterQuery(q, CovidDoc.H2, req.getH2(), "{!tag=h2}");
		SolrUtil.addFieldFilterQuery(q, CovidDoc.BC2_NAME, req.getBreadcrumb2(), "{!tag=bc2name}");
	}

	@GetMapping(value = "/covid19/monitor")
	public String monitor(Model model) {
		model.addAttribute("ui", UITools.instance());
		return "covid19-monitor";
	}
}