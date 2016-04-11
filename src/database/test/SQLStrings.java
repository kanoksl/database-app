package database.test;

public class SQLStrings {
    
    public static final String SQL_CURRENT_USER = "SELECT CURRENT_USER();";
    
    public static final String SQL_CUSTOMER_ID_ALL 
            = "SELECT customer_id FROM customer "
            + "WHERE customer_id != 'C0000000' ORDER BY customer_id;";
    
    public static final String SQL_CUSTOMER_ID_LATEST
            = "SELECT customer_id FROM customer ORDER BY customer_id DESC LIMIT 1;";
    
    public static final String SQL_INSERT_CUSTOMER 
            = "INSERT INTO customer (customer_id, first_name, last_name, gender, "
            + "date_of_birth, date_of_registration, customer_phone, customer_email) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
    
    public static final String SQL_UPDATE_CUSTOMER 
            = "UPDATE customer SET first_name = ?, last_name = ?, gender = ?, "
            + "date_of_birth = ?, date_of_registration = ?, customer_phone = ?, "
            + "customer_email = ? WHERE customer_id = ?;";
    
    
}
