package fr.zetioz.conditionalgui.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public final class EnabledDependenciesUtils {
	private EnabledDependenciesUtils() {}
	
	public static List<String> getEnabledDependencies()
	{
		List<String> enabledDependencies = new ArrayList<>();
		
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("SuperiorSkyblock"))
		{
			ConditionalGUIMain.getPlugin().getLogger().info("SuperiorSkyblock has been found! Hook enabled!");
			enabledDependencies.add("SuperiorSkyblock");
		}
		else
		{
			ConditionalGUIMain.getPlugin().getLogger().info("SuperiorSkyblock not found! Hook disabled!");
		}
		
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("mcMMO"))
		{
			ConditionalGUIMain.getPlugin().getLogger().info("mcMMO has been found! Hook enabled!");
			enabledDependencies.add("mcMMO");
		}
		else
		{
			ConditionalGUIMain.getPlugin().getLogger().info("mcMMO not found! Hook disabled!");
		}
		
		
		return enabledDependencies;
	}
}
