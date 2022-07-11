package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDaoH2 implements CompanyDao {

    private final Connection connectionH2;

    public CompanyDaoH2(Connection connectionH2) {
        this.connectionH2 = connectionH2;
    }

    @Override
    public List<Company> getAllCompanies() {
        final String queryGetAllCompany = "SELECT *\n" +
                                          "FROM COMPANY\n" +
                                          "ORDER BY ID ASC;\n";
        List<Company> companies = new ArrayList<>();
        try (Statement stmt = connectionH2.createStatement()) {
            try (ResultSet company = stmt.executeQuery(queryGetAllCompany)) {
                while (company.next()) {
                    int id = company.getInt("ID");
                    String name = company.getString("NAME");
                    companies.add(new Company(id, name));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return companies;
    }

    @Override
    public boolean addCompany(String name) {
        String insert = "INSERT INTO COMPANY (NAME) VALUES (?)";
        try (PreparedStatement preparedStatement = connectionH2.prepareStatement(insert)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}