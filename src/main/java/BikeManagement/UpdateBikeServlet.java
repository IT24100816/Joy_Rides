package BikeManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/UpdateBikeServlet")
@MultipartConfig
public class UpdateBikeServlet extends HttpServlet {
    private static final String BIKES_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Bikes.txt";
    private static final Logger LOGGER = Logger.getLogger(UpdateBikeServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get form parameters
        String originalBikeName = URLDecoder.decode(request.getParameter("originalBikeName"), StandardCharsets.UTF_8.toString());
        String bikeName = request.getParameter("bikeName");
        String bikePrice = request.getParameter("bikePrice");
        String ownerName = request.getParameter("ownerName");
        String availability = request.getParameter("availability");
        String currentPhotoPath = request.getParameter("currentPhotoPath");

        LOGGER.info("Updating bike: " + originalBikeName + " to new name: " + bikeName + ", availability: " + availability);

        // Handle file upload (bike photo)
        Part filePart = request.getPart("bikePhoto");
        String fileName = filePart != null && filePart.getSize() > 0 ? System.currentTimeMillis() + "_" + filePart.getSubmittedFileName() : currentPhotoPath;
        if (filePart != null && filePart.getSize() > 0) {
            String uploadPath = getServletContext().getRealPath("") + File.separator + "image";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();
            String filePath = uploadPath + File.separator + fileName;
            filePart.write(filePath);
            LOGGER.info("Uploaded new photo: " + fileName);
        }

        List<String> updatedLines = new ArrayList<>();

        // Read the current contents of Bikes.txt and update the matching bike
        try {
            File file = new File(BIKES_FILE_PATH);
            if (file.exists()) {
                List<String> lines = Files.readAllLines(Paths.get(BIKES_FILE_PATH));
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue; // Skip empty lines
                    String[] parts = line.trim().split("\\s*\\|\\s*");
                    if (parts.length == 6 && parts[0].equals(originalBikeName)) {
                        String lastUsed = String.valueOf(System.currentTimeMillis()); // Update lastUsed
                        String updatedLine = String.format("%s | %s | %s | %s | %s | %s", bikeName, fileName, bikePrice, ownerName, availability, lastUsed);
                        updatedLines.add(updatedLine);
                        LOGGER.info("Updated line: " + updatedLine);
                    } else {
                        updatedLines.add(line);
                    }
                }
            } else {
                LOGGER.severe("Bikes.txt file does not exist at: " + BIKES_FILE_PATH);
                throw new IOException("Bikes.txt file not found");
            }

            // Write the updated contents back to Bikes.txt
            Files.write(Paths.get(BIKES_FILE_PATH), updatedLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
            LOGGER.info("Successfully updated " + BIKES_FILE_PATH);
        } catch (IOException e) {
            LOGGER.severe("Failed to update " + BIKES_FILE_PATH + ": " + e.getMessage());
            request.setAttribute("errorMessage", "Failed to update bike details: " + e.getMessage());
            request.getRequestDispatcher("Bikes.jsp").forward(request, response);
            return;
        }

        // Redirect to BikeDetailsServlet with the updated bike name and owner check
        String encodedBikeName = URLEncoder.encode(bikeName, StandardCharsets.UTF_8.toString());
        String encodedOwnerName = URLEncoder.encode(ownerName, StandardCharsets.UTF_8.toString());
        LOGGER.info("Redirecting to BikeDetailsServlet with bikeName=" + encodedBikeName + "&ownerCheck=" + encodedOwnerName);
        response.sendRedirect("BikeDetailsServlet?bikeName=" + encodedBikeName + "&ownerCheck=" + encodedOwnerName);
    }
}