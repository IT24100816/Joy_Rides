<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JOY-RIDE - Update User</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body { background-color: #f0f0f0; font-family: 'Arial', sans-serif; }
        .container {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .form-group { margin-bottom: 1.5rem; }
        .form-label { font-weight: 600; color: #1E3A8A; }
        .form-control { border-radius: 8px; }
        .btn-custom {
            border: none;
            padding: 0.5rem 1.5rem;
            font-size: 1rem;
            font-weight: 700;
            border-radius: 50px;
            transition: transform 0.3s ease, background-color 0.3s ease;
            width: 120px;
        }
        .btn-primary { background-color: #0d6efd; color: white; }
        .btn-primary:hover { background-color: #0b5ed7; transform: scale(1.05); }
        .btn-secondary { background-color: #6c757d; color: white; }
        .btn-secondary:hover { background-color: #5c636a; transform: scale(1.05); }
    </style>
</head>
<body>
<div class="container">
    <h2 class="text-2xl font-bold mb-4 text-center text-gray-800">Update User Details</h2>
    <%
        String successMessage = request.getParameter("successMessage");
        String errorMessage = request.getParameter("errorMessage");
    %>
    <% if (successMessage != null) { %>
    <div class="alert alert-success" role="alert"><%= successMessage %></div>
    <% } %>
    <% if (errorMessage != null) { %>
    <div class="alert alert-danger" role="alert"><%= errorMessage %></div>
    <% } %>

    <form action="SaveUserUpdateServlet" method="post">
        <input type="hidden" name="oldUsername" value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>">
        <div class="form-group">
            <label class="form-label">Username:</label>
            <input type="text" class="form-control" name="username" value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>" required>
        </div>
        <div class="form-group">
            <label class="form-label">Email:</label>
            <input type="email" class="form-control" name="email" value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>" required>
        </div>
        <div class="form-group">
            <label class="form-label">City:</label>
            <input type="text" class="form-control" name="city" value="<%= request.getAttribute("city") != null ? request.getAttribute("city") : "" %>" required>
        </div>
        <div class="form-group">
            <label class="form-label">NIC:</label>
            <input type="text" class="form-control" name="nic" value="<%= request.getAttribute("nic") != null ? request.getAttribute("nic") : "" %>" required>
        </div>
        <div class="text-center">
            <button type="submit" class="btn btn-primary btn-custom">Save Changes</button>
            <a href="Users.jsp" class="btn btn-secondary btn-custom">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>