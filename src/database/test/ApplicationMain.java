package database.test;

import database.test.gui.DebugWindow;
import database.test.gui.LoginWindow;
import database.test.gui.PointOfSaleWindow;

public class ApplicationMain
        implements LoginWindow.LoginListener, PointOfSaleWindow.LogoutListener {

    private final DatabaseManager database;

    private DebugWindow window_debug;
    private LoginWindow window_login;
    private PointOfSaleWindow window_pos;

    public ApplicationMain() {
        this.database = new DatabaseManager("localhost", "3306", "retaildb_v1");
    }

    public void start() {
        // initializes the windows
        window_debug = new DebugWindow();
        window_login = new LoginWindow(this, database);
        window_pos = new PointOfSaleWindow(this, database);

        // displays the login window first
        window_debug.setVisible(true);
        window_login.setVisible(true);

        // automate login
        window_login.submit();
    }

    @Override
    public void login() {
        // when the user logged in successfully
        window_login.setVisible(false);
        window_pos.prepare();
        window_pos.setVisible(true);
    }

    @Override
    public void logout() {
        // when the user log out
        database.disconnect();
        window_pos.setVisible(false);
        // TODO: hide other windows too if exists
        window_login.setVisible(true);
    }

    //<editor-fold defaultstate="collapsed" desc="Actual Application Starting Point: The main() Method">
    private static ApplicationMain app = null;

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

        app = new ApplicationMain();
        app.start();
    }
    //</editor-fold>

}
