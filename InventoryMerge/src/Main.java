import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

	public static String[] MERGED_HEADERS = { "Theme/Department", "Title", "URL", "Content Type(s)", "Sub-Topics",
			"Modified Date", "Language" };

	public class OutputData {
		public String organization;
		public String title;
		public String URL;
		public String contentTypes;
		public String contentTypeContent;
		public String modifiedDate;
		public String language;

		public List<String> asList() {
			List<String> list = new ArrayList<String>();
			list.add(organization);
			list.add(title);
			list.add(URL);
			list.add(contentTypes);
			list.add(contentTypeContent);
			list.add(modifiedDate);
			list.add(language);
			return list;

		}
	}

	public HashMap<String, OutputData> covidMap = new HashMap<String, OutputData>();
	public HashMap<String, OutputData> aemMap = new HashMap<String, OutputData>();
	public HashMap<String, OutputData> finalMap = new HashMap<String, OutputData>();

	public HashMap<String, String> themeEn = new HashMap<String, String>();
	public HashMap<String, String> themeFr = new HashMap<String, String>();

	public static void main(String args[]) throws Exception {
		Main main = new Main();
		main.loadThemes();
		main.loadData();
		main.mergeData();
		main.outputData();
		main.outputDataHTML("en");
		//main.outputDataHTML("fr");
		// main.outputURLMatch();
	}

	public void mergeData() {
		for (String url : aemMap.keySet()) {
			OutputData tmpData = aemMap.get(url);
			if (covidMap.containsKey(url)) {
				OutputData data = covidMap.remove(url);
				aemMap.get(url).contentTypeContent += data.contentTypeContent;
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

	public void outputData() throws Exception {
		final BufferedWriter writer = Files.newBufferedWriter(Paths.get("./data/output.csv"));
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
			if (url.contains("www.canada.ca") && lang.toLowerCase().contains("en")) {
				html += "<tr>";
				List<String> list = finalMap.get(url).asList();
				for (int i = 0; i < list.size(); i++) {
					String elem = list.get(i);
					if (i != 1 && i != 2 && i != 6) {
						html += "<td>" + elem + "</td>";
					} else if (i == 2) {
						html += "<td><a href=\"" + elem + "\">" + list.get(1) + "</a></td>";
					}
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
			themeList += "<option value='"+theme+"'>"+theme+"</option>";
		}
		template = template.replace("<!-- THEMES -->", themeList);
		
		writeToFile(template, "./data/dataHTML_" + outputLang + ".html");
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
				Element english = null;
				Element french = null;
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
				if (data.contains("COVID") || data.contains("CORONAVIRUS") || data.contains("true")) {
					contentType += checkField + ": " + record.get(checkField) + "\n\r";
				}
			} catch (Exception e) {

			}
		}
		return contentType;
	}

	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
	}

	public String contentType(String contentTypeContent) {
		String substantiveContentTypes[] = { "Title", "H2", "dcterms.subject", "desc", "Description", "Name",
				"Page title", "H1", "Keywords", "Primary topic", "Additional topics" };
		String contentType;
		if (stringContainsItemFromList(contentTypeContent, substantiveContentTypes)) {
			contentType = "Substantive";
		} else if (contentTypeContent.contains("Has Alert")) {
			contentType = "Contains Covid Alert";
		} else {
			contentType = "Links to Covid pages";
		}
		return contentType;
	}

	public String determineOrganization(String url, String lang) {
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

	public void loadData() throws Exception {
		System.out.println("");
		Reader in2 = new FileReader("./data/covid19-2020-03-31_en.csv");
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in2);
		for (CSVRecord record : records2) {
			// System.out.println(record.get("URL"));
			OutputData outputData = new OutputData();
			outputData.title = record.get("Title");
			outputData.URL = record.get("URL");
			outputData.language = record.get("Language");
			outputData.organization = this.determineOrganization(outputData.URL, outputData.language);
			outputData.contentTypeContent = this.contentTypeContent(record);
			outputData.contentTypes = this.contentType(outputData.contentTypeContent);
			outputData.modifiedDate = record.get("Last Modified");

			this.covidMap.put(record.get("URL"), outputData);
		}
		System.out.println("");
		Reader in3 = new FileReader("./data/covid19-2020-03-31_fr.csv");
		Iterable<CSVRecord> records3 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in3);
		for (CSVRecord record : records3) {
			// System.out.println(record.get("URL"));
			OutputData outputData = new OutputData();
			outputData.title = record.get("Title");
			outputData.URL = record.get("URL");
			outputData.language = record.get("Language");
			outputData.organization = this.determineOrganization(outputData.URL, outputData.language);
			outputData.contentTypeContent = this.contentTypeContent(record);
			outputData.contentTypes = this.contentType(outputData.contentTypeContent);
			outputData.modifiedDate = record.get("Last Modified");
			outputData.language = record.get("Language");
			this.covidMap.put(record.get("URL"), outputData);
		}

		Reader in = new FileReader("./data/gcPageReport-publish-03-31-2020.csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			OutputData outputData = new OutputData();
			outputData.contentTypeContent = this.contentTypeContent(record);
			if (!outputData.contentTypeContent.equals("")) {
				outputData.title = record.get("Page title");
				outputData.URL = record.get("Public path");
				outputData.language = this.determineLanguage(outputData.URL, record.get("gcLanguage"));
				outputData.organization = this.determineOrganization(outputData.URL, outputData.language);
				outputData.contentTypes = this.contentType(outputData.contentTypeContent);
				outputData.modifiedDate = record.get("Last Modified date");
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
