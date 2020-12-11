package fr.zetioz.conditionalgui.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.zetioz.conditionalgui.ConditionalGUIMain;
import fr.zetioz.conditionalgui.enums.Conditions;
import fr.zetioz.conditionalgui.hooks.McMMOHook;
import fr.zetioz.conditionalgui.hooks.SuperiorSkyblockHook;

public final class ConditionsChecker {

	private YamlConfiguration configsFile;
	private YamlConfiguration database;
	
	/* Injecting the Yaml Configuration throught the constructor 
	 * to be sure to use the same instance of configuration as the GUI */
	public ConditionsChecker(YamlConfiguration configsFile, YamlConfiguration database)
	{
		this.configsFile = configsFile;
		this.database = database;
	}
	
	public boolean isPassed(Player p, String rankName)
	{
		if(configsFile.isConfigurationSection("ranks." + rankName) && configsFile.isConfigurationSection("ranks." + rankName + ".conditions"))
		{
			List<String> conditionsListRaw = new ArrayList<>(configsFile.getConfigurationSection("ranks." + rankName + ".conditions").getKeys(false));
			List<String> conditionsListUpper = new ArrayList<>(conditionsListRaw);
			conditionsListUpper.replaceAll(String::toUpperCase);
			int conditionsToRespect = 0;
			for(String condition : conditionsListUpper)
			{
				if(Conditions.valueOf(condition) != null && Conditions.valueOf(condition) != Conditions.NOT_A_CONDITION && Conditions.valueOf(condition) != Conditions.NO_CONDITION)
				{
					conditionsToRespect++;
				}
			}
			int i = 0;
			int conditionsRepected = 0;
			for(String condition : conditionsListUpper)
			{
				Conditions conditionToCheck = Conditions.valueOf(condition) != null ? Conditions.valueOf(condition) : Conditions.NOT_A_CONDITION;
				switch(conditionToCheck)
				{
					case KILL:
						System.out.println("KILL: " + conditionsListRaw.get(i));
						if(database.getInt("players." + p.getName() + ".kills") >= configsFile.getInt("ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))
						{
							conditionsRepected++;
						}
						break;
					case MONEY:
						if(ConditionalGUIMain.getEconomy().getBalance(p) >= configsFile.getDouble("ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))
						{
							conditionsRepected++;
						}
						break;
					case XP:
						if(p.getTotalExperience() >= configsFile.getDouble("ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))
						{
							conditionsRepected++;
						}
						break;
					case MINED_TOTAL:
						if(database.getInt("players." + p.getName() + ".mined_total") >= configsFile.getInt("ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))
						{
							conditionsRepected++;
						}
						break;
					case MINED_BLOCKS:
						if(configsFile.contains("ranks." + rankName + ".conditions." + conditionsListRaw.get(i) + ".blocks_list")
							&& !configsFile.getStringList("ranks." + rankName + ".conditions." + conditionsListRaw.get(i) + ".blocks_list").isEmpty())
						{
							boolean conditionChecked = true;
							for(String blockDecoder : configsFile.getStringList("ranks." + rankName + ".conditions." + conditionsListRaw.get(i) + ".blocks_list"))
							{
								String[] decodedCondition = blockDecoder.split(":");
								try
								{								
									if(Material.valueOf(decodedCondition[0]) != null)
									{
										if(Material.valueOf(decodedCondition[0]).isBlock())
										{											
											if(database.getInt("players." + p.getPlayer().getName() + ".mined_" + decodedCondition[0].toLowerCase()) < Integer.valueOf(decodedCondition[1]))
											{
												conditionChecked = false;
												break;
											}
										}
										else
										{
											ConditionalGUIMain.getPlugin().getLogger().severe("You seem using a mined block condition on a block that's not an actual breakable block! Please change that in the config file.");
											ConditionalGUIMain.getPlugin().getLogger().severe("Block ignored. Wrong material in config file: " + decodedCondition[0]);
										}
									}
									else
									{
										ConditionalGUIMain.getPlugin().getLogger().severe("You seem using a mined block condition on a block that's not an actual material type! Please change that in the config file.");
										ConditionalGUIMain.getPlugin().getLogger().severe("Material ignored. Wrong material in config file: " + decodedCondition[0]);
									}
								}
								catch(NumberFormatException ex)
								{
									ConditionalGUIMain.getPlugin().getLogger().severe("You seem using a wrong integer input into a mined block condition! Please change that in the config file.");
									ConditionalGUIMain.getPlugin().getLogger().severe("Block ignored. Wrong integer in config file: " + blockDecoder);
								}
							}
							if(conditionChecked)
							{
								conditionsRepected++;
							}
						}
						else
						{
							ConditionalGUIMain.getPlugin().getLogger().severe("You seem using a mined blocks condition but there are no blocks in the blocks list to watch. Please add blocks to watch in the list!");
							ConditionalGUIMain.getPlugin().getLogger().severe("This condition has been ignored!");
							conditionsRepected++;
						}
						break;
					case RANK_NEEDED:
						if(ConditionalGUIMain.getPermissions().playerInGroup(p, configsFile.getString("ranks." + rankName + ".conditions." + conditionsListRaw.get(i))))
						{
							conditionsRepected++;
						}
						break;
					case MCMMO_LEVEL:
						if(ConditionalGUIMain.getEnabledDependencies().contains("mcMMO") && McMMOHook.getPlayerMcMMOLevel(p) >= configsFile.getInt("ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))
						{
							conditionsRepected++;
						}
						else if(!ConditionalGUIMain.getEnabledDependencies().contains("mcMMO"))
						{
							ConditionalGUIMain.getPlugin().getLogger().warning("You are using a McMMO condition whitout having McMMO enabled on your server!");
							ConditionalGUIMain.getPlugin().getLogger().warning("The condition will be ignored!");
							conditionsRepected++;
						}
						break;
					case ISLAND_LEVEL:
						if(ConditionalGUIMain.getEnabledDependencies().contains("SuperiorSkyblock"))
						{
							BigDecimal islandLevel = SuperiorSkyblockHook.getIslandLevel(p);
							if(islandLevel.compareTo(new BigDecimal(configsFile.getInt("ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))) >= 0)
							{
								conditionsRepected++;
							}
						}
						else
						{
							ConditionalGUIMain.getPlugin().getLogger().warning("You are using a SuperiorSkyblock condition whitout having SuperiorSkyblock enabled on your server!");
							ConditionalGUIMain.getPlugin().getLogger().warning("The condition will be ignored!");
							conditionsRepected++;
						}
						break;
					case ISLAND_GENERATOR:
						if(ConditionalGUIMain.getEnabledDependencies().contains("SuperiorSkyblock"))
						{
							String schematicName = SuperiorSkyblockHook.getIslandGenerator(p);
							if(schematicName.equals(configsFile.getString("ranks." + rankName + ".conditions." + conditionsListRaw.get(i))))
							{
								conditionsRepected++;
							}
						}
						else
						{
							ConditionalGUIMain.getPlugin().getLogger().warning("You are using a SuperiorSkyblock condition whitout having SuperiorSkyblock enabled on your server!");
							ConditionalGUIMain.getPlugin().getLogger().warning("The condition will be ignored!");
							conditionsRepected++;
						}
						break;
					case ISLAND_SIZE:
						if(ConditionalGUIMain.getEnabledDependencies().contains("SuperiorSkyblock"))
						{
							int iSize = SuperiorSkyblockHook.getIslandSize(p);
							if(iSize >= configsFile.getInt("ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))
							{
								conditionsRepected++;
							}
						}
						else
						{
							ConditionalGUIMain.getPlugin().getLogger().warning("You are using a SuperiorSkyblock condition whitout having SuperiorSkyblock enabled on your server!");
							ConditionalGUIMain.getPlugin().getLogger().warning("The condition will be ignored!");
							conditionsRepected++;
						}
						break;
					default:
				}
				i++;
			}
			if(conditionsRepected == conditionsToRespect)
			{
				return true;
			}
		}
		return false;
	}
}
