package nuvemplugins.solarymarket.database;

import java.sql.SQLException;
import java.sql.Statement;

public class SQLite implements Database
{
	private org.bukkit.plugin.Plugin plugin;
	private java.sql.Connection connection;
	private Statement statement;

	public SQLite(org.bukkit.plugin.Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public java.sql.Connection getConnection()
	{
		return this.connection;
	}

	@Override
	public boolean open()
	{
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = java.sql.DriverManager
					.getConnection("jdbc:sqlite:" + this.plugin.getDataFolder().getPath() + "/database.db");
			this.statement = this.connection.createStatement();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return this.connection();
	}

	@Override
	public boolean connection()
	{
		return this.connection != null;
	}

	@Override
	public boolean close()
	{
		if (this.connection()) {
			try {
				this.statement.close();
				this.connection.close();
				this.statement = null;
				this.connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return this.connection();
	}

	@Override
	public String getHostname()
	{
		return "localhost";
	}

	@Override
	public String getDatabase()
	{
		return "database.db";
	}

	@Override
	public String getUsername()
	{
		return "root";
	}

	@Override
	public String getPassword()
	{
		return "";
	}

	@Override
	public String getType()
	{
		return "SQLite";
	}

	@Override
	public int getPort()
	{
		return 3306;
	}

	@Override
	public java.sql.ResultSet query(String query)
	{
		try {
			return this.statement.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean execute(String string)
	{
		try {
			return this.statement.execute(string);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Statement getStatement()
	{
		return this.statement;
	}
}
