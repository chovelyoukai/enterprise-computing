import java.io.*;
import java.sql.*;
import java.util.*;
import com.mysql.cj.jdbc.MysqlDataSource;

public class DatabaseConnection
{
	private Connection connection;
	private boolean connectedToDatabase = false;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private Statement statement;
	private int rows;
	
	public void connect()
			throws SQLException, ClassNotFoundException, IOException
	{
		MysqlDataSource dataSource = null;
		String driver, url, username, password;
        
		driver = "com.mysql.cj.jdbc.Driver";
		url = "jdbc:mysql://localhost:3306/project3";
		username = "project3client";
		password = "cl1entp@ssword";
		
		Class.forName(driver);
		dataSource = new MysqlDataSource();
		dataSource.setUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		
		connection = dataSource.getConnection();
		
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
		
		connectedToDatabase = true;
	}
	
	public String getColumnName(int column) throws IllegalStateException
	{
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		
		try
		{
			return metaData.getColumnName(column + 1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return "";
	}
	
	public int getColumnCount() throws IllegalStateException
	{
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		
		try
		{
			return metaData.getColumnCount();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public int getRowCount() throws IllegalStateException
	{
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		
    	return rows;
	}
	
	public Object getValueAt(int row, int column) throws IllegalStateException
	{
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		
		try
		{
			resultSet.absolute(row + 1);
			return resultSet.getObject(column + 1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return "";
	}
	
	public void setQuery(String query) throws IllegalStateException,
		SQLException
	{		
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		
		resultSet = statement.executeQuery(query);
		metaData = resultSet.getMetaData();
		resultSet.last();
		rows = resultSet.getRow();
	}
	
	public int setUpdate(String update) throws IllegalStateException,
		SQLException
	{
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		
		return statement.executeUpdate(update);
	}
	
	public void disconnectFromDatabase()
	{
		if (!connectedToDatabase)
		{
			return;
		}
		else
		{
			try
			{
				statement.close();
				connection.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{
				connectedToDatabase = false;
			}
		}
	}
}
