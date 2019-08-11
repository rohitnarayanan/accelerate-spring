package accelerate.spring.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import accelerate.commons.constant.CommonConstants;
import accelerate.commons.data.DataMap;
import accelerate.commons.util.CommonUtils;
import accelerate.commons.util.StringUtils;

/**
 * Class providing utility methods for web app operations
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 3, 2017
 */
public class WebUtils {
	/**
	 * @param aRequest
	 * @return
	 */
	public static DataMap debugRequest(HttpServletRequest aRequest) {
		DataMap debugMap = DataMap.newMap();

		Map<String, Object> requestDetails = new TreeMap<>();
		requestDetails.put("contextPath", aRequest.getContextPath());
		requestDetails.put("localAddr", aRequest.getLocalAddr());
		requestDetails.put("localName", aRequest.getLocalName());
		requestDetails.put("localPort", aRequest.getLocalPort());
		requestDetails.put("pathInfo", aRequest.getPathInfo());
		requestDetails.put("pathTranslated", aRequest.getPathTranslated());
		requestDetails.put("protocol", aRequest.getProtocol());
		requestDetails.put("queryString", aRequest.getQueryString());
		requestDetails.put("remoteAddr", aRequest.getRemoteAddr());
		requestDetails.put("remoteHost", aRequest.getRemoteHost());
		requestDetails.put("remotePort", aRequest.getRemotePort());
		requestDetails.put("remoteUser", aRequest.getRemoteUser());
		requestDetails.put("requestURI", aRequest.getRequestURI());
		requestDetails.put("requestURL", aRequest.getRequestURL());
		requestDetails.put("scheme", aRequest.getScheme());
		requestDetails.put("serverName", aRequest.getServerName());
		requestDetails.put("serverPort", aRequest.getServerPort());
		requestDetails.put("servletPath", aRequest.getServletPath());
		debugMap.put("requestDetails", requestDetails);

		Map<String, Object> requestParams = new TreeMap<>();
		aRequest.getParameterMap().entrySet().parallelStream()
				.forEach(entry -> requestParams.put(entry.getKey(), entry.getValue()[0]));
		debugMap.put("requestParams", requestParams);

		return debugMap;
	}

	/**
	 * @param aRequest
	 * @return
	 */
	public static final DataMap debugRequestDeep(HttpServletRequest aRequest) {
		DataMap debugMap = debugRequest(aRequest);

		Map<String, Object> requestAttributes = new TreeMap<>();
		Collections.list(aRequest.getAttributeNames()).parallelStream()
				.forEach(name -> requestAttributes.put(name, aRequest.getAttribute(name)));
		debugMap.put("requestAttributes", requestAttributes);

		return debugMap;
	}

	/**
	 * @param aRequest {@link HttpServletRequest} instance
	 * @return
	 */
	public static final DataMap debugErrorRequest(HttpServletRequest aRequest) {
		DataMap debugMap = debugRequest(aRequest);

		Map<String, Object> errorDetails = new TreeMap<>();
		errorDetails.put("requestURI", aRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
		errorDetails.put("errorStatusCode", aRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
		errorDetails.put("errorMessage", aRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE));
		errorDetails.put("errorType", aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE));
		errorDetails.put("errorStackTrace",
				CommonUtils.getErrorLog((Throwable) aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION)));
		debugMap.put("errorDetails", errorDetails);

		return debugMap;
	}

	/**
	 * @param aRequest
	 * @param aResponse
	 * @param aCookieList
	 * @throws ServletException
	 */
	public static final void logout(HttpServletRequest aRequest, HttpServletResponse aResponse, String... aCookieList)
			throws ServletException {
		HttpSession session = aRequest.getSession(false);
		if (session != null) {
			LOGGER.debug("Invalidating session[{}] for user [{}]", session.getId(), aRequest.getRemoteUser());
			session.invalidate();
		}

		aRequest.logout();
		if (aCookieList != null) {
			deleteCookies(aRequest, aResponse, aCookieList);
		}
	}

	/**
	 * @param aRequest
	 * @param aResponse
	 * @param aCookieList
	 */
	public static final void deleteCookies(HttpServletRequest aRequest, HttpServletResponse aResponse,
			String... aCookieList) {
//		Assert.noNullElements(new Object[] { aRequest, aResponse, aCookieList }, "All arguments are required");
//		Assert.noNullElements(aCookieList, "List of cookies are required");

		Arrays.stream(aCookieList).map(cookieName -> {
			LOGGER.debug("Resetting cookie [{}]", cookieName);
			Cookie cookie = new Cookie(cookieName, null);
			cookie.setPath(StringUtils.defaultString(aRequest.getContextPath(), CommonConstants.UNIX_PATH_SEPARATOR));
			cookie.setMaxAge(0);
			return cookie;
		}).forEach(cookie -> aResponse.addCookie(cookie));
	}

	/**
	 * hidden constructor
	 */
	private WebUtils() {
	}

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebUtils.class);
}