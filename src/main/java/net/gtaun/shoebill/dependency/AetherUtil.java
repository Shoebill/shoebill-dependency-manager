/**
 * Copyright (C) 2012 MK124
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

package net.gtaun.shoebill.dependency;

import java.io.File;

import net.gtaun.shoebill.dependency.manual.ManualRepositorySystemFactory;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.util.DefaultRepositorySystemSession;

/**
 * 
 * 
 * @author MK124
 */
public class AetherUtil
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
