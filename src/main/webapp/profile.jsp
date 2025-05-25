<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>JOY-RIDE | Profile</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    .bike-photo { width: 50px; height: 50px; object-fit: cover; border-radius: 4px; }
    .btn-extend { background-color: #28a745; color: white; padding: 0.3rem 0.8rem; font-size: 0.9rem; font-weight: 600; border-radius: 20px; transition: transform 0.3s ease, background-color 0.3s ease; }
    .btn-extend:hover { background-color: #218838; transform: scale(1.05); }
    .btn-return { background-color: #dc3545; color: white; padding: 0.3rem 0.8rem; font-size: 0.9rem; font-weight: 600; border-radius: 20px; transition: transform 0.3s ease, background-color 0.3s ease; }
    .btn-return:hover { background-color: #c82333; transform: scale(1.05); }
    .btn-share { background-color: #17a2b8; color: white; padding: 0.3rem 0.8rem; font-size: 0.9rem; font-weight: 600; border-radius: 20px; transition: transform 0.3s ease, background-color 0.3s ease; }
    .btn-share:hover { background-color: #138496; transform: scale(1.05); }
    th, td { padding: 0.5rem; }
    th:nth-child(1), td:nth-child(1) { width: 10%; }
    th:nth-child(2), td:nth-child(2) { width: 20%; }
    th:nth-child(3), td:nth-child(3) { width: 40%; }
    th:nth-child(4), td:nth-child(4) { width: 30%; }
    .error-message { background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 15px; }
    .success-message { background-color: #d4edda; color: #155724; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 15px; }
  </style>
</head>
<body class="bg-gray-100">
<div class="container mx-auto px-6 py-16">
  <h1 class="text-4xl font-bold text-center mb-8">User Profile</h1>
  <div class="bg-white shadow-lg rounded-lg p-6 max-w-md mx-auto">
    <h2 class="text-2xl font-semibold mb-4">Profile Details</h2>
    <p class="text-lg mb-2"><strong>Username:</strong> <%= session.getAttribute("username") %></p>
    <p class="text-lg mb-2"><strong>Email:</strong> <%= session.getAttribute("email") %></p>
    <p class="text-lg mb-2"><strong>City:</strong> <%= session.getAttribute("city") %></p>
    <p class="text-lg mb-6"><strong>NIC Number:</strong> <%= session.getAttribute("nic") %></p>
    <div class="flex justify-between">
      <a href="EditProfile.jsp" class="bg-[#f76b64] text-white px-4 py-2 rounded-lg font-bold hover:bg-[#fa8a76] transition">Edit Profile</a>
      <a href="DeleteAccountServlet" class="bg-red-600 text-white px-4 py-2 rounded-lg font-bold hover:bg-red-700 transition" onclick="return confirm('Are you sure you want to delete your account? This action cannot be undone.');">Delete Account</a>
    </div>
    <a href="index.jsp" class="block text-center mt-4 text-[#f76b64] hover:underline">Back to Home</a>
  </div>

  <!-- My Bikes Section -->
  <%
    String username = (String) session.getAttribute("username");
    if (username != null) {
      String bikesFilePath = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/Bikes.txt";
      String paymentFilePath = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/confirmed_payment.txt";
      List<String> paymentLines = null;
      try {
        paymentLines = Files.readAllLines(Paths.get(paymentFilePath));
      } catch (Exception e) {
        request.setAttribute("errorMessage", "Error reading confirmed_payment.txt: " + e.getMessage());
      }
      boolean hasRentedBikes = false;
  %>
  <!-- Display Error/Success Messages -->
  <%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
    if (errorMessage != null) {
  %>
  <div class="error-message mt-4 max-w-4xl mx-auto">
    <%= errorMessage %>
  </div>
  <%
    }
    if (successMessage != null) {
  %>
  <div class="success-message mt-4 max-w-4xl mx-auto">
    <%= successMessage %>
  </div>
  <%
    }
  %>
  <div class="bg-white shadow-lg rounded-lg p-6 max-w-4xl mx-auto mt-8">
    <h2 class="text-2xl font-bold mb-4">My Bikes</h2>
    <table class="w-full text-left">
      <thead>
      <tr>
        <th>Photo</th>
        <th>Bike Name</th>
        <th>Remaining Time</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <%
        if (paymentLines != null) {
          for (String paymentLine : paymentLines) {
            if (paymentLine.trim().isEmpty()) continue;
            String[] paymentData = paymentLine.split("\\s*\\|\\s*");
            if (paymentData.length != 9) {
              System.out.println("Skipping malformed line in confirmed_payment.txt: " + paymentLine);
              continue;
            }
            String paymentUsername = paymentData[2].trim();
            String bikeName = paymentData[1].trim();
            String rentalDays = paymentData[4].trim();
            String totalPayment = paymentData[5].trim();
            String additionalServicesStr = paymentData[6].trim();
            String additionalNotes = paymentData[8].trim();

            if (paymentUsername.equals(username) && additionalNotes.startsWith("Processed")) {
              hasRentedBikes = true;
              String[] notesParts = additionalNotes.split(",");
              if (notesParts.length != 2 || !notesParts[1].matches("\\d+")) {
                System.out.println("Invalid additionalNotes format for bike " + bikeName + ": " + additionalNotes);
                continue;
              }
              Long rentalEndTime = Long.parseLong(notesParts[1]);

              String photoPath = "default.jpg";
              double pricePerHour = 0.0;
              try {
                List<String> bikeLines = Files.readAllLines(Paths.get(bikesFilePath));
                for (String bikeLine : bikeLines) {
                  if (bikeLine.trim().isEmpty()) continue;
                  String[] bikeData = bikeLine.split("\\s*\\|\\s*");
                  if (bikeData.length >= 3 && bikeData[0].trim().equals(bikeName)) {
                    photoPath = bikeData[1].trim();
                    pricePerHour = Double.parseDouble(bikeData[2].trim());
                    break;
                  }
                }
              } catch (Exception e) {
                request.setAttribute("errorMessage", "Error reading Bikes.txt: " + e.getMessage());
                continue;
              }

              String[] additionalServices = additionalServicesStr.equals("None") ? new String[0] : additionalServicesStr.split(",\\s*");
              String encodedBikeName = URLEncoder.encode(bikeName, "UTF-8");
      %>
      <tr>
        <td><img src="image/<%= photoPath %>" alt="<%= bikeName %>" class="bike-photo"></td>
        <td><%= bikeName %></td>
        <td>
          <span id="countdown-<%= encodedBikeName %>">Time Left: Calculating...</span>
          <script>
            function startCountdown(rentalEndTime, elementId, bikeName) {
              const countdownElement = document.getElementById(elementId);
              if (!countdownElement) {
                console.error('Countdown element not found for bike:', bikeName);
                return;
              }
              if (!rentalEndTime || isNaN(rentalEndTime) || rentalEndTime <= Date.now()) {
                console.error('Invalid rental end time:', rentalEndTime);
                countdownElement.textContent = "Rental period has ended.";
                return;
              }

              function updateCountdown() {
                const now = Date.now();
                const timeRemaining = Math.max(0, rentalEndTime - now);

                if (timeRemaining <= 0) {
                  countdownElement.textContent = "Rental period has ended.";
                  return false;
                }

                const days = Math.floor(timeRemaining / (1000 * 60 * 60 * 24));
                const hours = Math.floor((timeRemaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                const minutes = Math.floor((timeRemaining % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((timeRemaining % (1000 * 60)) / 1000);

                const dayText = days === 1 ? "day" : "days";
                countdownElement.textContent = `Time Left: ${days} ${dayText} ${hours} hours ${minutes} minutes ${seconds} seconds`;
                return true;
              }

              if (!updateCountdown()) return;

              const intervalId = setInterval(updateCountdown, 1000);
              window.addEventListener('unload', () => clearInterval(intervalId));
            }

            startCountdown(<%= rentalEndTime %>, 'countdown-<%= encodedBikeName %>', '<%= bikeName %>');
          </script>
        </td>
        <td class="flex space-x-2">
          <a href="UpdateRentDetails.jsp?bikeName=<%= encodedBikeName %>&pricePerHour=<%= pricePerHour %>&totalPayment=<%= totalPayment %>&rentalDays=<%= rentalDays %><%= additionalServices.length > 0 && java.util.Arrays.asList(additionalServices).contains("Helmet") ? "&helmet=true" : "" %><%= additionalServices.length > 0 && java.util.Arrays.asList(additionalServices).contains("Gloves") ? "&gloves=true" : "" %><%= additionalServices.length > 0 && java.util.Arrays.asList(additionalServices).contains("RainJacket") ? "&rainJacket=true" : "" %>" class="btn-extend">Extend</a>
          <form action="ReturnBikeServlet" method="POST" onsubmit="return confirm('Are you sure you want to return this bike?');">
            <input type="hidden" name="bikeName" value="<%= encodedBikeName %>">
            <button type="submit" class="btn-return">Return</button>
          </form>
          <button class="btn-share" onclick="alert('Share functionality not implemented yet.');">Share</button>
        </td>
      </tr>
      <%
            }
          }
        }
        if (!hasRentedBikes) {
      %>
      <tr>
        <td colspan="4" class="text-center text-gray-700">No bikes rented.</td>
      </tr>
      <%
        }
      %>
      </tbody>
    </table>
  </div>
  <%
    }
  %>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>