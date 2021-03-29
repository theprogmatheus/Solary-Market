package com.redeskyller.bukkit.solarymarket.manager;

import com.redeskyller.bukkit.solarymarket.util.Config;

public class Mensagens {
	private Config config;

	public Mensagens()
	{
		reload();
	}

	public void reload()
	{
		this.config = new Config(com.redeskyller.bukkit.solarymarket.app.SolaryMarket.instance, "mensagens.yml");
	}

	public String get(String string)
	{
		string = string.toUpperCase();
		return this.config.getString(string).replace("&", "§");
	}
}
