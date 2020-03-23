package ca.tbssct.ep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

public class Util {
	
	public static String propPath = "/home/requests/";
	static Logger logger = LoggerFactory.getLogger(Util.class);

	public static String fileToString(String path) throws Exception {
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public static void setPropPath(String path) {
		propPath = path;
	}

	

	public static void writeFile(String path, String content) throws Exception {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"))) {
			writer.write(content);
		}
	}

	public static String GetVerificationURL() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("server");
	}

	public static String GetPublicIp() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("publicIP");
	}

	public static String GetValuesTemplate() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("helmValuesTemplate");
	}

	public static String GetHost() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("host");
	}

	public static String getAdminEmail() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("adminEmail");

	}

	public static boolean isDemoMode() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("demoMode").equals("true");

	}

	public static String getAPIKeyPath() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("secretPath");
	}

	public static String getRequestPath() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("requestPath");
	}

	public static String getAzureScriptPath() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("azureScriptPath");
	}

	public static String getVeleroScriptPath() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("veleroScriptPath");
	}

	public static String getHelmScriptPath() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(propPath)));
		} catch (Exception e) {

		}
		return prop.getProperty("secretPath");
	}

	public static View handleError(String error, String instanceName, Logger logger) {
		logger.error(instanceName + " " + error);
		Map<String, String> personalisation = new HashMap<>();
		personalisation.put("instanceName", instanceName);
		personalisation.put("error", error);
		personalisation.put("environment", Util.GetHost());
		try {
			Notification.getNotificationClient().sendEmail("d9ce2f37-7ec1-4848-833b-de3d3947673e", Util.getAdminEmail(),
					personalisation, instanceName);
		} catch (Exception e) {
			logger.error("Cannot send error message through notify...");
		}
		return new RedirectView(
				"e?lang=" + LocaleContextHolder.getLocale().getDisplayLanguage().substring(0, 2).toLowerCase());
	}
	
	public static String ExecuteCommand(String workingDirectory, String command) {
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
					if ((line2 = b.readLine()) != null) {
						output.append(line2 + "\n").toString();
					}
					return "ERROR " + output; 
					
				} catch (final IOException e) {
					return "ERROR " + e.getMessage();
				}
			}

		} catch (IOException e) {
			Util.handleError(e.getMessage(), command, logger);
			return e.getMessage();
		} catch (InterruptedException e) {
			Util.handleError(e.getMessage(), command, logger);
			return e.getMessage();
		}
	}

}
