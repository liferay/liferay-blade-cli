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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
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
		catch (ParameterException pe) {
			System.err.println(pe.getMessage());

			jCommander.usage();
		}
	}

	public PropertiesLocator(PropertiesLocatorArgs propertiesLocatorArgs) throws Exception {
		_outputFile = _generateOutputFile(propertiesLocatorArgs);

		String title = "Checking the location for old properties in the new version";

		_outputFile.println(title);
		_printUnderline(title);

		try {
			File propertiesFile = propertiesLocatorArgs.getPropertiesFile();

			Properties oldProperties = _getProperties(propertiesFile.toPath());

			File bundleDir = propertiesLocatorArgs.getBundleDir();

			Path bundlePath = bundleDir.toPath();

			Properties newProperties = _getCurrentPortalProperties(bundlePath);

			SortedSet<String> stilExistsProperties = new TreeSet<>();

			SortedSet<String> missingProperties = _getRemovedProperties(
				oldProperties, newProperties, stilExistsProperties);

			Stream<String> stream = missingProperties.stream();

			SortedSet<PropertyProblem> problems = stream.map(
				PropertyProblem::new
			).collect(
				Collectors.toCollection(TreeSet::new)
			);

			problems = _manageExceptions(problems);

			_outputFile.println();

			problems = _checkPortletProperties(problems, bundlePath);

			_outputFile.println();

			problems = _checkConfigurationProperties(problems, bundlePath);

			_outputFile.println();
			_outputFile.println(
				"We haven't found a new property for the following old properties (check if you still need them or " +
					"check the documentation to find a replacement):");

			Stream<PropertyProblem> problemsStream = problems.stream();

			missingProperties = problemsStream.filter(
				problem -> problem.getType() == PropertyProblemType.MISSING
			).map(
				problem -> problem.getPropertyName()
			).collect(
				Collectors.toCollection(TreeSet::new)
			);

			_printProperties(missingProperties);

			_outputFile.println();
			_outputFile.println("The following properties still exist in the new portal.properties:");

			_printProperties(stilExistsProperties);

			_problems = problems;
		}
		finally {
			_outputFile.close();
		}
	}

	public SortedSet<PropertyProblem> getProblems() {
		return _problems;
	}

	private static String[] _addConfigurationPropertiesByHeritance(
		String superClass, String[] configFields, Map<String, ConfigurationClassData> configClassesMap) {

		if (!superClass.equals("java/lang/Object")) {
			ConfigurationClassData superClassData = configClassesMap.get(superClass);

			String[] superConfigFields = new String[0];

			if (superClassData != null) {
				superConfigFields = _addConfigurationPropertiesByHeritance(
					superClassData.getSuperClass(), superClassData.getConfigFields(), configClassesMap);
			}

			return ArrayUtil.append(configFields, superConfigFields);
		}

		return configFields;
	}

	private static SortedSet<PropertyProblem> _checkConfigurationProperties(
			SortedSet<PropertyProblem> problems, Path searchPathRoot)
		throws IOException {

		Map<String, ConfigurationClassData> configClassesMap = new HashMap<>();

		try (Stream<Path> paths = Files.walk(searchPathRoot)) {
			Stream<File> files = paths.map(path -> path.toFile());

			files.filter(
				file -> {
					String absolutePath = file.getAbsolutePath();

					return ((absolutePath.endsWith(".jar")) ||
						 (absolutePath.endsWith(".lpkg"))) &&
						 (!absolutePath.contains("/osgi/state/"));
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
									if (zipEntryJar.getName().endsWith("Configuration.class")) {
										configClassesMap.put(
											zipEntryJar.getName().replace(".class", StringPool.BLANK),
											new ConfigurationClassData(jarIs));
									}

									zipEntryJar = jarIs.getNextEntry();
								}
							}
							catch (Exception e) {
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
										catch (Exception e) {
											continue;
										}
									}
								}
							}
						}
					}
					catch (Exception e) {
						System.out.println("Unable to get portlet properties");

						e.printStackTrace();

						return;
					}
				}
			);
		}

		List<Pair<String, String[]>> configurationProperties = _getConfigurationProperties(configClassesMap);

		SortedSet<PropertyProblem> updatedProblems = new TreeSet<>();

		Stream<PropertyProblem> problemsStream = problems.stream();

		problemsStream.filter(
			problem -> problem.getType() == PropertyProblemType.MISSING
		).forEach(
			problem -> {
				String property = problem.getPropertyName();

				List<Pair<String, String>> mostLikelyMatches = _getMostLikelyMatches(
					property, configurationProperties, _getPortletNames(property));

				if (!mostLikelyMatches.isEmpty()) {
					updatedProblems.add(
						new PropertyProblem(
							property, PropertyProblemType.OSGI, "This property has been modularized",
							mostLikelyMatches));
				}
				else {
					updatedProblems.add(problem);
				}
			}
		);

		Stream<PropertyProblem> stream = updatedProblems.stream();

		boolean foundOsgiProblems = stream.filter(
			problem -> problem.getType() == PropertyProblemType.OSGI
		).findAny(
		).isPresent();

		if (foundOsgiProblems) {
			_outputFile.println("Properties moved to OSGI configuration:");

			stream = updatedProblems.stream();

			stream.filter(
				problem -> problem.getType() == PropertyProblemType.OSGI
			).peek(
				problem -> {
					String property = problem.getPropertyName();

					_outputFile.print("\t");
					_outputFile.println(property + " can match with the following OSGI properties:");
				}
			).map(
				problem -> problem.getReplacements()
			).forEach(
				replacements -> {
					Stream<Pair<String, String>> replacementsStream = replacements.stream();

					replacementsStream.sorted(
						(r1, r2) -> r1.first().compareTo(r2.first())
					).forEach(
						replacement -> {
							String path = replacement.first();

							String configFileName = StringUtil.replace(
								path, StringPool.FORWARD_SLASH.charAt(0), StringPool.PERIOD.charAt(0));

							_outputFile.print("\t\t");
							_outputFile.println(replacement.second() + " from " + configFileName);
						}
					);
				}
			);
		}

		return updatedProblems;
	}

	private static SortedSet<PropertyProblem> _checkPortletProperties(
			SortedSet<PropertyProblem> problems, Path bundlePath)
		throws Exception {

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

		Path searchPathRoot = bundlePath.resolve("osgi");

		try (Stream<Path> paths = Files.walk(searchPathRoot)) {
			Stream<Path> filter = paths.filter(ignoreStateFilter);

			filter.map(
				jarPath -> jarPath.toAbsolutePath().toString()
			).filter(
				PropertiesLocator::_isLiferayJar
			).forEach(
				jarAbsolutePath -> {
					try (JarFile jarFile = new JarFile(jarAbsolutePath)) {
						JarEntry portletPropertiesFile = jarFile.getJarEntry("portlet.properties");

						Properties portletProperties = new Properties();

						if (portletPropertiesFile != null) {
							_getPropertiesFromJar(
								"jar:file:" + jarAbsolutePath + "!/portlet.properties", portletProperties);
						}

						Enumeration<Object> enuKeys = portletProperties.keys();

						String[] propertyKeys = new String[0];

						while (enuKeys.hasMoreElements()) {
							propertyKeys = ArrayUtil.append(propertyKeys, (String)enuKeys.nextElement());
						}

						if (propertyKeys.length != 0) {
							portletsProperties.add(
								new Pair<String, String[]>(jarAbsolutePath + "/portlet.properties", propertyKeys));
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			);
		}

		try (Stream<Path> paths = Files.walk(searchPathRoot)) {
			Stream<Path> filter = paths.filter(ignoreStateFilter);

			Stream<Path> filter2 = filter.filter(lpkgFilter);

			filter2.map(
				lpkgPath -> lpkgPath.toAbsolutePath().toString()
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
										if (zipEntryJar.getName().equals("portlet.properties")) {
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
								catch (Exception e) {
									continue;
								}
							}
						}
					}
					catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}

			);
		}

		Stream<PropertyProblem> stream = problems.stream();

		SortedSet<PropertyProblem> updatedProblems = new TreeSet<>();

		stream.forEach(
			problem -> {
				String property = problem.getPropertyName();

				List<Pair<String, String>> mostLikelyMatches = _getMostLikelyMatches(
					property, portletsProperties, _getPortletNames(property));

				if (!mostLikelyMatches.isEmpty()) {
					PropertyProblem updatedProblem = new PropertyProblem(
						property, PropertyProblemType.MODULARIZED, null, mostLikelyMatches);

					updatedProblems.add(updatedProblem);
				}
				else {
					updatedProblems.add(problem);
				}
			});

		Predicate<? super PropertyProblem> propertyMoved =
			problem -> problem.getType() == PropertyProblemType.MODULARIZED;

		boolean somePropertiesMoved = false;

		Stream<PropertyProblem> updatedProblemsStream = updatedProblems.stream();

		long count = updatedProblemsStream.filter(
			propertyMoved
		).count();

		if (count > 0) {
			somePropertiesMoved = true;
		}

		if (somePropertiesMoved) {
			_outputFile.println("Some properties have been moved to a module portlet.properties:");

			updatedProblemsStream = updatedProblems.stream();

			updatedProblemsStream.filter(
				propertyMoved
			).filter(
				problem -> problem.getReplacements() != null
			).peek(
				problem -> {
					String foundProperty = problem.getPropertyName();

					_outputFile.print("\t");
					_outputFile.println(foundProperty + " can match with the following portlet properties:");
				}
			).flatMap(
				problem -> problem.getReplacements().stream()
			).forEach(
				replacement -> {
					Path modulePath = Paths.get(replacement.first());

					Path relativePath = bundlePath.relativize(modulePath);

					String path = relativePath.toString();

					path = path.replaceAll("\\\\", "/");

					_outputFile.print("\t\t");
					_outputFile.println(replacement.second() + " from " + path);
				}
			);
		}

		return updatedProblems;
	}

	private static List<Pair<String, String>> _filterMostLikelyMatches(
		String property, String[] portletNames, List<Pair<String, String>> mostLikelyMatches) {

		List<Pair<String, String>> theMostLikelyMatches = new ArrayList<>();

		String[] portletNameAsProperty = new String[1];

		portletNameAsProperty[0] = _getPortletNameAsProperty(portletNames);

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
		else {
			return mostLikelyMatches;
		}
	}

	private static PrintWriter _generateOutputFile(PropertiesLocatorArgs propertiesLocatorArgs)
		throws FileNotFoundException {

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

	private static List<Pair<String, String[]>> _getConfigurationProperties(
		Map<String, ConfigurationClassData> configClassesMap) {

		List<Pair<String, String[]>> configurationProperties = new ArrayList<>();

		for (Map.Entry<String, ConfigurationClassData> configClass : configClassesMap.entrySet()) {
			String className = configClass.getKey();
			ConfigurationClassData configClassData = configClass.getValue();

			String[] allConfigFields = _addConfigurationPropertiesByHeritance(
				configClassData.getSuperClass(), configClassData.getConfigFields(), configClassesMap);

			if (allConfigFields.length > 0) {
				configurationProperties.add(new Pair<>(className, allConfigFields));
			}
		}

		return configurationProperties;
	}

	private static Properties _getCurrentPortalProperties(Path bundlePath) throws Exception {
		Properties properties = new Properties();

		BiPredicate<Path, BasicFileAttributes> matcher = (path, attrs) -> {
			String pathString = path.toString();

			return attrs.isRegularFile() && pathString.endsWith(_PORTAL_IMPL_RELATIVE_PATH);
		};

		try (Stream<Path> paths = Files.find(bundlePath, Integer.MAX_VALUE, matcher)) {
			Stream<Path> singlePath = paths.limit(1);

			singlePath.forEach(
				path -> {
					try {
						_getPropertiesFromJar("jar:file:" + path.toString() + "!/portal.properties", properties);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				});
		}

		if (properties.isEmpty()) {
			throw new Exception("File portal.properties doesn't exist in " + bundlePath);
		}

		return properties;
	}

	private static String _getEquivalence(String portletName) {
		String equivalence = _portletNameEquivalences.get(portletName);

		if (equivalence != null) {
			return equivalence;
		}

		return portletName;
	}

	private static File _getJarFile() throws Exception {
		ProtectionDomain protectionDomain = PropertiesLocator.class.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		return new File(url.toURI());
	}

	private static List<Pair<String, String>> _getMostLikelyMatches(
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

	private static int _getOccurrences(String originalProperty, String property) {
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

	private static String _getPortletNameAsProperty(String[] portletNames) {
		String portletNameAsProperty = StringPool.BLANK;

		for (String portletName : portletNames) {
			if (portletNameAsProperty.length() > 0) {
				portletNameAsProperty += StringPool.PERIOD;
			}

			portletNameAsProperty += portletName;
		}

		return portletNameAsProperty;
	}

	/*
		We get portlet names from first two words in a property
	 */
	private static String[] _getPortletNames(String property) {
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

	private static Properties _getProperties(Path propertiesPath) throws Exception {
		try (FileInputStream fileInput = new FileInputStream(propertiesPath.toFile())) {
			Properties properties = new Properties();

			properties.load(fileInput);

			fileInput.close();

			return properties;
		}
		catch (Exception e) {
			System.out.println("Unable to read properties file " + propertiesPath.toString());

			throw e;
		}
	}

	private static void _getPropertiesFromJar(String propertiesJarURL, Properties properties) throws Exception {
		try {
			URL url = new URL(propertiesJarURL);

			InputStream is = url.openStream();

			properties.load(is);
			is.close();
		}
		catch (Exception e) {
			System.out.println("Unable to read properties file " + propertiesJarURL);

			throw e;
		}
	}

	private static SortedSet<String> _getRemovedProperties(
		Properties oldProperties, Properties newProperties, SortedSet<String> remainedProperties) {

		SortedSet<String> removedProperties = new TreeSet<>();

		Enumeration<Object> enuKeys = oldProperties.keys();

		while (enuKeys.hasMoreElements()) {
			Object element = enuKeys.nextElement();

			String key = element.toString();

			if (newProperties.getProperty(key) == null) {
				removedProperties.add(key);
			}
			else {
				remainedProperties.add(key);
			}
		}

		return removedProperties;
	}

	private static boolean _isLiferayJar(String path) {
		if (!path.endsWith(".jar") || !path.contains("com.liferay")) {
			return false;
		}

		return true;
	}

	private static SortedSet<PropertyProblem> _manageExceptions(SortedSet<PropertyProblem> problems) {
		SortedSet<PropertyProblem> informationToPrint = new TreeSet<>();

		SortedSet<PropertyProblem> updatedProblems = new TreeSet<>();

		for (PropertyProblem problem : problems) {
			String property = problem.getPropertyName();

			if (property.endsWith("display.templates.config") && !property.equals("blogs.display.templates.config") &&
				!property.equals("dl.display.templates.config")) {

				PropertyProblem updatedProblem = new PropertyProblem(
					property, PropertyProblemType.REMOVED, "Overwrite the method in the ADT handler. See LPS-67466",
					null);

				informationToPrint.add(updatedProblem);

				updatedProblems.add(updatedProblem);
			}
			else if (property.endsWith("breadcrumb.display.style.default")) {
				PropertyProblem updatedProblem = new PropertyProblem(
					property, PropertyProblemType.MODULARIZED,
					" ddmTemplateKeyDefault in com.liferay.site.navigation.breadcrumb.web.configuration." +
						"SiteNavigationBreadcrumbWebTemplateConfiguration. More information at Breaking Changes for " +
							"Liferay 7: https://dev.liferay.com/develop/reference/-/knowledge_base/7-0/breaking-changes#replaced-the-breadcrumb-portlets-display-styles-with-adts",
					null);

				informationToPrint.add(updatedProblem);

				updatedProblems.add(problem);
			}
			else if (property.endsWith("breadcrumb.display.style.options")) {
				PropertyProblem updatedProblem = new PropertyProblem(
					property, PropertyProblemType.REMOVED,
					"Any DDM template as ddmTemplate_BREADCRUMB-HORIZONTAL-FTL can be used. More information at " +
						"Breaking Changes for Liferay 7: https://dev.liferay.com/develop/reference/-/knowledge_base/7-0/breaking-changes#replaced-the-breadcrumb-portlets-display-styles-with-adts",
					null);

				informationToPrint.add(updatedProblem);

				updatedProblems.add(problem);
			}
			else {
				updatedProblems.add(problem);
			}
		}

		if (!informationToPrint.isEmpty()) {
			_outputFile.println("Following portal properties present an exception:");

			for (PropertyProblem information : informationToPrint) {
				_outputFile.print("\t");
				_outputFile.println(information);
			}
		}

		return updatedProblems;
	}

	private static boolean _match(
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

	private static boolean _matchSuffix(String originalProperty, String property) {
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
		else {
			return false;
		}
	}

	private static boolean _pathContainsPortletName(String propertyPath, String[] portletNames) {
		for (String portletName : portletNames) {
			portletName = _getEquivalence(portletName);

			if (portletName != null) {
				if (propertyPath.contains(portletName)) {
					return true;
				}
			}
		}

		return false;
	}

	private static void _printProperties(Set<String> properties) {
		for (String property : properties) {
			_outputFile.print("\t");
			_outputFile.println(property);
		}
	}

	private static void _printUnderline(String text) {
		for (int i = 0; i < text.length(); i++) {
			_outputFile.print(StringPool.DASH);
		}

		_outputFile.println(StringPool.BLANK);
	}

	private static String _removeCommonPrefix(String property) {
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

	private static final String[] _COMMON_PREFIXES = {
		"asset", "dynamic.data.lists", "dynamic.data.mapping", "journal", "audit", "auth", "blogs", "bookmarks", "cas",
		"journal", "wiki"
	};

	private static final String _PORTAL_IMPL_RELATIVE_PATH =
		File.separator + "WEB-INF" + File.separator + "lib" + File.separator + "portal-impl.jar";

	private static PrintWriter _outputFile;
	private static final Map<String, String> _portletNameEquivalences;

	static {
		_portletNameEquivalences = new HashMap<>();

		_portletNameEquivalences.put("dl", "document-library");
	}

	private SortedSet<PropertyProblem> _problems;

}