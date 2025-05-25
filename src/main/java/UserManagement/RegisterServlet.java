package UserManagement;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

// Interface for abstracting user data storage (Abstraction)
interface UserRepository {
    boolean saveUser(User user, String hashedPassword) throws IOException;
}

// Concrete implementation of UserRepository for file-based storage
class FileUserRepository implements UserRepository {
    private static final String FILE_PATH = "/Users/samadhithjayasena/Library/CloudStorage/OneDrive-SriLankaInstituteofInformationTechnology/IntelliJ IDEA/Website/src/main/resources/users.txt";

    @Override
    public boolean saveUser(User user, String hashedPassword) throws IOException {
        try (java.io.FileWriter fileWriter = new java.io.FileWriter(FILE_PATH, true);
             java.io.PrintWriter writer = new java.io.PrintWriter(fileWriter)) {
            writer.println(user.getUsername() + " | " + user.getEmail() + " | " + hashedPassword + " | " +
                    user.getCity() + " | " + user.getNic());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
            return false;
        }
    }
}

@WebServlet("/registerServlet")
public class RegisterServlet extends HttpServlet {
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        userRepository = new FileUserRepository();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String city = request.getParameter("city");
        String nic = request.getParameter("nic");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");
        String formType = request.getParameter("formType");

        request.setAttribute("formType", formType);

        // Validate input fields
        if (isEmpty(username) || isEmpty(email) || isEmpty(city) || isEmpty(nic) ||
                isEmpty(password) || isEmpty(confirmPassword)) {
            request.setAttribute("errorMessage", "All fields are required!");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
            return;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match!");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
            return;
        }

        User user = new RegularUser(username, email, city, nic);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("RegisterServlet: Hashed Password = " + hashedPassword);

        // Save user data using repository (Abstraction)
        if (userRepository.saveUser(user, hashedPassword)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", user.getUsername());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("city", user.getCity());
            session.setAttribute("nic", user.getNic());
            session.setAttribute("isAdmin", false);
            session.setAttribute("userObject", user);
            response.sendRedirect(user.getRedirectPage());
        } else {
            request.setAttribute("errorMessage", "Failed to save user data. Please try again.");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}