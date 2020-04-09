import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

	public String[] OUTPUT_HEADERS_EN = { "Theme", "Department", "Title", "Content Type(s)", "H2", "Keywords",
			"Modified Date", "Language", "AEM Content Type", "Page Performance", "URL", "Last Published date",
			"Comments" };

	public String[] OUTPUT_HEADERS_FR = {};

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
		public String lastPublishedDate = "";
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
			list.add("<a href=\"" + URL + "\">" + URL + "</a>");
			list.add(lastPublishedDate);
			list.add("");
			return list;

		}
	}

	public HashMap<String, OutputData> covidMap = new HashMap<String, OutputData>();
	public HashMap<String, OutputData> aemMap = new HashMap<String, OutputData>();
	public HashMap<String, OutputData> finalMap = new HashMap<String, OutputData>();

	public Map<String, String> themeEn = new HashMap<String, String>();
	public Map<String, String> themeFr = new HashMap<String, String>();

	public HashSet<String> usedThemesEn = new HashSet<String>();
	public HashSet<String> usedThemesFr = new HashSet<String>();

	public Map<String, String> urlDepartmentsEn = new HashMap<String, String>();
	public Map<String, String> urlDepartmentsFr = new HashMap<String, String>();
	public Map<String, String> aemDepartmentsEn = new HashMap<String, String>();
	public Map<String, String> aemDepartmentsFr = new HashMap<String, String>();

	public HashSet<String> UsedDepartmentsEn = new HashSet<String>();
	public HashSet<String> UsedDepartmentsFr = new HashSet<String>();

	DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public String importDate = "";

	public static void main(String args[]) throws Exception {
		Main main = new Main(args[0]);

		main.loadFrenchOutputHeaders();
		main.loadThemes();
		main.loadDepartments();
		main.loadData();
		main.mergeData();
		main.outputDataHTML("en");
		main.outputDataHTML("fr");

	}

	public class BiLang {
		String en;
		String fr;
		String aemName;
	}

	public Main(String importDate) {
		this.importDate = importDate;
	}

	public void dumpAEMFrench() throws Exception {
		Reader in = new FileReader("./data/aemdepartments.csv");
		Iterable<CSVRecord> aemRecords = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		HashMap<String, String> aemMapEN = new HashMap<String, String>();
		List<BiLang> outputList = new ArrayList<BiLang>();
		for (CSVRecord record : aemRecords) {
			aemMapEN.put(record.get("English").toUpperCase().trim(), record.get("Publisher organization name"));
		}

		Reader in2 = new FileReader("./data/deptNamesAndAcronyms.csv");
		Iterable<CSVRecord> deptNamesRecords = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in2);
		for (CSVRecord record : deptNamesRecords) {
			String appliedName = record.get("Applied EN");
			String legalName = record.get("Legal EN");
			List<String> toRemove = new ArrayList<String>();
			for (String aemKey : aemMapEN.keySet()) {
				if (aemKey.contains(appliedName.toUpperCase()) || appliedName.toUpperCase().contains(aemKey)) {
					BiLang biLang = new BiLang();
					biLang.en = legalName;
					biLang.fr = record.get("Legal FR");
					biLang.aemName = aemMapEN.get(aemKey);
					outputList.add(biLang);
					toRemove.add(appliedName.toUpperCase());
					break;
				} else if (aemKey.contains(legalName.toUpperCase()) || legalName.toUpperCase().contains(aemKey)) {
					BiLang biLang = new BiLang();
					biLang.en = legalName;
					biLang.fr = record.get("Legal FR");
					biLang.aemName = aemMapEN.get(aemKey);
					outputList.add(biLang);
					toRemove.add(legalName.toUpperCase());
					break;
				}
			}
			for (String remove : toRemove) {
				aemMapEN.remove(remove);
			}
		}
		final BufferedWriter writer = Files.newBufferedWriter(Paths.get("./export/aemdepartmentsfinal.csv"));
		final CSVFormat csvFormat = CSVFormat.EXCEL.withHeader("Publisher organization name", "English", "French");
		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
			for (BiLang biLang : outputList) {
				csvPrinter.printRecord(biLang.aemName, biLang.en, biLang.fr);
			}
		}

	}

	public void dumpAEMPublishers() throws Exception {
		HashSet<String> departments = new HashSet<String>();

		Reader in = new FileReader("./import/gcPageReport-publish-" + this.importDate + ".csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String publisher = record.get("Publisher organization name");
			publisher = publisher.replace("gc:institutions", "");
			String publishers[] = publisher.split("/");
			for (String pub : publishers) {
				departments.add(pub);
			}
		}

		final BufferedWriter writer = Files.newBufferedWriter(Paths.get("./export/aemdepartments.csv"));
		final CSVFormat csvFormat = CSVFormat.EXCEL.withHeader("Publisher organization name", "English", "French");
		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
			for (String publisher : departments) {
				if (publisher.length() > 0) {
					csvPrinter.printRecord(publisher, capitalizeWord(publisher.replace("-", " ")), "");
				}
			}
		}
	}

	public String capitalizeWord(String str) {
		if (str.length() > 0) {
			String words[] = str.split("\\s");
			String capitalizeWord = "";
			for (String w : words) {
				String first = w.substring(0, 1);
				String afterfirst = w.substring(1);
				capitalizeWord += first.toUpperCase() + afterfirst + " ";
			}
			return capitalizeWord.trim();
		} else {
			return str;
		}
	}

	public void loadFrenchOutputHeaders() throws Exception {
		List<String> list = new ArrayList<String>();
		HashMap<String, String> frenchHeader = new HashMap<String, String>();
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.withFirstRecordAsHeader()
				.parse(new FileReader("./data/headers.csv"));
		for (CSVRecord record : records2) {
			String en = record.get(0);
			String fr = record.get(1);
			frenchHeader.put(en, fr);
		}

		for (String enHeader : OUTPUT_HEADERS_EN) {
			if (frenchHeader.containsKey(enHeader)) {
				list.add(frenchHeader.get(enHeader));
			} else {
				list.add(enHeader);
			}
		}
		this.OUTPUT_HEADERS_FR = list.stream().toArray(String[]::new);
	}

	public void dumpHeaders() throws Exception {
		final BufferedWriter writer = Files.newBufferedWriter(Paths.get("./export/headers.csv"));
		final CSVFormat csvFormat = CSVFormat.EXCEL.withHeader("English", "French");
		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);) {
			for (String header : OUTPUT_HEADERS_EN) {
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
				if (!aemMap.get(url).contentTypes.equals("News")) {
					aemMap.get(url).contentTypes = data.contentTypes;
				}
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
			this.urlDepartmentsEn.put(url, en);
			this.urlDepartmentsFr.put(url, fr);
		}

		Reader in = new FileReader("./data/aemdepartments.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			String aemType = record.get(0);
			String en = record.get(1);
			String fr = record.get(2);
			this.aemDepartmentsEn.put(aemType, en);
			this.aemDepartmentsFr.put(aemType, fr);
		}

		this.UsedDepartmentsEn.add("N/A");
		this.UsedDepartmentsFr.add("N/A");

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

	public void outputData(String theme, String lang) throws Exception {
		final BufferedWriter writer = Files.newBufferedWriter(Paths.get("./data/" + theme + "-" + lang + ".csv"));
		final CSVFormat csvFormat = CSVFormat.EXCEL.withHeader(OUTPUT_HEADERS_EN);
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

		// Insert themes
		String themeList = "";

		List<String> themes = new ArrayList<String>(this.usedThemesEn);
		if (outputLang.contains("fr")) {
			themes = new ArrayList<String>(this.usedThemesFr);
		}
		themes.add("N/A");

		Collections.sort(themes, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				o1 = Normalizer.normalize(o1, Normalizer.Form.NFD);
				o2 = Normalizer.normalize(o2, Normalizer.Form.NFD);
				return o1.compareTo(o2);
			}
		});

		for (String theme : themes) {
			themeList += "<option value='" + theme + "'>" + theme + "</option>";
		}
		template = template.replace("<!-- THEMES -->", themeList);

		// Insert departments
		String deptList = "";
		List<String> depts = new ArrayList<String>(this.UsedDepartmentsEn);
		if (outputLang.contains("fr")) {
			depts = new ArrayList<String>(this.UsedDepartmentsFr);
		}

		Collections.sort(depts, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				o1 = Normalizer.normalize(o1, Normalizer.Form.NFD);
				o2 = Normalizer.normalize(o2, Normalizer.Form.NFD);
				return o1.compareTo(o2);
			}
		});

		for (String dept : depts) {
			deptList += "<option value='" + dept + "'>" + dept + "</option>";
		}
		template = template.replace("<!-- DEPARTMENTS -->", deptList);

		// Insert departments
		String labelList = "";
		String[] headers = OUTPUT_HEADERS_EN;
		if (outputLang.equals("fr")) {
			headers = OUTPUT_HEADERS_FR;
		}
		for (int i = 0; i < headers.length; i++) {
			if (i == (headers.length - 1)) {
				labelList += "<th class='export'>" + headers[i] + "</th>";
			} else {
				labelList += "<th>" + headers[i] + "</th>";
			}
		}
		template = template.replace("<!-- LABELS -->", labelList);

		// Insert toggle columns
		String togglecolumns = "";
		for (int i = 0; i < headers.length; i++) {
			if (i > 2) {
				//if (i != (headers.length - 1)) {
					togglecolumns += "<label for='toggle" + i + "'>"
							+ "<input type='checkbox' CHECKED class='toggle-vis' name='toggle" + i + "' id='toggle" + i
							+ "' data-column='" + i + "' />&nbsp;<span>" + headers[i] + "</span></label>&nbsp;";
				//} else {
				//	togglecolumns += "<label for='toggle" + i + "'>"
				//			+ "<input type='checkbox' class='toggle-vis' name='toggle" + i + "' id='toggle" + i
				//			+ "' data-column='" + i + "' />&nbsp;<span>" + headers[i] + "</span></label>&nbsp;";
				//}
			}
		}
		template = template.replace("<!-- TOGGLE COLUMNS -->", togglecolumns);

		template = template.replace("<!-- IMPORT DATE -->", this.importDate);

		String html = "";
		for (String url : finalMap.keySet()) {
			String lang = finalMap.get(url).language;
			if (lang.toLowerCase().contains(outputLang)) {
				html += "<tr>";
				List<String> list = finalMap.get(url).asList();
				for (int i = 0; i < list.size(); i++) {
					if (i != (list.size() - 1)) {
						String elem = list.get(i);
						html += "<td>" + elem + "</td>";
					}
					else {
						if (lang.contains("en")) {
						html += "<td><button class='btn'>Submit</button></td>";
						} else {
							html += "<td><button class='btn'>Soumettre</button></td>";	
						}
					}
				}
				html += "</tr>";
			}
		}
		template = template.replace("<!-- ROW DATA -->", html);

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
		boolean covid = false;
		String checkFields[] = { "Title", "Has Alert", "H2", "dcterms.subject", "desc", "Description", "Name",
				"Page title", "H1", "Keywords", "Primary topic", "Additional topics", "desc", "dcterms.subject",
				"Content type", "URL", "Public path" };
		String contentType = "";
		Date afterDate = null;
		try {
			afterDate = DATE_FORMAT.parse("2019-01-01");
		} catch (Exception e) {
		}

		Date modifiedDate = this.getLastModifiedDate(record);
		if (modifiedDate == null || modifiedDate.after(afterDate)) {
			// String url = "";
			// try {
			// url = record.get("Public path");
			// } catch (Exception e) {
			// }
			// System.out.println(url);
			for (String checkField : checkFields) {
				try {
					String data = record.get(checkField).toUpperCase().trim();
					if (data.contains("CANADA EMERGENCY RESPONSE BENEFIT")
							|| data.contains("PRESTATION CANADIENNE D’URGENCE") || data.contains("CERB")
							|| data.contains("COVID") || data.contains("CORONAVIRUS")
							|| (checkField.equals("Has Alert") && data.contains("TRUE"))) {
						covid = true;
						contentType += checkField + ": " + record.get(checkField) + "\n\r";
					} else if (checkField.equals("Content type") && (data.equals("GC:CONTENT-TYPES/STATEMENTS")
							|| data.equals("GC:CONTENT-TYPES/SPEECHES") || data.equals("GC:CONTENT-TYPES/BACKGROUNDERS")
							|| data.equals("GC:CONTENT-TYPES/NEWS-RELEASES")
							|| data.equals("GC:CONTENT-TYPES/MEDIA-ADVISORIES"))) {
						contentType += "Content type: News" + "\n\r";
					} else if (checkField.equals("Public path")
							|| checkField.equals("URL") && (data.contains("NEWS") || data.contains("NOUVELLES"))) {
						contentType += "Content type: News" + "\n\r";
						// System.out.println("Checkfield:"+checkField);
					}

				} catch (Exception e) {

				}
			}
		}
		if (covid) {
			return contentType;
		} else {
			return "";
		}
	}

	public Date getLastModifiedDate(CSVRecord record) {
		try {
			String lastModified = record.get("Last Modified");
			return DATE_FORMAT.parse(lastModified);
		} catch (Exception e) {
			// System.out.println(e.getMessage());
		}
		try {
			String lastModifiedDate = record.get("Last Modified date");
			return DATE_FORMAT.parse(lastModifiedDate);
		} catch (Exception e) {

		}
		return null;
	}

	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		for (int i = 0; i < items.length; i++) {
			if (inputStr.toLowerCase().contains(items[i].toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public String determineContentType(String contentTypeContent, String lang) {
		String substantiveContentTypes[] = { "Title", "H2", "dcterms.subject", "desc", "Description", "Name",
				"Page title", "H1", "Keywords", "Primary topic", "Additional topics" };
		String contentType;
		if (contentTypeContent.contains("Content type: News")) {
			if (lang.contains("en")) {
				contentType = "News";
			} else {
				contentType = "Nouvelles";
			}
		} else if (stringContainsItemFromList(contentTypeContent, substantiveContentTypes)) {

			if (lang.contains("en")) {
				contentType = "Main Covid-19 content";
			} else {
				contentType = "Contenu principal COVID";
			}
		} else if (contentTypeContent.contains("Has Alert")) {
			if (lang.contains("en")) {
				contentType = "Contains Covid Alert";
			} else {
				contentType = "Contient une alerte COVID";
			}
		} else {

			if (lang.equals("en")) {
				contentType = "Links to Covid pages";
			} else {
				contentType = "Liens vers des pages COVID";
			}
		}
		return contentType;
	}

	public String determineTheme(String url, String lang) {
		Map<String, String> themes = this.themeEn;
		HashSet<String> usedTheme = this.usedThemesEn;
		if (lang.toLowerCase().contains("fr")) {
			themes = this.themeFr;
			usedTheme = this.usedThemesFr;
		}
		for (String key : themes.keySet()) {
			if (url.contains(key)) {
				usedTheme.add(themes.get(key));
				return themes.get(key);
			}
		}
		return "N/A";
	}

	public String determineDept(String url, String lang, CSVRecord record) {
		Map<String, String> depts = this.urlDepartmentsEn;
		HashSet<String> usedDepts = this.UsedDepartmentsEn;
		if (lang.toLowerCase().contains("fr")) {
			depts = this.urlDepartmentsFr;
			usedDepts = this.UsedDepartmentsFr;
		}
		for (String key : urlDepartmentsEn.keySet()) {
			if (url.contains(key)) {
				String dept = depts.get(key);
				usedDepts.add(dept);
				return depts.get(key);
			}
		}
		// if not found in the department list then
		// Publisher organization name
		// Author
		String dept = "";
		try {
			String aemDept = record.get("Publisher organization name").replace("gc:institutions/", "");
			if (aemDept != null && !aemDept.equals("")) {
				if (lang.contains("en")) {
					dept = aemDepartmentsEn.get(aemDept);
				} else {
					dept = aemDepartmentsFr.get(aemDept);
				}
			}
		} catch (Exception e) {

		}
		if (dept == null || dept.equals("")) {
			try {
				dept = record.get("Author");
			} catch (Exception e2) {
			}
		}

		// one last hail mary
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
			String author = doc.select("meta[name=Author]").get(0).attr("content");
			dept = author;
			System.out.println(url + " - " + dept);
		} catch (Exception e) {
			try {
				String author = doc.select("meta[name=dcterms.creator]").get(0).attr("content");
				dept = author;
				System.out.println(url + " - " + dept);
			} catch (Exception e2) {
			}
		}

		if (dept.contains(",")) {
			String deptsplit[] = dept.split(",");
			dept = deptsplit[deptsplit.length - 1].trim();
		}
		if (dept.contains("none") || dept.contains("English name")) {
			dept = "";
		}

		if (dept != null && !dept.equals("")) {
			if (lang.contains("en")) {
				this.UsedDepartmentsEn.add(dept);
			} else {
				this.UsedDepartmentsFr.add(dept);
			}
			return dept;
		} else {

			return "N/A";
		}
	}

	public void loadData() throws Exception {
		System.out.println("");
		Reader in2 = new FileReader("./import/covid19-" + this.importDate + "_en.csv");
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in2);
		for (CSVRecord record : records2) {
			String contentTypeContent = this.contentTypeContent(record);
			if (!contentTypeContent.equals("")) {
				// System.out.println(record.get("URL"));
				OutputData outputData = new OutputData();
				outputData.title = record.get("Title");
				outputData.URL = record.get("URL");
				outputData.language = record.get("Language");
				outputData.department = this.determineDept(outputData.URL, outputData.language, record);
				outputData.theme = this.determineTheme(outputData.URL, outputData.language);
				outputData.h2 = record.get("H2");
				outputData.keywords = record.get("desc");
				outputData.contentTypes = this.determineContentType(this.contentTypeContent(record),
						outputData.language);
				outputData.modifiedDate = record.get("Last Modified");
				outputData.language = record.get("Language");
				this.covidMap.put(record.get("URL"), outputData);
			}
		}
		System.out.println("");
		Reader in3 = new FileReader("./import/covid19-" + this.importDate + "_fr.csv");
		Iterable<CSVRecord> records3 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in3);
		for (CSVRecord record : records3) {
			String contentTypeContent = this.contentTypeContent(record);
			if (!contentTypeContent.equals("")) {
				// System.out.println(record.get("URL"));
				OutputData outputData = new OutputData();
				outputData.title = record.get("Title");
				outputData.URL = record.get("URL");
				outputData.language = record.get("Language");
				outputData.department = this.determineDept(outputData.URL, outputData.language, record);
				outputData.theme = this.determineTheme(outputData.URL, outputData.language);
				outputData.h2 = record.get("H2");
				outputData.keywords = record.get("desc");
				outputData.contentTypes = this.determineContentType(this.contentTypeContent(record),
						outputData.language);
				outputData.modifiedDate = record.get("Last Modified");
				outputData.language = record.get("Language");
				this.covidMap.put(record.get("URL"), outputData);
			}
		}

		Reader in = new FileReader("./import/gcPageReport-publish-" + this.importDate + ".csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			OutputData outputData = new OutputData();
			String contentTypeContent = this.contentTypeContent(record);
			if (!contentTypeContent.equals("")) {
				outputData.title = record.get("Page title");
				outputData.URL = record.get("Public path");
				outputData.language = this.determineLanguage(outputData.URL, record.get("gcLanguage"));
				outputData.department = this.determineDept(outputData.URL, outputData.language, record);
				outputData.theme = this.determineTheme(outputData.URL, outputData.language);
				outputData.contentTypes = this.determineContentType(contentTypeContent, outputData.language);
				outputData.modifiedDate = record.get("Last Modified date");
				outputData.h2 = "";
				outputData.keywords = record.get("Keywords");
				outputData.AEMContentType = record.get("Content type");
				outputData.lastPublishedDate = record.get("Last Published date");
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
