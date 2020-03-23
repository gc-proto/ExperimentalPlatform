package ca.tbssct.ep;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.tbssct.ep.web.EPRequest;

public class EnvironmentCreator {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final String HELM_SCRIPTS = Util.getHelmScriptPath();
	public static final String AZURE_SCRIPTS = Util.getAzureScriptPath();
	public static final String VELERO_SCRIPTS = Util.getVeleroScriptPath();

	public EnvironmentCreator() {

	}

	public boolean assignDNS(String instanceName) {
		String publicIP = Util.GetPublicIp();
		if (!publicIP.equals("null")) {
			// now use the command line to add a DNS entry using the azure command line.
			logger.info(Util.ExecuteCommand(HELM_SCRIPTS,
					"az network dns record-set a add-record -g DNSZone -z " + Util.GetHost() + " -n " + instanceName
							+ " -a " + publicIP));
			// check that the DNS record is available.
			boolean keepGoing = true;
			int count = 0;
			while (keepGoing) {
				String response = Util.ExecuteCommand(HELM_SCRIPTS,
						"nslookup " + instanceName + Util.GetHost());
				logger.info(response);
				if (response.contains(publicIP)) {
					logger.info("DNS entry found. Confirmation will be sent");
					keepGoing = false;
				} else {
					try {
						Thread.sleep(60000);
					} catch (Exception e) {

					}
					if (count >= 240) {
						Util.handleError("Failed to assign DNS entry failing the creation...", instanceName, logger);
						return false;
					} else {
						count++;
					}
				}
			}
			return true;
		}
		Util.handleError("Failed to assign DNS entry failing the creation...", instanceName, logger);
		return false;

	}

	public boolean deployDrupal(String instanceName, EPRequest epRequest) {
		try {
			String output = Util.ExecuteCommand(HELM_SCRIPTS, "cp " + HELM_SCRIPTS
					+ Util.GetValuesTemplate() + " " + HELM_SCRIPTS + "values-" + instanceName + ".yaml");
			if (output.toUpperCase().contains("ERROR")) {
				Util.handleError(output, instanceName, logger);
				return false;
			}
			logger.info(output);
			EnvironmentCreator.this.updateValuesFile(HELM_SCRIPTS + "values-" + instanceName + ".yaml",
					epRequest.getPassword(), epRequest.getEmailAddress(), instanceName);
			String helmMsg = Util.ExecuteCommand(HELM_SCRIPTS, "helm install " + instanceName
					+ " --namespace " + instanceName + " -f values-" + instanceName + ".yaml --timeout 30m --wait .");
			logger.info(helmMsg);
			if (helmMsg.toUpperCase().contains("ERROR")) {
				logger.info("Trying again once...");
				helmMsg = Util.ExecuteCommand(HELM_SCRIPTS, "helm delete " + instanceName);
				helmMsg = Util.ExecuteCommand(HELM_SCRIPTS,
						"helm install " + instanceName + " --namespace " + instanceName + " -f values-" + instanceName
								+ ".yaml --timeout 30m --wait .");
				logger.info(helmMsg);
				if (helmMsg.toUpperCase().contains("ERROR")) {
					Util.handleError(helmMsg, instanceName, logger);
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}

		} catch (Exception e) {
			Util.handleError(e.getMessage(), instanceName, logger);
			return false;
		}
	}

	public boolean createNFSShares(String instanceName) {
		// add the secret to the share
		String output = Util.ExecuteCommand(AZURE_SCRIPTS, "./createNFSSecret.sh " + instanceName);
		if (output.toUpperCase().contains("ERROR") && !output.toUpperCase().contains("ALREADY EXISTS")) {
			Util.handleError(output, instanceName, logger);
			return false;
		} else {
			output = Util.ExecuteCommand(AZURE_SCRIPTS,
					"./createNFSShare.sh " + instanceName + "-drupal-private");
			output += Util.ExecuteCommand(AZURE_SCRIPTS,
					"./createNFSShare.sh " + instanceName + "-drupal-public");
			output += Util.ExecuteCommand(AZURE_SCRIPTS,
					"./createNFSShare.sh " + instanceName + "-drupal-themes");
			if ((output.toUpperCase().contains("\"CREATED\": FALSE") || output.toUpperCase().contains("ERROR"))
					&& !output.toUpperCase().contains("ALREADY EXISTS")) {
				Util.handleError(output, instanceName, logger);
				return false;
			} else {
				logger.info(output);
				return true;
			}
		}
	}

	public boolean createNamespace(String instanceName) {
		String output = Util.ExecuteCommand(HELM_SCRIPTS,
				"kubectl create namespace " + instanceName + "-drupal");
		if (output.toUpperCase().contains("ERROR") && !output.toUpperCase().contains("ALREADY EXISTS")) {
			Util.handleError(output, instanceName, logger);
			return false;
		} else {
			return true;
		}

	}

	public void create(EPRequest epRequest) throws Exception {
		String instanceName = epRequest.getDomainNamePrefix();
		Thread thread = new Thread() {
			public void run() {
				try {
					// assign the temporary DNS
					boolean dnsAssigned = EnvironmentCreator.this.assignDNS(instanceName);
					if (dnsAssigned) {
						boolean namespaceCreated = EnvironmentCreator.this.createNamespace(instanceName);
						if (namespaceCreated) {
							boolean createNFSShares = EnvironmentCreator.this.createNFSShares(instanceName);
							if (createNFSShares) {
								boolean drupalDeployed = EnvironmentCreator.this.deployDrupal(instanceName, epRequest);
								if (drupalDeployed) {
									boolean backupScheduled = EnvironmentCreator.this.scheduleBackup(instanceName);
									if (backupScheduled) {
										Map<String, String> personalisation = new HashMap<>();
										personalisation.put("username", "admin");
										personalisation.put("password", epRequest.getPassword());
										personalisation.put("loginURL",
												"https://" + instanceName + ".alpha.experimentation.ca/en/user/login");
										personalisation.put("contactEmail", Util.getAdminEmail());
										Notification.getNotificationClient().sendEmail(
												"a32135a9-2088-461c-8ea5-8044207497a3", epRequest.getEmailAddress(),
												personalisation, null);
										Notification.getNotificationClient().sendEmail(
												"a32135a9-2088-461c-8ea5-8044207497a3", Util.getAdminEmail(),
												personalisation, null);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					Util.handleError(e.getMessage(), instanceName, logger);
				}

			}
		};

		thread.start();

	}

	public boolean scheduleBackup(String instanceName) {
		String output = Util.ExecuteCommand(AZURE_SCRIPTS,
				"./schedule-namespace.sh " + instanceName + "-drupal");
		if (output.toUpperCase().contains("ERROR")) {
			Util.handleError(output, instanceName, logger);
			return false;
		}
		return true;
	}

	public void updateValuesFile(String path, String password, String siteEmail, String instanceName) throws Exception {
		String content = Util.fileToString(path);
		content = content.replace("##password##", password);
		content = content.replace("##siteEmail##", siteEmail);
		content = content.replace("##publicShare##", instanceName + "-drupal-public");
		content = content.replace("##privateShare##", instanceName + "-drupal-private");
		content = content.replace("##themesShare##", instanceName + "-drupal-themes");
		content = content.replace("##host##", instanceName + "." + Util.GetHost());
		content = content.replace("##hostSecret##",
				((instanceName + "." + Util.GetHost()).toLowerCase().replace(".", "-") + "-tls-secret"));
		Util.writeFile(path, content);
	}

	

}
