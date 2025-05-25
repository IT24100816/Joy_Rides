package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/AdminListServlet")
public class AdminListServlet extends HttpServlet {
    private static final String ADMIN_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Admin.txt";
    private static final String USER_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/users.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Map<String, Object>> userDataList = new ArrayList<>();
        Set<String> adminUsernames = new HashSet<>();

        // Read admin usernames from Admin.txt
        try (BufferedReader adminReader = new BufferedReader(new FileReader(ADMIN_FILE_PATH))) {
            String line;
            while ((line = adminReader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length >= 1) {
                    adminUsernames.add(parts[0].trim());
                    System.out.println("Admin found: " + parts[0].trim()); // Debugging
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading Admin.txt: " + e.getMessage()); // Debugging
            request.setAttribute("errorMessage", "Error loading admin data: " + e.getMessage());
            request.getRequestDispatcher("AdminManagement.jsp").forward(request, response);
            return;
        }

        // Read users from users.txt and check against admin usernames
        try (BufferedReader userReader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = userReader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(" \\| ");
                if (parts.length >= 5) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("username", parts[0].trim());
                    userData.put("email", parts[1].trim());
                    userData.put("city", parts[3].trim());
                    userData.put("nic", parts[4].trim());
                    boolean isAdmin = adminUsernames.contains(parts[0].trim());
                    userData.put("isAdmin", isAdmin);
                    userDataList.add(userData);
                    System.out.println("User processed: " + parts[0].trim() + ", isAdmin: " + isAdmin); // Debugging
                } else {
                    System.out.println("Invalid user line format: " + line); // Debugging
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users.txt: " + e.getMessage()); // Debugging
            request.setAttribute("errorMessage", "Error loading user data: " + e.getMessage());
            request.getRequestDispatcher("AdminManagement.jsp").forward(request, response);
            return;
        }

        System.out.println("Total users processed: " + userDataList.size()); // Debugging
        request.setAttribute("userDataList", userDataList);
        request.getRequestDispatcher("AdminManagement.jsp").forward(request, response);
    }
}
