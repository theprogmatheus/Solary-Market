package nuvemplugins.solarymarket.app;

import java.io.File;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import nuvemplugins.solarymarket.database.Database;
import nuvemplugins.solarymarket.database.MySQL;
import nuvemplugins.solarymarket.database.SQLite;
import nuvemplugins.solarymarket.lib.fanciful.FancyMessage;
import nuvemplugins.solarymarket.manager.Mensagens;
import nuvemplugins.solarymarket.plugin.MercadoManager;
import nuvemplugins.solarymarket.util.Config;
import nuvemplugins.solarymarket.util.Vault;

public class SolaryMarket implements Listener
{

	public SolaryMarket(JavaPlugin javaplugin) {
		instance = javaplugin;
	}

	public static final String PLUGIN_NAME = "Solary-Market";
	public static final String AUTHOR = "Sr_Edition";
	public static final String VERSION = "1.1";

	public static String table;
	public static JavaPlugin instance;
	public static Database database;
	public static Mensagens mensagens;
	public static Config config;
	public static MercadoManager manager;
	public static Vault vault;

	public void onEnable()
	{
		if (!new File(instance.getDataFolder(), "config.yml").exists()) {
			instance.saveResource("config.yml", false);
		}
		if (!new File(instance.getDataFolder(), "mensagens.yml").exists()) {
			instance.saveResource("mensagens.yml", false);
		}

		vault = new Vault();
		if (vault.setupEconomy()) {
			this.database();
			mensagens = new Mensagens();
			config = new Config(instance, "config.yml");
			manager = new MercadoManager();
			vault.setupChat();
			Bukkit.getPluginManager().registerEvents(this, instance);
		} else {
			echo("sistema de economia nao encontrado, plugin desativado!");
			Bukkit.getPluginManager().disablePlugin(instance);
		}

	}

	public void onDisable()
	{
		if (database != null) {
			if (database.connection()) {
				database.close();
			}
		}
	}

	public void database()
	{
		try {
			echo("iniciando banco de dados...");
			FileConfiguration config = instance.getConfig();
			boolean usemysql = config.getBoolean("mysql.enable");
			if (usemysql) {
				echo("tipo do banco de dados \"MySQL\" selecionado.");
				String hostname = config.getString("mysql.hostname");
				String database_name = config.getString("mysql.database");
				String username = config.getString("mysql.username");
				String password = config.getString("mysql.password");
				String table_name = config.getString("mysql.table");
				int port = config.getInt("mysql.port");
				MySQL mysql = new MySQL(instance);
				mysql.setHostname(hostname);
				mysql.setDatabase(database_name);
				mysql.setUsername(username);
				mysql.setPassword(password);
				mysql.setPort(port);
				table = table_name;
				database = mysql;
			} else {
				echo("tipo do banco de dados \"SQLite\" selecionado.");
				table = "HuntersMarket".toLowerCase();
				database = new SQLite(instance);
			}

			echo("testando conexao com banco de dados...");
			if (database.open()) {
				echo("conexao testada com sucesso, tudo OK!");
				database.close();
			} else {
				echo("houve um erro ao conectar-se com o banco de dados!");
				echo("tipo do banco de dados \"SQLite\" selecionado.");
				table = "HuntersMarket".toLowerCase();
				database = new SQLite(instance);
			}

			database.open();

			database.execute("create table if not exists " + table + "_mercado"
					+ " (player varchar(40), categoria varchar(40), uuid varchar(40), preco double, tempo long, cache blob);");

			database.execute("create table if not exists " + table + "_punicoes"
					+ " (player varchar(40), staff varchar(40), tempo long, motivo varchar(100));");

			database.execute("create table if not exists " + table + "_expirados"
					+ " (player varchar(40), uuid varchar(40), cache blob);");
			database.close();
		} catch (Exception e) {
			e.printStackTrace();
			echo("houve um erro ao tentar iniciar o banco de dados.");
		}
	}

	public static int getTotalItens(Player player)
	{
		int totalitens = 0;
		try {
			database.open();
			ResultSet result = database
					.query("select * from " + table + "_mercado where player='" + player.getName() + "';");
			while (result.next()) {
				totalitens++;
			}
			result = database.query("select * from " + table + "_expirados where player='" + player.getName() + "';");
			while (result.next()) {
				totalitens++;
			}
			database.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return totalitens;
	}

	public static int getMaxItens(Player player)
	{
		YamlConfiguration yaml = config.getYaml();
		int maxitens = yaml.getInt("limites.default");
		for (String section : yaml.getConfigurationSection("limites.permissions").getKeys(false)) {
			if (player.hasPermission("solarymarket.limite." + section.toLowerCase())) {
				int i = yaml.getInt("limites.permissions." + section);
				if (i > maxitens) {
					maxitens = i;
				}
			}
		}
		return maxitens;
	}

	public static boolean sell(Player player)
	{
		return getTotalItens(player) < getMaxItens(player);
	}

	public static String numberFormat(double valor)
	{
		return NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR")).format(valor);
	}

	public static void echo(String message)
	{
		instance.getLogger().info(message);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		try {
			database.open();
			ResultSet mercado_result = database
					.query("select * from " + table + "_mercado where player='" + player.getName() + "';");

			while (mercado_result.next()) {
				long tempo = mercado_result.getLong("tempo");
				if (System.currentTimeMillis() >= tempo) {
					UUID uuid = UUID.fromString(mercado_result.getString("uuid"));

					ResultSet result = database
							.query("select * from " + table.concat("_mercado") + " where uuid='" + uuid + "';");
					if (result.next()) {
						String cache = result.getString("cache");
						database.execute("insert into " + table.concat("_expirados") + " values ('" + player.getName()
								+ "', '" + uuid + "', '" + cache + "');");
						database.execute("delete from " + table.concat("_mercado").concat(" where uuid='")
								.concat(uuid.toString()).concat("';"));
					}
				}
			}

			ResultSet result = database
					.query("select * from " + table + "_expirados where player='" + player.getName() + "';");
			if (result.next()) {
				player.sendMessage("");
				player.sendMessage(
						"§eParece que você havia colocado alguns itens no mercado, infelizmente nenhum jogador os comprou. Para coletar-los ");
				FancyMessage msg = new FancyMessage("§lCLIQUE §lAQUI");
				msg.color(ChatColor.GREEN).command("/mercado coletar")
						.then(" ou utilize o comando \"/mercado coletar\"").color(ChatColor.YELLOW).send(player);
				player.sendMessage("");
			}
			database.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}