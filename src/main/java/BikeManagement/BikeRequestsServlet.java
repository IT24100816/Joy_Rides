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
    private static final String QUEUE_ATTRIBUTE = "rentalRequestQueue";

    // Circular Queue implementation for RentalRequest
    public static class CircularQueue {
        private RentalRequest[] queue;
        private int front;
        private int rear;
        private int size;
        private int capacity;

        public CircularQueue(int capacity) {
            this.capacity = capacity;
            this.queue = new RentalRequest[capacity];
            this.front = 0;
            this.rear = -1;
            this.size = 0;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public boolean isFull() {
            return size == capacity;
        }

        public void enqueue(RentalRequest request) {
            if (isFull()) {
                System.out.println("CircularQueue: Queue is full, cannot enqueue request: " + request.getRequestId());
                return;
            }
            rear = (rear + 1) % capacity;
            queue[rear] = request;
            size++;
            System.out.println("CircularQueue: Enqueued request: " + request.getRequestId());
        }

        public RentalRequest dequeue() {
            if (isEmpty()) {
                System.out.println("CircularQueue: Queue is empty, cannot dequeue.");
                return null;
            }
            RentalRequest request = queue[front];
            queue[front] = null;
            front = (front + 1) % capacity;
            size--;
            System.out.println("CircularQueue: Dequeued request: " + request.getRequestId());
            return request;
        }

        public RentalRequest peek() {
            if (isEmpty()) {
                System.out.println("CircularQueue: Queue is empty, cannot peek.");
                return null;
            }
            return queue[front];
        }

        public void populateQueue(List<RentalRequest> requests) {
            for (RentalRequest request : requests) {
                if (!isFull()) {
                    enqueue(request);
                } else {
                    System.out.println("CircularQueue: Queue is full, skipping request: " + request.getRequestId());
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the bikeName parameter
        String bikeName = request.getParameter("bikeName");
        String action = request.getParameter("action"); // For "next" action to get the next request

        // Debug: Log the request
        System.out.println("BikeRequestsServlet: Received request for bikeName: " + bikeName + ", action: " + action);

        // Validate bikeName
        if (bikeName == null || bikeName.trim().isEmpty()) {
            System.out.println("BikeRequestsServlet: Bike name is missing");
            request.setAttribute("errorMessage", "Bike name is missing.");
            request.getRequestDispatcher("BikeRequests.jsp").forward(request, response);
            return;
        }

        // Get or initialize the circular queue from ServletContext
        CircularQueue rentalQueue = (CircularQueue) getServletContext().getAttribute(QUEUE_ATTRIBUTE);
        if (rentalQueue == null) {
            // Fetch all requests to initialize the queue
            List<RentalRequest> rentalRequests = new ArrayList<>();
            try {
                List<String> lines = Files.readAllLines(Paths.get(PAYMENT_FILE_PATH));
                System.out.println("BikeRequestsServlet: Reading Payment.txt to initialize queue");
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
                    // Filter out processed requests and match bikeId
                    if (bikeId.equals(bikeName) && !additionalNotes.startsWith("Processed")) {
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

            // Initialize the queue with a capacity (e.g., 100)
            rentalQueue = new CircularQueue(100);
            rentalQueue.populateQueue(rentalRequests);
            getServletContext().setAttribute(QUEUE_ATTRIBUTE, rentalQueue);
            System.out.println("BikeRequestsServlet: Initialized circular queue with " + rentalRequests.size() + " requests");
        }

        // Handle actions
        RentalRequest currentRequest = null;
        if ("next".equals(action)) {
            // Dequeue the current request and get the next one
            rentalQueue.dequeue();
            currentRequest = rentalQueue.peek();
            System.out.println("BikeRequestsServlet: Action 'next' - Current request: " + (currentRequest != null ? currentRequest.getRequestId() : "None"));
        } else {
            // On initial load, just peek at the first request
            currentRequest = rentalQueue.peek();
            System.out.println("BikeRequestsServlet: Initial load - Current request: " + (currentRequest != null ? currentRequest.getRequestId() : "None"));
        }

        // Set the current request and bikeName attributes
        request.setAttribute("currentRequest", currentRequest);
        request.setAttribute("bikeName", bikeName); // Ensure bikeName is available in JSP

        // Forward to BikeRequests.jsp
        System.out.println("BikeRequestsServlet: Forwarding to BikeRequests.jsp");
        request.getRequestDispatcher("BikeRequests.jsp").forward(request, response);
    }
}
