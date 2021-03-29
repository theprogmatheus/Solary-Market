package com.redeskyller.bukkit.solarymarket.lib.itembuilder.parts;

import org.bukkit.inventory.ItemStack;

public class ItemName implements ItemPart {
	private String name;

	public ItemName(String constructor)
	{
		try {
			if (constructor.startsWith("name:"))
				this.name = constructor.replace("name:", "").replace("&", "§").replace("_", " ");
		} catch (Exception localException) {
		}
	}

	@Override
	public ItemStack send(ItemStack item)
	{
		if ((item != null) && (!item.getType().equals(org.bukkit.Material.AIR)) && (getName() != null)) {
			org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(getName());
			item.setItemMeta(meta);
		}
		return item;
	}

	public String getName()
	{
		return this.name;
	}
}
