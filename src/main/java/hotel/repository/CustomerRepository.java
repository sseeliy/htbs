package hotel.repository;

import hotel.db.DatabaseConnection;
import hotel.util.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class CustomerRepository implements ICustomerRepository {

    private static final Logger log = LoggerUtil.getLogger();

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
            throw new RuntimeException("find customer failed");
        }

        return null;
    }

    @Override
    public int createCustomer(String name, String email) {
        String sql = "INSERT INTO customers(name, email) VALUES (?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("id");

        } catch (Exception e) {
            log.warning("createCustomer error: " + e.getMessage());
            throw new RuntimeException("create customer failed");
        }
    }
}