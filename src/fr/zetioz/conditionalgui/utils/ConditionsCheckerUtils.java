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

public final class ConditionsCheckerUtils {

	private YamlConfiguration configsFile;
	private YamlConfiguration database;
	
	/* Injecting the Yaml Configuration throught the constructor 
	 * to be sure to use the same instance of configuration as the GUI */
	public ConditionsCheckerUtils(YamlConfiguration configsFile, YamlConfiguration database)
	{
		this.configsFile = configsFile;
		this.database = database;
	}
	
	public boolean isPassed(Player p, String GUIName, String rankName)
	{
		if(configsFile.isConfigurationSection(GUIName + ".ranks." + rankName) && configsFile.isConfigurationSection(GUIName + ".ranks." + rankName + ".conditions"))
		{
			List<String> conditionsListRaw = new ArrayList<>(configsFile.getConfigurationSection(GUIName + ".ranks." + rankName + ".conditions").getKeys(false));
			List<String> conditionsListUpper = new ArrayList<>(conditionsListRaw);
			conditionsListUpper.replaceAll(String::toUpperCase);
			int conditionsToRespect = 0;
			for(String condition : conditionsListUpper)
			{
				String[] conditionSplit = null;
				conditionSplit = AdvancedCheckUtils.mathDecoder(condition);
				condition = conditionSplit != null ? conditionSplit[1] : condition;
				
				if(EnumCheckUtils.isValidEnum(Conditions.class, condition) && Conditions.valueOf(condition) != Conditions.NOT_A_CONDITION && Conditions.valueOf(condition) != Conditions.NO_CONDITION)
				{
					conditionsToRespect++;
				}
			}
			int i = 0;
			int conditionsRepected = 0;
			for(String condition : conditionsListUpper)
			{
				String[] conditionSplit = null;
				String mathCondition = null;
				conditionSplit = AdvancedCheckUtils.mathDecoder(condition);
				mathCondition = conditionSplit != null ? conditionSplit[0] : null;
				condition = conditionSplit != null ? conditionSplit[1] : condition;
				
				Conditions conditionToCheck = EnumCheckUtils.isValidEnum(Conditions.class, condition) ? Conditions.valueOf(condition) : Conditions.NOT_A_CONDITION;
				switch(conditionToCheck)
				{
					case KILL:
						int pKills = database.getInt("players." + p.getName() + ".kills"); // Player kills
						int cKills = configsFile.getInt(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i)); // Configuration kills
						if(AdvancedCheckUtils.checkMath(mathCondition, pKills, cKills))
						{
							conditionsRepected++;
						}
						break;
					case MONEY:
						double pBalance = ConditionalGUIMain.getEconomy().getBalance(p);
						double cBalance = configsFile.getDouble(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i));
						if(AdvancedCheckUtils.checkMath(mathCondition, pBalance, cBalance))
						{
							conditionsRepected++;
						}
						break;
					case XP:
						double pEXP = p.getTotalExperience();
						double cEXP = configsFile.getDouble(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i));
						if(AdvancedCheckUtils.checkMath(mathCondition, pEXP, cEXP))
						{
							conditionsRepected++;
						}
						break;
					case MINED_TOTAL:
						int pTotalMined = database.getInt("players." + p.getName() + ".mined_total");
						int cTotalMined = configsFile.getInt(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i));
						if(AdvancedCheckUtils.checkMath(mathCondition, pTotalMined, cTotalMined))
						{
							conditionsRepected++;
						}
						break;
					case MINED_BLOCKS:
						if(configsFile.contains(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i) + ".blocks_list")
							&& !configsFile.getStringList(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i) + ".blocks_list").isEmpty())
						{
							boolean conditionChecked = true;
							int pBlockMined = 0;
							int cBlockMined = 0;
							for(String blockDecoder : configsFile.getStringList(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i) + ".blocks_list"))
							{
								String[] decodedCondition = blockDecoder.split(":");
								try
								{
									Material decodedMat = Material.valueOf(decodedCondition[0]);
									if(decodedMat.isBlock())
									{
										pBlockMined = database.getInt("players." + p.getPlayer().getName() + ".mined_" + decodedCondition[0].toLowerCase());
										cBlockMined = Integer.valueOf(decodedCondition[1]);
										if(!AdvancedCheckUtils.checkMath(mathCondition, pBlockMined, cBlockMined))
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
								catch(NumberFormatException ex)
								{
									ConditionalGUIMain.getPlugin().getLogger().severe("You seem using a wrong integer input into a mined block condition! Please change that in the config file.");
									ConditionalGUIMain.getPlugin().getLogger().severe("Block ignored. Wrong integer in config file: " + blockDecoder);
									ex.printStackTrace();
								}
								catch(IllegalArgumentException ex)
								{
									ConditionalGUIMain.getPlugin().getLogger().severe("You seem using a mined block condition on a block that's not an actual material type! Please change that in the config file.");
									ConditionalGUIMain.getPlugin().getLogger().severe("Material ignored. Wrong material in config file: " + decodedCondition[0]);
									ex.printStackTrace();
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
						if(ConditionalGUIMain.getPermissions().playerInGroup(p, configsFile.getString(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i))))
						{
							conditionsRepected++;
						}
						break;
					case PERMISSION_NEEDED:
						boolean permissionPassed = true;
						for(String permission : configsFile.getStringList(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))
						{							
							if(!p.hasPermission(permission))
							{
								permissionPassed = false;
								break;
							}
						}
						if(permissionPassed)
						{
							conditionsRepected++;
						}
					case MCMMO_LEVEL:
						if(ConditionalGUIMain.getEnabledDependencies().contains("mcMMO"))
						{
							int pMcMMOLevel = McMMOHook.getPlayerMcMMOLevel(p);
							int cMcMMOLevel = configsFile.getInt(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i));
							if(AdvancedCheckUtils.checkMath(mathCondition, pMcMMOLevel, cMcMMOLevel))
							{								
								conditionsRepected++;
							}
						}
						else
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
							BigDecimal configIslandLevel = new BigDecimal(configsFile.getInt(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i)));
							if(AdvancedCheckUtils.checkMath(mathCondition, islandLevel, configIslandLevel))
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
							if(schematicName.equals(configsFile.getString(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i))))
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
						if(ConditionalGUIMain.getEnabledDependencies().contains("SuperiorSkyblock")
							|| ConditionalGUIMain.getEnabledDependencies().contains("SuperiorSkyblock2"))
						{
							int iSize = SuperiorSkyblockHook.getIslandSize(p);
							int cSize = configsFile.getInt(GUIName + ".ranks." + rankName + ".conditions." + conditionsListRaw.get(i));
							if(AdvancedCheckUtils.checkMath(mathCondition, iSize, cSize))
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
						ConditionalGUIMain.getPlugin().getLogger().warning("You are using an invalid condition: " + condition + "! Please, remove it from configuration file.");
						ConditionalGUIMain.getPlugin().getLogger().warning("The condition will be ignored!");
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
