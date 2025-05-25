package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/PromoteServlet")
public class PromoteServlet extends HttpServlet {
    private static final String USER_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/users.txt";
    private static final String ADMIN_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Admin.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect("AdminManagement.jsp?errorMessage=Username not provided.");
            return;
        }

        String userDetails = null;
        // Step 1: Find the user's full details in users.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length == 5 && parts[0].trim().equals(username)) {
                    userDetails = line; // Store the entire line (username | email | password | city | nic)
                    break;
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminManagement.jsp?errorMessage=Error loading user data: " + e.getMessage());
            return;
        }

        if (userDetails == null) {
            response.sendRedirect("AdminManagement.jsp?errorMessage=User not found for username: " + username);
            return;
        }

        // Step 2: Append the user details to Admin.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE_PATH, true))) {
            writer.write(userDetails);
            writer.newLine();
        } catch (IOException e) {
            response.sendRedirect("AdminManagement.jsp?errorMessage=Error saving admin data: " + e.getMessage());
            return;
        }

        // Step 3: Redirect back to AdminManagement.jsp with a success message
        response.sendRedirect("AdminManagement.jsp?successMessage=User '" + username + "' has been promoted to an admin successfully!");
    }
}

