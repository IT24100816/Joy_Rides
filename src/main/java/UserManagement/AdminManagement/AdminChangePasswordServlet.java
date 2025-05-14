package UserManagement.AdminManagement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/AdminChangePasswordServlet")
public class AdminChangePasswordServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            response.sendRedirect("AdminProfile.jsp?errorMessage=Username not provided.");
            return;
        }
        request.setAttribute("username", username);
        request.getRequestDispatcher("/AdminChangePassword.jsp").forward(request, response);
    }
}