package net.gtaun.shoebill.dependency;

import java.io.File;

import net.gtaun.shoebill.dependency.manual.ManualRepositorySystemFactory;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

public class Util
{
    public static RepositorySystem newRepositorySystem()
    {
        return ManualRepositorySystemFactory.newRepositorySystem();
    }

    public static DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system, File repoDir )
    {
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();
        
        LocalRepository localRepo = new LocalRepository( repoDir );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );

        return session;
    }
}
