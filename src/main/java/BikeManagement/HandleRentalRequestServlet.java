package BikeManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/HandleRentalRequestServlet")
public class HandleRentalRequestServlet extends HttpServlet {
    private static final String PAYMENT_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Joy_Rides/src/main/resources/Payment.txt";
    private static final String CONFIRMED_PAYMENT_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Joy_Rides/src/main/resources/confirmed_payment.txt";
    private static final String BIKES_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Joy_Rides/src/main/resources/Bikes.txt";
    private final BikeAvailabilityManager bikeAvailabilityManager;
    private static final Logger LOGGER = Logger.getLogger(HandleRentalRequestServlet.class.getName());

    public HandleRentalRequestServlet() {
        this.bikeAvailabilityManager = new BikeAvailabilityManagerImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Processing request in HandleRentalRequestServlet at " + new java.util.Date());

        String requestId = request.getParameter("requestId");
        String action = request.getParameter("action");
        String bikeName = request.getParameter("bikeName");
        String rentalDays = request.getParameter("rentalDays");

        // Log incoming parameters for debugging
        LOGGER.info("Received parameters: requestId=" + requestId + ", action=" + action + ", bikeName=" + bikeName + ", rentalDays=" + rentalDays);

        // Only require requestId, action, and bikeName; rentalDays is optional (only for accept)
        if (requestId == null || action == null || bikeName == null) {
            LOGGER.warning("Missing required parameters: requestId=" + requestId + ", action=" + action + ", bikeName=" + bikeName);
            request.setAttribute("errorMessage", "Missing required parameters.");
            response.sendRedirect("BikeRequests.jsp");
            return;
        }

        // Read Payment.txt to find the request
        List<String> paymentLines = new ArrayList<>();
        String renterUsername = null;
        String fullRequestLine = null;
        boolean requestFound = false;

        try {
            LOGGER.info("Reading Payment.txt");
            paymentLines = new ArrayList<>(Files.readAllLines(Paths.get(PAYMENT_FILE_PATH)));
            for (int i = 0; i < paymentLines.size(); i++) {
                String line = paymentLines.get(i).trim();
                if (line.isEmpty()) continue;
                String[] paymentData = line.split("\\s*\\|\\s*");
                if (paymentData.length != 9) {
                    LOGGER.warning("Skipping malformed line in Payment.txt: " + line);
                    continue; // Skip malformed lines
                }
                if (paymentData[0].equals(requestId)) {
                    requestFound = true;
                    renterUsername = paymentData[2];
                    fullRequestLine = line; // Store the full line for confirmed_payment.txt
                    if (action.equals("accept")) {
                        LOGGER.info("Accepting rental request for bike: " + bikeName);
                        if (rentalDays == null) {
                            LOGGER.severe("Missing rentalDays for accept action");
                            request.setAttribute("errorMessage", "Rental days are required for accept action.");
                            response.sendRedirect("BikeRequests.jsp");
                            return;
                        }
                        long days;
                        try {
                            days = Long.parseLong(rentalDays.trim());
                        } catch (NumberFormatException e) {
                            LOGGER.severe("Invalid rentalDays format: " + rentalDays);
                            request.setAttribute("errorMessage", "Invalid rental days format.");
                            response.sendRedirect("BikeRequests.jsp");
                            return;
                        }
                        long rentalEndTime = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000);
                        bikeAvailabilityManager.updateBikeAvailability(bikeName, "Not Available");
                        // Remove the line from paymentLines
                        paymentLines.remove(i);
                        // Save to confirmed_payment.txt
                        saveToConfirmedPayment(fullRequestLine, rentalEndTime);
                        request.getSession().setAttribute("successMessage", "Bike Rented Successfully!");
                        request.getSession().setAttribute("renterUsername_" + bikeName, renterUsername);
                        request.getSession().setAttribute("rentalEndTime_" + bikeName, rentalEndTime);
                        request.getSession().setAttribute("isRented_" + bikeName, true);
                    } else if (action.equals("reject")) {
                        LOGGER.info("Rejecting rental request for bike: " + bikeName);
                        // Remove the line from paymentLines for reject
                        paymentLines.remove(i);
                        LOGGER.info("Removed requestId " + requestId + " from paymentLines for reject action");
                    }
                    break; // Exit loop after processing the request
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to read Payment.txt: " + e.getMessage());
            request.setAttribute("errorMessage", "Failed to read payment records: " + e.getMessage());
            response.sendRedirect("BikeRequests.jsp");
            return;
        }

        if (!requestFound) {
            LOGGER.warning("Rental request not found for requestId: " + requestId);
            request.setAttribute("errorMessage", "Rental request not found.");
            response.sendRedirect("BikeRequests.jsp");
            return;
        }

        // Write updated payments back to Payment.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PAYMENT_FILE_PATH))) {
            LOGGER.info("Writing updated payment requests to Payment.txt");
            for (String line : paymentLines) {
                if (!line.trim().isEmpty()) {
                    writer.write(line);
                    writer.newLine();
                    LOGGER.info("Written line to Payment.txt: " + line);
                }
            }
            LOGGER.info("Successfully updated Payment.txt with " + paymentLines.size() + " lines");
        } catch (IOException e) {
            LOGGER.severe("Failed to update Payment.txt: " + e.getMessage());
            request.setAttribute("errorMessage", "Failed to update payment records: " + e.getMessage());
            response.sendRedirect("BikeRequests.jsp");
            return;
        }

        // Verify the file update (optional debug step)
        List<String> updatedLines = Files.readAllLines(Paths.get(PAYMENT_FILE_PATH));
        LOGGER.info("Verified Payment.txt contains " + updatedLines.size() + " lines after update");

        // Clear the queue to force reinitialization with updated Payment.txt
        getServletContext().removeAttribute("rentalRequestQueue");

        // Ensure redirection occurs
        if (action.equals("accept")) {
            LOGGER.info("Attempting redirect to Bikes.jsp with bikeName: " + bikeName);
            try {
                response.sendRedirect("Bikes.jsp?bikeName=" + java.net.URLEncoder.encode(bikeName, "UTF-8"));
                LOGGER.info("Redirect to Bikes.jsp executed successfully");
            } catch (Exception e) {
                LOGGER.severe("Failed to redirect to Bikes.jsp: " + e.getMessage());
                response.sendRedirect("BikeRequests.jsp?error=RedirectFailed");
            }
        } else {
            LOGGER.info("Redirecting to BikeRequestsServlet with bikeName: " + bikeName);
            response.sendRedirect("BikeRequestsServlet?bikeName=" + java.net.URLEncoder.encode(bikeName, "UTF-8"));
        }
    }

    private void saveToConfirmedPayment(String requestLine, long rentalEndTime) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIRMED_PAYMENT_FILE_PATH, true))) {
            // Split the original request line to modify the additionalNotes field
            String[] paymentData = requestLine.split("\\s*\\|\\s*");
            if (paymentData.length != 9) {
                LOGGER.severe("Invalid payment line format: " + requestLine);
                return;
            }
            // Update the additionalNotes field (index 8) to "Processed,rentalEndTime"
            paymentData[8] = "Processed," + rentalEndTime;
            // Reconstruct the line
            String confirmedLine = String.join(" | ", paymentData);
            writer.write(confirmedLine);
            writer.newLine();
            LOGGER.info("Saved confirmed payment to confirmed_payment.txt: " + confirmedLine);
        } catch (IOException e) {
            LOGGER.severe("Failed to save to confirmed_payment.txt: " + e.getMessage());
        }
    }
}
