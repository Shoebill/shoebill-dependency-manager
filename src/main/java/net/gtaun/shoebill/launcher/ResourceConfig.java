package net.gtaun.shoebill.launcher;

import java.io.InputStream;
import java.util.List;

import net.gtaun.shoebill.util.config.YamlConfiguration;

public class ResourceConfig
{
	private String runtimeLibrary;
	private List<String> plugins;
	private List<String> gamemodes;
	
	@SuppressWarnings("unchecked")
	public ResourceConfig(InputStream in)
	{
		YamlConfiguration config = new YamlConfiguration();
		config.read(in);
	
		runtimeLibrary = config.getString("runtime");
		plugins = (List<String>) config.getList("plugins");
		gamemodes = (List<String>) config.getList("gamemodes");
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
}
