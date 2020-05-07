package ca.canada.treasury.testbed.web;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

public class LanguageFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		if (req.getMethod().equals("GET")) {
			String lang = (String) req.getParameter("lang");
			String altLang = "fr";
			String altLangText = "Français";
			String requestURL = req.getRequestURL().toString();
			String queryString = req.getQueryString();

			if (lang == null) {
				if (queryString == null) {
					res.sendRedirect(requestURL + "?lang=en");
				} else {
					res.sendRedirect(requestURL + "?lang=en&" + queryString);
				}
				return;
			}
			if (queryString != null) {
				requestURL = requestURL + "?" + queryString;
			}
			if (!lang.equals("en")) {
				lang = "fr";
				altLang = "en";
				altLangText = "English";
			}
			if (queryString == null) {
				requestURL = requestURL + "?lang=" + altLang;
			} else {
				requestURL = requestURL.replace("lang=" + lang, "lang=" + altLang);
			}
			request.setAttribute("langUrl", requestURL);
			request.setAttribute("lang", lang);
			request.setAttribute("altLang", altLang);
			request.setAttribute("altLangText", altLangText);
		}

		chain.doFilter(request, response);

	}
}
