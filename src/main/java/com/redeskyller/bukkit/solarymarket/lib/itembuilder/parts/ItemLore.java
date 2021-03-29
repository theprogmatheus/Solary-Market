package com.redeskyller.bukkit.solarymarket.lib.itembuilder.parts;

import org.bukkit.inventory.ItemStack;

public class ItemLore implements ItemPart {
	private String lore;

	public ItemLore(String constructor)
	{
		try {
			if (constructor.startsWith("lore:"))
				this.lore = constructor.replace("lore:", "").replace("&", "§").replace("_", " ");
		} catch (Exception localException) {
		}
	}

	@Override
	public ItemStack send(ItemStack item)
	{
		if ((item != null) && (!item.getType().equals(org.bukkit.Material.AIR)) && (getLore() != null)) {
			java.util.List<String> lore = new java.util.ArrayList<>();
			org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
			if (meta.getLore() != null)
				lore = meta.getLore();
			lore.add(getLore());
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public String getLore()
	{
		return this.lore;
	}
}
