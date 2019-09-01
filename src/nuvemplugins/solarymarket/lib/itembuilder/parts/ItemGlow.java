package nuvemplugins.solarymarket.lib.itembuilder.parts;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemGlow implements ItemPart
{

	private static final String nsmPackage = ("net.minecraft.server.<version>");
	private static final String obcPackage = ("org.bukkit.craftbukkit.<version>");

	private boolean glow;

	public ItemGlow(String constructor) {
		this.glow = (constructor.equalsIgnoreCase("glow:true"));
	}

	@Override
	public ItemStack send(ItemStack item)
	{
		if ((item != null) && (item.getType() != org.bukkit.Material.AIR) && (this.glow)) {
			try {
				Class<?> craftItemStackClass = this.obcClass("inventory.CraftItemStack");

				Class<?> nbtBaseClass = this.nmsClass("NBTBase");
				Class<?> nbtTagCompoundClass = this.nmsClass("NBTTagCompound");
				Class<?> nbtTagListClass = this.nmsClass("NBTTagList");

				Object nmsItemStack = craftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null,
						item);

				Object itemNBT = nmsItemStack.getClass().getMethod("getTag", new Class[0]).invoke(nmsItemStack,
						new Object[0]);

				if (itemNBT == null) {
					itemNBT = nbtTagCompoundClass.newInstance();
					itemNBT.getClass().getMethod("setString", String.class, String.class).invoke(itemNBT, "ItemGlow",
							String.valueOf(this.glow));
				}

				itemNBT.getClass().getMethod("set", String.class, nbtBaseClass).invoke(itemNBT, "ench",
						nbtTagListClass.newInstance());

				nmsItemStack.getClass().getMethod("setTag", nbtTagCompoundClass).invoke(nmsItemStack, itemNBT);

				return (ItemStack) craftItemStackClass.getDeclaredMethod("asBukkitCopy", nmsItemStack.getClass())
						.invoke(null, nmsItemStack);

			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		return item;
	}

	private Class<?> nmsClass(String className)
	{
		try {
			return (Class.forName(nsmPackage.replace("<version>", this.nmsVersion()).concat(".").concat(className)));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	private Class<?> obcClass(String className)
	{
		try {
			return (Class.forName(obcPackage.replace("<version>", this.nmsVersion()).concat(".").concat(className)));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	private String nmsVersion()
	{
		return Bukkit.getServer().getClass().getName().split("\\.")[3];
	}
}
