package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.title=RepositoryLocalServiceUtil changes",
		"problem.section=#removed-methods-getgrouplocalrepositoryimpl-and-getlocalrepositoryimpl-from-repositorylocalservice-and-repositoryservice",
		"problem.summary=Removed Methods getGroupLocalRepositoryImpl and getLocalRepositoryImpl from RepositoryLocalService and RepositoryService",
		"problem.tickets=LPS-55566",
	},
	service = FileMigrator.class
)
public class RepositoryServiceUtilGetRepositoryImplInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findMethodInvocations(null,
				"RepositoryLocalServiceUtil", "getRepositoryImpl", null);
	}

}
