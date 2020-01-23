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

package com.liferay.blade.cli.command;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;

/**
 * @author Gregory Amerson
 */
public class JavaProcesses {

	public static Collection<JavaProcess> list() {
		return list(Optional.empty());
	}

	@SuppressWarnings("unchecked")
	public static Collection<JavaProcess> list(Optional<Consumer<String>> logger) {
		Collection<JavaProcess> javaProcesses = new ArrayList<>();

		String version = System.getProperty("java.specification.version");

		try {
			if (version.indexOf('.') > -1) {
				version = version.substring(version.indexOf('.') + 1);
			}
			int versionNumber = Integer.parseInt(version);

			if (versionNumber > 8) {

				Class c = Class.forName("java.lang.ProcessHandle");

				Method m = c.getDeclaredMethod("allProcesses", null);

				Object o = m.invoke(null, null);

				Stream<?> handles = (Stream<?>) o;

				List<?> list = handles.collect(Collectors.toList());

				for (Object lo : list) {

					Class<?> clazz = lo.getClass();

					Method pidMethod = clazz.getDeclaredMethod("pid");
					
					pidMethod.setAccessible(true);

					Method infoMethod = clazz.getDeclaredMethod("info");
					
					infoMethod.setAccessible(true);

					long pid = (long) pidMethod.invoke(lo);

					String info = (String) String.valueOf(infoMethod.invoke(lo));

					javaProcesses.add(new JavaProcess((int) pid, info));
				}

			} else {

				Thread thread = Thread.currentThread();

				ClassLoader cl = thread.getContextClassLoader();

				ClassLoader toolsClassloader = null;

				try {
					toolsClassloader = _getToolsClassLoader(cl);

					if (toolsClassloader != null) {
						thread.setContextClassLoader(toolsClassloader);

						_log(logger, "Trying to load VirtualMachine class...");

						Class<?> vmClass = toolsClassloader.loadClass("com.sun.tools.attach.VirtualMachine");

						Method listMethod = vmClass.getMethod("list");

						List<Object> vmds = (List<Object>) listMethod.invoke(null);

						_log(logger, "Found " + vmds.size() + " vms on this machine.");

						for (Object vmd : vmds) {
							Class<?> vmdClass = toolsClassloader
									.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");

							Method displayNameMethod = vmdClass.getMethod("displayName");

							String displayName = (String) displayNameMethod.invoke(vmd);

							Method idMethod = vmdClass.getMethod("id");

							String id = (String) idMethod.invoke(vmd);

							_log(logger,
									"Found vm id of " + id + " with name " + displayName + ". Trying to attach...");

							javaProcesses.add(new JavaProcess(Integer.parseInt(id), displayName));
						}
					}
				}

				catch (Exception e) {
					e.printStackTrace();
				} finally {
					thread.setContextClassLoader(cl);

					// try to get custom classloader to unload native libs

					try {
						if (toolsClassloader != null) {
							Field nl = ClassLoader.class.getDeclaredField("nativeLibraries");

							nl.setAccessible(true);

							Vector<?> nativeLibs = (Vector<?>) nl.get(toolsClassloader);

							for (Object nativeLib : nativeLibs) {
								Class<?> clazz = nativeLib.getClass();

								Field nameField = clazz.getDeclaredField("name");

								nameField.setAccessible(true);

								String name = (String) nameField.get(nativeLib);

								File nativeLibFile = new File(name);

								String nativeLibFileName = nativeLibFile.getName();

								if (nativeLibFileName.contains("attach")) {
									Method f = clazz.getDeclaredMethod("finalize");

									f.setAccessible(true);
									f.invoke(nativeLib);
								}
							}
						}
					} catch (Exception e) {
						throw e;
					}
				}
			}
			return javaProcesses;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static void main(String[] args) {
		Collection<JavaProcess> processes = list(Optional.of(msg -> System.out.println(msg)));

		Predicate<JavaProcess> tomcatFilter = process -> {
			String displayName = process.getDisplayName();

			return displayName.contains("org.apache.catalina.startup.Bootstrap");
		};

		Stream<JavaProcess> stream = processes.stream();

		Optional<JavaProcess> tomcatProcess = stream.filter(
			tomcatFilter
		).findAny();

		System.out.println(
			"tomcatProcess = " +
				tomcatProcess.get(
				).getId());
	}

	public static int maxProcessId() {
		Stream<JavaProcess> listStream = list().stream();

		return listStream.mapToInt(
			JavaProcess::getId
		).max(
		).getAsInt();
	}

	private static File _findJdkJar(String jar) throws IOException {
		File retval = null;

		final String jarPath = File.separator + "lib" + File.separator + jar;
		final String javaHome = System.getProperty("java.home");

		File jarFile = new File(javaHome + jarPath);

		if (jarFile.exists()) {
			retval = jarFile;
		}
		else {
			jarFile = new File(javaHome + "/.." + jarPath);

			if (jarFile.exists()) {
				retval = jarFile.getCanonicalFile();
			}
		}

		return retval;
	}

	private static ClassLoader _getToolsClassLoader(ClassLoader parent) throws IOException {
		File toolsJar = _findJdkJar("tools.jar");

		if ((toolsJar != null) && toolsJar.exists()) {
			URL toolsUrl = null;

			try {
				URI toolsURI = toolsJar.toURI();

				toolsUrl = toolsURI.toURL();
			}
			catch (MalformedURLException murle) {
			}

			URL[] urls = {toolsUrl};

			return new URLClassLoader(urls, parent);
		}

		throw new IOException("Could not find tools.jar in JDK at this location: " + toolsJar);
	}

	private static void _log(Optional<Consumer<String>> logger, String msg) {
		if (logger.isPresent()) {
			Consumer<String> consumer = logger.get();

			consumer.accept(msg);
		}
	}

}