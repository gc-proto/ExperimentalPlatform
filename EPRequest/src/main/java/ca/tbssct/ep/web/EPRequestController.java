package ca.tbssct.ep.web;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ca.tbssct.ep.Notification;


@Controller
public class EPRequestController {

	public static String SERVER = "http://localhost:8888/verification?id=";

	@GetMapping("/request")
	public String greetingForm(Model model) {
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
			Notification.getNotificationClient().sendEmail(this.getConfirmationTemplateId(),
					request.getEmailAddress(), personalisation, requestName);
			XMLEncoder encoder = null;
			try {
				encoder = new XMLEncoder(new BufferedOutputStream(
						new FileOutputStream("/home/hyma/ExperimentalPlatform/requests/" + requestName)));
			} catch (FileNotFoundException fileNotFound) {
				System.out.println("ERROR: While Creating or Opening the File dvd.xml");
				
			}
			encoder.writeObject(request);
			encoder.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "result";
	}

	public String getConfirmationTemplateId() {
		return "d5604c35-5a3c-4b3d-b084-6fc5c2abad2f";
	}
}
