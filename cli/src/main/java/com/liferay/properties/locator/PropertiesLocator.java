/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.properties.locator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.util.ArrayUtil;
import com.liferay.blade.cli.util.CamelCaseUtil;
import com.liferay.blade.cli.util.ListUtil;
import com.liferay.blade.cli.util.Pair;
import com.liferay.blade.cli.util.StringPool;
import com.liferay.blade.cli.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Alberto Chaparro
 * @author Gregory Amerson
 */
public class PropertiesLocator {

	public static void main(String[] args) throws Exception {
		PropertiesLocatorArgs propertiesLocatorArgs = new PropertiesLocatorArgs();

		JCommander jCommander = new JCommander(propertiesLocatorArgs);

		try {
			File jarFile = _getJarFile();

			if (jarFile.isFile()) {
				jCommander.setProgramName("java -jar " + jarFile.getName());
			}
			else {
				jCommander.setProgramName(PropertiesLocator.class.getName());
			}

			jCommander.parse(args);

			if (propertiesLocatorArgs.isHelp()) {
				jCommander.usage();
			}
			else {
				new PropertiesLocator(propertiesLocatorArgs);
			}
		}
		catch (ParameterException parameterException) {
			System.err.println(parameterException.getMessage());

			jCommander.usage();
		}
	}

	public PropertiesLocator(PropertiesLocatorArgs propertiesLocatorArgs) throws Exception {
		try (PrintWriter outputWriter = _getOutputWriter(propertiesLocatorArgs)) {
			Set<String> oldPropertyKeys = _getPropertyKeys(propertiesLocatorArgs.getPropertiesFile());

			File bundleDir = propertiesLocatorArgs.getBundleDir();

			bundleDir = bundleDir.getAbsoluteFile();

			_bundlePath = bundleDir.toPath();

			Properties newProperties = _getCurrentPortalProperties();

			_problems = _getProblems(oldPropertyKeys, newProperties);

			_manageExceptions(_problems);

			_managePortletProperties(_problems);

			_manageConfigurationProperties(_problems);

			_printInfo(outputWriter, _problems);
		}
		catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
	}

	public SortedSet<PropertyProblem> getProblems() {
		return _problems;
	}

	private static File _getJarFile() throws Exception {
		ProtectionDomain protectionDomain = PropertiesLocator.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		return new File(url.toURI());
	}

	private static boolean _isLiferayJar(String path) {
		if (!path.endsWith(".jar") || !path.contains("com.liferay")) {
			return false;
		}

		return true;
	}

	private String[] _addConfigurationPropertiesByInheritance(
		String superClass, String[] configFields, Map<String, ConfigurationClassData> configClassesMap) {

		if (!superClass.equals("java/lang/Object")) {
			ConfigurationClassData superClassData = configClassesMap.get(superClass);

			String[] superConfigFields = new String[0];

			if (superClassData != null) {
				superConfigFields = _addConfigurationPropertiesByInheritance(
					superClassData.getSuperClass(), superClassData.getConfigFields(), configClassesMap);
			}

			return ArrayUtil.append(configFields, superConfigFields);
		}

		return configFields;
	}

	private List<Pair<String, String>> _filterMostLikelyMatches(
		String property, String[] portletNames, List<Pair<String, String>> mostLikelyMatches) {

		List<Pair<String, String>> theMostLikelyMatches = new ArrayList<>();

		String[] portletNameAsProperty = {_getPortletNameAsProperty(portletNames)};

		for (Pair<String, String> match : mostLikelyMatches) {

			// Check for containing whole portletName in the path

			if (_pathContainsPortletName(match.first(), portletNameAsProperty)) {
				theMostLikelyMatches.add(new Pair<>(match.first(), match.second()));
			}
		}

		if (!theMostLikelyMatches.isEmpty()) {
			mostLikelyMatches = theMostLikelyMatches;

			theMostLikelyMatches = new ArrayList<>();
		}

		for (Pair<String, String> match : mostLikelyMatches) {

			// Check for containing same suffix the original property

			if (_matchSuffix(property, match.second())) {
				theMostLikelyMatches.add(new Pair<>(match.first(), match.second()));
			}
		}

		if (!theMostLikelyMatches.isEmpty()) {
			return theMostLikelyMatches;
		}

		return mostLikelyMatches;
	}

