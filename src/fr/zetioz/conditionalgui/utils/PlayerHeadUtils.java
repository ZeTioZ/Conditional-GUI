package fr.zetioz.conditionalgui.utils;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public final class PlayerHeadUtils {

	private PlayerHeadUtils() {}
	
	public static ItemStack getPlayerHead(OfflinePlayer offlinePlayer)
	{
		ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
		
		skullMeta.setOwningPlayer(offlinePlayer);
		skullItem.setItemMeta(skullMeta);
		
		return skullItem;
	}
	
    public static ItemStack getPlayerHead(String textureURL)
    {
	    ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
	    
	    if(textureURL.isEmpty())return skullItem;
	 
	 
	    SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
	    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
	    profile.getProperties().put("textures", new Property("textures", textureURL));
	    try
	    {
	    	Field profileField = skullMeta.getClass().getDeclaredField("profile");
	        profileField.setAccessible(true);
	        profileField.set(skullMeta, profile);
	    }
	    catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
	    {
	        e.printStackTrace();
	    }
	    skullItem.setItemMeta(skullMeta);
	    return skullItem;
    }
}
