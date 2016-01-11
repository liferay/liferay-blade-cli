package com.liferay.blade.cli.aether;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;

public class ExtractArtifact
{

    public static void main( String[] args )
        throws Exception
    {

        RepositorySystem system = Booter.newRepositorySystem();
        RepositorySystemSession session = Booter.newRepositorySystemSession( system );

        String groupId = "com.liferay.maven.archetypes";
        String artifactId = "liferay-portlet-primefaces-archetype";

        // Cannot seem to pull from my local .m2 when it is given as a "remote" repo
        //
        // String groupId = "com.liferay.faces.maven.archetypes";
        // String artifactId = "primefaces-portlet-liferay-jsf-2.2-archetype";

        String range = "[0,)";

        // pick an artifact
        Artifact artifactRange = new DefaultArtifact( groupId + ":" + artifactId + ":" + range );

        // find the newest version of this artifact
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact( artifactRange );
        rangeRequest.setRepositories( Booter.repos( system, session ) );
        VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );
        Version newestVersion = rangeResult.getHighestVersion();
        System.out.println("main: newestVersion = " + newestVersion);

        // get the newest version of the artifact
        Artifact artifact = new DefaultArtifact( groupId + ":" + artifactId + ":" + newestVersion );
        System.out.println("main: artifact.getVersion() = " + artifact.getVersion());
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( artifact );
        artifactRequest.setRepositories( Booter.repos( system, session ) );
        ArtifactResult artifactResult = system.resolveArtifact( session, artifactRequest );
        artifact = artifactResult.getArtifact();

        System.out.println("artifact.getFile() = " + artifact.getFile());
        unzip(new File(artifact.getFile().getAbsolutePath()), new File("target/" + artifactId), null);
    }

    static void unzip(File srcFile, File destDir, String entryToStart) throws IOException {
        try(final ZipFile zip = new ZipFile(srcFile)) {
            final Enumeration<? extends ZipEntry> entries = zip.entries();

            boolean foundStartEntry = entryToStart == null;

            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();

                if (!foundStartEntry) {
                    foundStartEntry = entryToStart.equals(entry.getName());
                    continue;
                }

                if (entry.isDirectory()) {
                    continue;
                }

                String entryName = null;

                if( entryToStart == null ) {
                    entryName = entry.getName();
                }
                else {
                    entryName = entry.getName().replaceFirst( entryToStart, "" );
                }

                final File f = new File(destDir, entryName);
                final File dir = f.getParentFile();

                if (!dir.exists() && !dir.mkdirs()) {
                    final String msg = "Could not create dir: " + dir.getPath();
                    throw new IOException(msg);
                }


                try(final InputStream in = zip.getInputStream(entry);
                    final FileOutputStream out = new FileOutputStream(f);) {

                    final byte[] bytes = new byte[1024];
                    int count = in.read(bytes);

                    while (count != -1) {
                        out.write(bytes, 0, count);
                        count = in.read(bytes);
                    }

                    out.flush();
                }
            }
        }
    }

}
