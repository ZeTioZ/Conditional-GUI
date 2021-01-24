package fr.zetioz.conditionalgui.utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public class GUIOpenerUtils
{	
	private GUIOpenerUtils() {}
	
	@SuppressWarnings("deprecation")
	public static void openGUI(Player player, String GUIName)
	{
		YamlConfiguration configsFile = null;
		YamlConfiguration messagesFile = null;
		YamlConfiguration database = null;
		
		try
		{
			configsFile = ConditionalGUIMain.getFilesManager().getSimpleYaml("configs");
			messagesFile = ConditionalGUIMain.getFilesManager().getSimpleYaml("messages");
			database = ConditionalGUIMain.getFilesManager().getSimpleYaml("database");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return;
		}

		final String prefix = ColorUtils.color(messagesFile.getString("prefix"));
		final ConditionsCheckerUtils conditionChecker = new ConditionsCheckerUtils(configsFile, database);
		
		List<String> rankList;
		
		if(configsFile.isConfigurationSection(GUIName + ".ranks"))
		{			
			rankList = new ArrayList<>(configsFile.getConfigurationSection(GUIName + ".ranks").getKeys(false));
		}
		else
		{
			for(String line : ColorUtils.color(messagesFile.getStringList("errors.no-ranks-available")))
			{
				player.sendMessage(prefix + line);
			}
			return;
		}
		int ranksCount = rankList.size();
		
		// Stop the GUI opening if the ranks are not setup yet
		if(ranksCount == 0)
		{
			for(String line : ColorUtils.color(messagesFile.getStringList("errors.no-ranks-available")))
			{
				player.sendMessage(prefix + line);
			}
			return;
		}
		
		double rows = Math.ceil(ranksCount / 7.0) + 2.0; // + 2 for the borders
		
		// Border Item
		ItemStack border = null;
		try {			
			border = ItemBuilderUtils.build(Material.valueOf(configsFile.getString("gui.borders.icon").toUpperCase()), ColorUtils.color(configsFile.getString("gui.borders.name")));
		}
		catch(IllegalArgumentException ex) {
			for(String line : messagesFile.getStringList("errors.invalid-icon"))
			{
				ConditionalGUIMain.getPlugin().getLogger().severe(line.replace("{icon}", configsFile.getString("gui.borders.icon").toUpperCase()));
			}
			return;
		}
		
		Inventory inv = Bukkit.createInventory(player, (int) (rows * 9), ColorUtils.color(configsFile.getString(GUIName + ".title")));
		Boolean addInner = true;
		int y = 0;
		int z = 0;
		for(int x = 0; x < rows * 9; x++)
		{
			if(y == 0 || y == rows - 1 || x % 9 == 0 || (x + 1) % 9 == 0)
			{
				inv.setItem(x, border);
			}
			else
			{
				try {
					
					/*
					 * Inner item is calculated and customized by the config and also by the fact if
					 * the player can or cannot upgrade to this rank with ConditionsCheck#isPassed
					 * 
					 * Here the z variable iterate throught the ranks list from 0 to size - 1
					 * 			x iterate throught the GUI slots from 0 to size - 1
					 * 			y take a count of the row where the cursor is to check if it's the top or bottom line
					 */
					if(addInner)
					{
						List<String> lore = new ArrayList<>();
						List<String> ownedRanks = database.getStringList("players." + player.getName() + ".owned-ranks");
						
						String upgradable;
						if(!ownedRanks.contains(rankList.get(z)) || configsFile.getBoolean(GUIName + ".ranks." + rankList.get(z) + ".recaimable"))
						{
							upgradable = conditionChecker.isPassed(player, GUIName, rankList.get(z)) ? ColorUtils.color(messagesFile.getString("upgradable")) : ColorUtils.color(messagesFile.getString("not-upgradable"));
						}
						else
						{
							upgradable = ColorUtils.color(messagesFile.getString("already-owned"));
						}
						
						for(String loreLine : ColorUtils.color(configsFile.getStringList(GUIName + ".ranks." + rankList.get(z) + ".lore"))) {
							loreLine = loreLine.replace("{upgradable}", upgradable).replace("{rank}", ColorUtils.color(configsFile.getString(GUIName + ".ranks." + rankList.get(z) + ".name")));
							lore.add(loreLine);
						}
						Material iconMaterial = Material.valueOf(configsFile.getString(GUIName + ".ranks." + rankList.get(z) + ".icon").toUpperCase());
						ItemStack rankItem = null;
						if(iconMaterial == Material.PLAYER_HEAD)
						{
							ItemStack iconHead = null;
							if(new String(Base64.getDecoder().decode(configsFile.getString(GUIName + ".ranks." + rankList.get(z) + ".head-texture").getBytes())).contains("http://textures.minecraft.net/texture/"))
							{
								iconHead = PlayerHeadUtils.getPlayerHead(configsFile.getString(GUIName + ".ranks." + rankList.get(z) + ".head-texture"));
							}
							else
							{								
								iconHead = PlayerHeadUtils.getPlayerHead(Bukkit.getOfflinePlayer(configsFile.getString(GUIName + ".ranks." + rankList.get(z) + ".head-texture")));
							}
							rankItem = ItemBuilderUtils.build(iconHead,
									ColorUtils.color(configsFile.getString(GUIName + ".ranks." + rankList.get(z) + ".display-name-color") + rankList.get(z)),
									lore);
						}
						else
						{							
							rankItem = ItemBuilderUtils.build(iconMaterial,
									ColorUtils.color(configsFile.getString(GUIName + ".ranks." + rankList.get(z) + ".display-name-color") + rankList.get(z)),
									lore);
						}
						inv.setItem(x, rankItem);
					}
				}
				catch(IllegalArgumentException ex)
				{
					for(String line : messagesFile.getStringList("errors.invalid-icon"))
					{
						ConditionalGUIMain.getPlugin().getLogger().severe(line.replace("{icon}", configsFile.getString(GUIName + ".ranks." + rankList.get(z) + ".icon").toUpperCase()));
						ex.printStackTrace();
					}
				}
				if(z < rankList.size() - 1)
				{					
					z++;
				}
				else
				{
					addInner = false;
				}
			}
			if((x + 1) % 9 == 0)
			{
				y++;
			}
		}
		player.openInventory(inv);
	}
}
