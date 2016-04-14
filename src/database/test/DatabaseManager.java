package database.test;

import static database.test.DatabaseUtilities.nullable;
import static database.test.DatabaseUtilities.toChar;
import static database.test.DatabaseUtilities.toLocalDate;

import database.test.data.Customer;
import database.test.data.Product;
import database.test.data.ShoppingList;
import database.test.data.Supplier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
            System.err.println("Error disconnecting from the database :\n\t" + ex);
            System.err.flush();
        }
        return !connected;
    }

    public boolean isConnected() {
        return connected;
    }

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
    //</editor-fold>

    //<editor-fold desc="Database Management: Customers">
    public List<String> queryListOfCustomerIDs() {
        try {
            return DatabaseUtilities.querySingleColumnToList(statement, SQLStrings.SQL_CUSTOMER_ID_ALL);
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>(); // return an empty list
        }
    }

    public List<Customer> queryAllCustomers() {
        List<Customer> list = new LinkedList<>();
        try {
            ResultSet result = statement.executeQuery(SQLStrings.SQL_SELECT_ALL_CUSTOMERS);

            while (result.next()) {
                list.add(resultSetRowToCustomer(result));
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public Customer queryCustomer(String searchID) {
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SELECT_A_CUSTOMER);
            // set the parameters
            p.setString(1, searchID);
            // execute the statement
            ResultSet result = p.executeQuery();

            if (result.next()) {
                return resultSetRowToCustomer(result);
            }
            return null;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    private static Customer resultSetRowToCustomer(ResultSet result)
            throws SQLException {
        String id = result.getString("customer_id");
        String firstName = result.getString("first_name");
        String lastName = result.getString("last_name");
        char gender = toChar(result.getString("gender"));
        LocalDate birthday = toLocalDate(result.getDate("date_of_birth"));
        LocalDate regdate = toLocalDate(result.getDate("date_of_registration"));
        String phone = result.getString("customer_phone");
        String email = result.getString("customer_email");

        return new Customer(id, firstName, lastName, gender,
                birthday, regdate, phone, email);
    }

    public int insertCustomer(Customer c)
            throws SQLException {
        // prepare a statement
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_INSERT_CUSTOMER);
        // set the parameters
        p.setString(1, c.getID());
        p.setString(2, nullable(c.getFirstName())); // will fail if null
        p.setString(3, nullable(c.getLastName()));
        p.setString(4, nullable(c.getGender()));
        p.setDate(5, nullable(c.getBirthDay()));
        p.setDate(6, nullable(c.getRegisteredDate())); // actually won't be null
        p.setString(7, nullable(c.getPhoneNumber()));
        p.setString(8, nullable(c.getEmailAddress()));
        // execute the statement
        return p.executeUpdate();
    }

    public int updateCustomer(Customer c)
            throws SQLException {
        // prepare a statement
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_UPDATE_CUSTOMER);
        // set the parameters
        p.setString(1, c.getID());
        p.setString(2, nullable(c.getFirstName())); // will fail if null
        p.setString(3, nullable(c.getLastName()));
        p.setString(4, nullable(c.getGender()));
        p.setDate(5, nullable(c.getBirthDay()));
        p.setDate(6, nullable(c.getRegisteredDate())); // actually won't be null
        p.setString(7, nullable(c.getPhoneNumber()));
        p.setString(8, nullable(c.getEmailAddress()));
        p.setString(9, c.getID());
        // execute the statement
        return p.executeUpdate();
    }

    public void deleteCustomer(Customer c)
            throws SQLException {
        // TODO: 1. find all sale with customer_id = c.id, update them to CDELETED
        // TODO: 2. delete the customer
        throw new SQLException("not supported yet");
    }

    public String suggestNextCustomerID() {
        return DatabaseUtilities.suggestNextID(statement,
                SQLStrings.SQL_CUSTOMER_ID_LATEST, "C", 8);
    }
    //</editor-fold>

    //<editor-fold desc="Database Management: Products / Categories / Pricing">
    public List<String> queryListOfSellingProductIDs() {
        try {
            return DatabaseUtilities.querySingleColumnToList(statement, SQLStrings.SQL_PRODUCT_ID_ALL_AVAILABLE);
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>(); // return an empty list
        }
    }

    public Product queryProduct(String searchID) {
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SELECT_A_PRODUCT);
            // set the parameters
            p.setString(1, searchID);
            // execute the statement
            ResultSet result = p.executeQuery();

            if (result.next()) {
                String id = result.getString("product_id");
                String name = result.getString("product_name");
                String description = result.getString("product_description");
                int stockQuantity = result.getInt("stock_quantity");
                boolean selling = result.getBoolean("selling_status");
                String categoryID = result.getString("category_id");

                return new Product(id, name, description, stockQuantity, selling, categoryID);
            }
            return null;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public boolean insertProduct(Product p) {
        return false;
    }

    public boolean updateProduct(Product p) {
        return false;
    }

    public String suggestNextProductID() {
        return DatabaseUtilities.suggestNextID(statement,
                SQLStrings.SQL_PRODUCT_ID_LATEST, "P", 8);
    }

    public TableModel queryCategoryOverview() {
        try {
            ResultSet result = statement.executeQuery(SQLStrings.SQL_CATEGORY_OVERVIEW);
            return DatabaseUtilities.buildTableModel(result);
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public int insertCategory(String categoryID, String categoryName)
            throws SQLException {
        // prepare a statement
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_INSERT_CATEGORY);
        // set the parameters
        p.setString(1, categoryID);
        p.setString(2, categoryName);
        // execute the statement
        return p.executeUpdate();
    }

    public int updateCategory(String oldCategoryID, String categoryID, String categoryName)
            throws SQLException {
        // prepare a statement
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_UPDATE_CATEGORY);
        // set the parameters
        p.setString(1, categoryID);
        p.setString(2, categoryName);
        p.setString(3, oldCategoryID);
        // execute the statement
        return p.executeUpdate();
    }

    public int deleteCategory(String categoryID)
            throws SQLException {
        // prepare a statement
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_DELETE_CATEGORY);
        // set the parameters
        p.setString(1, categoryID);
        // execute the statement
        return p.executeUpdate();
    }
    //</editor-fold>

    //<editor-fold desc="Database Management: Suppliers">
    public Supplier querySupplier(String searchID) {
        return null;
    }

    public boolean insertSupplier(Supplier s) {
        return false;
    }

    public boolean updateSupplier(Supplier s) {
        return false;
    }

    public String suggestNextSupplierID() {
        return DatabaseUtilities.suggestNextID(statement,
                SQLStrings.SQL_SUPPLIER_ID_LATEST, "S", 8);
    }
    //</editor-fold>

    //<editor-fold desc="Database Management: Sale Records">
    public boolean insertSaleRecord(ShoppingList shoppingList) {
        return false;
    }
    //</editor-fold>

}
