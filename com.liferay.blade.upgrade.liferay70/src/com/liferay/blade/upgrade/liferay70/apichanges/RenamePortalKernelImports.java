/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.ImportStatementMigrator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java",
		"problem.summary=The portal-kernel and portal-impl folders have many packages with the same name. Therefore, all of these packages are affected by the split package problem",
		"problem.tickets=LPS-61952",
		"problem.title=Renamed Packages to Fix the Split Packages Problem",
		"problem.section=#renamed-packages-to-fix-the-split-packages-problem",
		"auto.correct=import"
	},
	service = {
		FileMigrator.class,
		AutoMigrator.class
	}
)
public class RenamePortalKernelImports extends ImportStatementMigrator {

	private final static String[] IMPORTS = new String[] {
			"com.liferay.counter",
			"com.liferay.mail.model",
			"com.liferay.mail.service",
			"com.liferay.mail.util",
			"com.liferay.portal.exception",
			"com.liferay.portal.jdbc.pool.metrics",
			"com.liferay.portal.kernel.mail",
			"com.liferay.portal.layoutconfiguration.util",
			"com.liferay.portal.layoutconfiguration.util.xml",
			"com.liferay.portal.mail",
			"com.liferay.portal.model",
			"com.liferay.portal.model.adapter",
			"com.liferay.portal.model.impl",
			"com.liferay.portal.portletfilerepository",
			"com.liferay.portal.repository.proxy",
			"com.liferay.portal.security.auth",
			"com.liferay.portal.security.exportimport",
			"com.liferay.portal.security.ldap",
			"com.liferay.portal.security.membershippolicy",
			"com.liferay.portal.security.permission",
			"com.liferay.portal.security.permission.comparator",
			"com.liferay.portal.security.pwd",
			"com.liferay.portal.security.xml",
			"com.liferay.portal.service.configuration",
			"com.liferay.portal.service.http",
			"com.liferay.portal.service.permission",
			"com.liferay.portal.service.persistence.impl",
			"com.liferay.portal.theme",
			"com.liferay.portal.util",
			"com.liferay.portal.util.comparator",
			"com.liferay.portal.verify.model",
			"com.liferay.portal.webserver",
			"com.liferay.portlet",
			"com.liferay.portlet.admin.util",
			"com.liferay.portlet.announcements",
			"com.liferay.portlet.asset",
			"com.liferay.portlet.backgroundtask.util.comparator",
			"com.liferay.portlet.blogs",
			"com.liferay.portlet.blogs.exception",
			"com.liferay.portlet.blogs.model",
			"com.liferay.portlet.blogs.service",
			"com.liferay.portlet.blogs.service.persistence",
			"com.liferay.portlet.blogs.util.comparator",
			"com.liferay.portlet.documentlibrary",
			"com.liferay.portlet.dynamicdatamapping",
			"com.liferay.portlet.expando",
			"com.liferay.portlet.exportimport",
			"com.liferay.portlet.imagegallerydisplay.display.context",
			"com.liferay.portlet.journal.util",
			"com.liferay.portlet.layoutsadmin.util",
			"com.liferay.portlet.messageboards",
			"com.liferay.portlet.messageboards.constants",
			"com.liferay.portlet.messageboards.exception",
			"com.liferay.portlet.messageboards.model",
			"com.liferay.portlet.messageboards.service",
			"com.liferay.portlet.messageboards.service.persistence",
			"com.liferay.portlet.messageboards.util",
			"com.liferay.portlet.messageboards.util.comparator",
			"com.liferay.portlet.mobiledevicerules",
			"com.liferay.portlet.portletconfiguration.util",
			"com.liferay.portlet.rolesadmin.util",
			"com.liferay.portlet.sites.util",
			"com.liferay.portlet.social",
			"com.liferay.portlet.trash",
			"com.liferay.portlet.useradmin.util",
			"com.liferay.portlet.ratings",
			"com.liferay.portlet.ratings.definition",
			"com.liferay.portlet.ratings.display.context",
			"com.liferay.portlet.ratings.exception",
			"com.liferay.portlet.ratings.model",
			"com.liferay.portlet.ratings.service",
			"com.liferay.portlet.ratings.service.persistence",
			"com.liferay.portlet.ratings.transformer",
			"com.liferay.portal.service"
	};

