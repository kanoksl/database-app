package database.test;

import database.test.gui.LoginWindow;
import database.test.gui.PointOfSaleWindow;
import java.awt.Window;

public class ApplicationMain
        implements LoginWindow.LoginListener, PointOfSaleWindow.LogoutListener {

    private static DatabaseManager database; // singleton

    private LoginWindow window_login;
    private PointOfSaleWindow window_pos;

    public ApplicationMain() {
        database = new DatabaseManager("localhost", "3306", "retaildb_v1");
    }

    public void start() {
        // initializes the window
        window_login = new LoginWindow(this, database);
        window_login.setVisible(true);
        // automate login
//        window_login.submit();
    }

    @Override
    public void login() {
        // when the user logged in successfully
        window_login.setVisible(false);
        window_pos = new PointOfSaleWindow(this);
        window_pos.setVisible(true);
    }

    @Override
    public void logout() {
        // when the user log out
        database.disconnect();
        System.gc();
        for (Window window : Window.getWindows()) {
            System.out.println("Disposing: " + window.getClass().getName());
            window.dispose(); // destroy all the windows
        }

        window_login = new LoginWindow(this, database);
        window_login.setVisible(true);
    }

    /**
     * Get the singleton instance of DatabaseManager that is initialized when
     * the application starts and is logged in by the user through the login
     * window GUI.
     *
     * @return The instance of DatabaseManager used by the application
     */
    public static DatabaseManager getDatabaseInstance() {
        return database;
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
