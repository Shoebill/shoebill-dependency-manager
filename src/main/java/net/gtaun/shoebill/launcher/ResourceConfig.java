package net.gtaun.shoebill.launcher;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.util.config.YamlConfiguration;

public class ResourceConfig
{
	private String runtimeLibrary;
	private List<String> plugins;
	private List<String> gamemodes;
	private List<Map<String, Object>> repositories; 
	
	@SuppressWarnings("unchecked")
	public ResourceConfig(InputStream in)
	{
		YamlConfiguration config = new YamlConfiguration();
		config.read(in);
	
		runtimeLibrary = config.getString("runtime");
		plugins = (List<String>) config.getList("plugins");
		gamemodes = (List<String>) config.getList("gamemodes");
		repositories = (List<Map<String,Object>>) config.getList("repositories");
	}
	
	public String getRuntimeLibrary()
	{
		return runtimeLibrary;
	}
	
	public List<String> getPlugins()
	{
		return plugins;
	}
	
	public List<String> getGamemodes()
	{
		return gamemodes;
	}
	
	public List<Map<String, Object>> getRepositories()
	{
		return repositories;
	}
}
