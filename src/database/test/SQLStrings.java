package database.test;

public class SQLStrings {

    public static final String SQL_CURRENT_USER = "SELECT CURRENT_USER();";

    // customers
    public static final String SQL_CUSTOMER_ID_ALL
            = "SELECT customer_id FROM customer "
            + "WHERE customer_id != 'C0000000' AND customer_id != 'CDELETED' ORDER BY customer_id;";

    public static final String SQL_CUSTOMER_ID_LATEST
            = "SELECT customer_id FROM customer WHERE customer_id != 'CDELETED' ORDER BY customer_id DESC LIMIT 1;";

    public static final String SQL_INSERT_CUSTOMER
            = "INSERT INTO customer (customer_id, first_name, last_name, gender, "
            + "date_of_birth, date_of_registration, customer_phone, customer_email) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String SQL_UPDATE_CUSTOMER
            = "UPDATE customer SET first_name = ?, last_name = ?, gender = ?, "
            + "date_of_birth = ?, date_of_registration = ?, customer_phone = ?, "
            + "customer_email = ? WHERE customer_id = ?;";

    public static final String SQL_SELECT_A_CUSTOMER
            = "SELECT * FROM CUSTOMER WHERE customer_id = ?;";

    // products
    public static final String SQL_PRODUCT_ID_ALL
            = "SELECT product_id FROM product "
            + "WHERE product_id != 'P0000000' ORDER BY product_id;";

    public static final String SQL_PRODUCT_ID_LATEST
            = "SELECT product_id FROM product ORDER BY product_id DESC LIMIT 1;";

    public static final String SQL_CATEGORY_OVERVIEW
            = "SELECT * FROM category_overview;";
    
    public static final String SQL_INSERT_CATEGORY
            = "INSERT INTO category (category_id, category_name) VALUES(?, ?);";
    
    public static final String SQL_UPDATE_CATEGORY
            = "UPDATE category SET category_id = ?, category_name = ? WHERE category_id = ?;";
    
    public static final String SQL_DELETE_CATEGORY
            = "DELETE FROM category WHERE category_id = ?;";
    
    // suppliers
    public static final String SQL_SUPPLIER_ID_LATEST
            = "SELECT supplier_id FROM supplier ORDER BY supplier_id DESC LIMIT 1;";

}
