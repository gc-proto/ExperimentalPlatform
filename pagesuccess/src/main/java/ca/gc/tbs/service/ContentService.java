package ca.gc.tbs.service;

import org.springframework.stereotype.Service;

@Service
public class ContentService {
	
	class CleanResult {
		boolean emailCleaned = false;
		boolean sinCleaned = false;
		boolean phoneCleaned = false;
		boolean postalCleaned = false;
		String content = "";
	}
	
	public ContentService() {
		BadWords.loadConfigs();
	}
	
	public String cleanContent(String content) {
		CleanResult result = new CleanResult();
		result.content = content;
		String newContent = this.cleanPostalCode(result.content);
		if (!newContent.contentEquals(content)) {
			result.postalCleaned = true;
			result.content = newContent;
		}
		newContent = this.cleanPhoneNumber(content);
		if (!newContent.contentEquals(content)) {
			result.postalCleaned = true;
			result.content = newContent;
		}
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
