package com.liferay.blade.cli;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class SamplesVisitor extends SimpleFileVisitor<Path> {
	private Collection<Path> paths = new HashSet<>();

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        throws IOException
    {
    	super.preVisitDirectory(dir, attrs);
    	if (Files.exists(dir.resolve("src"))) {
    		paths.add(dir);
            return FileVisitResult.SKIP_SUBTREE;
    	}
        return FileVisitResult.CONTINUE;
    }

    public Collection<Path> getPaths() {
    	return Collections.unmodifiableCollection(paths);
    }
    
    public void clear() {
    	paths.clear();
    }
}
