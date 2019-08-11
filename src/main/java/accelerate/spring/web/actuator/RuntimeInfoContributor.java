package accelerate.spring.web.actuator;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import accelerate.commons.data.DataMap;
import accelerate.commons.util.DateTimeUtils;
import accelerate.spring.ProfileConstants;

/**
 * {@link InfoContributor} to provide runtime information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
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
		DataMap dataMap = DataMap.newMap();

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
