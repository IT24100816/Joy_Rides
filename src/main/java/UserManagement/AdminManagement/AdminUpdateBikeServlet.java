package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@WebServlet("/AdminUpdateBikeServlet")
public class AdminUpdateBikeServlet extends HttpServlet {
    private static final String BIKES_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Bikes.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bikeName = request.getParameter("bikeName");
        if (bikeName == null || bikeName.trim().isEmpty()) {
            response.sendRedirect("AdminBikeDetails.jsp?errorMessage=Bike name not provided.");
            return;
        }

        String[] bikeDetails = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(BIKES_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 3 && parts[0].trim().equals(bikeName)) {
                    bikeDetails = parts;
                    break;
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminBikeDetails.jsp?errorMessage=Error loading bike data: " + e.getMessage());
            return;
        }

        if (bikeDetails == null) {
            response.sendRedirect("AdminBikeDetails.jsp?errorMessage=Bike not found for name: " + bikeName);
            return;
        }

        // Set bike details as request attributes for the update page
        request.setAttribute("bikeName", bikeDetails[0]);
        request.setAttribute("photo", bikeDetails.length > 1 ? bikeDetails[1].trim() : "N/A");
        request.setAttribute("rentPerHour", bikeDetails.length > 2 ? bikeDetails[2].trim() : "N/A");
        request.setAttribute("ownerStatus", bikeDetails.length > 3 ? bikeDetails[3].trim() + " - " + (bikeDetails.length > 4 ? bikeDetails[4].trim() : "N/A") : "N/A");
        request.getRequestDispatcher("/AdminBikeUpdate.jsp").forward(request, response);
    }
}