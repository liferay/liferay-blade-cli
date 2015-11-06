package com.liferay.blade.eclipse.provider;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.liferay.blade.api.MigrationConstants;
import com.liferay.blade.api.SourceFile;

public class WorkspaceFile implements SourceFile {
	
	File _file;
	WorkspaceHelper _helper = new WorkspaceHelper();

	@Override
	public IFile getIFile(File file) {
		IFile retval = null;

		// first try to find this file in the current workspace
		final IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
				.findFilesForLocationURI(file.toURI());

		// if there are multiple files in this workspace use the shortest path
		if (files != null && files.length == 1) {
			retval = files[0];
		} else if (files != null && files.length > 0) {
			for (IFile ifile : files) {
				if (retval == null) {
					retval = ifile;
				} else {
					// prefer the path that is shortest (to avoid a nested
					// version)
					if (ifile.getFullPath().segmentCount() <
							retval.getFullPath().segmentCount()) {
						retval = ifile;
					}
				}
			}
		}

		if (retval == null) {
			try {
				retval = _helper.createIFile( MigrationConstants.HELPER_PROJECT_NAME, file );
			} catch (CoreException | IOException e) {
				e.printStackTrace();
			}
		}

		return retval;
	}

	@Override
	public void setFile(File file) {
		_file = file;
	}

}
