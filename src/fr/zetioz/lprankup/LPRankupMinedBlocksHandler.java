package fr.zetioz.lprankup;

import java.io.FileNotFoundException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LPRankupMinedBlocksHandler implements Listener
{
	private YamlConfiguration database;

	public LPRankupMinedBlocksHandler()
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
	public void onPlayerMineBlock(BlockBreakEvent e)
	{
		database.set("players." + e.getPlayer().getName() + ".mined_blocks", database.getInt("players." + e.getPlayer().getName() + ".mined_blocks") + 1);
	}
}
