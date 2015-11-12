package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.summary=All Shopping Cart APIs previously exposed as Liferay Portal API in 6.2 have been move out from portal-service into separate OSGi modules",
		"problem.tickets=LPS-55355",
		"problem.title=Shopping Cart APIs migrated to OSGi module",
		"problem.section=#legacy"
	},
	service = FileMigrator.class
)
public class ShoppingCartLegacyAPI extends JavaFileMigrator {

	private static final String[] SERVICE_API_PREFIXES =  {
		"com.liferay.portlet.shopping.service.ShoppingCart",
		"com.liferay.portlet.shopping.service.ShoppingCategory",
		"com.liferay.portlet.shopping.service.ShoppingCoupon",
		"com.liferay.portlet.shopping.service.ShoppingItemField",
		"com.liferay.portlet.shopping.service.ShoppingItem",
		"com.liferay.portlet.shopping.service.ShoppingItemPrice",
		"com.liferay.portlet.shopping.service.ShoppingOrderItem",
		"com.liferay.portlet.shopping.service.ShoppingOrder"
	};

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findServiceAPIs(SERVICE_API_PREFIXES);
	}
}
