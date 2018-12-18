package accelerate.spring.web.actuator.info;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import accelerate.commons.data.DataMap;

/**
 * {@link InfoContributor} to provide diskspace information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@ConditionalOnWebApplication
@ConditionalOnExpression("#{'${accelerate.spring.web.actuator.info.diskspace:${accelerate.spring.defaults:disabled}}' == 'enabled'}")
@Component
public class DiskSpaceInfoContributor implements InfoContributor {
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
		 * File system information
		 */
		long mbVal = 1024 * 1024;
		dataMap.putAnd("unit", "mb").put("roots", Arrays.stream(File.listRoots())
				.map(aFSRoot -> DataMap.newMap().putAnd("path", aFSRoot.getAbsolutePath())
						.putAnd("totalSpace", (aFSRoot.getTotalSpace() / mbVal) + " mb")
						.putAnd("usableSpace", (aFSRoot.getUsableSpace() / mbVal) + " mb")
						.putAnd("freeSpace", (aFSRoot.getFreeSpace() / mbVal) + " mb"))
				.collect(Collectors.toList()));

		aBuilder.withDetail("diskspace", dataMap);
	}
}
