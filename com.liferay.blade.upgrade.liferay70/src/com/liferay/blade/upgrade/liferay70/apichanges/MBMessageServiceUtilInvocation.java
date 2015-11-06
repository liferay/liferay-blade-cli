
package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

@Component(
	property = {
		"file.extensions=java",
		"problem.title=MBMessageService API Changes",
		"problem.section=#removed-permissionclassname-permissionclasspk-and-permissionowner-parameters-from-mbmessage-api",
		"problem.summary=Removed permissionClassName, permissionClassPK, and permissionOwner Parameters from MBMessage API",
		"problem.tickets=LPS-55877",
	},
	service = FileMigrator.class
)
public class MBMessageServiceUtilInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
	    final List<SearchResult> result = new ArrayList<SearchResult>();

        result.addAll( javaFileChecker.findMethodInvocations(null,
            "MBMessageServiceUtil", "addDiscussionMessage", null) ) ;

        result.addAll( javaFileChecker.findMethodInvocations(null,
            "MBMessageServiceUtil", "deleteDiscussionMessage", null) ) ;

        result.addAll( javaFileChecker.findMethodInvocations(null,
            "MBMessageServiceUtil", "updateDiscussionMessage", null) ) ;

		return result;
	}

}
