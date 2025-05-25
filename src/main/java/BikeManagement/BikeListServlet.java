package BikeManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;

@WebServlet("/BikeListServlet")
public class BikeListServlet extends HttpServlet {
    private static final String BIKES_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Bikes.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // First pass: count valid lines to size the array
        int lineCount = countValidLines();
        String[][] bikeDataArray = new String[lineCount][];
        int index = 0;

        // Second pass: read and populate the array
        File file = new File(BIKES_FILE_PATH);
        if (file.exists()) {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(BIKES_FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] bike = line.trim().split("\\s*\\|\\s*");
                    if (bike.length == 6) {
                        // Check lastUsed is numeric, else set to "0"
                        if (!isNumeric(bike[5])) {
                            bike[5] = "0";
                        }
                        bikeDataArray[index++] = bike;
                    }
                }
            }
        } else {
            request.setAttribute("errorMessage", "Bikes.txt file not found.");
        }

        // Sort bikes using Quick Sort
        quickSort(bikeDataArray, 0, index - 1);

        // Pass data to JSP
        request.setAttribute("bikeDataList", bikeDataArray);
        request.getRequestDispatcher("/Bikes.jsp").forward(request, response);
    }

    // Count valid lines in the file
    private int countValidLines() throws IOException {
        int count = 0;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(BIKES_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.trim().split("\\s*\\|\\s*");
                if (parts.length == 6) {
                    count++;
                }
            }
        }
        return count;
    }

    // Check if string is numeric
    private boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Quick Sort
    private void quickSort(String[][] arr, int low, int high) {
        if (low < high) {
            // Find partition index
            int pi = partition(arr, low, high);

            // Sort left and right
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    // Partition method for Quick Sort
    private int partition(String[][] arr, int low, int high) {
        String[] pivot = arr[high];
        int i = (low - 1); // Index of smaller element

        for (int j = low; j < high; j++) {
            // If current element is smaller than or equal to pivot
            if (compareBikes(arr[j], pivot) < 0) {
                i++;

                // Swap arr[i] and arr[j]
                String[] temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Swap arr[i+1] and arr[high] (or pivot)
        String[] temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    // Compare two bikes
    private int compareBikes(String[] bike1, String[] bike2) {
        int avail1 = bike1[4].equalsIgnoreCase("Available") ? 0 : 1;
        int avail2 = bike2[4].equalsIgnoreCase("Available") ? 0 : 1;

        if (avail1 != avail2) {
            return avail1 - avail2; // Available first
        } else {
            long lastUsed1 = Long.parseLong(bike1[5]);
            long lastUsed2 = Long.parseLong(bike2[5]);
            return Long.compare(lastUsed1, lastUsed2); // Smaller lastUsed first
        }
    }
}
