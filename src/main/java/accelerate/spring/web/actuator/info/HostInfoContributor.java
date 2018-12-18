package accelerate.spring.web.actuator.info;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import accelerate.commons.data.DataMap;

/**
 * {@link InfoContributor} to provide host information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@ConditionalOnWebApplication
@ConditionalOnExpression("#{'${accelerate.spring.web.actuator.info.host:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
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
		DataMap<Object> dataMap = new DataMap<>();

		try {
			InetAddress localhost = InetAddress.getLocalHost();
			dataMap.putAnd("hostname", localhost.getHostName()).putAnd("address", localhost.getHostAddress());
		} catch (@SuppressWarnings("unused") UnknownHostException error) {
			dataMap.put("hostname", "UNAVAILABLE");
		}

		/*
		 * OS information
		 */
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		dataMap.putAnd("os-name", operatingSystemMXBean.getName()).putAnd("os-arch", operatingSystemMXBean.getArch())
				.putAnd("os-version", operatingSystemMXBean.getVersion())
				.putAnd("availableProcessors", operatingSystemMXBean.getAvailableProcessors())
				.putAnd("systemLoadAverage", operatingSystemMXBean.getSystemLoadAverage());

		aBuilder.withDetail("host", dataMap);
	}
}
