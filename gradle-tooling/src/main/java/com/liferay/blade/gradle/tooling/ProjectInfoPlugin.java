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

package com.liferay.blade.gradle.tooling;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.PublishArtifactSet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class ProjectInfoPlugin implements Plugin<Project> {

	@Inject
	public ProjectInfoPlugin(ToolingModelBuilderRegistry toolingModelBuilderRegistry) {
		_toolingModelBuilderRegistry = toolingModelBuilderRegistry;
	}

	@Override
	public void apply(Project project) {
		_toolingModelBuilderRegistry.register(new ProjectInfoBuilder());
	}

	private final ToolingModelBuilderRegistry _toolingModelBuilderRegistry;

	private static class ProjectInfoBuilder implements ToolingModelBuilder {

		@Override
		public Object buildAll(String modelName, Project project) {
			Set<String> pluginClassNames = new HashSet<>();

			for (Plugin<?> plugin : project.getPlugins()) {
				Class<?> clazz = plugin.getClass();

				pluginClassNames.add(clazz.getName());
			}

			Set<Task> buildTasks = project.getTasksByName("build", true);

			Set<Task> jarTasks = project.getTasksByName("jar", true);

			Set<Task> tasks = new HashSet<>();

			tasks.addAll(buildTasks);
			tasks.addAll(jarTasks);

			Map<String, Set<File>> projectOutputFiles = new HashMap<>();

			for (Task task : tasks) {
				Project taskProject = task.getProject();

				String projectPath = taskProject.getPath();

				TaskOutputs outputs = task.getOutputs();

				FileCollection fileCollection = outputs.getFiles();

				Set<File> files = fileCollection.getFiles();

				Set<File> outputFiles = projectOutputFiles.computeIfAbsent(projectPath, p -> new HashSet<>());

				outputFiles.addAll(files);
			}

			ConfigurationContainer configurations = project.getConfigurations();

			ExtensionContainer liferayExtensionContainer = project.getExtensions();

			Object liferayExtension = liferayExtensionContainer.findByName("liferay");

			String liferayHome = _getExtensionProperty(liferayExtension, "liferayHome");

			String deployDir =  _getExtensionProperty(liferayExtension, "deployDir");
			
			Project rootProject = project.getRootProject();
			
			ExtensionContainer workspaceExtensionContainer = rootProject.getExtensions();

			Object workspaceExtension = workspaceExtensionContainer.findByName("liferayWorkspace");

			String dockerImageLiferay = _getExtensionProperty(workspaceExtension, "dockerImageLiferay");

			String dockerImageId =  _getExtensionProperty(workspaceExtension, "dockerImageId");

			String dockerContainerId = _getExtensionProperty(workspaceExtension, "dockerContainerId");

			try {
				Configuration archivesConfiguration = configurations.getByName(Dependency.ARCHIVES_CONFIGURATION);

				PublishArtifactSet artifacts = archivesConfiguration.getArtifacts();

				FileCollection fileCollection = artifacts.getFiles();

				Set<File> files = fileCollection.getFiles();

				Set<File> outputFiles = projectOutputFiles.computeIfAbsent(project.getPath(), p -> new HashSet<>());

				outputFiles.addAll(files);
			}
			catch (Exception e) {
			}

			return new DefaultModel(
				pluginClassNames, projectOutputFiles, deployDir, liferayHome, dockerImageLiferay, dockerImageId,
				dockerContainerId);
		}

		@Override
		public boolean canBuild(String modelName) {
			return modelName.equals(ProjectInfo.class.getName());
		}

		private String _getExtensionProperty(Object extension, String property) {
			if (extension != null) {
				Class<?> clazz = extension.getClass();

				try {
					BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

					for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
						String propertyDescriptorName = propertyDescriptor.getName();

						Method method = propertyDescriptor.getReadMethod();

						if ((method != null) && property.equals(propertyDescriptorName)) {
							Object value = method.invoke(extension);

							return String.valueOf(value);
						}
					}
				}
				catch (Exception e) {
				}
			}

			return null;
		}
	}

}