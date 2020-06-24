import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.codec.Charsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.common.graph.ElementOrder.Type;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

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
		public String audience = "";
		public String newPage = "";
		public String comments = "";
		public String uniqueVisitors = "";

		public List<String> asList() {
			List<String> list = new ArrayList<String>();
			list.add(theme);
			list.add(department);
			list.add("<a href=\"" + URL + "\">" + title + "</a>");
			list.add(this.uniqueVisitors);
			list.add(language);
			list.add(modifiedDate);
			list.add(lastPublishedDate);
			list.add("<a href=\"" + URL + "\">" + URL + "</a>");
			list.add(h2);
			list.add(keywords);
			list.add(contentTypes);
			list.add(AEMContentType);
			list.add(audience);
			if (URL.contains("www.canada.ca")) {
				list.add("<a href=\"https://pageperformance.tbs.alpha.canada.ca?url=" + URL + "&start="
						+ Main.sevenDaysAgo + "&end=" + Main.today + "\">" + title + "</a>");
			} else {
				list.add("");
			}
			list.add("");
			return list;

		}
	}

	public String[] OUTPUT_HEADERS_EN = { "Theme", "Department", "Title", "Unique Visitors (Weekly)", "Language",
			"Modified Date", "Last Published date", "URL", "Subtitle (H2)", "Keywords", "Content Type(s)",
			"AEM Content Type", "Audience", "Page Performance", "Comments" };

	public HashMap<String, OutputData> covidMap = new HashMap<String, OutputData>();
	public HashMap<String, OutputData> aemMap = new HashMap<String, OutputData>();
	public HashMap<String, OutputData> finalMap = new HashMap<String, OutputData>();

	public Map<String, String> themeEn = new HashMap<String, String>();
	public Map<String, String> themeFr = new HashMap<String, String>();

	// public HashSet<String> usedThemesEn = new HashSet<String>();
	// public HashSet<String> usedThemesFr = new HashSet<String>();

	public Map<String, String> urlDepartmentsEn = new HashMap<String, String>();
	public Map<String, String> urlDepartmentsFr = new HashMap<String, String>();
	public Map<String, String> aemDepartmentsEn = new HashMap<String, String>();
	public Map<String, String> aemDepartmentsFr = new HashMap<String, String>();

	private static String CREDENTIALS_FILE_PATH = "secrets/credentials.json";
	private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/drive");
	// private static final String APPLICATION_NAME = "Inventory Merge";

	// public HashSet<String> UsedDepartmentsEn = new HashSet<String>();
	// public HashSet<String> UsedDepartmentsFr = new HashSet<String>();

	// public Set<String> completURLList;

	DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public String importDate = "";

	public static String today;
	// private String yesterday;
	public static String sevenDaysAgo;
	// private String thirtyDaysAgo;
	private String datePostFix = "T00:00:00.000";

	public static void main(String args[]) throws Exception {

		Main main = new Main(args[0]);
		// downloadAEMDump("05-22-2020");
		// main.dumpAEMAudience();
		main.calculateDates();
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

	public Main(String importDate) throws Exception {
		this.importDate = importDate;
		// System.out.println("URL Count:" + this.completURLList.size());
		// this.compareManualList("en");
		// this.compareManualList("fr");
	}

//	private static void downloadAEMDump(String date) throws GeneralSecurityException, IOException {
//
//		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//		Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
//				.setHttpRequestInitializer(getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME).build();
//		// Print the names and IDs for up to 10 files.
//		FileList result = service.files().list().setQ(
//				"'1g-Hw69qCUiGpG6-chf0UMYAj9RCDr6HP' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
//				.setSpaces("drive").setFields("nextPageToken, files(id, name, parents)").execute();
//		List<com.google.api.services.drive.model.File> files = result.getFiles();
//		if (files == null || files.isEmpty()) {
//			System.out.println("No files found.");
//		} else {
//			System.out.println("Files:");
//			for (com.google.api.services.drive.model.File file : files) {
//				System.out.printf("%s (%s)\n", file.getName(), file.getId());
//				if (file.getName().contains(date) && file.getName().contains("publish")) {
//					try (FileOutputStream outputStream = new FileOutputStream("import/" + file.getName())) {
//						service.files().export(file.getId(), "text/csv").executeMediaAndDownloadTo(outputStream);
//
//					}
//
//				}
//			}
//		}
//
//	}

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = new FileInputStream(new File(CREDENTIALS_FILE_PATH));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	public void calculateDates() {
		today = this.calculateDays(-1);
		// this.yesterday = this.calculateDays(-1);
		sevenDaysAgo = this.calculateDays(-7);
		// this.thirtyDaysAgo = this.calculateDays(-30);
	}

	public String calculateMonth(int month) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, month);
		return DATE_FORMAT.format(cal.getTime());
	}

	public String calculateDays(int days) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, days);
		return DATE_FORMAT.format(cal.getTime());
	}

	public class PagePerformance {
		String type;
		String url;
		String oUrl;
		String[] dates = new String[2];
	}

	public String determineUniqueVisits(String url) throws IOException {

		if (url.contains("www.canada.ca")) {
			String json = null;
			JsonObject obj = null;
			JsonObject summary = null;
			JsonArray array = null;
			try {
				PagePerformance pojo1 = new PagePerformance();
				pojo1.type = "metrics";
				pojo1.oUrl = url.replace("http://", "").replace("https://", "");
				pojo1.url = url.replace("http://", "").replace("https://", "");
				pojo1.dates[0] = sevenDaysAgo + this.datePostFix;
				pojo1.dates[1] = today + this.datePostFix;

				String postUrl = "https://pageperformance.tbs.alpha.canada.ca/php/process.php?mode=update";
				// String postUrl = "http://localhost:8282/php/process.php?mode=update";
				Gson gson = new Gson();
				HttpPost post = new HttpPost(postUrl);
				String jsonReq = gson.toJson(pojo1);
				StringEntity postingString = new StringEntity(jsonReq);
				post.setEntity(postingString);
				post.setHeader("Content-type", "application/json");
				int timeout = 5 * 60 * 1000;
				RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout)
						.setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).build();
				HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

				HttpResponse response = client.execute(post);

				HttpEntity entity = response.getEntity();
				Header encodingHeader = entity.getContentEncoding();

				// you need to know the encoding to parse correctly
				Charset encoding = encodingHeader == null ? StandardCharsets.UTF_8
						: Charsets.toCharset(encodingHeader.getValue());

				// use org.apache.http.util.EntityUtils to read json as string
				json = EntityUtils.toString(entity, encoding);
				obj = JsonParser.parseString(json).getAsJsonObject();
				summary = obj.get("summaryData").getAsJsonObject();
				array = summary.get("filteredTotals").getAsJsonArray();
				String returnValue = array.get(10).getAsInt() + "";
				System.out.println("URL: " + url + " Visitors:" + returnValue);
				if (returnValue == null || returnValue.contains("null")) {
					returnValue = "";
				}
				return returnValue;
			} catch (Exception e) {
				 System.out.println("Could not find in cache: " + url + " " + e.getMessage());
			}
		}
		return "";
	}

