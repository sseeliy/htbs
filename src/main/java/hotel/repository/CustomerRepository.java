package hotel.repository;

import hotel.db.DatabaseConnection;
import hotel.security.Role;
import hotel.util.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class CustomerRepository implements ICustomerRepository {

    private static final Logger log = LoggerUtil.getInstance().getLogger();

    @Override
    public Integer findCustomerIdByEmail(String email) {
        String sql = "SELECT id FROM customers WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");

        } catch (Exception e) {
            log.warning("findCustomerIdByEmail error: " + e.getMessage());
            throw new RuntimeException("Find customer failed");
        }

        return null;
    }

    @Override
    public int createCustomer(String name, String email) {
        String sql = "INSERT INTO customers(name, email, role) VALUES (?, ?, 'USER') RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("id");

        } catch (Exception e) {
            log.warning("createCustomer error: " + e.getMessage());
            throw new RuntimeException("Create customer failed");
        }
    }

    @Override
    public Role getRoleByEmail(String email) {
        String sql = "SELECT role FROM customers WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String roleStr = rs.getString("role");
                if (roleStr == null) return Role.USER;
                try {
                    return Role.valueOf(roleStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return Role.USER;
                }
            }

        } catch (Exception e) {
            log.warning("getRoleByEmail error: " + e.getMessage());
            throw new RuntimeException("Get role failed");
        }

        return Role.USER;
    }

    @Override
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;

        } catch (Exception e) {
            log.warning("isEmailExists error: " + e.getMessage());
            throw new RuntimeException("Check email failed");
        }
    }
}