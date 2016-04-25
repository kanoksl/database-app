package database.test;

@SuppressWarnings("ALL")
public class SQLStrings {

    //<editor-fold desc="SQL Commands: Miscellaneous">
    public static final String SQL_CURRENT_USER = "SELECT CURRENT_USER();";
    //</editor-fold>

    //<editor-fold desc="SQL Commands: Customers - Management">
    public static final String SQL_SELECT_ALL_CUSTOMER_IDS
            = "SELECT customer_id FROM customer "
            + "WHERE (customer_id != 'C0000000') AND (customer_id != 'CDELETED') "
            + "ORDER BY customer_id;";
    public static final String SQL_SELECT_LATEST_CUSTOMER_ID
            = "SELECT customer_id FROM customer "
            + "WHERE customer_id != 'CDELETED' "
            + "ORDER BY customer_id DESC "
            + "LIMIT 1;";

    public static final String SQL_SELECT_A_CUSTOMER
            = "SELECT * FROM customer "
            + "WHERE customer_id = ?;";
    public static final String SQL_SELECT_ALL_CUSTOMERS
            = "SELECT * FROM customer;";

    public static final String SQL_INSERT_CUSTOMER
            = "INSERT INTO customer (customer_id, first_name, last_name, gender, "
            + "    date_of_birth, date_of_registration, customer_phone, customer_email) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String SQL_UPDATE_CUSTOMER
            = "UPDATE customer SET customer_id = ?, first_name = ?, last_name = ?, gender = ?, "
            + "    date_of_birth = ?, date_of_registration = ?, customer_phone = ?, customer_email = ? "
            + "WHERE customer_id = ?;";

    public static final String SQL_DELETE_CUSTOMER_UPDATE_SALE
            = "UPDATE sale SET customer_id = 'CDELETED' "
            + "WHERE customer_id = ?;";
    public static final String SQL_DELETE_CUSTOMER
            = "DELETE FROM customer WHERE customer_id = ?;";
    //</editor-fold>
    //<editor-fold desc="SQL Commands: Customers - Search & Query">
    public static final String SQL_SEARCH_CUSTOMER_BY_NAME
            = "SELECT * FROM customer "
            + "WHERE CONCAT(first_name, ' ', last_name) LIKE ?;";

    public static final String SQL_QUERY_CUSTOMER_SHOPPING_HISTORY
            = "SELECT sale_id, sale_date, sale_time, item_count, special_discount, discounted_total "
            + "FROM sale_overview "
            + "WHERE (customer_id = ?) AND (sale_date BETWEEN ? AND ?);";

    public static final String SQL_QUERY_SINGLE_SALE_DETAIL
            = "SELECT  p.product_id, p.product_name, sd.sale_quantity AS quantity, "
            + "    pr.product_price AS unit_price, (sd.sale_quantity * pr.product_price) AS subtotal "
            + "FROM product p, product_price pr, sale_detail sd, sale s "
            + "WHERE (s.sale_id = ?) "
            + "    AND (s.sale_id = sd.sale_id) "
            + "    AND (sd.product_id = p.product_id) "
            + "    AND (sd.product_id = pr.product_id) "
            + "    AND (s.sale_date BETWEEN pr.start_date AND pr.end_date);";
    //</editor-fold>

    //<editor-fold desc="SQL Commands: Products - Management">
    public static final String SQL_SELECT_ALL_PRODUCT_IDS
            = "SELECT product_id FROM product "
            + "ORDER BY product_id;";
    public static final String SQL_SELECT_ALL_AVAILABLE_PRODUCT_IDS
            = "SELECT product_id FROM product "
            + "WHERE (selling_status = 1) AND (stock_quantity > 0) "
            + "ORDER BY product_id;";
    public static final String SQL_SELECT_LATEST_PRODUCT_ID
            = "SELECT product_id FROM product "
            + "ORDER BY product_id DESC "
            + "LIMIT 1;";

    public static final String SQL_SELECT_A_PRODUCT
            = "SELECT pd.*, pr.product_price FROM product pd, product_price pr "
            + "WHERE (pd.product_id = ?) AND (pd.product_id = pr.product_id) "
            + "    AND (CURDATE() BETWEEN pr.start_date AND pr.end_date);";
    public static final String SQL_SELECT_ALL_PRODUCTS
            = "SELECT pd.*, pr.product_price FROM product pd, product_price pr "
            + "WHERE (pd.product_id = pr.product_id) "
            + "    AND (CURDATE() BETWEEN pr.start_date AND pr.end_date);";

