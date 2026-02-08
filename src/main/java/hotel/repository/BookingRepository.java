package hotel.repository;

import hotel.db.DatabaseConnection;
import hotel.util.LoggerUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BookingRepository implements IBookingRepository {

    private static final Logger log = LoggerUtil.getLogger();

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
            throw new RuntimeException("create booking failed");
        }
    }

    @Override
    public boolean isRoomAvailableForDates(int roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql =
                "SELECT COUNT(*) FROM bookings " +
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
            throw new RuntimeException("availability check failed");
        }
    }

    @Override
    public Integer findActiveBookingIdByRoomId(int roomId) {
        String sql =
                "SELECT id FROM bookings " +
                        "WHERE room_id = ? AND check_out > CURRENT_DATE " +
                        "ORDER BY check_in DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");

        } catch (Exception e) {
            log.warning("findActiveBookingIdByRoomId error: " + e.getMessage());
            throw new RuntimeException("find booking failed");
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
            throw new RuntimeException("delete failed");
        }
    }

    @Override
    public List<String> getAllBookingsDetails() {
        List<String> list = new ArrayList<>();

        String sql =
                "SELECT b.id, b.check_in, b.check_out, c.name, c.email, r.room_number " +
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
            throw new RuntimeException("load bookings failed");
        }

        return list;
    }

    @Override
    public long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        return java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    }
}