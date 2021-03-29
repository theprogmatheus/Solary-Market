package com.redeskyller.bukkit.solarymarket.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config extends YamlConfiguration {

	private final JavaPlugin plugin;
	private final File file;

	public Config(final JavaPlugin plugin, final String configName)
	{
		this(plugin, new File(plugin.getDataFolder(), configName));
	}

	public Config(final JavaPlugin plugin, final File file)
	{
		this.plugin = plugin;
		this.file = file;
	}

	public Config load()
	{
		try {

			if (!this.file.exists()) {
				this.plugin.getDataFolder().mkdirs();
				this.plugin.saveResource(this.file.getName(), false);
			}

			load(new InputStreamReader(new FileInputStream(this.file), "UTF-8"));

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return this;
	}

	public JavaPlugin getPlugin()
	{
		return this.plugin;
	}

	public File getFile()
	{
		return this.file;
	}

	@Override
	public String getString(String path)
	{
		String string = super.getString(path);
		if (string != null)
			return string.replace("&", "§");

		return string;
	}
}
