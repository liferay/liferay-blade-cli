package com.liferay.blade.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.powermock.reflect.Whitebox;

import com.liferay.blade.cli.Extensions;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.WorkspaceMetadata;

public class HelloMavenTest {
	
	@Before
	public void setUp() throws Exception {
		
		temporaryFolder.newFolder(".blade", "extensions");

		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");
		
		Whitebox.setInternalState(Extensions.class, "_USER_HOME_DIR", temporaryFolder.getRoot());
		
	}

	
	@Test
	public void testMavenInitWithNameWorkspaceDirectoryEmpty() throws Exception {
		_setupTestExtensions();
		
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f", "-b", "maven", "newproject"};

		File newproject = new File(_workspaceDir, "newproject");

		Assert.assertTrue(newproject.mkdirs());

		new BladeTest().run(args);

		Assert.assertTrue(new File(newproject, "pom.xml").exists());

		Assert.assertTrue(new File(newproject, "modules").exists());

		String contents = new String(Files.readAllBytes(new File(newproject, "pom.xml").toPath()));

		Assert.assertTrue(contents, contents.contains("3.2.1"));

		File metadataFile = new File(_workspaceDir, "blade.properties");

		Assert.assertTrue(metadataFile.exists());

		WorkspaceMetadata metadata = BladeUtil.getWorkspaceMetadata(_workspaceDir);

		Assert.assertEquals("maven", metadata.getProfileName());
		
		args = new String[] {"--base", _workspaceDir.getPath(), "hello", "--name", "foobar"};
		
		String content = TestUtil.runBlade(args);
		
		Assert.assertTrue(content.contains("maven"));

	}
	
	private void _setupTestExtensions() throws Exception {
		File extensionsDir = new File(temporaryFolder.getRoot(), ".blade/extensions");

		extensionsDir.mkdirs();

		Assert.assertTrue("Unable to create test extensions dir.", extensionsDir.exists());

		Path extensionsPath = extensionsDir.toPath();

		_setupTestExtension(extensionsPath, System.getProperty("sampleCommandJarFile"));
	}
	
	private static void _setupTestExtension(Path extensionsPath, String jarPath) throws IOException {
		File sampleJarFile = new File(jarPath);

		Assert.assertTrue(sampleJarFile.getAbsolutePath() + " does not exist.", sampleJarFile.exists());

		Path sampleJarPath = extensionsPath.resolve(sampleJarFile.getName());

		Files.copy(sampleJarFile.toPath(), sampleJarPath, StandardCopyOption.REPLACE_EXISTING);

		Assert.assertTrue(Files.exists(sampleJarPath));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _workspaceDir = null;
}
