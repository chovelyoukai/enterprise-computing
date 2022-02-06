import java.io.*;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class BusinessLogicServlet extends HttpServlet
{
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		DatabaseConnection userdb = new DatabaseConnection();
		String command = request.getParameter("sqlinput");
		String message = "";
		if (command != null)
		{	
			try
			{
				userdb.connect();
				String[] commands = command.split(";");
				command = commands[0];
				// regex to remove leading whitespace
				command = command.replaceAll("^\\s+", "");
				if ((command.toLowerCase().startsWith("update") ||
					command.toLowerCase().startsWith("insert")) &&
					command.toLowerCase().contains("shipments"))
				{
					message = doBusinessLogic(userdb, command, message);
				}
				else if (command.toLowerCase().startsWith("select"))
				{
					userdb.setQuery(command);
					
					int rows = userdb.getRowCount();
					int cols = userdb.getColumnCount();
					
					message = message + "<h2>Output</h2>";
					
					message = message + "<table style=\"width:100%\">";

					message = message + "<tr>";
					for (int col = 0; col < cols; col++)
					{
						message = message + "<th>" + userdb.getColumnName(col) + "</th>";
					}
					message = message + "</tr>";
					
					for (int row = 0; row < rows; row++)
					{
						message = message + "<tr>";
						for (int col = 0; col < cols; col++)
						{
							message = message + "<td>" + userdb.getValueAt(row, col) + "</td>";
						}
						message = message + "</tr>";
					}
					
					message = message + "</table>";
				}
				else
				{
					int rows = userdb.setUpdate(command);
					message = message + rows + " row(s) updated.</br>";
					message = message + "Business logic not triggered.";
				}
				userdb.disconnectFromDatabase();
			}
			catch(SQLException e)
			{
				message = message + "SQL Error: " + e.getMessage();
			}
			catch(Exception e)
			{
				message = message + "Error: " + e.getMessage();
			}
		}
		
		HttpSession session = request.getSession();
		
		session.setAttribute("message", message);
		if (command == null)
		{
			command = "";
		}
		session.setAttribute("sqlinput", command);
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/project3.jsp");
		rd.forward(request, response);
	}
	
	public String doBusinessLogic(DatabaseConnection userdb, String command, String message) throws IllegalStateException, SQLException
	{
		userdb.setUpdate("drop table if exists oldShipments;");
		userdb.setUpdate("create table oldShipments like shipments;");
		userdb.setUpdate("insert into oldShipments select * from shipments;");
		int rows = userdb.setUpdate(command);
		int updated = userdb.setUpdate("update suppliers set status = status + 5 where snum in (select distinct snum from shipments left join oldShipments" +
		    " using (snum, pnum, jnum, quantity) where oldShipments.snum is null and quantity >= 100);");
		
		message = message + rows + " row(s) updated.</br>";
		if (updated > 0)
		{
			message = message + "Business logic triggered - " + updated + " row(s) updated by business logic.";
		}
		else
		{
			message = message + "Business logic triggered - no rows updated.";
		}
		return message;
	}
}
