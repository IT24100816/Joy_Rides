package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/DemoteServlet")
public class DemoteServlet extends HttpServlet {
    private static final String ADMIN_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Admin.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect("AdminManagement.jsp?errorMessage=Username not provided.");
            return;
        }

        List<String> adminLines = new ArrayList<>();
        boolean demotedAdmin = false;

        // Step 1: Remove the admin from Admin.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length >= 5 && parts[0].trim().equals(username)) {
                    demotedAdmin = true; // Skip this line (effectively deleting it)
                } else {
                    adminLines.add(line);
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminManagement.jsp?errorMessage=Error loading admin data: " + e.getMessage());
            return;
        }

        // Step 2: Write the updated data back to Admin.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE_PATH))) {
            for (String adminLine : adminLines) {
                writer.write(adminLine);
                writer.newLine();
            }
        } catch (IOException e) {
            response.sendRedirect("AdminManagement.jsp?errorMessage=Error saving admin data: " + e.getMessage());
            return;
        }

        // Step 3: Redirect back to AdminManagement.jsp with a success message
        if (demotedAdmin) {
            response.sendRedirect("AdminManagement.jsp?successMessage=Admin '" + username + "' has been demoted to a user successfully!");
        } else {
            response.sendRedirect("AdminManagement.jsp?errorMessage=Admin not found for username: " + username);
        }
    }
}
