package carsharing;

import java.util.List;

public interface CompanyDao {
    // more about DAO
    // https://www.tutorialspoint.com/design_pattern/data_access_object_pattern.htm

    List<Company> getAllCompanies();
    Company getCompany(int  id);
    Company getCompany(String name);
    boolean addCompany(Company company);

}
