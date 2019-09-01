package nuvemplugins.solarymarket.util;

import java.io.File;

public class Config
{
	private org.bukkit.plugin.Plugin plugin;
	private String name;
	private File file;
	private org.bukkit.configuration.file.YamlConfiguration yaml;

	public Config(org.bukkit.plugin.Plugin plugin, String name) {
		this.plugin = plugin;
		this.name = name;
		this.reload();
	}

	public void reload()
	{
		try {
			if (this.name.contains("/")) {
				String[] split = this.name.split("/");
				if (split.length >= 2) {
					File folder = new File(split[0]);
					folder.mkdirs();
					this.file = new File(folder, split[1]);
				} else {
					this.name = this.name.replace("/", "");
					this.file = new File(this.plugin.getDataFolder(), this.name);
					if (!this.file.exists()) {
						this.plugin.saveResource(this.name, false);
					}
				}
			} else {
				this.file = new File(this.plugin.getDataFolder(), this.name);
				if (!this.file.exists()) {
					this.plugin.saveResource(this.name, false);
				}
			}
			this.yaml = org.bukkit.configuration.file.YamlConfiguration
					.loadConfiguration(new java.io.InputStreamReader(new java.io.FileInputStream(this.file), "UTF-8"));
		} catch (Exception e) {
			System.out.println(this.file.getPath());
			e.printStackTrace();
		}
	}

	public void save()
	{
		try {
			if ((this.yaml != null) && (this.file != null)) {
				this.yaml.save(this.file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getString(String path)
	{
		return this.yaml.getString(path).replace("&", "§");
	}

	public org.bukkit.plugin.Plugin getPlugin()
	{
		return this.plugin;
	}

	public String getName()
	{
		return this.name;
	}

	public File getFile()
	{
		return this.file;
	}

	public org.bukkit.configuration.file.YamlConfiguration getYaml()
	{
		return this.yaml;
	}
}
