package ca.gc.tbs.service;

import org.springframework.stereotype.Service;

@Service
public class ContentService {
	public ContentService() {
		BadWords.loadConfigs();
	}
	
	public String cleanContent(String content) {
		content = this.cleanPostalCode(content);
		content = this.cleanPhoneNumber(content);
		content = this.cleanEmailAddress(content);
		content = this.cleanSIN(content);
		content = BadWords.censor(content);
		return content;
	}

	private String cleanPostalCode(String content) {
		return content.replaceAll("[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d", "### ###");
	}

	private String cleanSIN(String content) {
		return content.replaceAll("\\d{3}\\s?\\d{3}\\s?\\d{3}", "### ### ###");
	}

	private String cleanPhoneNumber(String content) {
		return content.replaceAll("\\D*([2-9]\\d{2})(\\D*)([2-9]\\d{2})(\\D*)(\\d{4})\\D*", "# ### ### ###");
	}

	private String cleanEmailAddress(String content) {
		return content.replaceAll("([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})", "####@####.####");
	}

}
