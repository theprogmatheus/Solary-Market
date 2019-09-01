package nuvemplugins.solarymarket.lib.nbt;

public class NBTTagCompound
{
	private Object nbtTag;
	private nuvemplugins.solarymarket.util.Reflection reflection;

	public NBTTagCompound() {
		this.reflection = new nuvemplugins.solarymarket.util.Reflection();
		try {
			this.setNbtTag(this.getReflection().getNmsClass("NBTTagCompound").getConstructor(new Class[0])
					.newInstance(new Object[0]));
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	public nuvemplugins.solarymarket.util.Reflection getReflection()
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
			this.getReflection().getNmsClass("NBTTagCompound")
					.getMethod("set", new Class[] { String.class, this.getReflection().getNmsClass("NBTBase") })
					.invoke(this.nbtTag, new Object[] { name, nbtTagCompound.getNbtTag() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public NBTTagCompound getCompound(String name)
	{
		NBTTagCompound tag = new NBTTagCompound();
		try {
			Object nbt = this.getReflection().getNmsClass("NBTTagCompound")
					.getMethod("getCompound", new Class[] { String.class }).invoke(this.nbtTag, new Object[] { name });
			if (nbt != null) {
				tag.setNbtTag(nbt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tag;
	}

	public void setString(String key, String value)
	{
		try {
			if (value == null) {
				this.remove(key);
			} else {
				this.getNbtTag().getClass().getMethod("setString", new Class[] { String.class, String.class })
						.invoke(this.getNbtTag(), new Object[] { key, value });
			}
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	public String getString(String key)
	{
		String value = null;
		try {
			value = (String) this.getNbtTag().getClass().getMethod("getString", new Class[] { String.class })
					.invoke(this.getNbtTag(), new Object[] { key });
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return value;
	}

	public double getDouble(String key)
	{
		double value = 0.0D;
		try {
			value = ((Double) this.getNbtTag().getClass().getMethod("getDouble", new Class[] { String.class })
					.invoke(this.getNbtTag(), new Object[] { key })).doubleValue();
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return value;
	}

	public void setDouble(String key, double value)
	{
		try {
			this.getNbtTag().getClass().getMethod("setDouble", new Class[] { String.class, Double.TYPE })
					.invoke(this.getNbtTag(), new Object[] { key, Double.valueOf(value) });
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}

	public boolean has(String key)
	{
		if (this.getString(key) == null) {
			return false;
		}
		return !this.getString(key).isEmpty();
	}

	public void remove(String key)
	{
		try {
			this.getNbtTag().getClass().getMethod("remove", new Class[] { String.class }).invoke(this.getNbtTag(),
					new Object[] { key });
		} catch (Exception erro) {
			erro.printStackTrace();
		}
	}
}