	private final static String[] IMPORTS_FIXED = new String[] {
			"com.liferay.counter.kernel",
			"com.liferay.mail.kernel.model",
			"com.liferay.mail.kernel.service",
			"com.liferay.mail.kernel.util",
			"com.liferay.portal.kernel.exception",
			"com.liferay.portal.kernel.jdbc.pool.metrics",
			"com.liferay.mail.kernel.model",
			"com.liferay.portal.kernel.layoutconfiguration.util",
			"com.liferay.portal.kernel.layoutconfiguration.util.xml",
			"com.liferay.portal.kernel.mail",
			"com.liferay.portal.kernel.model",
			"com.liferay.portal.kernel.model.adapter",
			"com.liferay.portal.kernel.model.impl",
			"com.liferay.portal.kernel.portletfilerepository",
			"com.liferay.portal.kernel.repository.proxy",
			"com.liferay.portal.kernel.security.auth",
			"com.liferay.portal.kernel.security.exportimport",
			"com.liferay.portal.kernel.security.ldap",
			"com.liferay.portal.kernel.security.membershippolicy",
			"com.liferay.portal.kernel.security.permission",
			"com.liferay.portal.kernel.security.permission.comparator",
			"com.liferay.portal.kernel.security.pwd",
			"com.liferay.portal.kernel.security.xml",
			"com.liferay.portal.kernel.service.configuration",
			"com.liferay.portal.kernel.service.http",
			"com.liferay.portal.kernel.service.permission",
			"com.liferay.portal.kernel.service.persistence.impl",
			"com.liferay.portal.kernel.theme",
			"com.liferay.portal.kernel.util",
			"com.liferay.portal.kernel.util.comparator",
			"com.liferay.portal.kernel.verify.model",
			"com.liferay.portal.kernel.webserver",
			"com.liferay.kernel.portlet",
			"com.liferay.admin.kernel.util",
			"com.liferay.announcements.kernel",
			"com.liferay.asset.kernel",
			"com.liferay.background.task.kernel.util.comparator",
			"com.liferay.blogs.kernel",
			"com.liferay.blogs.kernel.exception",
			"com.liferay.blogs.kernel.model",
			"com.liferay.blogs.kernel.service",
			"com.liferay.blogs.service.persistence",
			"com.liferay.blogs.kernel.util.comparator",
			"com.liferay.document.library.kernel",
			"com.liferay.dynamic.data.mapping.kernel",
			"com.liferay.expando.kernel",
			"com.liferay.exportimport.kernel",
			"com.liferay.image.gallery.display.kernel.display.context",
			"com.liferay.journal.kernel.util",
			"com.liferay.layouts.admin.kernel.util",
			"com.liferay.message.boards.kernel",
			"com.liferay.message.boards.kernel.constants",
			"com.liferay.message.boards.kernel.exception",
			"com.liferay.message.boards.kernel.model",
			"com.liferay.message.boards.kernel.service",
			"com.liferay.message.boards.kernel.service.persistence",
			"com.liferay.message.boards.kernel.util",
			"com.liferay.message.boards.kernel.util.comparator",
			"com.liferay.mobile.device.rules",
			"com.liferay.portlet.configuration.kernel.util",
			"com.liferay.roles.admin.kernel.util",
			"com.liferay.sites.kernel.util",
			"com.liferay.social.kernel",
			"com.liferay.trash.kernel",
			"com.liferay.users.admin.kernel.util",
			"com.liferay.ratings.kernel",
			"com.liferay.ratings.kernel.definition",
			"com.liferay.ratings.kernel.display.context",
			"com.liferay.ratings.kernel.exception",
			"com.liferay.ratings.kernel.model",
			"com.liferay.ratings.kernel.service",
			"com.liferay.ratings.kernel.service.persistence",
			"com.liferay.ratings.kernel.transformer",
			"com.liferay.portal.kernel.service"
	};

	public RenamePortalKernelImports() {
		super(IMPORTS, IMPORTS_FIXED);
	}

	@Override
	public List<SearchResult> searchFile(File file, JavaFile javaFile) {
		final List<SearchResult> searchResults = new ArrayList<>();

		for (String importName : getImports().keySet()) {
			final List<SearchResult> importResult = javaFile.findImports(importName, IMPORTS);

			if (importResult.size() != 0) {
				for (SearchResult result : importResult) {
					// make sure that our import is not in list of fixed imports
					boolean skip = false;

					if (result.searchContext != null) {
						for (String fixed : IMPORTS_FIXED) {
							if (result.searchContext.contains(fixed)) {
								skip = true;
								break;
							}
						}
					}

					if (!skip) {
						result.autoCorrectContext = getPrefix() + importName;
						searchResults.add(result);
					}
				}

			}
		}

		return removeDuplicate(searchResults);
	}

	private List<SearchResult> removeDuplicate(List<SearchResult> searchResults) {
		final List<SearchResult> newList = new ArrayList<>();

		for (SearchResult searchResult : searchResults) {
			if (!newList.contains(searchResult)) {
				newList.add(searchResult);
			}
		}

		return newList;
	}

}