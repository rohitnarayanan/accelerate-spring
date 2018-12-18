package accelerate.spring.web.view;

import static accelerate.commons.constants.CommonConstants.EMPTY_STRING;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import accelerate.commons.data.DataMap;
import accelerate.commons.utils.WebUtils;

/**
 * {@link RestController} providing API for monitoring information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 16, 2018
 */
@ConditionalOnWebApplication
@ConditionalOnExpression("#{'${accelerate.spring.web.view.error:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
@Controller
public class ErrorView extends BaseHTMLView {
	/**
	 * Main HTML string
	 */
	private static final String HTML_FRAGMENT_BODY = "<div class=\"jumbotron alert alert-danger\">" //
			+ "<div>" //
			+ "<h2>Exception</h2>" //
			+ "<p>There was an error in processing your request. Please try again in a little while or contact the Administrator for further help.</p>" //
			+ "</div>" //
			+ "</div>" //
			+ "<div><p><strong><u>Error Details</u></strong></p><ul>%s</ul></div>" //
			+ "<div><p><strong><u>Request Details</u></strong></p><ul>%s</ul></div>" //
			+ "<div><p><strong><u>Request Parameters</u></strong></p><ul>%s</ul></div>";

	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, path = {
			"${server.error.path:${error.path:/error}}" }, produces = "text/html")
	public @ResponseBody String error(HttpServletRequest aRequest) {
		DataMap<Object> debugMap = WebUtils.debugErrorRequest(aRequest);
		String format = "<li>%s = %s</li>";

		StringBuilder errorDetailsBuffer = new StringBuilder();
		((Map<?, ?>) debugMap.get("errorDetails")).entrySet()
				.forEach(entry -> errorDetailsBuffer.append(String.format(format, entry.getKey(), entry.getValue())));

		StringBuilder requestDetailsBuffer = new StringBuilder();
		((Map<?, ?>) debugMap.get("requestDetails")).entrySet()
				.forEach(entry -> requestDetailsBuffer.append(String.format(format, entry.getKey(), entry.getValue())));

		StringBuilder requestParamsBuffer = new StringBuilder();
		((Map<?, ?>) debugMap.get("requestParams")).entrySet()
				.forEach(entry -> requestParamsBuffer.append(String.format(format, entry.getKey(), entry.getValue())));

		return buildHTML(getAppName() + " - Error", EMPTY_STRING,
				String.format(HTML_FRAGMENT_BODY, errorDetailsBuffer, requestDetailsBuffer, requestParamsBuffer),
				EMPTY_STRING);
	}
}
