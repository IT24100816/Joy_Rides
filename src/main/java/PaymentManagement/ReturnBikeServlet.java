package PaymentManagement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ReturnBikeServlet")
public class ReturnBikeServlet extends HttpServlet {
    private static final String CONFIRMED_PAYMENT_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/confirmed_payment.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bikeName = request.getParameter("bikeName");
        if (bikeName == null || bikeName.trim().isEmpty()) {
            response.sendRedirect("profile.jsp");
            return;
        }

        try {
            // Read all lines from confirmed_payment.txt
            List<String> paymentLines = new ArrayList<>(Files.readAllLines(Paths.get(CONFIRMED_PAYMENT_FILE_PATH)));
            boolean removed = false;

            // Find and remove the line matching the bikeName
            for (int i = 0; i < paymentLines.size(); i++) {
                String[] paymentData = paymentLines.get(i).split("\\s*\\|\\s*");
                if (paymentData.length >= 2 && paymentData[1].trim().equals(bikeName)) {
                    paymentLines.remove(i);
                    removed = true;
                    break;
                }
            }

            // Write the updated lines back to confirmed_payment.txt
            if (removed) {
                Files.write(Paths.get(CONFIRMED_PAYMENT_FILE_PATH), paymentLines);
                // Optionally, update bike availability in Bikes.txt to "Available"
                updateBikeAvailability(bikeName, "Available");
                request.getSession().setAttribute("successMessage", "Bike returned successfully!");
            } else {
                System.out.println("No payment details found for bike: " + bikeName);
                request.getSession().setAttribute("errorMessage", "No payment details found for the bike.");
            }

            // Redirect to profile.jsp to reflect the changes
            response.sendRedirect("index.jsp");

        } catch (IOException e) {
            System.out.println("Error updating confirmed_payment.txt: " + e.getMessage());
            request.getSession().setAttribute("errorMessage", "Error updating payment records: " + e.getMessage());
            response.sendRedirect("profile.jsp");
        }
    }

    private void updateBikeAvailability(String bikeName, String availability) throws IOException {
        String bikesFilePath = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Bikes.txt";
        List<String> bikeLines = new ArrayList<>(Files.readAllLines(Paths.get(bikesFilePath)));
        boolean updated = false;

        for (int i = 0; i < bikeLines.size(); i++) {
            String[] bikeData = bikeLines.get(i).split("\\s*\\|\\s*");
            if (bikeData.length >= 5 && bikeData[0].trim().equals(bikeName)) {
                bikeData[4] = availability; // Update the availability field (index 4)
                bikeLines.set(i, String.join(" | ", bikeData));
                updated = true;
                break;
            }
        }

        if (updated) {
            Files.write(Paths.get(bikesFilePath), bikeLines);
            System.out.println("Updated bike availability for " + bikeName + " to " + availability);
        } else {
            System.out.println("No bike found to update availability for: " + bikeName);
        }
    }
}