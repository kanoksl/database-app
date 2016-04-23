package database.test.gui;

import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;

/**
 * Containing constants shared by many GUI windows.
 */
public class Const {

    public static final int LOW_STOCK_TRESHOLD = 5;
    
    // Colors
    public static final Color COLOR_HIGHLIGHT_BG = Color.decode("#333333");
    public static final Color COLOR_HIGHLIGHT_FG = Color.decode("#FFFFFF");
    public static final Color COLOR_ERROR_TEXT = Color.RED;
    public static final Color COLOR_LOGIN_WINDOW_BG = Color.WHITE;
    public static final Color COLOR_TABLE_GRID = Color.decode("#CCCCCC");
    public static final Color COLOR_CHECKOUT_WINDOW_BG = Color.decode("#FFFFFF");
    public static final Color COLOR_CHECKOUT_WINDOW_ACCENT = Color.decode("#F0F0F0");
    public static final Color COLOR_BG_LIGHTGRAY = Color.decode("#F5F5F5");

    // Fonts
    public static final Font FONT_DEFAULT_12 = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_DEFAULT_12_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_DEFAULT_16 = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_DEFAULT_24 = new Font("Segoe UI", Font.PLAIN, 24);

    // Strings - Window Titles
    public static final String APP_TITLE = "Retail Store Database System";
    public static final String STORE_NAME = "printf Clothing Shop";
    
    public static final String WIN_TITLE_LOGIN = "Login";
    public static final String WIN_TITLE_POINTOFSALE = "Point of Sale";

    // Strings - Others
    public static final String CURRENCY = "฿";

    // File Paths / Resource Paths
    public static final String PATH_TO_LOGO_IMAGE = "/database/resource/logo_new.png";

    public static enum InfoWindowMode {
        ADD, EDIT, VIEW
    };

    // EditCustomerInfoWindow (ECIW)
    public static final String ECIW_WARNING_ID = "This ID is already used.";
    public static final String ECIW_WARNING_NAME = "The first name cannot be blank.";
    public static final String ECIW_HEADER_ADD = "Register New Customer";
    public static final String ECIW_HEADER_EDIT = "Edit Customer Information";
    public static final String ECIW_HEADER_VIEW = "View Customer Information";
    
    // EditCategoryInfoWindow (ECATIW)
    public static final String ECATIW_HEADER_ADD = "New Category";
    public static final String ECATIW_HEADER_EDIT = "Edit Category";

    // EditProductInfoWindow (EPIW)
    public static final String EPIW_HEADER_ADD = "Add New Product";
    public static final String EPIW_HEADER_EDIT = "Edit Product Information";
    public static final String EPIW_HEADER_VIEW = "View Product Information";
    
    // EditSupplierInfoWindow (ESIW)
    public static final String ESIW_HEADER_ADD = "Add New Supplier";
    public static final String ESIW_HEADER_EDIT = "Edit Supplier Information";
    public static final String ESIW_HEADER_VIEW = "View Supplier Information";
    
    // JOptionPane Messages
    public static final String MESSAGE_POS_CONFIRM_LOGOUT
            = "The current shopping list is not checked out yet.\nAre you sure you want to logout?";
    public static final String MESSAGE_POS_CONFIRM_EXIT
            = "The current shopping list is not checked out yet.\nAre you sure you want to exit?";
    public static final String MESSAGE_POS_CONFIRM_CLEAR
            = "Clear the current shopping list?";

    // Database-Related
    public static final String UNREGISTERED_CUSTOMER_ID = "C0000000";
    public static final String UNREGISTERED_CUSTOMER_NAME = "Unregistered Customer";
    public static final String DELETED_CUSTOMER_ID = "CDELETED";

    // DateTime
    public static final LocalDate SQL_MINDATE = LocalDate.of(1000, 1, 1);
    public static final LocalDate SQL_MAXDATE = LocalDate.of(9999, 12, 31);
}
