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
    private static final String PAYMENT_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Payment.txt";
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

        // Validate inputs
        if (username == null || bikeName == null || updatedTotalPayment == null || filePart == null) {
            request.setAttribute("errorMessage", "Missing required fields or file upload.");
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Fetch rental days and additional services from RentalRequests.txt
        String rentalDays = "0";
        String additionalServices = "None";
        String orderNumber = "N/A";
        try {
            List<String> rentalLines = Files.readAllLines(Paths.get(RENTAL_REQUESTS_FILE));
            for (String line : rentalLines) {
                if (line.trim().isEmpty()) continue;
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
                        if (currentBikeName.equals(bikeName) && currentUsername.equals(username)) {
                            orderNumber = orderPart[1].trim();
                            rentalDays = daysPart[1].trim();
                            additionalServices = servicesPart[1].trim();
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Error reading RentalRequests.txt: " + e.getMessage());
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        if ("N/A".equals(orderNumber)) {
            request.setAttribute("errorMessage", "No matching rental request found for bike: " + bikeName + " and username: " + username);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Save the uploaded bank slip image
        String fileName = username + "_" + orderNumber + "_slip.jpg";
        String filePath = UPLOAD_DIRECTORY + fileName;
        try {
            File uploadDir = new File(UPLOAD_DIRECTORY);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            filePart.write(filePath);
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Error saving bank slip image: " + e.getMessage());
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Update Payment.txt
        File paymentFile = new File(PAYMENT_FILE_PATH);
        if (!paymentFile.exists()) {
            request.setAttribute("errorMessage", "Payment.txt does not exist at: " + PAYMENT_FILE_PATH);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }
        if (!paymentFile.canWrite()) {
            request.setAttribute("errorMessage", "Cannot write to Payment.txt at: " + PAYMENT_FILE_PATH);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        List<String> paymentLines = Files.readAllLines(Paths.get(PAYMENT_FILE_PATH));
        boolean updated = false;
        for (int i = 0; i < paymentLines.size(); i++) {
            String line = paymentLines.get(i);
            if (line.trim().isEmpty()) continue;
            String[] data = line.split("\\s*\\|\\s*");
            if (data.length == 9) {
                String currentOrderNumber = data[0].trim();
                String currentBikeName = data[1].trim();
                String currentUsername = data[2].trim();
                if (currentOrderNumber.equals(orderNumber) && currentBikeName.equals(bikeName) && currentUsername.equals(username)) {
                    String newLine = data[0] + " | " + data[1] + " | " + data[2] + " | " + data[3] + " | " + data[4] + " | " +
                            rentalDays + " | " + updatedTotalPayment + " | " + additionalServices + " | " + fileName;
                    paymentLines.set(i, newLine);
                    updated = true;
                    break;
                }
            }
        }

        if (!updated) {
            request.setAttribute("errorMessage", "No matching payment record found for bike: " + bikeName + ", username: " + username + ", order: " + orderNumber);
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Write updated lines back to Payment.txt
        try {
            Files.write(Paths.get(PAYMENT_FILE_PATH), paymentLines);
        } catch (IOException e) {
            request.setAttribute("errorMessage", "Error writing to Payment.txt: " + e.getMessage());
            request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
            return;
        }

        // Set success message and forward back to UpdatePayment.jsp
        request.setAttribute("successMessage", "Payment Successful!");
        request.setAttribute("bikeName", bikeName);
        request.setAttribute("currentTotalPayment", currentTotalPayment);
        request.setAttribute("updatedTotalPayment", updatedTotalPayment);
        request.getRequestDispatcher("/UpdatePayment.jsp").forward(request, response);
    }
}