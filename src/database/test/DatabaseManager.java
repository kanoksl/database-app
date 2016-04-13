package database.test;

import static database.test.DatabaseUtilities.toLocalDate;

import database.test.data.Customer;
import database.test.data.Product;
import database.test.data.ShoppingList;
import database.test.data.Supplier;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
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
            return DatabaseUtilities.querySingleColumnToList(statement,
                    SQLStrings.SQL_CUSTOMER_ID_ALL);
        } catch (SQLException ex) {
            System.err.println(ex);
            return new ArrayList<>(); // return an empty list
        }
    }

    public Customer queryCustomer(String searchID) {
        try {
            PreparedStatement p = connection.prepareStatement(
                    SQLStrings.SQL_SELECT_A_CUSTOMER);

            p.setString(1, searchID);
            ResultSet result = p.executeQuery();

            if (result.next()) {
                String id = result.getString("customer_id");
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                String genderStr = result.getString("gender");
                char gender = (genderStr == null) ? '\0' : genderStr.charAt(0);

                LocalDate birthday = toLocalDate(result.getDate("date_of_birth"));
                LocalDate regdate = toLocalDate(result.getDate("date_of_registration"));
                String phone = result.getString("customer_phone");
                String email = result.getString("customer_email");

                return new Customer(id, firstName, lastName, gender,
                        birthday, regdate, phone, email);
            }
            return null;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public int insertCustomer(Customer c)
            throws SQLException {
        PreparedStatement p = connection.prepareStatement(
                SQLStrings.SQL_INSERT_CUSTOMER);
        // TODO: test insert customer

        p.setString(1, c.getID());
        p.setString(2, c.getFirstName());
        p.setString(3, c.getLastName());
        if (c.getGender() == '\0') {
            p.setNull(4, Types.CHAR);
        } else {
            p.setString(4, String.valueOf(c.getGender()));
        }
        LocalDate birthDay = c.getBirthDay();
        if (birthDay == null) {
            p.setNull(5, Types.DATE);
        } else {
            p.setDate(5, Date.valueOf(birthDay));
        }
        p.setDate(6, Date.valueOf(c.getRegisteredDate()));
        p.setString(7, c.getPhoneNumber());
        p.setString(8, c.getEmailAddress());

        int rowCount = p.executeUpdate();
        System.out.println("Insert new customer successful. Returned " + rowCount);
        return rowCount;
    }

    public int updateCustomer(Customer c)
            throws SQLException {
        PreparedStatement p = connection.prepareStatement(
                SQLStrings.SQL_UPDATE_CUSTOMER);
        // TODO: test update customer

        p.setString(1, c.getFirstName());
        p.setString(2, c.getLastName());
        if (c.getGender() == '\0') {
            p.setNull(3, Types.CHAR);
        } else {
            p.setString(3, String.valueOf(c.getGender()));
        }
        LocalDate birthDay = c.getBirthDay();
        if (birthDay == null) {
            p.setNull(4, Types.DATE);
        } else {
            p.setDate(4, Date.valueOf(birthDay));
        }
        p.setDate(5, Date.valueOf(c.getRegisteredDate()));
        p.setString(6, c.getPhoneNumber());
        p.setString(7, c.getEmailAddress());

        p.setString(8, c.getID());

        int rowCount = p.executeUpdate();
        System.out.println("Update customer (id = " + c.getID() + ") successful. Returned " + rowCount);
        return rowCount;
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
    public Product queryProduct(String searchID) {
        return null;
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
            ResultSet result = statement.executeQuery(
                    SQLStrings.SQL_CATEGORY_OVERVIEW);
            return DatabaseUtilities.buildTableModel(result);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public int insertCategory(String categoryID, String categoryName)
            throws SQLException {
        PreparedStatement p = connection.prepareStatement(
                SQLStrings.SQL_INSERT_CATEGORY);
        p.setString(1, categoryID);
        p.setString(2, categoryName);

        int rowCount = p.executeUpdate();
        System.out.println("Insert category (id = " + categoryID + ") successful. Returned " + rowCount);
        return rowCount;
    }

    public int updateCategory(String oldCategoryID, String newCategoryID, String categoryName)
            throws SQLException {
        PreparedStatement p = connection.prepareStatement(
                SQLStrings.SQL_UPDATE_CATEGORY);
        p.setString(1, newCategoryID);
        p.setString(2, categoryName);
        p.setString(3, oldCategoryID);

        int rowCount = p.executeUpdate();
        System.out.println("Update category (id = " + oldCategoryID + " --> " + newCategoryID + ") successful. Returned " + rowCount);
        return rowCount;
    }
    
    public int deleteCategory(String categoryID)
            throws SQLException {
        PreparedStatement p = connection.prepareStatement(
                SQLStrings.SQL_DELETE_CATEGORY);
        p.setString(1, categoryID);

        int rowCount = p.executeUpdate();
        System.out.println("Delete category (id = " + categoryID + ") successful. Returned " + rowCount);
        return rowCount;
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
