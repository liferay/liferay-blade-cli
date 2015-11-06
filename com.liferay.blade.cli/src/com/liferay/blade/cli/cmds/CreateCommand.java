
package com.liferay.blade.cli.cmds;

import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

import com.liferay.blade.cli.CreateOptions;
import com.liferay.blade.cli.blade;

public class CreateCommand {

	final private blade lfr;
	final private CreateOptions options;
	final static private List<String> textExtensions =
		Arrays.asList(
			".bnd", ".java", ".project", ".xml", ".jsp", ".css", ".jspf",
			".js", ".properties");

	public CreateCommand(blade lfr, CreateOptions options) throws Exception {
		this.lfr = lfr;
		this.options = options;

		String help = options._command().subCmd(options, this);

		if (help != null) {
			System.out.println(help);
		}
	}

	@Arguments(arg = {"name"})
	interface PortletOptions extends Options {
		@Description("If a class is generated in the project, " +
				"provide the name of the class to be generated." +
				" If not provided defaults to Project name.")
		public String classname();

	}

	@Description(value = "Use basic portlet template for new project")
	public void _portlet(PortletOptions opts) throws Exception {
		List<String> args = opts._arguments();
		String name = args.get(0);
		String classname = opts.classname();

		createFromTemplate(Type.portlet, name, classname, "");
	}

	@Arguments(arg = {"name"})
	interface JSPPortletOptions extends Options {
	}

	@Description(value = "Use mvcportlet with jsps template for new project")
	public void _jspportlet(JSPPortletOptions opts) throws Exception {
		List<String> args = opts._arguments();
		String name = args.get(0);

		createFromTemplate(Type.jspportlet, name, null, "");
	}

	@Arguments(arg = {"name", "[service]"})
	interface ServiceOptions extends Options {
		@Description("If a class is generated in the project, " +
				"provide the name of the class to be generated." +
				" If not provided defaults to Project name.")
		public String classname();
	}

	@Description(value = "Creates a project with a single service component")
	public void _service(ServiceOptions opts) throws Exception {
		String classname = opts.classname();
		List<String> args = opts._arguments();
		String name =  args.get(0);
		String service = args.get(1);
		createFromTemplate(Type.service, name, classname, service);
	}

	@Arguments(arg = {"name", "[service]"})
	interface ServiceWrapperOptions extends ServiceOptions {

	}

	@Description(value = "Creates a project with a single service wrapper component")
	public void _servicewrapper(ServiceWrapperOptions opts) throws Exception {
		String classname = opts.classname();
		List<String> args = opts._arguments();
		String name =  args.get(0);
		String service = args.get(1);

		createFromTemplate(Type.servicewrapper, name, classname, service);
	}

	private void createFromTemplate(Type type, String name, String classname, String service) throws Exception {
		File base = lfr.getBase();

		File dir = options.dir();

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

		InputStream in = getClass().getResourceAsStream("/templates.zip");

		if (in == null) {
			lfr.error(
				"Cannot find templates in this jar %s", "/templates.zip");
			return;
		}

		Build build = options.build();

		if (build == null) {
			build = Build.gradle;
		}

		Pattern glob = Pattern.compile("^" +
			build.toString() + "/" + type + "/.*|\\...+/.*");

		Map<String, String> subs = new HashMap<>();
		subs.put("templates/" + build + "/" + type + "/", "");
		subs.put("_name_", name.toLowerCase());
		subs.put("_NAME_", WordUtils.capitalize(name));
		subs.put("_package_path_", name.replaceAll("\\.", "/"));
		subs.put("_package_", name.toLowerCase().replaceAll("-", "."));

		if (classname == null) {
			classname = WordUtils.capitalize(name);
		}

		if (Type.service.equals(type)) {
			if (service.isEmpty()) {
				lfr.error(
					"if type is service, the fully qualified name of service " +
						"must be specified after the service argument.");
				printHelp();
				return;
			}

			subs.put("_SERVICE_FULL_", service);
			subs.put(
				"_SERVICE_SHORT_",
				service.substring(service.lastIndexOf('.') + 1));
		}

		if (Type.servicewrapper.equals(type)) {
			if (service.isEmpty()) {
				lfr.error(
					"if type is service, the fully qualified name of service " +
						"must be specified after the service argument.");
				printHelp();
				return;
			}

			subs.put("_SERVICE_FULL_", service);
			subs.put(
				"_SERVICE_SHORT_",
				service.substring(service.lastIndexOf('.') + 1));
		}

		else if (Type.portlet.equals(type) || Type.jspportlet.equals(type)) {

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
	}

	private void copy(Build build, Type type,
		File workspaceDir, InputStream in, Pattern glob, boolean overwrite,
		Map<String, String> subs) throws Exception {

		lfr.trace("Glob:" + glob);

		Jar jar = new Jar("dot", in);

		try {
			for (Entry<String, Resource> e : jar.getResources().entrySet()) {
				String path = e.getKey();

				lfr.trace(
					"path %s matches ? %s : \n", path,
					!((glob != null && !glob.matcher(path).matches())));

				if (glob != null && !glob.matcher(path).matches())
					continue;

				Resource r = e.getValue();

				for (String key : subs.keySet()) {
					path = path.replaceAll(key, subs.get(key));
				}

				path =
					path.replaceAll(build.name() + "/" + type.name() + "/", "");

				File dest = Processor.getFile(workspaceDir, path);

				lfr.trace("Dest Dir:" + path);

				if (overwrite ||
					dest.lastModified() < r.lastModified() ||
					r.lastModified() <= 0) {

					lfr.trace("copy %s to %s \n", path, dest);

					File dp = dest.getParentFile();
					if (!dp.exists() && !dp.mkdirs()) {
						throw new IOException(
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

	private void printHelp() throws Exception {
		Formatter f = new Formatter();
		options._command().help(f, this);
		lfr.out().println(f);
		f.close();
	}

}
