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
import org.springframework.web.servlet.view.RedirectView;

import ca.tbssct.ep.Notification;
import ca.tbssct.ep.Util;

@Controller
public class EPRequestController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/request")
	public String greetingForm(Model model) {
		logger.info("Vistor has accessed the form.");
		model.addAttribute("request", new EPRequest());
		return "index";
	}

	@PostMapping("/requestPost")
	public RedirectView handleRequest(@ModelAttribute EPRequest request) throws Exception {
		// avoid long domain names cap it to 30 characters
		String domainNamePrefix = request.getDomainNamePrefix().toLowerCase();
		domainNamePrefix = domainNamePrefix.substring(0, Math.min(domainNamePrefix.length(), 30));
		request.setDomainNamePrefix(domainNamePrefix);

		String requestName = request.getDomainNamePrefix() + "_" + System.currentTimeMillis();

		Map<String, String> personalisation = new HashMap<>();
		personalisation.put("name", request.getYourName());
		personalisation.put("link", Util.GetVerificationURL() + "/verification?id=" + requestName);

		try {
			logger.info("Sending email through notify:" + request.getEmailAddress());
			Notification.getNotificationClient().sendEmail(this.getConfirmationTemplateId(), request.getEmailAddress(),
					personalisation, requestName);
			XMLEncoder encoder = null;
			try {
				logger.info("Writing file:" + requestName);
				encoder = new XMLEncoder(
						new BufferedOutputStream(new FileOutputStream("/home/requests/" + requestName)));
			} catch (FileNotFoundException fileNotFound) {
				Util.handleError("ERROR: While Creating or Opening the request file: " + requestName, requestName,
						logger);
			}
			encoder.writeObject(request);
			encoder.close();
		} catch (Exception e) {
			Util.handleError(e.getMessage(), request.getDomainNamePrefix(), logger);
		}
		RedirectView view = new RedirectView("result");
		return view;
	}

	@GetMapping("/result")
	public String handleGetRequest() {
		return "result";
	}

	public String getConfirmationTemplateId() {
		return "d5604c35-5a3c-4b3d-b084-6fc5c2abad2f";
	}

}
