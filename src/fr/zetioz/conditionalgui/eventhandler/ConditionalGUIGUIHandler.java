package fr.zetioz.conditionalgui.eventhandler;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.zetioz.conditionalgui.ConditionalGUIMain;
import fr.zetioz.conditionalgui.utils.ColorUtils;
import fr.zetioz.conditionalgui.utils.ConditionsCheckerUtils;
import fr.zetioz.conditionalgui.utils.ItemBuilderUtils;

public class ConditionalGUIGUIHandler implements Listener
{
	
	private YamlConfiguration messagesFile;
	private YamlConfiguration configsFile;
	private YamlConfiguration database;
	private ConditionsCheckerUtils conditionChecker;
	private String prefix;
	private Map<String, String> guisTitles;
	
	public ConditionalGUIGUIHandler()
	{
		try
		{
			messagesFile = ConditionalGUIMain.getFilesManager().getSimpleYaml("messages");
			configsFile = ConditionalGUIMain.getFilesManager().getSimpleYaml("configs");
			database = ConditionalGUIMain.getFilesManager().getSimpleYaml("database");
			prefix = ColorUtils.color(messagesFile.getString("prefix"));
			guisTitles = new HashMap<>();
			for(String guiName : configsFile.getKeys(false))
			{
				if(!guiName.equals("gui"))
				{					
					guisTitles.put(ColorUtils.discolor(ColorUtils.color(configsFile.getString(guiName + ".title"))), guiName);
				}
			}
			conditionChecker = new ConditionsCheckerUtils(configsFile, ConditionalGUIMain.getFilesManager().getSimpleYaml("database"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent e)
	{
		if(e.getClickedInventory() != null && e.getView().getTitle() != null && guisTitles.containsKey(ColorUtils.discolor(e.getView().getTitle())))
		{
			// Gui management
			e.setCancelled(true);
			
			final String GUIName = guisTitles.get(ColorUtils.discolor(e.getView().getTitle()));
			final Player p = (Player) e.getWhoClicked();
			
			// Getting the actual border item
			ItemStack border = null;
			try
			{			
				border = ItemBuilderUtils.build(Material.valueOf(configsFile.getString("gui.borders.icon").toUpperCase()), ColorUtils.color(configsFile.getString("gui.borders.name")));
			}
			catch(IllegalArgumentException ex)
			{
				for(String line : messagesFile.getStringList("errors.invalid-icon"))
				{
					ConditionalGUIMain.getPlugin().getLogger().severe(line.replace("{icon}", configsFile.getString("gui.borders.icon").toUpperCase()));
				}
				return;
			}
			
			if(e.getCurrentItem() != null && e.getCurrentItem() != border && e.getCurrentItem().getType() != Material.AIR)
			{
				Set<String> ranksList = configsFile.getConfigurationSection(GUIName + ".ranks").getKeys(false);
				String itemName = ColorUtils.discolor(e.getCurrentItem().getItemMeta().getDisplayName());
				List<String> ownedRanks = database.getStringList("players." + p.getName() + ".owned-ranks");
				if(ranksList.contains(itemName))
				{
					boolean passed = conditionChecker.isPassed(p, GUIName, itemName);
					boolean owned = ownedRanks.contains(itemName);
					boolean reclaimable = configsFile.getBoolean(GUIName + ".ranks." + itemName + ".recaimable");
					
					if(passed && (!owned || (owned && reclaimable)))
					{
						if(!owned)
						{
							ownedRanks.add(itemName);
							database.set("players." + p.getName() + ".owned-ranks", ownedRanks);
						}
						if(!owned || (owned && reclaimable))
						{							
							for(String command : configsFile.getStringList(GUIName + ".ranks." + itemName + ".commands"))
							{
								Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("{player}", p.getName()));
							}
							for(String line : ColorUtils.color(configsFile.getStringList(GUIName + ".ranks." + itemName + ".rankup-message")))
							{
								line = line.replace("{player}", p.getName())
										.replace("{rank}", ColorUtils.color(configsFile.getString(GUIName + ".ranks." + itemName + ".name")));
								p.sendMessage(prefix + line);
							}
							p.closeInventory();
						}
					}
					else if(!passed && (!owned || (owned && reclaimable)))
					{
						for(String line : ColorUtils.color(messagesFile.getStringList("errors.conditions-not-met")))
						{
							p.sendMessage(prefix + line);
						}
					}
					else if(passed && owned && !reclaimable)
					{
						for(String line : ColorUtils.color(messagesFile.getStringList("errors.already-owned")))
						{
							p.sendMessage(prefix + line);
						}
					}
				}
			}
		}
	}
}
