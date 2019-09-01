package nuvemplugins.solarymarket.lib.itembuilder.parts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemEffect implements ItemPart
{

	private PotionEffectType effectType;
	private Integer level;
	private Integer duration;

	public ItemEffect(String constructor) {
		try {

			if (constructor.contains(":")) {
				constructor = constructor.toUpperCase();
				String[] split = constructor.split(":");

				this.effectType = PotionEffectType.getByName(split[0]);

				String atributes = split[1];

				if (atributes.contains("-")) {
					String[] durationSplit = atributes.split("-");
					this.level = Integer.valueOf(durationSplit[0]) - 1;
					this.duration = Integer.valueOf(durationSplit[1]);
				} else {
					this.level = Integer.valueOf(atributes) - 1;
					this.duration = 30;
				}

			}
		} catch (Exception localException) {
		}
	}

	@Override
	public ItemStack send(ItemStack item)
	{
		if ((item != null) && (item.getType() == Material.POTION) && (this.effectType != null) && (this.level != null)
				&& (this.duration != null)) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			meta.addCustomEffect(new PotionEffect(this.effectType, this.duration * 20, this.level), false);
			item.setItemMeta(meta);
		}

		return item;
	}
}
