<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.io.IOException" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>JOY-RIDE | Admin Update Rental</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    body { background-color: #f1f1f1; }
    .container { max-width: 500px; margin-top: 50px; }
    .card {
      border-radius: 12px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
      background-color: #ffffff;
    }
    .card-header {
      background-color: #1e3a8a; /* Blue theme to match admin page */
      color: white;
      border-top-left-radius: 12px;
      border-top-right-radius: 12px;
      border-bottom: none;
      padding: 1.25rem;
      font-size: 1.4rem;
      font-weight: 600;
    }
    .card-body { padding: 1.5rem 2rem; }
    .card-footer {
      border-top: none;
      padding: 1rem 2rem;
      background-color: #e0e7ff; /* Light blue footer */
      border-bottom-left-radius: 12px;
      border-bottom-right-radius: 12px;
    }
    .card-footer .btn-secondary {
      background-color: #6b7280;
      border: none;
      border-radius: 20px;
      padding: 0.5rem 1.5rem;
      font-weight: 500;
      transition: background-color 0.3s ease;
    }
    .card-footer .btn-secondary:hover { background-color: #4b5563; }
    .card-footer .btn-update {
      background-color: #3b82f6; /* Blue button */
      color: white;
      border: none;
      border-radius: 20px;
      padding: 0.5rem 1.5rem;
      font-weight: 500;
      transition: background-color 0.3s ease, transform 0.3s ease;
    }
    .card-footer .btn-update:hover {
      background-color: #2563eb;
      transform: scale(1.03);
    }
    .form-group { margin-bottom: 1.25rem; }
    .card-body label {
      font-size: 1rem;
      font-weight: 500;
      color: #1e293b; /* Dark blue text */
      margin-bottom: 0.5rem;
      display: block;
    }
    .card-body input[type="number"], .card-body input[type="text"] {
      width: 100%;
      padding: 0.65rem;
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      font-size: 1rem;
      background-color: #f5f5f5;
      transition: border-color 0.3s ease, box-shadow 0.3s ease;
    }
    .card-body input[readonly] {
      background-color: #e0e7ff;
      cursor: not-allowed;
    }
    .card-body input[type="number"]:focus {
      border-color: #3b82f6;
      box-shadow: 0 0 0 0.2rem rgba(59, 130, 246, 0.25);
      outline: none;
    }
    .card-body .additional-services { margin-bottom: 1.25rem; }
    .card-body .additional-services label {
      font-weight: 500;
      color: #1e293b;
    }
    .card-body .additional-services .checkbox-group {
      display: flex;
      align-items: center;
      gap: 1.5rem;
      margin-top: 0.5rem;
    }
    .card-body .additional-services .checkbox-group label {
      font-weight: 400;
      color: #475569;
      margin-bottom: 0;
      display: inline-block;
      margin-left: 0.25rem;
      cursor: pointer;
    }
    .card-body .additional-services input[type="checkbox"] {
      width: 1.1rem;
      height: 1.1rem;
      cursor: pointer;
    }
  </style>
</head>
<body>
<div class="container">
  <div class="card">
    <%
      String bikeName = request.getParameter("bikeName");
      String username = request.getParameter("username");
      if (bikeName == null || username == null) {
    %>
    <div class="alert alert-danger" role="alert">
      Error: Bike name or username is missing. Please go back and try again.
    </div>
    <%
        return;
      }
      bikeName = URLDecoder.decode(bikeName, "UTF-8");
    %>
    <div class="card-header">
      Update Rental: <%= bikeName %> for <%= username %>
    </div>
    <%
      String rentalRequestFilePath = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/RentalRequests.txt";
      String orderNumber = "N/A";
      String currentRentalDays = "1";
      String currentServices = "";
      boolean rentalFound = false;

      try {
        List<String> rentalLines = Files.readAllLines(Paths.get(rentalRequestFilePath));
        for (String line : rentalLines) {
          if (line.trim().isEmpty()) continue;
          String[] parts = line.split("\\s*\\|\\s*");
          if (parts.length == 5) {
            String[] bikePart = parts[0].split("\\s*:\\s*");
            String[] usernamePart = parts[1].split("\\s*:\\s*");
            String[] orderPart = parts[2].split("\\s*:\\s*");
            String[] daysPart = parts[3].split("\\s*:\\s*");
            String[] servicesPart = parts[4].split("\\s*:\\s*");

            if (bikePart.length == 2 && usernamePart.length == 2 && orderPart.length == 2 && daysPart.length == 2 && servicesPart.length == 2) {
              String currentBikeName = bikePart[1].trim();
              String currentUsername = usernamePart[1].trim();
              if (currentBikeName.equals(bikeName) && currentUsername.equals(username)) {
                orderNumber = orderPart[1].trim();
                currentRentalDays = daysPart[1].trim();
                currentServices = servicesPart[1].trim();
                rentalFound = true;
                break;
              }
            }
          }
        }

        if (!rentalFound) {
    %>
    <div class="alert alert-danger" role="alert">
      No matching rental request found for bike <%= bikeName %> and username <%= username %>.
    </div>
    <%
        return;
      }
    } catch (IOException e) {
    %>
    <div class="alert alert-danger" role="alert">
      Error reading rental data: <%= e.getMessage() %>
    </div>
    <%
        return;
      }
    %>
    <form action="UpdateRentalServlet" method="POST">
      <div class="card-body">
        <input type="hidden" name="bikeName" value="<%= bikeName %>">
        <input type="hidden" name="username" value="<%= username %>">
        <input type="hidden" name="orderNumber" value="<%= orderNumber %>">
        <input type="hidden" name="source" value="admin"> <!-- Indicate admin update -->
        <div class="form-group">
          <label for="username">Username</label>
          <input type="text" id="username" name="username" value="<%= username %>" readonly>
        </div>
        <div class="form-group">
          <label for="orderNumber">Order Number</label>
          <input type="text" id="orderNumber" name="orderNumber" value="<%= orderNumber %>" readonly>
        </div>
        <div class="form-group">
          <label for="rentalDays">Rental Days</label>
          <input type="number" id="rentalDays" name="rentalDays" min="1" value="<%= currentRentalDays %>" required>
        </div>
        <div class="form-group additional-services">
          <label>Additional Services ($25 each)</label>
          <div class="checkbox-group">
            <div>
              <input type="checkbox" id="helmet" name="additionalServices" value="Helmet" <%= currentServices.contains("Helmet") ? "checked" : "" %>>
              <label for="helmet">Helmet</label>
            </div>
            <div>
              <input type="checkbox" id="gloves" name="additionalServices" value="Gloves" <%= currentServices.contains("Gloves") ? "checked" : "" %>>
              <label for="gloves">Gloves</label>
            </div>
            <div>
              <input type="checkbox" id="rainJacket" name="additionalServices" value="RainJacket" <%= currentServices.contains("RainJacket") ? "checked" : "" %>>
              <label for="rainJacket">Rain Jacket</label>
            </div>
          </div>
        </div>
      </div>
      <div class="card-footer">
        <a href="RentDetails.jsp" class="btn btn-secondary">Cancel</a>
        <button type="submit" class="btn btn-update">Submit Update</button>
      </div>
    </form>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>