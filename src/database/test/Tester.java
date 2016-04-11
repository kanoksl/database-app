package database.test;

import database.test.data.*;
import database.test.gui.*;

public class Tester {

    public static void main(String[] args) {
        //<editor-fold defaultstate="collapsed" desc="Setting GUI Look and Feel">
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Error setting look and feel:\n\t" + ex);
        }
        //</editor-fold>

        DatabaseManager database = new DatabaseManager("localhost", "3306", "retaildb_v1");
        database.setUsername("root");
        database.setPassword("admin");
        database.connect();

        EditCustomerInfoWindow cw = new EditCustomerInfoWindow();
        cw.setDatabase(database);
        
//        cw.setMode(Const.InfoWindowModes.AddNew);
//        cw.setCustomer(Customer.createNewCustomer(database.getNextCustomerID()));
//        cw.showCustomerInfo();

        cw.setMode(Const.InfoWindowModes.Editable);
        cw.setCustomer(database.queryCustomer("C0000001"));
        cw.showCustomerInfo();
        
        cw.setVisible(true);
        
        
    }

}
