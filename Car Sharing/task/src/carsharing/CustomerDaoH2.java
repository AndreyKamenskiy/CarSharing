package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoH2 implements CustomerDao {

    private Connection connectionH2;

    public CustomerDaoH2(Connection connectionH2) {
        this.connectionH2 = connectionH2;
    }

    @Override
    public List<Customer> getAllCustomers() {
        final String queryGetAllCustomers = "SELECT *\n" +
                "FROM customer\n" +
                "ORDER BY ID ASC;\n";
        List<Customer> customers = new ArrayList<>();
        try (Statement stmt = connectionH2.createStatement()) {
            try (ResultSet customer = stmt.executeQuery(queryGetAllCustomers)) {
                while (customer.next()) {
                    int id = customer.getInt("ID");
                    String name = customer.getString("NAME");
                    int rentedCarId = customer.getInt("rented_car_id");
                    // in case rented_car_id == null getInt will return 0;
                    customers.add(new Customer(id, name, rentedCarId ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return customers;
    }

    @Override
    public boolean addCustomer(String name) {
        String insert = "INSERT INTO customer (name) VALUES (?)"; // rented_car_id = null for new customer
        try (PreparedStatement preparedStatement = connectionH2.prepareStatement(insert)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void rentACar(Customer customer, Car car) {
        if (customer.getRentedCarId() != 0) {
            throw new IllegalArgumentException("Customer already rent a car id:" + customer.getRentedCarId());
        }
        customer.setRentedCarId(car.getId());
        String insert = "UPDATE customer \n" +
                        "SET {rented_car_id = ?}\n" +
                        "WHERE id = ?;";
        try (PreparedStatement preparedStatement = connectionH2.prepareStatement(insert)) {
            preparedStatement.setInt(1, car.getId());
            preparedStatement.setInt(2, customer.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void returnCar(Customer customer) {
        if (customer.getRentedCarId() == 0) {
            throw new IllegalArgumentException("Customer did not rent a car");
        }
        customer.setRentedCarId(0);
        String update = "UPDATE customer \n" +
                "SET {rented_car_id = NULL}\n" +
                "WHERE id = ?;";
        try (PreparedStatement preparedStatement = connectionH2.prepareStatement(update)) {
            preparedStatement.setInt(1, customer.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRented(Customer customer) {
        return customer.getRentedCarId() != 0;
    }

    @Override
    public int getRentedCar(Customer customer) {
        return customer.getRentedCarId();
    }
}
