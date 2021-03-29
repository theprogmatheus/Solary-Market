package com.redeskyller.bukkit.solarymarket.lib.itembuilder.parts;

import org.bukkit.inventory.ItemStack;

public class ItemSkull implements ItemPart {

	private String name;

	public ItemSkull(String constructor)
	{
		try {
			if (constructor.startsWith("skull:"))
				this.name = constructor.replace("skull:", "").replace("&", "§");
		} catch (Exception localException) {
		}
	}

	@Override
	public ItemStack send(ItemStack item)
	{

		if ((item != null) && (!item.getType().equals(org.bukkit.Material.AIR))
				&& (item.getType().equals(org.bukkit.Material.SKULL_ITEM)) && (item.getDurability() == 3)
				&& (getName() != null)) {
			org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) item.getItemMeta();
			meta.setOwner(getName());
			item.setItemMeta(meta);
		}
		return item;
	}

	public String getName()
	{
		return this.name;
	}
}
