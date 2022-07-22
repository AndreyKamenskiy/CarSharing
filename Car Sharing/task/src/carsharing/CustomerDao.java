package carsharing;

import java.util.List;

public interface CustomerDao {
    List<Customer> getAllCustomers();
    boolean addCustomer(String name);
    void rentACar(Customer customer, Car car);
    void returnCar(Customer customer);
    boolean isRented(Customer customer);
    int getRentedCar(Customer customer);
}
