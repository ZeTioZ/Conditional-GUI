package fr.zetioz.conditionalgui.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import fr.zetioz.conditionalgui.ConditionalGUIMain;

public final class McMMOHook {
	
	private McMMOHook()
	{
		ConditionalGUIMain.getPlugin().getLogger().info("McMMO Hook Constructor");
	}
	
	public static Boolean isEnabled()
	{
		return ConditionalGUIMain.getPlugin().getServer().getPluginManager().isPluginEnabled("mcMMO");
	}
	
	public static int getPlayerMcMMOLevel(Player player)
	{
		try {
			Class<?> ExpApiClass = Class.forName("com.gmail.nossr50.api.ExperienceAPI");
			Constructor<?> ExpAPIConstructor = ExpApiClass.getDeclaredConstructor();
			ExpAPIConstructor.setAccessible(true);
			Object ExpAPI = ExpAPIConstructor.newInstance(null);
			Method getPowerLevel = ExpApiClass.getDeclaredMethod("getPowerLevel", Player.class);
			return (int) getPowerLevel.invoke(ExpAPI, player);
		}
		catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex)
		{
			ConditionalGUIMain.getPlugin().getLogger().severe("You're using a McMMO related class while McMMO is not enabled!");
			ex.printStackTrace();
		}
		return 0;
	}
}
