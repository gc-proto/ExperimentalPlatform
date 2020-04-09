package ca.tbssct.ep;

import uk.gov.service.notify.NotificationClient;

public class Notification {
	
	public static String INFORMATION_TEMPLATE_ID = "c01d8299-bcbf-4373-9ebf-783aaa58187f";
	public static String CONFIRMATION_TEMPLATE_ID = "d5604c35-5a3c-4b3d-b084-6fc5c2abad2f";
	public static String COMMENT_EMAIL_TEMPLATE_ID = "05640e52-0374-4e58-8d78-113130c47e16";

	public static NotificationClient getNotificationClient() {
		NotificationClient client = new NotificationClient(getAPIKey(), "https://api.notification.alpha.canada.ca");
		return client;
	}

	private static String getAPIKey() {
		try {
			return Util.fileToString(Util.getAPIKeyPath() + "notification.key").trim();
		} catch (Exception e) {

		}
		return "";
	}
}
