package hotel.ui;

import hotel.controller.BookingController;
import hotel.entity.Category;
import hotel.entity.Room;
import hotel.repository.CustomerRepository;
import hotel.security.Role;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private final BookingController controller = new BookingController();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final Scanner scanner = new Scanner(System.in);

    private String currentUserEmail = null;
    private Role currentUserRole = Role.USER;

    public void start() {
        login();

        while (true) {
            showMenu();
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Please enter a number.");
                continue;
            }

            if (choice == 0) {
                System.out.println("Goodbye!");
                break;
            }

            handleChoice(choice);
        }
    }

    private void login() {
        System.out.println("\n=== HOTEL BOOKING SYSTEM ===");
        System.out.println("Please login with your email:");

        while (currentUserEmail == null) {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("Email cannot be empty");
                continue;
            }

            currentUserEmail = email;
            currentUserRole = customerRepository.getRoleByEmail(email);

            System.out.println("Welcome, " + email + "! Role: " + currentUserRole);

            // Auto-register if not exists?
            if (customerRepository.findCustomerIdByEmail(email) == null) {
                System.out.print("Email not registered. Enter your name to register: ");
                String name = scanner.nextLine().trim();
                if (name.length() >= 2) {
                    customerRepository.createCustomer(name, email);
                    System.out.println("Registration successful!");
                } else {
                    System.out.println("Name too short. Using guest account.");
                }
            }
        }
    }

    private void showMenu() {
        System.out.println("\n=== HOTEL BOOKING SYSTEM ===");
        System.out.println("User: " + currentUserEmail + " (" + currentUserRole + ")");
        System.out.println("1. Show all rooms");
        System.out.println("2. Show available rooms for dates");
        System.out.println("3. Book a room");
        System.out.println("4. Cancel booking");
        System.out.println("5. Show all bookings");
        System.out.println("6. Get full booking details (JOIN demo)"); // NEW

        // LAMBDA DEMO
        System.out.println("7. Show rooms cheaper than $100 (Lambda demo)"); // NEW

        // CATEGORIES
        System.out.println("8. Show rooms by category"); // NEW

        // ADMIN ONLY
        if (currentUserRole == Role.ADMIN) {
            System.out.println("9. [ADMIN] Show all customers with roles");
        }

        System.out.println("0. Exit");
    }

    private void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    showAllRooms();
                    break;
                case 2:
                    showAvailableRooms();
                    break;
                case 3:
                    bookRoom();
                    break;
                case 4:
                    cancelBooking();
                    break;
                case 5:
                    showAllBookings();
                    break;
                case 6:
                    showFullBookingDetails(); // JOIN DEMO
                    break;
                case 7:showRoomsCheaperThan100(); // LAMBDA DEMO
                    break;
                case 8:
                    showRoomsByCategory(); // CATEGORIES
                    break;
                case 9:
                    if (currentUserRole == Role.ADMIN) {
                        showAllCustomers();
                    } else {
                        System.out.println("Invalid option");
                    }
                    break;
                default:
                    System.out.println("Invalid option");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showAllCustomers() {
    }

    private void showAllRooms() {
        List<Room> rooms = controller.getAllRooms();
        System.out.println("\n=== ALL ROOMS ===");
        for (Room room : rooms) {
            System.out.println(room);
        }
    }

    private void showAvailableRooms() {
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkIn = controller.parseDate(scanner.nextLine());
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        LocalDate checkOut = controller.parseDate(scanner.nextLine());

        List<Room> rooms = controller.getAvailableRoomsForDates(checkIn, checkOut);
        System.out.println("\n=== AVAILABLE ROOMS ===");
        for (Room room : rooms) {
            System.out.println(room);
        }
    }

    private void bookRoom() {
        System.out.print("Enter room number: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkIn = controller.parseDate(scanner.nextLine());
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        LocalDate checkOut = controller.parseDate(scanner.nextLine());

        String result = controller.bookRoom(roomNumber, name, currentUserEmail, checkIn, checkOut);
        System.out.println(result);
    }

    private void cancelBooking() {
        System.out.print("Enter room number to cancel: ");
        int roomNumber = Integer.parseInt(scanner.nextLine());
        String result = controller.cancelBooking(roomNumber);
        System.out.println(result);
    }

    private void showAllBookings() {
        List<String> bookings = controller.getAllBookings();
        System.out.println("\n=== ALL BOOKINGS ===");
        for (String booking : bookings) {
            System.out.println(booking);
        }
    }

    // NEW - JOIN DEMO
    private void showFullBookingDetails() {
        System.out.print("Enter booking ID: ");
        int bookingId = Integer.parseInt(scanner.nextLine());
        String result = controller.getFullBookingDescription(bookingId);
        System.out.println(result);
    }

    // NEW - LAMBDA DEMO
    private void showRoomsCheaperThan100() {
        List<Room> rooms = controller.getRoomsCheaperThan(100);
        System.out.println("\n=== ROOMS CHEAPER THAN $100 (Lambda demo) ===");
        if (rooms.isEmpty()) {
            System.out.println("No rooms found");
        } else {
            for (Room room : rooms) {
                System.out.println(room);
            }
        }
    }

    // NEW - CATEGORIES
    private void showRoomsByCategory() {
        // First show all categories
        List<Room> allRooms = controller.getAllRooms();
        System.out.println("\n=== CATEGORIES ===");

        // Simple way - extract unique categories
        allRooms.stream()
                .map(room -> room.getCategory())
                .distinct()
                .forEach(cat -> System.out.println(cat.getId() + ". " + cat.getName()));

        System.out.print("Choose category ID: ");
        int categoryId = Integer.parseInt(scanner.nextLine());

        List<Room> rooms = controller.getRoomsByCategory(categoryId);
        System.out.println("\n=== ROOMS IN SELECTED CATEGORY ===");
        for (Room room : rooms) {
            System.out.println(room);
        }
    }
}
// ADMIN ONLY
//    private void showAllCustomers() {
//        // Simple implementation - could be improved
//        System.out.println("\n=== ALL CUSTOMERS (Admin only) ===");
//        System.out.println("This would show all customers with roles");
//        System.out.println("For demo: admin@hotel.com, john@gmail.com (ADMIN)");
//    }
//}