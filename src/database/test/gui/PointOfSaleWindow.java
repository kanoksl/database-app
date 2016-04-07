package database.test.gui;

import database.test.DatabaseManager;
import database.test.data.ShoppingList;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

public class PointOfSaleWindow
        extends javax.swing.JFrame {

    private DatabaseManager database = null;
    private LogoutListener logoutListener = null;

    private final ShoppingList shoppingList = new ShoppingList();

    private String validCustomerID = null;

    private static final String UNREGISTERED_CUSTOMER_ID = "C0000000";
    private static final String UNREGISTERED_CUSTOMER_NAME = "Unregistered Customer";

    /**
     * Create a new PointOfSaleWindow that is connected to the given database.
     *
     * @param logoutListener The object to be notified if the user log out.
     * @param database The retail store database.
     */
    public PointOfSaleWindow(LogoutListener logoutListener, DatabaseManager database) {
        // initializes the components
        this.initComponents();
        this.initListeners();
        this.initTableModel();
        this.setLocationRelativeTo(null);
        this.setColorTheme();

        this.database = database;
        this.logoutListener = logoutListener;

        this.setTitle(Const.WIN_TITLE_POINTOFSALE + " - " + Const.APP_TITLE);
        this.setLogo();
        this.clear();
    }

    public void populateComboBoxData() {
        // TODO: query the database for all customer IDs and product IDs
        // TODO: change the customer ID and product ID textboxes to comboboxes
    }

    //<editor-fold defaultstate="collapsed" desc="GUI + Data Code: Customer ID Checking">
    private void toggleRegisteredCustomer() {
        if (chkRegisteredCustomer.isSelected()) {
            // registered customer; input the ID and check
            tbxCustomerID.setText("");
            lblCustomerName.setText(" ");
            lblCustomerName.setForeground(Color.BLACK);
            validCustomerID = null;

            tbxCustomerID.setEnabled(true);
            btnCheckCustomerID.setEnabled(true);

            tbxCustomerID.requestFocus();
        } else {
            // unregistered customer; use the special ID
            tbxCustomerID.setText(UNREGISTERED_CUSTOMER_ID);
            lblCustomerName.setText(UNREGISTERED_CUSTOMER_NAME);
            lblCustomerName.setForeground(Color.DARK_GRAY);
            validCustomerID = UNREGISTERED_CUSTOMER_ID;

            tbxCustomerID.setEnabled(false);
            btnCheckCustomerID.setEnabled(false);
        }
    }

    private void checkCustomerID() {
        String id = tbxCustomerID.getText().trim();
        boolean exists = false;

        // TODO: query the database for the entered customer ID
        if (id.equals("0")) {
            exists = true;
        }
        String fname = "Charlie";
        String lname = "Franklyn";
        char gender = 'M';
        // ============================================

        // display the customer name
        if (exists) {
            validCustomerID = id;
            String displayName = String.format("%s%s %s", (gender == 'M')
                    ? "Mr. " : (gender == 'F') ? "Ms. " : "", fname, lname);
            lblCustomerName.setText(displayName);
            lblCustomerName.setForeground(Color.BLACK);
            menuCustomerCurrent.setText("Current Customer's Information... (" + displayName + ")");
            menuCustomerCurrent.setEnabled(true);
        } else {
            validCustomerID = null;
            lblCustomerName.setText("Error: invalid customer ID");
            lblCustomerName.setForeground(Const.COLOR_ERROR_TEXT);
            menuCustomerCurrent.setText("Current Customer's Information... (?)");
            menuCustomerCurrent.setEnabled(false);
        }
        this.updateConfirmButtonEnabled();
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GUI + Data Code: List Manipulation">
    /**
     * Add a new entry to the shopping list. If the product to be added already
     * exists in the list, add the quantity to the existing ProductLine.
     */
    private void listAdd() {
        String id = tbxProductID.getText().trim();
        int quantity = (int) spnQuantity.getValue();
        // TODO: find a way to deal with negative quantity
        try {
            int idx = shoppingList.addItem(id, quantity);
            // update the GUI
            this.updateScreen();
            this.updateTableSelection(idx);
            lblListAddMessage.setText("Done.");
            lblListAddMessage.setForeground(Color.BLACK);
        } catch (IllegalArgumentException ex) {
            lblListAddMessage.setText("Adding failed. Invalid product ID.");
            lblListAddMessage.setForeground(Const.COLOR_ERROR_TEXT);
        }

        // clear the product input
        tbxProductID.setSelectionStart(0);
        tbxProductID.setSelectionEnd(Integer.MAX_VALUE);
        spnQuantity.setValue(1);
        tbxProductID.requestFocus();
    }

    private void listRemove() {
        if (table.getSelectedRowCount() == 0 || shoppingList.isEmpty()) {
            return;
        }

        int[] selected = table.getSelectedRows();
        for (int i = selected.length - 1; i >= 0; i--) {
            shoppingList.removeItem(selected[i]);
        }

        this.updateScreen();
        this.updateTableSelection(Integer.MAX_VALUE);
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    /**
     * Clear all the data in the window.
     */
    public void clear() {
        tbxCustomerID.setText("");
        tbxProductID.setText("");
        spnQuantity.setValue(1);
        tbxCustomerID.requestFocus();

        shoppingList.clear();
        this.updateScreen();
    }

    private void setLogo() {
        // TODO: make the logo change according to user's settings
        logoLabel.setIcon(new ImageIcon(getClass().getResource(
                Const.PATH_TO_LOGO_IMAGE)));
    }

    public void setLoginNameDisplay() {
        String user = database.queryCurrentUser();    // in "user@host" format
        user = user.substring(0, user.indexOf("@"));  // get only the username
        this.lblLoginName.setText("Login: " + user);
        this.setTitle(Const.WIN_TITLE_POINTOFSALE + " - " + Const.APP_TITLE
                + " | Current User: " + user);
    }

    /**
     * Update the table UI and the total price display.
     */
    private void updateScreen() {
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
        btnConfirm.setEnabled(validCustomerID != null && !shoppingList.isEmpty());
    }

    private void setColorTheme() {
        tbxCustomerID.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        tbxCustomerID.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);

        tbxProductID.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        tbxProductID.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);
        JFormattedTextField spnQuantityText
                = (JFormattedTextField) spnQuantity.getEditor().getComponent(0);
        spnQuantityText.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        spnQuantityText.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);

        table.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        table.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        table.setGridColor(Const.COLOR_TABLE_GRID);
    }

    private void initListeners() {
        //<editor-fold desc="Menu Bar Items">
        menuLogout.addActionListener((ActionEvent) -> {
            if (logoutListener != null) {
                this.clear();
                logoutListener.logout();
            }
        });
        menuCustomerCurrent.addActionListener((ActionEvent) -> {
            EditCustomerInfoWindow win = new EditCustomerInfoWindow();
            win.setVisible(true);
        });
        //</editor-fold>
        //<editor-fold desc="Customer ID Components">
        chkRegisteredCustomer.addActionListener((ActionEvent) -> {
            toggleRegisteredCustomer();
        });
        btnCheckCustomerID.addActionListener((ActionEvent) -> {
            checkCustomerID();
        });
        tbxCustomerID.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkCustomerID();
                }
            }
        });
        //</editor-fold>
        //<editor-fold desc="List Manipulation Components">
        btnListAdd.addActionListener((ActionEvent) -> {
            listAdd();
        });
        btnListRemove.addActionListener((ActionEvent) -> {
            listRemove();
        });
        KeyAdapter enterKeyAdd = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    listAdd();
                }
            }
        };
        tbxProductID.addKeyListener(enterKeyAdd);
        JFormattedTextField spnQuantityText
                = (JFormattedTextField) spnQuantity.getEditor().getComponent(0);
        spnQuantityText.addKeyListener(enterKeyAdd);
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    listRemove();
                }
            }
        });
        // TODO: double-clicking a row in the table set the product ID in the textbox
        //</editor-fold>
        //<editor-fold desc="Confirm and Reset Buttons">
        btnConfirm.addActionListener((ActionEvent) -> {
            // TODO: confirm sale
        });
        btnClear.addActionListener((ActionEvent) -> {
            this.clear();
        });
        //</editor-fold>
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

        panel_customer = new javax.swing.JPanel();
        chkRegisteredCustomer = new javax.swing.JCheckBox();
        tbxCustomerID = new javax.swing.JTextField();
        btnCheckCustomerID = new javax.swing.JButton();
        lblCustomerName = new javax.swing.JLabel();
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
        tbxProductID = new javax.swing.JTextField();
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
        menuCustomer = new javax.swing.JMenu();
        menuCustomerCurrent = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuProduct = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        menuSupplier = new javax.swing.JMenu();

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
        chkRegisteredCustomer.setText("Registered Customer ID:");
        chkRegisteredCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        panel_customer.add(chkRegisteredCustomer, gridBagConstraints);

        tbxCustomerID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxCustomerID.setPreferredSize(new java.awt.Dimension(96, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 8, 8);
        panel_customer.add(tbxCustomerID, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
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
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
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

        tbxProductID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxProductID.setPreferredSize(new java.awt.Dimension(6, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        panel_controls.add(tbxProductID, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        panel_controls.add(spnQuantity, gridBagConstraints);

        lblListAddMessage.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblListAddMessage.setText("Adding failed. Invalid product ID.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 4);
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
        gridBagConstraints.insets = new java.awt.Insets(24, 4, 4, 4);
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

        menuLogout.setText("Logout");
        menuFile.add(menuLogout);

        menuBar.add(menuFile);

        menuCustomer.setText("Customer");

        menuCustomerCurrent.setText("Current Customer's Information... (FIRSTNAME LASTNAME)");
        menuCustomer.add(menuCustomerCurrent);
        menuCustomer.add(jSeparator1);

        jMenuItem1.setText("New Member Register...");
        menuCustomer.add(jMenuItem1);

        menuBar.add(menuCustomer);

        menuProduct.setText("Product");

        jMenuItem2.setText("Product List...");
        menuProduct.add(jMenuItem2);

        menuBar.add(menuProduct);

        menuSupplier.setText("Supplier");
        menuBar.add(menuSupplier);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheckCustomerID;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnListAdd;
    private javax.swing.JButton btnListRemove;
    private javax.swing.JCheckBox chkRegisteredCustomer;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel lblCustomerName;
    private javax.swing.JLabel lblListAddMessage;
    private javax.swing.JLabel lblLoginName;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuCustomer;
    private javax.swing.JMenuItem menuCustomerCurrent;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuLogout;
    private javax.swing.JMenu menuProduct;
    private javax.swing.JMenu menuSupplier;
    private javax.swing.JPanel panel_controls;
    private javax.swing.JPanel panel_customer;
    private javax.swing.JPanel panel_list;
    private javax.swing.JPanel panel_logo;
    private javax.swing.JPanel panel_totalPrice;
    private javax.swing.JSpinner spnQuantity;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane table_scrollPane;
    private javax.swing.JTextField tbxCustomerID;
    private javax.swing.JTextField tbxProductID;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    public interface LogoutListener {

        /**
         * Called when the user logs out. The application should disconnect from
         * the database and return to the login window.
         */
        void logout();
    }
}
