package accelerate.spring.web.view;

import static accelerate.commons.constants.CommonConstants.EMPTY_STRING;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import accelerate.commons.data.DataMap;
import accelerate.commons.utils.StringUtils;
import accelerate.commons.utils.WebUtils;

/**
 * {@link RestController} providing API for monitoring information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@ConditionalOnWebApplication
@ConditionalOnExpression("#{'${accelerate.spring.web.view.index:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
@Controller
public class IndexView extends BaseHTMLView {
	/**
	 * meta tag for home page redirect
	 */
	private String metaTag = "";

	/**
	 * Main HTML string
	 */
	private static final String HTML_FRAGMENT_BODY = "<h1 class=\"alert alert-primary\">Welcome to %s</h1>" //
			+ "	<div><p><strong><u>Request Details</u></strong></p><ul>%s</ul></div>" //
			+ "	<div><p><strong><u>Request Parameters</u></strong></p><ul>%s</ul></div>";

	/**
	 * Initialize method to prepare {@link #metaTag}
	 */
	@PostConstruct
	public void initialize() {
		String homePath = getWebConfigProps().getHomePath();
		if (!StringUtils.isEmpty(homePath)) {
			this.metaTag = String.format("<meta http-equiv=\"refresh\" content=\"0;%s\" />",
					getContextPath() + homePath);
		}
	}

	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = { "/", "/index" }, produces = "text/html")
	public @ResponseBody String index(HttpServletRequest aRequest) {
		DataMap<Object> debugMap = WebUtils.debugErrorRequest(aRequest);
		String format = "<li>%s = %s</li>";

		StringBuilder requestDetailsBuffer = new StringBuilder();
		((Map<?, ?>) debugMap.get("requestDetails")).entrySet().parallelStream()
				.forEach(entry -> requestDetailsBuffer.append(String.format(format, entry.getKey(), entry.getValue())));

		StringBuilder requestParamsBuffer = new StringBuilder();
		((Map<?, ?>) debugMap.get("requestParams")).entrySet().parallelStream()
				.forEach(entry -> requestParamsBuffer.append(String.format(format, entry.getKey(), entry.getValue())));

		return buildHTML(getAppName(), this.metaTag,
				String.format(HTML_FRAGMENT_BODY, getAppName(), requestDetailsBuffer, requestParamsBuffer),
				EMPTY_STRING);
	}
}
