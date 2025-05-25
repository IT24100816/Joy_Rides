package BikeManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/BikeDetailsServlet")
public class BikeDetailsServlet extends HttpServlet {
    private static final String BIKES_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Bikes.txt";
    private static final Logger LOGGER = Logger.getLogger(BikeDetailsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bikeName = request.getParameter("bikeName");
        LOGGER.info("Processing request in BikeDetailsServlet for bikeName: " + bikeName);

        if (bikeName == null || bikeName.trim().isEmpty()) {
            LOGGER.warning("Bike name is missing in the request.");
            request.setAttribute("errorMessage", "Bike name is required.");
            request.getRequestDispatcher("Bikes.jsp").forward(request, response);
            return;
        }

        String[] bikeData = new String[5];
        try (BufferedReader reader = new BufferedReader(new FileReader(BIKES_FILE_PATH))) {
            LOGGER.info("Reading Bikes.txt from path: " + BIKES_FILE_PATH);
            String line;
            boolean bikeFound = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length >= 6 && parts[0].trim().equalsIgnoreCase(bikeName.trim())) {
                    bikeData[0] = parts[0].trim(); // Bike name
                    bikeData[1] = parts[1].trim(); // Photo path
                    bikeData[2] = parts[2].trim(); // Price
                    bikeData[3] = parts[3].trim(); // Owner username (for ownership check)
                    bikeData[4] = parts[4].trim(); // Availability
                    request.setAttribute("ownerDisplayName", parts[5].trim());
                    bikeFound = true;
                    LOGGER.info("Bike found: " + bikeName + " with data: " + String.join(" | ", parts));
                    break;
                }
            }
            if (!bikeFound) {
                LOGGER.warning("Bike not found in Bikes.txt: " + bikeName);
                request.setAttribute("errorMessage", "Bike not found: " + bikeName);
                request.getRequestDispatcher("Bikes.jsp").forward(request, response);
                return;
            }
        } catch (IOException e) {
            LOGGER.severe("Error reading Bikes.txt: " + e.getMessage());
            request.setAttribute("errorMessage", "Error reading bike data: " + e.getMessage());
            request.getRequestDispatcher("Bikes.jsp").forward(request, response);
            return;
        }

        request.setAttribute("bikeData", bikeData);
        LOGGER.info("Forwarding to BikeDetails.jsp for bike: " + bikeName);
        request.getRequestDispatcher("BikeDetails.jsp").forward(request, response);
    }
}
