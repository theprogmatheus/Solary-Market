package com.redeskyller.bukkit.solarymarket.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.redeskyller.bukkit.solarymarket.util.CallBack;

public abstract class Database {

	private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

	protected Connection connection;

	public abstract Connection getConnection();

	public boolean checkConnection()
	{
		return checkConnection(false);
	}

	public boolean checkConnection(boolean showException)
	{
		try (ResultSet resultSet = this.connection.prepareStatement("SELECT 1").executeQuery()) {
			return (resultSet != null);
		} catch (Exception exception) {
			if (showException)
				exception.printStackTrace();
			return false;
		}
	}

	public void endConnection()
	{
		try {
			if (checkConnection())
				this.connection.close();

			this.connection = null;
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void executeAsync(String sql, CallBack<Boolean> callBack)
	{

		executorService.submit(() -> {

			try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {

				boolean result = preparedStatement.execute();

				if (callBack != null)
					callBack.call(result);

			} catch (Exception exception) {
				exception.printStackTrace();
			}

		});

	}

	public void executeAsync(String sql)
	{
		executeAsync(sql, null);
	}

	public void queryAsync(String sql, CallBack<ResultSet> callBack)
	{
		executorService.submit(() -> {

			try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {

				ResultSet resultSet = preparedStatement.executeQuery();

				if (callBack != null)
					callBack.call(resultSet);

			} catch (Exception exception) {
				exception.printStackTrace();
			}

		});
	}

	public void queryAsync(String sql)
	{
		queryAsync(sql, null);
	}

	public boolean execute(String sql)
	{
		boolean result = false;
		try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {

			result = preparedStatement.execute();

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return result;
	}

	public ResultSet query(String sql)
	{
		try {
			return getConnection().prepareStatement(sql).executeQuery();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

}
