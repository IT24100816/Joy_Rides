package RentAndRideManagement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UpdateRentalServlet")
public class UpdateRentalServlet extends HttpServlet {
    private static final String RENTAL_REQUESTS_FILE = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/RentalRequests.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        if (username == null) {
            username = (String) session.getAttribute("username");
        }
        String bikeName = request.getParameter("bikeName");
        String rentalDays = request.getParameter("rentalDays");
        String[] additionalServices = request.getParameterValues("additionalServices");
        String currentTotalPayment = request.getParameter("currentTotalPayment");
        String updatedTotalPayment = request.getParameter("updatedTotalPayment");
        String orderNumber = request.getParameter("orderNumber");
        String source = request.getParameter("source"); // Check if update is from admin

        System.out.println("UpdateRentalServlet: Processing update - bikeName: " + bikeName + ", username: " + username +
                ", orderNumber: " + orderNumber + ", rentalDays: " + rentalDays + ", updatedTotalPayment: " + updatedTotalPayment +
                ", additionalServices: " + (additionalServices != null ? String.join(",", additionalServices) : "none"));

        // Verify file exists and is writable
        File file = new File(RENTAL_REQUESTS_FILE);
        if (!file.exists()) {
            System.out.println("UpdateRentalServlet: File does not exist: " + RENTAL_REQUESTS_FILE);
            throw new ServletException("RentalRequests.txt does not exist at: " + RENTAL_REQUESTS_FILE);
        }
        if (!file.canWrite()) {
            System.out.println("UpdateRentalServlet: File is not writable: " + RENTAL_REQUESTS_FILE);
            throw new ServletException("Cannot write to RentalRequests.txt at: " + RENTAL_REQUESTS_FILE);
        }
        System.out.println("UpdateRentalServlet: File exists and is writable: " + RENTAL_REQUESTS_FILE);

        // Read and update RentalRequests.txt
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(RENTAL_REQUESTS_FILE));
            System.out.println("UpdateRentalServlet: Total lines read: " + lines.size());
            boolean updated = false;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) {
                    System.out.println("UpdateRentalServlet: Skipping empty line at index " + i);
                    continue;
                }
                System.out.println("UpdateRentalServlet: Checking line " + i + ": " + line);

                // Parse the labeled format
                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length == 5) {
                    String[] bikePart = parts[0].split("\\s*:\\s*");
                    String[] usernamePart = parts[1].split("\\s*:\\s*");
                    String[] orderPart = parts[2].split("\\s*:\\s*");
                    String[] daysPart = parts[3].split("\\s*:\\s*");
                    String[] servicesPart = parts[4].split("\\s*:\\s*");

                    if (bikePart.length == 2 && usernamePart.length == 2 && orderPart.length == 2 && daysPart.length == 2 && servicesPart.length == 2) {
                        String currentBikeName = bikePart[1].trim();
                        String currentUsername = usernamePart[1].trim();
                        String currentOrderNumber = orderPart[1].trim();

                        System.out.println("UpdateRentalServlet: Line " + i + " - BikeName: " + currentBikeName +
                                ", Username: " + currentUsername + ", OrderNumber: " + currentOrderNumber);

                        if (currentBikeName.equalsIgnoreCase(bikeName) &&
                                currentUsername.equalsIgnoreCase(username) &&
                                currentOrderNumber.equals(orderNumber)) {
                            System.out.println("UpdateRentalServlet: Match found at line " + i);
                            String newServices = (additionalServices != null && additionalServices.length > 0) ? String.join(", ", additionalServices) : "None";
                            String newLine = "Bike Name: " + currentBikeName + " | Username: " + currentUsername + " | Order Number: " + currentOrderNumber +
                                    " | Rental Days: " + rentalDays + " | Additional Services: " + newServices;
                            lines.set(i, newLine);
                            System.out.println("UpdateRentalServlet: Updated line to: " + newLine);
                            updated = true;
                            session.setAttribute("successMessage", "Rental updated successfully!");
                            break;
                        }
                    } else {
                        System.out.println("UpdateRentalServlet: Invalid format at line " + i + ", expected labeled fields, got: " + parts.length + " parts");
                    }
                } else {
                    System.out.println("UpdateRentalServlet: Invalid format at line " + i + ", expected 5 parts, got: " + parts.length);
                }
            }
            if (!updated) {
                System.out.println("UpdateRentalServlet: No matching record found for bikeName: " + bikeName +
                        ", username: " + username + ", orderNumber: " + orderNumber);
                session.setAttribute("errorMessage", "No matching rental record found.");
            } else {
                // Write back to file
                Files.write(Paths.get(RENTAL_REQUESTS_FILE), lines);
                System.out.println("UpdateRentalServlet: File write successful for " + RENTAL_REQUESTS_FILE);
            }
        } catch (IOException e) {
            System.out.println("UpdateRentalServlet: Error reading RentalRequests.txt: " + e.getMessage());
            session.setAttribute("errorMessage", "Failed to update rental details: " + e.getMessage());
            throw new ServletException("Failed to read RentalRequests.txt", e);
        }

        // Redirect or forward based on source
        if ("admin".equals(source)) {
            response.sendRedirect("RentDetails.jsp");
        } else {
            // Forward to UpdatePayment.jsp with parameters for user updates
            request.setAttribute("bikeName", bikeName);
            request.setAttribute("currentTotalPayment", currentTotalPayment);
            request.setAttribute("updatedTotalPayment", updatedTotalPayment);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
        }
    }
}
