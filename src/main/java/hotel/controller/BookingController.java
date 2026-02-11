package hotel.controller;

import hotel.dto.FullBookingInfo;
import hotel.entity.Room;
import hotel.factory.RepositoryFactory;
import hotel.repository.*;
import hotel.util.LoggerUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

public class BookingController {

    private static final Logger log = LoggerUtil.getInstance().getLogger();

    private final IRoomRepository roomRepository;
    private final IBookingRepository bookingRepository;
    private final ICustomerRepository customerRepository;

    public BookingController() {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        this.roomRepository = factory.getRoomRepository();
        this.bookingRepository = factory.getBookingRepository();
        this.customerRepository = factory.getCustomerRepository();
    }

    public List<Room> getAllRooms() {
        log.info("Get all rooms");
        return roomRepository.getAllRooms();
    }

    public List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            log.warning("Invalid dates: " + checkIn + " -> " + checkOut);
            throw new IllegalArgumentException("Check-out must be after check-in");
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

        // VALIDATION - ITERATION 2
        if (roomNumber <= 0 || roomNumber > 999) {
            return "Room number must be between 1 and 999";
        }
        if (name == null || name.trim().length() < 2) {
            return "Name must be at least 2 characters";
        }
        if (email == null || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "Invalid email format";
        }
        if (!checkOut.isAfter(checkIn)) {
            return "Check-out must be after check-in";
        }

        Room room = roomRepository.findByRoomNumber(roomNumber);
        if (room == null) {
            return "Room not found";
        }

        boolean available = bookingRepository.isRoomAvailableForDates(room.getId(), checkIn, checkOut);
        if (!available) {
            log.info("Booking failed: room not available. room=" + roomNumber);
            return "Room is not available for these dates";
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
        return "Booking successful. ID=" + bookingId + ", total=$" + total;
    }

    public String cancelBooking(int roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber);
        if (room == null) {
            return "Room not found";
        }

        Integer bookingId = bookingRepository.findActiveBookingIdByRoomId(room.getId());
        if (bookingId == null) {
            return "No active booking for this room";
        }

        bookingRepository.deleteBookingById(bookingId);
        log.info("Booking cancelled: id=" + bookingId + ", room=" + roomNumber);
        return "Booking cancelled successfully";
    }

    // NEW FOR ITERATION 2 - JOIN DEMO
    public String getFullBookingDescription(int bookingId) {
        log.info("Get full booking info: id="
                + bookingId);
        FullBookingInfo info = bookingRepository.getFullBookingInfo(bookingId);
        if (info == null) {
            return "Booking not found";
        }
        return info.toString();
    }

    // NEW FOR ITERATION 2 - LAMBDA DEMO
    public List<Room> getRoomsCheaperThan(double maxPrice) {
        log.info("Get rooms cheaper than: $" + maxPrice);
        return roomRepository.getRoomsCheaperThan(maxPrice);
    }

    // NEW FOR ITERATION 2 - CATEGORIES
    public List<Room> getRoomsByCategory(int categoryId) {
        log.info("Get rooms by category: " + categoryId);
        return roomRepository.getRoomsByCategory(categoryId);
    }

    public LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            log.warning("Wrong date format: " + value);
            throw new IllegalArgumentException("Use format: YYYY-MM-DD");
        }
    }
}