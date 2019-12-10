package ca.tbssct.ep;

import java.io.BufferedReader;

import java.io.File;

import java.io.IOException;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.tbssct.ep.web.EPRequest;

public class EvironmentCreator {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	public static final String HELM_SCRIPTS = "/home/helm-drupal/drupal/";

	public EvironmentCreator() {

	}

	public void create(String mode, EPRequest epRequest) throws Exception {
		Thread thread = new Thread() {
			public void run() {
				try {
					String instanceName = epRequest.getDomainNamePrefix();
					if (mode.equals("full")) {
						String output = EvironmentCreator.this.ExecuteCommand(HELM_SCRIPTS, "cp " + HELM_SCRIPTS
								+ "values-template.yaml " + HELM_SCRIPTS + "values-" + instanceName + ".yaml");
						logger.info(output);
						EvironmentCreator.this.updateValuesFile(HELM_SCRIPTS + "values-" + instanceName + ".yaml",
								epRequest.getPassword(), epRequest.getEmailAddress());
						EvironmentCreator.this.ExecuteCommand(HELM_SCRIPTS, "kubectl create namespace " + instanceName);
						EvironmentCreator.this.ExecuteCommand(HELM_SCRIPTS,
								"kubectl config set-context --current --namespace=" + instanceName);
						logger.info(EvironmentCreator.this.ExecuteCommand(HELM_SCRIPTS, "helm install --name "
								+ instanceName + " -f values-" + instanceName + ".yaml --timeout 1200 --wait ."));
						// wait for the public IP to be assigned
					}
					EvironmentCreator.this.ExecuteCommand(HELM_SCRIPTS,
							"kubectl config set-context --current --namespace=" + instanceName);
					if (mode.equals("full") || mode.equals("assignIP")) {
						boolean keepGoing = true;
						int count = 0;
						String publicIP = "null";
						while (keepGoing) {
							publicIP = EvironmentCreator.this
									.ExecuteCommand(HELM_SCRIPTS,
											"kubectl get svc " + instanceName
													+ "-nginx -o jsonpath=\"{.status.loadBalancer.ingress[*].ip}\"")
									.trim();
							if (!publicIP.equals("null") && !publicIP.contains("Error")) {
								keepGoing = false;
								logger.info(publicIP);
							} else {
								Thread.sleep(30000);
								if (count >= 20) {
									keepGoing = false;
								} else {
									count++;
								}
							}

						}
						if (!publicIP.equals("null")) {
							// now use the command line to add a DNS entry using the azure command line.
							logger.info(EvironmentCreator.this.ExecuteCommand(HELM_SCRIPTS,
									"az network dns record-set a add-record -g DNSZone -z ryanhyma.com -n "
											+ instanceName + " -a " + publicIP));
							Map<String, String> personalisation = new HashMap<>();
							personalisation.put("username", "admin");
							personalisation.put("password",epRequest.getPassword());
							personalisation.put("loginURL", "http://" + instanceName + ".ryanhyma.com/en/user/login");
							Notification.getNotificationClient().sendEmail("a32135a9-2088-461c-8ea5-8044207497a3",
									epRequest.getEmailAddress(), personalisation,null);
						}
					}
				} catch (Exception e) {

				}
			}

		};

		thread.start();

	}

	public void updateValuesFile(String path, String password, String siteEmail) throws Exception {
		String content = Util.fileToString(path);
		content = content.replace("##password##", password);
		content = content.replace("##siteEmail##", siteEmail);
		Util.writeFile(path, content);
	}

	public String ExecuteCommand(String workingDirectory, String command) {
		logger.info("Working Directory: " + workingDirectory);
		logger.info(command);
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory(new File(workingDirectory));
		processBuilder.command("bash", "-c", command);
		StringBuilder output = new StringBuilder();
		try {
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				return output.toString();
			} else {
				try (final BufferedReader b = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
					String line2;
					if ((line2 = b.readLine()) != null)
						return output.append(line2 + "\n").toString();
				} catch (final IOException e) {
					return e.getMessage();
				}
			}

		} catch (IOException e) {
			return e.getMessage();
		} catch (InterruptedException e) {
			return e.getMessage();
		}
		return "";
	}

}
