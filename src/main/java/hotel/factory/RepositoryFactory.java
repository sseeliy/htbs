package hotel.factory;

import hotel.repository.*;

public class RepositoryFactory {

    private RepositoryFactory() {
        // запрещаем создание объекта
    }

    public static IRoomRepository createRoomRepository() {
        return new RoomRepository();
    }

    public static IBookingRepository createBookingRepository() {
        return new BookingRepository();
    }

    public static ICustomerRepository createCustomerRepository() {
        return new CustomerRepository();
    }
}
