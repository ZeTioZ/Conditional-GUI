package fr.zetioz.conditionalgui.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemBuilder {
	
	private ItemBuilder(){}
	
	/**
	 * @param itemMaterial : Material to build the item stack from
	 * @param itemName : The display name of the item stack
	 * @param itemLore : The lore of the item stack
	*/
	public static final ItemStack build(Material itemMaterial, String itemName, List<String> itemLore)
	{
		ItemStack itemToBuild = new ItemStack(itemMaterial);
		ItemMeta iMeta = itemToBuild.getItemMeta();
		iMeta.setDisplayName(itemName);
		iMeta.setLore(itemLore);
		itemToBuild.setItemMeta(iMeta);
		return itemToBuild;
	}
	
	/**
	 * @param itemMaterial : Material to build the item stack from
	 * @param itemName : The display name of the item stack
	*/
	public static final ItemStack build(Material itemMaterial, String itemName)
	{
		ItemStack itemToBuild = new ItemStack(itemMaterial);
		ItemMeta iMeta = itemToBuild.getItemMeta();
		iMeta.setDisplayName(itemName);
		itemToBuild.setItemMeta(iMeta);
		return itemToBuild;
	}
}
