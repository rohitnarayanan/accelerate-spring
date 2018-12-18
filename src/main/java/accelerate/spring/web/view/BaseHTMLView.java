package accelerate.spring.web.view;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import accelerate.spring.web.config.WebConfigProps;

/**
 * {@link RestController} providing API for monitoring information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
public abstract class BaseHTMLView {
	/**
	 * {@link Logger} instance
	 */
	private static final Logger _logger = LoggerFactory.getLogger(BaseHTMLView.class);

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
	public static final String COMMON_STYLES = "<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css\" integrity=\"sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO\" crossorigin=\"anonymous\">";

	/**
	 * Main HTML string
	 */
	public static final String COMMON_SCRIPTS = "<script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\"></script>"
			+ "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js\" integrity=\"sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49\" crossorigin=\"anonymous\"></script>"
			+ "<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js\" integrity=\"sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy\" crossorigin=\"anonymous\"></script>"
			+ "<script src=\"https://cdn.jsdelivr.net/npm/vue/dist/vue.js\"></script>";

	/**
	 * HTML string for <HEAD/>
	 */
	public static final String HTML_FRAGMENT_HEAD = "<!DOCTYPE html><html lang=\"en\">" //
			+ "<head>" //
			+ "<meta charset=\"utf-8\">" //
			+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">" //
			+ "<meta name=\"description\" content=\"\">" //
			+ "<meta name=\"author\" content=\"\">" //
			+ "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"%s\" />" //
			+ "<title>%s</title>" //
			+ COMMON_STYLES //
			+ "%s" // additional head content
			+ "</head>";

	/**
	 * Main HTML string
	 */
	private static final String BASE_HTML = HTML_FRAGMENT_HEAD //
			+ "<body>" //
			+ "%s" // body content
			+ COMMON_SCRIPTS //
			+ "%s" // other scripts
			+ "</body>"//
			+ "</html>";

	/**
	 * @param aTitle
	 * @param aHeadFragment
	 * @param aBodyContent
	 * @param aScripts
	 * @return
	 */
	protected String buildHTML(String aTitle, String aHeadFragment, String aBodyContent, String aScripts) {
		StringBuilder htmlBuffer = new StringBuilder();
		htmlBuffer.append(String.format(BASE_HTML, getContextPath() + getWebConfigProps().getFavicon(), aTitle,
				aHeadFragment, aBodyContent, aScripts));

		_logger.debug("buildHTML --> {}", htmlBuffer);

		return htmlBuffer.toString();
	}

	/**
	 * @return
	 */
	protected String getContextPath() {
		return this.servletContext.getContextPath();
	}

	/**
	 * Getter method for "appName" property
	 * 
	 * @return appName
	 */
	protected String getAppName() {
		return this.appName;
	}

	/**
	 * Getter method for "webConfigProps" property
	 * 
	 * @return webConfigProps
	 */
	protected WebConfigProps getWebConfigProps() {
		return this.webConfigProps;
	}
}
