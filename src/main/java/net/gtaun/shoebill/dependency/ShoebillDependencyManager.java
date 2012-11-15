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
import java.util.Map;

/**
 * 
 * 
 * @author JoJLlmAn
 */
public class ShoebillDependencyManager
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			ResourceConfig config = new ResourceConfig( new FileInputStream( new File( "resources.yml" ) ) );
			
			RepoUpdater runtimeRepoUpdater = new RepoUpdater("shoebill/local-repo", "shoebill/libraries");
			RepoUpdater pluginsRepoUpdater = new RepoUpdater("shoebill/local-repo", "shoebill/plugins");
			RepoUpdater gamemodesRepoUpdater = new RepoUpdater("shoebill/local-repo", "shoebill/gamemodes");
			
			for (Map<String, Object> repo: config.getRepositories())
			{
				String id = repo.get("id").toString();
				String type = repo.get("type").toString();
				String url = repo.get("url").toString();
				runtimeRepoUpdater.AddRemoteRepo(id, type, url);
				pluginsRepoUpdater.AddRemoteRepo(id, type, url);
				gamemodesRepoUpdater.AddRemoteRepo(id, type, url);
			}
			
			runtimeRepoUpdater.AddDependency(config.getRuntimeLibrary(), "compile");
			pluginsRepoUpdater.AddDependencies(config.getPlugins(), "compile");
			gamemodesRepoUpdater.AddDependencies(config.getGamemodes(), "compile");
			
			runtimeRepoUpdater.UpdateDependencies();
			pluginsRepoUpdater.UpdateDependencies();
			gamemodesRepoUpdater.UpdateDependencies();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/*private static File findFile(File dir, String target)
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
	}*/
}
