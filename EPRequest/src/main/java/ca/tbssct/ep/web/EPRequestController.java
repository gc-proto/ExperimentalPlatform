package ca.tbssct.ep.web;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ca.tbssct.ep.Notification;


@Controller
public class EPRequestController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public static String SERVER = "http://eprequestform.canadacentral.cloudapp.azure.com/verification?id=";

	@GetMapping("/request")
	public String greetingForm(Model model) {
		logger.info("Vistor has accessed the form.");
		model.addAttribute("request", new EPRequest());
		return "index";
	}

	@PostMapping("/requestPost")
	public String greetingSubmit(@ModelAttribute EPRequest request) throws Exception {
		String requestName = request.getDomainNamePrefix() + "_" + System.currentTimeMillis();
		Map<String, String> personalisation = new HashMap<>();
		personalisation.put("name", request.getYourName());
		personalisation.put("link", SERVER + requestName);
		
		try {
			logger.info("Sending email through notify:"+request.getEmailAddress());
			Notification.getNotificationClient().sendEmail(this.getConfirmationTemplateId(),
					request.getEmailAddress(), personalisation, requestName);
			XMLEncoder encoder = null;
			try {
				logger.info("Writing file:"+requestName);
				encoder = new XMLEncoder(new BufferedOutputStream(
						new FileOutputStream("/home/requests/" + requestName)));
			} catch (FileNotFoundException fileNotFound) {
				logger.error("ERROR: While Creating or Opening the request file: "+requestName);
			}
			encoder.writeObject(request);
			encoder.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "result";
	}

	public String getConfirmationTemplateId() {
		return "d5604c35-5a3c-4b3d-b084-6fc5c2abad2f";
	}
	

}
