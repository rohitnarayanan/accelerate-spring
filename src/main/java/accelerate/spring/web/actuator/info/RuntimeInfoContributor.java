package accelerate.spring.web.actuator.info;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import accelerate.commons.data.DataMap;
import accelerate.commons.utils.DateTimeUtils;

/**
 * {@link InfoContributor} to provide runtime information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@ConditionalOnWebApplication
@ConditionalOnExpression("#{'${accelerate.spring.web.actuator.info.runtime:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
@Component
public class RuntimeInfoContributor implements InfoContributor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.boot.actuate.info.InfoContributor#contribute(org.
	 * springframework.boot.actuate.info.Info.Builder)
	 */
	/**
	 * @param aBuilder
	 */
	@Override
	public void contribute(Builder aBuilder) {
		DataMap<Object> dataMap = new DataMap<>();

		/*
		 * Runtime information
		 */
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		dataMap.putAnd("name", runtimeMXBean.getName()).putAnd("startTime", new Date(runtimeMXBean.getStartTime()))
				.putAnd("uptime", DateTimeUtils.convertToTime(runtimeMXBean.getUptime()))
				.putAnd("vmName", runtimeMXBean.getVmName()).putAnd("vmVersion", runtimeMXBean.getVmVersion())
				.putAnd("vmVendor", runtimeMXBean.getVmVendor()).putAnd("specName", runtimeMXBean.getSpecName())
				.putAnd("specVersion", runtimeMXBean.getSpecVersion())
				.putAnd("spencVendor", runtimeMXBean.getSpecVendor());

		aBuilder.withDetail("runtime", dataMap);
	}
}
