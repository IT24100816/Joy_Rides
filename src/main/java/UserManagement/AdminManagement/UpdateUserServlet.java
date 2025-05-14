package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@WebServlet("/UpdateUserServlet")
public class UpdateUserServlet extends HttpServlet {
    private static final String USER_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/users.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect("Users.jsp?errorMessage=Username not provided.");
            return;
        }

        String userDetails[] = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length == 5 && parts[0].trim().equals(username)) {
                    userDetails = parts;
                    break;
                }
            }
        } catch (IOException e) {
            response.sendRedirect("Users.jsp?errorMessage=Error loading user data: " + e.getMessage());
            return;
        }

        if (userDetails == null) {
            response.sendRedirect("Users.jsp?errorMessage=User not found for username: " + username);
            return;
        }

        // Set user details as request attributes for the update page
        request.setAttribute("username", userDetails[0]);
        request.setAttribute("email", userDetails[1]);
        request.setAttribute("city", userDetails[3]);
        request.setAttribute("nic", userDetails[4]);
        request.getRequestDispatcher("/AdminUserUpdate.jsp").forward(request, response);
    }
}