package fr.zetioz.conditionalgui.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public final class Color
{
	private Color() {}
	
	/*
	 * A simple way to color a single string
	 * Where @textToColor is indeed the line of text to color
	 */
	
	public static final String color(String textToColor)
	{
		return ChatColor.translateAlternateColorCodes('&', textToColor);
	}
	
	public static final List<String> color(List<String> textToColor)
	{
		List<String> coloredText = new ArrayList<>();
		
		for(String line : textToColor)
		{
			line = ChatColor.translateAlternateColorCodes('&', line);
			coloredText.add(line);
		}
		return coloredText;
	}
}
