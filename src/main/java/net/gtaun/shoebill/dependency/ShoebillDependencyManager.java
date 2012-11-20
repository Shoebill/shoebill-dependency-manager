/**
 * Copyright (C) 2012 JoJLlmAn
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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.gtaun.shoebill.ResourceConfig;
import net.gtaun.shoebill.ResourceConfig.RepositoryEntry;
import net.gtaun.shoebill.ShoebillConfig;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

/**
 * 
 * 
 * @author JoJLlmAn, MK124
 */
public class ShoebillDependencyManager
{
	private static final String SCOPE_RUNTIME = "runtime";

	private static final FilenameFilter JAR_FILENAME_FILTER = new FilenameFilter()
	{
		@Override
		public boolean accept(File dir, String name)
		{
			return name.endsWith(".jar");
		}
	};
	
	
	public static void main(String[] args) throws Exception
	{
		List<File> files = resolveDependencies();
		for (File file : files) System.out.println(file);
	}
	
	public static List<File> resolveDependencies() throws Exception
	{
		ShoebillConfig shoebillConfig = new ShoebillConfig(new FileInputStream(new File("shoebill/shoebill.yml")));
		ResourceConfig config = new ResourceConfig(new FileInputStream(new File(shoebillConfig.getShoebillDir(), "resources.yml")));
		
		final File repoDir = shoebillConfig.getRepositoryDir();
		final File libDir = shoebillConfig.getLibrariesDir();
		final File pluginsDir = shoebillConfig.getPluginsDir();
		final File gamemodesDir = shoebillConfig.getGamemodesDir();
		
		File[] libJarFiles = libDir.listFiles(JAR_FILENAME_FILTER);
		File[] pluginsJarFiles = pluginsDir.listFiles(JAR_FILENAME_FILTER);
		File[] gamemodesJarFiles = gamemodesDir.listFiles(JAR_FILENAME_FILTER);
		
		List<File> files = new ArrayList<>();
		
		if(libJarFiles != null) files.addAll(Arrays.asList(libJarFiles));
		if(pluginsJarFiles != null) files.addAll(Arrays.asList(pluginsJarFiles));
		if(gamemodesJarFiles != null) files.addAll(Arrays.asList(gamemodesJarFiles));
		
		if (shoebillConfig.isResolveDependencies())
		{
			RepositorySystem repoSystem = Util.newRepositorySystem();
			DefaultRepositorySystemSession session = Util.newRepositorySystemSession(repoSystem, repoDir);
			CollectRequest collectRequest = new CollectRequest();
			
			session.setRepositoryListener(new ShoebillRepositoryListener());

			for (RepositoryEntry repo : config.getRepositories())
			{
				collectRequest.addRepository(new RemoteRepository(repo.getId(), repo.getType(), repo.getUrl()));
			}
			
			// Runtime
			Artifact runtimeArtifact = new DefaultArtifact(config.getRuntime());
			collectRequest.addDependency(new Dependency(runtimeArtifact, SCOPE_RUNTIME));
			
			// Plugins
			for (String coords : config.getPlugins())
			{
				Artifact artifact = new DefaultArtifact(coords);
				collectRequest.addDependency(new Dependency(artifact, SCOPE_RUNTIME));
			}
			
			// Gamemode
			Artifact gamemodeArtifact = new DefaultArtifact(config.getGamemode());
			collectRequest.addDependency(new Dependency(gamemodeArtifact, SCOPE_RUNTIME));
			
			DependencyNode node = null;
			try
			{
				node = repoSystem.collectDependencies(session, collectRequest).getRoot();
			}
			catch (DependencyCollectionException e)
			{
				e.printStackTrace();
			}
			DependencyRequest dependencyRequest = new DependencyRequest(node, null);
			dependencyRequest.setFilter(new ShoebillDependencyFilter(libDir, pluginsDir, gamemodesDir));
			
			try
			{
				repoSystem.resolveDependencies(session, dependencyRequest);
			}
			catch (DependencyResolutionException e)
			{
				e.printStackTrace();
			}
			
			PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
			node.accept(nlg);
			
			files.addAll(nlg.getFiles());
		}
		else
		{
			System.out.println("Skip resolve dependencies." );
		}

		return files;
	}
}
