package fr.zetioz.conditionalgui.hooks;

import java.math.BigDecimal;

import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public final class SuperiorSkyblockHook {
	
	private SuperiorSkyblockHook()
	{
		ConditionalGUIMain.getPlugin().getLogger().info("Superior Skyblock Hook Constructor");
	}
	
	public static String getIslandGenerator(Player p)
	{
		SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(p);
		Island pIsland = sPlayer.getIsland();
		return pIsland.getSchematicName();
	}
	
	public static BigDecimal getIslandLevel(Player p)
	{
		SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(p);
		Island pIsland = sPlayer.getIsland();
		return pIsland.getIslandLevel();
	}
	
	public static int getIslandSize(Player p)
	{
		SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(p);
		Island pIsland = sPlayer.getIsland();
		return pIsland.getIslandSize();
	}
}
