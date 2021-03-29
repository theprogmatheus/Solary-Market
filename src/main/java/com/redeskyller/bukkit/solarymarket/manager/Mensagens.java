package com.redeskyller.bukkit.solarymarket.manager;

import com.redeskyller.bukkit.solarymarket.SolaryMarket;
import com.redeskyller.bukkit.solarymarket.util.Config;

public class Mensagens {

	private Config config;

	public Mensagens load()
	{
		this.config = new Config(SolaryMarket.getInstance(), "mensagens.yml").load();
		return this;
	}

	public String get(String string)
	{
		string = string.toUpperCase();
		return this.config.getString(string).replace("&", "§");
	}
}
