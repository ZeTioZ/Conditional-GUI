package fr.zetioz.conditionalgui.hooks;

import org.bukkit.entity.Player;

import com.gmail.nossr50.api.ExperienceAPI;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public final class McMMOHook {
	
	private McMMOHook()
	{
		ConditionalGUIMain.getPlugin().getLogger().info("McMMO Hook Constructor");
	}
	
	public static int getPlayerMcMMOLevel(Player player)
	{
		return ExperienceAPI.getPowerLevel(player);
	}
}
