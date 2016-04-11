package database.test;

import database.test.data.Customer;
import database.test.gui.GenericTableWindow;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DatabaseManager {

    private Connection connection = null;
    private Statement statement = null;

    private final String host;
    private final String port;
    private final String database;
    private String username;
    private String password;

    private boolean connected = false;

    public DatabaseManager(String host, String port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    //<editor-fold desc="Database Connection: Username/Password, Connect, Disconnect">
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(String.format(
                    "jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=false",
                    host, port, database, username, password));
            statement = connection.createStatement();
            connected = true;
        } catch (ClassNotFoundException ex) {
            System.err.println(ex);
            System.err.flush();
        } catch (SQLException ex) {
            System.err.println("Error connecting to the database :\n\t" + ex);
            System.err.flush();
        }
        return connected;
    }

    public boolean disconnect() {
        try {
            connection.close();
            connected = false;
        } catch (SQLException ex) {
            System.err.println("Error disconnecting the database :\n\t" + ex);
            System.err.flush();
        }
        return !connected;
    }

    public boolean isConnected() {
        return connected;
    }
    //</editor-fold>

    /**
     * Get the current user name from the database system.
     *
     * @return A result from 'SELECT CURRENT_USER()' command.
     */
    public String queryCurrentUser() {
        try {
            ResultSet result = statement.executeQuery(SQLStrings.SQL_CURRENT_USER);
            result.next();
            return result.getString(1);
        } catch (SQLException ex) {
            System.err.println(ex);
            return null;
        }
    }

    //<editor-fold desc="Database Management: Customers">
    public List<String> queryListOfCustomerIDs() {
        try {
            return querySingleColumnToList(statement, SQLStrings.SQL_CUSTOMER_ID_ALL);
        } catch (SQLException ex) {
            System.err.println(ex);
            return null;
        }
    }

    public Customer queryCustomer(String searchID) {
        if (!connected) {
            System.err.println("Cannot query when the database is not connected.");
            System.err.flush();
            return null;
        }
        
        try {
            ResultSet result = statement.executeQuery("SELECT * FROM CUSTOMER WHERE customer_id='" + searchID + "'");
            if (result.next()) {
                String id = result.getString("customer_id");
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                String genderStr = result.getString("gender");
                char gender = (genderStr == null) ? '\0' : genderStr.charAt(0);

                Date birthday = result.getDate("date_of_birth");
                Date regdate = result.getDate("date_of_registration");
                String phone = result.getString("customer_phone");
                String email = result.getString("customer_email");

                return new Customer(id, firstName, lastName, gender, birthday, regdate, phone, email);
            }
            return null;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public boolean insertCustomer(Customer c) {
        if (!connected) {
            System.err.println("Cannot insert when the database is not connected.");
            System.err.flush();
            return false;
        }

        if (!c.isValid()) {
            System.err.println("Cannot insert customer without required fields.");
            System.err.flush();
            return false;
        }

        try {
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_INSERT_CUSTOMER);
            // TODO: test insert customer

            p.setString(1, c.getId());
            p.setString(2, c.getFirstName());
            p.setString(3, c.getLastName());
            if (c.getGender() == '\0') {
                p.setNull(4, java.sql.Types.CHAR);
            } else {
                p.setString(4, String.valueOf(c.getGender()));
            }
            java.util.Date birthDay = c.getBirthDay();
            if (birthDay == null) {
                p.setNull(5, java.sql.Types.DATE);
            } else {
                p.setDate(5, new java.sql.Date(c.getBirthDay().getTime()));
            }
            p.setDate(6, new java.sql.Date(c.getRegisteredDate().getTime()));
            p.setString(7, c.getPhoneNumber());
            p.setString(8, c.getEmailAddress());

            int rowCount = p.executeUpdate();
            System.out.println("Insert new customer successful. Returned " + rowCount);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return false;
        }
    }

    public boolean updateCustomer(Customer c) {
        if (!connected) {
            System.err.println("Cannot update when the database is not connected.");
            System.err.flush();
            return false;
        }

        if (!c.isValid()) {
            System.err.println("Cannot update customer without required fields.");
            System.err.flush();
            return false;
        }

        try {
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_UPDATE_CUSTOMER);
            // TODO: test update customer

            p.setString(1, c.getFirstName());
            p.setString(2, c.getLastName());
            if (c.getGender() == '\0') {
                p.setNull(3, java.sql.Types.CHAR);
            } else {
                p.setString(3, String.valueOf(c.getGender()));
            }
            java.util.Date birthDay = c.getBirthDay();
            if (birthDay == null) {
                p.setNull(4, java.sql.Types.DATE);
            } else {
                p.setDate(4, new java.sql.Date(c.getBirthDay().getTime()));
            }
            p.setDate(5, new java.sql.Date(c.getRegisteredDate().getTime()));
            p.setString(6, c.getPhoneNumber());
            p.setString(7, c.getEmailAddress());

            p.setString(8, c.getId());
            

            int rowCount = p.executeUpdate();
            System.out.println("Update customer (id = " + c.getId() + ") successful. Returned " + rowCount);
            return true;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return false;
        }
    }

    public String getNextCustomerID() {
        try {
            ResultSet result = statement.executeQuery(SQLStrings.SQL_CUSTOMER_ID_LATEST);
            if (result.next()) {
                String latestID = result.getString("customer_id");
                // try getting the number part of the ID, add one, and create a new ID
                // assuming the ID is in 'C0000000' format
                int numNextID = Integer.parseInt(latestID.substring(1)) + 1;
                return String.format("C%07d", numNextID);
            } else {
                return null;
            }
        } catch (SQLException | NumberFormatException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Database Management: Products / Categories / Pricing">
    public boolean insertProduct() {
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Database Management: Suppliers">
    public boolean insertSupplier() {
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Database Management: Sale Records">
    public boolean insertSaleRecord() {
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Static Methods / Utilities">
    /**
     * Query the given database and return the first column of the result as a
     * list of strings.
     *
     * @param database The database to be queried.
     * @param sql The SQL query (SELECT) command.
     * @return First column of the query result.
     * @throws SQLException
     */
    public static List<String> querySingleColumnToList(DatabaseManager database, String sql)
            throws SQLException {
        return querySingleColumnToList(database.statement, sql);
    }

    /**
     * Query using the given Statement and return the first column of the result
     * as a list of strings.
     *
     * @param statement The Statement object connected to the database.
     * @param sql The SQL query (SELECT) command.
     * @return First column of the query result.
     * @throws SQLException
     */
    public static List<String> querySingleColumnToList(Statement statement, String sql)
            throws SQLException {
        List<String> list = new ArrayList<>();
        ResultSet result = statement.executeQuery(sql);
        while (result.next()) {
            list.add(result.getString(1));
        }
        return list;
    }

    /**
     * Query the given database and display the result in a GUI table window.
     *
     * @param database The database to be queried.
     * @param sql The SQL query (SELECT) command.
     * @throws SQLException
     */
    public static void queryToTable(DatabaseManager database, String sql)
            throws SQLException {
        queryToTable(database.statement, sql);
    }

    /**
     * Query using the given Statement and display the result in a GUI table
     * window.
     *
     * @param statement The Statement object connected to the database.
     * @param sql The SQL query (SELECT) command.
     * @throws SQLException
     */
    public static void queryToTable(Statement statement, String sql)
            throws SQLException {
//        System.out.println("Begin query: " + sql);
        ResultSet result = statement.executeQuery(sql);
        GenericTableWindow gui
                = new GenericTableWindow(buildTableModel(result));
        gui.setTitle(sql);
        gui.setVisible(true);
    }

    /**
     * Create a TableModel from a database query result.
     *
     * @param resultSet Query result from a database.
     * @return a TableModel for displaying the data in a JTable.
     * @throws SQLException
     */
    public static TableModel buildTableModel(ResultSet resultSet)
            throws SQLException {
        DefaultTableModel model = new DefaultTableModel();

        ResultSetMetaData meta = resultSet.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 0; i < columnCount; i++) {
            model.addColumn(meta.getColumnLabel(i + 1));
        }

        while (resultSet.next()) {
            Object[] rowData = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                rowData[i] = resultSet.getObject(i + 1);
            }
            model.addRow(rowData);
        }

        return model;
    }

    //</editor-fold>
    
    public interface DatabaseChangesListener {
        
    }
}
