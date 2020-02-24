import java.net.URL;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

	public static String[] URLS = {
			"https://wayfinding.tbs.alpha.canada.ca/en/landing/ryan-long-document-do-not-delete" };

	public static void main(String args[]) throws Exception {
		String urlString = "http://localhost:8983/solr/opt";
		SolrClient Solr = new HttpSolrClient.Builder(urlString).build();

		for (String url : URLS) {
			Document doc = Jsoup.parse(new URL(url), 30000);
			Elements elements = doc.select("[id^=longdocument]");
			for (Element element : elements) {
				Elements childElements = element.select("[id^=para]");
				for (Element childElem : childElements) {
					String id = childElem.attr("id");
					String ids[] = id.split("-");
					if (ids.length >= 3) {
						SolrInputDocument toIndexDoc = new SolrInputDocument();
						toIndexDoc.addField("longdocumentid", element.attr("id"));
						toIndexDoc.addField("paraid", Integer.parseInt(ids[2]));
						toIndexDoc.addField("sectionid", Integer.parseInt(ids[1]));
						toIndexDoc.addField("content", childElem.text());
						toIndexDoc.addField("url", url + "#" + childElem.attr("id"));
						toIndexDoc.addField("id", element.attr("id") + "-" + childElem.attr("id"));
						Solr.add(toIndexDoc);
					}
				}
			}
		}
		// Saving the changes
		Solr.commit();
	}
}
