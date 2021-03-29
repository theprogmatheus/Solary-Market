package com.redeskyller.bukkit.solarymarket;

import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.redeskyller.bukkit.solarymarket.commands.SolaryCommand;
import com.redeskyller.bukkit.solarymarket.database.Database;
import com.redeskyller.bukkit.solarymarket.database.MySQL;
import com.redeskyller.bukkit.solarymarket.database.SQLite;
import com.redeskyller.bukkit.solarymarket.lib.fanciful.FancyMessage;
import com.redeskyller.bukkit.solarymarket.manager.Mensagens;
import com.redeskyller.bukkit.solarymarket.plugin.MercadoManager;
import com.redeskyller.bukkit.solarymarket.util.Config;
import com.redeskyller.bukkit.solarymarket.util.Vault;

public class SolaryMarket extends JavaPlugin implements Listener {

	private static JavaPlugin instance;

	public static Database database;
	public static String tableName;

	public static Mensagens mensagens;
	public static Config config;

	public static MercadoManager manager;
	public static Vault vault;

	@Override
	public void onEnable()
	{
		instance = this;

		vault = new Vault();
		if (vault.setupEconomy()) {

			config = new Config(this, "config.yml").load();
			mensagens = new Mensagens().load();

			setupDatabase();

			manager = new MercadoManager().load();

			vault.setupChat();

			Bukkit.getPluginManager().registerEvents(this, this);
			getCommand("mercado").setExecutor(new SolaryCommand("mercado"));

		} else
			Bukkit.getPluginManager().disablePlugin(this);
	}

	@Override
	public void onDisable()
	{
		if (database != null)
			database.endConnection();
	}

	public static JavaPlugin getInstance()
	{
		return instance;
	}

	private void setupDatabase()
	{
		try {
			boolean mysql = config.getBoolean("mysql.enable");
			if (mysql) {
				tableName = config.getString("mysql.table");
				database = new MySQL(this);
			} else {
				tableName = "solarymarket";
				database = new SQLite(this);
			}

			database.execute("CREATE TABLE IF NOT EXISTS " + tableName + "_mercado"
					+ " (player varchar(40), categoria varchar(40), uuid varchar(40), preco double, tempo long, cache blob);");

			database.execute("CREATE TABLE IF NOT EXISTS " + tableName + "_punicoes"
					+ " (player varchar(40), staff varchar(40), tempo long, motivo varchar(100));");

			database.execute("CREATE TABLE IF NOT EXISTS " + tableName + "_expirados"
					+ " (player varchar(40), uuid varchar(40), cache blob);");

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static int getTotalItens(Player player)
	{
		int totalitens = 0;
		try {

			try (ResultSet resultSet = database
					.query("SELECT COUNT(1) FROM " + tableName + "_mercado WHERE player='" + player.getName() + "';")) {
				if (resultSet.next())
					totalitens += resultSet.getInt(1);
			}

			try (ResultSet resultSet = database.query(
					"SELECT COUNT(1) FROM " + tableName + "_expirados WHERE player='" + player.getName() + "';")) {
				if (resultSet.next())
					totalitens += resultSet.getInt(1);
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return totalitens;
	}

	public static int getMaxItens(Player player)
	{
		int maxitens = config.getInt("limites.default");
		for (String section : config.getConfigurationSection("limites.permissions").getKeys(false))
			if (player.hasPermission("solarymarket.limite." + section.toLowerCase())) {
				int i = config.getInt("limites.permissions." + section);
				if (i > maxitens)
					maxitens = i;
			}
		return maxitens;
	}

	public static boolean canSell(Player player)
	{
		return getTotalItens(player) < getMaxItens(player);
	}

	public static String currencyFormat(double valor)
	{

		String vaultFormat = vault.getEconomy().format(valor);
		if ((vaultFormat != null) && !vaultFormat.isEmpty())
			return vaultFormat;

		return NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR")).format(valor);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		try {

			try (ResultSet resultSet = database
					.query("SELECT * FROM " + tableName + "_mercado WHERE player='" + player.getName() + "';")) {
				while (resultSet.next()) {
					long tempo = resultSet.getLong("tempo");
					if (System.currentTimeMillis() >= tempo) {

						UUID uuid = UUID.fromString(resultSet.getString("uuid"));
						String cache = resultSet.getString("cache");

						database.execute("INSERT INTO " + tableName.concat("_expirados") + " VALUES ('"
								+ player.getName() + "', '" + uuid + "', '" + cache + "');");

						database.execute("DELETE FROM " + tableName.concat("_mercado").concat(" WHERE uuid='")
								.concat(uuid.toString()).concat("';"));

					}
				}
			}

			try (

					ResultSet resultSet = database.query("SELECT COUNT(1) FROM " + tableName
							+ "_expirados WHERE player='" + player.getName() + "';")) {

				if (resultSet.next() && (resultSet.getInt(1) > 0)) {
					player.sendMessage("");
					player.sendMessage(
							"§eParece que você havia colocado alguns itens no mercado, infelizmente nenhum jogador os comprou. Para coletar-los ");
					FancyMessage msg = new FancyMessage("§lCLIQUE §lAQUI");
					msg.color(ChatColor.GREEN).command("/mercado coletar")
							.then(" ou utilize o comando \"/mercado coletar\"").color(ChatColor.YELLOW).send(player);
					player.sendMessage("");
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}