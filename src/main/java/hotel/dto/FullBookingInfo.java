package hotel.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FullBookingInfo {
    private int bookingId;
    private int roomNumber;
    private String roomType;
    private String customerName;
    private String customerEmail;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double pricePerNight;
    private String categoryName;
    private long nights;
    private double totalPrice;

    public FullBookingInfo(int bookingId, int roomNumber, String roomType,
                           String customerName, String customerEmail,
                           LocalDate checkIn, LocalDate checkOut,
                           double pricePerNight, String categoryName) {
        this.bookingId = bookingId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.pricePerNight = pricePerNight;
        this.categoryName = categoryName;
        this.nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalPrice = this.nights * pricePerNight;
    }

    public int getBookingId() { return bookingId; }
    public int getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getPricePerNight() { return pricePerNight; }
    public String getCategoryName() { return categoryName; }
    public long getNights() { return nights; }
    public double getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        return "=== FULL BOOKING DETAILS ===\n" +
                "Booking ID: " + bookingId + "\n" +
                "Room: " + roomNumber + " (" + roomType + ") - " + categoryName + "\n" +
                "Customer: " + customerName + " (" + customerEmail + ")\n" +
                "Check-in: " + checkIn + "\n" +
                "Check-out: " + checkOut + "\n" +
                "Nights: " + nights + "\n" +
                "Price per night: $" + pricePerNight + "\n" +
                "TOTAL: $" + totalPrice + "\n" +
                "============================";
    }
}
