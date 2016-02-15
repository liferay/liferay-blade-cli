package com.liferay.blade.cli.gradle;

import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;

import com.liferay.blade.gradle.model.CustomModel;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map.Entry;
import java.util.Set;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Gregory Amerson
 */
public class GradleTooling {

	private static void copy(InputStream in, File outputDir) throws Exception {

		try (Jar jar = new Jar("dot", in)) {
			for (Entry<String, Resource> e : jar.getResources().entrySet()) {
				String path = e.getKey();

				Resource r = e.getValue();

//				path = path.replaceAll(type + "/" + template + "/", "");

				File dest = Processor.getFile(outputDir, path);

				if ((dest.lastModified() < r.lastModified()) ||
					(r.lastModified() <= 0)) {

					File dp = dest.getParentFile();

					if (!dp.exists() && !dp.mkdirs()) {
						throw new Exception("Could not create directory " + dp);
					}

					IO.copy(r.openInputStream(), dest);
				}
			}
		}
	}

	private static <T> T getModel(
		Class<T> modelClass, File cacheDir, File projectDir ) throws Exception {

		T retval = null;

		final GradleConnector connector = GradleConnector.newConnector();
		connector.forProjectDirectory(projectDir);

		ProjectConnection connection = null;

		try {
			connection = connector.connect();
			ModelBuilder<T> modelBuilder =
				(ModelBuilder<T>) connection.model(modelClass);

			final File depsDir = new File(cacheDir, "deps");

			if (!depsDir.exists()) {
				depsDir.mkdirs();

				InputStream in =
					GradleTooling.class.getResourceAsStream("/deps.zip");

				copy(in, depsDir);
			}

			final String initScriptTemplate =
				IO.collect(
					GradleTooling.class.getResourceAsStream("init.gradle"));

			String path = depsDir.getAbsolutePath();

			path = path.replaceAll("\\\\", "/");

			final String initScriptContents =
				initScriptTemplate.replaceFirst("%deps%", path);

			File scriptFile = Files.createTempFile(
				"blade", "init.gradle").toFile();

			IO.write(initScriptContents.getBytes(), scriptFile);

			modelBuilder.withArguments("--init-script", scriptFile.getAbsolutePath());

			retval = modelBuilder.get();
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		return retval;
	}

	public static Set<File> getOutputFiles(File cacheDir, File buildDir)
		throws Exception {

		final CustomModel model =
			getModel(CustomModel.class, cacheDir, buildDir);

		return model.getOutputFiles();
	}

	public static File findLatestAvailableArtifact(String artifact)
		throws Exception {

		return
			findLatestAvailableArtifact(
				artifact, "http://cdn.repository.liferay.com/nexus/content/groups/public");
	}

	public static File findLatestAvailableArtifact(
			String downloadDep, String repo) throws Exception {

		File projectDir = Files.createTempDirectory("blade").toFile();

		final String buildScriptTemplate =
			IO.collect(
				GradleTooling.class.getResourceAsStream("dep.gradle"));

		String buildScript =
			buildScriptTemplate.replaceFirst("%repo%", repo).replaceFirst(
				"%dep%", downloadDep);

		File buildGradle = new File(projectDir, "build.gradle");

		IO.write(buildScript.getBytes(), buildGradle);

		ProjectConnection connection = null;

		try {
			final GradleConnector connector =
				GradleConnector.newConnector().forProjectDirectory(projectDir);

			connection = connector.connect();

			connection.newBuild().forTasks("copyDep").run();
		}
		finally {
			connection.close();
		}

		return new File(projectDir, "build").listFiles()[0];
	}

}
