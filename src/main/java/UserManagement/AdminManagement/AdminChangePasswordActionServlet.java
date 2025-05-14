package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/AdminChangePasswordActionServlet")
public class AdminChangePasswordActionServlet extends HttpServlet {
    private static final String ADMIN_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Admin.txt";
    private static final String USER_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/users.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("username", username);
            request.setAttribute("errorMessage", "New passwords do not match.");
            request.getRequestDispatcher("/AdminChangePassword.jsp").forward(request, response);
            return;
        }

        List<String> adminLines = new ArrayList<>();
        boolean updatedAdmin = false;
        String currentHashedPassword = null;
        String adminEmail = null;

        // Step 1: Read Admin.txt to find the current hashed password and email
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length >= 5 && parts[0].trim().equals(username)) {
                    currentHashedPassword = parts[2].trim();
                    adminEmail = parts[1].trim();
                    adminLines.add(line);
                } else {
                    adminLines.add(line);
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Error loading admin data: " + e.getMessage());
            return;
        }

        // Step 2: Verify the current password
        if (currentHashedPassword == null || !BCrypt.checkpw(currentPassword, currentHashedPassword)) {
            request.setAttribute("username", username);
            request.setAttribute("errorMessage", "Current password is incorrect.");
            request.getRequestDispatcher("/AdminChangePassword.jsp").forward(request, response);
            return;
        }

        // Step 3: Update the password in Admin.txt
        String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
            String line;
            adminLines.clear();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length >= 5 && parts[0].trim().equals(username)) {
                    adminLines.add(parts[0] + " | " + parts[1] + " | " + hashedNewPassword + " | " + parts[3] + " | " + parts[4]);
                    updatedAdmin = true;
                } else {
                    adminLines.add(line);
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Error loading admin data: " + e.getMessage());
            return;
        }

        // Step 4: Write the updated data back to Admin.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE_PATH))) {
            for (String adminLine : adminLines) {
                writer.write(adminLine);
                writer.newLine();
            }
        } catch (IOException e) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Error saving admin data: " + e.getMessage());
            return;
        }

        // Step 5: Update the password in users.txt if the user exists
        if (updatedAdmin && adminEmail != null) {
            List<String> userLines = new ArrayList<>();
            boolean updatedUser = false;

            // Read users.txt to find the matching user
            try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(" \\| ");
                    if (parts.length >= 5 && parts[0].trim().equals(username) && parts[1].trim().equals(adminEmail)) {
                        userLines.add(parts[0] + " | " + parts[1] + " | " + hashedNewPassword + " | " + parts[3] + " | " + parts[4]);
                        updatedUser = true;
                    } else {
                        userLines.add(line);
                    }
                }
            } catch (IOException e) {
                response.sendRedirect("AdminProfile.jsp?errorMessage=Error loading user data: " + e.getMessage());
                return;
            }

            // Write the updated data back to users.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {
                for (String userLine : userLines) {
                    writer.write(userLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                response.sendRedirect("AdminProfile.jsp?errorMessage=Error saving user data: " + e.getMessage());
                return;
            }

            if (updatedUser) {
                response.sendRedirect("AdminProfile.jsp?successMessage=Password changed successfully in both Admin and User records!");
            } else {
                response.sendRedirect("AdminProfile.jsp?successMessage=Password changed successfully! Note: No matching user found in users.txt.");
            }
        } else {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Admin not found for username: " + username);
        }
    }
}