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
    private static final String PAYMENT_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Payment.txt";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bikeName = request.getParameter("bikeName");
        if (bikeName == null || bikeName.trim().isEmpty()) {
            response.sendRedirect("profile.jsp");
            return;
        }

        try {
            // Read all lines from Payment.txt
            List<String> paymentLines = new ArrayList<>(Files.readAllLines(Paths.get(PAYMENT_FILE_PATH)));
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

            // Write the updated lines back to Payment.txt
            if (removed) {
                Files.write(Paths.get(PAYMENT_FILE_PATH), paymentLines);
            } else {
                // If no matching entry was found, you can handle this case if needed
                System.out.println("No payment details found for bike: " + bikeName);
            }

            // Redirect to index.jsp
            response.sendRedirect("index.jsp");

        } catch (IOException e) {
            // Log the error and redirect back to profile.jsp with an error message if needed
            System.out.println("Error updating Payment.txt: " + e.getMessage());
            response.sendRedirect("profile.jsp");
        }
    }
}