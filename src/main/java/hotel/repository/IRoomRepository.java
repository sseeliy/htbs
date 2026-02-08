package hotel.repository;

import hotel.entity.Room;

import java.time.LocalDate;
import java.util.List;

public interface IRoomRepository {
    List<Room> getAllRooms();
    Room findByRoomNumber(int roomNumber);
    List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut);
}