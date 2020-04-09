package ca.tbssct.ep.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.tbssct.ep.Notification;

@Controller
public class SendEmailController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@CrossOrigin
	@PostMapping("/email")
	public @ResponseBody String handleRequest(HttpServletRequest request) throws Exception {
		// avoid long domain names cap it to 30 characters
		try {
			logger.info("Sending comment email through notify." + request.getParameter("dtoemailAddress"));
			Map<String, String> personalisation = new HashMap<>();
			personalisation.put("name", request.getParameter("name"));
			personalisation.put("url", request.getParameter("commenturl"));
			personalisation.put("comment", request.getParameter("comments"));
			personalisation.put("email", request.getParameter("emailAddress"));
			Notification.getNotificationClient().sendEmail(Notification.COMMENT_EMAIL_TEMPLATE_ID,
					request.getParameter("dtoemailAddress"), personalisation, "");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return e.getMessage();
		}
		return "Success";
	}
}
