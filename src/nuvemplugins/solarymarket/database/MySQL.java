package nuvemplugins.solarymarket.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.Plugin;

public class MySQL implements Database
{
	private String hostname;
	private String database;
	private String password;
	private String username;
	private int port;
	private Connection connection;
	private Statement statement;

	public MySQL(Plugin plugin) {
	}

	@Override
	public boolean open()
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");

			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + "/" + this.database,
					this.username, this.password);
			this.statement = this.connection.createStatement();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return this.connection();
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
	public boolean connection()
	{
		return this.connection != null;
	}

	@Override
	public ResultSet query(String query)
	{
		try {
			return this.statement.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Connection getConnection()
	{
		return this.connection;
	}

	@Override
	public String getHostname()
	{
		return this.hostname;
	}

	@Override
	public String getDatabase()
	{
		return this.database;
	}

	@Override
	public String getUsername()
	{
		return this.username;
	}

	@Override
	public String getPassword()
	{
		return this.password;
	}

	@Override
	public String getType()
	{
		return "MySQL";
	}

	@Override
	public int getPort()
	{
		return this.port;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	public void setDatabase(String database)
	{
		this.database = database;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	@Override
	public boolean execute(String string)
	{
		try {
			this.statement.execute(string);
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
