package hotel.controller;

import hotel.entity.Room;
import hotel.repository.*;
import hotel.util.LoggerUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

public class BookingController {

    private static final Logger log = LoggerUtil.getLogger();

    private final IRoomRepository roomRepository = new RoomRepository();
    private final IBookingRepository bookingRepository = new BookingRepository();
    private final ICustomerRepository customerRepository = new CustomerRepository();

    public List<Room> getAllRooms() {
        log.info("Get all rooms");
        return roomRepository.getAllRooms();
    }

    public List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            log.warning("Invalid dates: " + checkIn + " -> " + checkOut);
            throw new IllegalArgumentException("Invalid dates.");
        }
        log.info("Get available rooms for: " + checkIn + " -> " + checkOut);
        return roomRepository.getAvailableRoomsForDates(checkIn, checkOut);
    }

    public List<String> getAllBookings() {
        log.info("Get all bookings");
        return bookingRepository.getAllBookingsDetails();
    }

    public String bookRoom(int roomNumber, String name, String email,
                           LocalDate checkIn, LocalDate checkOut) {

        if (roomNumber <= 0) return "Room number must be positive.";
        if (name == null || name.trim().isEmpty()) return "Name is empty.";
        if (email == null || !email.contains("@")) return "Email is invalid.";
        if (!checkOut.isAfter(checkIn)) return "Invalid dates.";

        Room room = roomRepository.findByRoomNumber(roomNumber);
        if (room == null) return "Room not found.";

        boolean available = bookingRepository.isRoomAvailableForDates(room.getId(), checkIn, checkOut);
        if (!available) {
            log.info("Booking failed: room not available. room=" + roomNumber);
            return "Room is not available.";
        }

        Integer customerId = customerRepository.findCustomerIdByEmail(email);
        if (customerId == null) {
            customerId = customerRepository.createCustomer(name, email);
            log.info("Customer created: " + email);
        }

        int bookingId = bookingRepository.createBooking(customerId, room.getId(), checkIn, checkOut);

        long nights = bookingRepository.calculateNights(checkIn, checkOut);
        double total = nights * room.getPricePerNight();

        log.info("Booking created: id=" + bookingId + ", room=" + roomNumber + ", total=" + total);
        return "Booked. ID=" + bookingId + ", total=" + total;
    }

    public String cancelBooking(int roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber);
        if (room == null) return "Room not found.";

        Integer bookingId = bookingRepository.findActiveBookingIdByRoomId(room.getId());
        if (bookingId == null) return "No active booking.";

        bookingRepository.deleteBookingById(bookingId);
        log.info("Booking cancelled: id=" + bookingId + ", room=" + roomNumber);

        return "Booking cancelled.";
    }

    public LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            log.warning("Wrong date format: " + value);
            throw new IllegalArgumentException("Wrong date format");
        }
    }
}