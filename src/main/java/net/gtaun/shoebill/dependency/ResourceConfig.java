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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.util.config.YamlConfiguration;

/**
 * 
 * 
 * @author JoJLlmAn, MK124
 */
public class ResourceConfig
{
	static final class RepositoryEntry
	{
		private String id;
		private String type;
		private String url;
		
		RepositoryEntry(Map<String, Object> map)
		{
			id = map.get("id").toString();
			type = map.get("type").toString();
			url = map.get("url").toString();
		}
		
		public String getId()
		{
			return id;
		}
		
		public String getType()
		{
			return type;
		}
		
		public String getUrl()
		{
			return url;
		}
	}
	
	
	private List<RepositoryEntry> repositories;
	private String runtime;
	private List<String> plugins;
	private String gamemode;
	
	
	@SuppressWarnings("unchecked")
	public ResourceConfig(InputStream in)
	{
		YamlConfiguration config = new YamlConfiguration();
		config.read(in);
		
		List<Map<String, Object>> repoMaps = (List<Map<String, Object>>) config.getList("repositories");
		repositories = new ArrayList<>(repoMaps.size());
		
		for (Map<String, Object> map : repoMaps)
		{
			repositories.add(new RepositoryEntry(map));
		}
		
		runtime = config.getString("runtime");
		plugins = (List<String>) config.getList("plugins");
		gamemode = config.getString("gamemodes");
	}
	
	public List<RepositoryEntry> getRepositories()
	{
		return repositories;
	}
	
	public String getRuntime()
	{
		return runtime;
	}
	
	public List<String> getPlugins()
	{
		return plugins;
	}
	
	public String getGamemode()
	{
		return gamemode;
	}
}