	private void _getCommentedPropertiesFromJar(String propertiesJarURL, Properties properties) throws Exception {
		URL url = new URL(propertiesJarURL);

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
			bufferedReader.lines(
			).filter(
				line -> line.matches(".*#[a-zA-Z\\.\\[\\]]+=.*")
			).map(
				line -> line.substring(line.indexOf("#") + 1, line.indexOf("="))
			).forEach(
				line -> properties.put(line, "")
			);
		}
		catch (Exception exception) {
			System.err.println("Unable to read properties file from jar " + propertiesJarURL);

			throw exception;
		}
	}

	private List<Pair<String, String[]>> _getConfigurationProperties(
		Map<String, ConfigurationClassData> configClassesMap) {

		List<Pair<String, String[]>> configurationProperties = new ArrayList<>();

		for (Map.Entry<String, ConfigurationClassData> configClass : configClassesMap.entrySet()) {
			ConfigurationClassData configClassData = configClass.getValue();

			String[] allConfigFields = _addConfigurationPropertiesByInheritance(
				configClassData.getSuperClass(), configClassData.getConfigFields(), configClassesMap);

			if (allConfigFields.length > 0) {
				String className = configClass.getKey();

				configurationProperties.add(new Pair<>(className, allConfigFields));
			}
		}

		return configurationProperties;
	}

	private Properties _getCurrentPortalProperties() throws Exception {
		Properties properties = new Properties();

		BiPredicate<Path, BasicFileAttributes> matcher = (path, attrs) -> {
			String pathString = path.toString();

			return attrs.isRegularFile() && pathString.endsWith(_PORTAL_IMPL_RELATIVE_PATH);
		};

		try (Stream<Path> paths = Files.find(_bundlePath, Integer.MAX_VALUE, matcher)) {
			Optional<Path> portalImplPath = paths.findFirst();

			portalImplPath.ifPresent(
				path -> {
					try {
						_getPropertiesFromJar("jar:file:" + path.toString() + "!/portal.properties", properties);
						_getCommentedPropertiesFromJar(
							"jar:file:" + path.toString() + "!/portal.properties", properties);
					}
					catch (Exception exception) {
					}
				});
		}

		_removeScopedProperties(properties);

		if (properties.isEmpty()) {
			throw new Exception("File portal.properties does not exist in " + _bundlePath);
		}

		return properties;
	}

	private String _getEquivalence(String portletName) {
		String equivalence = _portletNameEquivalences.get(portletName);

		if (equivalence != null) {
			return equivalence;
		}

		return portletName;
	}

	private List<Pair<String, String>> _getMostLikelyMatches(
		String property, List<Pair<String, String[]>> matches, String[] portletNames) {

		List<Pair<String, String>> mostLikelyMatches = new ArrayList<>();

		//Default min occurrences to match
		int maxOccurrences = 2;

		for (Pair<String, String[]> match : matches) {
			for (String matchProperty : match.second()) {
				if (_match(property, matchProperty, match.first(), maxOccurrences, portletNames)) {
					int occurrences = _getOccurrences(property, matchProperty);

					if (occurrences > maxOccurrences) {
						mostLikelyMatches.clear();

						maxOccurrences = occurrences;
					}

					mostLikelyMatches.add(new Pair<>(match.first(), matchProperty));
				}
			}
		}

		if (!mostLikelyMatches.isEmpty()) {
			mostLikelyMatches = _filterMostLikelyMatches(property, portletNames, mostLikelyMatches);
		}

		return mostLikelyMatches;
	}

	private int _getOccurrences(String originalProperty, String property) {
		String originalPropertyWithoutPrefix = _removeCommonPrefix(originalProperty);

		if (!property.contains(StringPool.PERIOD)) {
			//Camel case property
			property = CamelCaseUtil.fromCamelCase(property, StringPool.PERIOD.charAt(0));
		}

		String[] propertyWords = StringUtil.split(property, StringPool.PERIOD);

		String[] originalPropertyWords = StringUtil.split(originalPropertyWithoutPrefix, StringPool.PERIOD);

		List<String> originalPropertyWordsList = ListUtil.fromArray(originalPropertyWords);

		int numOccurrences = 0;

		for (String word : propertyWords) {
			if (originalPropertyWordsList.contains(word)) {
				numOccurrences++;
			}
		}

		return numOccurrences;
	}

	private PrintWriter _getOutputWriter(PropertiesLocatorArgs propertiesLocatorArgs) throws FileNotFoundException {
		File outputFile = propertiesLocatorArgs.getOutputFile();

		if (outputFile != null) {
			return new PrintWriter(outputFile);
		}
		else if (propertiesLocatorArgs.isQuiet()) {
			return new PrintWriter(
				new OutputStream() {

					@Override
					public void write(int b) {
					}

				});
		}
		else {
			return new PrintWriter(System.out);
		}
	}

	private String _getPortletNameAsProperty(String[] portletNames) {
		String portletNameAsProperty = StringPool.BLANK;

		for (String portletName : portletNames) {
			if (portletNameAsProperty.length() > 0) {
				portletNameAsProperty += StringPool.PERIOD;
			}

			portletNameAsProperty += portletName;
		}

		return portletNameAsProperty;
	}

	private String[] _getPortletNames(String property) {
		String[] portletNames = new String[0];

		int index = 0;

		while ((portletNames.length < 2) && (index != -1)) {
			index = property.indexOf(StringPool.PERIOD);

			String portletName;

			if (index == -1) {
				portletName = property;
			}
			else {
				portletName = property.substring(0, index);

				property = property.substring(index + 1);
			}

			portletNames = ArrayUtil.append(portletNames, portletName);
		}

		return portletNames;
	}

	/*
		We get portlet names from first two words in a property
	 */
	private SortedSet<PropertyProblem> _getProblems(Set<String> oldPropertyKeys, Properties newProperties) {
		SortedSet<PropertyProblem> problems = new TreeSet<>();

		for (String oldPropertyKey : oldPropertyKeys) {
			if (oldPropertyKey.contains("[")) {
				oldPropertyKey = oldPropertyKey.substring(0, oldPropertyKey.indexOf("["));
			}

			if (newProperties.getProperty(oldPropertyKey) == null) {
				problems.add(new PropertyProblem(oldPropertyKey, PropertyProblemType.MISSING));
			}
			else {
				problems.add(new PropertyProblem(oldPropertyKey, PropertyProblemType.NONE));
			}
		}

		return problems;
	}

	private void _getPropertiesFromJar(String propertiesJarURL, Properties properties) throws Exception {
		try {
			URL url = new URL(propertiesJarURL);

			InputStream is = url.openStream();

			properties.load(is);

			is.close();
		}
		catch (Exception exception) {
			System.out.println("Unable to read properties file " + propertiesJarURL);

			throw exception;
		}
	}

	private Set<String> _getPropertyKeys(File file) throws Exception {
		try {
			List<String> lines = Files.readAllLines(file.toPath());

			return lines.stream(
			).map(
				String::trim
			).filter(
				line ->
					!line.startsWith("#") && !line.equals(StringPool.BLANK) && line.contains(StringPool.EQUALS) &&
					line.contains(StringPool.PERIOD)
			).map(
				line -> line.substring(0, line.indexOf(StringPool.EQUALS))
			).collect(
				Collectors.toSet()
			);
		}
		catch (Exception exception) {
			System.out.println("Unable to read properties file " + file.getCanonicalPath());

			throw exception;
		}
	}

	private void _manageConfigurationProperties(SortedSet<PropertyProblem> problems) throws IOException {
		Map<String, ConfigurationClassData> configClassesMap = new TreeMap<>();

		try (Stream<Path> paths = Files.walk(_bundlePath)) {
			Stream<File> files = paths.map(path -> path.toFile());

			files.filter(
				file -> {
					String absolutePath = file.getAbsolutePath();

					return (absolutePath.endsWith(".jar") || absolutePath.endsWith(".lpkg")) &&
						   !absolutePath.contains("/osgi/state/");
				}
			).forEach(
				file -> {
					try {
						String absolutePath = file.getAbsolutePath();

						if (_isLiferayJar(absolutePath)) {
							FileInputStream inputStream = new FileInputStream(absolutePath);

							try (JarInputStream jarIs = new JarInputStream(inputStream)) {
								ZipEntry zipEntryJar = jarIs.getNextEntry();

								while (zipEntryJar != null) {
									String name = zipEntryJar.getName();

									if (name.endsWith("Configuration.class")) {
										configClassesMap.put(
											name.replace(".class", StringPool.BLANK),
											new ConfigurationClassData(jarIs));
									}

									zipEntryJar = jarIs.getNextEntry();
								}
							}
							catch (Exception exception) {
								System.out.println("Unable to read the content of " + absolutePath);

								return;
							}
						}
						else if (absolutePath.endsWith(".lpkg")) {
							try (ZipFile zipFile = new ZipFile(absolutePath)) {
								Enumeration<?> enu = zipFile.entries();

								while (enu.hasMoreElements()) {
									ZipEntry zipEntry = (ZipEntry)enu.nextElement();

									if (_isLiferayJar(zipEntry.getName())) {
										InputStream inputStream = zipFile.getInputStream(zipEntry);

										try (JarInputStream jarIs = new JarInputStream(inputStream)) {
											ZipEntry zipEntryJar = jarIs.getNextEntry();

											while (zipEntryJar != null) {
												String name = zipEntryJar.getName();

												if (name.endsWith("Configuration.class")) {
													configClassesMap.put(
														name.replace(".class", StringPool.BLANK),
														new ConfigurationClassData(jarIs));
												}

												zipEntryJar = jarIs.getNextEntry();
											}
										}
										catch (Exception exception) {
										}
									}
								}
							}
						}
					}
					catch (Exception exception) {
						System.out.println("Unable to get portlet properties");

						return;
					}
				}
			);
		}

		List<Pair<String, String[]>> configurationProperties = _getConfigurationProperties(configClassesMap);

		problems.stream(
		).filter(
			problem -> problem.getType() == PropertyProblemType.MISSING
		).forEach(
			problem -> {
				String property = problem.getPropertyName();

				List<Pair<String, String>> mostLikelyMatches = _getMostLikelyMatches(
					property, configurationProperties, _getPortletNames(property));

				if (!mostLikelyMatches.isEmpty()) {
					problem.setReplacements(mostLikelyMatches);
					problem.setType(PropertyProblemType.OSGI);
				}
			}
		);
	}

	private void _manageExceptions(SortedSet<PropertyProblem> problems) {
		for (PropertyProblem problem : problems) {
			if (problem.getType() != PropertyProblemType.MISSING) {
				continue;
			}

			String property = problem.getPropertyName();

			if (property.endsWith("display.templates.config") && !property.equals("blogs.display.templates.config") &&
				!property.equals("dl.display.templates.config")) {

				problem.setMessage("Overwrite the method in the ADT handler. See LPS-67466");
				problem.setType(PropertyProblemType.REMOVED);
			}
			else if (property.endsWith("breadcrumb.display.style.default")) {
				problem.setMessage(
					"Use ddmTemplateKeyDefault in com.liferay.site.navigation.breadcrumb.web.internal.configuration." +
						"SiteNavigationBreadcrumbWebTemplateConfiguration. More information at Breaking Changes for " +
							"Liferay 7: https://dev.liferay.com/develop/reference/-/knowledge_base/7-0/breaking-changes#replaced-the-breadcrumb-portlets-display-styles-with-adts");

				problem.setType(PropertyProblemType.OSGI);
			}
			else if (property.endsWith("breadcrumb.display.style.options")) {
				problem.setMessage(
					"Any DDM template as ddmTemplate_BREADCRUMB-HORIZONTAL-FTL can be used. More information at " +
						"Breaking Changes for Liferay 7: https://dev.liferay.com/develop/reference/-/knowledge_base/7-0/breaking-changes#replaced-the-breadcrumb-portlets-display-styles-with-adts");

				problem.setType(PropertyProblemType.REMOVED);
			}
			else if (property.startsWith("upgrade.processes")) {
				problem.setMessage("Upgrade process properties are not longer needed");
				problem.setType(PropertyProblemType.REMOVED);
			}
		}
	}

	private void _managePortletProperties(SortedSet<PropertyProblem> problems) throws Exception {
		List<Pair<String, String[]>> portletsProperties = new ArrayList<>();

		// We don't need to analyze war files since, they are still like in previous versions so properties
		// still remain in the same place

		Predicate<Path> ignoreStateFilter = p -> {
			String path = p.toString();

			return !path.contains("/osgi/state/");
		};

		Predicate<Path> lpkgFilter = p -> {
			String path = p.toString();

			return path.endsWith(".lpkg");
		};

		Path searchPathRoot = _bundlePath.resolve("osgi");

		try (Stream<Path> paths = Files.walk(searchPathRoot)) {
			Stream<Path> filter = paths.filter(ignoreStateFilter);

			filter.map(
				jarPath -> jarPath.toAbsolutePath()
			).map(
				absolutePath -> absolutePath.toString()
			).filter(
				PropertiesLocator::_isLiferayJar
			).forEach(
				absolutePath -> {
					try (JarFile jarFile = new JarFile(absolutePath)) {
						JarEntry portletPropertiesFile = jarFile.getJarEntry("portlet.properties");

						Properties portletProperties = new Properties();

						if (portletPropertiesFile != null) {
							_getPropertiesFromJar(
								"jar:file:" + absolutePath + "!/portlet.properties", portletProperties);
						}

						Enumeration<Object> enuKeys = portletProperties.keys();

						String[] propertyKeys = new String[0];

						while (enuKeys.hasMoreElements()) {
							propertyKeys = ArrayUtil.append(propertyKeys, (String)enuKeys.nextElement());
						}

						if (propertyKeys.length != 0) {
							portletsProperties.add(
								new Pair<String, String[]>(absolutePath + "/portlet.properties", propertyKeys));
						}
					}
					catch (Exception exception) {
					}
				}
			);
		}

		try (Stream<Path> paths = Files.walk(searchPathRoot)) {
			paths.filter(
				ignoreStateFilter
			).filter(
				lpkgFilter
			).map(
				lpkgPath -> lpkgPath.toAbsolutePath()
			).map(
				absolutePath -> absolutePath.toString()
			).forEach(
				lpkgAbsolutePath -> {
					try (ZipFile zipFile = new ZipFile(lpkgAbsolutePath)) {
						Enumeration<?> enu = zipFile.entries();

						while (enu.hasMoreElements()) {
							ZipEntry zipEntry = (ZipEntry)enu.nextElement();

							if (_isLiferayJar(zipEntry.getName())) {
								InputStream inputStream = zipFile.getInputStream(zipEntry);

								try (JarInputStream jarIs = new JarInputStream(inputStream)) {
									ZipEntry zipEntryJar = jarIs.getNextEntry();

									while (zipEntryJar != null) {
										String zipEntryJarName = zipEntryJar.getName();

										if (zipEntryJarName.equals("portlet.properties")) {
											Properties portletProperties = new Properties();

											portletProperties.load(jarIs);

											Enumeration<Object> enuKeys = portletProperties.keys();

											String[] propertyKeys = new String[0];

											while (enuKeys.hasMoreElements()) {
												propertyKeys = ArrayUtil.append(
													propertyKeys, (String)enuKeys.nextElement());
											}

											if (propertyKeys.length != 0) {
												String prop =
													lpkgAbsolutePath + "/" + zipEntry.getName() + "/portlet.properties";

												portletsProperties.add(new Pair<String, String[]>(prop, propertyKeys));
											}

											break;
										}

										zipEntryJar = jarIs.getNextEntry();
									}
								}
								catch (Exception exception) {
								}
							}
						}
					}
					catch (IOException ioException) {
					}
				}
			);
		}

		problems.stream(
		).filter(
			problem -> problem.getType() == PropertyProblemType.MISSING
		).forEach(
			problem -> {
				String property = problem.getPropertyName();

				List<Pair<String, String>> mostLikelyMatches = _getMostLikelyMatches(
					property, portletsProperties, _getPortletNames(property));

				if (!mostLikelyMatches.isEmpty()) {
					problem.setReplacements(mostLikelyMatches);
					problem.setType(PropertyProblemType.MODULARIZED);
				}
			}
		);
	}

	private boolean _match(
		String originalProperty, String newProperty, String newPropertyPath, int minOccurrences,
		String[] portletNames) {

		if (!_pathContainsPortletName(newPropertyPath, portletNames)) {
			return false;
		}

		int numOccurrences = _getOccurrences(originalProperty, newProperty);

		if ((numOccurrences == 0) || (numOccurrences < minOccurrences)) {
			return false;
		}

		return true;
	}

	private boolean _matchSuffix(String originalProperty, String property) {
		if (!property.contains(StringPool.PERIOD)) {
			//Camel case property
			property = CamelCaseUtil.fromCamelCase(property, StringPool.PERIOD.charAt(0));
		}

		String[] propertyWords = StringUtil.split(property, StringPool.PERIOD);

		String propertySuffix =
			propertyWords[propertyWords.length - 2] + StringPool.PERIOD + propertyWords[propertyWords.length - 1];

		if (originalProperty.endsWith(propertySuffix)) {
			return true;
		}

		return false;
	}

	private boolean _pathContainsPortletName(String propertyPath, String[] portletNames) {
		for (String portletName : portletNames) {
			portletName = _getEquivalence(portletName);

			if ((portletName != null) && propertyPath.contains(portletName)) {
				return true;
			}
		}

		return false;
	}

	private void _printInfo(PrintWriter outputFile, SortedSet<PropertyProblem> problems) {
		problems.forEach(
			problem -> {
				outputFile.println(problem.getPropertyName());

				outputFile.print("\t");

				if (problem.getType() == PropertyProblemType.NONE) {
					outputFile.println("KEEP - This property is still present in the new portal.properties.");
				}
				else if (problem.getType() == PropertyProblemType.MISSING) {
					outputFile.println(
						"ANALYZE - This property is not present in thew new portal.properties. Check if you still need it or check the documentation to find a replacement");
				}
				else if (problem.getType() == PropertyProblemType.REMOVED) {
					outputFile.println("REMOVE - " + problem.getMessage());
				}
				else if (problem.getType() == PropertyProblemType.OSGI) {
					outputFile.print("MODULARIZE AS OSGI - ");

					if (problem.getMessage() != null) {
						outputFile.print(problem.getMessage());
					}
					else {
						outputFile.println(
							"This property matches with the following OSGI config, select the most appropriate:");

						_printOSGIReplacements(outputFile, problem.getReplacements());
					}
				}
				else if (problem.getType() == PropertyProblemType.MODULARIZED) {
					outputFile.print("MODULARIZE - ");

					if (problem.getMessage() != null) {
						outputFile.print(problem.getMessage());
					}
					else {
						outputFile.println(
							"This property matches with the following portlet properties, select the most appropriate:");

						_printModularizedReplacements(outputFile, problem.getReplacements());
					}
				}

				outputFile.println();
			});
	}

	private void _printModularizedReplacements(PrintWriter outputFile, List<Pair<String, String>> replacements) {
		replacements.forEach(
			replacement -> {
				String path = String.valueOf(_bundlePath.relativize(Paths.get(replacement.first())));

				path = path.replaceAll("\\\\", "/");

				outputFile.print("\t\t- ");
				outputFile.println(replacement.second() + " from " + path);
			});
	}

	private void _printOSGIReplacements(PrintWriter outputFile, List<Pair<String, String>> replacements) {
		replacements.stream(
		).sorted(
			(r1, r2) -> {
				String r1First = r1.first();
				String r2First = r2.first();

				return r1First.compareTo(r2First);
			}
		).forEach(
			replacement -> {
				String path = replacement.first();

				String configFileName = StringUtil.replace(
					path, StringPool.FORWARD_SLASH.charAt(0), StringPool.PERIOD.charAt(0));

				outputFile.print("\t\t- ");
				outputFile.println(replacement.second() + " from " + configFileName);
			}
		);
	}

	private String _removeCommonPrefix(String property) {
		for (String prefix : _COMMON_PREFIXES) {
			if (property.startsWith(prefix)) {
				property = property.replace(prefix, StringPool.BLANK);

				if (property.startsWith(StringPool.PERIOD)) {
					property = property.substring(1);
				}

				break;
			}
		}

		return property;
	}

	private void _removeScopedProperties(Properties properties) {
		Set<String> propertiesSet = properties.stringPropertyNames();

		for (String property : propertiesSet) {
			if (property.contains("[")) {
				property = property.substring(0, property.indexOf("["));
			}

			properties.put(property, "");
		}
	}

	private static final String[] _COMMON_PREFIXES = {
		"asset", "dynamic.data.lists", "dynamic.data.mapping", "journal", "audit", "auth", "blogs", "bookmarks", "cas",
		"journal", "wiki"
	};

	private static final String _PORTAL_IMPL_RELATIVE_PATH =
		File.separator + "WEB-INF" + File.separator + "shielded-container-lib" + File.separator + "portal-impl.jar";

	private static Path _bundlePath;

	@SuppressWarnings("serial")
	private static final Map<String, String> _portletNameEquivalences = new TreeMap<String, String>() {
		{
			put("dl", "document-library");
		}
	};

	private SortedSet<PropertyProblem> _problems = Collections.emptySortedSet();

}