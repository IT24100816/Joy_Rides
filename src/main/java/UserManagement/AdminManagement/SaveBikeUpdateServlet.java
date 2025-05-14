package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/SaveBikeUpdateServlet")
public class SaveBikeUpdateServlet extends HttpServlet {
    private static final String BIKES_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Bikes.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String oldBikeName = request.getParameter("oldBikeName");
        String bikeName = request.getParameter("bikeName");
        String photo = request.getParameter("photo");
        String rentPerHour = request.getParameter("rentPerHour");
        String ownerStatus = request.getParameter("ownerStatus");

        if (bikeName == null || photo == null || rentPerHour == null || ownerStatus == null ||
                bikeName.trim().isEmpty() || photo.trim().isEmpty() || rentPerHour.trim().isEmpty() || ownerStatus.trim().isEmpty()) {
            response.sendRedirect("AdminBikeUpdate.jsp?errorMessage=All fields are required.");
            return;
        }

        // Read all bikes from Bikes.txt
        List<String> bikeLines = new ArrayList<>();
        boolean bikeFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(BIKES_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 3 && parts[0].trim().equals(oldBikeName)) {
                    // Replace the existing bike entry with updated details
                    bikeLines.add(bikeName + "|" + photo + "|" + rentPerHour + "|" + ownerStatus);
                    bikeFound = true;
                } else {
                    bikeLines.add(line);
                }
            }
        } catch (IOException e) {
            response.sendRedirect("AdminBikeUpdate.jsp?errorMessage=Error loading bike data: " + e.getMessage());
            return;
        }

        if (!bikeFound) {
            response.sendRedirect("AdminBikeUpdate.jsp?errorMessage=Bike not found: " + oldBikeName);
            return;
        }

        // Write updated bike data back to Bikes.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BIKES_FILE_PATH))) {
            for (String bikeLine : bikeLines) {
                writer.write(bikeLine);
                writer.newLine();
            }
        } catch (IOException e) {
            response.sendRedirect("AdminBikeUpdate.jsp?errorMessage=Error saving bike data: " + e.getMessage());
            return;
        }

        response.sendRedirect("AdminBikeDetails.jsp?successMessage=Bike details updated successfully for " + bikeName + "!");
    }
}