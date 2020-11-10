package fr.zetioz.conditionalgui.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public class FilesManager
{
	private static ConditionalGUIMain plugin = ConditionalGUIMain.getPlugin();
	private Map<String, YamlConfiguration> simpleYaml;
	
	public FilesManager()
	{
		simpleYaml = new HashMap<>();
	}
	
	/*
	 * Get any file loaded on the file hashmap by name
	 * 
	 * @fileName: Name of the configuration file to get	
	 */
	
    public YamlConfiguration getSimpleYaml(String fileName) throws FileNotFoundException
    {
    	if(this.simpleYaml.containsKey(fileName))
    	{
    		return this.simpleYaml.get(fileName);
    	}
    	else
    	{
    		Bukkit.getLogger().severe("The file " + fileName + " doens't exists or hasn't been loaded yet !");
    		throw new FileNotFoundException();
    	}
    }
    /*
     * Simple way to make and save any configs file to an hashmap to use them later
	 * It avoids copy/paste any kind of method to create any new custom configs file
	 * 
	 * fileName: Name of the file to create as a configs.
	 * 
	 * NB: Doesn't create it if it already exists and only load it in the hashmap
     */
    
    public void createSimpleYaml(String fileName)
    {
    	try
    	{
    		File yamlFile = new File(plugin.getDataFolder(), fileName + ".yml");    		
    		if (!yamlFile.exists())
    		{
    			yamlFile.getParentFile().mkdirs();
    			plugin.saveResource(fileName + ".yml", false);
    		}
    		
    		simpleYaml.put(fileName, new YamlConfiguration());
    		
    		try
    		{
    			simpleYaml.get(fileName).load(yamlFile);
    		}
    		catch (IOException | InvalidConfigurationException e)
    		{
    			plugin.getLogger().severe("An error occured while loading the " + fileName + " file!");
    			e.printStackTrace();
    		}
    	}
    	catch (NullPointerException ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    public void saveSimpleYaml(String fileName)
    {
    	File yamlFile = new File(plugin.getDataFolder(), fileName + ".yml");
    	
    	try
    	{
			simpleYaml.get(fileName).save(yamlFile);
		}
    	catch (IOException e)
    	{
    		plugin.getLogger().severe("An error occured while saving the " + fileName + " file!");
			e.printStackTrace();
		}
    }
}
