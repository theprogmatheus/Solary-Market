package com.redeskyller.bukkit.solarymarket.lib.itembuilder.parts;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemEnchant implements ItemPart {
	private org.bukkit.enchantments.Enchantment enchant;
	private Integer level;

	public ItemEnchant(String constructor)
	{
		try {
			if (constructor.contains(":")) {
				String[] split = constructor.split(":");
				this.enchant = getEnchantmentBySimpleName(split[0]);
				this.level = Integer.valueOf(split[1]);
			}
		} catch (Exception localException) {
		}
	}

	@Override
	public ItemStack send(ItemStack item)
	{
		if ((item != null) && (this.level != null) && (this.enchant != null) && (this.level.intValue() > 0)
				&& (!item.getType().equals(org.bukkit.Material.AIR))) {
			org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
			if (item.getType().equals(org.bukkit.Material.ENCHANTED_BOOK))
				((org.bukkit.inventory.meta.EnchantmentStorageMeta) meta).addStoredEnchant(this.enchant,
						this.level.intValue(), true);
			else
				meta.addEnchant(this.enchant, this.level.intValue(), true);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static Enchantment getEnchantmentBySimpleName(String enchantmentName)
	{
		if (enchantmentName == null)
			return null;

		switch (enchantmentName.toLowerCase()) {

		case "power":
			return Enchantment.ARROW_DAMAGE;
		case "flame":
			return Enchantment.ARROW_FIRE;
		case "infinite":
			return Enchantment.ARROW_INFINITE;
		case "sharpness":
			return Enchantment.DAMAGE_ALL;
		case "smite":
			return Enchantment.DAMAGE_UNDEAD;
		case "efficiency":
			return Enchantment.DIG_SPEED;
		case "aqua_affinity":
			return Enchantment.WATER_WORKER;
		case "bane_of_arthropods":
			return Enchantment.DAMAGE_ARTHROPODS;
		case "blast_protection":
			return Enchantment.PROTECTION_EXPLOSIONS;
		case "unbreaking":
			return Enchantment.DURABILITY;
		case "protection":
			return Enchantment.PROTECTION_ENVIRONMENTAL;
		case "fortune":
			return Enchantment.LOOT_BONUS_BLOCKS;
		case "looting":
			return Enchantment.LOOT_BONUS_MOBS;
		case "feather_falling":
			return Enchantment.PROTECTION_FALL;
		case "fire_protection":
			return Enchantment.PROTECTION_FIRE;
		case "luck_of_the_sea":
			return Enchantment.LUCK;
		case "respiration":
			return Enchantment.OXYGEN;

		default:
			Enchantment enchantment;
			try {
				enchantment = Enchantment.getByName(enchantmentName.toUpperCase());
			} catch (Exception exception) {
				enchantment = null;
			}
			return enchantment;
		}
	}

	public static String getSimpleNameByEnchantment(Enchantment enchantment)
	{
		if (enchantment == null)
			return null;

		switch (enchantment.getName().toUpperCase()) {

		case "ARROW_DAMAGE":
			return "power";

		case "ARROW_FIRE":
			return "flame";

		case "ARROW_INFINITE":
			return "infinite";

		case "DAMAGE_ALL":
			return "sharpness";

		case "DAMAGE_UNDEAD":
			return "smite";

		case "DIG_SPEED":
			return "efficiency";

		case "WATER_WORKER":
			return "aqua_affinity";

		case "DAMAGE_ARTHROPODS":
			return "bane_of_arthropods";

		case "PROTECTION_EXPLOSIONS":
			return "blast_protection";

		case "DURABILITY":
			return "unbreaking";

		case "PROTECTION_ENVIRONMENTAL":
			return "protection";

		case "LOOT_BONUS_BLOCKS":
			return "fortune";

		case "LOOT_BONUS_MOBS":
			return "looting";

		case "PROTECTION_FALL":
			return "feather_falling";

		case "PROTECTION_FIRE":
			return "fire_protection";

		case "LUCK":
			return "luck_of_the_sea";

		case "OXYGEN":
			return "respiration";

		default:
			return enchantment.getName().toLowerCase();
		}
	}
}
