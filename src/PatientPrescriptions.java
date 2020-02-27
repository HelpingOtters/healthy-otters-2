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

@WebServlet("/PatientPrescriptions")
public class PatientPrescriptions extends HttpServlet {
   private static final long serialVersionUID = 1L;
   private String patientID;

   // database URL
   static final String DB_URL = "jdbc:mysql://localhost/pharm";

   // Database credentials.  COMPLETE THE FOLLOWING STATEMENTS
   static final String USER = "root";
   static final String PASS = "Fbcjapan1!";

   protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

      String sql = "SELECT patient_id, prescription_id, trade_name, generic_name, \n" + 
         "(refill - number_of_refills) AS remaining_refills,\n" + 
         "    is_filled\n" + 
         "FROM patient JOIN prescription USING (patient_id)\n" + 
         "    JOIN drug USING (drug_id)\n" + 
         "WHERE patient_id = ?;";
   
      
      response.setContentType("text/html"); // Set response content type
      PrintWriter out = response.getWriter();
      
      patientID = request.getParameter("patient_id").trim();
      
      if(validInput()) {
         try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            conn.setAutoCommit(false); 

            // prepare select statement to get patient's prescriptions
            PreparedStatement pstmt =  conn.prepareStatement(sql);
            // SET VALUES FOR PARAMETER MARKERS 
            pstmt.setString(1, patientID);
            ResultSet rs = pstmt.executeQuery();
            
            // Start HTML code and styling            
            out.println("<!DOCTYPE HTML><head><link rel=\"stylesheet\" href=\"search.css\"><html><body><div class=\"container\" style=\"width:100%; color:white;\">");
            out.println("  <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
                  + " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
            out.println("  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
            out.println("<link rel=\"stylesheet\" href=\"search.css\">"); 
            
            // Create Page header 
            out.println("<h3><br/> Your Prescriptions <br/></h3>");

            // Check if the result set came back with any rows
            if(rs.next()) {
               // Put the pointer back to the first row
               rs.beforeFirst();
               
               /**
                * 
                * patient_id, prescription_id, trade_name, generic_name, \n" + 
         "(refill - number_of_refills) AS remaining_refills,\n" + 
         "    is_filled\n" + 
                */
               
               // Create table headers 
               out.println("<table align =\"center\">");
               out.println("<tr>"); 
               out.println("<th>Patient ID</th>");
               out.println("<th>Prescription ID</th>");
               out.println("<th>Trade Name</th>");
               out.println("<th>Generic Name</th>");
               out.println("<th>Remaining Refills</th>");
               out.println("<th>Prescription Filled</th>");
               out.println("</tr>");

              
               while (rs.next()) {
                  out.println("<tr>");
                  out.println("<td>" + rs.getString(1) + "</td>");
                  out.println("<td>" + rs.getString(2) + "</td>");
                  out.println("<td>" + rs.getString(3) + "</td>");
                  out.println("<td>" + rs.getString(4) + "</td>");
                  out.println("<td>" + rs.getString(5) + "</td>");
                  
                  // Check if this prescription is filled or not
                  if(rs.getString(6).equals("1")) {
                     out.println("<td align = \"right\">" + "Yes" + "</td>");
                  } else {
                     out.println("<td align = \"right\">" + "No" + "</td>");

                  }
                  out.println("</tr>");
               }
               rs.close();
               out.println("</table>");
               
               // HTML code for the "fill" prescription button
               out.println("<form action = \"FillPrescription\" method = \"POST\">");
               out.println("<button align=\"left\" type=\"submit\" class=\"btn btn-primary\" style=\"margin:2em 0 1.8em 0; width:50%;\" >Fill Prescription</button>");
               //out.println("<input type=\"hidden\" name=\"pres_id\" value=" + prescriptionID + ">");
               out.println("<input type=\"hidden\" name=\"patient_id\" value=" + patientID + ">");
               
               out.println("</form>");
               
               
               conn.commit();
            }
            else {
               out.println("<p> No Current Prescriptions Availa </p>");
            }
            
            out.println("</div></body></head></html>");
       
            
            
         } catch (SQLException e) {
            // Handle errors
            e.printStackTrace();
         }  
      } 
      else {
         //out.println("<!DOCTYPE HTML><html><body>");
         out.println("<!DOCTYPE HTML><head><link rel=\"stylesheet\" href=\"search.css\"><html><body><div class=\"container\" style=\"width:100%; color:white;\">");

         out.println("  <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\""
               + " integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
         out.println("  <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
         out.println("<link rel=\"stylesheet\" href=\"search.css\">");
         out.println("<p> Invalid Entry </p>");
         out.println("</div></body></head></html>");

      }

      
   }
   
   /** 
    * Test for valid user input
    * @return
    */
   private boolean validInput() 
   {
      //check for empty strings
      if(patientID == "" || !patientID.matches("[0-9]+")) {
         return false;
      }
      
      return true;
   }

}