    public static final String SQL_SELECT_PRODUCT_NAME_AND_STOCK
            = "SELECT product_name, stock_quantity FROM product "
            + "WHERE (product_id = ?);";

    public static final String SQL_INSERT_PRODUCT
            = "INSERT INTO product (product_id, product_name, product_description, "
            + "    stock_quantity, selling_status, category_id) "
            + "VALUES (?, ?, ?, ?, ?, ?);";
    public static final String SQL_INSERT_NEW_PRODUCT_PRICE
            = "INSERT INTO product_price (product_id, start_date, end_date, product_price) "
            + "VALUES (?, '1000-01-01', '9999-12-31', ?);";

    public static final String SQL_UPDATE_PRODUCT
            = "UPDATE product SET product_id = ?, product_name = ?, product_description = ?, "
            + "    stock_quantity = ?, selling_status = ?, category_id = ? "
            + "WHERE product_id = ?;";
    public static final String SQL_UPDATE_PRODUCT_SUBTRACT_STOCK_QUANTITY
            = "UPDATE product SET stock_quantity = (stock_quantity - ?) "
            + "WHERE product_id = ?;";

    public static final String SQL_DELETE_PRODUCT
            = "DELETE FROM product WHERE product_id = ?;";
    //</editor-fold>
    //<editor-fold desc="SQL Commands: Products - Search & Query">
    public static final String SQL_QUERY_PRODUCT_PRICING_HISTORY
            = "SELECT start_date, end_date, (1 + DATEDIFF(end_date, start_date)) AS duration, product_price  "
            + "FROM product_price "
            + "WHERE product_id = ? "
            + "ORDER BY start_date;";
    public static final String SQL_QUERY_PRODUCT_SELLING_HISTORY
            = "SELECT s.sale_id, CONCAT(s.sale_date, ' ', TIME_FORMAT(s.sale_time, '%H:%i')) AS date_time, "
            + "    s.customer_id, c.first_name, sd.sale_quantity AS quantity, "
            + "    pr.product_price AS unit_price, (sd.sale_quantity * pr.product_price) AS subtotal  "
            + "FROM sale s, sale_detail sd, product_price pr, customer c "
            + "WHERE (pr.product_id = ?) "
            + "	   AND (s.sale_id = sd.sale_id) "
            + "	   AND (s.customer_id = c.customer_id) "
            + "	   AND (sd.product_id = pr.product_id) "
            + "	   AND (s.sale_date BETWEEN pr.start_date AND pr.end_date)"
            + "ORDER BY s.sale_id;";

    public static final String SQL_QUERY_PRODUCT_SALE_RECORD
            = "SELECT s.sale_id FROM sale s, sale_detail sd "
            + "WHERE (s.sale_id = sd.sale_id) AND (sd.product_id = ?);";
    public static final String SQL_QUERY_PRODUCT_SALE_RECORD_TODAY
            = "SELECT s.sale_id FROM sale s, sale_detail sd "
            + "WHERE (s.sale_id = sd.sale_id) AND (s.sale_date = CURDATE()) AND (sd.product_id = ?);";

    public static final String SQL_SEARCH_PRODUCT_BY_NAME
            = "SELECT * FROM product "
            + "WHERE product_name LIKE ?;";

    public static final String SQL_SEARCH_PRODUCT_BY_FILTER_BASECASE // category + status + name
            = "SELECT pd.*, pr.product_price, c.category_name "
            + "FROM product pd, product_price pr, category c "
            + "WHERE (pd.product_id = pr.product_id) AND (pd.category_id = c.category_id) "
            + "    AND (CURDATE() BETWEEN pr.start_date AND pr.end_date) ";
    //</editor-fold>
    //<editor-fold desc="SQL Commands: Products - Pricing Updates">
    public static final String SQL_UPDATE_PRODUCT_PRICE_TODAY_ROW_PRICE
            = "UPDATE product_price SET product_price = ? "
            + "WHERE (product_id = ?) AND (start_date = CURDATE());";
    public static final String SQL_UPDATE_PRODUCT_PRICE_TOMORROW_ROW_PRICE
            = "UPDATE product_price SET product_price = ? "
            + "WHERE (product_id = ?) AND (start_date = (CURDATE() + INTERVAL 1 DAY));";

