package database.test;

import static database.test.DatabaseUtilities.nullable;
import static database.test.DatabaseUtilities.toChar;
import static database.test.DatabaseUtilities.toLocalDate;
import static database.test.DatabaseUtilities.toLocalTime;

import database.test.data.Customer;
import database.test.data.Product;
import database.test.data.ShoppingList;
import database.test.data.Supplier;

import java.awt.Component;
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

import javax.swing.JOptionPane;
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

    public List<Customer> queryCustomersByName(String searchString) {
        List<Customer> list = new LinkedList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SEARCH_CUSTOMER_BY_NAME);
            // set the parameters
            p.setString(1, '%' + searchString + '%');
            // execute the statement
            ResultSet result = p.executeQuery();

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

    /**
     *
     * @param customerID
     * @param dateFrom
     * @param dateTo
     * @return [sale_date:date, sale_time:time, item_count:int, discount:double,
     * total:double, sale_id:string]
     */
    public List<Object[]> queryCustomerShoppingHistory(String customerID, LocalDate dateFrom, LocalDate dateTo) {
        List<Object[]> list = new ArrayList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_QUERY_CUSTOMER_SHOPPING_HISTORY);
            // set the parameters
            p.setString(1, customerID);
            p.setDate(2, nullable(dateFrom));
            p.setDate(3, nullable(dateTo));
            // execute the statement
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Object[] row = new Object[6];
                row[0] = toLocalDate(result.getDate("sale_date"));
                row[1] = toLocalTime(result.getTime("sale_time"));
                row[2] = result.getInt("item_count");
                row[3] = result.getDouble("special_discount");
                row[4] = result.getDouble("discounted_total");
                row[5] = result.getString("sale_id");
                list.add(row);
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param saleID
     * @return [product_id, product_name, quantity, unit_price, subtotal]
     */
    public List<Object[]> querySingleSaleDetail(String saleID) {
        List<Object[]> list = new ArrayList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_QUERY_SINGLE_SALE_DETAIL);
            // set the parameters
            p.setString(1, saleID);
            // execute the statement
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Object[] row = new Object[5];
                row[0] = result.getString("product_id");
                row[1] = result.getString("product_name");
                row[2] = result.getInt("quantity");
                row[3] = result.getDouble("unit_price");
                row[4] = result.getDouble("subtotal");
                list.add(row);
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>();
        }
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

    public void deleteCustomer(String customerID)
            throws SQLException {
        // TODO: 1. find all sale with customer_id = c.id, update them to CDELETED
        // TODO: 2. delete the customer
        connection.setAutoCommit(false);
        try {
            PreparedStatement p1 = connection.prepareStatement(SQLStrings.SQL_DELETE_CUSTOMER_UPDATE_SALE);
            p1.setString(1, customerID);
            p1.executeUpdate();

            PreparedStatement p2 = connection.prepareStatement(SQLStrings.SQL_DELETE_CUSTOMER);
            p2.setString(1, customerID);
            p2.executeUpdate();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
        connection.setAutoCommit(true);
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

    public List<String> queryListOfAllProductIDs() {
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
                double price = result.getDouble("product_price");

                return new Product(id, name, description, stockQuantity, selling, categoryID, price);
            }
            return null;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    /**
     *
     * @param productID
     * @return [start_date:date, end_date:date, duration:int, price:double]
     */
    public List<Object[]> queryProductPricingHistory(String productID) {
        List<Object[]> list = new ArrayList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_PRODUCT_PRICING_HISTORY);
            // set the parameters
            p.setString(1, productID);
            // execute the statement
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Object[] row = new Object[4];
                row[0] = toLocalDate(result.getDate("start_date"));
                row[1] = toLocalDate(result.getDate("end_date"));
                row[2] = result.getInt("duration");
                row[3] = result.getDouble("product_price");
                list.add(row);
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>();
        }
    }

    public List<String> queryListOfProductSupplierIDs(String productID) {
        List<String> suppliers = new ArrayList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_PRODUCT_SUPPLIER_ID_LIST);
            // set the parameters
            p.setString(1, productID);
            // execute the statement
            ResultSet result = p.executeQuery();
            while (result.next()) {
                suppliers.add(result.getString("supplier_id"));
            }
            return suppliers;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>();
        }
    }

    public List<String> queryListOfSupplierProductIDs(String supplierID) {
        List<String> products = new ArrayList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SUPPLIER_PRODUCT_ID_LIST);
            // set the parameters
            p.setString(1, supplierID);
            // execute the statement
            ResultSet result = p.executeQuery();
            while (result.next()) {
                products.add(result.getString("product_id"));
            }
            return products;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>();
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

    public List<String> queryListOfCategoryIDs() {
        try {
            return DatabaseUtilities.querySingleColumnToList(statement, SQLStrings.SQL_CATEGORY_ID_ALL);
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>(); // return an empty list
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
    public List<String> queryListOfSupplierIDs() {
        try {
            return DatabaseUtilities.querySingleColumnToList(statement, SQLStrings.SQL_SUPPLIER_ID_ALL);
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>(); // return an empty list
        }
    }

    public List<Supplier> queryAllSuppliers() {
        List<Supplier> list = new LinkedList<>();
        try {
            ResultSet result = statement.executeQuery(SQLStrings.SQL_SELECT_ALL_SUPPLIERS);

            while (result.next()) {
                list.add(resultSetRowToSupplier(result));
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public List<Supplier> querySuppliersByName(String searchString) {
        List<Supplier> list = new LinkedList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SEARCH_SUPPLIER_BY_NAME);
            // set the parameters
            p.setString(1, '%' + searchString + '%');
            // execute the statement
            ResultSet result = p.executeQuery();

            while (result.next()) {
                list.add(resultSetRowToSupplier(result));
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public Supplier querySupplier(String searchID) {
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SELECT_A_SUPPLIER);
            // set the parameters
            p.setString(1, searchID);
            // execute the statement
            ResultSet result = p.executeQuery();

            if (result.next()) {
                return resultSetRowToSupplier(result);
            }
            return null;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    private static Supplier resultSetRowToSupplier(ResultSet result)
            throws SQLException {
        String id = result.getString("supplier_id");
        String name = result.getString("supplier_name");
        String address = result.getString("supplier_address");
        String phone = result.getString("supplier_phone");
        String email = result.getString("supplier_email");
        String website = result.getString("supplier_website");
        String notes = result.getString("notes");

        return new Supplier(id, name, address, phone, email, website, notes);
    }

    public int insertSupplier(Supplier s)
            throws SQLException {
        // prepare a statement
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_INSERT_SUPPLIER);
        // set the parameters
        p.setString(1, s.getID());
        p.setString(2, nullable(s.getName())); // will fail if null
        p.setString(3, nullable(s.getAddress()));
        p.setString(4, nullable(s.getPhoneNumber()));
        p.setString(5, nullable(s.getEmailAddress()));
        p.setString(6, nullable(s.getWebsite()));
        p.setString(7, nullable(s.getNotes()));
        // execute the statement
        return p.executeUpdate();
    }

    public int updateSupplier(Supplier s)
            throws SQLException {
        // prepare a statement
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_UPDATE_SUPPLIER);
        // set the parameters
        p.setString(1, s.getID());
        p.setString(2, nullable(s.getName())); // will fail if null
        p.setString(3, nullable(s.getAddress()));
        p.setString(4, nullable(s.getPhoneNumber()));
        p.setString(5, nullable(s.getEmailAddress()));
        p.setString(6, nullable(s.getWebsite()));
        p.setString(7, nullable(s.getNotes()));
        p.setString(8, s.getID());
        // execute the statement
        return p.executeUpdate();
    }

    public int deleteSupplier(String supplierID)
            throws SQLException {
        // prepare a statement
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_DELETE_SUPPLIER);
        // set the parameters
        p.setString(1, supplierID);
        // execute the statement
        return p.executeUpdate();
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

    //<editor-fold desc="Database Operations with Message Dialogs">
    public boolean tryInsertCustomer(Customer customer, Component caller) {
        try {
            this.insertCustomer(customer); // actual insert operation
            JOptionPane.showMessageDialog(caller,
                    "The new customer was successfully added to the database:\n>> "
                    + customer.shortDescription(),
                    "Register Customer",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(caller,
                    "Error adding the customer to the database:\n>> " + ex.getMessage(),
                    "Register Customer",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean tryUpdateCustomer(Customer customer, Component caller) {
        try {
            this.updateCustomer(customer); // actual update operation
            JOptionPane.showMessageDialog(caller,
                    "The following customer's information was successfully updated:\n>> "
                    + customer.shortDescription(),
                    "Edit Customer Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(caller,
                    "Error updating the customer's information:\n>> " + ex.getMessage(),
                    "Edit Customer Info",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean tryDeleteCustomer(Customer customer, Component caller) {
        boolean result = false;
        int proceed = JOptionPane.showConfirmDialog(caller,
                "Delete the following customer's information from the database?\n>> "
                + customer.shortDescription(),
                "Delete Customer Info",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
        if (proceed == JOptionPane.OK_OPTION) {
            try {
                this.deleteCustomer(customer.getID()); // actual delete operation
                JOptionPane.showMessageDialog(caller,
                        "The customer's information was successfully deleted.",
                        "Delete Customer Info",
                        JOptionPane.INFORMATION_MESSAGE);
                result = true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(caller,
                        "Error deleting the customer's information:\n>> " + ex.getMessage(),
                        "Delete Customer Info",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    public boolean tryInsertCategory(String categoryID, String categoryName, Component caller) {
        try {
            this.insertCategory(categoryID, categoryName);
            JOptionPane.showMessageDialog(caller,
                    "The new category was successfully added to the database.",
                    "Manage Categories",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(caller,
                    "Error adding new category to the database:\n>>" + ex.getMessage(),
                    "Manage Categories",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean tryUpdateCategory(String categoryID_original, String categoryID, String categoryName, Component caller) {
        try {
            this.updateCategory(categoryID_original, categoryID, categoryName);
            JOptionPane.showMessageDialog(caller,
                    "The category information was successfully updated.",
                    "Manage Categories",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(caller,
                    "Error updating the category information:\n>>" + ex.getMessage(),
                    "Manage Categories",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean tryDeleteCategory(String categoryID, String categoryName, Component caller) {
        boolean result = false;
        int proceed = JOptionPane.showConfirmDialog(caller,
                "Delete the following category from the database?\n>> "
                + categoryID + " - " + categoryName
                + "\n\n(The products in the category will not be deleted.)",
                "Manage Categories",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null);
        if (proceed == JOptionPane.OK_OPTION) {
            try {
                this.deleteCategory(categoryID); // actual delete operation
                JOptionPane.showMessageDialog(caller,
                        "The category was successfully deleted.",
                        "Manage Categories",
                        JOptionPane.INFORMATION_MESSAGE);
                result = true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(caller,
                        "Error deleting the category:\n>>" + ex.getMessage(),
                        "Manage Categories",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    public boolean tryInsertProduct(Product product, Component caller) {
        throw new UnsupportedOperationException();
    }

    public boolean tryUpdateProduct(Product product, Component caller) {
        throw new UnsupportedOperationException();
    }

    public boolean tryDeleteProduct(Product product, Component caller) {
        throw new UnsupportedOperationException();
    }

    public boolean tryInsertSupplier(Supplier supplier, Component caller) {
        try {
            this.insertSupplier(supplier); // actual insert operation
            JOptionPane.showMessageDialog(caller,
                    "The new supplier was successfully added to the database:\n>> "
                    + supplier.shortDescription(),
                    "Add New Supplier",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(caller,
                    "Error adding the supplier to the database:\n>> " + ex.getMessage(),
                    "Add New Supplier",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean tryUpdateSupplier(Supplier supplier, Component caller) {
        try {
            this.updateSupplier(supplier); // actual update operation
            JOptionPane.showMessageDialog(caller,
                    "The following supplier's information was successfully updated:\n>> "
                    + supplier.shortDescription(),
                    "Edit Supplier Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(caller,
                    "Error updating the supplier's information:\n>> " + ex.getMessage(),
                    "Edit Supplier Info",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean tryDeleteSupplier(Supplier supplier, Component caller) {
        boolean result = false;
        int proceed = JOptionPane.showConfirmDialog(caller,
                "Delete the following supplier from the database?\n>> "
                + supplier.shortDescription(),
                "Manage Suppliers",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null);
        if (proceed == JOptionPane.OK_OPTION) {
            try {
                this.deleteSupplier(supplier.getID()); // actual delete operation
                JOptionPane.showMessageDialog(caller,
                        "The supplier information was successfully deleted.",
                        "Manage Suppliers",
                        JOptionPane.INFORMATION_MESSAGE);
                result = true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(caller,
                        "Error deleting the supplier information:\n>>" + ex.getMessage(),
                        "Manage Suppliers",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }
    //</editor-fold>
}
