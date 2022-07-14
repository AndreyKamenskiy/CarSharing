package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDaoH2 implements CarDao{

    private final Connection connectionH2;

    public CarDaoH2(Connection connectionH2) {
        this.connectionH2 = connectionH2;
    }

    @Override
    public List<Car> getCompanyCars(Company company) {
        final String queryGetCompanyCars = "SELECT id, name\n" +
                "FROM car\n" +
                "WHERE company_id = ?\n" +
                "ORDER BY ID ASC;\n";
        List<Car> carList = new ArrayList<>();
        int companyId = company.getId();
        try (PreparedStatement preparedStatement = connectionH2.prepareStatement(queryGetCompanyCars)) {
            preparedStatement.setInt(1, companyId);
            try (ResultSet cars = preparedStatement.executeQuery()) {
                while (cars.next()) {
                    int id = cars.getInt("id");
                    String name = cars.getString("name");
                    carList.add(new Car(id, name, companyId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return carList;
    }

    @Override
    public boolean addCar(Company company, String carName) {
        String insert = "INSERT INTO car (name, company_id) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connectionH2.prepareStatement(insert)) {
            preparedStatement.setString(1, carName);
            preparedStatement.setInt(2, company.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