    public static final String SQL_UPDATE_PRODUCT_PRICE_CURRENT_ROW_END_DATE
            = "UPDATE product_price SET end_date = ? "
            + "WHERE (product_id = ?) AND (CURDATE() BETWEEN start_date AND end_date);";
    public static final String SQL_UPDATE_PRODUCT_PRICE_YESTERDAY_ROW_END_DATE
            = "UPDATE product_price SET end_date = ? "
            + "WHERE (product_id = ?) AND (end_date = (CURDATE() - INTERVAL 1 DAY));";

    public static final String SQL_INSERT_PRODUCT_PRICE_ROW
            = "INSERT INTO product_price (product_id, start_date, end_date, product_price) "
            + "VALUES (?, ?, '9999-12-31', ?)";

    public static final String SQL_SELECT_PRODUCT_PRICE_ENDING_TODAY
            = "SELECT product_price FROM product_price "
            + "WHERE (product_id = ?) AND (end_date = CURDATE());";
    public static final String SQL_SELECT_PRODUCT_PRICE_ENDING_YESTERDAY
            = "SELECT product_price FROM product_price "
            + "WHERE (product_id = ?) AND (end_date = (CURDATE() - INTERVAL 1 DAY));";

    public static final String SQL_DELETE_PRODUCT_PRICE_TODAY_ROW
            = "DELETE FROM product_price "
            + "WHERE (product_id = ?) AND (start_date = CURDATE());";
    public static final String SQL_DELETE_PRODUCT_PRICE_TOMORROW_ROW
            = "DELETE FROM product_price "
            + "WHERE (product_id = ?) AND (start_date = (CURDATE() + INTERVAL 1 DAY));";
    //</editor-fold>

    //<editor-fold desc="SQL Commands: Categories - Management">
    public static final String SQL_SELECT_CATEGORY_OVERVIEW
            = "SELECT * FROM category_overview "
            + "ORDER BY category_id;";

    public static final String SQL_SELECT_ALL_CATEGORY_IDS
            = "SELECT category_id FROM category "
            + "ORDER BY category_id;";

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

    //<editor-fold desc="SQL Commands: Suppliers - Management">
    public static final String SQL_SELECT_ALL_SUPPLIER_IDS
            = "SELECT supplier_id FROM supplier "
            + "ORDER BY supplier_id;";
    public static final String SQL_SELECT_LATEST_SUPPLIER_ID
            = "SELECT supplier_id FROM supplier "
            + "ORDER BY supplier_id DESC "
            + "LIMIT 1;";

    public static final String SQL_SELECT_A_SUPPLIER
            = "SELECT * FROM supplier "
            + "WHERE supplier_id = ?;";
    public static final String SQL_SELECT_ALL_SUPPLIERS
            = "SELECT * FROM supplier "
            + "ORDER BY supplier_id;";

    public static final String SQL_INSERT_SUPPLIER
            = "INSERT INTO supplier (supplier_id, supplier_name, supplier_address, "
            + "    supplier_phone, supplier_email, supplier_website, notes) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String SQL_UPDATE_SUPPLIER
            = "UPDATE supplier SET supplier_id = ?, supplier_name = ?, supplier_address = ?, "
            + "    supplier_phone = ?, supplier_email = ?, supplier_website = ?, notes = ? "
            + "WHERE supplier_id = ?;";

    public static final String SQL_DELETE_SUPPLIER
            = "DELETE FROM supplier "
            + "WHERE supplier_id = ?;";
    //</editor-fold>
    //<editor-fold desc="SQL Commands: Suppliers - Search & Query">
    public static final String SQL_SEARCH_SUPPLIER_BY_NAME
            = "SELECT * FROM supplier "
            + "WHERE supplier_name LIKE ? "
            + "ORDER BY supplier_id;";
    //</editor-fold>

