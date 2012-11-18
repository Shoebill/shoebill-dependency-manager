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
import java.util.Map;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
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
	private static final String SHOEBILL_PATH = "shoebill/";
	private static final String REPOSITORY_DIR = "repository/";
	private static final String LIBRARIES_DIR = "libraries/";
	private static final String PLUGINS_DIR = "plugins/";
	private static final String GAMEMODES_DIR = "gamemodes/";
	
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
		ResourceConfig config = new ResourceConfig(new FileInputStream(new File(SHOEBILL_PATH + "resources.yml")));
		
		final File shoebillDir = new File(SHOEBILL_PATH);
		final File repoDir = new File(shoebillDir, REPOSITORY_DIR);
		final File libDir = new File(shoebillDir, LIBRARIES_DIR);
		final File pluginsDir = new File(shoebillDir, PLUGINS_DIR);
		final File gamemodesDir = new File(shoebillDir, GAMEMODES_DIR);
		
		List<File> files = new ArrayList<>();
		files.addAll(Arrays.asList(libDir.listFiles(JAR_FILENAME_FILTER)));
		files.addAll(Arrays.asList(pluginsDir.listFiles(JAR_FILENAME_FILTER)));
		files.addAll(Arrays.asList(gamemodesDir.listFiles(JAR_FILENAME_FILTER)));
		
		RepositorySystem repoSystem = Util.newRepositorySystem();
		DefaultRepositorySystemSession session = Util.newRepositorySystemSession(repoSystem, repoDir);
		CollectRequest collectRequest = new CollectRequest();
		
		session.setRepositoryListener(new ShoebillRepositoryListener());

		for (Map<String, Object> repo : config.getRepositories())
		{
			String id = repo.get("id").toString();
			String type = repo.get("type").toString();
			String url = repo.get("url").toString();
			collectRequest.addRepository(new RemoteRepository(id, type, url));
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
		
		DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();
		DependencyRequest dependencyRequest = new DependencyRequest(node, null);
		dependencyRequest.setFilter(new ShoebillDependencyFilter(libDir, pluginsDir, gamemodesDir));
		
		repoSystem.resolveDependencies(session, dependencyRequest);
		
		PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		node.accept(nlg);
		
		files.addAll(nlg.getFiles());
		return files;
	}
}
