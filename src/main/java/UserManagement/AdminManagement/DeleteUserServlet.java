package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/DeleteUserServlet")
public class DeleteUserServlet extends HttpServlet {
    private static final String USER_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/users.txt";
    private static final String ADMIN_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Admin.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect("Users.jsp?errorMessage=Username not provided.");
            return;
        }

        // Step 1: Remove the user from users.txt and get their email for Admin.txt check
        List<String> userLines = new ArrayList<>();
        String userEmail = null;
        boolean userFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length == 5 && parts[0].trim().equals(username)) {
                    userEmail = parts[1].trim();
                    userFound = true;
                    continue; // Skip this user (effectively deleting them)
                }
                userLines.add(line);
            }
        } catch (IOException e) {
            response.sendRedirect("Users.jsp?errorMessage=Error loading user data: " + e.getMessage());
            return;
        }

        if (!userFound) {
            response.sendRedirect("Users.jsp?errorMessage=User not found for username: " + username);
            return;
        }

        // Write updated user data back to users.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {
            for (String userLine : userLines) {
                writer.write(userLine);
                writer.newLine();
            }
        } catch (IOException e) {
            response.sendRedirect("Users.jsp?errorMessage=Error saving user data: " + e.getMessage());
            return;
        }

        // Step 2: Check and remove from Admin.txt if the user is an admin
        List<String> adminLines = new ArrayList<>();
        boolean adminFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length == 5 && parts[0].trim().equals(username) && parts[1].trim().equals(userEmail)) {
                    adminFound = true;
                    continue; // Skip this admin (effectively deleting them)
                }
                adminLines.add(line);
            }
        } catch (IOException e) {
            response.sendRedirect("Users.jsp?errorMessage=Error loading admin data: " + e.getMessage());
            return;
        }

        // Write updated admin data back to Admin.txt if changes were made
        if (adminFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE_PATH))) {
                for (String adminLine : adminLines) {
                    writer.write(adminLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                response.sendRedirect("Users.jsp?errorMessage=Error saving admin data: " + e.getMessage());
                return;
            }
        }

        // Step 3: Redirect back to Users.jsp with a success message
        String successMessage = adminFound ?
                "User '" + username + "' has been deleted successfully from both users and admins!" :
                "User '" + username + "' has been deleted successfully!";
        response.sendRedirect("Users.jsp?successMessage=" + URLEncoder.encode(successMessage, "UTF-8"));
    }
}
