import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Main {

	public static String[] AEM_HEADER = { "Name", "Page title", "H1", "Language (jcr:language)", "Page path",
			"Opposite language page title", "Opposite language page path", "Hide in navigation",
			"Show left navigation menu", "Breadcrumb parent", "Date created", "Date issued", "Last Modified date",
			"Last Modified by", "Date modified overridden", "Date modified override", "Last Published date",
			"Last Published by", "Last Unpublished date", "Last Unpublished by", "Review date",
			"Owner organization name", "Publisher organization name", "Licence", "Description", "Keywords",
			"Primary topic", "Additional topics", "Content type", "Audience", "Geographic region name", "Subject",
			"Minister", "Free subject", "Contributor", "Content provider", "ISBN", "ISSN",
			"Departmental catalogue number", "Template", "Public path", "Locked by", "gcLanguage", "showRightRail",
			"No index", "No follow", "Navigation title", "Open graph image", "Open graph image alt text" };

	public static String[] SEARCH_HEADERS = { "URL", "Language", "Title", "Has Alert", "H2", "Breadcrumb LVL2 Name",
			"Breadcrumb LVL2 URL", "Breadcrumb LVL3 Name", "Breadcrumb LVL3 URL", "Breadcrumb LVL4 Name",
			"Breadcrumb LVL4 URL", "Last Modified", "Last Crawled", "Author", "dcterms.subject", "dcterms.audience",
			"dcterms.type", "desc" };

	public static String[] MERGED_HEADERS = { "Theme", "Department", "Title", "Content Type(s)", "H2", "Keywords",
			"Modified Date", "Language", "AEM Content Type", "Page Performance", "Comments" };

	public class OutputData {
		public String department;
		public String theme;
		public String title;
		public String URL;
		public String contentTypes;
		public String h2;
		public String keywords;
		public String modifiedDate;
		public String language;
		public String AEMContentType = "";
		public String pagePerformance = "";
		public String comments = "";

		public List<String> asList() {
			List<String> list = new ArrayList<String>();
			list.add(theme);
			list.add(department);
			list.add("<a href=\"" + URL + "\">" + title + "</a>");
			list.add(contentTypes);
			list.add(h2);
			list.add(keywords);
			list.add(modifiedDate);
			list.add(language);
			list.add(AEMContentType);
			java.util.Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String format = formatter.format(date);
			list.add("<a href=\"https://pageperformance.tbs.alpha.canada.ca?url=" + URL + "&start=2020-01-01" + "&end="
					+ format + "\">" + title + "</a>");
			list.add("");
			return list;

		}
	}

	public HashMap<String, OutputData> covidMap = new HashMap<String, OutputData>();
	public HashMap<String, OutputData> aemMap = new HashMap<String, OutputData>();
	public HashMap<String, OutputData> finalMap = new HashMap<String, OutputData>();

	public HashMap<String, String> themeEn = new HashMap<String, String>();
	public HashMap<String, String> themeFr = new HashMap<String, String>();

	public HashMap<String, String> departmentsEn = new HashMap<String, String>();
	public HashMap<String, String> departmentsFr = new HashMap<String, String>();

	public static void main(String args[]) throws Exception {
		Main main = new Main();
		main.dumpHeaders();
		main.loadThemes();
		main.loadDepartments();
		main.loadData();
		main.mergeData();
		main.outputDataHTML("en");
		main.outputDataHTML("fr");
		// main.outputURLMatch();
	}

	public void dumpHeaders() throws Exception {
		final BufferedWriter writer = Files.newBufferedWriter(Paths.get("./export/headers.csv"));
		final CSVFormat csvFormat = CSVFormat.EXCEL.withHeader("English", "French");
		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);) {
			for (String header : MERGED_HEADERS) {
				csvPrinter.printRecord(header, "");
			}
			for (String header : SEARCH_HEADERS) {
				csvPrinter.printRecord(header, "");
			}
			for (String header : AEM_HEADER) {
				csvPrinter.printRecord(header, "");
			}
		}
	}

	public void mergeData() {
		for (String url : aemMap.keySet()) {
			// OutputData tmpData = aemMap.get(url);
			if (covidMap.containsKey(url)) {
				OutputData data = covidMap.remove(url);
				aemMap.get(url).h2 += data.h2;
				if (aemMap.get(url).keywords.equals("")) {
					aemMap.get(url).keywords += data.keywords;
				}
				aemMap.get(url).contentTypes = data.contentTypes;
			}
		}
		finalMap.putAll(aemMap);
		finalMap.putAll(covidMap);
	}

	private static String readLineByLineJava8(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

	public void loadDepartments() throws Exception {
		Reader in2 = new FileReader("./data/departments.csv");
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in2);
		for (CSVRecord record : records2) {
			String url = record.get(2);
			String en = record.get(0);
			String fr = record.get(1);
			this.departmentsEn.put(url, en);
			this.departmentsFr.put(url, fr);
		}

	}

	public void loadThemes() throws Exception {
		Reader in2 = new FileReader("./data/themes_en.csv");
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.parse(in2);
		for (CSVRecord record : records2) {
			// System.out.println(record.get("URL"));
			this.themeEn.put(record.get(0), record.get(1));
		}

		Reader in1 = new FileReader("./data/themes_fr.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in1);
		for (CSVRecord record : records) {
			// System.out.println(record.get("URL"));
			this.themeFr.put(record.get(0), record.get(1));
		}
	}

	public void outputData(String lang) throws Exception {

	}

	public void outputData(String theme, String lang) throws Exception {
		final BufferedWriter writer = Files.newBufferedWriter(Paths.get("./data/" + theme + "-" + lang + ".csv"));
		final CSVFormat csvFormat = CSVFormat.EXCEL.withHeader(MERGED_HEADERS);
		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);) {
			for (String url : finalMap.keySet())
				csvPrinter.printRecord(finalMap.get(url).asList());
		}
	}

	public void writeToFile(String content, String fileName) throws IOException {
		FileWriter fileWriter = new FileWriter(fileName);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print(content);
		printWriter.close();
	}

	public void outputDataHTML(String outputLang) throws Exception {
		String template = readLineByLineJava8("./data/template_" + outputLang + ".html");
		String html = "";
		for (String url : finalMap.keySet()) {
			String lang = finalMap.get(url).language;
			if (lang.toLowerCase().contains(outputLang)) {
				html += "<tr>";
				List<String> list = finalMap.get(url).asList();
				for (int i = 0; i < list.size(); i++) {
					String elem = list.get(i);
					html += "<td>" + elem + "</td>";
				}
				html += "</tr>";
			}
		}
		template = template.replace("<!-- ROW DATA -->", html);

		// Insert themes
		String themeList = "";
		HashSet<String> themes = new HashSet<String>(this.themeEn.values());
		if (outputLang.contains("fr")) {
			themes = new HashSet<String>(this.themeFr.values());
		}

		for (String theme : themes) {
			themeList += "<option value='" + theme + "'>" + theme + "</option>";
		}
		template = template.replace("<!-- THEMES -->", themeList);

		// Insert departments
		String deptList = "";
		HashSet<String> depts = new HashSet<String>(this.departmentsEn.values());
		if (outputLang.contains("fr")) {
			themes = new HashSet<String>(this.departmentsFr.values());
		}

		for (String dept : depts) {
			deptList += "<option value='" + dept + "'>" + dept + "</option>";
		}
		template = template.replace("<!-- DEPARTMENTS -->", deptList);

		// Insert departments
		String labelList = "";
		for (String label : MERGED_HEADERS) {
			labelList += "<th>" + label + "</th>";
		}
		template = template.replace("<!-- LABELS -->", labelList);

		// Insert toggle columns
		String togglecolumns = "";
		for (int i = 0; i < MERGED_HEADERS.length; i++) {
			if (i > 2) {
				togglecolumns += "<a class='toggle-vis' data-column='" + i + "' href=\"" + MERGED_HEADERS[i] + "\">"
						+ MERGED_HEADERS[i] + "</a> - ";
			}
		}
		togglecolumns = togglecolumns.substring(0, togglecolumns.length() - 3);
		template = template.replace("<!-- TOGGLE COLUMNS -->", togglecolumns);

		writeToFile(template,
				"../docker/site-optimization/docker/images/covid19inv_nginx/covid19_" + outputLang + ".html");
	}

	public void outputURLMatch() throws Exception {
		final BufferedWriter writer = Files.newBufferedWriter(Paths.get("./data/themeMatch2.csv"));
		final CSVFormat csvFormat = CSVFormat.EXCEL.withHeader("Theme/Org English", "Theme/Org French", "URLs");
		HashSet<String> set = new HashSet<String>();
		for (String url : finalMap.keySet()) {
			if (!url.contains("www.canada.ca")) {
				url = url.replace("http://", "").replace("https://", "");
				url = url.substring(0, url.indexOf("/"));
				set.add(url);
			}

		}

		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);) {
			for (String url : set) {
				Document document = Jsoup.connect("http://" + url).get();
				// Element english = null;
				// Element french = null;
				Elements creators = document.select("meta[name=dcterms.creator]");
				if (creators.size() > 1) {
					if (creators.get(0).hasAttr("lang") && creators.get(0).attr("lang").equals("fr")) {
						csvPrinter.printRecord(creators.get(1).attr("content"), creators.get(0).attr("content"), url);
					} else if (creators.get(1).hasAttr("lang") && creators.get(1).attr("lang").equals("fr")) {
						csvPrinter.printRecord(creators.get(0).attr("content"), creators.get(1).attr("content"), url);
					} else if (creators.get(0).hasAttr("lang") && creators.get(0).attr("lang").equals("en")) {
						csvPrinter.printRecord(creators.get(0).attr("content"), creators.get(1).attr("content"), url);
					} else if (creators.get(1).hasAttr("lang") && creators.get(1).attr("lang").equals("en")) {
						csvPrinter.printRecord(creators.get(1).attr("content"), creators.get(0).attr("content"), url);
					} else {
						csvPrinter.printRecord(creators.get(0).attr("content"), creators.get(1).attr("content"), url);
					}
				} else {

				}

			}
		}
	}

	public String contentTypeContent(CSVRecord record) {
		String checkFields[] = { "Title", "Has Alert", "H2", "dcterms.subject", "desc", "Description", "Name",
				"Page title", "H1", "Keywords", "Primary topic", "Additional topics" };
		String contentType = "";
		for (String checkField : checkFields) {
			try {
				String data = record.get(checkField).toUpperCase();
				if (data.contains("COVID") || data.contains("CORONAVIRUS")
						|| (checkField.equals("Has Alert") && data.contains("TRUE"))) {
					contentType += checkField + ": " + record.get(checkField) + "\n\r";
				}
			} catch (Exception e) {

			}
		}
		return contentType;
	}

	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		for (int i = 0; i < items.length; i++) {
			if (inputStr.toLowerCase().contains(items[i].toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public String determineContentType(String contentTypeContent) {
		String substantiveContentTypes[] = { "Title", "H2", "dcterms.subject", "desc", "Description", "Name",
				"Page title", "H1", "Keywords", "Primary topic", "Additional topics" };
		String contentType;
		if (stringContainsItemFromList(contentTypeContent, substantiveContentTypes)) {
			contentType = "Main Covid-19 content";
		} else if (contentTypeContent.contains("Has Alert")) {
			contentType = "Contains Covid Alert";
		} else {
			contentType = "Links to Covid pages";
		}
		return contentType;
	}

	public String determineTheme(String url, String lang) {
		// TODO put other themes in check.
		if (url.contains("www.canada.ca")) {
			HashMap<String, String> themes = this.themeEn;
			if (lang.toLowerCase().contains("fr")) {
				themes = this.themeFr;
			}
			for (String key : themes.keySet()) {
				if (url.contains(key)) {
					return themes.get(key);
				}
			}
		}
		return "N/A";
	}

	public String determineDept(String url, String lang) {
		HashMap<String, String> depts = this.departmentsEn;
		if (lang.toLowerCase().contains("fr")) {
			depts = this.departmentsFr;
		}
		for (String key : departmentsEn.keySet()) {
			if (url.contains(key)) {
				return depts.get(key);
			}
		}
		return "N/A";
	}

	public void loadData() throws Exception {
		System.out.println("");
		Reader in2 = new FileReader("./import/covid19-2020-03-31_en.csv");
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in2);
		for (CSVRecord record : records2) {
			// System.out.println(record.get("URL"));
			OutputData outputData = new OutputData();
			outputData.title = record.get("Title");
			outputData.URL = record.get("URL");
			outputData.language = record.get("Language");
			outputData.department = this.determineDept(outputData.URL, outputData.language);
			outputData.theme = this.determineTheme(outputData.URL, outputData.language);
			outputData.h2 = record.get("H2");
			outputData.keywords = record.get("desc");
			outputData.contentTypes = this.determineContentType(this.contentTypeContent(record));
			outputData.modifiedDate = record.get("Last Modified");
			outputData.language = record.get("Language");
			this.covidMap.put(record.get("URL"), outputData);
		}
		System.out.println("");
		Reader in3 = new FileReader("./import/covid19-2020-03-31_fr.csv");
		Iterable<CSVRecord> records3 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in3);
		for (CSVRecord record : records3) {
			// System.out.println(record.get("URL"));
			OutputData outputData = new OutputData();
			outputData.title = record.get("Title");
			outputData.URL = record.get("URL");
			outputData.language = record.get("Language");
			outputData.department = this.determineDept(outputData.URL, outputData.language);
			outputData.theme = this.determineTheme(outputData.URL, outputData.language);
			outputData.h2 = record.get("H2");
			outputData.keywords = record.get("desc");
			outputData.contentTypes = this.determineContentType(this.contentTypeContent(record));
			outputData.modifiedDate = record.get("Last Modified");
			outputData.language = record.get("Language");
			this.covidMap.put(record.get("URL"), outputData);
		}

		Reader in = new FileReader("./import/gcPageReport-publish-03-31-2020.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			OutputData outputData = new OutputData();
			String contentTypeContent = this.contentTypeContent(record);
			if (!contentTypeContent.equals("")) {
				outputData.title = record.get("Page title");
				outputData.URL = record.get("Public path");
				outputData.language = this.determineLanguage(outputData.URL, record.get("gcLanguage"));
				outputData.department = this.determineDept(outputData.URL, outputData.language);
				outputData.theme = this.determineTheme(outputData.URL, outputData.language);
				outputData.contentTypes = this.determineContentType(contentTypeContent);
				outputData.modifiedDate = record.get("Last Modified date");
				outputData.h2 = "";
				outputData.keywords = record.get("Keywords");
				outputData.AEMContentType = record.get("Content type");
				this.aemMap.put(record.get("Public path"), outputData);
			}
		}

	}

	public String determineLanguage(String url, String langRecordValue) {
		if (langRecordValue == null || langRecordValue.contentEquals("")) {
			if (url.contains("/en/")) {
				return "en";
			} else {
				return "fr";
			}
		} else {
			return langRecordValue;
		}
	}

	public void matchURLs() throws Exception {
		int count = 0;
		for (String covidURL : covidMap.keySet()) {
			if (covidURL.toUpperCase().contains("WWW.CANADA.CA")) {
				for (String aemURL : aemMap.keySet()) {
					if (aemURL.equals(covidURL)) {
						System.out.println("Match URL: " + aemURL);
						count++;
						break;
					}

				}
			}
		}
		System.out.println("Number of matches: " + count);
	}

	public void outputURLs() throws Exception {
		Reader in = new FileReader("./data/gcPageReport-publish-03-31-2020.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			System.out.println(record.get("Page path"));
			break;
		}
		System.out.println("");
		Reader in2 = new FileReader("./data/covid19-2020-03-31_en.csv");
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in2);
		for (CSVRecord record : records2) {
			System.out.println(record.get("URL"));
			break;
		}
	}

	public void outputHeaders() throws Exception {
		Reader in = new FileReader("./data/gcPageReport-publish-03-31-2020.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
		for (CSVRecord record : records) {
			for (int i = 0; i < 49; i++) {
				System.out.print("\"" + record.get(i) + "\",");
			}
			break;
		}
		System.out.println("");
		Reader in2 = new FileReader("./data/covid19-2020-03-31_en.csv");
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.parse(in2);
		for (CSVRecord record : records2) {
			for (int i = 0; i < 18; i++) {
				System.out.print("\"" + record.get(i) + "\",");
			}
			break;
		}
	}
}
