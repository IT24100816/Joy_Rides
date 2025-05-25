package UserManagement.AdminManagement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminDeleteBikeServlet extends HttpServlet {
    private static final String BIKES_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Bikes.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bikeName = request.getParameter("bikeName");
        if (bikeName == null || bikeName.trim().isEmpty()) {
            response.sendRedirect("AdminBikeDetails.jsp?error=" + java.net.URLEncoder.encode("Bike name is missing", StandardCharsets.UTF_8));
            return;
        }

        bikeName = java.net.URLDecoder.decode(bikeName, StandardCharsets.UTF_8);
        List<String> updatedLines = new ArrayList<>();
        File inputFile = new File(BIKES_FILE_PATH);
        File tempFile = new File(inputFile.getAbsolutePath() + ".tmp");

        if (!inputFile.exists()) {
            response.sendRedirect("AdminBikeDetails.jsp?error=" + java.net.URLEncoder.encode("Bikes.txt file does not exist at: " + BIKES_FILE_PATH, StandardCharsets.UTF_8));
            return;
        }
        if (!inputFile.canWrite()) {
            response.sendRedirect("AdminBikeDetails.jsp?error=" + java.net.URLEncoder.encode("Cannot write to Bikes.txt file. Check permissions.", StandardCharsets.UTF_8));
            return;
        }

        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|");
                if (parts.length > 0 && parts[0].trim().equals(bikeName)) {
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine + System.lineSeparator());
            }
        } catch (IOException e) {
            response.sendRedirect("AdminBikeDetails.jsp?error=" + java.net.URLEncoder.encode("Error reading/writing file: " + e.getMessage(), StandardCharsets.UTF_8));
            return;
        }

        if (!found) {
            response.sendRedirect("AdminBikeDetails.jsp?error=" + java.net.URLEncoder.encode("Bike '" + bikeName + "' not found in Bikes.txt", StandardCharsets.UTF_8));
            return;
        }

        try {
            if (!inputFile.delete()) {
                response.sendRedirect("AdminBikeDetails.jsp?error=" + java.net.URLEncoder.encode("Failed to delete original Bikes.txt file", StandardCharsets.UTF_8));
                return;
            }
            if (!tempFile.renameTo(inputFile)) {
                response.sendRedirect("AdminBikeDetails.jsp?error=" + java.net.URLEncoder.encode("Failed to rename temp file to Bikes.txt", StandardCharsets.UTF_8));
                return;
            }
        } catch (Exception e) {
            response.sendRedirect("AdminBikeDetails.jsp?error=" + java.net.URLEncoder.encode("Error during file replacement: " + e.getMessage(), StandardCharsets.UTF_8));
            return;
        }

        response.sendRedirect("AdminBikeDetails.jsp?success=" + java.net.URLEncoder.encode("Bike '" + bikeName + "' deleted successfully", StandardCharsets.UTF_8));
    }
}
