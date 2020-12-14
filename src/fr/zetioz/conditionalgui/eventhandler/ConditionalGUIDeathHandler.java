package fr.zetioz.conditionalgui.eventhandler;

import java.io.FileNotFoundException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public class ConditionalGUIDeathHandler implements Listener
{
	private YamlConfiguration database;
	
	public ConditionalGUIDeathHandler()
	{
		try
		{
			database = ConditionalGUIMain.getFilesManager().getSimpleYaml("database");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		if(e.getEntity().getKiller() != null)
		{			
			database.set("players." + e.getEntity().getKiller().getName() + ".kills", database.getInt("players." + e.getEntity().getKiller().getName() + ".kills") + 1);
		}
	}
}
