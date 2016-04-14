package database.test.gui;

public class EditProductInfoWindow
        extends javax.swing.JFrame {

    /**
     * Creates new form EditCustomerInfoWindow.
     */
    public EditProductInfoWindow() {
        this.initComponents();
        this.initListeners();
        this.setLocationRelativeTo(null);
        this.setColorTheme();

        this.setTitle("" + " - " + Const.APP_TITLE);
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    private void setColorTheme() {

    }

    private void initListeners() {

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

        panel_header = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        tabbedPane = new javax.swing.JTabbedPane();
        panel_productInfo = new javax.swing.JPanel();
        panel_basic = new javax.swing.JPanel();
        javax.swing.JLabel l_prodID = new javax.swing.JLabel();
        tbxProductID = new javax.swing.JTextField();
        lblWarnID = new javax.swing.JLabel();
        javax.swing.JLabel l_prodName = new javax.swing.JLabel();
        tbxProductName = new javax.swing.JFormattedTextField();
        lblWarnName = new javax.swing.JLabel();
        javax.swing.JLabel l_cat = new javax.swing.JLabel();
        cbxCategory = new javax.swing.JComboBox<>();
        btnViewCategory = new javax.swing.JButton();
        panel_desc = new javax.swing.JPanel();
        txtDescription_scrollPane = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        panel_pricing = new javax.swing.JPanel();
        javax.swing.JLabel l_curPrice = new javax.swing.JLabel();
        lblCurrentPrice = new javax.swing.JLabel();
        javax.swing.JLabel l_newPrice = new javax.swing.JLabel();
        spnPrice = new javax.swing.JSpinner();
        panel_stock = new javax.swing.JPanel();
        javax.swing.JLabel l_curStock = new javax.swing.JLabel();
        javax.swing.JLabel lblCurrentStock = new javax.swing.JLabel();
        javax.swing.JLabel l_newStock = new javax.swing.JLabel();
        spnStock = new javax.swing.JSpinner();
        panel_suppliers = new javax.swing.JPanel();
        tableSuppliers_scrollPane = new javax.swing.JScrollPane();
        tableSuppliers = new javax.swing.JTable();
        btnSupplierView = new javax.swing.JButton();
        btnSupplierAdd = new javax.swing.JButton();
        btnSupplierDelete = new javax.swing.JButton();
        chkDiscontinued = new javax.swing.JCheckBox();
        panel_commandButtons = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        panel_pricingHistory = new javax.swing.JPanel();
        lblPriceSubheader = new javax.swing.JLabel();
        tablePrice_scrollPane = new javax.swing.JScrollPane();
        tablePrice = new javax.swing.JTable();
        panel_bottom_price = new javax.swing.JPanel();
        lblPriceStats = new javax.swing.JLabel();
        panel_sellingHistory = new javax.swing.JPanel();
        lblSellingSubheader = new javax.swing.JLabel();
        tableSells_scrollPane = new javax.swing.JScrollPane();
        tableSells = new javax.swing.JTable();
        panel_bottom_sells = new javax.swing.JPanel();
        lblSellingStats = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(940, 600));
        setMinimumSize(new java.awt.Dimension(940, 600));
        setPreferredSize(new java.awt.Dimension(940, 600));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Add/View/Edit Product Information");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 8, 16);
        panel_header.add(headerLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(panel_header, gridBagConstraints);

        tabbedPane.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N

        panel_productInfo.setLayout(new java.awt.GridBagLayout());

        panel_basic.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Basic Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_basic.setLayout(new java.awt.GridBagLayout());

        l_prodID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_prodID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_prodID.setText("Product ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 20, 4, 4);
        panel_basic.add(l_prodID, gridBagConstraints);

        tbxProductID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxProductID.setEnabled(false);
        tbxProductID.setMinimumSize(new java.awt.Dimension(140, 22));
        tbxProductID.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 2, 4);
        panel_basic.add(tbxProductID, gridBagConstraints);

        lblWarnID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblWarnID.setForeground(java.awt.Color.red);
        lblWarnID.setText("This ID is already used.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 4);
        panel_basic.add(lblWarnID, gridBagConstraints);

        l_prodName.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_prodName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_prodName.setText("Product Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 20, 2, 4);
        panel_basic.add(l_prodName, gridBagConstraints);

        try {
            tbxProductName.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("****************************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        tbxProductName.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        tbxProductName.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxProductName.setMaximumSize(new java.awt.Dimension(188, 22));
        tbxProductName.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxProductName.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 2, 4);
        panel_basic.add(tbxProductName, gridBagConstraints);

        lblWarnName.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblWarnName.setForeground(java.awt.Color.red);
        lblWarnName.setText("The product name cannot be blank.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 16, 0);
        panel_basic.add(lblWarnName, gridBagConstraints);

        l_cat.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_cat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_cat.setText("Category:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 20, 8, 4);
        panel_basic.add(l_cat, gridBagConstraints);

        cbxCategory.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxCategory.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        panel_basic.add(cbxCategory, gridBagConstraints);

        btnViewCategory.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnViewCategory.setText("View Categories...");
        btnViewCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewCategory.setMaximumSize(new java.awt.Dimension(128, 26));
        btnViewCategory.setMinimumSize(new java.awt.Dimension(128, 26));
        btnViewCategory.setName(""); // NOI18N
        btnViewCategory.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        panel_basic.add(btnViewCategory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 0);
        panel_productInfo.add(panel_basic, gridBagConstraints);

        panel_desc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Description", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_desc.setLayout(new java.awt.GridBagLayout());

        txtDescription_scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        txtDescription.setRows(5);
        txtDescription_scrollPane.setViewportView(txtDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 4, 4, 4);
        panel_desc.add(txtDescription_scrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 0);
        panel_productInfo.add(panel_desc, gridBagConstraints);

        panel_pricing.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pricing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_pricing.setLayout(new java.awt.GridBagLayout());

        l_curPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        l_curPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_curPrice.setText("Current Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        panel_pricing.add(l_curPrice, gridBagConstraints);

        lblCurrentPrice.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCurrentPrice.setText("0.00 ฿");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 8);
        panel_pricing.add(lblCurrentPrice, gridBagConstraints);

        l_newPrice.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_newPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_newPrice.setText("New Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        panel_pricing.add(l_newPrice, gridBagConstraints);

        spnPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        spnPrice.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 5.0d));
        spnPrice.setEditor(new javax.swing.JSpinner.NumberEditor(spnPrice, "0.00"));
        spnPrice.setMinimumSize(new java.awt.Dimension(64, 26));
        spnPrice.setPreferredSize(new java.awt.Dimension(96, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_pricing.add(spnPrice, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 0, 8);
        panel_productInfo.add(panel_pricing, gridBagConstraints);

        panel_stock.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Stock Quantity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_stock.setLayout(new java.awt.GridBagLayout());

        l_curStock.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        l_curStock.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_curStock.setText("Current Amount In Stock:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        panel_stock.add(l_curStock, gridBagConstraints);

        lblCurrentStock.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCurrentStock.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 8);
        panel_stock.add(lblCurrentStock, gridBagConstraints);

        l_newStock.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_newStock.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_newStock.setText("New Amount:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        panel_stock.add(l_newStock, gridBagConstraints);

        spnStock.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        spnStock.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spnStock.setMinimumSize(new java.awt.Dimension(64, 26));
        spnStock.setPreferredSize(new java.awt.Dimension(96, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_stock.add(spnStock, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 0, 8);
        panel_productInfo.add(panel_stock, gridBagConstraints);

        panel_suppliers.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Suppliers", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_suppliers.setLayout(new java.awt.GridBagLayout());

        tableSuppliers.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableSuppliers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Supplier ID", "Supplier Name"
            }
        ));
        tableSuppliers.setGridColor(new java.awt.Color(204, 204, 204));
        tableSuppliers.setRowHeight(20);
        tableSuppliers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableSuppliers_scrollPane.setViewportView(tableSuppliers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_suppliers.add(tableSuppliers_scrollPane, gridBagConstraints);

        btnSupplierView.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnSupplierView.setText("View Selected...");
        btnSupplierView.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSupplierView.setMaximumSize(new java.awt.Dimension(128, 26));
        btnSupplierView.setMinimumSize(new java.awt.Dimension(128, 26));
        btnSupplierView.setName(""); // NOI18N
        btnSupplierView.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_suppliers.add(btnSupplierView, gridBagConstraints);

        btnSupplierAdd.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnSupplierAdd.setText("Add Supplier...");
        btnSupplierAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSupplierAdd.setMaximumSize(new java.awt.Dimension(128, 26));
        btnSupplierAdd.setMinimumSize(new java.awt.Dimension(128, 26));
        btnSupplierAdd.setName(""); // NOI18N
        btnSupplierAdd.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_suppliers.add(btnSupplierAdd, gridBagConstraints);

        btnSupplierDelete.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnSupplierDelete.setText("Delete Selected");
        btnSupplierDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSupplierDelete.setMaximumSize(new java.awt.Dimension(128, 26));
        btnSupplierDelete.setMinimumSize(new java.awt.Dimension(128, 26));
        btnSupplierDelete.setName(""); // NOI18N
        btnSupplierDelete.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_suppliers.add(btnSupplierDelete, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 0, 8);
        panel_productInfo.add(panel_suppliers, gridBagConstraints);

        chkDiscontinued.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkDiscontinued.setSelected(true);
        chkDiscontinued.setText("This product is no longer being sold (discontinued)");
        chkDiscontinued.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        chkDiscontinued.setPreferredSize(new java.awt.Dimension(188, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 12, 12, 0);
        panel_productInfo.add(chkDiscontinued, gridBagConstraints);

        btnSave.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnSave.setText("Save");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.setMaximumSize(new java.awt.Dimension(128, 36));
        btnSave.setMinimumSize(new java.awt.Dimension(128, 36));
        btnSave.setName(""); // NOI18N
        btnSave.setPreferredSize(new java.awt.Dimension(96, 28));

        btnCancel.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.setMaximumSize(new java.awt.Dimension(128, 36));
        btnCancel.setMinimumSize(new java.awt.Dimension(128, 36));
        btnCancel.setName(""); // NOI18N
        btnCancel.setPreferredSize(new java.awt.Dimension(96, 28));

        javax.swing.GroupLayout panel_commandButtonsLayout = new javax.swing.GroupLayout(panel_commandButtons);
        panel_commandButtons.setLayout(panel_commandButtonsLayout);
        panel_commandButtonsLayout.setHorizontalGroup(
            panel_commandButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_commandButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panel_commandButtonsLayout.setVerticalGroup(
            panel_commandButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_commandButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_commandButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        panel_productInfo.add(panel_commandButtons, gridBagConstraints);

        tabbedPane.addTab("Product Information", panel_productInfo);

        panel_pricingHistory.setLayout(new java.awt.GridBagLayout());

        lblPriceSubheader.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblPriceSubheader.setText("PRODUCT NAME (PRODUCTID)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        panel_pricingHistory.add(lblPriceSubheader, gridBagConstraints);

        tablePrice.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tablePrice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Start Date", "End Date", "Unit Price"
            }
        ));
        tablePrice.setGridColor(new java.awt.Color(204, 204, 204));
        tablePrice.setRowHeight(20);
        tablePrice_scrollPane.setViewportView(tablePrice);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        panel_pricingHistory.add(tablePrice_scrollPane, gridBagConstraints);

        panel_bottom_price.setLayout(new java.awt.GridBagLayout());

        lblPriceStats.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblPriceStats.setText("The price of this product has been changed 0 times.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        panel_bottom_price.add(lblPriceStats, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        panel_pricingHistory.add(panel_bottom_price, gridBagConstraints);

        tabbedPane.addTab("Pricing History", panel_pricingHistory);

        panel_sellingHistory.setLayout(new java.awt.GridBagLayout());

        lblSellingSubheader.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblSellingSubheader.setText("PRODUCT NAME (PRODUCTID)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        panel_sellingHistory.add(lblSellingSubheader, gridBagConstraints);

        tableSells.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableSells.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Sale ID", "Date/Time", "Customer ID", "Customer Name", "Quantity", "Unit Price", "Subtotal"
            }
        ));
        tableSells.setGridColor(new java.awt.Color(204, 204, 204));
        tableSells.setRowHeight(20);
        tableSells_scrollPane.setViewportView(tableSells);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        panel_sellingHistory.add(tableSells_scrollPane, gridBagConstraints);

        panel_bottom_sells.setLayout(new java.awt.GridBagLayout());

        lblSellingStats.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblSellingStats.setText("This product has been purchased 0 times for a total of 0 units. Total income: 0 ฿.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        panel_bottom_sells.add(lblSellingStats, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        panel_sellingHistory.add(panel_bottom_sells, gridBagConstraints);

        tabbedPane.addTab("Selling History", panel_sellingHistory);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 4, 4);
        getContentPane().add(tabbedPane, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSupplierAdd;
    private javax.swing.JButton btnSupplierDelete;
    private javax.swing.JButton btnSupplierView;
    private javax.swing.JButton btnViewCategory;
    private javax.swing.JComboBox<String> cbxCategory;
    private javax.swing.JCheckBox chkDiscontinued;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel lblCurrentPrice;
    private javax.swing.JLabel lblPriceStats;
    private javax.swing.JLabel lblPriceSubheader;
    private javax.swing.JLabel lblSellingStats;
    private javax.swing.JLabel lblSellingSubheader;
    private javax.swing.JLabel lblWarnID;
    private javax.swing.JLabel lblWarnName;
    private javax.swing.JPanel panel_basic;
    private javax.swing.JPanel panel_bottom_price;
    private javax.swing.JPanel panel_bottom_sells;
    private javax.swing.JPanel panel_commandButtons;
    private javax.swing.JPanel panel_desc;
    private javax.swing.JPanel panel_header;
    private javax.swing.JPanel panel_pricing;
    private javax.swing.JPanel panel_pricingHistory;
    private javax.swing.JPanel panel_productInfo;
    private javax.swing.JPanel panel_sellingHistory;
    private javax.swing.JPanel panel_stock;
    private javax.swing.JPanel panel_suppliers;
    private javax.swing.JSpinner spnPrice;
    private javax.swing.JSpinner spnStock;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tablePrice;
    private javax.swing.JScrollPane tablePrice_scrollPane;
    private javax.swing.JTable tableSells;
    private javax.swing.JScrollPane tableSells_scrollPane;
    private javax.swing.JTable tableSuppliers;
    private javax.swing.JScrollPane tableSuppliers_scrollPane;
    private javax.swing.JTextField tbxProductID;
    private javax.swing.JFormattedTextField tbxProductName;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JScrollPane txtDescription_scrollPane;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
