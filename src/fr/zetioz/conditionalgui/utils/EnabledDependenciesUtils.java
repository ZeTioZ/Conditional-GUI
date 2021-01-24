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
		
		for(String softdependencies : ConditionalGUIMain.getPlugin().getDescription().getSoftDepend())
		{
			if(Bukkit.getPluginManager().isPluginEnabled(softdependencies))
			{
				ConditionalGUIMain.getPlugin().getLogger().info(softdependencies + " has been found! Hook enabled!");
				enabledDependencies.add(softdependencies);
			}
			else
			{
				ConditionalGUIMain.getPlugin().getLogger().info(softdependencies + " not found! Hook disabled!");
			}
		}
		
		return enabledDependencies;
	}
}

