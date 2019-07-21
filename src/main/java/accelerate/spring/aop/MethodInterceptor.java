package accelerate.spring.aop;

/**
 * Provide an implementation of this interface to get call backs before and
 * after any method is invoked in the application.
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since July 05, 2019
 */
public interface MethodInterceptor {
	/**
	 * Method to get a call back before any method gets invoked in the application
	 * 
	 * @param aTarget
	 * @param aMethod
	 * @param aParams
	 * 
	 */
	@SuppressWarnings("unused")
	default void beforeMethod(Object aTarget, String aMethod, Object... aParams) {
		// default implementation
	}

	/**
	 * Method to get a call back after any method gets invoked in the application
	 * 
	 * @param aTarget
	 * @param aMethod
	 * @param aReturnType
	 * @param aReturnValue
	 * @param aError
	 * 
	 */
	@SuppressWarnings("unused")
	default void afterMethod(Object aTarget, String aMethod, String aReturnType, Object aReturnValue,
			Throwable aError) {
		// default implementation
	}
}
