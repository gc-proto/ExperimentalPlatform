package ca.tbssct;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.UserAgent;

public class Main {

	private HashSet<String> links = new HashSet<String>();
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private String today;
	private String sevenDaysAgo;
	private String thirtyDaysAgo;
	public static long WAIT_TIME = 10000;
	public static long FIVE_MINUTES = 1000 * 60 * 5;
	public static final String PAGE_PERFORMANCE_URL = "https://pageperformance.alpha.canada.ca";
	public static int NUM_THREADS = 6;
	private ExecutorService executor = null;
	public static AtomicInteger numCached = new AtomicInteger(0);

	public static void main(String[] args) throws Exception {

		Main main = new Main();
		main.setupDriver();
		main.calculateDates();
		main.downloadPageList();
		main.cachePages();
		main.cachePages();
	}

	public Main() {
		this.executor = Executors.newFixedThreadPool(NUM_THREADS);
	}

	public void setupDriver() {

	}

	public void cachePages() {
		this.cachePages(thirtyDaysAgo, today);
		this.cachePages(sevenDaysAgo, today);
		this.cachePages(today, today);
	}

	public void cachePages(String from, String to) {
		for (String url : links) {
			if (url.contains("www.canada.ca")) {
				Runnable cacher = new PageCacher(url, from, to);
				this.executor.execute(cacher);
			}
		}
		this.executor.shutdown();
		while (!this.executor.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		}
	}

	public static class PageCacher implements Runnable {

		String from;
		String to;
		String pageURL;

		public PageCacher(String pageURL, String from, String to) {
			this.from = from;
			this.to = to;
			this.pageURL = pageURL;
		}

		public void run() {
			WebDriver driver = new JBrowserDriver(Settings.builder().timezone(Timezone.AMERICA_NEWYORK)
					.userAgent(UserAgent.CHROME).javascript(true).build());
			try {
				URL url = new URL(PAGE_PERFORMANCE_URL + "?url=" + URLEncoder.encode(pageURL, "UTF-8") + "&start="
						+ from + "&end=" + to);

				driver.get(url.toString());
				boolean keepGoing = true;
				long startTime = System.currentTimeMillis();
				long maxTime = 10 * 60 * 1000;
				while (keepGoing) {
					WebElement loading = driver.findElement(By.cssSelector("#loading"));
					if (!loading.isDisplayed()) {
						keepGoing = false;
						System.out.println("Num:" + numCached.incrementAndGet() + " Cached page: Code:"
								+ driver.getTitle() + " URL:" + url.toString());

					} else {
						System.out.println(loading.getText() + " Caching page: " + url.toString() + " From: " + from
								+ " To: " + to);
						Thread.sleep(10000);
						long difference = System.currentTimeMillis() - startTime;
						if (difference >= maxTime) {
							System.out.println("Gave Up:" + pageURL + " : " + difference / 1000 / 60);
							keepGoing = false;
						}
					}
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				driver.quit();
			}

		}

	}

	public void calculateDates() {
		this.today = format.format(new Date());
		this.sevenDaysAgo = this.calculateDate(-7);
		this.thirtyDaysAgo = this.calculateDate(-30);
	}

	public String calculateDate(int days) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, days);
		return format.format(cal.getTime());
	}

	public void downloadPageList() throws Exception {
		Document doc = Jsoup.connect("https://covid-19inventory.tbs.alpha.canada.ca/covid19_fr.html").get();
		this.links = new HashSet<String>();
		links.addAll(this.parseDocument(doc));
		doc = Jsoup.connect("https://covid-19inventory.tbs.alpha.canada.ca/covid19_en.html").get();
		links.addAll(this.parseDocument(doc));
	}

	public List<String> parseDocument(Document doc) {
		List<String> list = new ArrayList<String>();
		Element table = doc.getElementById("dataset-filter");
		Elements elements = table.select("td:eq(2) a");
		for (Element element : elements) {
			String url = element.attr("href");
			list.add(url);
		}
		return list;
	}

}
