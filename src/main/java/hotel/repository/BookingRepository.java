package hotel.repository;

import hotel.db.DatabaseConnection;
import hotel.dto.FullBookingInfo;
import hotel.util.LoggerUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BookingRepository implements IBookingRepository {

    private static final Logger log = LoggerUtil.getInstance().getLogger();

    @Override
    public int createBooking(int customerId, int roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql = "INSERT INTO bookings(customer_id, room_id, check_in, check_out) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, roomId);
            ps.setDate(3, Date.valueOf(checkIn));
            ps.setDate(4, Date.valueOf(checkOut));

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("id");

        } catch (Exception e) {
            log.warning("createBooking error: " + e.getMessage());
            throw new RuntimeException("Create booking failed");
        }
    }

    @Override
    public boolean isRoomAvailableForDates(int roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT COUNT(*) FROM bookings " +
                "WHERE room_id = ? " +
                "AND NOT (check_out <= ? OR check_in >= ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(checkIn));
            ps.setDate(3, Date.valueOf(checkOut));

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) == 0;

        } catch (Exception e) {
            log.warning("isRoomAvailableForDates error: " + e.getMessage());
            throw new RuntimeException("Availability check failed");
        }
    }

    @Override
    public Integer findActiveBookingIdByRoomId(int roomId) {
        String sql = "SELECT id FROM bookings " +
                "WHERE room_id = ? AND check_out > CURRENT_DATE " +
                "ORDER BY check_in DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");

        } catch (Exception e) {
            log.warning("findActiveBookingIdByRoomId error: " + e.getMessage());
            throw new RuntimeException("Find booking failed");
        }

        return null;
    }

    @Override
    public void deleteBookingById(int bookingId) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ps.executeUpdate();

        } catch (Exception e) {
            log.warning("deleteBookingById error: " + e.getMessage());
            throw new RuntimeException("Delete failed");
        }
    }

    @Override
    public List<String> getAllBookingsDetails() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT b.id, b.check_in, b.check_out, c.name, c.email, r.room_number " +
                "FROM bookings b " +
                "JOIN customers c ON b.customer_id = c.id " +
                "JOIN rooms r ON b.room_id = r.id " +
                "ORDER BY b.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String line = "Booking{id=" + rs.getInt("id") +
                        ", room=" + rs.getInt("room_number") +
                        ", customer=" + rs.getString("name") +
                        " (" + rs.getString("email") + ")" +
                        ", in=" + rs.getDate("check_in") +
                        ", out=" + rs.getDate("check_out") +
                        "}";
                list.add(line);
            }

        } catch (Exception e) {
            log.warning("getAllBookingsDetails error: " + e.getMessage());
            throw new RuntimeException("Load bookings failed");
        }

        return list;
    }

    @Override
    public long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        return java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    @Override
    public FullBookingInfo getFullBookingInfo(int bookingId) {
        String sql = "SELECT b.id, b.check_in, b.check_out, " +
                "c.name as customer_name, c.email as customer_email, " +
                "r.room_number, r.room_type, r.price_per_night, " +
                "cat.name as category_name " +
                "FROM bookings b " +
                "JOIN customers c ON b.customer_id = c.id " +
                "JOIN rooms r ON b.room_id = r.id " +
                "LEFT JOIN categories cat ON r.category_id = cat.id " +
                "WHERE b.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new FullBookingInfo(
                        rs.getInt("id"),
                        rs.getInt("room_number"),
                        rs.getString("room_type"),
                        rs.getString("customer_name"),
                        rs.getString("customer_email"),
                        rs.getDate("check_in").toLocalDate(),
                        rs.getDate("check_out").toLocalDate(),
                        rs.getDouble("price_per_night"),
                        rs.getString("category_name") != null ? rs.getString("category_name") : "No category"
                );
            }

        } catch (Exception e) {
            log.warning("getFullBookingInfo error: " + e.getMessage());
            throw new RuntimeException("Get full booking info failed");
        }

        return null;
    }
}