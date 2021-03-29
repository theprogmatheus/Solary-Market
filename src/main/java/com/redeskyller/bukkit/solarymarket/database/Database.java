package com.redeskyller.bukkit.solarymarket.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract interface Database {
	public abstract boolean open();

	public abstract boolean close();

	public abstract boolean connection();

	public abstract ResultSet query(String paramString);

	public abstract boolean execute(String paramString);

	public abstract Connection getConnection();

	public abstract Statement getStatement();

	public abstract String getHostname();

	public abstract String getDatabase();

	public abstract String getUsername();

	public abstract String getPassword();

	public abstract String getType();

	public abstract int getPort();
}
