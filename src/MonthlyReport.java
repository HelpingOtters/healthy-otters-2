package dw;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MonthlyReport")
public class MonthlyReport extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// database URL
	static final String DB_URL = "jdbc:mysql://localhost/pharm";

	// Database credentials.  COMPLETE THE FOLLOWING STATEMENTS
	static final String USER = "root";
	static final String PASS = "Bandit0!";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		String sql =  "SELECT dg.trade_name, SUM(quantity) AS \"sold\"\r\n" + 
				"FROM prescription pres JOIN drug dg\r\n" + 
				"		ON pres.drug_id = dg.drug_id\r\n" + 
				"WHERE pres.date BETWEEN DATE_SUB(CURDATE(), INTERVAL 1 MONTH) AND \r\n" + 
				"CURDATE()\r\n" + 
				"AND pharmacy_id = (SELECT pharmacy_id FROM pharmacy WHERE name LIKE ?)\r\n" + 
				"GROUP BY dg.trade_name;";

		response.setContentType("text/html"); // Set response content type
		PrintWriter out = response.getWriter();

		String name = request.getParameter("name");

		if(name != "") {
			try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {

				//Prepare Select
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, name);
				ResultSet rs = pstmt.executeQuery();

				//Start html output
				out.println("<!DOCTYPE HTML><head><link rel=\"stylesheet\" href=\"search.css\"><html><body><div class=\"container\" style=\"width:100% color:white;\">");
				out.println("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
						+ " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
				out.println("	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
				out.println("<link rel=\"stylesheet\" href=\"search.css\">");

				
				out.println("<p align = \"center\"> Pharmacy: " + name + "</p>\n");

				//Table Headers
				out.println("<table align =\"center\">");
				out.println("<tr>");	
				//out.println("<th>Date</th>");
				out.println("<th>Drug Name</th>");
				out.println("<th>Quantity Sold</th>");

				//creates rows with data for each row from the result set
				while(rs.next()) {
					out.println("<tr>");
					//out.println("<td>" + rs.getString("date") + "</td>");
					out.println("<td align =\"left\">" + rs.getString("trade_name") + "</td>");
					out.println("<td align =\"center\">" + rs.getString("sold") + "</td>");
					out.println("</tr>");
				}

				rs.close();
				out.println("</table>");
				out.println("</body></html>");
				out.println("<div><body><head><html>");
				


			} catch (SQLException e) {
				// Handle errors

				e.printStackTrace();
			}  
		}
		else {
			out.println("<!DOCTYPE HTML><html><body>");
			out.println("	<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
					+ " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
			out.println("	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
			out.println("<link rel=\"stylesheet\" href=\"search.css\">");
			out.println("<p> Invalid Entry </p>");
			
		}
	}

}
