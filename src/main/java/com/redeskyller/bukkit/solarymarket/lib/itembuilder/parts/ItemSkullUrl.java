package com.redeskyller.bukkit.solarymarket.lib.itembuilder.parts;

import java.lang.reflect.Field;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class ItemSkullUrl implements ItemPart {

	private String name;

	public ItemSkullUrl(String constructor)
	{
		try {
			if (constructor.startsWith("skullurl:"))
				this.name = constructor.replace("skullurl:", "").replace("&", "§");
		} catch (Exception localException) {
		}
	}

	@Override
	public ItemStack send(ItemStack item)
	{
		if ((item != null) && (!item.getType().equals(org.bukkit.Material.AIR))
				&& (item.getType().equals(org.bukkit.Material.SKULL_ITEM)) && (item.getDurability() == 3)
				&& (getName() != null))
			try {
				GameProfile profile = new GameProfile(UUID.randomUUID(), null);
				PropertyMap propertyMap = profile.getProperties();

				if (propertyMap != null) {
					byte[] encodedData = new Base64()
							.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", getName()).getBytes());

					propertyMap.put("textures", new Property("textures", new String(encodedData)));

					ItemMeta meta = item.getItemMeta();
					Class<?> metaClass = meta.getClass();
					Field field = metaClass.getDeclaredField("profile");
					field.setAccessible(true);
					field.set(meta, profile);
					item.setItemMeta(meta);
				}

			} catch (Exception exception) {
				exception.printStackTrace();
			}
		return item;
	}

	public String getName()
	{
		return this.name;
	}
}