    //<editor-fold desc="SQL Commands: Product-Supplier - Management">
    public static final String SQL_SELECT_SUPPLIERS_OF_A_PRODUCT
            = "SELECT supplier_id FROM product_supplier "
            + "WHERE product_id = ? "
            + "ORDER BY supplier_id;";
    public static final String SQL_SELECT_PRODUCTS_OF_A_SUPPLIER
            = "SELECT product_id FROM product_supplier "
            + "WHERE supplier_id = ? "
            + "ORDER BY product_id;";

    public static final String SQL_PRODUCTSUPPLIER_INSERT
            = "INSERT INTO product_supplier (product_id, supplier_id) VALUES (?, ?);";

    public static final String SQL_PRODUCTSUPPLIER_DELETE_BY_PRODUCT
            = "DELETE FROM product_supplier WHERE product_id = ?;";
    public static final String SQL_PRODUCTSUPPLIER_DELETE_BY_SUPPLIER
            = "DELETE FROM product_supplier WHERE supplier_id = ?;";
    //</editor-fold>
    //<editor-fold desc="SQL Commands: Product-Supplier - Miscellaneous">
    public static final String SQL_SEARCH_ID_AND_NAME_PRODUCTS
            = "SELECT product_id, product_name FROM product "
            + "WHERE (product_id LIKE ?) OR (product_name LIKE ?)"
            + "ORDER BY product_id;";
    public static final String SQL_SEARCH_ID_AND_NAME_SUPPLIERS
            = "SELECT supplier_id, supplier_name FROM supplier "
            + "WHERE (supplier_id LIKE ?) OR (supplier_name LIKE ?)"
            + "ORDER BY supplier_id;";
    //</editor-fold>

    //<editor-fold desc="SQL Commands: Sale Records">
    public static final String SQL_SELECT_LATEST_SALE_ID
            = "SELECT sale_id FROM sale "
            + "ORDER BY sale_id DESC "
            + "LIMIT 1;";

    public static final String SQL_INSERT_SALE
            = "INSERT INTO sale (sale_id, sale_date, sale_time, special_discount, customer_id) "
            + "VALUES (?, ?, ?, ?, ?);";
    public static final String SQL_INSERT_SALE_DETAIL
            = "INSERT INTO sale_detail (sale_id, product_id, sale_quantity) "
            + "VALUES (?, ?, ?);";

    public static final String SQL_DELETE_SALE
            = "DELETE FROM sale "
            + "WHERE sale_id = ?;";
    //</editor-fold>

    public static final String SQL_QUERY_SALE_HISTORY
            = "SELECT sale_id, sale_date, sale_time, customer_id, item_count, special_discount, discounted_total "
            + "FROM sale_overview "
            + "WHERE (sale_date BETWEEN ? AND ?);";

    //<editor-fold desc="SQL Commands: Statistics">
    public static final String SQL_STATS_CUSTOMER_COUNT_BY_GENDER
            = "SELECT (CASE gender WHEN 'M' THEN 'Male' WHEN 'F' THEN 'Female' "
            + "    ELSE 'Unknown' END) AS Gender, COUNT(customer_id) AS Count "
            + "FROM customer WHERE (customer_id != 'C0000000') AND (customer_id != 'CDELETED') "
            + "GROUP BY Gender "
            + "ORDER BY (CASE gender WHEN 'M' THEN 1 WHEN 'F' THEN 2 ELSE 3 END);";

    public static final String SQL_STATS_CUSTOMER_COUNT_BY_AGE
            = "SELECT 'Under 20' AS 'Age Range', COUNT(customer_id) AS 'Count' FROM customer_age WHERE age BETWEEN 0 AND 19 UNION \n"
            + "SELECT '20 - 29', COUNT(customer_id) FROM customer_age WHERE age BETWEEN 20 AND 29 UNION \n"
            + "SELECT '30 - 39', COUNT(customer_id) FROM customer_age WHERE age BETWEEN 30 AND 39 UNION \n"
            + "SELECT '40 - 49', COUNT(customer_id) FROM customer_age WHERE age BETWEEN 40 AND 49 UNION \n"
            + "SELECT '50 - 59', COUNT(customer_id) FROM customer_age WHERE age BETWEEN 50 AND 59 UNION \n"
            + "SELECT '60 - 69', COUNT(customer_id) FROM customer_age WHERE age BETWEEN 60 AND 69 UNION \n"
            + "SELECT '70 - 79', COUNT(customer_id) FROM customer_age WHERE age BETWEEN 70 AND 79 UNION \n"
            + "SELECT 'Above 79', COUNT(customer_id) FROM customer_age WHERE age >= 80;";

