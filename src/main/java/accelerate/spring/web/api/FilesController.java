package accelerate.spring.web.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import accelerate.commons.constant.CommonConstants;
import accelerate.commons.data.DataMap;
import accelerate.commons.exception.ApplicationException;
import accelerate.commons.util.NIOUtils;
import accelerate.commons.util.StringUtils;
import accelerate.spring.ProfileConstants;
import accelerate.spring.logging.Profiled;

/**
 * {@link RestController} providing API for file operations
 * 
 * @version 1.0 Initial Version
 * @author Rohit Narayanan
 * @since October 20, 2018
 */
@Profile(ProfileConstants.PROFILE_WEB)
@ConditionalOnWebApplication
@ConditionalOnExpression("${accelerate.spring.web.api.files:${accelerate.spring.defaults:true}}")
@RestController
@RequestMapping(path = "${accelerate.spring.web.api:/webapi}/files", produces = MediaType.APPLICATION_JSON_VALUE)
@Profiled
public class FilesController {
	/**
	 * @param aPathString
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public static DataMap list(@RequestParam(name = "path") String aPathString) {
		final Path dirPath = Paths.get(aPathString);
		final DataMap dataMap = DataMap.newMap();

		dataMap.put("path", aPathString);
		if (Files.exists(dirPath)) {
			if (Files.isDirectory(dirPath)) {
				try {
					dataMap.put("directories", Files.list(dirPath).filter(aPath -> Files.isDirectory(aPath))
							.map(aPath -> NIOUtils.getFileName(aPath)).collect(Collectors.toList()));
					dataMap.put("files", Files.list(dirPath).filter(aPath -> Files.isRegularFile(aPath))
							.map(aPath -> NIOUtils.getFileName(aPath)).collect(Collectors.toList()));
				} catch (@SuppressWarnings("unused") IOException error) {
					dataMap.put("msg", "error reading directory contents");
				}

			} else {
				dataMap.put("msg", "path points to a file");
			}
		} else {
			dataMap.put("msg", "path does not exist");
		}

		return dataMap;
	}

	/**
	 * @param aPathString
	 * @param aFileNames
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public static DataMap delete(@RequestParam(name = "path") String aPathString,
			@RequestParam(name = "files", required = false) String aFileNames) {
		final Path dirPath = Paths.get(aPathString);

		Map<String, Boolean> deleteMap = Arrays.stream(StringUtils.split(aFileNames, CommonConstants.COMMA))
				.map(aFileName -> dirPath.resolve(aFileName))
				.collect(Collectors.toMap(aPath -> NIOUtils.getFileName(aPath), aPath -> {
					try {
						Files.delete(aPath);
					} catch (IOException error) {
						throw new ApplicationException(error);
					}

					return Files.exists(aPath);
				}));

		DataMap dataMap = list(aPathString);
		dataMap.put("deleted", deleteMap);
		return dataMap;
	}
}
