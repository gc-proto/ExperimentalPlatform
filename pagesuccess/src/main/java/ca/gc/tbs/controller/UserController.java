package ca.gc.tbs.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ca.gc.tbs.domain.User;
import ca.gc.tbs.repository.UserRepository;

@Controller
public class UserController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository repository;

	@GetMapping(value = "/u/update")
	public @ResponseBody String updateUser(HttpServletRequest request) {
		try {
			Optional<User> opt = repository.findById(request.getParameter("id"));
			User user = opt.get();
			boolean enabled = Boolean.parseBoolean(request.getParameter("enabled"));
			user.setEnabled(enabled);
			this.repository.save(user);
			return "Updated";
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	@GetMapping(value = "/u/delete")
	public @ResponseBody String deleteProblem(HttpServletRequest request) {
		try {
			this.repository.deleteById(request.getParameter("id"));
			return "deleted";
		} catch (Exception e) {
			return "Error:" + e.getMessage();
		}
	}

	public String getData() {

		String returnData = "";
		try {
			StringBuilder builder = new StringBuilder();
			List<User> users = this.repository.findAll();
			for (User user : users) {
				builder.append("<tr><td>" + user.getEmail() + "</td>");

				builder.append("<td>" + user.getDepartment() + "</td>");
				List<String> roles = user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toList());

				builder.append("<td>" + roles + "</td>");
				builder.append("<td>" + user.getDateCreated() + "</td>");
				builder.append("<td>" + (user.isEnabled() ? "Enabled" : "Awaiting approval") + "</td>");
				builder.append("<td><div class='btn-group'>");
				if (!user.isEnabled()) {
					builder.append("<button id='enable" + user.getId()
							+ "' class='btn btn-xs enableBtn'>Enable</button>");
				} else {
					builder.append("<button id='disable" + user.getId()
							+ "' class='btn btn-xs disableBtn'>Disable</button>");
				}
				builder.append("<button id='delete" + user.getId()
						+ "' class='btn btn-xs deleteBtn'>Delete</button>");

				builder.append("</div></td>");
				builder.append("</tr>");
			}
			returnData = builder.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return returnData;
	}

	@GetMapping(value = "/u/index")
	public ModelAndView dashboard() throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.addObject("data", this.getData());
		mav.setViewName("users");
		return mav;
	}

}
