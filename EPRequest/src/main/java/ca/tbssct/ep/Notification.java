package ca.tbssct.ep;

import uk.gov.service.notify.NotificationClient;

public class Notification {

	public static String KEY_PATH = "/home/secrets/notification.key";

	public static NotificationClient getNotificationClient() {
		NotificationClient client = new NotificationClient(getAPIKey(), "https://api.notification.alpha.canada.ca"); 
		return client;
	}

	private static String getAPIKey() {
		try {
			return Util.fileToString(KEY_PATH).trim();
		} catch (Exception e) {

		}
		return "";
	}
}
