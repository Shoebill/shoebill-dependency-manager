/**
 * Copyright (C) 2012 JoJLlmAn
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * 
 * 
 * @author JoJLlmAn
 */
public class RepoUpdater
{
	private String localRepoPath;
	private String savePath;
	private Map<DefaultArtifact, String> artifacts;
	private ArrayList<RemoteRepository> repos;
	
	
	public RepoUpdater(String localRepoPath, String savePath)
	{
		this.localRepoPath = new String(localRepoPath);
		this.savePath = new String(savePath);
		
		File dir = new File(savePath);
		if (!dir.exists()) dir.mkdir();
		
		artifacts = new HashMap<DefaultArtifact, String>();
		repos = new ArrayList<RemoteRepository>();
	}
	
	public void UpdateDependencies()
	{
		try
		{
			RepositorySystem repoSystem = newRepositorySystem();
			RepositorySystemSession session = newSession(repoSystem);
			CollectRequest collectRequest = new CollectRequest();
			
			for (DefaultArtifact artifact : artifacts.keySet())
			{
				collectRequest.addDependency(new Dependency(artifact, artifacts.get(artifact)));
			}
			for (RemoteRepository repo : repos)
			{
				collectRequest.addRepository(repo);
			}
			
			DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();
			DependencyRequest dependencyRequest = new DependencyRequest(node, null);
			repoSystem.resolveDependencies(session, dependencyRequest);
			PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
			node.accept(nlg);
			
			for (String string : nlg.getClassPath().split(";"))
			{
				copyFile(string, savePath + "/" + new File(string).getName());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void AddDependency(String coords, String scope)
	{
		try
		{
			DefaultArtifact artifact = new DefaultArtifact(coords);
			if (artifact != null)
			{
				if (!artifacts.containsKey(artifact)) artifacts.put(artifact, scope);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void AddDependency(DefaultArtifact artifact, String scope)
	{
		if (artifact != null)
		{
			if (!artifacts.containsKey(artifact)) artifacts.put(artifact, scope);
		}
	}
	
	public void AddDependencies(List<String> coords, String scope)
	{
		try
		{
			for (String string : coords)
			{
				DefaultArtifact artifact = new DefaultArtifact(string);
				if (artifact != null)
				{
					if (!artifacts.containsKey(artifact)) artifacts.put(artifact, scope);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void RemoveDependency(String coords)
	{
		try
		{
			DefaultArtifact artifact = new DefaultArtifact(coords);
			if (artifacts.containsKey(artifact)) artifacts.remove(artifact);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void RemoveDependency(DefaultArtifact artifact)
	{
		if (artifacts.containsKey(artifact)) artifacts.remove(artifact);
	}
	
	public void ClearDependencies()
	{
		artifacts.clear();
	}
	
	public void AddRemoteRepo(String id, String type, String url)
	{
		try
		{
			RemoteRepository remoteRepo = new RemoteRepository(id, type, url);
			if (remoteRepo != null)
			{
				if (!repos.contains(remoteRepo)) repos.add(remoteRepo);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void AddRemoteRepo(RemoteRepository remoteRepo)
	{
		if (remoteRepo != null)
		{
			if (!repos.contains(remoteRepo)) repos.add(remoteRepo);
		}
	}
	
	public void RemoveRemoteRepo(String id, String type, String url)
	{
		try
		{
			RemoteRepository remoteRepo = new RemoteRepository(id, type, url);
			if (repos.contains(remoteRepo)) repos.remove(remoteRepo);
			/*
			 * for (RemoteRepository repo : repos)
			 * {
			 * if(repo.equals(remoteRepo))
			 * {
			 * repos.remove(repo);
			 * break;
			 * }
			 * }
			 */
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void RemoveRemoteRepo(RemoteRepository remoteRepo)
	{
		if (repos.contains(remoteRepo)) repos.remove(remoteRepo);
		/*
		 * for (RemoteRepository repo : repos)
		 * {
		 * if(repo.equals(remoteRepo))
		 * {
		 * repos.remove(repo);
		 * break;
		 * }
		 * }
		 */
	}
	
	public void ClearRemoteRepo()
	{
		repos.clear();
	}
	
	private static boolean copyFile(String from, String to)
	{
		try
		{
			File oldFile = new File(from);
			File newFile = new File(to);
			
			if (oldFile.isFile())
			{
				byte[] buffer = new byte[1024];
				int byteRead = 0;
				InputStream inputStream = new FileInputStream(oldFile);
				FileOutputStream outputStream = new FileOutputStream(newFile, false);
				while ((byteRead = inputStream.read(buffer)) != -1)
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
	
	private static RepositorySystem newRepositorySystem() throws Exception
	{
		return new DefaultPlexusContainer().lookup(RepositorySystem.class);
	}
	
	private RepositorySystemSession newSession(RepositorySystem system)
	{
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();
		
		LocalRepository localRepo = new LocalRepository(localRepoPath);
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(localRepo));
		
		return session;
	}
}
