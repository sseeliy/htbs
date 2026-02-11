package hotel.repository;

import hotel.entity.Room;
import hotel.entity.Category;

import java.time.LocalDate;
import java.util.List;

public interface IRoomRepository {
    List<Room> getAllRooms();
    Room findByRoomNumber(int roomNumber);
    List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut);

    // NEW FOR ITERATION 2
    List<Room> getRoomsByCategory(int categoryId);
    List<Category> getAllCategories();
    List<Room> getRoomsCheaperThan(double maxPrice);
}