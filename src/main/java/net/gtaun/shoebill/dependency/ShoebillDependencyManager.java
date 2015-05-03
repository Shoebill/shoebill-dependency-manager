/**
 * Copyright (C) 2012 JoJLlmAn
 * Copyright (C) 2012-2014 MK124
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

import net.gtaun.shoebill.ResourceConfig;
import net.gtaun.shoebill.ResourceConfig.RepositoryEntry;
import net.gtaun.shoebill.ShoebillArtifactLocator;
import net.gtaun.shoebill.ShoebillConfig;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 *
 *
 * @author JoJLlmAn, MK124
 */
public class ShoebillDependencyManager
{
	private static final String VERSION_FILENAME = "/version.yml";
	private static final String SHOEBILL_CONFIG_PATH = "./shoebill/shoebill.yml";

	private static final String PROPERTY_JAR_FILES = "jarFiles";
	private static final String SCOPE_RUNTIME = "runtime";

	private static final FilenameFilter JAR_FILENAME_FILTER = (dir, name) -> name.endsWith(".jar");

	static
	{
		DependencyManagerVersion version = new DependencyManagerVersion(ShoebillDependencyManager.class.getResourceAsStream(VERSION_FILENAME));

		String startupMessage = version.getName() + " " + version.getVersion();
		if (version.getBuildNumber() != 0) startupMessage += " Build " + version.getBuildNumber();
		startupMessage += " (for " + version.getSupport() + ")";

		System.out.println(startupMessage);
		System.out.println("Build date: " + version.getBuildDate());
	}


	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Throwable
	{
		Map<String, Object> properties = resolveDependencies();
		List<File> files = List.class.cast(properties.get(PROPERTY_JAR_FILES));
		for (File file : files) System.out.println(file);
	}

	public static Map<String, Object> resolveDependencies() throws Throwable
	{
		ShoebillConfig shoebillConfig = new ShoebillConfig(new FileInputStream(SHOEBILL_CONFIG_PATH));
		ResourceConfig resourceConfig = new ResourceConfig(new FileInputStream(new File(shoebillConfig.getShoebillDir(), "resources.yml")));
		ShoebillArtifactLocator artifactLocator = new ShoebillArtifactLocator(shoebillConfig, resourceConfig);

		File onlineModeOnceFile = new File(shoebillConfig.getShoebillDir(), "ONLINE_MODE_ONCE");
		if (onlineModeOnceFile.exists())
		{
			onlineModeOnceFile.delete();
			resourceConfig.setOfflineMode(false);
		}

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
			System.out.println("Resolving dependencies...");

			RepositorySystem repoSystem = AetherUtil.newRepositorySystem();
			DefaultRepositorySystemSession session = AetherUtil.newRepositorySystemSession(repoSystem, repoDir);
			CollectRequest collectRequest = new CollectRequest();

			session.setRepositoryListener(new ShoebillRepositoryListener());
			session.setUpdatePolicy(resourceConfig.getCacheUpdatePolicy());
			session.setOffline(resourceConfig.isOfflineMode());

			for (RepositoryEntry repo : resourceConfig.getRepositories())
			{
				Authentication auth = null;

				String username = repo.getUsername();
				String password = repo.getPassword();
				if (username != null && password != null)
				{
					auth = new AuthenticationBuilder().addUsername(username).addPassword(password).build();
				}

				RemoteRepository repository = new RemoteRepository.Builder(repo.getId(), repo.getType(), repo.getUrl())
					.setAuthentication(auth)
					.build();

				collectRequest.addRepository(repository);
			}

			BiConsumer<String, Artifact> checkOverrideDependencies = (coord, artifact) ->
			{
				File file = artifactLocator.getOverrideFile(coord);
				if (file == null) return;

				try (JarFile jarFile = new JarFile(file))
				{
					ZipEntry entry = jarFile.getEntry("META-INF/maven/" + artifact.getGroupId() + "/" + artifact.getArtifactId() + "/pom.xml");
					if (entry == null) throw new FileNotFoundException();

					Model model = new MavenXpp3Reader().read(jarFile.getInputStream(entry));
					model.getDependencies().forEach((d) ->
					{
						Artifact a = new DefaultArtifact(d.getGroupId(), d.getArtifactId(), "jar", d.getVersion());
						collectRequest.addDependency(new Dependency(a, SCOPE_RUNTIME));
					});
				}
				catch (Exception e)
				{
					System.out.println("Missing artifact descriptor for overridden jar: " + file.getPath());
				}
			};

			// Runtimes
			for (String coord : resourceConfig.getRuntimes())
			{
				if (coord.contains(":") == false)
				{
					System.out.println("Skipped artifact " + coord + " (Runtime)");
					continue;
				}

				Artifact artifact = new DefaultArtifact(coord);
				collectRequest.addDependency(new Dependency(artifact, SCOPE_RUNTIME));
				checkOverrideDependencies.accept(coord, artifact);
			}

			// Plugins
			for (String coord : resourceConfig.getPlugins())
			{
				if (coord.contains(":") == false)
				{
					System.out.println("Skipped artifact " + coord + " (Plugin)");
					continue;
				}

				Artifact artifact = new DefaultArtifact(coord);
				collectRequest.addDependency(new Dependency(artifact, SCOPE_RUNTIME));
				checkOverrideDependencies.accept(coord, artifact);
			}

			// Gamemode
			String gamemodeCoord = resourceConfig.getGamemode();
			if (gamemodeCoord.contains(":"))
			{
				Artifact gamemodeArtifact = new DefaultArtifact(resourceConfig.getGamemode());
				collectRequest.addDependency(new Dependency(gamemodeArtifact, SCOPE_RUNTIME));
				checkOverrideDependencies.accept(gamemodeCoord, gamemodeArtifact);
			}
			else
			{
				System.out.println("Skipped artifact " + gamemodeCoord + " (Gamemode)");
			}

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
			dependencyRequest.setFilter(new ShoebillDependencyFilter(artifactLocator));

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

		Map<String, Object> properties = new HashMap<>();
		properties.put(PROPERTY_JAR_FILES, files);
		return properties;
	}
}
