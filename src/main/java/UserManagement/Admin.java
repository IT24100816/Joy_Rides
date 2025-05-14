package UserManagement;

// Admin class inheriting from User
public class Admin extends User {
    public Admin(String username, String email, String city, String nic) {
        super(username, email, city, nic);
    }

    @Override
    public String display() {
        return "Admin: " + username + " | Email: " + email + " (Admin Privileges)";
    }

    @Override
    public String getRedirectPage() {
        return "AdminDashboard.jsp";
    }
//changes
    // Admin-specific method (abstraction)
    public void performAdminAction() {
        System.out.println("Admin action performed by: " + username);
    }

}
