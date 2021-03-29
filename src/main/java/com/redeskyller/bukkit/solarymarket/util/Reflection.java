package com.redeskyller.bukkit.solarymarket.util;

public class Reflection {
	private String nmsPackage;
	private String obcPackage;
	private String bukkitVersion;

	public Reflection()
	{
		this.bukkitVersion = org.bukkit.Bukkit.getServer().getClass().getName().split("\\.")[3];
		this.obcPackage = ("org.bukkit.craftbukkit." + getBukkitVersion());
		this.nmsPackage = ("net.minecraft.server." + getBukkitVersion());
	}

	public String getObcPackage()
	{
		return this.obcPackage;
	}

	public String getNmsPackage()
	{
		return this.nmsPackage;
	}

	public String getBukkitVersion()
	{
		return this.bukkitVersion;
	}

	public Class<?> getNmsClass(String name)
	{
		Class<?> classe = null;
		try {
			classe = Class.forName(getNmsPackage().concat(".").concat(name));
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return classe;
	}

	public Class<?> getObcClass(String name)
	{
		Class<?> classe = null;
		try {
			classe = Class.forName(getObcPackage().concat(".").concat(name));
		} catch (Exception erro) {
			erro.printStackTrace();
		}
		return classe;
	}
}
