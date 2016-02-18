package com.liferay.blade.gradle.plugin;

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.blade.gradle.model.DefaultModel;

import java.io.File;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.tooling.provider.model.ToolingModelBuilder;
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry;

/**
 * @author Gregory Amerson
 */
public class CustomModelPlugin implements Plugin<Project> {

	@Inject
	public CustomModelPlugin(ToolingModelBuilderRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void apply(Project project) {
		registry.register(new CustomModelBuilder());
	}

	private final ToolingModelBuilderRegistry registry;

	private static class CustomModelBuilder implements ToolingModelBuilder {

		@Override
		public Object buildAll(String modelName, Project project) {
			Set<String> pluginClassNames = new HashSet<>();

			for (Plugin<?> plugin : project.getPlugins()) {
				Class<?> clazz = plugin.getClass();

				pluginClassNames.add(clazz.getName());
			}

			Set<Task> jarTasks = project.getTasksByName("jar", true);

			Set<Task> buildTasks = project.getTasksByName("build", true);

			Set<File> outputFiles = new HashSet<>();

			for (Task task : jarTasks) {
				outputFiles.addAll(task.getOutputs().getFiles().getFiles());
			}

			for (Task task : buildTasks) {
				outputFiles.addAll(task.getOutputs().getFiles().getFiles());
			}

			return new DefaultModel(pluginClassNames, outputFiles);
		}

		@Override
		public boolean canBuild(String modelName) {
			return modelName.equals(CustomModel.class.getName());
		}

	}

}