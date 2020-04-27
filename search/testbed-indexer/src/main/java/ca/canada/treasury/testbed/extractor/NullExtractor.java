package ca.canada.treasury.testbed.extractor;

import com.norconex.collector.http.url.impl.GenericLinkExtractor;

public class NullExtractor extends GenericLinkExtractor {
	public boolean accepts(String url, com.norconex.commons.lang.file.ContentType contentType) {
		return false;

	}
}
