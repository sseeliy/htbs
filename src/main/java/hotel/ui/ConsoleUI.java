package hotel.ui;

import hotel.controller.BookingController;
import hotel.entity.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private final BookingController controller = new BookingController();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n=== Hotel Booking System (with Dates) ===");
            System.out.println("1. Show all rooms");
            System.out.println("2. Show available rooms for dates");
            System.out.println("3. Book a room");
            System.out.println("4. Cancel booking");
            System.out.println("5. Show all bookings");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Please enter a number.");
                continue;
            }

            if (choice == 1) {
                List<Room> rooms = controller.getAllRooms();
                for (int i = 0; i < rooms.size(); i++) {
                    System.out.println(rooms.get(i));
                }

            } else if (choice == 2) {
                handleShowAvailableRooms();

            } else if (choice == 3) {
                handleBookRoom();

            } else if (choice == 4) {
                handleCancelBooking();

            } else if (choice == 5) {
                List<String> bookings = controller.getAllBookings();
                for (int i = 0; i < bookings.size(); i++) {
                    System.out.println(bookings.get(i));
                }

            } else if (choice == 0) {
                System.out.println("Goodbye!");
                return;

            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private void handleShowAvailableRooms() {
        try {
            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            LocalDate checkIn = controller.parseDate(scanner.nextLine());

            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            LocalDate checkOut = controller.parseDate(scanner.nextLine());

            List<Room> rooms = controller.getAvailableRoomsForDates(checkIn, checkOut);
            for (int i = 0; i < rooms.size(); i++) {
                System.out.println(rooms.get(i));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleBookRoom() {
        try {
            System.out.print("Enter room number: ");
            int roomNumber = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter customer name: ");
            String name = scanner.nextLine();

            System.out.print("Enter customer email: ");
            String email = scanner.nextLine();

            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            LocalDate checkIn = controller.parseDate(scanner.nextLine());

            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            LocalDate checkOut = controller.parseDate(scanner.nextLine());

            String result = controller.bookRoom(roomNumber, name, email, checkIn, checkOut);
            System.out.println(result);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleCancelBooking() {
        try {
            System.out.print("Enter room number to cancel booking: ");
            int roomNumber = Integer.parseInt(scanner.nextLine());

            String result = controller.cancelBooking(roomNumber);
            System.out.println(result);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}