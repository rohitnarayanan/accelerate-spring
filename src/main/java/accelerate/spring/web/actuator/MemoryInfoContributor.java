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
		dataMap.add("maxMemory", (runtime.maxMemory() / mbVal) + " mb")
				.add("totalMemory", (runtime.totalMemory() / mbVal) + " mb")
				.add("freeMemory", (runtime.freeMemory() / mbVal) + " mb")
				.add("usedMemory", ((runtime.totalMemory() - runtime.freeMemory()) / mbVal) + " mb")
				.add("heapMemory", memoryMXBean.getHeapMemoryUsage().toString())
				.add("nonHeapMemory", memoryMXBean.getNonHeapMemoryUsage().toString())
				.add("isVerbose", String.valueOf(memoryMXBean.isVerbose()));

		/*
		 * populate additional information if verbose is enabled
		 */
		if (this.verbose) {
			dataMap.put("pools", ManagementFactory.getMemoryPoolMXBeans().parallelStream()
					.map(memoryPoolMXBean -> DataMap.newMap().add("name", memoryPoolMXBean.getName())
							.add("valid", memoryPoolMXBean.isValid()).add("type", memoryPoolMXBean.getType())
							.add("usage", memoryPoolMXBean.getUsage()).add("peakUsage", memoryPoolMXBean.getPeakUsage())
							.add("collectionUsage", memoryPoolMXBean.getCollectionUsage()))
					.collect(Collectors.toList()));

			dataMap.put("pools",
					ManagementFactory.getGarbageCollectorMXBeans().parallelStream()
							.map(collectorMXBean -> DataMap.newMap().add("name", collectorMXBean.getName())
									.add("valid", collectorMXBean.isValid())
									.add("collectionTime", collectorMXBean.getCollectionTime())
									.add("collectionCount", collectorMXBean.getCollectionCount()))
							.collect(Collectors.toList()));

			/*
			 * populate Class Loader details
			 */
			ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
			dataMap.put("classLoader",
					DataMap.newMap().add("verbose", classLoadingMXBean.isVerbose())
							.add("loadedClassCount", classLoadingMXBean.getLoadedClassCount())
							.add("totalLoadedClassCount", classLoadingMXBean.getTotalLoadedClassCount())
							.add("unloadedClassCount", classLoadingMXBean.getUnloadedClassCount()));
		}

		aBuilder.withDetail("memory", dataMap);
	}
}
