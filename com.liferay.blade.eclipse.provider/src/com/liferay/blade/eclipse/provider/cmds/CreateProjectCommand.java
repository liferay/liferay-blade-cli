package com.liferay.blade.eclipse.provider.cmds;

import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;
import com.liferay.blade.api.ProjectBuild;
import com.liferay.blade.api.ProjectType;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.felix.service.command.CommandProcessor;
import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		CommandProcessor.COMMAND_SCOPE + "=blade",
		CommandProcessor.COMMAND_FUNCTION + "=createProject"
	},
	service = Command.class
)
public class CreateProjectCommand implements Command {

	private static final List<String> textExtensions =
		Arrays.asList(
			".bnd", ".java", ".project", ".xml", ".jsp", ".css", ".jspf",
			".js", ".properties", ".gradle");

	public Object createProject(
			File base, File dir, String typeValue, String buildValue,
			String name, String classname, String service, String packageName)
		throws Exception {

		final ProjectType type;

		if (typeValue == null) {
			type = ProjectType.portlet;
		}
		else {
			type = ProjectType.valueOf(typeValue);
		}

		File workDir = null;

		if (dir != null) {
			workDir = Processor.getFile(dir, name);
			name = workDir.getName();
			base = workDir.getParentFile();
		}
		else {
			workDir = Processor.getFile(base, name);
			name = workDir.getName();
			base = workDir.getParentFile();
		}

		workDir.mkdirs();

		final InputStream in = getClass().getResourceAsStream("/templates.zip");

		if (in == null) {
			return "Cannot find templates";
		}

		final ProjectBuild build;

		if (buildValue == null) {
			build = ProjectBuild.gradle;
		}
		else {
			build = ProjectBuild.valueOf(buildValue);
		}

		final Pattern glob = Pattern.compile("^" +
			build.toString() + "/" + type + "/.*|\\...+/.*");

		final Map<String, String> subs = new HashMap<>();
		subs.put("templates/" + build + "/" + type + "/", "");
		subs.put("_name_", name.toLowerCase());
		subs.put("_NAME_", WordUtils.capitalize(name));
		subs.put("_package_path_", name.replaceAll("\\.", "/"));
		subs.put("_package_", name.toLowerCase().replaceAll("-", "."));

		if (classname == null) {
			classname = WordUtils.capitalize(name);
		}

		if (ProjectType.service.equals(type)) {
			if (service.isEmpty()) {
				return
					"if type is service, the fully qualified name of " +
					"service must be specified after the service argument.";
			}

			subs.put("_SERVICE_FULL_", service);
			subs.put(
				"_SERVICE_SHORT_",
				service.substring(service.lastIndexOf('.') + 1));
		}

		if (ProjectType.servicewrapper.equals(type)) {
			if (service.isEmpty()) {
				return
					"if type is service, the fully qualified name of service " +
					"must be specified after the service argument.";
			}

			subs.put("_SERVICE_FULL_", service);
			subs.put(
				"_SERVICE_SHORT_",
				service.substring(service.lastIndexOf('.') + 1));
		}
		
		if (ProjectType.servicebuilder.equals(type)) {
			if (packageName.isEmpty()) {
				return
					"if type is servicebuilder, the name of the root package" +
					"within which to create service builder classes must be" +
					"specified.";
			}

			subs.put("_pkg_", packageName);
			subs.put("_api_", packageName + ".api");
			subs.put("_svc_", packageName + ".svc");
			subs.put("_web_", packageName + ".web");
			subs.put("_portlet_", packageName + ".portlet");
			subs.put("_portletpkg_", packageName.replaceAll("\\.", "/") + "/portlet");
			
			if (!classname.contains("Portlet")) {
				classname += "Portlet";
			}
		}

		else if (ProjectType.portlet.equals(type) || ProjectType.jspportlet.equals(type)) {
			if (!classname.contains("Portlet")) {
				classname += "Portlet";
			}
		}

		subs.put("_CLASSNAME_", classname);

		String unNormalizedPortletFqn =
				name.toLowerCase().replaceAll("-", ".") + "_" + classname;

		subs.put(
			"_portlet_fqn_",
			unNormalizedPortletFqn.replaceAll("\\.", "_"));

		copy(build, type, workDir, in, glob, true, subs);

		return null;
	}

	private void copy(ProjectBuild build, ProjectType type,
		File workspaceDir, InputStream in, Pattern glob, boolean overwrite,
		Map<String, String> subs) throws Exception {

		Jar jar = new Jar("dot", in);

		try {
			for (Entry<String, Resource> e : jar.getResources().entrySet()) {
				String path = e.getKey();
				
				if (glob != null && !glob.matcher(path).matches())
					continue;

				Resource r = e.getValue();

				for (String key : subs.keySet()) {
					path = path.replaceAll(key, subs.get(key));
				}

				path =
					path.replaceAll(build.name() + "/" + type.name() + "/", "");

				File dest = Processor.getFile(workspaceDir, path);

				if (overwrite ||
					dest.lastModified() < r.lastModified() ||
					r.lastModified() <= 0) {

					File dp = dest.getParentFile();

					if (!dp.exists() && !dp.mkdirs()) {
						throw new CommandException(
							"Could not create directory " + dp);
					}

					IO.copy(r.openInputStream(), dest);

					if (isTextFile(dest)) {
						process(dest, subs);
					}

				}
			}
		}
		finally {
			jar.close();
		}
	}

	private void process(File dest, Map<String, String> subs) throws Exception {
		String content = new String(IO.read(dest));
		String newContent = content;

		for (String sub : subs.keySet()) {
			newContent = newContent.replaceAll(sub, subs.get(sub));
		}

		if (!content.equals(newContent)) {
			IO.write(newContent.getBytes(), dest);
		}
	}

	private boolean isTextFile(File dest) {
		String name = dest.getName();

		return textExtensions.contains(
			name.substring(name.lastIndexOf("."), name.length()));
	}

	@Override
	public Object execute(Map<String, ?> parameters) throws CommandException {
		final File base = (File) parameters.get("base");
		final File dir = (File) parameters.get("dir");
		final String typeValue = (String) parameters.get("typeValue");
		final String buildValue = (String) parameters.get("buildValue");
		final String name = (String) parameters.get("name");
		final String classname = (String) parameters.get("classname");
		final String service = (String) parameters.get("service");
		final String packageName = (String) parameters.get("packageName");

		try {
			return createProject(
				base, dir, typeValue, buildValue, name, classname, service, packageName);
		} catch (Exception e) {
			throw new CommandException("Error creating project.", e);
		}
	}

}