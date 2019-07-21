package accelerate.spring.web;

/**
 * PUT DESCRIPTION HERE
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since May 21, 2018
 */
public class WebConstants {
	/**
	 * Main HTML string
	 */
	public static final String COMMON_STYLES = "	<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css\" />";

	/**
	 * Main HTML string
	 */
	public static final String COMMON_SCRIPTS = "	<script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\"></script>" //
			+ "	<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js\"></script>" //
			+ "	<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js\"></script>";
	/**
	 * Main HTML string
	 */
	public static final String HTML_FRAGMENT_START = "<!DOCTYPE html><html lang=\"en\">" //
			+ "<head>" //
			+ "	<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"%s/%s\" />" //
			+ "	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\" />" //
			+ "	<title>%s</title>";

	/**
	 * Main HTML string
	 */
	public static final String HTML_FRAGMENT_END = WebConstants.COMMON_SCRIPTS + "</body></html>";
}
