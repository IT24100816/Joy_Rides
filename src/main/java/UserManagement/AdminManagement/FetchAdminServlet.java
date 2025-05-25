package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/FetchAdminServlet")
public class FetchAdminServlet extends HttpServlet {
    private static final String ADMIN_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Admin.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username not provided.");
            request.getRequestDispatcher("/UpdateAdmin.jsp").forward(request, response);
            return;
        }

        Map<String, String> adminDetails = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length >= 5 && parts[0].trim().equals(username)) {
                    adminDetails.put("username", parts[0].trim());
                    adminDetails.put("email", parts[1].trim());
                    adminDetails.put("city", parts[3].trim());
                    adminDetails.put("nic", parts[4].trim());
                    break;
                }
            }
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Error loading admin data: " + e.getMessage());
            request.getRequestDispatcher("/UpdateAdmin.jsp").forward(request, response);
            return;
        }

        if (adminDetails.isEmpty()) {
            request.setAttribute("errorMessage", "Admin not found for username: " + username);
        } else {
            request.setAttribute("adminDetails", adminDetails);
        }
        request.getRequestDispatcher("/UpdateAdmin.jsp").forward(request, response);
    }
}
