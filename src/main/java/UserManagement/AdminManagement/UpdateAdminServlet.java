package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/UpdateAdminServlet")
public class UpdateAdminServlet extends HttpServlet {
    private static final String ADMIN_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Admin.txt";
    private static final String USER_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/users.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String originalUsername = (String) session.getAttribute("username");
        String newUsername = request.getParameter("username");
        String email = request.getParameter("email");
        String city = request.getParameter("city");
        String nic = request.getParameter("nic");

        // Validate input
        if (originalUsername == null || newUsername == null || email == null || city == null || nic == null ||
                originalUsername.trim().isEmpty() || newUsername.trim().isEmpty() || email.trim().isEmpty() || city.trim().isEmpty() || nic.trim().isEmpty()) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=All fields are required.");
            return;
        }

        // Step 1: Check if the new username is already taken in Admin.txt or users.txt
        boolean usernameTaken = false;
        if (!newUsername.equals(originalUsername)) {
            // Check Admin.txt
            try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(" \\| ");
                    if (parts.length >= 5 && parts[0].trim().equals(newUsername)) {
                        usernameTaken = true;
                        break;
                    }
                }
            } catch (IOException e) {
                response.sendRedirect("AdminProfile.jsp?errorMessage=Error checking admin data: " + e.getMessage());
                return;
            }

            // Check users.txt
            if (!usernameTaken) {
                try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(" \\| ");
                        if (parts.length >= 5 && parts[0].trim().equals(newUsername)) {
                            usernameTaken = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    response.sendRedirect("AdminProfile.jsp?errorMessage=Error checking user data: " + e.getMessage());
                    return;
                }
            }

            if (usernameTaken) {
                response.sendRedirect("AdminProfile.jsp?errorMessage=Username '" + newUsername + "' is already taken.");
                return;
            }
        }

        List<String> adminLines = new ArrayList<>();
        boolean updatedAdmin = false;
        String adminPassword = null;

        // Step 2: Update Admin.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length >= 5 && parts[0].trim().equals(originalUsername)) {
                    adminPassword = parts[2].trim(); // Store the password to keep it unchanged
                    adminLines.add(newUsername + " | " + email + " | " + adminPassword + " | " + city + " | " + nic);
                    updatedAdmin = true;
                } else {
                    adminLines.add(line);
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Error loading admin data: " + e.getMessage());
            return;
        }

        // Step 3: Write the updated data back to Admin.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE_PATH))) {
            for (String adminLine : adminLines) {
                writer.write(adminLine);
                writer.newLine();
            }
        } catch (IOException e) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Error saving admin data: " + e.getMessage());
            return;
        }

        // Step 4: Update users.txt if the user exists
        if (updatedAdmin) {
            List<String> userLines = new ArrayList<>();
            boolean updatedUser = false;

            // Read users.txt to find the matching user
            try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(" \\| ");
                    if (parts.length >= 5 && parts[0].trim().equals(originalUsername)) {
                        String userPassword = parts[2].trim(); // Keep the user's password unchanged
                        userLines.add(newUsername + " | " + email + " | " + userPassword + " | " + city + " | " + nic);
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

            // Step 5: Update session attributes
            session.setAttribute("username", newUsername);
            session.setAttribute("email", email);
            session.setAttribute("city", city);
            session.setAttribute("nic", nic);

            // Step 6: Redirect with appropriate message
            if (updatedUser) {
                response.sendRedirect("AdminProfile.jsp?successMessage=Profile updated successfully in both Admin and User records!");
            } else {
                response.sendRedirect("AdminProfile.jsp?successMessage=Profile updated successfully! Note: No matching user found in users.txt.");
            }
        } else {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Admin not found for username: " + originalUsername);
        }
    }
}
