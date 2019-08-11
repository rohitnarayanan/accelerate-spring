package accelerate.spring.aop;

import static accelerate.commons.constant.CommonConstants.SPACE;

import java.util.Arrays;
import java.util.function.Consumer;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

/**
 * Spring {@link Aspect} to capture method calls within the application. This
 * method publishes 'before' and 'after' events for any listener to handle.
 * 
 * NOTE: This uses spring's runtime AOP, and is able to capture method calls on
 * spring managed beans only. It does not capture constructors, or internal
 * method calls. For a more comprehensive profiling use other tools or enable
 * LoadTime/CompileTime weaving in your application.
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since July 05, 2019
 */
@Configuration
@Aspect
public class SpringBootAspect {
	/**
	 * The base-packages for the application
	 */
	@Value("${accelerate.spring.base-packages:accelerate}")
	private String[] basePackages;

	/**
	 * All {@link MethodInterceptor} implementations available
	 */
	@Autowired(required = false)
	private MethodInterceptor[] interceptors = null;

	/**
	 * This method intercepts all method call in the application. It then filters
	 * the ones that are inside 'accelerate.spring.base-packages' and allows
	 * all MethodInterceptor(ss.
	 * 
	 * @param aJoinPoint the {@link JoinPoint} caught by the {@link Around} advice
	 * @return the object returned by the target method
	 * @throws Throwable
	 */
	public Object interceptMethod(ProceedingJoinPoint aJoinPoint) throws Throwable {
		if (!interceptCall(aJoinPoint)) {
			return aJoinPoint.proceed();
		}

		String[] signature = aJoinPoint.getSignature().toString().split(SPACE);
		String returnType = signature[0];
		String method = signature[1];
		final Object[] returnValue = { null };
		final Throwable[] error = { null };

		invokeInterceptors(
				aInterceptor -> aInterceptor.beforeMethod(aJoinPoint.getTarget(), method, aJoinPoint.getArgs()));

		try {
			returnValue[0] = aJoinPoint.proceed();
		} catch (Throwable throwable) {
			error[0] = throwable;
		}

		invokeInterceptors(aInterceptor -> aInterceptor.afterMethod(aJoinPoint.getTarget(), method, returnType,
				returnValue[0], error[0]));

		if (error[0] != null) {
			throw error[0];
		}

		return returnValue[0];
	}

	/**
	 * Check if interceptors are available and apply the given function
	 * 
	 * @param aConsumer
	 */
	private void invokeInterceptors(final Consumer<MethodInterceptor> aConsumer) {
		if (ObjectUtils.isEmpty(this.interceptors)) {
			return;
		}

		Arrays.stream(this.interceptors).forEach(aInterceptor -> aConsumer.accept(aInterceptor));
	}

	/**
	 * This method determines whether the method call should be intercepted
	 * 
	 * @param aJoinPoint
	 * @return
	 */
	private boolean interceptCall(ProceedingJoinPoint aJoinPoint) {
		String packageName = aJoinPoint.getTarget().getClass().getPackageName();
		for (String basePackage : this.basePackages) {
			if (packageName.startsWith(basePackage)) {
				LOGGER.trace("Intercepting method {}", aJoinPoint.getSignature());
				return true;
			}
		}

		return false;
	}

	/**
	 * {@link Logger} instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootAspect.class);
}