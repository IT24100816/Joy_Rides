package BikeManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/BikeRequestsServlet")
public class BikeRequestsServlet extends HttpServlet {
    private static final String PAYMENT_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Payment.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the bikeName parameter
        String bikeName = request.getParameter("bikeName");

        // Debug: Log the request
        System.out.println("BikeRequestsServlet: Received request for bikeName: " + bikeName);

        // Validate bikeName
        if (bikeName == null || bikeName.trim().isEmpty()) {
            System.out.println("BikeRequestsServlet: Bike name is missing");
            request.setAttribute("errorMessage", "Bike name is missing.");
            request.getRequestDispatcher("BikeRequests.jsp").forward(request, response);
            return;
        }

        // Fetch payment requests for the specified bike from Payment.txt
        List<RentalRequest> rentalRequests = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(PAYMENT_FILE_PATH));
            System.out.println("BikeRequestsServlet: Reading Payment.txt");
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] paymentData = line.trim().split("\\s*\\|\\s*");
                if (paymentData.length != 9) {
                    System.out.println("BikeRequestsServlet: Skipping malformed line: " + line);
                    continue; // Skip malformed lines
                }
                String orderNumber = paymentData[0].trim();
                String bikeId = paymentData[1].trim();
                String username = paymentData[2].trim();
                String email = paymentData[3].trim();
                String rentalDays = paymentData[4].trim();
                String totalPayment = paymentData[5].trim();
                String additionalServices = paymentData[6].trim();
                String fileName = paymentData[7].trim();
                String additionalNotes = paymentData[8].trim();
                if (bikeId.equals(bikeName)) {
                    RentalRequest rentalRequest = new RentalRequest(orderNumber, bikeId, username, email, rentalDays, totalPayment, additionalServices, fileName, additionalNotes);
                    rentalRequests.add(rentalRequest);
                    System.out.println("BikeRequestsServlet: Found payment request for bike " + bikeName + ": " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("BikeRequestsServlet: Failed to read Payment.txt: " + e.getMessage());
            request.setAttribute("errorMessage", "Failed to fetch payment requests: " + e.getMessage());
            request.getRequestDispatcher("BikeRequests.jsp").forward(request, response);
            return;
        }

        // Debug: Log the number of rental requests found
        System.out.println("BikeRequestsServlet: Found " + rentalRequests.size() + " payment requests for bike " + bikeName);

        // Set the rentalRequests attribute
        request.setAttribute("rentalRequests", rentalRequests);

        // Forward to BikeRequests.jsp
        System.out.println("BikeRequestsServlet: Forwarding to BikeRequests.jsp");
        request.getRequestDispatcher("BikeRequests.jsp").forward(request, response);
    }
}