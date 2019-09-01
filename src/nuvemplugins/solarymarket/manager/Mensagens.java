package nuvemplugins.solarymarket.manager;

import nuvemplugins.solarymarket.util.Config;

public class Mensagens
{
	private Config config;

	public Mensagens() {
		this.reload();
	}

	public void reload()
	{
		this.config = new Config(nuvemplugins.solarymarket.app.SolaryMarket.instance, "mensagens.yml");
	}

	public String get(String string)
	{
		string = string.toUpperCase();
		return this.config.getString(string).replace("&", "§");
	}
}
