package fr.zetioz.conditionalgui.eventhandler;

import java.io.FileNotFoundException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public class ConditionalGUIMinedBlocksHandler implements Listener
{
	private YamlConfiguration database;

	public ConditionalGUIMinedBlocksHandler()
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
	public void onPlayerMineBlock(BlockBreakEvent e)
	{
		database.set("players." + e.getPlayer().getName() + ".mined_total", database.getInt("players." + e.getPlayer().getName() + ".mined_total") + 1);
		database.set("players." + e.getPlayer().getName() + ".mined_" + e.getBlock().getType().name().toLowerCase(), database.getInt("players." + e.getPlayer().getName() + ".mined_" + e.getBlock().getType().name().toLowerCase()) + 1);
	}
}
