package com.redeskyller.bukkit.solarymarket.lib.itembuilder.parts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFlag implements ItemPart {

	private org.bukkit.inventory.ItemFlag itemFlag;

	public ItemFlag(String constructor)
	{
		try {
			if (constructor.startsWith("flag:"))
				this.itemFlag = org.bukkit.inventory.ItemFlag.valueOf(constructor.replace("flag:", "").toUpperCase());
		} catch (Exception localException) {
			this.itemFlag = null;
		}
	}

	@Override
	public ItemStack send(ItemStack item)
	{
		if ((item != null) && (item.getType() != Material.AIR) && (this.itemFlag != null)) {
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.addItemFlags(this.itemFlag);
			item.setItemMeta(itemMeta);
		}
		return item;
	}

}
