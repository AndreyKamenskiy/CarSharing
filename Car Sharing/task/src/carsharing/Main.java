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
    private static CustomerDao customerDao;

    public static void main(String[] args) {
        try (Connection conn = getConnection(DB_URL + getDBName(args))) {
            createCompanyTable(conn);
            createCarTable(conn);
            createCustomerTable(conn);
            companyDao = new CompanyDaoH2(conn);
            carDao = new CarDaoH2(conn);
            customerDao = new CustomerDaoH2(conn);

            new Menu().addItem(new Menu.MenuItem("Log in as a manager", loginAsManager))
                    .addItem(new Menu.MenuItem("Log in as a customer", loginAsCustomer))
                    .addItem(new Menu.MenuItem("Create a customer", createNewCustomer))
                    .callback();

            //runMainMenu();
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
                    ");\n";
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

    private static void createCustomerTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS customer (\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    name VARCHAR UNIQUE NOT NULL,\n" +
                    "    rented_car_id INT ,\n" +
                    "    FOREIGN KEY (rented_car_id) REFERENCES car(id)" +
                    ")\n";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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

    private static void showCompanyCars(List<Car> cars) {
        if (cars.size() > 0) {
            StringBuilder carsToPrint = new StringBuilder();
            carsToPrint.append("Car list:\n");
            int i = 1;
            for (Car c : cars) {
                carsToPrint.append(String.format("%d. %s%n", i++, c.getName()));
            }
            System.out.print(carsToPrint);
        } else {
            System.out.println("The car list is empty!");
        }
        System.out.println();
    }

    private static void addNewCar(Company company) {
        if (carDao.addCar(company, getName("Enter the car name:"))) {
            System.out.println("The car was added!\n");
        } else {
            System.out.println("Something went wrong!\n");
        }
    }

    private static class CompanyMenu extends Menu {
        CompanyMenu(Company company) {
            super();
            setTitle(String.format("'%s' company", company.getName()));
            setExitItem("Back");
            addItem(new MenuItem("Car list", () -> showCompanyCars(carDao.getCompanyCars(company))));
            addItem(new MenuItem("Create a car", () -> addNewCar(company)));
        }
    }

    private static final Callbackable companyListMenu = () -> {
        List<Company> companies = companyDao.getAllCompanies();
        if (companies.size() > 0) {
            Choice<Company> choice = new Choice<>();
            new SelectMenu<>(companies, choice)
                    .setTitle("Choose a company:")
                    .setExitItem("Back")
                    .callback();
            if (choice.hasChoice()) {
                new CompanyMenu(choice.getChoice()).callback();
            }
        } else {
            System.out.println("The company list is empty!\n");
        }
    };

    private static final Callbackable createNewCompany = () -> {
        if (companyDao.addCompany(getName("Enter the company name:"))) {
            System.out.println("The company was created!\n");
        } else {
            System.out.println("Something went wrong!\n");
        }
    };
    private static final Callbackable loginAsManager = () -> new Menu()
            .addItem(new Menu.MenuItem("Company list", companyListMenu))
            .addItem(new Menu.MenuItem("Create a company", createNewCompany))
            .setExitItem("Back")
            .callback();

    private static class Choice<T> {
        private T choice = null;

        public void setChoice(T choice) {
            this.choice = choice;
        }

        public T getChoice() {
            return choice;
        }

        public boolean hasChoice() {
            return choice != null;
        }
    }

    private static class SelectMenu<T> extends Menu {
        SelectMenu(List<T> items, Choice<T> choice) {
            super();
            singleRun();
            for (T currentItem : items) {
                addItem(new MenuItem(currentItem.toString(), () -> choice.setChoice(currentItem)));
            }
        }
    }

    private static class CustomerMenu extends Menu {
        private Customer customer;

        private final Callbackable rentACar = () -> {
            if (customerDao.isRented(customer)) {
                System.out.print("You've already rented a car!\n\n");
                return;
            }
            List<Company> companies = companyDao.getAllCompanies();
            if (companies.size() > 0) {
                Choice<Company> companyChoice = new Choice<>();
                new SelectMenu<>(companies, companyChoice)
                        .setTitle("Choose a company:")
                        .setExitItem("Back")
                        .callback();
                if (companyChoice.hasChoice()) {
                    Company company = companyChoice.getChoice();
                    List<Car> availableCar = companyDao.getAvailableCars(company);
                    if (availableCar.size() == 0) {
                        System.out.printf("No available cars in the '%s' company%n%n", company.getName());
                        return;
                    }
                    Choice<Car> carChoice = new Choice<>();
                    new SelectMenu<>(availableCar, carChoice)
                            .setTitle("Choose a car:")
                            .setExitItem("Back")
                            .callback();
                    if (carChoice.hasChoice()) {
                        customerDao.rentACar(customer, carChoice.getChoice());
                        // customer.setRentedCarId(carChoice.getChoice().getId());
                        System.out.printf("You rented '%s'%n%n", carChoice.getChoice().getName());
                    }
                }
            } else {
                System.out.println("The company list is empty!\n");
            }
        };

        private final Callbackable returnCar = () -> {
            if (!customerDao.isRented(customer)) {
                System.out.print("You didn't rent a car!\n\n");
                return;
            }
            customerDao.returnCar(customer);
            System.out.println("You've returned a rented car!\n");
        };

        private final Callbackable showRentedCar = () -> {
            if (!customerDao.isRented(customer)) {
                System.out.print("You didn't rent a car!\n\n");
                return;
            }
            Car car = carDao.getCarById(customerDao.getRentedCar(customer));
            String carName = car.getName();
            String companyName = companyDao.getCompanyById(car.getCompanyId()).getName();
            System.out.printf("Your rented car:%n%s%nCompany:%n%s%n%n", carName, companyName);
        };

        CustomerMenu(Customer customer) {
            super();
            this.customer = customer;
            setExitItem("Back");
            addItem(new MenuItem("Rent a car", rentACar));
            addItem(new MenuItem("Return a rented car", returnCar));
            addItem(new MenuItem("My rented car", showRentedCar));
        }

    }

    private static final Callbackable loginAsCustomer = () -> {
        List<Customer> allCustomers = customerDao.getAllCustomers();
        if (allCustomers.size() > 0) {
            Menu menu = new Menu().setTitle("Customer list:").setExitItem("Back");
            // todo: change to Select menu
            for (Customer currentCustomer : allCustomers) {
                menu.addItem(new Menu.MenuItem(currentCustomer.getName(), new CustomerMenu(currentCustomer)));
            }
            menu.singleRun().callback();
        } else {
            System.out.println("The customer list is empty!");
        }
    };

    private static final Callbackable createNewCustomer = () -> {
        if (customerDao.addCustomer(getName("Enter the customer name:"))) {
            System.out.println("The customer was added!\n");
        } else {
            System.out.println("Something went wrong!\n");
        }
    };

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
        //STEP 2: Open a connection
        Connection conn = DriverManager.getConnection(url);
        conn.setAutoCommit(true);
        return conn;
    }

}
