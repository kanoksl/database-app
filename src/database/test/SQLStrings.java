package database.test;

public class SQLStrings {
    // TODO: add all required SQL commands here

    //<editor-fold desc="SQL Commands: Miscellaneous">
    public static final String SQL_CURRENT_USER = "SELECT CURRENT_USER();";
    //</editor-fold>

    //<editor-fold desc="SQL Commands: Customers">
    public static final String SQL_CUSTOMER_ID_ALL
            = "SELECT customer_id FROM customer "
            + "WHERE (customer_id != 'C0000000') AND (customer_id != 'CDELETED') "
            + "ORDER BY customer_id;";

    public static final String SQL_CUSTOMER_ID_LATEST
            = "SELECT customer_id FROM customer "
            + "WHERE customer_id != 'CDELETED' "
            + "ORDER BY customer_id DESC "
            + "LIMIT 1;";

    public static final String SQL_INSERT_CUSTOMER
            = "INSERT INTO customer (customer_id, first_name, last_name, gender, "
            + "    date_of_birth, date_of_registration, customer_phone, customer_email) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String SQL_UPDATE_CUSTOMER
            = "UPDATE customer SET customer_id = ?, first_name = ?, last_name = ?, gender = ?, "
            + "    date_of_birth = ?, date_of_registration = ?, customer_phone = ?, customer_email = ? "
            + "WHERE customer_id = ?;";

    public static final String SQL_SELECT_A_CUSTOMER
            = "SELECT * FROM customer "
            + "WHERE customer_id = ?;";

    public static final String SQL_SELECT_ALL_CUSTOMERS
            = "SELECT * FROM customer;";
    
    public static final String SQL_SEARCH_CUSTOMER_BY_NAME
            = "SELECT * FROM customer "
            + "WHERE (first_name LIKE ?) OR (last_name LIKE ?);";
    //</editor-fold>

    //<editor-fold desc="SQL Commands: Products">
    public static final String SQL_PRODUCT_ID_ALL_AVAILABLE
            = "SELECT product_id FROM product "
            + "WHERE (selling_status = 1) AND (stock_quantity > 0) "
            + "ORDER BY product_id;";

    public static final String SQL_PRODUCT_ID_LATEST
            = "SELECT product_id FROM product "
            + "ORDER BY product_id DESC "
            + "LIMIT 1;";

    public static final String SQL_INSERT_PRODUCT
            = "INSERT INTO product (product_id, product_name, product_description, "
            + "    stock_quantity, selling_status, category_id) "
            + "VALUES (?, ?, ?, ?, ?, ?);";

    public static final String SQL_UPDATE_PRODUCT
            = "UPDATE product SET product_id = ?, product_name = ?, product_description = ?, "
            + "    stock_quantity = ?, selling_status = ?, category_id = ? "
            + "WHERE product_id = ?;";

    public static final String SQL_SELECT_A_PRODUCT
            = "SELECT * FROM product "
            + "WHERE product_id = ?;";
    
    public static final String SQL_SEARCH_PRODUCT_BY_NAME
            = "SELECT * FROM product "
            + "WHERE product_name LIKE ?;";

    public static final String SQL_SEARCH_PRODUCT_BY_QUANTITY
            = "SELECT * FROM product "
            + "WHERE stock_quantity <= ?;";
    //</editor-fold>

    //<editor-fold desc="SQL Commands: Products - Categories">
    public static final String SQL_CATEGORY_OVERVIEW
            = "SELECT * FROM category_overview;";

    public static final String SQL_INSERT_CATEGORY
            = "INSERT INTO category (category_id, category_name) "
            + "VALUES (?, ?);";

    public static final String SQL_UPDATE_CATEGORY
            = "UPDATE category SET category_id = ?, category_name = ? "
            + "WHERE category_id = ?;";

    public static final String SQL_DELETE_CATEGORY
            = "DELETE FROM category "
            + "WHERE category_id = ?;";
    //</editor-fold>

    //<editor-fold desc="SQL Commands: Suppliers">
    public static final String SQL_SUPPLIER_ID_LATEST
            = "SELECT supplier_id FROM supplier "
            + "ORDER BY supplier_id DESC "
            + "LIMIT 1;";

    public static final String SQL_INSERT_SUPPLIER
            = "INSERT INTO supplier (supplier_id, supplier_name, supplier_address, "
            + "    supplier_phone, supplier_email, supplier_website, notes) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String SQL_UPDATE_SUPPLIER
            = "UPDATE supplier SET supplier_id = ?, supplier_name = ?, supplier_address = ?, "
            + "    supplier_phone = ?, supplier_email = ?, supplier_website = ?, notes = ? "
            + "WHERE supplier_id = ?;";

    public static final String SQL_SELECT_A_SUPPLIER
            = "SELECT * FROM supplier "
            + "WHERE supplier_id = ?;";
    //</editor-fold>
    
    public static final java.sql.Date SQL_MINDATE = java.sql.Date.valueOf("1000-01-01");
    public static final java.sql.Date SQL_MAXDATE = java.sql.Date.valueOf("9999-12-31");
}
