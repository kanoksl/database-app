package database.test;

import static database.test.DatabaseUtility.nullable;
import static database.test.DatabaseUtility.toChar;
import static database.test.DatabaseUtility.toLocalDate;
import static database.test.DatabaseUtility.toLocalTime;

import database.test.data.Customer;
import database.test.data.Product;
import database.test.data.ShoppingList;
import database.test.data.ShoppingList.LineItem;
import database.test.data.Supplier;
import database.test.gui.Const;

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
import java.util.logging.Level;
import java.util.logging.Logger;

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
            return DatabaseUtility.querySingleColumnToList(statement, SQLStrings.SQL_SELECT_ALL_CUSTOMER_IDS);
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
        connection.setAutoCommit(false);
        try {
            PreparedStatement p1 = connection.prepareStatement(SQLStrings.SQL_DELETE_CUSTOMER_UPDATE_SALE);
            p1.setString(1, customerID);
            p1.executeUpdate();

            PreparedStatement p2 = connection.prepareStatement(SQLStrings.SQL_DELETE_CUSTOMER);
            p2.setString(1, customerID);
            p2.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw ex;
        }
        connection.setAutoCommit(true);
    }

    public String suggestNextCustomerID() {
        return DatabaseUtility.suggestNextID(statement,
                SQLStrings.SQL_SELECT_LATEST_CUSTOMER_ID, "C", 8);
    }
    //</editor-fold>

    //<editor-fold desc="Database Management: Products / Categories / Pricing">
    public List<String> queryListOfSellingProductIDs() {
        try {
            return DatabaseUtility.querySingleColumnToList(statement, SQLStrings.SQL_SELECT_ALL_AVAILABLE_PRODUCT_IDS);
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>(); // return an empty list
        }
    }

    public List<String> queryListOfAllProductIDs() {
        try {
            return DatabaseUtility.querySingleColumnToList(statement, SQLStrings.SQL_SELECT_ALL_PRODUCT_IDS);
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>(); // return an empty list
        }
    }

    public List<Product> queryAllProducts() {
        List<Product> list = new LinkedList<>();
        try {
            ResultSet result = statement.executeQuery(SQLStrings.SQL_SELECT_ALL_PRODUCTS);

            while (result.next()) {
                list.add(resultSetRowToProduct(result));
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public List<Product> queryProductsByName(String searchString) {
        List<Product> list = new LinkedList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SEARCH_PRODUCT_BY_NAME);
            // set the parameters
            p.setString(1, '%' + searchString + '%');
            // execute the statement
            ResultSet result = p.executeQuery();

            while (result.next()) {
                list.add(resultSetRowToProduct(result));
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public List<Product> queryProductsByFilter(String filterSQL) {
        List<Product> list = new LinkedList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SEARCH_PRODUCT_BY_FILTER_BASECASE + filterSQL);
            // execute the statement
            ResultSet result = p.executeQuery();

            while (result.next()) {
                list.add(resultSetRowToProduct(result));
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
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
                return resultSetRowToProduct(result);
            }
            return null;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    private static Product resultSetRowToProduct(ResultSet result)
            throws SQLException {
        String id = result.getString("product_id");
        String name = result.getString("product_name");
        String description = result.getString("product_description");
        int stockQuantity = result.getInt("stock_quantity");
        boolean selling = result.getBoolean("selling_status");
        String categoryID = result.getString("category_id");
        double price = result.getDouble("product_price");

        return new Product(id, name, description, stockQuantity, selling, categoryID, price);
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
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_QUERY_PRODUCT_PRICING_HISTORY);
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

    /**
     *
     * @param productID
     * @return [sale_id, date_time, customer_id, first_name, quantity,
     * unit_price, subtotal]
     */
    public List<Object[]> queryProductSellingHistory(String productID) {
        List<Object[]> list = new ArrayList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_QUERY_PRODUCT_SELLING_HISTORY);
            // set the parameters
            p.setString(1, productID);
            // execute the statement
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Object[] row = new Object[7];
                row[0] = result.getString("sale_id");
                row[1] = result.getString("date_time");
                row[2] = result.getString("customer_id");
                row[3] = result.getString("first_name");
                row[4] = result.getInt("quantity");
                row[5] = result.getDouble("unit_price");
                row[6] = result.getDouble("subtotal");
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
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SELECT_SUPPLIERS_OF_A_PRODUCT);
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
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SELECT_PRODUCTS_OF_A_SUPPLIER);
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

    public void insertProduct(Product p)
            throws SQLException {
        connection.setAutoCommit(false);
        try {
            PreparedStatement p1 = connection.prepareStatement(SQLStrings.SQL_INSERT_PRODUCT);
            p1.setString(1, p.getID());
            p1.setString(2, nullable(p.getName())); // will fail if null
            p1.setString(3, nullable(p.getDescription()));
            p1.setInt(4, p.getStockQuantity());
            p1.setBoolean(5, p.isSelling());
            p1.setString(6, nullable(p.getCategoryID()));
            p1.executeUpdate();

            PreparedStatement p2 = connection.prepareStatement(SQLStrings.SQL_INSERT_NEW_PRODUCT_PRICE);
            p2.setString(1, p.getID());
            p2.setDouble(2, p.getCurrentPrice());
            p2.executeUpdate();

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw ex;
        }
        connection.setAutoCommit(true);
    }

    public String updateProduct(Product p)
            throws SQLException {
        String message = null;
        connection.setAutoCommit(false);

        String productID = p.getID();
        double newPrice = p.getCurrentPrice();

        try {
            PreparedStatement p1 = connection.prepareStatement(SQLStrings.SQL_UPDATE_PRODUCT);
            p1.setString(1, productID);
            p1.setString(2, nullable(p.getName())); // will fail if null
            p1.setString(3, nullable(p.getDescription()));
            p1.setInt(4, p.getStockQuantity());
            p1.setBoolean(5, p.isSelling());
            p1.setString(6, nullable(p.getCategoryID()));
            p1.setString(7, productID);
            p1.executeUpdate();

            if (p.isPriceChanged()) {
                PreparedStatement p2 = connection.prepareStatement(
                        SQLStrings.SQL_QUERY_PRODUCT_SALE_RECORD_TODAY);
                p2.setString(1, productID);
                ResultSet r2 = p2.executeQuery();

                boolean isSoldToday = r2.next();
                r2.close();

                if (isSoldToday) {
                    PreparedStatement p3 = connection.prepareStatement(
                            SQLStrings.SQL_UPDATE_PRODUCT_PRICE_TOMORROW_ROW_PRICE);
                    p3.setDouble(1, newPrice);
                    p3.setString(2, productID);
                    if (p3.executeUpdate() == 0) {
                        PreparedStatement p4 = connection.prepareStatement(
                                SQLStrings.SQL_UPDATE_PRODUCT_PRICE_CURRENT_ROW_END_DATE);
                        p4.setDate(1, nullable(LocalDate.now()));
                        p4.setString(2, productID);
                        p4.executeUpdate();
                        PreparedStatement p5 = connection.prepareStatement(
                                SQLStrings.SQL_INSERT_PRODUCT_PRICE_ROW);
                        p5.setString(1, productID);
                        p5.setDate(2, nullable(LocalDate.now().plusDays(1)));
                        p5.setDouble(3, newPrice);
                        p5.executeUpdate();
                        message = "The product has been sold today. The new price "
                                + "will take effect tomorrow.";
                    } else {
                        PreparedStatement p6 = connection.prepareStatement(
                                SQLStrings.SQL_SELECT_PRODUCT_PRICE_ENDING_TODAY);
                        p6.setString(1, productID);
                        ResultSet r6 = p6.executeQuery();
                        r6.next();
                        double prevPrice = r6.getDouble("product_price");
                        if (prevPrice == newPrice) {
                            PreparedStatement p7 = connection.prepareStatement(
                                    SQLStrings.SQL_DELETE_PRODUCT_PRICE_TOMORROW_ROW);
                            p7.setString(1, productID);
                            p7.executeUpdate();

                            PreparedStatement p8 = connection.prepareStatement(
                                    SQLStrings.SQL_UPDATE_PRODUCT_PRICE_CURRENT_ROW_END_DATE);
                            p8.setDate(1, SQLStrings.SQL_MAXDATE);
                            p8.setString(2, productID);
                            p8.executeUpdate();

                            message = "The product's price change has been reverted.";
                        } else {
                            message = "The product's price (starting tomorrow) has been updated.";
                        }
                    }
                } else {
                    PreparedStatement p3 = connection.prepareStatement(
                            SQLStrings.SQL_UPDATE_PRODUCT_PRICE_TODAY_ROW_PRICE);
                    p3.setDouble(1, newPrice);
                    p3.setString(2, productID);
                    if (p3.executeUpdate() == 0) {
                        PreparedStatement p4 = connection.prepareStatement(
                                SQLStrings.SQL_UPDATE_PRODUCT_PRICE_CURRENT_ROW_END_DATE);
                        p4.setDate(1, nullable(LocalDate.now().minusDays(1)));
                        p4.setString(2, productID);
                        p4.executeUpdate();
                        PreparedStatement p5 = connection.prepareStatement(
                                SQLStrings.SQL_INSERT_PRODUCT_PRICE_ROW);
                        p5.setString(1, productID);
                        p5.setDate(2, nullable(LocalDate.now()));
                        p5.setDouble(3, newPrice);
                        p5.executeUpdate();
                        message = "The product's price has been updated.";
                    } else {
                        PreparedStatement p6 = connection.prepareStatement(
                                SQLStrings.SQL_SELECT_PRODUCT_PRICE_ENDING_YESTERDAY);
                        p6.setString(1, productID);
                        ResultSet r6 = p6.executeQuery();
                        r6.next();
                        double prevPrice = r6.getDouble("product_price");
                        if (prevPrice == newPrice) {
                            PreparedStatement p7 = connection.prepareStatement(
                                    SQLStrings.SQL_DELETE_PRODUCT_PRICE_TODAY_ROW);
                            p7.setString(1, productID);
                            p7.executeUpdate();

                            PreparedStatement p8 = connection.prepareStatement(
                                    SQLStrings.SQL_UPDATE_PRODUCT_PRICE_YESTERDAY_ROW_END_DATE);
                            p8.setDate(1, SQLStrings.SQL_MAXDATE);
                            p8.setString(2, productID);
                            p8.executeUpdate();

                            message = "The product's price change has been reverted.";
                        } else {
                            message = "The product's price has been updated (again).";
                        }
                    }
                }
            }

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw ex;
        }
        connection.setAutoCommit(true);
        return message;
    }

    public void deleteProduct(String productID)
            throws SQLException {
        PreparedStatement p1 = connection.prepareStatement(SQLStrings.SQL_QUERY_PRODUCT_SALE_RECORD);
        p1.setString(1, productID);
        ResultSet r1 = p1.executeQuery();
        boolean hasBeenSold = r1.next();
        if (hasBeenSold) {
            throw new SQLException("Cannot delete a product that appears in a sale record.");
        } else {
            PreparedStatement p2 = connection.prepareStatement(SQLStrings.SQL_DELETE_PRODUCT);
            p2.setString(1, productID);
            p2.executeUpdate();
        }
    }

    public void updateProductSupplierRelationship(Product p, List<Supplier> allSuppliers)
            throws SQLException {
        String productID = p.getID();
        connection.setAutoCommit(false);
        try {
            PreparedStatement p1 = connection.prepareStatement(SQLStrings.SQL_PRODUCTSUPPLIER_DELETE_BY_PRODUCT);
            p1.setString(1, productID);
            p1.executeUpdate();

            if (!allSuppliers.isEmpty()) {
                PreparedStatement p2 = connection.prepareStatement(SQLStrings.SQL_PRODUCTSUPPLIER_INSERT);
                for (Supplier s : allSuppliers) {
                    p2.setString(1, productID);
                    p2.setString(2, s.getID());
                    p2.addBatch();
                }
                p2.executeBatch();
            }

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw ex;
        }
        connection.setAutoCommit(true);
    }

    public void updateProductSupplierRelationship(Supplier s, List<Product> allProducts)
            throws SQLException {
        String supplierID = s.getID();
        connection.setAutoCommit(false);
        try {
            PreparedStatement p1 = connection.prepareStatement(SQLStrings.SQL_PRODUCTSUPPLIER_DELETE_BY_SUPPLIER);
            p1.setString(1, supplierID);
            p1.executeUpdate();

            if (!allProducts.isEmpty()) {
                PreparedStatement p2 = connection.prepareStatement(SQLStrings.SQL_PRODUCTSUPPLIER_INSERT);
                for (Product p : allProducts) {
                    p2.setString(1, p.getID());
                    p2.setString(2, supplierID);
                    p2.addBatch();
                }
                p2.executeBatch();
            }

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw ex;
        }
        connection.setAutoCommit(true);
    }

    public String suggestNextProductID() {
        return DatabaseUtility.suggestNextID(statement,
                SQLStrings.SQL_SELECT_LATEST_PRODUCT_ID, "P", 8);
    }

    public TableModel queryCategoryOverview() {
        try {
            ResultSet result = statement.executeQuery(SQLStrings.SQL_SELECT_CATEGORY_OVERVIEW);
            return DatabaseUtility.buildTableModel(result);
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }
    }

    public List<String> queryListOfCategoryIDs() {
        try {
            return DatabaseUtility.querySingleColumnToList(statement, SQLStrings.SQL_SELECT_ALL_CATEGORY_IDS);
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
            return DatabaseUtility.querySingleColumnToList(statement, SQLStrings.SQL_SELECT_ALL_SUPPLIER_IDS);
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
        return DatabaseUtility.suggestNextID(statement,
                SQLStrings.SQL_SELECT_LATEST_SUPPLIER_ID, "S", 8);
    }

    public List<String[]> queryIDsAndNames(String sql, String searchString) {
        List<String[]> list = new LinkedList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(sql);
            // set the parameters
            p.setString(1, '%' + searchString + '%');
            p.setString(2, '%' + searchString + '%');
            // execute the statement
            ResultSet result = p.executeQuery();

            while (result.next()) {
                list.add(new String[]{result.getString(1), result.getString(2)});
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new LinkedList<>();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Database Management: Sale Records">
    public void insertSaleRecord(ShoppingList list) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String saleID = this.suggestNextSaleID();
            PreparedStatement p1 = connection.prepareStatement(SQLStrings.SQL_INSERT_SALE);
            p1.setString(1, saleID);
            p1.setDate(2, nullable(list.getCheckoutDate()));
            p1.setTime(3, nullable(list.getCheckoutTime()));
            p1.setDouble(4, list.getDiscountPercent());
            Customer c = list.getCustomer();
            p1.setString(5, c == null ? Const.UNREGISTERED_CUSTOMER_ID : c.getID());
            p1.executeUpdate();

            PreparedStatement p2 = connection.prepareStatement(SQLStrings.SQL_INSERT_SALE_DETAIL);
            for (LineItem item : list.getList()) {
                p2.setString(1, saleID);
                p2.setString(2, item.getProductID());
                p2.setInt(3, item.getQuantity());
                p2.addBatch();
            }
            p2.executeBatch();

            list.setSaleID(saleID);

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw ex;
        }
        connection.setAutoCommit(true);
    }

    public void updateProductQuantities(ShoppingList list)
            throws SQLException {
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_UPDATE_PRODUCT_SUBTRACT_STOCK_QUANTITY);
        for (LineItem item : list.getList()) {
            p.setInt(1, item.getQuantity());
            p.setString(2, item.getProductID());
            p.addBatch();
        }
        p.executeBatch();
    }

    public List<Object[]> querySaleHistory(LocalDate dateFrom, LocalDate dateTo) {
        List<Object[]> list = new ArrayList<>();
        try {
            // prepare a statement
            PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_QUERY_SALE_HISTORY);
            // set the parameters
            p.setDate(1, nullable(dateFrom));
            p.setDate(2, nullable(dateTo));
            // execute the statement
            ResultSet result = p.executeQuery();
            while (result.next()) {
                Object[] row = new Object[7];
                row[0] = result.getString("sale_id");
                row[1] = toLocalDate(result.getDate("sale_date"));
                row[2] = toLocalTime(result.getTime("sale_time"));
                row[3] = result.getString("customer_id");
                row[4] = result.getInt("item_count");
                row[5] = result.getDouble("special_discount");
                row[6] = result.getDouble("discounted_total");
                list.add(row);
            }
            return list;
        } catch (SQLException ex) {
            System.err.println(ex);
            System.err.flush();
            return new ArrayList<>();
        }
    }

    public int deleteSaleRecord(String saleID)
            throws SQLException {
        // prepare a statement
        PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_DELETE_SALE);
        // set the parameters
        p.setString(1, saleID);
        // execute the statement
        return p.executeUpdate();
    }

    public String suggestNextSaleID() {
        return DatabaseUtility.suggestNextID(statement,
                SQLStrings.SQL_SELECT_LATEST_SALE_ID, "SL", 12);
    }
    //</editor-fold>

    public List<Object[]> query(String sql, int columnCount)
            throws SQLException {
        List<Object[]> list = new LinkedList<>();
        PreparedStatement p = connection.prepareStatement(sql);
        ResultSet r = p.executeQuery();
        while (r.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = r.getObject(i + 1);
            }
            list.add(row);
        }
        return list;
    }

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

    public boolean tryInsertProduct(Product product, List<Supplier> suppliers, Component caller) {
        try {
            this.insertProduct(product); // actual insert operation
            this.updateProductSupplierRelationship(product, suppliers);
            JOptionPane.showMessageDialog(caller,
                    "The new product was successfully added to the database:\n>> "
                    + product.shortDescription(),
                    "Add New Product",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(caller,
                    "Error adding the product to the database:\n>> " + ex.getMessage(),
                    "Add New Product",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean tryUpdateProduct(Product product, List<Supplier> suppliers, Component caller) {
        try {
            String message = this.updateProduct(product); // actual update operation
            if (suppliers != null) {
                this.updateProductSupplierRelationship(product, suppliers);
            }
            JOptionPane.showMessageDialog(caller,
                    "The following product's information was successfully updated:\n>> "
                    + product.shortDescription() + (message == null ? "" : "\n\n" + message),
                    "Edit Product Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(caller,
                    "Error updating the product's information:\n>> " + ex.getMessage(),
                    "Edit Product Info",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean tryDeleteProduct(Product product, Component caller) {
        boolean result = false;
        int proceed = JOptionPane.showConfirmDialog(caller,
                "Delete the following product from the database?\n>> "
                + product.shortDescription(),
                "Manage Products",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null);
        if (proceed == JOptionPane.OK_OPTION) {
            try {
                this.deleteProduct(product.getID()); // actual delete operation
                JOptionPane.showMessageDialog(caller,
                        "The product information was successfully deleted.",
                        "Manage Products",
                        JOptionPane.INFORMATION_MESSAGE);
                result = true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(caller,
                        "Error deleting the product information:\n>>" + ex.getMessage(),
                        "Manage Products",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    public boolean tryInsertSupplier(Supplier supplier, List<Product> products, Component caller) {
        try {
            this.insertSupplier(supplier); // actual insert operation
            this.updateProductSupplierRelationship(supplier, products);
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

    public boolean tryUpdateSupplier(Supplier supplier, List<Product> products, Component caller) {
        try {
            this.updateSupplier(supplier); // actual update operation
            if (products != null) {
                this.updateProductSupplierRelationship(supplier, products);
            }
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

    public boolean tryProcessSale(ShoppingList list, Component caller) {
        try {
            this.insertSaleRecord(list);
            this.updateProductQuantities(list);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(caller,
                    "Error recording sale information:\n>> " + ex.getMessage(),
                    "Checkout",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean tryDeleteSaleRecord(String saleID, Component caller) {
        boolean result = false;
        int proceed = JOptionPane.showConfirmDialog(caller,
                "Are you sure you want to delete the following sale record?\n>> "
                + saleID,
                "Sale Records",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null);
        if (proceed == JOptionPane.OK_OPTION) {
            try {
                this.deleteSaleRecord(saleID); // actual delete operation
                JOptionPane.showMessageDialog(caller,
                        "The sale record was successfully deleted.",
                        "Sale Records",
                        JOptionPane.INFORMATION_MESSAGE);
                result = true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(caller,
                        "Error deleting the sale record:\n>>" + ex.getMessage(),
                        "Sale Records",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }
    //</editor-fold>

    public String checkStock(ShoppingList list, int treshold) {
        List<Product> outOfStock = new ArrayList<>();
        List<Product> lowOnStock = new ArrayList<>();

        for (LineItem item : list.getList()) {
            try {
                PreparedStatement p = connection.prepareStatement(SQLStrings.SQL_SELECT_PRODUCT_NAME_AND_STOCK);
                p.setString(1, item.getProductID());
                ResultSet r = p.executeQuery();
                r.next();
                int stock = r.getInt("stock_quantity");
                if (stock <= 0) {
                    outOfStock.add(this.queryProduct(item.getProductID()));
                } else if (stock <= treshold) {
                    lowOnStock.add(this.queryProduct(item.getProductID()));
                }
            } catch (SQLException ex) {
                System.err.println("Error checking stock: " + ex);
            }
        }

        System.out.println("Result from checkStock():");
        System.out.println("Low on Stock: " + lowOnStock.size() + " product(s)");
        for (Product product : lowOnStock) {
            System.out.println(product);
        }
        System.out.println("Out of Stock: " + outOfStock.size() + " product(s)");
        for (Product product : outOfStock) {
            System.out.println(product);
        }

        StringBuilder sb = new StringBuilder();
        if (!lowOnStock.isEmpty()) {
            sb.append("The following products are now almost out of stock:\n");
            for (Product product : lowOnStock) {
                sb.append(">> ").append(product.getStockQuantity())
                        .append(" remaining: ")
                        .append(product.shortDescription()).append("\n");
            }
        }
        if (!outOfStock.isEmpty()) {
            sb.append("\n");
            sb.append("The following products are now out of stock:\n");
            for (Product product : outOfStock) {
                sb.append(">> ").append(product.shortDescription()).append("\n");
            }
        }
        if (!lowOnStock.isEmpty() || !outOfStock.isEmpty()) {
            sb.append("\nFor more details, go to 'Store Management > Manage Products...'");
        }
        return sb.toString();
    }
}