//	public String determineNewURL(String url, String lang) {
//		if (!this.completURLList.contains(url)) {
//			if (lang.contains("en")) {
//				return "Yes";
//			} else {
//				return "Oui";
//			}
//		} else {
//			if (lang.contains("en")) {
//				return "No";
//			} else {
//				return "Non";
//			}
//		}
//	}

	public String determineAudience(CSVRecord record) {
		try {
			String audience = record.get("Audience").replace("gc:audience/", ", ").replace("-", " ").trim()
					.replaceFirst(",", "");
			audience = this.capitalizeWord(audience);
			return audience;
		} catch (Exception e) {
			// System.out.println(e.getMessage());
		}
		try {
			String audience = this.capitalizeWord(record.get("dcterms.audience"));
			return audience;
		} catch (Exception e) {

		}
		return "";

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
		if (str.length() > 0 && !str.equals("")) {
			str = str.trim().replaceAll(" +", " ");
			// System.out.println("Word:"+str);
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

	public Set<String> loadManualList(String lang) throws Exception {
		Set<String> urls = new HashSet<String>();
		Reader in2 = new FileReader("./import/Page inventory-Table 1.csv");
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in2);
		for (CSVRecord record : records2) {
			String en = record.get(0);
			String fr = record.get(1);
			if (en != null && !en.equals("") && lang.contains("en")) {
				urls.add(en);
			}
			if (fr != null && !fr.equals("") && lang.contains("fr")) {
				urls.add(fr);
			}
		}
		return urls;
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

		// this.UsedDepartmentsEn.add("N/A");
		// this.UsedDepartmentsFr.add("N/A");

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

//	public void updateURLList() throws Exception {
//		Set<String> urls = this.completURLList;
//		urls.addAll(this.finalMap.keySet());
//		final BufferedWriter writer = Files
//				.newBufferedWriter(Paths.get("./data/completeURLList-" + this.importDate + ".csv"));
//		final CSVFormat csvFormat = CSVFormat.EXCEL.withHeader("URL");
//		try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);) {
//			for (String url : urls) {
//				csvPrinter.printRecord(url);
//			}
//		} catch (Exception e) {
//
//		}
//		System.out.println("URL Count:" + urls.size());
//	}

//	public void compareManualList(String lang) throws Exception {
//		Set<String> manualList = this.loadManualList(lang);
//		int count = 0;
//		for (String manualURL : manualList) {
//			boolean found = false;
//			for (String url : this.completURLList) {
//				String tmpURL = url.trim().toUpperCase();
//				String tmpManualURL = manualURL.trim().toUpperCase();
//				if (tmpURL.contains(tmpManualURL) || tmpManualURL.contains(tmpURL)) {
//					found = true;
//					break;
//				}
//			}
//			if (!found) {
//				count++;
//				System.out.println("<url>" + manualURL.replace("&", "&amp;").trim() + "</url>");
//			}
//		}
//		System.out.println("Missed Count:" + count);
//	}

//	public Set<String> readURLList() throws Exception {
//		Set<String> urls = new HashSet<String>();
//		Reader in = new FileReader("./data/completeURLList-" + this.lastImportDate + ".csv");
//		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
//		for (CSVRecord record : records) {
//			// System.out.println(record.get("URL"));
//			urls.add(record.get(0));
//		}
//		return urls;
//	}

	public void outputDataHTML(String outputLang) throws Exception {
		String template = readLineByLineJava8("./data/template_" + outputLang + ".html");

		// Insert themes
		String themeList = "";

		List<String> themes = new ArrayList<String>(this.themeEn.values());
		if (outputLang.contains("fr")) {
			themes = new ArrayList<String>(this.themeFr.values());
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

		for (String theme : new LinkedHashSet<String>(themes)) {
			themeList += "<option value='" + theme + "'>" + theme + "</option>";
		}
		template = template.replace("<!-- THEMES -->", themeList);

		// Insert departments
		String deptList = "";
		List<String> depts = new ArrayList<String>(this.aemDepartmentsEn.values());
		depts.addAll(this.urlDepartmentsEn.values());
		if (outputLang.contains("fr")) {
			depts = new ArrayList<String>(this.aemDepartmentsFr.values());
			depts.addAll(this.urlDepartmentsFr.values());
		}

		Collections.sort(depts, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				o1 = Normalizer.normalize(o1, Normalizer.Form.NFD);
				o2 = Normalizer.normalize(o2, Normalizer.Form.NFD);
				return o1.compareTo(o2);
			}
		});

		for (String dept : new LinkedHashSet<String>(depts)) {
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
				// if (i != (headers.length - 1)) {
				togglecolumns += "<label for='toggle" + i + "'>"
						+ "<input type='checkbox' CHECKED class='toggle-vis' name='toggle" + i + "' id='toggle" + i
						+ "' data-column='" + i + "' />&nbsp;<span>" + headers[i] + "</span></label>&nbsp;";
				// } else {
				// togglecolumns += "<label for='toggle" + i + "'>"
				// + "<input type='checkbox' class='toggle-vis' name='toggle" + i + "'
				// id='toggle" + i
				// + "' data-column='" + i + "' />&nbsp;<span>" + headers[i] +
				// "</span></label>&nbsp;";
				// }
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
					} else {
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

	public String contentTypeContent(Map<String, String> record, String url) {
		boolean covid = false;
		String checkFields[] = { "title_s", "dcterms_description_s", "id", "body_t_h1" };
		String contentType = "";

		for (String checkField : checkFields) {
			try {
				String data = record.get(checkField).toUpperCase().trim();
				if (data.contains("CANADA EMERGENCY RESPONSE BENEFIT")
						|| data.contains("PRESTATION CANADIENNE D’URGENCE") || data.contains("CERB")
						|| data.contains("COVID") || data.contains("CORONAVIRUS")
						|| (checkField.equals("Has Alert") && data.contains("TRUE"))) {
					covid = true;
					contentType += checkField + ": " + record.get(checkField) + "\n\r";
				} else if ((checkField.equals("id")) && (data.contains("NEWS") || data.contains("NOUVELLES"))) {
					contentType += "Content type: News" + "\n\r";
					// System.out.println("Checkfield:"+checkField);
				}

			} catch (Exception e) {

			}
		}
		if (covid) {
			return contentType;
		} else {
			return "";
		}
	}

	public String contentTypeContent(CSVRecord record, String url) {
		boolean covid = false;
		String checkFields[] = { "Title", "Has Alert", "H2", "dcterms.subject", "desc", "Description", "Name",
				"Page title", "H1", "Keywords", "Primary topic", "Additional topics", "desc", "dcterms.subject",
				"Content type", "URL", "Public path" };
		String contentType = "";

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
				} else if ((checkField.equals("Public path") || checkField.equals("URL"))
						&& (data.contains("NEWS") || data.contains("NOUVELLES"))) {
					contentType += "Content type: News" + "\n\r";
					// System.out.println("Checkfield:"+checkField);
				}

			} catch (Exception e) {

			}
		}
		if (covid) {
			return contentType;
		} else {
			return "";
		}
	}

	public Date getLastModifiedDate(CSVRecord record, String url, boolean canadaca) {
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
		if ((!canadaca && !url.contains("www.canada.ca")) || canadaca) {
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
				String dateModified = doc.select("time[property=dateModified]").get(0).text().trim();
				System.out.println(url + " - " + dateModified);
				return DATE_FORMAT.parse(dateModified);

			} catch (Exception e) {
			}
		}

		return null;
	}

	public Date getLastModifiedDate(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
			String dateModified = doc.select("time[property=dateModified]").get(0).text().trim();
			System.out.println(url + " - " + dateModified);
			return DATE_FORMAT.parse(dateModified);

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
		String substantiveContentTypes[] = { "Title", "dcterms.subject", "desc", "Description", "Name", "Page title",
				"H1", "Keywords", "Primary topic", "Additional topics" };
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
		// HashSet<String> usedTheme = this.usedThemesEn;
		if (lang.toLowerCase().contains("fr")) {
			themes = this.themeFr;
			// usedTheme = this.usedThemesFr;
		}
		for (String key : themes.keySet()) {
			if (url.contains(key)) {
				// usedTheme.add(themes.get(key));
				return themes.get(key);
			}
		}
		return "N/A";
	}

	public String determineDept(String url, String lang, CSVRecord record, Map<String, String> map) {
		Map<String, String> depts = this.urlDepartmentsEn;
		// HashSet<String> usedDepts = this.UsedDepartmentsEn;
		if (lang.toLowerCase().contains("fr")) {
			depts = this.urlDepartmentsFr;
			// usedDepts = this.UsedDepartmentsFr;
		}
		for (String key : urlDepartmentsEn.keySet()) {
			if (url.contains(key)) {
				// String dept = depts.get(key);
				// usedDepts.add(dept);
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
		if (!url.contains(".pdf") && !url.contains(".docx")) {
			if (dept == null || dept.equals("")) {
				try {
					dept = record.get("Author");
				} catch (Exception e2) {
				}
			}
			if (dept == null || dept.equals("")) {
				try {
					dept = map.get("dcterms_creator_s");
				} catch (Exception e2) {
				}
			}

			// one last hail mary
			if (dept == null || dept.equals("")) {
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
			}
		}

		if (dept != null && dept.contains(",")) {
			String deptsplit[] = dept.split(",");
			dept = deptsplit[deptsplit.length - 1].trim();
		}
		if (dept != null && (dept.contains("none") || dept.contains("English name"))) {
			dept = "";
		}

		if (dept != null && !dept.equals("") && !dept.equals("null")) {
			if (lang.contains("en")) {
				this.urlDepartmentsEn.put(url, dept);
				// this.UsedDepartmentsEn.add(dept);
			} else {
				this.urlDepartmentsFr.put(url, dept);
				// this.UsedDepartmentsFr.add(dept);
			}
			return dept;
		} else {

			return "N/A";
		}
	}

	public void loadData() throws Exception {

		Date afterDate = null;
		try {
			afterDate = DATE_FORMAT.parse("2019-01-01");
		} catch (Exception e) {
		}

		Reader in4 = new FileReader("./import/covid-inventory-" + this.importDate + ".json");
		@SuppressWarnings("rawtypes")
		java.lang.reflect.Type mapType = new TypeToken<Map<String, List>>() {
		}.getType();
		Map<String, List<Map<String, String>>> son = new Gson().fromJson(in4, mapType);
		List<Map<String, String>> list = son.get("docs");
		for (Map<String, String> record : list) {
			OutputData outputData = new OutputData();
			outputData.URL = ((String) record.get("id")).toLowerCase();
			Date modifiedDate = null;
			try {
				modifiedDate = DATE_FORMAT.parse((String) record.get("dateModified_nf"));
			} catch (Exception e) {
				modifiedDate = this.getLastModifiedDate(outputData.URL);
			}

			String contentTypeContent = this.contentTypeContent(record, outputData.URL);
			if (!contentTypeContent.equals("")) {
				// System.out.println(record.get("URL"));
				outputData.title = record.get("title_s");
				outputData.language = record.get("lang_s");
				outputData.department = this.determineDept(outputData.URL, outputData.language, null, record);
				outputData.theme = this.determineTheme(outputData.URL, outputData.language);
				outputData.h2 = "";
				outputData.keywords = record.get("dcterms_description_s");
				if (outputData.keywords == null || outputData.keywords.equals("")) {
					outputData.keywords = record.get("description_s");
					if (outputData.keywords == null) {
						outputData.keywords = "";
					}
				}
				outputData.contentTypes = this.determineContentType(contentTypeContent, "en");
				if (modifiedDate == null) {
					outputData.modifiedDate = "";
				} else {
					outputData.modifiedDate = DATE_FORMAT.format(modifiedDate);
				}
				outputData.language = record.get("lang_s");
				outputData.audience = "";
				// outputData.newPage = this.determineNewURL(outputData.URL, "en");
				outputData.uniqueVisitors = this.determineUniqueVisits(outputData.URL);
				this.covidMap.put(outputData.URL, outputData);
			}
		}
		System.out.println(list.size());

		System.out.println("");
		Reader in2 = new FileReader("./import/covid19-" + this.importDate + "_en.csv");
		Iterable<CSVRecord> records2 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in2);
		for (CSVRecord record : records2) {
			OutputData outputData = new OutputData();
			outputData.URL = record.get("URL").toLowerCase();
			Date modifiedDate = this.getLastModifiedDate(record, outputData.URL, false);
			if (modifiedDate != null && modifiedDate.after(afterDate)) {
				String contentTypeContent = this.contentTypeContent(record, outputData.URL);
				if (!contentTypeContent.equals("")) {
					// System.out.println(record.get("URL"));
					outputData.title = record.get("Title");
					outputData.language = record.get("Language");
					outputData.department = this.determineDept(outputData.URL, outputData.language, record, null);
					outputData.theme = this.determineTheme(outputData.URL, outputData.language);
					outputData.h2 = record.get("H2");
					outputData.keywords = record.get("desc");
					outputData.contentTypes = this.determineContentType(contentTypeContent, "en");
					outputData.modifiedDate = DATE_FORMAT.format(modifiedDate);
					outputData.language = record.get("Language");
					outputData.audience = this.determineAudience(record);
					// outputData.newPage = this.determineNewURL(outputData.URL, "en");
					// outputData.uniqueVisitors = this.determineUniqueVisits(outputData.URL);
					this.covidMap.put(outputData.URL, outputData);
				}
			}
		}
		System.out.println("");
		Reader in3 = new FileReader("./import/covid19-" + this.importDate + "_fr.csv");
		Iterable<CSVRecord> records3 = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in3);
		for (CSVRecord record : records3) {
			OutputData outputData = new OutputData();
			outputData.URL = record.get("URL").toLowerCase();
			Date modifiedDate = this.getLastModifiedDate(record, outputData.URL, false);
			if (modifiedDate != null && modifiedDate.after(afterDate)) {

				String contentTypeContent = this.contentTypeContent(record, outputData.URL);
				if (!contentTypeContent.equals("")) {
					// System.out.println(record.get("URL"));
					outputData.title = record.get("Title");
					outputData.language = record.get("Language");
					outputData.department = this.determineDept(outputData.URL, outputData.language, record, null);
					outputData.theme = this.determineTheme(outputData.URL, outputData.language);
					outputData.h2 = record.get("H2");
					outputData.keywords = record.get("desc");
					outputData.contentTypes = this.determineContentType(contentTypeContent, "fr");
					outputData.modifiedDate = DATE_FORMAT.format(modifiedDate);
					outputData.language = record.get("Language");
					outputData.audience = this.determineAudience(record);
					// outputData.uniqueVisitors = this.determineUniqueVisits(outputData.URL);
					// outputData.newPage = this.determineNewURL(outputData.URL, "fr");
					this.covidMap.put(outputData.URL, outputData);
				}
			}
		}

		Reader in = new FileReader("./import/gcPageReport-publish-" + this.importDate + ".csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			OutputData outputData = new OutputData();
			outputData.URL = record.get("Public path").toLowerCase();
			Date modifiedDate = this.getLastModifiedDate(record, outputData.URL, false);
			if (modifiedDate != null && modifiedDate.after(afterDate)) {
				String contentTypeContent = this.contentTypeContent(record, outputData.URL);
				if (!contentTypeContent.equals("")) {
					outputData.title = record.get("Page title");
					outputData.language = this.determineLanguage(outputData.URL, record.get("gcLanguage"));
					outputData.department = this.determineDept(outputData.URL, outputData.language, record, null);
					outputData.theme = this.determineTheme(outputData.URL, outputData.language);
					outputData.contentTypes = this.determineContentType(contentTypeContent, outputData.language);
					outputData.modifiedDate = DATE_FORMAT.format(modifiedDate);
					outputData.h2 = "";
					outputData.keywords = record.get("Keywords");
					String contentType = record.get("Content type").replace("gc:content-types/", ", ").replace("-", " ")
							.trim().replaceFirst(",", "");
					outputData.AEMContentType = this.capitalizeWord(contentType);
					outputData.lastPublishedDate = record.get("Last Published date");
					outputData.audience = this.determineAudience(record);
					// outputData.newPage = this.determineNewURL(outputData.URL,
					// outputData.language);
					outputData.uniqueVisitors = this.determineUniqueVisits(outputData.URL);
					this.aemMap.put(outputData.URL, outputData);
				}
			}
		}

	}

	public void dumpAEMContentType() throws Exception {
		Reader in = new FileReader("./import/gcPageReport-publish-" + this.importDate + ".csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			OutputData outputData = new OutputData();
			System.out.println(record.get("Content type"));
			String contentType = record.get("Content type").replace("gc:content-types/", ", ").replace("-", " ").trim()
					.replaceFirst(",", "");
			outputData.AEMContentType = this.capitalizeWord(contentType);

			System.out.println(outputData.AEMContentType);

		}
	}

	public void dumpAEMAudience() throws Exception {
		Reader in = new FileReader("./import/gcPageReport-publish-" + this.importDate + ".csv");
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
		for (CSVRecord record : records) {
			OutputData outputData = new OutputData();
			System.out.println(record.get("Audience"));
			String contentType = record.get("Audience").replace("gc:audience/", ", ").replace("-", " ").trim()
					.replaceFirst(",", "");
			outputData.AEMContentType = this.capitalizeWord(contentType);

			System.out.println(outputData.AEMContentType);

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
			if (covidURL.contains("www.canada.ca")) {
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
