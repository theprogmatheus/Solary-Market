package nuvemplugins.solarymarket;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nuvemplugins.solarymarket.app.SolaryMarket;
import nuvemplugins.solarymarket.commands.SolaryCommand;

public class Main extends JavaPlugin
{

	public static Main main;
	public static SolaryMarket plugin;

	@Override
	public void onEnable()
	{
		plugin = new SolaryMarket(this);
		plugin.onEnable();
		if (Bukkit.getPluginManager().isPluginEnabled(this)) {
			this.getCommand("mercado").setExecutor(new SolaryCommand("mercado"));
		}
	}

	@Override
	public void onDisable()
	{
		plugin.onDisable();
	}

}
