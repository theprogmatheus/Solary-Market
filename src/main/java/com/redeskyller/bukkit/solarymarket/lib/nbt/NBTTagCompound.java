package com.redeskyller.bukkit.solarymarket.lib.nbt;

public class NBTTagCompound {
	private Object nbtTag;
	private com.redeskyller.bukkit.solarymarket.util.Reflection reflection;

	public NBTTagCompound()
	{
		this.reflection = new com.redeskyller.bukkit.solarymarket.util.Reflection();
		try {
			setNbtTag(getReflection().getNmsClass("NBTTagCompound").getConstructor(new Class[0])
					.newInstance(new Object[0]));
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	public com.redeskyller.bukkit.solarymarket.util.Reflection getReflection()
	{
		return this.reflection;
	}

	public Object getNbtTag()
	{
		return this.nbtTag;
	}

	public void setNbtTag(Object nbtTag)
	{
		this.nbtTag = nbtTag;
	}

	public void setCompound(String name, NBTTagCompound nbtTagCompound)
	{
		try {
			getReflection().getNmsClass("NBTTagCompound")
					.getMethod("set", new Class[] { String.class, getReflection().getNmsClass("NBTBase") })
					.invoke(this.nbtTag, new Object[] { name, nbtTagCompound.getNbtTag() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public NBTTagCompound getCompound(String name)
	{
		NBTTagCompound tag = new NBTTagCompound();
		try {
			Object nbt = getReflection().getNmsClass("NBTTagCompound")
					.getMethod("getCompound", new Class[] { String.class }).invoke(this.nbtTag, new Object[] { name });
			if (nbt != null)
				tag.setNbtTag(nbt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tag;
	}

	public void setString(String key, String value)
	{
		try {
			if (value == null)
				remove(key);
			else
				getNbtTag().getClass().getMethod("setString", new Class[] { String.class, String.class })
						.invoke(getNbtTag(), new Object[] { key, value });
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	public String getString(String key)
	{
		String value = null;
		try {
			value = (String) getNbtTag().getClass().getMethod("getString", new Class[] { String.class })
					.invoke(getNbtTag(), new Object[] { key });
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return value;
	}

	public double getDouble(String key)
	{
		double value = 0.0D;
		try {
			value = ((Double) getNbtTag().getClass().getMethod("getDouble", new Class[] { String.class })
					.invoke(getNbtTag(), new Object[] { key })).doubleValue();
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return value;
	}

	public void setDouble(String key, double value)
	{
		try {
			getNbtTag().getClass().getMethod("setDouble", new Class[] { String.class, Double.TYPE }).invoke(getNbtTag(),
					new Object[] { key, Double.valueOf(value) });
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	public boolean has(String key)
	{
		if (getString(key) == null)
			return false;
		return !getString(key).isEmpty();
	}

	public void remove(String key)
	{
		try {
			getNbtTag().getClass().getMethod("remove", new Class[] { String.class }).invoke(getNbtTag(),
					new Object[] { key });
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}
}
