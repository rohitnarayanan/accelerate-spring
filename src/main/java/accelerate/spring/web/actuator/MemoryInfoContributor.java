package accelerate.spring.web.actuator;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import accelerate.commons.data.DataMap;
import accelerate.spring.ProfileConstants;

/**
 * {@link InfoContributor} to provide memory information
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 22, 2018
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
@Component
public class MemoryInfoContributor implements InfoContributor {
	/**
	 * Flag to toggle verbose heap information
	 */
	@Value("${accelerate.spring.web.actuator.info.memory.verbose:false}")
	private Boolean verbose = true;

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

		Runtime runtime = Runtime.getRuntime();
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		long mbVal = 1024 * 1024;

		// get heap details from the runtime
		dataMap.putAnd("maxMemory", (runtime.maxMemory() / mbVal) + " mb")
				.putAnd("totalMemory", (runtime.totalMemory() / mbVal) + " mb")
				.putAnd("freeMemory", (runtime.freeMemory() / mbVal) + " mb")
				.putAnd("usedMemory", ((runtime.totalMemory() - runtime.freeMemory()) / mbVal) + " mb")
				.putAnd("heapMemory", memoryMXBean.getHeapMemoryUsage().toString())
				.putAnd("nonHeapMemory", memoryMXBean.getNonHeapMemoryUsage().toString())
				.putAnd("isVerbose", String.valueOf(memoryMXBean.isVerbose()));

		/*
		 * populate additional information if verbose is enabled
		 */
		if (this.verbose) {
			dataMap.put("pools",
					ManagementFactory.getMemoryPoolMXBeans().parallelStream().map(memoryPoolMXBean -> DataMap.newMap()
							.putAnd("name", memoryPoolMXBean.getName()).putAnd("valid", memoryPoolMXBean.isValid())
							.putAnd("type", memoryPoolMXBean.getType()).putAnd("usage", memoryPoolMXBean.getUsage())
							.putAnd("peakUsage", memoryPoolMXBean.getPeakUsage())
							.putAnd("collectionUsage", memoryPoolMXBean.getCollectionUsage()))
							.collect(Collectors.toList()));

			dataMap.put("pools",
					ManagementFactory.getGarbageCollectorMXBeans().parallelStream()
							.map(collectorMXBean -> DataMap.newMap().putAnd("name", collectorMXBean.getName())
									.putAnd("valid", collectorMXBean.isValid())
									.putAnd("collectionTime", collectorMXBean.getCollectionTime())
									.putAnd("collectionCount", collectorMXBean.getCollectionCount()))
							.collect(Collectors.toList()));

			/*
			 * populate Class Loader details
			 */
			ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
			dataMap.put("classLoader",
					DataMap.newMap().putAnd("verbose", classLoadingMXBean.isVerbose())
							.putAnd("loadedClassCount", classLoadingMXBean.getLoadedClassCount())
							.putAnd("totalLoadedClassCount", classLoadingMXBean.getTotalLoadedClassCount())
							.putAnd("unloadedClassCount", classLoadingMXBean.getUnloadedClassCount()));
		}

		aBuilder.withDetail("memory", dataMap);
	}
}
