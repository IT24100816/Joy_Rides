package UserManagement.AdminManagement;

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

@WebServlet("/DeleteRentalRequestServlet")
public class DeleteRentalRequestServlet extends HttpServlet {
    private static final String RENTAL_REQUESTS_FILE = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/RentalRequests.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderNumber = request.getParameter("orderNumber");

        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Order number is missing.");
            request.getRequestDispatcher("/RentDetails.jsp").forward(request, response);
            return;
        }

        // Verify file exists and is writable
        File file = new File(RENTAL_REQUESTS_FILE);
        if (!file.exists()) {
            request.setAttribute("errorMessage", "RentalRequests.txt does not exist at: " + RENTAL_REQUESTS_FILE);
            request.getRequestDispatcher("/RentDetails.jsp").forward(request, response);
            return;
        }
        if (!file.canWrite()) {
            request.setAttribute("errorMessage", "Cannot write to RentalRequests.txt at: " + RENTAL_REQUESTS_FILE);
            request.getRequestDispatcher("/RentDetails.jsp").forward(request, response);
            return;
        }

        // Read all lines from RentalRequests.txt
        List<String> lines = new ArrayList<>();
        boolean found = false;
        try {
            lines = Files.readAllLines(Paths.get(RENTAL_REQUESTS_FILE));
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length == 5) {
                    String[] orderPart = parts[2].split("\\s*:\\s*");
                    if (orderPart.length == 2 && orderPart[1].trim().equals(orderNumber)) {
                        lines.remove(i); // Remove the matching line
                        found = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Error reading RentalRequests.txt: " + e.getMessage());
            request.getRequestDispatcher("/RentDetails.jsp").forward(request, response);
            return;
        }

        if (!found) {
            request.setAttribute("errorMessage", "No rental request found with order number: " + orderNumber);
            request.getRequestDispatcher("/RentDetails.jsp").forward(request, response);
            return;
        }

        // Write the updated lines back to RentalRequests.txt
        try {
            Files.write(Paths.get(RENTAL_REQUESTS_FILE), lines);
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Error writing to RentalRequests.txt: " + e.getMessage());
            request.getRequestDispatcher("/RentDetails.jsp").forward(request, response);
            return;
        }

        // Redirect to RentDetails.jsp to refresh the table
        response.sendRedirect("RentDetails.jsp");
    }
}
