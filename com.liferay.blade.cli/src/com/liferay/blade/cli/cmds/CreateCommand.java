
package com.liferay.blade.cli.cmds;

import aQute.bnd.osgi.Processor;
import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.ProjectBuild;
import com.liferay.blade.api.ProjectType;
import com.liferay.blade.cli.CreateOptions;
import com.liferay.blade.cli.blade;

import java.io.File;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class CreateCommand {

	final private blade blade;
	final private CreateOptions options;
	final private BundleContext bundleContext =
		FrameworkUtil.getBundle(CreateCommand.class).getBundleContext();

	public CreateCommand(blade lfr, CreateOptions options) throws Exception {
		this.blade = lfr;
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

		createFromTemplate(ProjectType.portlet, name, classname, "", "");
	}

	@Arguments(arg = {"name"})
	interface JSPPortletOptions extends Options {
	}

	@Description(value = "Use mvcportlet with jsps template for new project")
	public void _jspportlet(JSPPortletOptions opts) throws Exception {
		List<String> args = opts._arguments();
		String name = args.get(0);

		createFromTemplate(ProjectType.jspportlet, name, null, "", "");
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
		createFromTemplate(ProjectType.service, name, classname, service, "");
	}

	@Arguments(arg = {"name", "[packageName]"})
	interface ServiceBuilderOptions extends ServiceOptions {

	}

	@Description(value = "Creates a service builder project with three modules using a multi-project build configuration")
	public void _servicebuilder(ServiceBuilderOptions opts) throws Exception {
		List<String> args = opts._arguments();
		String name =  args.get(0);
		String packageName = args.get(1);

		createFromTemplate(ProjectType.servicebuilder, name, null, "", packageName);
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

		createFromTemplate(ProjectType.servicewrapper, name, classname, service, "");
	}

	private void createFromTemplate(
			ProjectType type, String name, String classname, String service, String packageName)
		throws Exception {

		File base = blade.getBase();

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

		ProjectBuild build = options.build();

		if (build == null) {
			build = ProjectBuild.gradle;
		}

		final Collection<ServiceReference<Command>> refs =
			bundleContext.getServiceReferences(
				Command.class, "(osgi.command.function=createProject)");

		if (refs != null && refs.size() > 0) {
			final Command command =
				bundleContext.getService(refs.iterator().next());

			final Map<String, Object> parameters = new HashMap<>();

			parameters.put("base", base);
			parameters.put("dir", dir);
			parameters.put("typeValue", type.toString());
			parameters.put("buildValue", build.toString());
			parameters.put("name", name);
			parameters.put("classname", classname);
			parameters.put("service", service);
			parameters.put("packageName", packageName);

			final Object errors = command.execute(parameters);

			if (errors != null) {
				blade.error(errors.toString());

				if ("printHelp".equals(errors.toString())) {
					printHelp();
				}
			}
		}
		else {
			blade.error("Unable to obtain createProject command");
		}
	}

	private void printHelp() throws Exception {
		Formatter f = new Formatter();
		options._command().help(f, this);
		blade.out().println(f);
		f.close();
	}

}
