package accelerate.spring.web;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.walgreens.springboot.config.ConfigConstants;
import com.walgreens.springboot.logging.Profiled;

/**
 * {@link RestController} providing API for monitoring information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 16, 2018
 */
@Profile(ConfigConstants.PROFILE_WEB)
@ConditionalOnWebApplication
@ConditionalOnExpression("${com.walgreens.springboot.web.default-view.index:${com.walgreens.springboot.defaults:true}}")
@Controller
@Profiled
public class DefaultIndexPage {
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
	private static final String BASE_HTML = WebConstants.HTML_FRAGMENT_START + WebConstants.COMMON_STYLES //
			+ " %s" // redirect meta tag
			+ "</head>" //
			+ "<body>" //
			+ "	<h1 class=\"alert alert-primary\">Welcome to %s</h1>" //
			+ WebConstants.HTML_FRAGMENT_END;

	/**
	 * Meta Tag string for redirect
	 */
	private static final String REDIRECT_TAG = "<meta http-equiv=\"refresh\" content=\"0;%s\" />";

	/**
	 * Final HTML string for Index Page
	 */
	private String htmlString = null;

	/**
	 * Initialize method to prepare {@link #htmlString}
	 */
	@PostConstruct
	public void initialize() {
		String contextPath = this.servletContext.getContextPath();

		String metaTag = "";
		if (!StringUtils.isEmpty(this.webConfigProps.getHomePath())) {
			metaTag = String.format(REDIRECT_TAG, contextPath + this.webConfigProps.getHomePath());
		}

		this.htmlString = String.format(BASE_HTML, contextPath, this.webConfigProps.getFaviconPath(), this.appName,
				metaTag, this.appName);
	}

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = { "/", "/index" }, produces = "text/html")
	public @ResponseBody String index() {
		return this.htmlString;
	}
}