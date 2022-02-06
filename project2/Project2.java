import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Properties;

public class Project2 extends JFrame
{
	static final String DEFAULT_QUERY = "";
	private final String[] drivers = {"com.mysql.cj.jdbc.Driver",
		"oracle.jdbc.driver.OracleDriver", "com.ibm.db2.jdbc.netDB2Driver",
		"com.jdbc.odbc.jdbcOdbcDriver"};
	private final String[] urls = {"jdbc:mysql://localhost:3306/project2?useTimezone=true&serverTimezone=UTC",
			"jdbc:mysql://localhost:3306/bikedb?useTimezone=true&serverTimezone=UTC",
			"jdbc:mysql://localhost:3306/test?useTimezone=true&serverTimezone=UTC"};
	private ResultSetTableModel tableModel;
	private ResultSetTableModel operationsLog;
	private JTextArea queryArea;
	
	public Project2()
	{
		super("Project 2 MySQL Tool");
		
		try
		{
			tableModel = new ResultSetTableModel();
			operationsLog = new ResultSetTableModel();
			Properties properties = new Properties();
			FileInputStream fileIn = null;
			
			String opDriver, opUrl, opUsername, opPassword;
			fileIn = new FileInputStream("db.properties");
			properties.load(fileIn);
			opDriver = properties.getProperty("MYSQL_DB_DRIVER");
			opUrl = properties.getProperty("MYSQL_DB_URL");
			opUsername = properties.getProperty("MYSQL_DB_USERNAME");
			opPassword = properties.getProperty("MYSQL_DB_PASSWORD");
			operationsLog.connect(opDriver, opUrl, opUsername, opPassword);
			
			queryArea = new JTextArea(DEFAULT_QUERY, 3, 100);
			queryArea.setWrapStyleWord(true);
			queryArea.setLineWrap(true);
			
			JLabel driverLabel = new JLabel("Driver: ");
			JLabel urlLabel = new JLabel("URL: ");
			JLabel usernameLabel = new JLabel("Username: ");
			JLabel passwordLabel = new JLabel("Password: ");
			JLabel infoLabel = new JLabel("Database Information");
			JLabel inputLabel = new JLabel("Database Command");
			JLabel outputLabel = new JLabel("Database Output");
			JLabel connectStatus = new JLabel("Not Connected.");
			connectStatus.setForeground(Color.red);
			
			JScrollPane scrollPane = new JScrollPane(queryArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			Box scrollBox = Box.createVerticalBox();
			scrollBox.add(inputLabel);
			scrollBox.add(scrollPane);
			
			JTextField username = new JTextField();
			JPasswordField password = new JPasswordField();
			Box usernameBox = Box.createHorizontalBox();
			Box passwordBox = Box.createHorizontalBox();
			usernameBox.add(usernameLabel);
			usernameBox.add(username);
			passwordBox.add(passwordLabel);
			passwordBox.add(password);
			
			JButton submitButton = new JButton("Submit Command");
			submitButton.setBackground(Color.blue);
			submitButton.setForeground(Color.yellow);
			submitButton.setBorderPainted(false);
			submitButton.setOpaque(true);
			
			JButton clearButton = new JButton("Clear Command");
			clearButton.setBackground(Color.red);
			clearButton.setForeground(Color.white);
			clearButton.setBorderPainted(false);
			clearButton.setOpaque(true);
			
			JButton clearButton2 = new JButton("Clear Results");
			clearButton2.setBackground(Color.gray);
			clearButton2.setForeground(Color.black);
			clearButton2.setBorderPainted(false);
			clearButton2.setOpaque(true);
			
			JButton connectButton = new JButton("Connect to Database");
			connectButton.setBackground(Color.blue);
			connectButton.setForeground(Color.yellow);
			connectButton.setBorderPainted(false);
			connectButton.setOpaque(true);
			
			JComboBox<String> driverSelect = new JComboBox<String>();
			JComboBox<String> urlSelect = new JComboBox<String>();
			urlSelect.setPrototypeDisplayValue("jdbc:mysql://localhost:3306/project2");
			
			Box driverBox = Box.createHorizontalBox();
			Box urlBox = Box.createHorizontalBox();
			driverBox.add(driverLabel);
			driverBox.add(driverSelect);
			urlBox.add(urlLabel);
			urlBox.add(urlSelect);
			
			for (int i = 0; i < drivers.length; i++)
			{
				driverSelect.addItem(drivers[i]);
			}
			for (int i = 0; i < urls.length; i++)
			{
				urlSelect.addItem(urls[i]);
			}		
			
			
			JPanel selectPanel = new JPanel();
			selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.PAGE_AXIS));
			
			selectPanel.add(infoLabel);
			selectPanel.add(driverBox);
			selectPanel.add(urlBox);
			selectPanel.add(usernameBox);
			selectPanel.add(passwordBox);
			selectPanel.add(connectButton);
			selectPanel.add(connectStatus);
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));

			buttonPanel.add(submitButton);
			buttonPanel.add(clearButton);
			buttonPanel.add(clearButton2);
			
			Box topBox = Box.createHorizontalBox();
			topBox.add(selectPanel);
			topBox.add(scrollBox);
			topBox.add(buttonPanel);
			
			Box connectBox = Box.createVerticalBox();
			connectBox.add(topBox);
			connectBox.add(connectStatus);
			
			JTable resultTable = new JTable();
			resultTable.setGridColor(Color.black);
			Box resultBox = Box.createVerticalBox();
			resultBox.add(new JScrollPane(resultTable));
			resultBox.add(outputLabel);
			
			add(connectBox, BorderLayout.NORTH);
			add(resultBox, BorderLayout.CENTER);
			
			submitButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						String[] commands = queryArea.getText().split(";");
						for (String command : commands)
						{
							command = command.replace("\n", " ");
    						// regex to remove leading whitespace
							command = command.replaceAll("^\\s+", "");
							if (command.equals(""))
							{
								continue;
							}
							try
							{
								System.out.println("|" + command + "|");
    							if (command.toLowerCase().startsWith("select"))
    							{
    								tableModel.setQuery(command);
    								operationsLog.setUpdate("update operationscount set num_queries = num_queries + 1");
    							}
    							else
    							{
    								tableModel.setUpdate(command);
    								operationsLog.setUpdate("update operationscount set num_updates = num_updates + 1");
    							}
    							resultTable.setModel(tableModel);
							}
							catch (SQLException e)
							{
								JOptionPane.showMessageDialog(null, e.getMessage(),
									"Database Error", JOptionPane.ERROR_MESSAGE);
							}
							catch(IllegalStateException e)
							{
								JOptionPane.showMessageDialog(null, e.getMessage(),
									"Database Error", JOptionPane.ERROR_MESSAGE);
								tableModel.disconnectFromDatabase();
							}
						}
					}
				}
			);
			
			clearButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						queryArea.setText("");
					}
				}
			);
			
			clearButton2.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						resultTable.setModel(new DefaultTableModel());;
					}
				}
			);
			
			connectButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						try
						{
						tableModel.disconnectFromDatabase();
						tableModel.connect(String.valueOf(driverSelect.getSelectedItem()),
								String.valueOf(urlSelect.getSelectedItem()), username.getText(),
							String.valueOf(password.getPassword()));
						
						connectStatus.setText("Connected to " + urlSelect.getSelectedItem());
						connectStatus.setForeground(Color.green);
						}
						catch (SQLException e)
						{
							JOptionPane.showMessageDialog(null, e.getMessage(),
								"Database Error", JOptionPane.ERROR_MESSAGE);
							tableModel.disconnectFromDatabase();
						}
						catch(ClassNotFoundException e)
						{
							JOptionPane.showMessageDialog(null, e.getMessage(),
								"Driver Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			);
			
			setSize(1024, 720);
			setVisible(true);
		}
		catch(IllegalStateException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(),
				"Database Error", JOptionPane.ERROR_MESSAGE);
			tableModel.disconnectFromDatabase();
			System.exit(1);
		}
		catch(FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(),
				"Cannot open database properties file", JOptionPane.ERROR_MESSAGE);
			tableModel.disconnectFromDatabase();
			System.exit(1);
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(),
				"Cannot read database properties file", JOptionPane.ERROR_MESSAGE);
			tableModel.disconnectFromDatabase();
			System.exit(1);
		}
		catch(ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(),
				"Driver Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch (SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(),
				"Database Error", JOptionPane.ERROR_MESSAGE);
			tableModel.disconnectFromDatabase();
			System.exit(1);
		}
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
			{
				public void windowClosed(WindowEvent event)
				{
					tableModel.disconnectFromDatabase();
					operationsLog.disconnectFromDatabase();
					System.exit(0);
				}
			}
		);
	}
	
	public static void main(String[] args)
	{
		new Project2();
	}
}
