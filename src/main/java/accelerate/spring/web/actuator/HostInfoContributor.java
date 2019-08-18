package accelerate.spring.web.actuator;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import accelerate.commons.data.DataMap;
import accelerate.spring.ProfileConstants;

/**
 * {@link InfoContributor} to provide host information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
@Component
public class HostInfoContributor implements InfoContributor {
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

		try {
			InetAddress localhost = InetAddress.getLocalHost();
			dataMap.add("hostname", localhost.getHostName()).add("address", localhost.getHostAddress());
		} catch (@SuppressWarnings("unused") UnknownHostException error) {
			dataMap.put("hostname", "UNAVAILABLE");
		}

		/*
		 * OS information
		 */
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		dataMap.add("os-name", operatingSystemMXBean.getName()).add("os-arch", operatingSystemMXBean.getArch())
				.add("os-version", operatingSystemMXBean.getVersion())
				.add("availableProcessors", operatingSystemMXBean.getAvailableProcessors())
				.add("systemLoadAverage", operatingSystemMXBean.getSystemLoadAverage());

		aBuilder.withDetail("host", dataMap);
	}
}
