package net.gtaun.shoebill.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

public class FirstRepoAttemp
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			ArrayList<String> gamemodeList = new ArrayList<String>();
			ArrayList<String> pluginList = new ArrayList<String>();
			ResourceConfig config = new ResourceConfig( new FileInputStream( new File( "resources.yml" ) ) );
			RepositorySystem repoSystem = newRepositorySystem();
			RepositorySystemSession session = newSession( repoSystem );
			RemoteRepository remoteRepo = new RemoteRepository( "central", "default", "http://repo1.maven.org/maven2/" );
			DefaultArtifact artifact;
			CollectRequest collectRequest = new CollectRequest();
			
			//----------------------------------------------------------------------libraries
			collectRequest.addRepository( remoteRepo );	
			artifact = new DefaultArtifact( config.getRuntimeLibrary() );
			collectRequest.addDependency( new Dependency( artifact, "compile" ) );			
			DependencyNode node = repoSystem.collectDependencies( session, collectRequest ).getRoot();
			DependencyRequest dependencyRequest = new DependencyRequest( node, null );
			repoSystem.resolveDependencies( session, dependencyRequest  );
			PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
			node.accept( nlg );
			//System.out.println(nlg.getClassPath());
			for (String string : nlg.getClassPath().split(";"))
			{
				copyFile(string, "shoebill/libraries/" + new File(string).getName());
			}
			
			//----------------------------------------------------------------------plugins
			collectRequest = new CollectRequest();
			collectRequest.addRepository(remoteRepo);
			for ( String string : config.getPlugins() )
			{
				artifact = new DefaultArtifact( string );
				pluginList.add(artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar");
				collectRequest.addDependency( new Dependency( artifact, "compile" ) );
			}
			node = repoSystem.collectDependencies( session, collectRequest ).getRoot();
			dependencyRequest = new DependencyRequest( node, null );
			repoSystem.resolveDependencies( session, dependencyRequest  );
			nlg = new PreorderNodeListGenerator();
			node.accept( nlg );
			for (String string : nlg.getClassPath().split(";"))
			{
				copyFile(string, "shoebill/plugins/" + new File(string).getName());
			}
			
			//----------------------------------------------------------------------gamemodes
			collectRequest = new CollectRequest();
			collectRequest.addRepository(remoteRepo);
			for ( String string : config.getGamemodes() )
			{
				artifact = new DefaultArtifact( string );
				gamemodeList.add(artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar");
				collectRequest.addDependency( new Dependency( artifact, "compile" ) );
			}		
			node = repoSystem.collectDependencies( session, collectRequest ).getRoot();
			dependencyRequest = new DependencyRequest( node, null );
			repoSystem.resolveDependencies( session, dependencyRequest  );
			nlg = new PreorderNodeListGenerator();
			node.accept( nlg );
			for (String string : nlg.getClassPath().split(";"))
			{
				copyFile(string, "shoebill/gamemodes/" + new File(string).getName());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static RepositorySystem newRepositorySystem() throws Exception
	{
	    return new DefaultPlexusContainer().lookup( RepositorySystem.class );
	}

	private static RepositorySystemSession newSession( RepositorySystem system )
    {
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();

        LocalRepository localRepo = new LocalRepository( "shoebill/local-repo" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );

        return session;
    }
	
	private static File findFile(File dir, String target)
	{
		File fileFound = null;
		for (File file : dir.listFiles())
		{
			if(file.isDirectory())
			{
				fileFound = findFile(file, target);
				if(fileFound != null)
					break;
			}
			else if(target.compareTo(file.getName()) == 0)
			{
				fileFound = file;
				break;
			}
		}
		
		return fileFound;
	}
	
	private static boolean copyFile(String from, String to)
	{
		try
		{
			File oldFile = new File(from);
			File newFile = new File(to);
			if(oldFile.isFile())
			{
				byte[] buffer = new byte[1024];
				int byteRead = 0;
				InputStream inputStream = new FileInputStream(oldFile);
				FileOutputStream outputStream = new FileOutputStream(newFile, false);
				while( (byteRead = inputStream.read(buffer)) != -1)
				{
					outputStream.write(buffer, 0, byteRead);
				}
				
				inputStream.close();
				outputStream.close();
			}
			else
			{
				return false;
			}
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
