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
	
	
	public static void main(String[] args) throws Exception
	{
		ResourceConfig config = new ResourceConfig(new FileInputStream(new File(SHOEBILL_PATH + "resources.yml")));
		
		final File shoebillDir = new File(SHOEBILL_PATH);
		final File repoDir = new File(shoebillDir, REPOSITORY_DIR);
		
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
		collectRequest.addDependency(new Dependency(runtimeArtifact, "runtime"));
		
		// Plugins
		for (String coords : config.getPlugins())
		{
			Artifact artifact = new DefaultArtifact(coords);
			collectRequest.addDependency(new Dependency(artifact, "runtime"));
		}
		
		// Gamemode
		Artifact gamemodeArtifact = new DefaultArtifact(config.getGamemode());
		collectRequest.addDependency(new Dependency(gamemodeArtifact, "runtime"));
		
		DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();
		DependencyRequest dependencyRequest = new DependencyRequest(node, null);
		repoSystem.resolveDependencies(session, dependencyRequest);
		PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		node.accept(nlg);
	}
}
