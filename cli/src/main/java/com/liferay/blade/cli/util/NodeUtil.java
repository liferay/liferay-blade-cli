package com.liferay.blade.cli.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;

public class NodeUtil {

    private static String _nodeVersion = "8.4.0";

    public static Path downloadNode() throws IOException {
        File userHome =  new File(System.getProperty("user.home"));

        Path userHomePath = userHome.toPath();

        Path bladeCachePath = userHomePath.resolve(".blade" + File.separator + "cache");

        Path nodeCachePath = bladeCachePath.resolve("node");

        if (!Files.exists(nodeCachePath)) {
            Files.createDirectories(nodeCachePath);

            String nodeURL = getNodeURL();
            
            System.out.println("Node URL: " + nodeURL);

            Path downloadPath = bladeCachePath.resolve(
                    nodeURL.substring(nodeURL.lastIndexOf("/") + 1));

            if (!downloadPath.toFile().exists()) {
                BladeUtil.downloadLink(nodeURL, downloadPath);
            }

            FileUtil.unpack(downloadPath, nodeCachePath, 1);
            if (OSDetector.isWindows()) {
            	Path nodePath = Files.list(nodeCachePath).findFirst().get();
            	
            	Files.list(nodePath).forEach(x -> {
					try {
						Files.move(x, nodeCachePath.resolve(x.getFileName()), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
            	
            	Files.delete(nodePath);
            }
            else {
	            Files.setPosixFilePermissions(nodeCachePath.resolve("bin/node"), PosixFilePermissions.fromString("rwxrwxr--"));
	            Files.setPosixFilePermissions(nodeCachePath.resolve("bin/npm"), PosixFilePermissions.fromString("rwxrwxr--"));
            }
        }

        return nodeCachePath;
    }

    public static Path downloadYo() throws Exception {
        File userHome =  new File(System.getProperty("user.home"));

        Path userHomePath = userHome.toPath();

        Path bladeCachePath = userHomePath.resolve(".blade" + File.separator + "cache");

        Path nodeDirPath = bladeCachePath.resolve("node");

        Path yoDirPath = bladeCachePath.resolve("yo");

        Path nodeModulesDirPath = yoDirPath.resolve("node_modules");

        if (!Files.exists(nodeModulesDirPath)) {
            Files.createDirectories(yoDirPath);

            InputStream inputStream = NodeUtil.class.getResourceAsStream(
                "dependencies" + File.separator + "package.json");

            Path targetPath = yoDirPath.resolve("package.json");

            Files.copy(
                inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

            File npmDir = getNpmDir(nodeDirPath.toFile());

            Process process;
            if (OSDetector.isWindows()) {
               	process = BladeUtil.startProcess(
            			nodeDirPath.toString() + File.separator + "node.exe " + npmDir + File.separator + "bin" + File.separator + "npm-cli.js install --scripts-prepend-node-path", yoDirPath.toFile());
             	
            }
            else {
            	
            	process = BladeUtil.startProcess(
            			nodeDirPath.toString() + File.separator + "bin" + File.separator + "node " + npmDir + File.separator + "bin" + File.separator + "npm-cli.js install", yoDirPath.toFile());
            }

            int returnCode = process.waitFor();
            
            if (returnCode != 0) {
            	throw new RuntimeException("Problem occurred while downloading yo");
            }
        }

        return yoDirPath;
    }

    public static File getNpmDir(File nodeDir) {
        File nodeModulesDir = new File(nodeDir, "node_modules");

        if (!nodeModulesDir.exists()) {
            nodeModulesDir = new File(nodeDir, "lib" + File.separator + "node_modules");
        }

        return new File(nodeModulesDir, "npm");
    }

    public static String getNodeVersion() {
        return _nodeVersion;
    }

    public static String getNodeURL() {
        String nodeVersion = getNodeVersion();

        if (nodeVersion == null || nodeVersion.equals("")) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("http://nodejs.org/dist/v");
        sb.append(nodeVersion);
        sb.append("/node-v");
        sb.append(nodeVersion);
        sb.append('-');

        String os = "linux";

        if (OSDetector.isApple()) {
            os = "darwin";
        }
        else if (OSDetector.isWindows()) {
            os = "win";
        }

        sb.append(os);
        sb.append("-x");

        String bitmode = OSDetector.getBitmode();

        if (bitmode.equals("32")) {
            bitmode = "86";
        }

        sb.append(bitmode);

        if (OSDetector.isWindows()) {
            sb.append(".zip");
        }
        else {
            sb.append(".tar.gz");
        }

        return sb.toString();
    }
}
