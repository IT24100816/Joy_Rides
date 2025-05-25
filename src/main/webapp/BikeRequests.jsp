<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="BikeManagement.RentalRequest" %>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>JOY-RIDE - Bike Rental Requests</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
  <style>
    body { background-color: #f0f0f0; }
    .btn-primary { background-color: #f76b64; border: none; padding: 0.5rem 1.5rem; font-size: 1rem; font-weight: 700; border-radius: 50px; transition: transform 0.3s ease, background-color 0.3s ease; }
    .btn-primary:hover { background-color: #fa8a76; transform: scale(1.05); }
    .btn-success { background-color: #28a745; border: none; padding: 0.5rem 1.5rem; font-size: 1rem; font-weight: 700; border-radius: 50px; transition: transform 0.3s ease, background-color 0.3s ease; }
    .btn-success:hover { background-color: #218838; transform: scale(1.05); }
    .btn-danger { background-color: #dc3545; border: none; padding: 0.5rem 1.5rem; font-size: 1rem; font-weight: 700; border-radius: 50px; transition: transform 0.3s ease, background-color 0.3s ease; }
    .btn-danger:hover { background-color: #c82333; transform: scale(1.05); }
    .btn-secondary { background-color: #6c757d; border: none; padding: 0.5rem 1.5rem; font-size: 1rem; font-weight: 700; border-radius: 50px; transition: transform 0.3s ease, background-color 0.3s ease; }
    .btn-secondary:hover { background-color: #5a6268; transform: scale(1.05); }
    .request-card { background-color: #f9f9f9; border: 1px solid #ddd; border-radius: 8px; padding: 15px; margin-top: 10px; }
    .request-card p { margin: 5px 0; }
    .alert-success { background-color: #d4edda; color: #155724; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 15px; }
    .alert-danger { background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 15px; }
    .dropdown-menu { min-width: 150px; }
    nav .text-4xl { font-size: 2.75rem; }
    nav ul li a { font-size: 1.125rem; }
    nav .bg-white.text-\#f76b64 { padding: 0.55rem 1.2rem; font-size: 1.125rem; color: #f76b64; }
    #mobile-menu a, #mobile-menu button { font-size: 1.125rem; }
  </style>
</head>
<body>
<%
  String bikeName = (String) request.getAttribute("bikeName"); // Use the attribute set by BikeRequestsServlet
  if (bikeName == null) {
    bikeName = request.getParameter("bikeName"); // Fallback to parameter if attribute is not set
  }
  String errorMessage = (String) request.getAttribute("errorMessage");
  String successMessage = (String) session.getAttribute("successMessage");
%>
<!-- Navigation Bar -->
<nav class="bg-[#f76b64] shadow-lg">
  <div class="container mx-auto px-6 py-4 flex justify-between items-center">
    <a href="index.jsp" class="text-4xl font-extrabold text-black">JOY-RIDE</a>
    <div class="hidden lg:flex items-center w-full max-w-lg">
      <label for="searchInput" class="sr-only">Search bikes or rides</label>
      <input type="text" id="searchInput" placeholder="Search bikes or rides..." class="w-full px-4 py-2 rounded-l-lg focus:outline-none"/>
      <button class="bg-white text-[#f76b64] px-4 py-2 rounded-r-lg hover:bg-gray-200 transition">Search</button>
    </div>
    <ul class="hidden lg:flex space-x-6">
      <li><a href="index.jsp" class="text-white hover:underline">Home</a></li>
      <li><a href="#" class="text-white hover:underline">About</a></li>
      <li><a href="#" class="text-white hover:underline">Services</a></li>
      <li><a href="BikeListServlet" class="text-white hover:underline">Bikes</a></li>
      <li><a href="#" class="text-white hover:underline">Contact</a></li>
    </ul>
    <div class="hidden lg:flex items-center gap-4">
      <%
        String username = (String) session.getAttribute("username");
        if (username != null) {
      %>
      <div class="dropdown">
        <button class="bg-transparent text-white px-2 py-2 rounded-lg hover:bg-gray-200 transition" type="button" id="profileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
          <i class="fas fa-user-circle fa-2x"></i>
        </button>
        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="profileDropdown">
          <li><a class="dropdown-item" href="profile.jsp">Profile</a></li>
          <li><a class="dropdown-item" href="SearchMembers.jsp">Search Members</a></li>
          <li><a class="dropdown-item" href="ChangePassword.jsp">Change Password</a></li>
          <li><a class="dropdown-item" href="LogoutServlet">Logout</a></li>
        </ul>
      </div>
      <% } else { %>
      <a href="Login.jsp" class="bg-white text-[#f76b64] px-4 py-2 rounded-lg hover:bg-gray-200 transition">Login/Sign Up</a>
      <% } %>
    </div>
    <button id="menu-toggle" class="lg:hidden text-white focus:outline-none">
      <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16m-7 6h7"></path>
      </svg>
    </button>
  </div>
  <div id="mobile-menu" class="lg:hidden hidden bg-[#f76b64] p-4 space-y-4">
    <label for="mobileSearchInput" class="sr-only">Search</label>
    <input type="text" id="mobileSearchInput" placeholder="Search..." class="w-full px-4 py-2 rounded-lg focus:outline-none"/>
    <a href="index.jsp" class="block text-white hover:underline">Home</a>
    <a href="#" class="block text-white hover:underline">About</a>
    <a href="#" class="block text-white hover:underline">Services</a>
    <a href="BikeListServlet" class="block text-white hover:underline">Bikes</a>
    <a href="#" class="block text-white hover:underline">Contact</a>
    <% if (username != null) { %>
    <div class="dropdown">
      <button class="bg-transparent text-white px-2 py-2 rounded-lg hover:bg-gray-200 transition block w-full text-left" type="button" id="mobileProfileDropdown" data-bs-toggle="dropdown" aria-expanded="false">
        <i class="fas fa-user-circle fa-lg mr-2"></i> Profile
      </button>
      <ul class="dropdown-menu" aria-labelledby="mobileProfileDropdown">
        <li><a class="dropdown-item" href="profile.jsp">Profile</a></li>
        <li><a class="dropdown-item" href="SearchMembers.jsp">Search Members</a></li>
        <li><a class="dropdown-item" href="ChangePassword.jsp">Change Password</a></li>
        <li><a class="dropdown-item" href="LogoutServlet">Logout</a></li>
      </ul>
    </div>
    <% } else { %>
    <a href="Login.jsp" class="block bg-white text-[#f76b64] text-center px-4 py-2 rounded-lg hover:bg-gray-200 transition">Login/Sign Up</a>
    <% } %>
  </div>
</nav>

<!-- Bike Rental Requests Section -->
<section class="container mx-auto px-6 py-16">
  <%
    if (errorMessage != null) {
  %>
  <div class="alert alert-danger" role="alert">
    <%= errorMessage %>
  </div>
  <%
    }
    if (successMessage != null) {
  %>
  <div class="alert alert-success" id="success-alert" role="alert">
    <%= successMessage %>
  </div>
  <%
      session.removeAttribute("successMessage"); // Clear the success message after displaying
    }
  %>

  <%
    if (bikeName == null || bikeName.isEmpty()) {
  %>
  <div class="max-w-md mx-auto">
    <p class="text-center text-gray-700">No bike specified.</p>
    <a href="BikeListServlet" class="btn-primary text-white mt-4 inline-block">Back to Bikes</a>
  </div>
  <%
  } else {
    RentalRequest currentRequest = (RentalRequest) request.getAttribute("currentRequest");
  %>
  <div class="max-w-md mx-auto">
    <h2 class="text-2xl font-bold mb-3">Payment Requests for <%= bikeName %></h2>
    <a href="BikeDetailsServlet?bikeName=<%= URLEncoder.encode(bikeName, "UTF-8") %>" class="btn-primary text-white mb-4 inline-block">Back to Bike Details</a>
    <%
      if (currentRequest != null) {
    %>
    <div class="mt-4">
      <h3 class="text-xl font-semibold mb-3">Current Payment Request</h3>
      <div class="request-card" id="request-card-<%= currentRequest.getRequestId() %>">
        <p><strong>Order Number:</strong> <%= currentRequest.getRequestId() %></p>
        <p><strong>Username:</strong> <%= currentRequest.getRenterUsername() %></p>
        <p><strong>Email:</strong> <%= currentRequest.getEmail() %></p>
        <p><strong>Additional Services:</strong> <%= currentRequest.getAdditionalServices().isEmpty() ? "None" : currentRequest.getAdditionalServices() %></p>
        <p><strong>Rental Days:</strong> <%= currentRequest.getRentalDays() %></p>
        <p><strong>Total Payment:</strong> <%= currentRequest.getTotalPayment() %></p>
        <p><strong>File Name:</strong> <%= currentRequest.getFileName() %></p>
        <p><strong>Additional Notes:</strong> <%= currentRequest.getAdditionalNotes() %></p>
        <div class="flex space-x-3 mt-2">
          <form action="HandleRentalRequestServlet" method="POST" onsubmit="return validateAcceptForm(this)">
            <input type="hidden" name="requestId" value="<%= URLEncoder.encode(String.valueOf(currentRequest.getRequestId()), "UTF-8") %>">
            <input type="hidden" name="action" value="accept">
            <input type="hidden" name="bikeName" value="<%= URLEncoder.encode(bikeName, "UTF-8") %>">
            <input type="hidden" name="rentalDays" value="<%= currentRequest.getRentalDays() %>">
            <button type="submit" class="btn-success text-white">Accept</button>
          </form>
          <form action="HandleRentalRequestServlet" method="POST">
            <input type="hidden" name="requestId" value="<%= URLEncoder.encode(String.valueOf(currentRequest.getRequestId()), "UTF-8") %>">
            <input type="hidden" name="action" value="reject">
            <input type="hidden" name="bikeName" value="<%= URLEncoder.encode(bikeName, "UTF-8") %>">
            <button type="submit" class="btn-danger text-white">Reject</button>
          </form>
          <form action="BikeRequestsServlet" method="GET">
            <input type="hidden" name="bikeName" value="<%= URLEncoder.encode(bikeName, "UTF-8") %>">
            <input type="hidden" name="action" value="next">
            <button type="submit" class="btn-secondary text-white">Next Request</button>
          </form>
        </div>
      </div>
    </div>
    <%
    } else {
    %>
    <p class="text-gray-700 mt-4">No more payment requests for this bike.</p>
    <%
      }
    %>
  </div>
  <%
    }
  %>
</section>

<!-- JavaScript for Mobile Menu Toggle, Form Validation, and Success Message Handling -->
<script>
  const menuToggle = document.getElementById('menu-toggle');
  const mobileMenu = document.getElementById('mobile-menu');
  menuToggle.addEventListener('click', () => mobileMenu.classList.toggle('hidden'));

  function validateAcceptForm(form) {
    const rentalDaysInput = form.querySelector('input[name="rentalDays"]');
    const rentalDays = parseInt(rentalDaysInput.value, 10);
    if (isNaN(rentalDays) || rentalDays <= 0) {
      alert('Rental days must be a positive number.');
      return false;
    }
    return true;
  }

  // Handle success message and redirect
  window.onload = function() {
    const successAlert = document.getElementById('success-alert');
    if (successAlert) {
      setTimeout(() => {
        <% if (bikeName != null) { %>
        window.location.href = 'BikeDetailsServlet?bikeName=<%= URLEncoder.encode(bikeName, "UTF-8") %>';
        <% } else { %>
        window.location.href = 'BikeListServlet'; // Fallback redirect if bikeName is null
        <% } %>
      }, 2000); // Redirect after 2 seconds
    }
  };
</script>
</body>
</html>