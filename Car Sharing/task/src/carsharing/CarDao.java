package carsharing;

import java.util.List;

public interface CarDao {
    List<Car> getCompanyCars(Company company);
    boolean addCar(Company company,String carName);
    Car getCarById(int id);
}
