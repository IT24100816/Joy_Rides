package BikeManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/BikeListServlet")
public class BikeListServlet extends HttpServlet {
    private static final String BIKES_FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Bikes.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String[]> bikeDataList = new ArrayList<>();

        // Read bike data from file
        File file = new File(BIKES_FILE_PATH);
        if (file.exists()) {
            List<String> lines = Files.readAllLines(Paths.get(BIKES_FILE_PATH));
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                String[] bike = line.trim().split("\\s*\\|\\s*");
                if (bike.length == 6) {
                    // Check lastUsed is number, else set to "0"
                    if (!isNumeric(bike[5])) {
                        bike[5] = "0";
                    }
                    bikeDataList.add(bike);
                }
            }
        } else {
            request.setAttribute("errorMessage", "Bikes.txt file not found.");
        }

        // Sort bikes using Quick Sort
        quickSort(bikeDataList, 0, bikeDataList.size() - 1);

        // Pass data to JSP
        request.setAttribute("bikeDataList", bikeDataList);
        request.getRequestDispatcher("/Bikes.jsp").forward(request, response);
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
    private void quickSort(List<String[]> arr, int low, int high) {
        if (low < high) {
            // Find partition index
            int pi = partition(arr, low, high);

            // Sort left and right
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    // Partition method for Quick Sort
    private int partition(List<String[]> arr, int low, int high) {
        String[] pivot = arr.get(high);
        int i = (low - 1); // Index of smaller element

        for (int j = low; j < high; j++) {
            // If current element is smaller than or
            // equal to pivot
            if (compareBikes(arr.get(j), pivot) < 0) {
                i++;

                // Swap arr[i] and arr[j]
                String[] temp = arr.get(i);
                arr.set(i, arr.get(j));
                arr.set(j, temp);
            }
        }

        // Swap arr[i+1] and arr[high] (or pivot)
        String[] temp = arr.get(i + 1);
        arr.set(i + 1, arr.get(high));
        arr.set(high, temp);

        return i + 1;
    }

    // Compare two bikes:
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