    public static final String SQL_STATS_CATEGORY_PRODUCT_COUNTS
            = "SELECT category_name AS Category, product_count AS ProductCount "
            + "FROM category_overview;";
    public static final String SQL_STATS_CATEGORY_STOCK_AMOUNTS
            = "SELECT category_name AS Category, total_quantity AS Amount "
            + "FROM category_overview;";

    public static final String SQL_STATS_SUPPLIER_PRODUCT_COUNTS
            = "SELECT CONCAT(s.supplier_id, ' : ', s.supplier_name) AS Supplier, COUNT(ps.product_id) AS ProductCount "
            + "FROM supplier s, product_supplier ps "
            + "WHERE s.supplier_id = ps.supplier_id "
            + "GROUP BY s.supplier_id "
            + "ORDER BY ProductCount DESC;";

    public static final String SQL_STATS_UNIT_SOLD_BY_PRODUCT
            = "SELECT p.product_id, p.product_name, IFNULL(SUM(sd.sale_quantity), 0) "
            + "FROM product p, sale s, sale_detail sd "
            + "WHERE (p.product_id = sd.product_id) AND (s.sale_id = sd.sale_id) AND (s.sale_date BETWEEN ? AND ?) "
            + "GROUP BY p.product_id "
            + "ORDER BY p.product_id;";
    public static final String SQL_STATS_UNIT_SOLD_BY_CATEGORY
            = "SELECT c.category_id, c.category_name, IFNULL(SUM(sd.sale_quantity), 0) "
            + "FROM category c, product p, sale s, sale_detail sd "
            + "WHERE (c.category_id = p.category_id) AND (p.product_id = sd.product_id) AND (s.sale_id = sd.sale_id) AND (s.sale_date BETWEEN ? AND ?) "
            + "GROUP BY c.category_id "
            + "ORDER BY c.category_id;";

    public static final String SQL_STATS_SALES_BY_PERIOD_PART1_MONTH
            = "SELECT DATE_FORMAT(so.sale_date, '%Y-%m') AS Period, \n"
            + "	   SUM(sd.sale_quantity) AS TotalUnitSold, ROUND(SUM(sd.sale_quantity) / DAY(LAST_DAY(so.sale_date)), 2) AS AvgUnitSold, \n"
            + "    SUM(so.discounted_total) AS TotalIncome, ROUND(SUM(so.discounted_total) / DAY(LAST_DAY(so.sale_date)), 2) AS AvgIncome \n";
    public static final String SQL_STATS_SALES_BY_PERIOD_PART1_WEEK
            = "SELECT INSERT(YEARWEEK(so.sale_date), 5, 0, '-W') AS Period, \n"
            + "	   SUM(sd.sale_quantity) AS TotalUnitSold, ROUND(SUM(sd.sale_quantity) / 7, 2) AS AvgUnitSold, \n"
            + "    SUM(so.discounted_total) AS TotalIncome, ROUND(SUM(so.discounted_total) / 7, 2) AS AvgIncome \n";
    public static final String SQL_STATS_SALES_BY_PERIOD_PART1_DAY 
            = "SELECT so.sale_date AS Period, SUM(sd.sale_quantity) AS TotalUnitSold, SUM(so.discounted_total) AS TotalIncome \n";
    public static final String SQL_STATS_SALES_BY_PERIOD_PART2
            = "FROM sale_overview so, sale_detail sd, product p \n"
            + "WHERE (so.sale_id = sd.sale_id) AND (sd.product_id = p.product_id) \n"
            + "    AND (so.sale_date BETWEEN ? AND ?) ";
    public static final String SQL_STATS_SALES_BY_PERIOD_PART3 = "GROUP BY Period ORDER BY Period;";
    
    public static final String SQL_SELECT_YEARWEEK = "SELECT YEARWEEK(?);";
    //</editor-fold>


    public static final java.sql.Date SQL_MINDATE = java.sql.Date.valueOf("1000-01-01");
    public static final java.sql.Date SQL_MAXDATE = java.sql.Date.valueOf("9999-12-31");
}
