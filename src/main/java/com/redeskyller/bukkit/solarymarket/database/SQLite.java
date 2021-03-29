package com.redeskyller.bukkit.solarymarket.database;

import java.sql.Connection;
import java.sql.DriverManager;

import org.bukkit.plugin.java.JavaPlugin;

public class SQLite extends Database {

	private final JavaPlugin plugin;

	public SQLite(final JavaPlugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public Connection getConnection()
	{
		try {
			if (!(checkConnection())) {
				// load driver
				Class.forName("org.sqlite.JDBC");

				this.connection = DriverManager
						.getConnection("jdbc:sqlite:" + this.plugin.getDataFolder().getPath() + "/storage.db");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return this.connection;
	}

}
