package hotel.repository;

import hotel.db.DatabaseConnection;
import hotel.entity.Category;
import hotel.entity.Room;
import hotel.util.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RoomRepository implements IRoomRepository {

    private static final Logger log = LoggerUtil.getInstance().getLogger();

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.id, r.room_number, r.room_type, r.price_per_night, " +
                "r.category_id, c.name as category_name " +
                "FROM rooms r " +
                "LEFT JOIN categories c ON r.category_id = c.id " +
                "ORDER BY r.room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapRoom(rs));
            }

        } catch (Exception e) {
            log.warning("getAllRooms error: " + e.getMessage());
            throw new RuntimeException("Load rooms failed");
        }

        return rooms;
    }

    @Override
    public Room findByRoomNumber(int roomNumber) {
        String sql = "SELECT r.id, r.room_number, r.room_type, r.price_per_night, " +
                "r.category_id, c.name as category_name " +
                "FROM rooms r " +
                "LEFT JOIN categories c ON r.category_id = c.id " +
                "WHERE r.room_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRoom(rs);
            }

        } catch (Exception e) {
            log.warning("findByRoomNumber error: " + e.getMessage());
            throw new RuntimeException("Find room failed");
        }

        return null;
    }

    @Override
    public List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT r.id, r.room_number, r.room_type, r.price_per_night, " +
                "r.category_id, c.name as category_name " +
                "FROM rooms r " +
                "LEFT JOIN categories c ON r.category_id = c.id " +
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
                rooms.add(mapRoom(rs));
            }

        } catch (Exception e) {
            log.warning("getAvailableRoomsForDates error: " + e.getMessage());
            throw new RuntimeException("Load available rooms failed");
        }

        return rooms;
    }

    @Override
    public List<Room> getRoomsByCategory(int categoryId) {
        return getAllRooms().stream()
                .filter(room -> room.getCategory() != null && room.getCategory().getId() == categoryId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name FROM categories ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }

        } catch (Exception e) {
            log.warning("getAllCategories error: " + e.getMessage());
            throw new RuntimeException("Load categories failed");
        }

        return categories;
    }

    @Override
    public List<Room> getRoomsCheaperThan(double maxPrice) {
        return getAllRooms().stream()
                .filter(room -> room.getPricePerNight() < maxPrice)
                .collect(Collectors.toList());
    }

    private Room mapRoom(ResultSet rs) throws Exception {
        Room room = new Room(
                rs.getInt("id"),
                rs.getInt("room_number"),
                rs.getString("room_type"),
                rs.getDouble("price_per_night")
        );

        int categoryId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            Category category = new Category(
                    categoryId,
                    rs.getString("category_name")
            );
            room.setCategory(category);
        }

        return room;
    }
}