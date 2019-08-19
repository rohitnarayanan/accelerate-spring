package accelerate.spring.web;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import accelerate.commons.data.DataMap;
import accelerate.spring.ProfileConstants;
import accelerate.spring.logging.Profiled;

/**
 * {@link RestController} providing REST_API for monitoring information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 16, 2018
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
@ConditionalOnExpression("${accelerate.spring.web.default-view.error:${accelerate.spring.defaults:true}}")
@Component("error")
@Profiled
public class DefaultErrorView implements View {
	/**
	 * {@link ServletContext} instance to get context path
	 */
	@Autowired
	private ServletContext servletContext = null;

	/**
	 * Display name of the application
	 */
	@Value("${spring.application.name}")
	private String appName = null;

	/**
	 * {@link WebConfigProps} instance
	 */
	@Autowired
	private WebConfigProps webConfigProps = null;

	/**
	 * Main HTML string
	 */
	private static final String HTML_COMMON_SECTION = WebConstants.HTML_FRAGMENT_START + WebConstants.COMMON_STYLES //
			+ "</head>" //
			+ "<body>" //
			+ "	<div class=\"jumbotron alert alert-danger\">" //
			+ "		<div>" //
			+ "			<h2>Exception</h2>" //
			+ "			<p>There was an error in processing your request. Please try again in a little while or contact the Administrator for further help.</p>" //
			+ "		</div>" //
			+ "	</div>";

	/**
	 * Main HTML string
	 */
	private static final String HTML_ERROR_DETAILS = "	<div><p><strong><u>Error Details</u></strong></p><ul>%s</ul></div>" //
			+ "	<div><p><strong><u>Request Details</u></strong></p><ul>%s</ul></div>" //
			+ "	<div><p><strong><u>Request Parameters</u></strong></p><ul>%s</ul></div>";

	/**
	 * Formatted HTML Section
	 */
	private String htmlSectionStart = null;

	/**
	 * 
	 */
	@PostConstruct
	public void initialize() {
		this.htmlSectionStart = String.format(HTML_COMMON_SECTION, this.servletContext.getContextPath(),
				this.webConfigProps.getFaviconPath(), this.appName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.View#getContentType()
	 */
	/**
	 * @return
	 */
	@Override
	public String getContentType() {
		return MediaType.TEXT_HTML_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.View#render(java.util.Map,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	/**
	 * @param aModel
	 * @param aRequest
	 * @param aResponse
	 * @throws Exception
	 */
	@Override
	public void render(Map<String, ?> aModel, HttpServletRequest aRequest, HttpServletResponse aResponse)
			throws Exception {
		if (aResponse.isCommitted()) {
			LOGGER.error(
					"Cannot render error page for request [{}] with exception [{}] as the response has already been committed. As a result, the response may have the wrong status code.");
			return;
		}

		if (aResponse.getContentType() == null) {
			aResponse.setContentType(getContentType());
		}

		DataMap debugMap = WebUtils.debugErrorRequest(aRequest);
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

		aResponse.getWriter().append(this.htmlSectionStart)
				.append(String.format(HTML_ERROR_DETAILS, errorDetailsBuffer.toString(),
						requestDetailsBuffer.toString(), requestParamsBuffer.toString()))
				.append(WebConstants.HTML_FRAGMENT_END);
	}

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultErrorView.class);
}
