package com.redeskyller.bukkit.solarymarket.lib.nbt;

import com.redeskyller.bukkit.solarymarket.util.Reflection;

public class NBTItem {
	private org.bukkit.inventory.ItemStack item;
	private Reflection reflection;

	public NBTItem()
	{
		this.reflection = new Reflection();
	}

	public NBTItem(org.bukkit.inventory.ItemStack item)
	{
		this.reflection = new Reflection();
		setItem(item);
	}

	public Reflection getReflection()
	{
		return this.reflection;
	}

	public void setItem(org.bukkit.inventory.ItemStack item)
	{
		this.item = item;
	}

	public org.bukkit.inventory.ItemStack getItem()
	{
		return this.item;
	}

	public NBTTagCompound getTag()
	{
		NBTTagCompound tag = new NBTTagCompound();
		try {
			Object nmsItem = getReflection().getObcClass("inventory.CraftItemStack")
					.getMethod("asNMSCopy", new Class[] { org.bukkit.inventory.ItemStack.class })
					.invoke(new Object[0], new Object[] { getItem() });
			Object nmsItemTag = nmsItem.getClass().getMethod("getTag", new Class[0]).invoke(nmsItem, new Object[0]);
			if (nmsItemTag != null)
				tag.setNbtTag(nmsItemTag);
			this.item =

					((org.bukkit.inventory.ItemStack) getReflection().getObcClass("inventory.CraftItemStack")
							.getMethod("asBukkitCopy", new Class[] { getReflection().getNmsClass("ItemStack") })
							.invoke(new Object[0], new Object[] { nmsItem }));
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return tag;
	}

	public void setTag(NBTTagCompound tag)
	{
		try {
			if (tag != null) {
				Object nmsItem = getReflection().getObcClass("inventory.CraftItemStack")
						.getMethod("asNMSCopy", new Class[] { org.bukkit.inventory.ItemStack.class })
						.invoke(new Object[0], new Object[] { getItem() });
				nmsItem.getClass().getMethod("setTag", new Class[] { getReflection().getNmsClass("NBTTagCompound") })
						.invoke(nmsItem, new Object[] { tag.getNbtTag() });
				this.item =

						((org.bukkit.inventory.ItemStack) getReflection().getObcClass("inventory.CraftItemStack")
								.getMethod("asBukkitCopy", new Class[] { getReflection().getNmsClass("ItemStack") })
								.invoke(new Object[0], new Object[] { nmsItem }));
			}
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}
}
