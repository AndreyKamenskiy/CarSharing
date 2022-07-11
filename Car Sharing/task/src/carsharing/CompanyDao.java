package carsharing;

import java.util.List;

public interface CompanyDao {
    // more about DAO
    // https://www.tutorialspoint.com/design_pattern/data_access_object_pattern.htm

    List<Company> getAllCompanies();
    boolean addCompany(String name);

}
