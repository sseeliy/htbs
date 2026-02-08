package hotel.repository;

public interface ICustomerRepository {
    Integer findCustomerIdByEmail(String email);
    int createCustomer(String name, String email);
}