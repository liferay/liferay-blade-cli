package com.liferay.blade.upgrade.liferay70;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.api.SourceFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

public abstract class AbstractFileMigrator<T extends SourceFile> implements FileMigrator {

	BundleContext _context;
	List<String> _fileExtentions;
	String _problemTitle;
	String _problemSummary;
	String _problemTickets;
	String _sectionKey;
	final Class<T> _type;
	
	public AbstractFileMigrator(Class<T> type) {
		_type = type;
	}

	@Activate
	public void activate(ComponentContext ctx) {
		_context = ctx.getBundleContext();

		final Dictionary<String, Object> properties = ctx.getProperties();

		_fileExtentions = Arrays.asList(((String)properties.get("file.extensions")).split(","));

		_problemTitle = (String)properties.get("problem.title");
		_problemSummary = (String)properties.get("problem.summary");
		_problemTickets = (String)properties.get("problem.tickets");
		_sectionKey = (String)properties.get("problem.section");
	}

	@Override
	public List<Problem> analyze(File file) {
		final List<Problem> problems = new ArrayList<>();

		final List<SearchResult> searchResults = searchFile(file, createFileChecker(_type, file));

		if (searchResults != null) {
			String sectionHtml = MarkdownParser.getSection("BREAKING_CHANGES.markdown", _sectionKey);

			for (SearchResult searchResult : searchResults) {
				String fileExtension = new Path(file.getAbsolutePath()).getFileExtension();

				problems.add(new Problem(_problemTitle, _problemSummary,
					fileExtension, _problemTickets, file, searchResult.startLine,
					searchResult.startOffset, searchResult.endOffset, sectionHtml,
					searchResult.autoCorrectContext));
			}
		}

		return problems;
	}

	protected T createFileChecker(Class<T> type, File file) {
		final String fileExtension = new Path(file.getAbsolutePath()).getFileExtension();
		
		try {
			final Collection<ServiceReference<T>> refs = _context.getServiceReferences(type,
					"(file.extension=" + fileExtension + ")");

			if (refs != null && refs.size() > 0) {
				T service = _context.getService(refs.iterator().next());

				final T fileCheckerFile = type.cast(service);

				if (fileCheckerFile == null) {
					throw new IllegalArgumentException(
							"Could not find " + type.getSimpleName() + " service for specified file " + file.getName());
				}
				else {
					fileCheckerFile.setFile(file);
				}

				return fileCheckerFile;
			}
		} catch (InvalidSyntaxException e) {
		} 

		return null;
	}

	protected abstract List<SearchResult> searchFile(File file, T fileChecker);

}
