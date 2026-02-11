package hotel.factory;

import hotel.repository.*;

public class RepositoryFactory {

    private static RepositoryFactory instance;

    private final IRoomRepository roomRepository;
    private final IBookingRepository bookingRepository;
    private final ICustomerRepository customerRepository;

    private RepositoryFactory() {
        this.roomRepository = new RoomRepository();
        this.bookingRepository = new BookingRepository();
        this.customerRepository = new CustomerRepository();
    }

    public static RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    public IRoomRepository getRoomRepository() {
        return roomRepository;
    }

    public IBookingRepository getBookingRepository() {
        return bookingRepository;
    }

    public ICustomerRepository getCustomerRepository() {
        return customerRepository;
    }
}
