package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/SaveUserUpdateServlet")
public class SaveUserUpdateServlet extends HttpServlet {
    private static final String USER_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/users.txt";
    private static final String ADMIN_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Admin.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String oldUsername = request.getParameter("oldUsername"); // Hidden field or session-based if needed
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String city = request.getParameter("city");
        String nic = request.getParameter("nic");

        if (username == null || email == null || city == null || nic == null ||
                username.trim().isEmpty() || email.trim().isEmpty() || city.trim().isEmpty() || nic.trim().isEmpty()) {
            response.sendRedirect("AdminUserUpdate.jsp?errorMessage=All fields are required.");
            return;
        }

        // Fetch the old password from users.txt (assuming it's the 3rd field)
        String oldPassword = null;
        List<String> userLines = new ArrayList<>();
        boolean userFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length == 5 && parts[0].trim().equals(oldUsername != null ? oldUsername : username)) {
                    oldPassword = parts[2].trim();
                    userLines.add(username + " | " + email + " | " + oldPassword + " | " + city + " | " + nic);
                    userFound = true;
                } else {
                    userLines.add(line);
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminUserUpdate.jsp?errorMessage=Error loading user data: " + e.getMessage());
            return;
        }

        if (!userFound) {
            response.sendRedirect("AdminUserUpdate.jsp?errorMessage=User not found.");
            return;
        }

        // Write updated user data back to users.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {
            for (String userLine : userLines) {
                writer.write(userLine);
                writer.newLine();
            }
        } catch (IOException e) {
            response.sendRedirect("AdminUserUpdate.jsp?errorMessage=Error saving user data: " + e.getMessage());
            return;
        }

        // Check and update Admin.txt if the user is an admin
        List<String> adminLines = new ArrayList<>();
        boolean adminFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length == 5 && parts[0].trim().equals(oldUsername != null ? oldUsername : username) && parts[1].trim().equals(email)) {
                    adminLines.add(username + " | " + email + " | " + oldPassword + " | " + city + " | " + nic);
                    adminFound = true;
                } else {
                    adminLines.add(line);
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminUserUpdate.jsp?errorMessage=Error loading admin data: " + e.getMessage());
            return;
        }

        if (adminFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE_PATH))) {
                for (String adminLine : adminLines) {
                    writer.write(adminLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                response.sendRedirect("AdminUserUpdate.jsp?errorMessage=Error saving admin data: " + e.getMessage());
                return;
            }
        }

        response.sendRedirect("Users.jsp?successMessage=User details updated successfully for " + username + "!");
    }
}