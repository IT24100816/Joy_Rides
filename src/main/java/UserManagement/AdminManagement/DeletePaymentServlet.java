package UserManagement.AdminManagement;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeletePaymentServlet")
public class DeletePaymentServlet extends HttpServlet {
    private static final String CONFIRMED_PAYMENT_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/confirmed_payment.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Log the incoming request
        System.out.println("DeletePaymentServlet: Received request with orderNumber: " + request.getParameter("orderNumber"));

        String orderNumber = request.getParameter("orderNumber");

        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            System.out.println("DeletePaymentServlet: Error - Order number is missing or empty.");
            request.setAttribute("errorMessage", "Order number is missing.");
            request.getRequestDispatcher("/PaymentDetails.jsp").forward(request, response);
            return;
        }

        // Verify file exists and is writable
        File file = new File(CONFIRMED_PAYMENT_FILE_PATH);
        if (!file.exists()) {
            System.out.println("DeletePaymentServlet: Error - confirmed_payment.txt does not exist at: " + CONFIRMED_PAYMENT_FILE_PATH);
            request.setAttribute("errorMessage", "confirmed_payment.txt does not exist at: " + CONFIRMED_PAYMENT_FILE_PATH);
            request.getRequestDispatcher("/PaymentDetails.jsp").forward(request, response);
            return;
        }
        if (!file.canWrite()) {
            System.out.println("DeletePaymentServlet: Error - Cannot write to confirmed_payment.txt at: " + CONFIRMED_PAYMENT_FILE_PATH);
            request.setAttribute("errorMessage", "Cannot write to confirmed_payment.txt at: " + CONFIRMED_PAYMENT_FILE_PATH);
            request.getRequestDispatcher("/PaymentDetails.jsp").forward(request, response);
            return;
        }

        // Read all lines from confirmed_payment.txt
        List<String> lines = new ArrayList<>();
        boolean found = false;
        try {
            lines = Files.readAllLines(Paths.get(CONFIRMED_PAYMENT_FILE_PATH));
            System.out.println("DeletePaymentServlet: Total lines read: " + lines.size());
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) {
                    System.out.println("DeletePaymentServlet: Skipping empty line at index " + i);
                    continue;
                }

                String[] parts = line.split(" \\| ");
                if (parts.length >= 9) {
                    String currentOrderNumber = parts[0].trim();
                    System.out.println("DeletePaymentServlet: Checking line " + i + " with orderNumber: " + currentOrderNumber);
                    if (currentOrderNumber.equalsIgnoreCase(orderNumber)) {
                        lines.remove(i); // Remove the matching line
                        found = true;
                        System.out.println("DeletePaymentServlet: Removed line with orderNumber: " + orderNumber);
                        break;
                    }
                } else {
                    System.out.println("DeletePaymentServlet: Invalid format at line " + i + ", expected >= 9 parts, got: " + parts.length);
                }
            }
        } catch (IOException e) {
            System.out.println("DeletePaymentServlet: Error reading confirmed_payment.txt: " + e.getMessage());
            request.setAttribute("errorMessage", "Error reading confirmed_payment.txt: " + e.getMessage());
            request.getRequestDispatcher("/PaymentDetails.jsp").forward(request, response);
            return;
        }

        if (!found) {
            System.out.println("DeletePaymentServlet: Error - No payment record found with orderNumber: " + orderNumber);
            request.setAttribute("errorMessage", "No payment record found with orderNumber: " + orderNumber);
            request.getRequestDispatcher("/PaymentDetails.jsp").forward(request, response);
            return;
        }

        // Write the updated lines back to confirmed_payment.txt
        try {
            Files.write(Paths.get(CONFIRMED_PAYMENT_FILE_PATH), lines);
            System.out.println("DeletePaymentServlet: Successfully wrote updated content to confirmed_payment.txt");
        } catch (IOException e) {
            System.out.println("DeletePaymentServlet: Error writing to confirmed_payment.txt: " + e.getMessage());
            request.setAttribute("errorMessage", "Error writing to confirmed_payment.txt: " + e.getMessage());
            request.getRequestDispatcher("/PaymentDetails.jsp").forward(request, response);
            return;
        }

        // Redirect to PaymentDetails.jsp with a success parameter
        System.out.println("DeletePaymentServlet: Redirecting to PaymentDetails.jsp with deleted=true");
        response.sendRedirect("PaymentDetails.jsp?deleted=true&orderNumber=" + URLEncoder.encode(orderNumber, StandardCharsets.UTF_8));
    }
}