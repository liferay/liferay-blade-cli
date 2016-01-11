package com.liferay.blade.cli.aether;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

/**
 * A helper to boot the repository system and a repository system session.
 */
public class Booter
{

    public static class ConsoleRepositoryListener implements RepositoryListener
    {

        public void artifactDeployed( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void artifactDeploying( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void artifactDescriptorInvalid( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void artifactDescriptorMissing( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void artifactDownloaded( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void artifactDownloading( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void artifactInstalled( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void artifactInstalling( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void artifactResolved( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void artifactResolving( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void metadataDeployed( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void metadataDeploying( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void metadataDownloaded( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void metadataDownloading( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void metadataInstalled( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void metadataInstalling( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void metadataInvalid( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void metadataResolved( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void metadataResolving( RepositoryEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

    }

    public static class ConsoleTransferListener implements TransferListener
    {

        public void transferCorrupted( TransferEvent arg0 ) throws TransferCancelledException
        {
            // TODO Auto-generated method stub

        }

        public void transferFailed( TransferEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

        public void transferInitiated( TransferEvent arg0 ) throws TransferCancelledException
        {
            // TODO Auto-generated method stub

        }

        public void transferProgressed( TransferEvent arg0 ) throws TransferCancelledException
        {
            // TODO Auto-generated method stub

        }

        public void transferStarted( TransferEvent arg0 ) throws TransferCancelledException
        {
            // TODO Auto-generated method stub

        }

        public void transferSucceeded( TransferEvent arg0 )
        {
            // TODO Auto-generated method stub

        }

    }

    public static RepositorySystem newRepositorySystem()
    {
        return ManualRepositorySystemFactory.newRepositorySystem();
    }

    public static DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system )
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository( "target/local-repo" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

        session.setTransferListener( new ConsoleTransferListener() );
        session.setRepositoryListener( new ConsoleRepositoryListener() );

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

        return session;
    }

    public static List<RemoteRepository> repos( RepositorySystem system, RepositorySystemSession session )
    {
        List<RemoteRepository> repos =  Arrays.asList(
            newLocalRepository(),
            newLiferayRepository(),
            newCentralRepository()
        );
        System.out.println("repos: " + repos);
        return new ArrayList<RemoteRepository>( repos );
    }

    private static RemoteRepository newLocalRepository()
    {
        return new RemoteRepository.Builder( "local", "default", "file:" + System.getProperty("user.home") + "/.m2/repository/" ).build();
    }

    private static RemoteRepository newCentralRepository()
    {
        return new RemoteRepository.Builder( "central", "default", "http://central.maven.org/maven2/" ).build();
    }

    private static RemoteRepository newLiferayRepository()
    {
        return new RemoteRepository.Builder( "local", "default", "https://repository.liferay.com/nexus/content/groups/public/" ).build();
    }

}
