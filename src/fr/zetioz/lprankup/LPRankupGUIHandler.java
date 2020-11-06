package fr.zetioz.lprankup;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.zetioz.lprankup.utils.Color;
import fr.zetioz.lprankup.utils.ConditionsChecker;
import fr.zetioz.lprankup.utils.ItemBuilder;

public class LPRankupGUIHandler implements Listener
{
	
	private YamlConfiguration messagesFile;
	private YamlConfiguration configsFile;
	private ConditionsChecker conditionChecker;
	private String prefix;
	
	public LPRankupGUIHandler()
	{
		try
		{
			messagesFile = LPRankupMain.getFilesManager().getSimpleYaml("messages");
			configsFile = LPRankupMain.getFilesManager().getSimpleYaml("configs");
			prefix = Color.color(messagesFile.getString("prefix"));
			conditionChecker = new ConditionsChecker(configsFile, LPRankupMain.getFilesManager().getSimpleYaml("database"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent e)
	{
		if(e.getClickedInventory() != null && e.getView().getTitle() != null && e.getView().getTitle().equals(Color.color(configsFile.getString("gui.title"))))
		{
			// Gui management
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			ItemStack border = null;
			try {			
				border = ItemBuilder.build(Material.valueOf(configsFile.getString("gui.borders.icon").toUpperCase()), Color.color(configsFile.getString("gui.borders.name")));
			}
			catch(IllegalArgumentException ex) {
				for(String line : messagesFile.getStringList("errors.invalid-icon"))
				{
					LPRankupMain.getPlugin().getLogger().severe(line.replace("{icon}", configsFile.getString("gui.borders.icon").toUpperCase()));
				}
				return;
			}
			if(e.getCurrentItem() != null && e.getCurrentItem() != border && e.getCurrentItem().getType() != Material.AIR)
			{
				Set<String> ranksList = configsFile.getConfigurationSection("ranks").getKeys(false);
				String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
				if(ranksList.contains(itemName) && conditionChecker.isPassed(p, itemName))
				{
					for(String command : configsFile.getStringList("ranks." + itemName + ".commands"))
					{
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("{player}", p.getName()));
					}
					for(String line : Color.color(configsFile.getStringList("ranks." + itemName + ".rankup-message")))
					{
						line = line.replace("{player}", p.getName())
								.replace("{rank}", Color.color(configsFile.getString("ranks." + itemName + ".name")));
						p.sendMessage(prefix + line);
					}
					p.closeInventory();
				}
			}
		}
	}
	
	public void openGUI(Player player)
	{
		List<String> rankList = new ArrayList<>(configsFile.getConfigurationSection("ranks").getKeys(false));
		int ranksCount = rankList.size();
		
		// Stop the GUI opening if the ranks are not setup yet
		if(ranksCount == 0)
		{
			for(String line : Color.color(messagesFile.getStringList("errors.no-ranks-available")))
			{
				player.sendMessage(prefix + line);
			}
			return;
		}
		
		double rows = Math.ceil(ranksCount / 7.0) + 2.0; // + 2 for the borders
		
		// Border Item
		ItemStack border = null;
		try {			
			border = ItemBuilder.build(Material.valueOf(configsFile.getString("gui.borders.icon").toUpperCase()), Color.color(configsFile.getString("gui.borders.name")));
		}
		catch(IllegalArgumentException ex) {
			for(String line : messagesFile.getStringList("errors.invalid-icon"))
			{
				LPRankupMain.getPlugin().getLogger().severe(line.replace("{icon}", configsFile.getString("gui.borders.icon").toUpperCase()));
			}
			return;
		}
		
		Inventory inv = Bukkit.createInventory(player, (int) (rows * 9), Color.color(configsFile.getString("gui.title")));
		Boolean addInner = true;
		for(int x = 0, y = 0, z = 0; x < rows * 9; x++)
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
						String upgradable = conditionChecker.isPassed(player, rankList.get(z)) ? Color.color(messagesFile.getString("upgradable")) : Color.color(messagesFile.getString("not-upgradable"));
						for(String loreLine : Color.color(configsFile.getStringList("ranks." + rankList.get(z) + ".lore"))) {
							loreLine = loreLine.replace("{upgradable}", upgradable).replace("{rank}", Color.color(configsFile.getString("ranks." + rankList.get(z) + ".name")));
							lore.add(loreLine);
						}
						ItemStack rankItem = ItemBuilder.build(Material.valueOf(configsFile.getString("ranks." + rankList.get(z) + ".icon").toUpperCase()),
								Color.color(rankList.get(z)),
								lore);
						inv.setItem(x, rankItem);
					}
				}
				catch(IllegalArgumentException ex)
				{
					for(String line : messagesFile.getStringList("errors.invalid-icon"))
					{
						LPRankupMain.getPlugin().getLogger().severe(line.replace("{icon}", configsFile.getString("ranks." + rankList.get(z) + ".icon").toUpperCase()));
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
