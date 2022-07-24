package carsharing;

public class Customer {
    private final int id;
    private final String name;
    private int rentedCarId; // 0 equals null, mean that no rented car now for customer.

    public Customer(int id, String name, int rentedCarId) {
        this.name = name;
        this.id = id;
        this.rentedCarId = rentedCarId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRentedCarId() {
        return rentedCarId;
    }

    public void setRentedCarId(Integer rentedCarId) {
        this.rentedCarId = rentedCarId;
    }

    @Override
    public String toString() {
        return name;
    }
}
