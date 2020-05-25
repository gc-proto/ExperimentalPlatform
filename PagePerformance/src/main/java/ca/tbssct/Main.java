package ca.tbssct;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

	private HashSet<String> links = new HashSet<String>();
	public static long WAIT_TIME = 10000;
	public static long FIVE_MINUTES = 1000 * 60 * 5;
	// public static final String PAGE_PERFORMANCE_URL =
	// "http://pageperformance-nginx/php/process-cj.php";
	public static final String PAGE_PERFORMANCE_URL = "https://pageperformance.alpha.canada.ca/php/process-cj.php";
	public static int NUM_THREADS = 3;
	private ExecutorService executor = null;
	public static AtomicInteger numCached = new AtomicInteger(0);

	public static void main(String[] args) throws Exception {

		Main main = new Main();
		main.downloadPageList();
		main.cachePages();
		main.cachePages();
	}

	public Main() {
		this.executor = new ThreadPoolExecutor(NUM_THREADS, NUM_THREADS, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	public void cachePages() {
		for (String url : links) {
			if (url.contains("www.canada.ca")) {
				Runnable cacher = new PageCacher(url);
				try {
					this.executor.execute(cacher);
				} catch (Exception e) {

				}
			}
		}
		this.executor.shutdown();
		while (!this.executor.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		}
		System.out.println("All pages cached.");
	}

	public static class PageCacher implements Runnable {

		String pageURL;

		public PageCacher(String pageURL) {
			this.pageURL = pageURL;
		}

		public void run() {
			try {
				URL url = new URL(PAGE_PERFORMANCE_URL + "?url=" + URLEncoder.encode(pageURL, "UTF-8"));
				System.out.println("Caching: " + pageURL);
				System.out.println("Cache call:" + url.toString());
				Document doc = Jsoup.connect(url.toString()).timeout(5 * 60 * 1000).get();
				System.out
						.println("Finished caching: " + pageURL + " Output:" + doc.outerHtml().replaceAll("\\s+", ""));
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void downloadPageList() throws Exception {
		Document doc = Jsoup.connect("https://covid-19inventory.tbs.alpha.canada.ca/covid19_fr.html").maxBodySize(0)
				.get();
		this.links = new HashSet<String>();
		links.addAll(this.parseDocument(doc));
		doc = Jsoup.connect("https://covid-19inventory.tbs.alpha.canada.ca/covid19_en.html").maxBodySize(0).get();
		links.addAll(this.parseDocument(doc));
		for (String link : links) {
			System.out.println(link);
		}
		System.out.println("End of links: " + links.size());
	}

	public List<String> parseDocument(Document doc) {
		List<String> list = new ArrayList<String>();
		Element table = doc.getElementById("dataset-filter");
		Elements elements = table.select("td:eq(2) a");
		System.out.println("Elements: " + elements.size());
		for (Element element : elements) {
			String url = element.attr("href");
			list.add(url);
		}
		return list;
	}

}
