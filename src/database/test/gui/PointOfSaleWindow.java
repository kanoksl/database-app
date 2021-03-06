package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.data.Customer;
import database.test.data.Product;
import database.test.data.ShoppingList;
import database.test.gui.Const.InfoWindowMode;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

public class PointOfSaleWindow
        extends javax.swing.JFrame
        implements ConfirmCheckoutPanel.CheckoutListener {

    private final DatabaseManager database = ApplicationMain.getDatabaseInstance();
    private LogoutListener logoutListener = null;

    private Customer currentCustomer = null;
    private final ShoppingList shoppingList = new ShoppingList();

    private JDialog dialog_checkout;
    private DebugWindow window_debug;
    private ManageCustomersWindow window_customers;
    private ManageProductsWindow window_products;
    private ManageCategoriesWindow window_categories;
    private ManageSuppliersWindow window_suppliers;
    private SaleHistoryWindow window_sales;
    private SaleStatisticWindow window_saleStat;
    private StatisticSelectWindow window_stats;

    /**
     * Create a new PointOfSaleWindow that is connected to the given database.
     *
     * @param logoutListener The object to be notified if the user log out.
     * @param database The retail store database.
     */
    public PointOfSaleWindow(LogoutListener logoutListener) {
        System.out.println("Initializing new PointOfSaleWindow");
        // initializes the components
        this.initComponents();
        cbxCustomerIDTextField = (JTextField) cbxCustomerID.getEditor().getEditorComponent();
        cbxProductIDTextField = (JTextField) cbxProductID.getEditor().getEditorComponent();
        this.initListeners();
        this.initTableModel();
        this.setColorTheme();

        this.logoutListener = logoutListener;

        this.setLocationRelativeTo(null);
        this.setTitle(Const.WIN_TITLE_POINTOFSALE + " - " + Const.APP_TITLE);
        this.loadLogoImage();
        this.loadLoginName();

        this.loadCustomerIDList();
        this.loadProductIDList();

        btnCheckCustomerID.setVisible(false);
        chkRegisteredCustomer.setSelected(false);
        this.toggleRegisteredCustomer();

        this.clear();
    }

    //<editor-fold defaultstate="collapsed" desc="GUI + Data Code: Customer ID Checking">
    /**
     * Switch between unregistered customer or registered customer mode.
     */
    private void toggleRegisteredCustomer() {
        if (chkRegisteredCustomer.isSelected()) {
            // registered customer; input the ID and check
            lblCustomerName.setForeground(Color.BLACK);
            cbxCustomerID.setEnabled(true);
            btnCheckCustomerID.setEnabled(true);
            cbxCustomerID.requestFocus();
        } else {
            // unregistered customer; use the special ID
            cbxCustomerID.setSelectedItem(Const.UNREGISTERED_CUSTOMER_ID);
            lblCustomerName.setText(Const.UNREGISTERED_CUSTOMER_NAME);
            lblCustomerName.setForeground(Color.DARK_GRAY);
            cbxCustomerID.setEnabled(false);
            btnCheckCustomerID.setEnabled(false);
        }
        this.checkCustomerID();
    }

    /**
     * Query the database for the entered customer ID to check whether the ID
     * exists or not. Then set the current customer to that ID if exists.
     */
    private void checkCustomerID() {
        String id = cbxCustomerIDTextField.getText().trim();
        if (id.equals(Const.DELETED_CUSTOMER_ID)) {
            id = "";
        }
        currentCustomer = database.queryCustomer(id);

        // display the customer name
        if (currentCustomer != null) {
            String displayName = currentCustomer.getDisplayName();
            lblCustomerName.setText(displayName);
            lblCustomerName.setForeground(Color.BLACK);
            if (currentCustomer.getID().equals(Const.UNREGISTERED_CUSTOMER_ID)) {
                menuViewCurrentCustomer.setEnabled(true);
                menuEditCurrentCustomer.setEnabled(false);
            } else {
                menuViewCurrentCustomer.setEnabled(true);
                menuEditCurrentCustomer.setEnabled(true);
            }
        } else {
            lblCustomerName.setText("Error: no customer found with that ID");
            lblCustomerName.setForeground(Const.COLOR_ERROR_TEXT);
            menuViewCurrentCustomer.setEnabled(false);
            menuEditCurrentCustomer.setEnabled(false);
        }
        this.updateConfirmButtonEnabled();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="GUI + Data Code: Shopping List Manipulation">
    /**
     * Query the database and check whether the entered product ID is valid or
     * not.
     *
     * @return The Product with that ID if it's available for selling (selling
     * status = 1 and quantity > 0). Null otherwise.
     */
    private Product checkProductID() {
        String id = cbxProductIDTextField.getText().trim();
        Product product = database.queryProduct(id);

        if (product == null) {
            lblListAddMessage.setText("<html>No product found with that ID.</html>");
            lblListAddMessage.setForeground(Const.COLOR_ERROR_TEXT);
        } else if (!product.isSelling()) {
            lblListAddMessage.setText("<html>This product is not being sold anymore.</html>");
            lblListAddMessage.setForeground(Const.COLOR_ERROR_TEXT);
        } else if (product.getStockQuantity() < 1) {
            lblListAddMessage.setText("<html>This product is currently out of stock.</html>");
            lblListAddMessage.setForeground(Const.COLOR_ERROR_TEXT);
        } else {
            int max = product.getStockQuantity();
            String shortName;
            if (product.getName().length() > 26) {
                shortName = product.getName().substring(0, 26) + "...";
            } else {
                shortName = product.getName();
            }
            lblListAddMessage.setText("<html>>> " + shortName + "<br/>"
                    + "Stock Quantity: " + max + "</html>");
            lblListAddMessage.setForeground(Color.BLACK);
            // set the maximum to the stock quantity
            int q = (int) spnQuantity.getValue();
            spnQuantity.setModel(new SpinnerNumberModel(Math.min(q, max), 1, max, 1));
            return product;
        }
        return null;
    }

    /**
     * Add a new entry to the shopping list. If the product to be added already
     * exists in the list, add the quantity to the existing ProductLine.
     */
    private void shoppingListAdd() {
        Product product = this.checkProductID();
        if (product == null) {
            return;
        }

        int quantity = (int) spnQuantity.getValue();
        int result[] = shoppingList.addItem(product, quantity);
        int amountActuallyAdded = result[0];
        if (amountActuallyAdded == 0) {
            int stock = product.getStockQuantity();
            lblListAddMessage.setText("<html>Cannot add.<br/>Only " + stock + " unit"
                    + (stock == 1 ? "" : "s") + " available in total.</html>");
            lblListAddMessage.setForeground(Const.COLOR_ERROR_TEXT);
        } else {
            // update the GUI
            if (amountActuallyAdded != quantity) {
                int stock = product.getStockQuantity();
                lblListAddMessage.setText("<html>Added " + amountActuallyAdded + " unit.<br/>Only "
                        + stock + " unit" + (stock == 1 ? "" : "s") + " available in total.</html>");
                lblListAddMessage.setForeground(Color.BLACK);
            } else {
                lblListAddMessage.setText("Added " + quantity + " unit.");
                lblListAddMessage.setForeground(Color.BLACK);
            }
            this.updateShoppingListGUI();
            this.updateTableSelection(result[1]);
        }
        // clear the product input
        spnQuantity.setValue(1);
        cbxProductIDTextField.setSelectionStart(0);
        cbxProductIDTextField.setSelectionEnd(Integer.MAX_VALUE);
        cbxProductIDTextField.requestFocus();
    }

    private void shoppingListRemove() {
        if (table.getSelectedRowCount() == 0 || shoppingList.isEmpty()) {
            return;
        }

        int[] selected = table.getSelectedRows();
        for (int i = selected.length - 1; i >= 0; i--) {
            shoppingList.removeItemAt(selected[i]);
        }

        this.updateShoppingListGUI();
        this.updateTableSelection(Integer.MAX_VALUE);
    }
    //</editor-fold>

    /**
     * Check out the shopping list.
     */
    public void checkout() {
        shoppingList.setCustomer(currentCustomer);

        System.out.println("checkout(): " + shoppingList);

        dialog_checkout = new JDialog(this, "Checkout", true);
        dialog_checkout.getContentPane().add(new ConfirmCheckoutPanel(this, shoppingList));
        dialog_checkout.pack();
        dialog_checkout.setResizable(false);
        dialog_checkout.setLocationRelativeTo(btnConfirm);
        dialog_checkout.setVisible(true);
    }

    @Override
    public void checkoutConfirmed() {
        System.out.println("checkoutConfirmed(): " + shoppingList);
        // TODO: show receipt and insert record to the database, notify low stock
        dialog_checkout.setVisible(false);
        dialog_checkout = null;

        if (database.tryProcessSale(shoppingList, this)) {
            ReceiptWindow rew = new ReceiptWindow();
            rew.setReceipt(ReceiptWindow.generateReceipt(shoppingList));
            rew.setLocationRelativeTo(logoLabel);
            rew.setVisible(true);

            String checkResult = database.checkStock(shoppingList, Const.LOW_STOCK_TRESHOLD);
            JOptionPane.showMessageDialog(this,
                    checkResult, "Check Stock",
                    JOptionPane.INFORMATION_MESSAGE);

            this.clear();
            this.loadProductIDList();

            rew.requestFocus();
        }
    }

    @Override
    public void checkoutCanceled() {
        dialog_checkout.setVisible(false);
        dialog_checkout = null;
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Menu Handlers - Displaying Other Windows">
    private void showCurrentCustomerInfoWindow(InfoWindowMode mode) {
        if (mode == InfoWindowMode.EDIT) {
            currentCustomer = EditCustomerInfoWindow.showEditCustomerDialog(this, currentCustomer);
            if (currentCustomer == null) {
                chkRegisteredCustomer.setSelected(false);
                this.toggleRegisteredCustomer();
            } else {
                this.checkCustomerID();
            }
        } else if (mode == InfoWindowMode.VIEW) {
            EditCustomerInfoWindow.showViewCustomerDialog(this, currentCustomer);
        }
    }

    private void showNewCustomerWindow() {
        Customer c = EditCustomerInfoWindow.showNewCustomerDialog(this);
        if (c != null) {
            this.loadCustomerIDList();
            chkRegisteredCustomer.setSelected(true);
            this.toggleRegisteredCustomer();
            cbxCustomerID.setSelectedItem(c.getID());
        }
    }

    private static void showDataWindow(DataDisplayWindow window) {
        if (!window.isVisible()) {
            window.refresh();
            window.setVisible(true);
        } else {
            window.requestFocus();
        }
    }

    private void showManageCustomersWindow() {
        if (window_customers == null) {
            window_customers = new ManageCustomersWindow();
        }
        showDataWindow(window_customers);
    }

    private void showManageProductsWindow() {
        if (window_products == null) {
            window_products = new ManageProductsWindow();
        }
        showDataWindow(window_products);
    }

    private void showManageCategoriesWindow() {
        if (window_categories == null) {
            window_categories = new ManageCategoriesWindow();
        }
        showDataWindow(window_categories);
    }

    private void showManageSuppliersWindow() {
        if (window_suppliers == null) {
            window_suppliers = new ManageSuppliersWindow();
        }
        showDataWindow(window_suppliers);
    }

    private void showSaleRecordsWindow() {
        if (window_sales == null) {
            window_sales = new SaleHistoryWindow();
        }
        showDataWindow(window_sales);
    }

    private void showSaleStatsWindow() {
        if (window_saleStat == null) {
            window_saleStat = new SaleStatisticWindow();
        }
        window_saleStat.statRefresh();
        showDataWindow(window_saleStat);
    }

    public void showStatisticSelectWindow() {
        if (window_stats == null) {
            window_stats = new StatisticSelectWindow();
        }
        if (!window_stats.isVisible()) {
            window_stats.setVisible(true);
        } else {
            window_stats.requestFocus();
        }
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    /**
     * Clear all the data in the window.
     */
    public void clear() {
        cbxProductIDTextField.setText("");
        lblListAddMessage.setText(" ");
        spnQuantity.setValue(1);

        chkRegisteredCustomer.setSelected(false);
        this.toggleRegisteredCustomer();
        chkRegisteredCustomer.requestFocus();

        shoppingList.clear();
        this.updateShoppingListGUI();
    }

    private void loadCustomerIDList() {
        // customer IDs
        List<String> customerIDs = database.queryListOfCustomerIDs();
        String[] customerIDsArray = new String[customerIDs.size()];
        customerIDs.toArray(customerIDsArray);
        cbxCustomerID.setModel(new DefaultComboBoxModel<>(customerIDsArray));
    }

    private void loadProductIDList() {
        // product IDs (selling and not out-of-stock)
        List<String> productIDs = database.queryListOfSellingProductIDs();
        String[] productIDsArray = new String[productIDs.size()];
        productIDs.toArray(productIDsArray);
        cbxProductID.setModel(new DefaultComboBoxModel<>(productIDsArray));
    }

    private void loadLogoImage() {
        // TODO: make the logo change according to user's settings
        logoLabel.setIcon(new ImageIcon(getClass().getResource(
                Const.PATH_TO_LOGO_IMAGE)));
    }

    private void loadLoginName() {
        String user = database.queryCurrentUser();    // in "user@host" format
        user = user.substring(0, user.indexOf("@"));  // get only the username
        lblLoginName.setText("Login: " + user);
        this.setTitle(Const.WIN_TITLE_POINTOFSALE + " - " + Const.APP_TITLE
                + " | Current User: " + user);
    }

    /**
     * Update the table UI and the total price display.
     */
    private void updateShoppingListGUI() {
        table.updateUI();
        int totalQuantity = shoppingList.getTotalQuantity();
        double totalPrice = shoppingList.getTotalPrice();
        lblTotal.setText(String.format("%,d Item%s  /  Total: %,.2f " + Const.CURRENCY,
                totalQuantity, ((totalQuantity == 1) ? "" : "s"), totalPrice));
        this.updateConfirmButtonEnabled();
    }

    private void updateTableSelection(int rowIndex) {
        rowIndex = Math.min(rowIndex, table.getRowCount() - 1);
        if (rowIndex < 0) {
            return;
        }
        table.setRowSelectionInterval(rowIndex, rowIndex);
        table.scrollRectToVisible(
                new Rectangle(table.getCellRect(rowIndex, 0, true)));
    }

    private void updateConfirmButtonEnabled() {
        btnConfirm.setEnabled(currentCustomer != null && !shoppingList.isEmpty());
    }

    private void setColorTheme() {
        cbxCustomerIDTextField.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        cbxCustomerIDTextField.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);

        cbxProductIDTextField.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        cbxProductIDTextField.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);
        JFormattedTextField spnQuantityText
                = (JFormattedTextField) spnQuantity.getEditor().getComponent(0);
        spnQuantityText.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        spnQuantityText.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);

        table.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        table.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        table.setGridColor(Const.COLOR_TABLE_GRID);

        lblListAddMessage.setText("<html> <br/> </html>");
    }

    private void initListeners() {
        //<editor-fold desc="Menu Bar Items">
        menuLogout.addActionListener((ActionEvent) -> {
            if (logoutListener != null) {
                int sure = 0;
                if (!shoppingList.isEmpty()) {
                    sure = JOptionPane.showConfirmDialog(this,
                            Const.MESSAGE_POS_CONFIRM_LOGOUT,
                            "Logout", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null);
                }
                if (sure == JOptionPane.YES_OPTION) {
                    this.clear();
                    logoutListener.logout();
                }
            }
        });
        menuExit.addActionListener((ActionEvent) -> {
            int sure = 0;
            if (!shoppingList.isEmpty()) {
                sure = JOptionPane.showConfirmDialog(this,
                        Const.MESSAGE_POS_CONFIRM_EXIT,
                        "Exit", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null);
            }
            if (sure == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        menuDebug.addActionListener((ActionEvent) -> {
            if (window_debug == null) {
                window_debug = new DebugWindow();
            }
            window_debug.setVisible(true);
        });
        menuViewCurrentCustomer.addActionListener((ActionEvent) -> {
            this.showCurrentCustomerInfoWindow(InfoWindowMode.VIEW);
        });
        menuEditCurrentCustomer.addActionListener((ActionEvent) -> {
            this.showCurrentCustomerInfoWindow(InfoWindowMode.EDIT);
        });
        menuNewCustomer.addActionListener((ActionEvent) -> {
            this.showNewCustomerWindow();
        });
        menuManageCustomers.addActionListener((ActionEvent) -> {
            this.showManageCustomersWindow();
        });
        menuManageProducts.addActionListener((ActionEvent) -> {
            this.showManageProductsWindow();
        });
        menuManageCategories.addActionListener((ActionEvent) -> {
            this.showManageCategoriesWindow();
        });
        menuManageSuppliers.addActionListener((ActionEvent) -> {
            this.showManageSuppliersWindow();
        });
        menuSaleRecords.addActionListener((ActionEvent) -> {
            this.showSaleRecordsWindow();
        });
        menuSaleStats.addActionListener((ActionEvent) -> {
            this.showSaleStatsWindow();
        });
        menuOtherStats.addActionListener((ActionEvent) -> {
            this.showStatisticSelectWindow();
        });
        //</editor-fold>
        //<editor-fold desc="Customer ID Components">
        chkRegisteredCustomer.addActionListener((ActionEvent) -> {
            this.toggleRegisteredCustomer();
        });
        btnCheckCustomerID.addActionListener((ActionEvent) -> {
            this.checkCustomerID();
        });
        cbxCustomerIDTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkCustomerID();
                }
            }
        });
        cbxCustomerID.addActionListener((ActionEvent) -> {
            if (database.isConnected()) {
                this.checkCustomerID();
            }
        });
        //</editor-fold>
        //<editor-fold desc="List Manipulation Components">
        btnListAdd.addActionListener((ActionEvent) -> {
            this.shoppingListAdd();
        });
        btnListRemove.addActionListener((ActionEvent) -> {
            this.shoppingListRemove();
        });
        KeyAdapter enterKeyAdd = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    shoppingListAdd();
                }
            }
        };
        cbxProductIDTextField.addKeyListener(enterKeyAdd);
        cbxProductID.addActionListener((ActionEvent) -> {
            if (database.isConnected()) {
                this.checkProductID();
            }
        });
        JFormattedTextField spnQuantityText = (JFormattedTextField) spnQuantity.getEditor().getComponent(0);
        spnQuantityText.addKeyListener(enterKeyAdd);
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    shoppingListRemove();
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRowCount() > 0) {
                    String id = shoppingList.getItemAt(table.getSelectedRow()).getProductID();
                    cbxProductIDTextField.setText(id);
                }
            }
        });
        //</editor-fold>
        //<editor-fold desc="Confirm and Reset Buttons">
        btnConfirm.addActionListener((ActionEvent) -> {
            this.checkout();
        });
        btnClear.addActionListener((ActionEvent) -> {
            int sure = 0;
            if (!shoppingList.isEmpty()) {
                sure = JOptionPane.showConfirmDialog(this,
                        Const.MESSAGE_POS_CONFIRM_CLEAR,
                        "Point of Sale", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null);
            }
            if (sure == 0) {
                this.clear();
            }
        });
        //</editor-fold>

        MouseListener productPopup = new ProductPopupMenuListener();
        cbxProductIDTextField.addMouseListener(productPopup);
        table.addMouseListener(productPopup);

        menuViewProduct.addActionListener((ActionEvent) -> {
            Product p = this.getPopupFocusedProduct();
            if (p != null) {
                EditProductInfoWindow.showViewProductDialog(this, p);
            }
        });
        menuEditProduct.setEnabled(false);
        menuEditProduct.addActionListener((ActionEvent) -> {
            Product p = this.getPopupFocusedProduct();
            if (p != null) {
                EditProductInfoWindow.showEditProductDialog(this, p);
            }
        });
    }

    private Product getPopupFocusedProduct() {
        Component invoker = popupProduct.getInvoker();
        if (invoker == table) {
            if (table.getSelectedRow() < 0 || shoppingList.isEmpty()) {
                return null;
            }
            String productID = shoppingList.getItemAt(table.getSelectedRow()).getProductID();
            return database.queryProduct(productID);
        } else if (invoker == cbxProductIDTextField) {
            return database.queryProduct(cbxProductIDTextField.getText().trim());
        }
        return null;
    }

    private void initTableModel() {
        table.setModel(shoppingList.getTableModel());

        // setting column sizes
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        final int width_id = 80, width_numbers = 100;
        TableColumnModel colm = table.getColumnModel();

        colm.getColumn(0).setMinWidth(width_id);
        colm.getColumn(0).setMaxWidth(width_id);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(2).setMinWidth(width_numbers);
        colm.getColumn(2).setMaxWidth(width_numbers);
        colm.getColumn(2).setResizable(false);

        colm.getColumn(3).setMinWidth(width_numbers);
        colm.getColumn(3).setMaxWidth(width_numbers);
        colm.getColumn(3).setResizable(false);

        colm.getColumn(4).setMinWidth(width_numbers);
        colm.getColumn(4).setMaxWidth(width_numbers);
        colm.getColumn(4).setResizable(false);

        // set alignments
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        colm.getColumn(2).setCellRenderer(rightRenderer);
        colm.getColumn(3).setCellRenderer(rightRenderer);
        colm.getColumn(4).setCellRenderer(rightRenderer);

        table.getTableHeader().setFont(Const.FONT_DEFAULT_12);
    }

    private final JTextField cbxCustomerIDTextField;
    private final JTextField cbxProductIDTextField;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GUI Code: Automatically Generated by NetBeans">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        popupProduct = new javax.swing.JPopupMenu();
        menuViewProduct = new javax.swing.JMenuItem();
        menuEditProduct = new javax.swing.JMenuItem();
        panel_customer = new javax.swing.JPanel();
        chkRegisteredCustomer = new javax.swing.JCheckBox();
        btnCheckCustomerID = new javax.swing.JButton();
        lblCustomerName = new javax.swing.JLabel();
        cbxCustomerID = new javax.swing.JComboBox<>();
        panel_list = new javax.swing.JPanel();
        table_scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        panel_totalPrice = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        panel_controls = new javax.swing.JPanel();
        panel_logo = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        javax.swing.JSeparator separator_controls_1 = new javax.swing.JSeparator();
        lblLoginName = new javax.swing.JLabel();
        javax.swing.JSeparator separator_controls_2 = new javax.swing.JSeparator();
        javax.swing.JLabel l_AddProductID = new javax.swing.JLabel();
        cbxProductID = new javax.swing.JComboBox<>();
        javax.swing.JLabel l_AddQuantity = new javax.swing.JLabel();
        spnQuantity = new javax.swing.JSpinner();
        lblListAddMessage = new javax.swing.JLabel();
        btnListAdd = new javax.swing.JButton();
        btnListRemove = new javax.swing.JButton();
        javax.swing.JSeparator separator_controls_3 = new javax.swing.JSeparator();
        btnConfirm = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuLogout = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator6 = new javax.swing.JPopupMenu.Separator();
        menuDebug = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator7 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        menuCustomer = new javax.swing.JMenu();
        menuViewCurrentCustomer = new javax.swing.JMenuItem();
        menuEditCurrentCustomer = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuNewCustomer = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuManageCustomers = new javax.swing.JMenuItem();
        menuStore = new javax.swing.JMenu();
        menuManageProducts = new javax.swing.JMenuItem();
        menuManageCategories = new javax.swing.JMenuItem();
        menuManageSuppliers = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuSaleRecords = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuSaleStats = new javax.swing.JMenuItem();
        menuOtherStats = new javax.swing.JMenuItem();

        menuViewProduct.setText("View Product Info...");
        popupProduct.add(menuViewProduct);

        menuEditProduct.setText("Edit Product Info...");
        popupProduct.add(menuEditProduct);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        setMinimumSize(new java.awt.Dimension(840, 600));
        setPreferredSize(new java.awt.Dimension(840, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_customer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel_customer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        panel_customer.setLayout(new java.awt.GridBagLayout());

        chkRegisteredCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkRegisteredCustomer.setSelected(true);
        chkRegisteredCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        chkRegisteredCustomer.setLabel("Registered Customer, ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        panel_customer.add(chkRegisteredCustomer, gridBagConstraints);

        btnCheckCustomerID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnCheckCustomerID.setText("Check");
        btnCheckCustomerID.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCheckCustomerID.setPreferredSize(new java.awt.Dimension(64, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panel_customer.add(btnCheckCustomerID, gridBagConstraints);

        lblCustomerName.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblCustomerName.setText("FIRSTNAME LASTNAME");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        panel_customer.add(lblCustomerName, gridBagConstraints);

        cbxCustomerID.setEditable(true);
        cbxCustomerID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxCustomerID.setMaximumRowCount(16);
        cbxCustomerID.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "C0000000" }));
        cbxCustomerID.setPreferredSize(new java.awt.Dimension(108, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 8, 8);
        panel_customer.add(cbxCustomerID, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        getContentPane().add(panel_customer, gridBagConstraints);

        table_scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        table.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Product ID", "Product Name", "Quantity", "Unit Price", "Subtotal"
            }
        ));
        table.setGridColor(new java.awt.Color(204, 204, 204));
        table.setRowHeight(24);
        table.setSelectionBackground(new java.awt.Color(51, 51, 51));
        table_scrollPane.setViewportView(table);

        javax.swing.GroupLayout panel_listLayout = new javax.swing.GroupLayout(panel_list);
        panel_list.setLayout(panel_listLayout);
        panel_listLayout.setHorizontalGroup(
            panel_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(table_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
        );
        panel_listLayout.setVerticalGroup(
            panel_listLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_listLayout.createSequentialGroup()
                .addComponent(table_scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        getContentPane().add(panel_list, gridBagConstraints);

        panel_totalPrice.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel_totalPrice.setLayout(new java.awt.GridBagLayout());

        lblTotal.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal.setText("0 Items  /  Total: 0.00 ฿");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 16);
        panel_totalPrice.add(lblTotal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        getContentPane().add(panel_totalPrice, gridBagConstraints);

        panel_controls.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.disabledShadow"));
        panel_controls.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel_controls.setMaximumSize(new java.awt.Dimension(224, 2147483647));
        panel_controls.setMinimumSize(new java.awt.Dimension(224, 560));
        panel_controls.setPreferredSize(new java.awt.Dimension(224, 560));
        panel_controls.setLayout(new java.awt.GridBagLayout());

        panel_logo.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.disabledShadow"));
        panel_logo.setMaximumSize(new java.awt.Dimension(220, 150));
        panel_logo.setMinimumSize(new java.awt.Dimension(220, 150));
        panel_logo.setPreferredSize(new java.awt.Dimension(220, 150));

        logoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel.setMaximumSize(new java.awt.Dimension(220, 150));
        logoLabel.setMinimumSize(new java.awt.Dimension(220, 150));
        logoLabel.setPreferredSize(new java.awt.Dimension(220, 150));

        javax.swing.GroupLayout panel_logoLayout = new javax.swing.GroupLayout(panel_logo);
        panel_logo.setLayout(panel_logoLayout);
        panel_logoLayout.setHorizontalGroup(
            panel_logoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_logoLayout.createSequentialGroup()
                .addComponent(logoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panel_logoLayout.setVerticalGroup(
            panel_logoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        panel_controls.add(panel_logo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_controls.add(separator_controls_1, gridBagConstraints);

        lblLoginName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLoginName.setForeground(new java.awt.Color(102, 102, 102));
        lblLoginName.setText("Login: LOGIN NAME");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        panel_controls.add(lblLoginName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_controls.add(separator_controls_2, gridBagConstraints);

        l_AddProductID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_AddProductID.setText("Product ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 4);
        panel_controls.add(l_AddProductID, gridBagConstraints);

        cbxProductID.setEditable(true);
        cbxProductID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxProductID.setMaximumRowCount(16);
        cbxProductID.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "P0000000" }));
        cbxProductID.setPreferredSize(new java.awt.Dimension(108, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        panel_controls.add(cbxProductID, gridBagConstraints);

        l_AddQuantity.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_AddQuantity.setText("Quantity:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 4);
        panel_controls.add(l_AddQuantity, gridBagConstraints);

        spnQuantity.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        spnQuantity.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        panel_controls.add(spnQuantity, gridBagConstraints);

        lblListAddMessage.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblListAddMessage.setText("<html>Product:<br/>Stock Quantity:</html>");
        lblListAddMessage.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblListAddMessage.setMinimumSize(new java.awt.Dimension(78, 32));
        lblListAddMessage.setPreferredSize(new java.awt.Dimension(78, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 8);
        panel_controls.add(lblListAddMessage, gridBagConstraints);

        btnListAdd.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnListAdd.setText("Add to the List");
        btnListAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnListAdd.setPreferredSize(new java.awt.Dimension(123, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_controls.add(btnListAdd, gridBagConstraints);

        btnListRemove.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnListRemove.setText("Remove Selected");
        btnListRemove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnListRemove.setPreferredSize(new java.awt.Dimension(135, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 4);
        panel_controls.add(btnListRemove, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(24, 0, 4, 0);
        panel_controls.add(separator_controls_3, gridBagConstraints);

        btnConfirm.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnConfirm.setText("Confirm");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.setPreferredSize(new java.awt.Dimension(81, 48));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_controls.add(btnConfirm, gridBagConstraints);

        btnClear.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnClear.setText("Clear");
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClear.setPreferredSize(new java.awt.Dimension(63, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_controls.add(btnClear, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(panel_controls, gridBagConstraints);

        menuFile.setText("File");

        menuLogout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        menuLogout.setText("Logout");
        menuFile.add(menuLogout);
        menuFile.add(jSeparator6);

        menuDebug.setText("SQL Editor...");
        menuFile.add(menuDebug);
        menuFile.add(jSeparator7);

        menuExit.setText("Exit");
        menuFile.add(menuExit);

        menuBar.add(menuFile);

        menuCustomer.setText("Customer");

        menuViewCurrentCustomer.setText("View Current Customer's Information...");
        menuCustomer.add(menuViewCurrentCustomer);

        menuEditCurrentCustomer.setText("Edit Current Customer's Information...");
        menuCustomer.add(menuEditCurrentCustomer);
        menuCustomer.add(jSeparator2);

        menuNewCustomer.setLabel("Register New Customer...");
        menuCustomer.add(menuNewCustomer);
        menuCustomer.add(jSeparator1);

        menuManageCustomers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.CTRL_MASK));
        menuManageCustomers.setText("Customer List...");
        menuCustomer.add(menuManageCustomers);

        menuBar.add(menuCustomer);

        menuStore.setText("Store Management");

        menuManageProducts.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
        menuManageProducts.setText("Manage Products...");
        menuStore.add(menuManageProducts);

        menuManageCategories.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
        menuManageCategories.setText("Manage Categories...");
        menuStore.add(menuManageCategories);

        menuManageSuppliers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        menuManageSuppliers.setText("Manage Suppliers...");
        menuStore.add(menuManageSuppliers);
        menuStore.add(jSeparator4);

        menuSaleRecords.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.CTRL_MASK));
        menuSaleRecords.setText("View Sale Records...");
        menuStore.add(menuSaleRecords);
        menuStore.add(jSeparator5);

        menuSaleStats.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, java.awt.event.InputEvent.CTRL_MASK));
        menuSaleStats.setText("Sale-Related Statistics...");
        menuStore.add(menuSaleStats);

        menuOtherStats.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, java.awt.event.InputEvent.CTRL_MASK));
        menuOtherStats.setText("Other Statistics...");
        menuStore.add(menuOtherStats);

        menuBar.add(menuStore);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheckCustomerID;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnListAdd;
    private javax.swing.JButton btnListRemove;
    private javax.swing.JComboBox<String> cbxCustomerID;
    private javax.swing.JComboBox<String> cbxProductID;
    private javax.swing.JCheckBox chkRegisteredCustomer;
    private javax.swing.JLabel lblCustomerName;
    private javax.swing.JLabel lblListAddMessage;
    private javax.swing.JLabel lblLoginName;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuCustomer;
    private javax.swing.JMenuItem menuDebug;
    private javax.swing.JMenuItem menuEditCurrentCustomer;
    private javax.swing.JMenuItem menuEditProduct;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuLogout;
    private javax.swing.JMenuItem menuManageCategories;
    private javax.swing.JMenuItem menuManageCustomers;
    private javax.swing.JMenuItem menuManageProducts;
    private javax.swing.JMenuItem menuManageSuppliers;
    private javax.swing.JMenuItem menuNewCustomer;
    private javax.swing.JMenuItem menuOtherStats;
    private javax.swing.JMenuItem menuSaleRecords;
    private javax.swing.JMenuItem menuSaleStats;
    private javax.swing.JMenu menuStore;
    private javax.swing.JMenuItem menuViewCurrentCustomer;
    private javax.swing.JMenuItem menuViewProduct;
    private javax.swing.JPanel panel_controls;
    private javax.swing.JPanel panel_customer;
    private javax.swing.JPanel panel_list;
    private javax.swing.JPanel panel_logo;
    private javax.swing.JPanel panel_totalPrice;
    private javax.swing.JPopupMenu popupProduct;
    private javax.swing.JSpinner spnQuantity;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane table_scrollPane;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    public interface LogoutListener {

        /**
         * Called when the user logs out. The application should disconnect from
         * the database and return to the login window.
         */
        void logout();
    }

    private class ProductPopupMenuListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupProduct.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

}
