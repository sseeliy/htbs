package hotel.dto;

import java.time.LocalDate;

public class BookingFullDTO {

    private int bookingId;

    // customer
    private String customerName;
    private String customerEmail;

    // room
    private String roomNumber;
    private String roomCategory;
    private double roomPrice;

    // booking
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public BookingFullDTO(
            int bookingId,
            String customerName,
            String customerEmail,
            String roomNumber,
            String roomCategory,
            double roomPrice,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.roomNumber = roomNumber;
        this.roomCategory = roomCategory;
        this.roomPrice = roomPrice;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    // getters only (DTO is immutable for safety)

    public int getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomCategory() {
        return roomCategory;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
}
