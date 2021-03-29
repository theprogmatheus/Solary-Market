package com.redeskyller.bukkit.solarymarket.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MySQL extends Database {

	private final JavaPlugin plugin;

	public MySQL(final JavaPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public Connection getConnection()
	{
		try {
			if (!(checkConnection())) {

				this.plugin.getLogger().info("Conexao com MYSQL inexistente, tentando conectar-se...");

				// load mysql.yml file
				File configFile = new File(this.plugin.getDataFolder(), "config.yml");
				if (!configFile.exists()) {
					this.plugin.getDataFolder().mkdirs();
					this.plugin.saveResource("config.yml", false);
				}

				YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

				String hostname = config.getString("mysql.hostname");
				String database = config.getString("mysql.database");
				String username = config.getString("mysql.username");
				String password = config.getString("mysql.password");

				String connectionURL = "jdbc:mysql://" + hostname + "/" + database + "?autoReconnect=true&useSSL=false";

				// load driver
				Class.forName("com.mysql.jdbc.Driver");

				this.connection = DriverManager.getConnection(connectionURL, username, password);

				this.plugin.getLogger().info("Conexao com MYSQL estabelecida com sucesso.");
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return this.connection;
	}

}
