package fr.zetioz.lprankup;

import java.io.FileNotFoundException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LPRankupJoinHandler implements Listener
{
	public YamlConfiguration database;
	
	public LPRankupJoinHandler()
	{
		try
		{
			database = LPRankupMain.getFilesManager().getSimpleYaml("database");
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
