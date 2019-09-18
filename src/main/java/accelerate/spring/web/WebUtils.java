package accelerate.spring.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerMapping;

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
	 * @param aRequest {@link HttpServletRequest} instance
	 * @return
	 */
	public static final String extractPathFromPattern(HttpServletRequest aRequest) {
		String path = (String) aRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) aRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String finalPath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);

		LOGGER.debug("Extracted path [{}] from URL[{}] for pattern [{}]", finalPath, aRequest.getRequestURI(), path);
		return finalPath;
	}

	/**
	 * @param aRequest
	 * @return
	 */
	public static DataMap debugRequest(HttpServletRequest aRequest) {
		DataMap debugMap = DataMap.newMap();

		DataMap requestDetails = DataMap.newMap();
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

		debugMap.put("details", requestDetails);
		debugMap.put("params", aRequest.getParameterMap().entrySet().parallelStream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()[0])));
		debugMap.put("attributes", Collections.list(aRequest.getAttributeNames()));

		return debugMap;
	}

	/**
	 * @param aRequest
	 * @return
	 */
	public static DataMap debugSession(HttpServletRequest aRequest) {
		HttpSession session = aRequest.getSession(false);
		if (session == null) {
			return null;
		}

		return DataMap.newMap("id", session.getId(), "attributes", Collections.list(session.getAttributeNames()));
	}

	/**
	 * @param aRequest {@link HttpServletRequest} instance
	 * @return
	 */
	public static final DataMap debugErrorRequest(HttpServletRequest aRequest) {
		DataMap errorDetails = DataMap.newMap();
		errorDetails.put("requestURI", aRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
		errorDetails.put("errorStatusCode", aRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
		errorDetails.put("errorMessage", aRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE));
		errorDetails.put("errorType", aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE));
		errorDetails.put("errorStackTrace",
				CommonUtils.getErrorLog((Throwable) aRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION)));

		return debugRequest(aRequest).add("error", errorDetails);
	}

	/**
	 * @param aRequest
	 * @param aResponse
	 * @param aCookieList
	 * @throws ServletException
	 */
	public static final void logout(HttpServletRequest aRequest, HttpServletResponse aResponse, String... aCookieList)
			throws ServletException {
		Assert.noNullElements(aCookieList, "List of cookies cannot contain null values");

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
		Assert.notEmpty(aCookieList, "Cookie list cannot be empty");

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