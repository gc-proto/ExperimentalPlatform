package ca.tbssct.ep.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class LanguageFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String lang = (String) req.getParameter("lang");
		String altLang = "fr";
		String altLangText = "Français";
		String requestURL = req.getRequestURL().toString();
		String queryString = req.getQueryString();

		if (queryString != null) {
			requestURL = requestURL + "?" + queryString;
		}

		if (lang == null || lang.contentEquals("en")) {
			lang = "en";
		} else {
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

		chain.doFilter(request, response);

	}
}
