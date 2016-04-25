package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.data.Product;
import database.test.data.Supplier;
import database.test.gui.Const.InfoWindowMode;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

public class EditProductInfoWindow
        extends javax.swing.JFrame {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();

    private Product product = null;
    private Product trueProduct = null;

    private List<Supplier> suppliers = new ArrayList<>();
    private boolean suppliersChanged = false;

    /**
     * Creates new form EditCustomerInfoWindow.
     *
     * @param mode
     */
    public EditProductInfoWindow(InfoWindowMode mode) {
        this.initComponents();
        this.initializeBase();
        this.setColorTheme();

        switch (mode) {
            case ADD:
                this.initializeAddMode();
                break;
            case EDIT:
                this.initializeEditMode();
                break;
            case VIEW:
                this.initializeViewMode();
                break;
            default:
                break;
        }

        btnSupplierAdd.addActionListener((ActionEvent) -> {
            this.productSupplierAdd();
        });
        btnSupplierRemove.addActionListener((ActionEvent) -> {
            this.productSupplierRemove();
        });
        btnSupplierView.addActionListener((ActionEvent) -> {
            this.productSupplierView();
        });
    }

    private void setProduct(Product product) {
        this.product = product;
        this.trueProduct = database.queryProduct(product.getID());
        this.populateFormData();
        if (cbxProductID.isVisible()) {
            cbxProductID.setSelectedItem(product.getID());
        }
    }

    private void populateFormData() {
        tbxProductID.setText(product.getID());
        tbxProductName.setValue(product.getName());
        cbxCategory.setSelectedItem(product.getCategoryID());
        txtDescription.setText(product.getDescription());

        if (trueProduct != null) {
            lblCurrentPrice.setText(trueProduct.getCurrentPriceString());
            lblCurrentStock.setText(String.format("%,d", trueProduct.getStockQuantity()));
        }
        spnPrice.setValue(product.getCurrentPrice());
        spnStock.setValue(product.getStockQuantity());

        chkDiscontinued.setSelected(!product.isSelling());

        this.loadSuppliers();

        lblPriceSubheader.setText(product.shortDescription());
        this.loadPricingHistory();
        lblSellingSubheader.setText(product.shortDescription());

        // TODO: selling history
        this.loadSellingHistory();
    }

    private void collectFormData() {
        product.setID(tbxProductID.getText().trim());
        product.setName(tbxProductName.getText().trim());
        product.setCategoryID(cbxCategory.getSelectedItem().toString());
        product.setDescription(txtDescription.getText());

        product.setStockQuantity((int) spnStock.getValue());

        double newPrice = (Double) spnPrice.getValue();
        double oldPrice = (trueProduct == null) ? -1 : trueProduct.getCurrentPrice();
        if (newPrice != oldPrice) {
            System.out.println("--product price changed");
            product.setPriceChanged(true);
            product.setCurrentPrice(newPrice);
        }

        product.setSelling(!chkDiscontinued.isSelected());
    }

    private void productSupplierAdd() {
        List<String> add = SelectorWindow.showSupplierSelectorDialog(this);
        if (add != null) {
            for (String id : add) {
                boolean dupe = false;
                for (Supplier s : suppliers) {
                    if (s.getID().equals(id)) {
                        dupe = true;
                        break;
                    }
                }
                if (!dupe) {
                    suppliers.add(database.querySupplier(id));
                    suppliersChanged = true;
                }
            }
            tableSuppliers.updateUI();
        }
    }

    private void productSupplierRemove() {
        if (tableSuppliers.getSelectedRowCount() == 0 || suppliers.isEmpty()
                || tableSuppliers.getSelectedRow() >= suppliers.size()) {
            return;
        }
        suppliers.remove(tableSuppliers.getSelectedRow());
        suppliersChanged = true;
        tableSuppliers.updateUI();
    }

    private void productSupplierView() {
        if (tableSuppliers.getSelectedRowCount() == 0 || suppliers.isEmpty()
                || tableSuppliers.getSelectedRow() >= suppliers.size()) {
            return;
        }
        EditSupplierInfoWindow.showViewSupplierDialog(this,
                suppliers.get(tableSuppliers.getSelectedRow()));
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    private void initializeAddMode() {
        headerLabel.setText(Const.EPIW_HEADER_ADD);
        tabbedPane.remove(2); // selling history
        tabbedPane.remove(1); // pricing history
        cbxProductID.setVisible(false);
        cbxProductID.setEnabled(false);

        // listeners
        btnSave.addActionListener((ActionEvent) -> {
            this.collectFormData();
            if (database.tryInsertProduct(product, suppliers, this)) {
                SwingUtilities.getWindowAncestor(btnSave).dispose();
            }
        });
        btnCancel.addActionListener((ActionEvent) -> {
            product = null;
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });
    }

    private void initializeEditMode() {
        headerLabel.setText(Const.EPIW_HEADER_EDIT);
        tabbedPane.remove(2); // selling history
        cbxProductID.setVisible(false);
        cbxProductID.setEnabled(false);

        // listeners
        btnSave.addActionListener((ActionEvent) -> {
            this.collectFormData();
            if (database.tryUpdateProduct(product, suppliersChanged ? suppliers : null, this)) {
                SwingUtilities.getWindowAncestor(btnSave).dispose();
            }
        });
        btnCancel.addActionListener((ActionEvent) -> {
            product = database.queryProduct(product.getID());
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });
    }

    private void initializeViewMode() {
        headerLabel.setText(Const.EPIW_HEADER_VIEW);

        tbxProductName.setEditable(false);
        cbxCategory.setEnabled(false);
        txtDescription.setEditable(false);
        chkDiscontinued.setEnabled(false);
        spnPrice.setEnabled(false);
        spnStock.setEnabled(false);
        btnSupplierAdd.setEnabled(false);
        btnSupplierRemove.setEnabled(false);

        btnSave.setEnabled(false);
        btnSave.setVisible(false);
        btnCancel.setText("Close");
        btnCancel.addActionListener((ActionEvent) -> {
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });

        cbxProductID.setVisible(true);
        cbxProductID.setEnabled(true);
        List<String> productIDs = database.queryListOfAllProductIDs();
        String[] productIDsArray = new String[productIDs.size()];
        productIDs.toArray(productIDsArray);
        cbxProductID.setModel(new DefaultComboBoxModel<>(productIDsArray));
        cbxProductID.addActionListener((ActionEvent) -> {
            this.setProduct(database.queryProduct(cbxProductID.getSelectedItem().toString()));
        });
    }

    private void setColorTheme() {
        tablePrice.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tablePrice.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tablePrice.setGridColor(Const.COLOR_TABLE_GRID);
        tablePrice.setFont(Const.FONT_DEFAULT_12);
        tablePrice.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tablePrice.setRowHeight(24);

        tableSells.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tableSells.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tableSells.setGridColor(Const.COLOR_TABLE_GRID);
        tableSells.setFont(Const.FONT_DEFAULT_12);
        tableSells.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tableSells.setRowHeight(24);

        tableSuppliers.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tableSuppliers.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tableSuppliers.setGridColor(Const.COLOR_TABLE_GRID);
        tableSuppliers.setFont(Const.FONT_DEFAULT_12);
        tableSuppliers.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tableSuppliers.setRowHeight(24);
    }

    private void initializeBase() {
        lblWarnID.setText(" ");
        lblWarnName.setText(" ");

        tbxProductName.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                tbxProductName.setCaretPosition(tbxProductName.getText().trim().length());
            }
        });

        this.loadCategoryList();
    }

    private void loadCategoryList() {
        List<String> categoryIDs = database.queryListOfCategoryIDs();
        String[] categoryIDsArray = new String[categoryIDs.size()];
        categoryIDs.toArray(categoryIDsArray);
        cbxCategory.setModel(new DefaultComboBoxModel<>(categoryIDsArray));
    }

    private void loadPricingHistory() {
        List<Object[]> pricingList = database.queryProductPricingHistory(product.getID());
        if (pricingList.isEmpty()) {
            return;
        }

        Object[] lastRow = pricingList.get(pricingList.size() - 1);
        if (lastRow[0].equals(LocalDate.now().plusDays(1).toString())) {
            spnPrice.setValue((Double) lastRow[3]);
        }

        for (Object[] row : pricingList) {
            boolean isMinDate = row[0].toString().equals("1000-01-01");
            boolean isMaxDate = row[1].toString().equals("9999-12-31");
            row[0] = isMinDate ? "BEGINNING" : row[0].toString();
            row[1] = isMaxDate ? "UNSPECIFIED" : row[1].toString();
            row[2] = (isMinDate || isMaxDate) ? "-" : String.format("%,d days", row[2]);
            row[3] = String.format("%,.2f " + Const.CURRENCY, (Double) row[3]);
        }

        tablePrice.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Start Date", "End Date", "Duration", "Unit Price"};

            @Override
            public int getRowCount() {
                return pricingList.size();
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return pricingList.get(rowIndex)[columnIndex];
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }
        });
        tablePrice.updateUI();
        lblPriceStats.setText("The price of this product has been changed "
                + (pricingList.size() - 1) + " times.");
    }

    private void loadSellingHistory() {
        List<Object[]> history = database.queryProductSellingHistory(product.getID());

        tableSells.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Sale ID", "Date/Time", "Customer ID",
                "Customer Name", "Quantity", "Unit Price", "Subtotal"};

            @Override
            public int getRowCount() {
                return history.size();
            }

            @Override
            public int getColumnCount() {
                return 7;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return history.get(rowIndex)[columnIndex];
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return Integer.class;
                }
                if (columnIndex >= 5) {
                    return Double.class;
                } else {
                    return String.class;
                }
            }
        });

        // setting column headers and sizes
        tableSells.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel colm = tableSells.getColumnModel();

        colm.getColumn(0).setMinWidth(100);
        colm.getColumn(0).setMaxWidth(100);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(1).setMinWidth(120);
        colm.getColumn(1).setMaxWidth(120);
        colm.getColumn(1).setResizable(false);

        colm.getColumn(2).setMinWidth(90);
        colm.getColumn(2).setMaxWidth(90);
        colm.getColumn(2).setResizable(false);

        colm.getColumn(4).setCellRenderer(Util.TABLE_CELL_INTEGER);
        colm.getColumn(5).setCellRenderer(Util.TABLE_CELL_MONEY);
        colm.getColumn(6).setCellRenderer(Util.TABLE_CELL_MONEY);

        tableSells.updateUI();

        int sumQty = 0;
        double sumAmt = 0;
        for (Object[] row : history) {
            sumQty += (int) row[4];
            sumAmt += (double) row[6];
        }
        lblSellingStats.setText(String.format("This product has been purchased "
                + "%,d times for a total of %,d units. Total income: %,.2f %s.",
                history.size(), sumQty, sumAmt, Const.CURRENCY));

    }

    private void loadSuppliers() {
        suppliers = new ArrayList<>();
        List<String> supplierIDs = database.queryListOfProductSupplierIDs(product.getID());
        for (String id : supplierIDs) {
            suppliers.add(database.querySupplier(id));
        }

        tableSuppliers.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Supplier ID", "Supplier Name"};

            @Override
            public int getRowCount() {
                return suppliers.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    return suppliers.get(rowIndex).getID();
                } else {
                    return suppliers.get(rowIndex).getName();
                }
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }
        });
        tableSuppliers.updateUI();
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
        cbxProductID = new javax.swing.JComboBox<>();
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
        lblCurrentStock = new javax.swing.JLabel();
        javax.swing.JLabel l_newStock = new javax.swing.JLabel();
        spnStock = new javax.swing.JSpinner();
        panel_suppliers = new javax.swing.JPanel();
        tableSuppliers_scrollPane = new javax.swing.JScrollPane();
        tableSuppliers = new javax.swing.JTable();
        btnSupplierView = new javax.swing.JButton();
        btnSupplierAdd = new javax.swing.JButton();
        btnSupplierRemove = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

        cbxProductID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxProductID.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 16);
        panel_header.add(cbxProductID, gridBagConstraints);

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

        tbxProductID.setEditable(false);
        tbxProductID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 2, 8);
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
        cbxCategory.setMaximumSize(new java.awt.Dimension(140, 22));
        cbxCategory.setMinimumSize(new java.awt.Dimension(140, 22));
        cbxCategory.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        panel_basic.add(cbxCategory, gridBagConstraints);

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
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setWrapStyleWord(true);
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
        tableSuppliers.getTableHeader().setReorderingAllowed(false);
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

        btnSupplierRemove.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnSupplierRemove.setText("Remove Selected");
        btnSupplierRemove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSupplierRemove.setMaximumSize(new java.awt.Dimension(128, 26));
        btnSupplierRemove.setMinimumSize(new java.awt.Dimension(128, 26));
        btnSupplierRemove.setName(""); // NOI18N
        btnSupplierRemove.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_suppliers.add(btnSupplierRemove, gridBagConstraints);

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
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Start Date", "End Date", "Duration", "Unit Price"
            }
        ));
        tablePrice.setGridColor(new java.awt.Color(204, 204, 204));
        tablePrice.setRowHeight(20);
        tablePrice.getTableHeader().setReorderingAllowed(false);
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
        tableSells.getTableHeader().setReorderingAllowed(false);
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
    private javax.swing.JButton btnSupplierRemove;
    private javax.swing.JButton btnSupplierView;
    private javax.swing.JComboBox<String> cbxCategory;
    private javax.swing.JComboBox<String> cbxProductID;
    private javax.swing.JCheckBox chkDiscontinued;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel lblCurrentPrice;
    private javax.swing.JLabel lblCurrentStock;
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

    //<editor-fold desc="Static showDialog() Methods">
    /**
     * Show a dialog for adding a new product.
     *
     * @param owner The window that calls the dialog.
     * @return A newly created Product. Null if the user canceled.
     */
    public static Product showNewProductDialog(Frame owner) {
        EditProductInfoWindow win = new EditProductInfoWindow(InfoWindowMode.ADD);
        win.setProduct(Product.createNewProduct(database.suggestNextProductID()));

        Util.createAndShowDialog(owner, "Product Information - " + Const.APP_TITLE,
                win.getContentPane(), win.getPreferredSize());
        System.out.println("showNewProductDialog() returning: " + win.product);
        return win.product;
    }

    /**
     * Show a dialog for editing info of an existing product.
     *
     * @param owner The window that calls the dialog.
     * @param product The Product to be edited.
     * @return The same Product instance whether the user save their edits or
     * not.
     */
    public static Product showEditProductDialog(Frame owner, Product product) {
        EditProductInfoWindow win = new EditProductInfoWindow(InfoWindowMode.EDIT);
        win.setProduct(product);

        Util.createAndShowDialog(owner, "Product Information - " + Const.APP_TITLE,
                win.getContentPane(), win.getPreferredSize());
        System.out.println("showEditProductDialog() returning: " + win.product);
        return win.product;
    }

    /**
     * Show a dialog that allows the user to view info of an existing product.
     *
     * @param owner The window that calls the dialog.
     * @param product The Product to be viewed.
     */
    public static void showViewProductDialog(Frame owner, Product product) {
        EditProductInfoWindow win = new EditProductInfoWindow(InfoWindowMode.VIEW);
        win.setProduct(product);

        Util.createAndShowDialog(owner, "Product Information - " + Const.APP_TITLE,
                win.getContentPane(), win.getPreferredSize());
    }

    //</editor-fold>
}
