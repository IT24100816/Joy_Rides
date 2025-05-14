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

@WebServlet("/DeleteAdminServlet")
public class DeleteAdminServlet extends HttpServlet {
    private static final String ADMIN_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Admin.txt";
    private static final String USER_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/users.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Username not provided.");
            return;
        }

        List<String> adminLines = new ArrayList<>();
        boolean deletedAdmin = false;

        // Step 1: Remove the admin from Admin.txt
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length >= 5 && parts[0].trim().equals(username)) {
                    deletedAdmin = true; // Skip this line (effectively deleting it)
                } else {
                    adminLines.add(line);
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Error loading admin data: " + e.getMessage());
            return;
        }

        // Step 2: Write the updated data back to Admin.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ADMIN_FILE_PATH))) {
            for (String adminLine : adminLines) {
                writer.write(adminLine);
                writer.newLine();
            }
        } catch (IOException e) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Error saving admin data: " + e.getMessage());
            return;
        }

        // Step 3: Remove the user from users.txt if they exist
        if (deletedAdmin) {
            List<String> userLines = new ArrayList<>();
            boolean deletedUser = false;

            // Read users.txt to find the matching user
            try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(" \\| ");
                    if (parts.length >= 5 && parts[0].trim().equals(username)) {
                        deletedUser = true; // Skip this line (effectively deleting it)
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

            // Step 4: Invalidate the session to log out the admin
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Step 5: Redirect to index.jsp as a guest
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Admin not found for username: " + username);
        }
    }
}