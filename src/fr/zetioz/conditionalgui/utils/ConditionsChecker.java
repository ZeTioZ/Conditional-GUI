package fr.zetioz.conditionalgui.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.zetioz.conditionalgui.ConditionalGUIMain;
import fr.zetioz.conditionalgui.enums.Conditions;

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
					case MINED_BLOCKS:
						if(database.getInt("players." + p.getName() + ".mined_blocks") >= configsFile.getInt("ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))
						{
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
						if(McMMOHook.isEnabled() && McMMOHook.getPlayerMcMMOLevel(p) >= configsFile.getInt("ranks." + rankName + ".conditions." + conditionsListRaw.get(i)))
						{
							conditionsRepected++;
						}
						else if(!McMMOHook.isEnabled())
						{
							ConditionalGUIMain.getPlugin().getLogger().warning("You are using a McMMO condition whitout having McMMO enabled on your server!");
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
