package carsharing;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

//todo: add logging
//todo: make callback menu

public class Main {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:./src/carsharing/db/";

    private static CompanyDao companyDao;
    private static CarDao carDao;

    public static void main(String[] args) {
        try (Connection conn = getConnection(DB_URL + getDBName(args))) {
            createCompanyTable(conn);
            createCarTable(conn);
            companyDao = new CompanyDaoH2(conn);
            carDao = new CarDaoH2(conn);
            runMainMenu();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (ClassNotFoundException e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }

    private static void createCompanyTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS company (\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    name VARCHAR UNIQUE NOT NULL\n" +
                    ")\n";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void createCarTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS car (\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    name VARCHAR UNIQUE NOT NULL,\n" +
                    "    company_id INT NOT NULL,\n" +
                    "    FOREIGN KEY (company_id) REFERENCES company(id)" +
                    ")\n";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static int getChoice(String menu, int maxValue) {
        int choice;
        Scanner in = new Scanner(System.in);
        do {
            System.out.println(menu);
            try {
                choice = Integer.parseInt(in.nextLine());
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println();
                choice = -1;
            }
        } while (choice < 0 || choice > maxValue);
        return choice;
    }


    private static void runMainMenu() {
        final String mainMenu =
                "1. Log in as a manager\n" +
                        "0. Exit";
        final String companyMenu =
                "1. Company list\n" +
                        "2. Create a company\n" +
                        "0. Back";
        while (getChoice(mainMenu, 1) == 1) {
            int choice;
            do {
                choice = getChoice(companyMenu,2);
                if (choice == 1) {
                    runChooseCompanyMenu(companyDao.getAllCompanies());
                    continue;
                }
                if (choice == 2) {
                    if (companyDao.addCompany(getName("Enter the company name:"))) {
                        System.out.println("The company was created!\n");
                    } else {
                        System.out.println("Something went wrong!\n");
                    }
                }
            } while (choice != 0);
        }
    }

    private static String getName(String title) {
        Scanner in = new Scanner(System.in);
        String name;
        System.out.println(title);
        name = in.nextLine();
        System.out.println();
        return name;
    }

    private static void runChooseCompanyMenu(List<Company> companies) {

        if (companies.size() > 0) {
            StringBuilder menu = new StringBuilder();
            menu.append("Choose the company:\n");
            int i = 1;
            for (Company c : companies) {
                menu.append(String.format("%d. %s%n", i++, c.getName()));
            }
            menu.append("0. Back");
            int companyIndex = getChoice(menu.toString(), companies.size());
            if (companyIndex == 0) {
                return;
            }
            runCompanyMenu(companies.get(companyIndex - 1));
        } else {
            System.out.println("The company list is empty!");
            System.out.println();
        }
    }

    private static void runCompanyMenu(Company company) {
        final String menu = String.format(
                "'%s' company\n" +
                        "1. Car list\n" +
                        "2. Create a car\n" +
                        "0. Back", company.getName()
        );
        int choice;
        do {
            choice = getChoice(menu, 2);
            if (choice == 1) {
                showCompanyCars(carDao.getCompanyCars(company));
                continue;
            }
            if (choice == 2) {
                if (carDao.addCar(company, getName("Enter the car name:"))) {
                    System.out.println("The car was added!\n");
                } else {
                    System.out.println("Something went wrong!\n");
                }
            }
        } while (choice != 0);
    }

    private static void showCompanyCars(List<Car> cars) {
        if (cars.size() > 0) {
            StringBuilder carsToPrint = new StringBuilder();
            carsToPrint.append("Car list:\n");
            int i = 1;
            for (Car c : cars) {
                carsToPrint.append(String.format("%d. %s%n", i++, c.getName()));
            }
            System.out.println(carsToPrint);
        } else {
            System.out.println("The car list is empty!");
        }
        System.out.println();
    }

    private static String getDBName(String[] args) {
        final String keyRegEx = "\\s*-databaseFileName\\s*";
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i] != null && args[i].matches(keyRegEx)) {
                if (args[i + 1] != null && args[i].length() > 0) {
                    return args[i + 1];
                }
            }
        }
        return "default";
    }

    private static Connection getConnection(String url) throws SQLException, ClassNotFoundException {
        // STEP 1: Register JDBC driver
        Class.forName(JDBC_DRIVER);
        //DriverManager.drivers().forEach(d -> System.out.println(d.toString()));
        //STEP 2: Open a connection
        Connection conn = DriverManager.getConnection(url);
        conn.setAutoCommit(true);
        return conn;
    }

}
