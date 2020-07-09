import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CompareLinks {

	public static final String[] PROVINCE_LIST = {".ontario.ca",".org","www.princeedwardisland.ca","novascotia.ca",".com","www.investquebec.com","www.quebec.ca","manitoba.ca","yukon.ca", "www.oce-ontario.org", "www.novascotia.coop",
			"gov.nu.ca", "www.oce-ontario.org", ".bc.ca", "www.saskatchewan.ca" };

	public static void main(String args[]) throws Exception {
		List<String> links = new ArrayList<String>();
		try {
			// the file to be opened for reading
			FileInputStream fis = new FileInputStream("import/links.txt");
			Scanner sc = new Scanner(fis); // file to be scanned
			// returns true if there is another line to read
			while (sc.hasNextLine()) {
				String link = sc.nextLine();
				if (!link.contentEquals("") && link.contains("http")) {
					links.add(link.trim().toLowerCase());
				}
			}
			sc.close(); // closes the scanner
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String link : links) {
			System.out.println(link);
		}
		System.out.println("Size:" + links.size());

		// remove provincial sites
		for (String provinceURL : PROVINCE_LIST) {
			filterList(links, i -> i.contains(provinceURL));
		}
		System.out.println("After removing province links:"+links.size());

		
		
		List<String> covidLinks = downloadPageList();
		//for (String link: covidLinks) {
		//	if (link.equals("https://www.tradecommissioner.gc.ca/campaign-campagne/commerce-international-covid-19-international-trade.aspx?lang=eng")) {
		//		System.out.println("Found");
		//	}
		//}
		
		HashSet<String> normalizeLinks = new HashSet<String>();
		normalizeLinks.addAll(links);
		int count = 0;
		for (String link : normalizeLinks) {
			if (!covidLinks.contains(link)) {
				System.out.println(link);
				count++;
			}
		}
		System.out.println("Total Not found:" + count);

	}

	public static List<String> downloadPageList() throws Exception {
		Document doc = Jsoup.connect("https://covid-19inventory.tbs.alpha.canada.ca/covid19_fr.html").maxBodySize(0)
				.get();
		List<String> links = new ArrayList<String>();
		links.addAll(parseDocument(doc));
		doc = Jsoup.connect("https://covid-19inventory.tbs.alpha.canada.ca/covid19_en.html").maxBodySize(0).get();
		links.addAll(parseDocument(doc));
		System.out.println("Total inventory links: " + links.size());
		return links;
	}

	public static List<String> parseDocument(Document doc) {
		List<String> list = new ArrayList<String>();
		Element table = doc.getElementById("dataset-filter");
		Elements elements = table.select("td:eq(2) a");
		for (Element element : elements) {
			String url = element.attr("href");
			list.add(url.trim().toLowerCase());
		}
		return list;
	}

	// Generic method to remove elements from a list in java
	public static <T> void filterList(List<T> list, Predicate<T> condition) {
		for (int i = list.size() - 1; i >= 0; i--) {
			if (condition.test(list.get(i))) {
				list.remove(i);
			}
		}
	}

}
