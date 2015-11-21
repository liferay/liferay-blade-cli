
package com.liferay.blade.cli.cmds;

import aQute.bnd.osgi.Processor;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.ProjectBuild;
import com.liferay.blade.api.ProjectTemplate;
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

	final private blade _blade;
	final private CreateOptions _options;
	final private BundleContext _bundleContext =
		FrameworkUtil.getBundle(CreateCommand.class).getBundleContext();

	public CreateCommand(blade blade, CreateOptions options) throws Exception {
		_blade = blade;
		_options = options;

		List<String> args = options._arguments();

		if (args.size() < 2) {
			// TODO print out help for what project templates there are
			printHelp();
			return;
		}

		final Collection<ServiceReference<ProjectTemplate>> refs =
			_bundleContext.getServiceReferences(ProjectTemplate.class, null);

		final String projectTemplateName = args.remove(0);
		ProjectTemplate template = null;

		if (refs != null) {
			for (ServiceReference<ProjectTemplate> ref : refs) {
				String name = (String) ref.getProperty("name");

				if (projectTemplateName.equals(name)) {
					template = _bundleContext.getService(ref);
					break;
				}
			}
		}

		if (template == null) {
			_blade.error(
					"Unable to get project template " + projectTemplateName);
			return;
		}

		ProjectBuild build = _options.build();

		if (build == null) {
			build = ProjectBuild.gradle;
		}

		File dir = _options.dir();
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

		final ServiceReference<Command> ref =
			_bundleContext.getServiceReferences(
				Command.class, "(osgi.command.function=createProject)").iterator().next();

		final Command command = _bundleContext.getService(ref);
		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("workDir", workDir);
		parameters.put("projectTemplate", template);
		parameters.put("buildValue", build.toString());
		parameters.put("name", name);
		parameters.put("classname", options.classname());
		parameters.put("service", options.service());
		parameters.put("packageName", options.packagename());

		final Object errors = command.execute(parameters);

		if (errors != null) {
			_blade.error(errors.toString());

			if ("printHelp".equals(errors.toString())) {
				printHelp();
			}
		}
	}

	private void printHelp() throws Exception {
		Formatter f = new Formatter();
		_options._command().help(f, this);
		_blade.out().println(f);
		f.close();
	}

}
