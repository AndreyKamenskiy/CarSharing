package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:./src/carsharing/db/";

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

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);
            //STEP 2: Open a connection
            conn = DriverManager.getConnection(DB_URL + getDBName(args));
            conn.setAutoCommit(true);
            //STEP 3: Execute a query
            stmt = conn.createStatement();
            String sql = "CREATE TABLE COMPANY (\n" +
                         "    ID INT,\n" +
                         "    NAME VARCHAR\n" +
                         ")\n";
            stmt.executeUpdate(sql);
            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
