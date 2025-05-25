package PaymentManagement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/UpdatePaymentServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class UpdatePaymentServlet extends HttpServlet {
    private static final String CONFIRMED_PAYMENT_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/confirmed_payment.txt";
    private static final String RENTAL_REQUESTS_FILE = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/RentalRequests.txt";
    private static final String UPLOAD_DIRECTORY = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/uploads/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String bikeName = request.getParameter("bikeName");
        String currentTotalPayment = request.getParameter("currentTotalPayment");
        String updatedTotalPayment = request.getParameter("updatedTotalPayment");
        Part filePart = request.getPart("bankSlip");

        // Log inputs
        System.out.println("UpdatePaymentServlet: Received - username: " + username + ", bikeName: " + bikeName +
                ", currentTotalPayment: " + currentTotalPayment + ", updatedTotalPayment: " + updatedTotalPayment +
                ", filePart: " + (filePart != null ? filePart.getSubmittedFileName() : "null"));

        // Validate inputs
        if (username == null || bikeName == null || updatedTotalPayment == null || filePart == null) {
            String errorMessage = "Missing required fields or file upload.";
            System.out.println("UpdatePaymentServlet: " + errorMessage);
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Fetch rental days and additional services from RentalRequests.txt
        String rentalDays = "0";
        String additionalServices = "None";
        String orderNumber = "N/A";
        try {
            List<String> rentalLines = Files.readAllLines(Paths.get(RENTAL_REQUESTS_FILE));
            System.out.println("UpdatePaymentServlet: Total lines in RentalRequests.txt: " + rentalLines.size());
            for (String line : rentalLines) {
                if (line.trim().isEmpty()) {
                    System.out.println("UpdatePaymentServlet: Skipping empty line in RentalRequests.txt");
                    continue;
                }
                System.out.println("UpdatePaymentServlet: Checking RentalRequests line: " + line);
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
                        System.out.println("UpdatePaymentServlet: Parsed - BikeName: " + currentBikeName + ", Username: " + currentUsername);
                        if (currentBikeName.equalsIgnoreCase(bikeName) && currentUsername.equalsIgnoreCase(username)) {
                            orderNumber = orderPart[1].trim();
                            rentalDays = daysPart[1].trim();
                            additionalServices = servicesPart[1].trim();
                            System.out.println("UpdatePaymentServlet: Match found - OrderNumber: " + orderNumber +
                                    ", RentalDays: " + rentalDays + ", AdditionalServices: " + additionalServices);
                            break;
                        }
                    } else {
                        System.out.println("UpdatePaymentServlet: Invalid RentalRequests line format: " + line);
                    }
                } else {
                    System.out.println("UpdatePaymentServlet: Malformed RentalRequests line: " + line);
                }
            }
        } catch (IOException e) {
            String errorMessage = "Error reading RentalRequests.txt: " + e.getMessage();
            System.out.println("UpdatePaymentServlet: " + errorMessage);
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        if ("N/A".equals(orderNumber)) {
            String errorMessage = "No matching rental request found for bike: " + bikeName + " and username: " + username;
            System.out.println("UpdatePaymentServlet: " + errorMessage);
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Save the uploaded bank slip image
        String fileName = username + "_" + orderNumber + "_slip.jpg";
        String filePath = UPLOAD_DIRECTORY + fileName;
        System.out.println("UpdatePaymentServlet: Saving bank slip to: " + filePath);
        try {
            File uploadDir = new File(UPLOAD_DIRECTORY);
            if (!uploadDir.exists()) {
                System.out.println("UpdatePaymentServlet: Creating upload directory: " + UPLOAD_DIRECTORY);
                uploadDir.mkdirs();
            }
            filePart.write(filePath);
            System.out.println("UpdatePaymentServlet: Bank slip saved successfully: " + fileName);
        } catch (IOException e) {
            String errorMessage = "Error saving bank slip image: " + e.getMessage();
            System.out.println("UpdatePaymentServlet: " + errorMessage);
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Update confirmed_payment.txt
        File paymentFile = new File(CONFIRMED_PAYMENT_FILE_PATH);
        if (!paymentFile.exists()) {
            String errorMessage = "confirmed_payment.txt does not exist at: " + CONFIRMED_PAYMENT_FILE_PATH;
            System.out.println("UpdatePaymentServlet: " + errorMessage);
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }
        if (!paymentFile.canWrite()) {
            String errorMessage = "Cannot write to confirmed_payment.txt at: " + CONFIRMED_PAYMENT_FILE_PATH;
            System.out.println("UpdatePaymentServlet: " + errorMessage);
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        List<String> paymentLines = Files.readAllLines(Paths.get(CONFIRMED_PAYMENT_FILE_PATH));
        System.out.println("UpdatePaymentServlet: Total lines in confirmed_payment.txt: " + paymentLines.size());
        boolean updated = false;
        for (int i = 0; i < paymentLines.size(); i++) {
            String line = paymentLines.get(i);
            if (line.trim().isEmpty()) {
                System.out.println("UpdatePaymentServlet: Skipping empty line in confirmed_payment.txt");
                continue;
            }
            System.out.println("UpdatePaymentServlet: Checking confirmed_payment line: " + line);
            String[] data = line.split("\\s*\\|\\s*");
            if (data.length == 9) {
                String currentOrderNumber = data[0].trim();
                String currentBikeName = data[1].trim();
                String currentUsername = data[2].trim();
                System.out.println("UpdatePaymentServlet: Parsed - OrderNumber: " + currentOrderNumber +
                        ", BikeName: " + currentBikeName + ", Username: " + currentUsername);
                if (currentOrderNumber.equalsIgnoreCase(orderNumber) &&
                        currentBikeName.equalsIgnoreCase(bikeName) &&
                        currentUsername.equalsIgnoreCase(username)) {
                    // Recalculate rental end time based on updated rental days
                    long rentalEndTime = calculateRentalEndTime(rentalDays);
                    String newLine = data[0] + " | " + data[1] + " | " + data[2] + " | " + data[3] + " | " +
                            rentalDays + " | " + updatedTotalPayment + " | " + additionalServices + " | " + fileName + " | Processed," + rentalEndTime;
                    paymentLines.set(i, newLine);
                    updated = true;
                    System.out.println("UpdatePaymentServlet: Updated line to: " + newLine);
                    break;
                }
            } else {
                System.out.println("UpdatePaymentServlet: Malformed confirmed_payment line: " + line);
            }
        }

        if (!updated) {
            String errorMessage = "No matching payment record found for bike: " + bikeName + ", username: " + username + ", order: " + orderNumber;
            System.out.println("UpdatePaymentServlet: " + errorMessage);
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Write updated lines back to confirmed_payment.txt
        try {
            System.out.println("UpdatePaymentServlet: Writing updated lines to confirmed_payment.txt");
            Files.write(Paths.get(CONFIRMED_PAYMENT_FILE_PATH), paymentLines);
            System.out.println("UpdatePaymentServlet: Write successful");
        } catch (IOException e) {
            String errorMessage = "Error writing to confirmed_payment.txt: " + e.getMessage();
            System.out.println("UpdatePaymentServlet: " + errorMessage);
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Set success message and forward back to UpdatePayment.jsp
        System.out.println("UpdatePaymentServlet: Update successful, forwarding to UpdatePayment.jsp");
        request.setAttribute("successMessage", "Payment Updated Successfully!");
        request.setAttribute("bikeName", bikeName);
        request.setAttribute("currentTotalPayment", currentTotalPayment);
        request.setAttribute("updatedTotalPayment", updatedTotalPayment);
        request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
    }

    private long calculateRentalEndTime(String rentalDaysStr) {
        try {
            int rentalDays = Integer.parseInt(rentalDaysStr);
            // Current time in milliseconds
            long currentTime = System.currentTimeMillis();
            // Calculate rental period in milliseconds (rentalDays * 24 hours * 60 minutes * 60 seconds * 1000 milliseconds)
            long rentalPeriod = rentalDays * 24L * 60 * 60 * 1000;
            // Return the end time
            long endTime = currentTime + rentalPeriod;
            System.out.println("UpdatePaymentServlet: Calculated rental end time: " + endTime + " for " + rentalDays + " days");
            return endTime;
        } catch (NumberFormatException e) {
            // Default to 1 day if parsing fails
            long defaultEndTime = System.currentTimeMillis() + (24L * 60 * 60 * 1000);
            System.out.println("UpdatePaymentServlet: Failed to parse rentalDays, using default: " + defaultEndTime);
            return defaultEndTime;
        }
    }
}