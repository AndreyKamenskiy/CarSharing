package carsharing;

import java.sql.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

//todo: add logging

public class Main {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:./src/carsharing/db/";

    static final String[] mainMenu = {
            "1. Log in as a manager",
            "0. Exit",
    };

    static final String[] companyMenu = {
            "1. Company list",
            "2. Create a company",
            "0. Back",
    };

    public static void main(String[] args) {
        try (Connection conn = getConnection(DB_URL + getDBName(args))) {
            createCompanyTable(conn);
            CompanyDao companyDao = new CompanyDaoH2(conn);
            runMainMenu(companyDao);
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
            String sql = "CREATE TABLE IF NOT EXISTS COMPANY (\n" +
                         "    ID INT PRIMARY KEY AUTO_INCREMENT,\n" +
                         "    NAME VARCHAR UNIQUE NOT NULL\n" +
                         ")\n";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static int getChoice(final String[] menu) {
        int choice;
        Scanner in = new Scanner(System.in);
        do {
            Stream.of(menu).forEach(System.out::println);
            try {
                choice = in.nextInt();
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
                choice = -1;
            }
        } while (choice < 0 || choice >= menu.length);
        return choice;
    }

    private static void runMainMenu(CompanyDao companyDao) {
        while (getChoice(mainMenu) == 1) {
            int choice;
            do {
                choice = getChoice(companyMenu);
                if (choice == 1) {
                    showAllCompany(companyDao.getAllCompanies());
                    continue;
                }
                if (choice == 2) {
                    if (companyDao.addCompany(getCompanyName())) {
                        System.out.println("The company was created!\n");
                    } else {
                        System.out.println("Something went wrong!\n");
                    }
                }
            } while (choice != 0);
        }
    }

    private static String getCompanyName() {
        Scanner in = new Scanner(System.in);
        String name;
        do {
            System.out.println("Enter the company name:");
            try {
                name = in.nextLine();
                System.out.println();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                name = null;
            }
        } while (name == null);
        return name;
    }

    private static void showAllCompany(List<Company> companies) {
        if (companies.size() > 0) {
            System.out.println("Company list:");
            int i = 1;
            for (Company c : companies) {
                System.out.printf("%d. %s%n", i++, c.getName());
            }
        } else {
            System.out.println("The company list is empty!");
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
