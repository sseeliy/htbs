package hotel.repository;

import hotel.db.DatabaseConnection;
import hotel.entity.Room;
import hotel.util.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RoomRepository implements IRoomRepository {

    private static final Logger log = LoggerUtil.getLogger();

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT id, room_number, room_type, price_per_night FROM rooms ORDER BY room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("id"),
                        rs.getInt("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price_per_night")
                );
                rooms.add(room);
            }

        } catch (Exception e) {
            log.warning("getAllRooms error: " + e.getMessage());
            throw new RuntimeException("load rooms failed");
        }

        return rooms;
    }

    @Override
    public Room findByRoomNumber(int roomNumber) {
        String sql = "SELECT id, room_number, room_type, price_per_night FROM rooms WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomNumber);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Room(
                        rs.getInt("id"),
                        rs.getInt("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price_per_night")
                );
            }

        } catch (Exception e) {
            log.warning("findByRoomNumber error: " + e.getMessage());
            throw new RuntimeException("find room failed");
        }

        return null;
    }

    @Override
    public List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        List<Room> rooms = new ArrayList<>();

        String sql =
                "SELECT r.id, r.room_number, r.room_type, r.price_per_night " +
                        "FROM rooms r " +
                        "WHERE r.id NOT IN ( " +
                        "   SELECT b.room_id FROM bookings b " +
                        "   WHERE NOT (b.check_out <= ? OR b.check_in >= ?) " +
                        ") " +
                        "ORDER BY r.room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(checkIn));
            ps.setDate(2, java.sql.Date.valueOf(checkOut));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("id"),
                        rs.getInt("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price_per_night")
                );
                rooms.add(room);
            }

        } catch (Exception e) {
            log.warning("getAvailableRoomsForDates error: " + e.getMessage());
            throw new RuntimeException("load available rooms failed");
        }

        return rooms;
    }
}