package fr.zetioz.conditionalgui.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemBuilderUtils {
	
	private ItemBuilderUtils(){}
	
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
	
	/**
	 * @param itemStack : Item stack to modify the item stack from
	 * @param itemName : The display name of the item stack
	 * @param itemLore : The lore of the item stack
	*/
	public static final ItemStack build(ItemStack itemStack, String itemName, List<String> itemLore)
	{
		ItemMeta iMeta = itemStack.getItemMeta();
		iMeta.setDisplayName(itemName);
		iMeta.setLore(itemLore);
		itemStack.setItemMeta(iMeta);
		return itemStack;
	}
	
	/**
	 * @param itemStack : Item stack to modify the item stack from
	 * @param itemName : The display name of the item stack
	*/
	public static final ItemStack build(ItemStack itemStack, String itemName)
	{
		ItemMeta iMeta = itemStack.getItemMeta();
		iMeta.setDisplayName(itemName);
		itemStack.setItemMeta(iMeta);
		return itemStack;
	}
}
