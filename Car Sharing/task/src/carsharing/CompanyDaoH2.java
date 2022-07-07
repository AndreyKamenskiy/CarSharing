package carsharing;

import java.util.List;

public class CompanyDaoH2 implements CompanyDao{

    @Override
    public List<Company> getAllCompanies() {
        return List.of(Company.emptyCompany);
    }

    @Override
    public Company getCompany(int id) {
        return Company.emptyCompany;
    }

    @Override
    public Company getCompany(String name) {
        return Company.emptyCompany;
    }

    @Override
    public boolean addCompany(Company company) {
        return false;
    }
}
