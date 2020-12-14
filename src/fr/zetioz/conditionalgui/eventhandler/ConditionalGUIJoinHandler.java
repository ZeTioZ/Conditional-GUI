package fr.zetioz.conditionalgui.eventhandler;

import java.io.FileNotFoundException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public class ConditionalGUIJoinHandler implements Listener
{
	private YamlConfiguration database;
	
	public ConditionalGUIJoinHandler()
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
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(!database.isConfigurationSection("players." + e.getPlayer().getName()))
		{
			database.set("players." + e.getPlayer().getName() + ".kills", 0);
			database.set("players." + e.getPlayer().getName() + ".mined_blocks", 0);
		}
	}
}
