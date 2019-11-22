package ca.tbssct.ep;

import java.io.BufferedReader;

import java.io.File;


import java.io.IOException;

import java.io.InputStreamReader;

public class EvironmentCreator {

	public static final String HELM_SCRIPTS = "/home/hyma/helm-drupal/drupal/";

	public static void main(String[] args) throws Exception {
		new EvironmentCreator().create(args[0], args[1], args[2], args[3]);
	}
	
	
	public void create(String mode, String instanceName, String password, String siteEmail ) throws Exception {
		if (mode.equals("full")) {
			String output = this.ExecuteCommand(HELM_SCRIPTS,
					"cp " + HELM_SCRIPTS + "values-template.yaml " + HELM_SCRIPTS + "values-" + instanceName + ".yaml");
			System.out.println(output);
			this.updateValuesFile(HELM_SCRIPTS + "values-" + instanceName + ".yaml", password, siteEmail);
			this.ExecuteCommand(HELM_SCRIPTS, "kubectl create namespace " + instanceName);
			this.ExecuteCommand(HELM_SCRIPTS, "kubectl config set-context --current --namespace=" + instanceName);
			System.out.println(this.ExecuteCommand(HELM_SCRIPTS, "helm install --name " + instanceName + " -f values-"
					+ instanceName + ".yaml --timeout 1200 --wait ."));
			// wait for the public IP to be assigned
		}
		this.ExecuteCommand(HELM_SCRIPTS, "kubectl config set-context --current --namespace=" + instanceName);
		if (mode.equals("full") || mode.equals("assignIP")) {
			boolean keepGoing = true;
			int count = 0;
			String publicIP = "null";
			while (keepGoing) {
				publicIP = this.ExecuteCommand(HELM_SCRIPTS, "kubectl get svc " + instanceName
						+ "-nginx -o jsonpath=\"{.status.loadBalancer.ingress[*].ip}\"").trim();
				if (!publicIP.equals("null") && !publicIP.contains("Error")) {
					keepGoing = false;
					System.out.println(publicIP);
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
				System.out.println(this.ExecuteCommand(HELM_SCRIPTS,
						"az network dns record-set a add-record -g DNSZone -z ryanhyma.com -n " + instanceName + " -a "
								+ publicIP + " --ttl 600"));
			}
		}
	}

	public void updateValuesFile(String path, String password, String siteEmail) throws Exception {
		String content = Util.fileToString(path);
		content = content.replace("##password##", password);
		content = content.replace("##siteEmail##", siteEmail);
		Util.writeFile(path, content);
	}

	

	

	public String ExecuteCommand(String workingDirectory, String command) {
		System.out.println("Working Directory: " + workingDirectory);
		System.out.println(command);
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
