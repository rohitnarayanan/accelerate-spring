package accelerate.spring.web.view;

import static accelerate.commons.constants.CommonConstants.EMPTY_STRING;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import accelerate.spring.web.config.SecurityConfigProps;

/**
 * {@link RestController} providing API for monitoring information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@ConditionalOnWebApplication
@ConditionalOnExpression("#{'${accelerate.spring.web.view.login:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
@Controller
public class LoginView extends BaseHTMLView {
	/**
	 * {@link SecurityConfigProps} instance
	 */
	@Autowired
	private SecurityConfigProps securityConfigProps = null;

	/**
	 * Main HTML string
	 */
	private static final String HTML_FRAGMENT_BODY = "<h1 class=\"alert alert-secondary\">Login to %s</h1>" //
			+ "<div class=\"container-fluid\">" //
			+ "	<section>" //
			+ "		<div class=\"row\" style=\"margin-top: 20px;\">" //
			+ "			<div class=\"%s\">"//
			+ "				<i class=\"fas %s\"></i>%s" //
			+ "			</div>" //
			+ "		</div>" //
			+ "		<div class=\"row\">" //
			+ "			<form action=\"%s\" method=\"post\" class=\"form-signin\">"//
			+ "				<h2 class=\"form-signin-heading\">Please sign in</h2>"//
			+ "				<label for=\"inputUsername\" class=\"sr-only\">Username</label>"//
			+ "				<input type=\"text\" name=\"username\" id=\"inputUsername\" class=\"form-control\" placeholder=\"Username\" required=\"required\" autofocus=\"autofocus\" />"//
			+ "				<label for=\"inputPassword\" class=\"sr-only\">Password</label>"//
			+ "				<input type=\"password\" name=\"password\" id=\"inputPassword\" class=\"form-control\" placeholder=\"Password\" required=\"required\" />"//
			+ "				<button class=\"btn btn-lg btn-primary btn-block\" type=\"submit\">Sign in</button>"//
			+ "			</form>" //
			+ "		</div>" //
			+ "	</section>" //
			+ "</div>";

	/**
	 * @param aRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = { "#{securityConfigProps.loginPath}" }, produces = "text/html")
	public @ResponseBody String login(HttpServletRequest aRequest) {
		String alertClass = "";
		String iconClass = "";
		String alertMessage = "";
		String formAction = getContextPath() + this.securityConfigProps.getLoginPath();

		if (aRequest.getParameter("logout") != null) {
			alertClass = "alert-success";
			iconClass = "fa-check-circle";
			alertMessage = "You have been logged out";
		} else if (aRequest.getParameter("sessionExpired") != null) {
			alertClass = "alert-warning";
			iconClass = "fa-exclamation-triangle";
			alertMessage = "Session expired, please login again";
		} else if (aRequest.getParameter("sessionInvalid") != null) {
			alertClass = "alert-warning";
			iconClass = "fa-exclamation-triangle";
			alertMessage = "Invalid session, please login again";
		} else if (aRequest.getParameter("errorType") != null) {
			alertClass = "alert-warning";
			iconClass = "fa-exclamation-triangle";

			switch (aRequest.getParameter("errorType")) {
			case "incorrectLogin":
				alertMessage = "Invalid username or password, please try again";
				break;
			case "userDisabled":
				alertMessage = "User is disabled, contact administrator";
				break;
			case "userAccountExpired":
				alertMessage = "User account has expired, contact administrator";
				break;
			case "userCredentialsExpired":
				alertMessage = "Credentials have expired, please reset and try again";
				break;
			case "userAccountLocked":
				alertMessage = "User account is locked, contact administrator";
				break;
			default:
				alertMessage = "Error in login, please try again. If problem persists, contact administrator";
			}
		}

		// write the html
		return buildHTML(getAppName(),
				"<link rel=\"stylesheet\" href=\"" + getContextPath() + "/accelerate/styles/login.css\" >",
				String.format(HTML_FRAGMENT_BODY, getAppName(), alertClass, iconClass, alertMessage, formAction),
				EMPTY_STRING);
	}
}
