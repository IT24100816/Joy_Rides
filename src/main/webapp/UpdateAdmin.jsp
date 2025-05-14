<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>JOY-RIDE - Update Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0==" crossorigin="anonymous">
  <style>
    body { background-color: #f0f0f0; font-family: 'Arial', sans-serif; }
    .sidebar {
      background: linear-gradient(180deg, #1E3A8A, #0F1D4A);
      width: 250px;
      height: 100vh;
      position: fixed;
      top: 0;
      left: 0;
      padding-top: 20px;
      box-shadow: 2px 0 10px rgba(0, 0, 0, 0.2);
      transition: width 0.3s ease;
    }
    .sidebar:hover { width: 260px; }
    .sidebar .logo {
      text-align: center;
      margin-bottom: 2rem;
      transition: transform 0.3s ease;
    }
    .sidebar .logo:hover { transform: scale(1.05); }
    .sidebar a {
      color: white;
      padding: 15px 20px;
      display: flex;
      align-items: center;
      font-size: 1.1rem;
      transition: all 0.3s ease;
      border-left: 4px solid transparent;
      text-decoration: none;
    }
    .sidebar a:hover {
      background-color: #1a306f;
      border-left: 4px solid #3B82F6;
      padding-left: 25px;
    }
    .sidebar a i { margin-right: 10px; }
    .content { margin-left: 250px; padding: 20px; }
    .welcome-message {
      background: linear-gradient(90deg, #1E3A8A, #3B82F6);
      color: white;
      padding: 2rem;
      border-radius: 15px;
      text-align: center;
      margin-bottom: 2rem;
      animation: fadeIn 1.5s ease-in-out;
      box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
    }
    .welcome-message h1 {
      font-size: 2.5rem;
      font-weight: 800;
      margin-bottom: 0.5rem;
      text-transform: uppercase;
      letter-spacing: 2px;
    }
    .welcome-message p { font-size: 1.2rem; opacity: 0.9; }
    .update-form {
      background-color: white;
      padding: 2rem;
      border-radius: 10px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      max-width: 600px;
      margin: 0 auto;
    }
    .update-form .form-label {
      font-weight: 600;
      color: #1E3A8A;
    }
    .update-form .form-control {
      border-radius: 8px;
      border: 1px solid #ddd;
      padding: 0.75rem;
      transition: border-color 0.3s ease;
    }
    .update-form .form-control:focus {
      border-color: #3B82F6;
      box-shadow: 0 0 5px rgba(59, 130, 246, 0.3);
    }
    .btn-custom {
      border: none;
      padding: 0.75rem 1.5rem;
      font-size: 1rem;
      font-weight: 600;
      border-radius: 20px;
      transition: transform 0.3s ease, background-color 0.3s ease;
      cursor: pointer;
    }
    .btn-primary { background-color: #0d6efd; color: white; }
    .btn-primary:hover { background-color: #0b5ed7; transform: scale(1.05); }
    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(20px); }
      to { opacity: 1; transform: translateY(0); }
    }
  </style>
</head>
<body>
<!-- Side Navigation Bar -->
<div class="sidebar">
  <div class="logo">
    <a href="index.jsp" class="text-3xl font-extrabold text-white">JOY-RIDE</a>
  </div>
  <a href="AdminProfile.jsp"><i class="fas fa-user mr-2"></i> Profile</a>
  <a href="AdminDashboard.jsp"><i class="fas fa-tachometer-alt mr-2"></i> Dashboard</a>
  <a href="Users.jsp"><i class="fas fa-users mr-2"></i> Users</a>
  <a href="AdminListServlet"><i class="fas fa-user-shield mr-2"></i> Admin Management</a>
  <a href="AdminBikeDetails.jsp"><i class="fas fa-motorcycle mr-2"></i> Bikes</a>
  <a href="AdminReviews.jsp"><i class="fas fa-star mr-2"></i> Reviews & Feedback</a>
  <a href="RentDetails.jsp"><i class="fas fa-file-alt mr-2"></i> Rent Details</a>
  <a href="PaymentDetails.jsp"><i class="fas fa-credit-card mr-2"></i> Payment Details</a>
  <a href="LogoutServlet"><i class="fas fa-sign-out-alt mr-2"></i> Logout</a>
</div>

<!-- Main Content -->
<div class="content">
  <%
    // Check if the user is an admin
    Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
    if (isAdmin == null || !isAdmin) {
  %>
  <div class="alert alert-danger" role="alert">
    Access Denied: You must be an admin to view this page. Please <a href="Login.jsp">log in</a> as an admin.
  </div>
  <% return; } %>

  <div class="welcome-message">
    <h1>UPDATE ADMIN</h1>
    <% String username = (String) session.getAttribute("username"); %>
    <p>Hi<%= username != null ? ", " + username : "" %>! Update admin details here.</p>
  </div>

  <%
    String errorMessage = (String) request.getAttribute("errorMessage");
    Map<String, String> adminDetails = (Map<String, String>) request.getAttribute("adminDetails");
  %>
  <% if (errorMessage != null) { %>
  <div class="alert alert-danger" role="alert"><%= errorMessage %></div>
  <% return; } %>
  <% if (adminDetails == null) { %>
  <div class="alert alert-danger" role="alert">Admin details not available.</div>
  <% return; } %>

  <!-- Update Form -->
  <div class="update-form">
    <h3 class="text-2xl font-bold mb-4 text-center text-gray-800">Update Admin</h3>
    <form action="UpdateAdminServlet" method="post">
      <div class="mb-4">
        <label for="username" class="form-label">Username</label>
        <input type="text" class="form-control" id="username" name="username" value="<%= adminDetails.get("username") %>">
      </div>
      <div class="mb-4">
        <label for="email" class="form-label">Email</label>
        <input type="email" class="form-control" id="email" name="email" value="<%= adminDetails.get("email") %>" required>
      </div>
      <div class="mb-4">
        <label for="city" class="form-label">City</label>
        <input type="text" class="form-control" id="city" name="city" value="<%= adminDetails.get("city") %>" required>
      </div>
      <div class="mb-4">
        <label for="nic" class="form-label">NIC</label>
        <input type="text" class="form-control" id="nic" name="nic" value="<%= adminDetails.get("nic") %>" required>
      </div>
      <div class="text-center">
        <button type="submit" class="btn btn-primary btn-custom">Update</button>
      </div>
    </form>
  </div>
</div>
</body>
</html>