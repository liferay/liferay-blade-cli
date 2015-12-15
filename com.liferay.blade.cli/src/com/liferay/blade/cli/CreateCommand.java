package com.liferay.blade.cli;

import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

public class CreateCommand {

	private static final List<String> textExtensions =
		Arrays.asList(
			".bnd", ".java", ".project", ".xml", ".jsp", ".css", ".jspf", ".js",
			".properties", ".gradle", ".prefs");

	final private blade _blade;

	public CreateCommand(blade blade, CreateOptions options) throws Exception {
		_blade = blade;

		List<String> args = options._arguments();

		Template template = options.template();

		if (template == null) {
			template = Template.jspportlet;
		}

		File dir = options.dir();
		File base = _blade.getBase();
		String name = args.remove(0);
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

		String type = "standalone";

		final Pattern glob = Pattern.compile(
			"^" + type + "/" + template.name() + "/.*|\\...+/.*");

		final Map<String, String> subs = new HashMap<>();
		subs.put("templates/" + type + "/" + template.name() + "/", "");
		subs.put("_project_path_", workDir.getAbsolutePath());
		subs.put("_name_", name.toLowerCase());
		subs.put("_NAME_", WordUtils.capitalize(name));
		subs.put("_package_path_", name.replaceAll("\\.", "/"));
		subs.put("_package_", name.toLowerCase().replaceAll("-", "."));

		String classname = options.classname();

		if (isEmpty(classname)) {
			classname = WordUtils.capitalize(name);
		}

		String service = options.service();

		if ("service".equals(template.name())) {
			if (isEmpty(service)) {
				addError("Create", "if type is service, the fully qualified name of " +
					"service must be specified after the service argument.");
				return;
			}

			subs.put("_SERVICE_FULL_", service);
			subs.put(
				"_SERVICE_SHORT_",
				service.substring(service.lastIndexOf('.') + 1));
		}

		if ("servicewrapper".equals(template.name())) {
			if (isEmpty(service)) {
				addError("Create",
					"if type is service, the fully qualified name of service " +
					"must be specified after the service argument.");
				return;
			}

			subs.put("_SERVICE_FULL_", service);
			subs.put(
				"_SERVICE_SHORT_",
				service.substring(service.lastIndexOf('.') + 1));
		}

		String packageName = options.packagename();

		if ("servicebuilder".equals(template.name())) {
			if (isEmpty(packageName)) {
				addError("Create",
					"if type is servicebuilder, the name of the root package " +
					"within which to create service builder classes must be " +
					"specified.");
				return;
			}

			subs.put("_package_", packageName);
			subs.put("_api_", packageName + ".api");
			subs.put("_svc_", packageName + ".svc");
			subs.put("_web_", packageName + ".web");
			subs.put("_portlet_", packageName + ".portlet");
			subs.put("_portletpackage_", packageName.replaceAll("\\.", "/") + "/portlet");

			if (!classname.contains("Portlet")) {
				classname += "Portlet";
			}
		}
		else if ("portlet".equals(template.name()) || "jspportlet".equals(template.name())) {
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

		InputStream in = getClass().getResourceAsStream("/templates.zip");

		copy(type, template.name(), workDir, in, glob, true, subs);
	}

	private void copy(String type, String template, File workspaceDir,
		InputStream in, Pattern glob, boolean overwrite,
		Map<String, String> subs) throws Exception {

		try (Jar jar = new Jar("dot", in)) {
			for (Entry<String, Resource> e : jar.getResources().entrySet()) {
				String path = e.getKey();

				if (glob != null && !glob.matcher(path).matches())
					continue;

				Resource r = e.getValue();

				for (String key : subs.keySet()) {
					path = path.replaceAll(key, subs.get(key));
				}

				path =
					path.replaceAll(type + "/" + template + "/", "");

				File dest = Processor.getFile(workspaceDir, path);

				if (overwrite ||
					dest.lastModified() < r.lastModified() ||
					r.lastModified() <= 0) {

					File dp = dest.getParentFile();

					if (!dp.exists() && !dp.mkdirs()) {
						throw new Exception(
							"Could not create directory " + dp);
					}

					IO.copy(r.openInputStream(), dest);

					if (isTextFile(dest)) {
						process(dest, subs);
					}
				}
			}
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

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private boolean isEmpty(String str) {
	    if(str == null)
	        return true;

	    if(str.trim().isEmpty())
	        return true;

	    return false;
	}
}
