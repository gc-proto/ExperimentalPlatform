package ca.tbssct.ep.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ca.tbssct.ep.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.SendEmailResponse;

@Controller
public class EPRequestController {

	@GetMapping("/request")
	public String greetingForm(Model model) {
		model.addAttribute("request", new Request());
		return "index";
	}

	@PostMapping("/requestPost")
	public String greetingSubmit(@ModelAttribute Request request) throws Exception {
		Map<String, String> personalisation = new HashMap<>();
		personalisation.put("name", request.getYourName());
		personalisation.put("link", "http://www.google.com");
		SendEmailResponse response = null;
		try {
			response = Notification.getNotificationClient()
					.sendEmail(this.getConfirmationTemplateId(), "ryan.hyma@tbs-sct.gc.ca", personalisation, "asdfasdf");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "result";
	}

	public String getConfirmationTemplateId() {
		return "d5604c35-5a3c-4b3d-b084-6fc5c2abad2f";
	}
}
