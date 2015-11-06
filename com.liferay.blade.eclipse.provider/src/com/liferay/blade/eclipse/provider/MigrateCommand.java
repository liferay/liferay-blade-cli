package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.ProgressMonitor;
import com.liferay.blade.util.NullProgressMonitor;

import java.io.File;
import java.util.List;

import org.apache.felix.service.command.CommandProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		CommandProcessor.COMMAND_SCOPE + "=lfr",
		CommandProcessor.COMMAND_FUNCTION + "=migrate"
	},
	service = Object.class
)
public class MigrateCommand {

	public void migrate(File projectDir) {
		List<Problem> problems = projectMigrationService.findProblems(projectDir, npm);
		projectMigrationService.reportProblems(problems, Migration.DETAIL_LONG, "console");
	}

	public void migrate(File projectDir, String format, File outputFile) {
		List<Problem> problems = projectMigrationService.findProblems(projectDir, npm);
		projectMigrationService.reportProblems(problems, Migration.DETAIL_LONG, format, outputFile);
	}

	@Reference
	public void setProjectMigration(Migration projectMigration) {
		this.projectMigrationService = projectMigration;
	}

	private volatile Migration projectMigrationService;
	private final ProgressMonitor npm = new NullProgressMonitor();

}