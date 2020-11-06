package fr.zetioz.lprankup;

import java.io.FileNotFoundException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class LPRankupDeathHandler implements Listener
{
public YamlConfiguration database;
	
	public LPRankupDeathHandler()
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
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		database.set("players." + e.getEntity().getKiller().getName() + ".kills", database.getInt("players." + e.getEntity().getKiller().getName() + ".kills") + 1);
	}
}
