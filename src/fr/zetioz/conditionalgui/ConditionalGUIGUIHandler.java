package fr.zetioz.conditionalgui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Base64;
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

import fr.zetioz.conditionalgui.utils.ColorUtils;
import fr.zetioz.conditionalgui.utils.ConditionsChecker;
import fr.zetioz.conditionalgui.utils.ItemBuilder;
import fr.zetioz.conditionalgui.utils.PlayerHeadUtils;

public class ConditionalGUIGUIHandler implements Listener
{
	
	private YamlConfiguration messagesFile;
	private YamlConfiguration configsFile;
	private YamlConfiguration database;
	private ConditionsChecker conditionChecker;
	private String prefix;
	
	public ConditionalGUIGUIHandler()
	{
		try
		{
			messagesFile = ConditionalGUIMain.getFilesManager().getSimpleYaml("messages");
			configsFile = ConditionalGUIMain.getFilesManager().getSimpleYaml("configs");
			database = ConditionalGUIMain.getFilesManager().getSimpleYaml("database");
			prefix = ColorUtils.color(messagesFile.getString("prefix"));
			conditionChecker = new ConditionsChecker(configsFile, ConditionalGUIMain.getFilesManager().getSimpleYaml("database"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onGUIClick(InventoryClickEvent e)
	{
		if(e.getClickedInventory() != null && e.getView().getTitle() != null && e.getView().getTitle().equals(ColorUtils.color(configsFile.getString("gui.title"))))
		{
			// Gui management
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			ItemStack border = null;
			try {			
				border = ItemBuilder.build(Material.valueOf(configsFile.getString("gui.borders.icon").toUpperCase()), ColorUtils.color(configsFile.getString("gui.borders.name")));
			}
			catch(IllegalArgumentException ex) {
				for(String line : messagesFile.getStringList("errors.invalid-icon"))
				{
					ConditionalGUIMain.getPlugin().getLogger().severe(line.replace("{icon}", configsFile.getString("gui.borders.icon").toUpperCase()));
				}
				return;
			}
			if(e.getCurrentItem() != null && e.getCurrentItem() != border && e.getCurrentItem().getType() != Material.AIR)
			{
				Set<String> ranksList = configsFile.getConfigurationSection("ranks").getKeys(false);
				String itemName = ColorUtils.discolor(e.getCurrentItem().getItemMeta().getDisplayName());
				List<String> ownedRanks = database.getStringList("players." + p.getName() + ".owned-ranks");
				if(ranksList.contains(itemName) && conditionChecker.isPassed(p, itemName))
				{
					if(!ownedRanks.contains(itemName))
					{
						ownedRanks.add(itemName);
						database.set("players." + p.getName() + ".owned-ranks", ownedRanks);
					}
					else if(ownedRanks.contains(itemName) && !configsFile.getBoolean("ranks." + itemName + ".recaimable"))
					{
						for(String line : ColorUtils.color(messagesFile.getStringList("errors.already-owned")))
						{
							p.sendMessage(prefix + line);
						}
						return;
					}
					for(String command : configsFile.getStringList("ranks." + itemName + ".commands"))
					{
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("{player}", p.getName()));
					}
					for(String line : ColorUtils.color(configsFile.getStringList("ranks." + itemName + ".rankup-message")))
					{
						line = line.replace("{player}", p.getName())
								.replace("{rank}", ColorUtils.color(configsFile.getString("ranks." + itemName + ".name")));
						p.sendMessage(prefix + line);
					}
					p.closeInventory();
				}
				else if(!conditionChecker.isPassed(p, itemName) && (!ownedRanks.contains(itemName) || (ownedRanks.contains(itemName) && configsFile.getBoolean("ranks." + itemName + ".recaimable"))))
				{
					for(String line : ColorUtils.color(messagesFile.getStringList("errors.conditions-not-met")))
					{
						p.sendMessage(prefix + line);
					}
				}
				else if(!conditionChecker.isPassed(p, itemName) && ownedRanks.contains(itemName) && !configsFile.getBoolean("ranks." + itemName + ".recaimable"))
				{
					for(String line : ColorUtils.color(messagesFile.getStringList("errors.already-owned")))
					{
						p.sendMessage(prefix + line);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void openGUI(Player player)
	{
		List<String> rankList;
		if(configsFile.isConfigurationSection("ranks"))
		{			
			rankList = new ArrayList<>(configsFile.getConfigurationSection("ranks").getKeys(false));
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
			border = ItemBuilder.build(Material.valueOf(configsFile.getString("gui.borders.icon").toUpperCase()), ColorUtils.color(configsFile.getString("gui.borders.name")));
		}
		catch(IllegalArgumentException ex) {
			for(String line : messagesFile.getStringList("errors.invalid-icon"))
			{
				ConditionalGUIMain.getPlugin().getLogger().severe(line.replace("{icon}", configsFile.getString("gui.borders.icon").toUpperCase()));
			}
			return;
		}
		
		Inventory inv = Bukkit.createInventory(player, (int) (rows * 9), ColorUtils.color(configsFile.getString("gui.title")));
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
						List<String> ownedRanks = database.getStringList("players." + player.getName() + ".owned-ranks");
						String upgradable;
						if(!ownedRanks.contains(rankList.get(z)) || configsFile.getBoolean("ranks." + rankList.get(z) + ".recaimable"))
						{
							upgradable = conditionChecker.isPassed(player, rankList.get(z)) ? ColorUtils.color(messagesFile.getString("upgradable")) : ColorUtils.color(messagesFile.getString("not-upgradable"));
						}
						else
						{
							upgradable = ColorUtils.color(messagesFile.getString("already-owned"));
						}
						for(String loreLine : ColorUtils.color(configsFile.getStringList("ranks." + rankList.get(z) + ".lore"))) {
							loreLine = loreLine.replace("{upgradable}", upgradable).replace("{rank}", ColorUtils.color(configsFile.getString("ranks." + rankList.get(z) + ".name")));
							lore.add(loreLine);
						}
						Material iconMaterial = Material.valueOf(configsFile.getString("ranks." + rankList.get(z) + ".icon").toUpperCase());
						ItemStack rankItem = null;
						if(iconMaterial == Material.PLAYER_HEAD)
						{
							ItemStack iconHead = null;
							if(new String(Base64.getDecoder().decode(configsFile.getString("ranks." + rankList.get(z) + ".head-texture").getBytes())).contains("http://textures.minecraft.net/texture/"))
							{
								iconHead = PlayerHeadUtils.getPlayerHead(configsFile.getString("ranks." + rankList.get(z) + ".head-texture"));
							}
							else
							{								
								iconHead = PlayerHeadUtils.getPlayerHead(Bukkit.getOfflinePlayer(configsFile.getString("ranks." + rankList.get(z) + ".head-texture")));
							}
							rankItem = ItemBuilder.build(iconHead,
									ColorUtils.color(configsFile.getString("ranks." + rankList.get(z) + ".display-name-color") + rankList.get(z)),
									lore);
						}
						else
						{							
							rankItem = ItemBuilder.build(iconMaterial,
									ColorUtils.color(configsFile.getString("ranks." + rankList.get(z) + ".display-name-color") + rankList.get(z)),
									lore);
						}
						inv.setItem(x, rankItem);
					}
				}
				catch(IllegalArgumentException ex)
				{
					for(String line : messagesFile.getStringList("errors.invalid-icon"))
					{
						ConditionalGUIMain.getPlugin().getLogger().severe(line.replace("{icon}", configsFile.getString("ranks." + rankList.get(z) + ".icon").toUpperCase()));
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
