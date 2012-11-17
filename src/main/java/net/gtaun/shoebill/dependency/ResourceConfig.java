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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.util.config.YamlConfiguration;

/**
 * 
 * 
 * @author JoJLlmAn
 */
public class ResourceConfig
{
	private List<Map<String, Object>> repositories;
	private String runtime;
	private List<String> plugins;
	private String gamemode;
	
	
	@SuppressWarnings("unchecked")
	public ResourceConfig(InputStream in)
	{
		YamlConfiguration config = new YamlConfiguration();
		config.read(in);

		repositories = (List<Map<String, Object>>) config.getList("repositories");
		runtime = config.getString("runtime");
		plugins = (List<String>) config.getList("plugins");
		gamemode = config.getString("gamemodes");
	}
	
	public List<Map<String, Object>> getRepositories()
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
