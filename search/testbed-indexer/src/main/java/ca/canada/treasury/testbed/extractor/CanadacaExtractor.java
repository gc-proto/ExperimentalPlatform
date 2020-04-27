package ca.canada.treasury.testbed.extractor;

import com.norconex.collector.http.url.impl.GenericLinkExtractor;

public class CanadacaExtractor extends GenericLinkExtractor {

	public static final String[] INCLUDE_LIST = { "covid", "coronavirus", "cerb", "pcu",
			"Canada+Emergency+Response+Benefit", "Prestation+canadienne+d'urgence","sars" };

	public boolean accepts(String url, com.norconex.commons.lang.file.ContentType contentType) {
		if (url.toUpperCase().contains("CANADA.CA")) {
			for (String item : INCLUDE_LIST) {
				if (url.toUpperCase().contains(item.toUpperCase())) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}

	}
}
