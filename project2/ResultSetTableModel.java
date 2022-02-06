import java.io.*;
import java.sql.*;
import javax.swing.table.*;
import java.util.*;
import com.mysql.cj.jdbc.MysqlDataSource;

public class ResultSetTableModel extends AbstractTableModel
{
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int rows;
	private boolean connectedToDatabase = false;
	private boolean hasData;
	
	public void connect(String driver, String url, String username, String password)
		throws SQLException, ClassNotFoundException
	{
		MysqlDataSource dataSource = null;
		
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
	
	@SuppressWarnings("rawtypes")
	public Class getColumnClass(int column) throws IllegalStateException
	{
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		
		try
		{
			String classname = metaData.getColumnClassName(column + 1);
			return Class.forName(classname);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return Object.class;
	}
	
	public int getColumnCount() throws IllegalStateException
	{
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		if (!hasData)
		{
			return 0;
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
	
	public int getRowCount() throws IllegalStateException
	{
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		if (!hasData)
		{
			return 0;
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
			resultSet.next();
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
		
		hasData = true;
		fireTableStructureChanged();
	}
	
	public void setUpdate(String update) throws IllegalStateException,
		SQLException
	{
		if (!connectedToDatabase)
		{
			throw new IllegalStateException("Not connected to database.");
		}
		
		statement.executeUpdate(update);

		hasData = false;
		fireTableStructureChanged();
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