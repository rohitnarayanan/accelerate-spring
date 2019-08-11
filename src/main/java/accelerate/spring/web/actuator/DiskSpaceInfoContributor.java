package accelerate.spring.web.actuator;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import accelerate.commons.data.DataMap;
import accelerate.spring.ProfileConstants;

/**
 * {@link InfoContributor} to provide disk space information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
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
		DataMap dataMap = DataMap.newMap();

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
