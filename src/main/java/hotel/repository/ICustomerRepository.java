package hotel.repository;

import hotel.security.Role;

public interface ICustomerRepository {
    Integer findCustomerIdByEmail(String email);
    int createCustomer(String name, String email);

    // NEW FOR ITERATION 2
    Role getRoleByEmail(String email);
    boolean isEmailExists(String email);
}