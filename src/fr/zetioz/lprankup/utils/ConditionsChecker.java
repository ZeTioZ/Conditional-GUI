package fr.zetioz.lprankup.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.zetioz.lprankup.LPRankupMain;
import fr.zetioz.lprankup.enums.Conditions;

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
			List<String> conditionsList = new ArrayList<>(configsFile.getConfigurationSection("ranks." + rankName + ".conditions").getKeys(false));
			conditionsList.replaceAll(String::toUpperCase);
			int conditionsToRespect = 0;
			for(String condition : conditionsList)
			{
				if(Conditions.valueOf(condition) != null && Conditions.valueOf(condition) != Conditions.NOT_A_CONDITION && Conditions.valueOf(condition) != Conditions.NO_CONDITION)
				{
					conditionsToRespect++;
				}
			}
			int conditionsRepected = 0;
			for(String condition : conditionsList)
			{
				Conditions conditionToCheck = Conditions.valueOf(condition) != null ? Conditions.valueOf(condition) : Conditions.NOT_A_CONDITION;
				switch(conditionToCheck)
				{
					case KILL:
						if(database.getInt("players." + p.getName() + ".kills") >= configsFile.getInt("ranks." + rankName + ".conditions.kills"))
						{
							conditionsRepected++;
						}
						break;
					case MONEY:
						if(LPRankupMain.getEconomy().getBalance(p) >= configsFile.getDouble("ranks." + rankName + ".conditions.money"))
						{
							conditionsRepected++;
						}
						break;
					case XP:
						if(p.getTotalExperience() >= configsFile.getDouble("ranks." + rankName + ".conditions.xp"))
						{
							conditionsRepected++;
						}
						break;
					case MINED_BLOCKS:
						if(database.getInt("players." + p.getName() + ".mined_blocks") >= configsFile.getInt("ranks." + rankName + ".conditions.mined_blocks"))
						{
							conditionsRepected++;
						}
						break;
					case RANK_NEEDED:
						if(LPRankupMain.getPermissions().playerInGroup(p, configsFile.getString("ranks." + rankName + ".conditions.rank_needed")))
						{
							conditionsRepected++;
						}
						break;
					default:
				}	
			}
			if(conditionsRepected == conditionsToRespect)
			{
				return true;
			}
		}
		return false;
	}
}
