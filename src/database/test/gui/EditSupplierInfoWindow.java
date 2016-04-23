package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.data.Product;
import database.test.data.Supplier;
import database.test.gui.Const.InfoWindowMode;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

public class EditSupplierInfoWindow
        extends javax.swing.JFrame {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();

    private Supplier supplier = null;

    private List<Product> products = null;
    private boolean productsChanged = false;

    private EditSupplierInfoWindow(InfoWindowMode mode) {
        this.initComponents();
        this.setColorTheme();

        lblWarnID.setText(" ");
        lblWarnName.setText(" ");
        tbxSupplierName.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                tbxSupplierName.setCaretPosition(tbxSupplierName.getText().trim().length());
            }
        });

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

        btnProductAdd.addActionListener((ActionEvent) -> {
            this.supplierProductAdd();
        });
        btnProductRemove.addActionListener((ActionEvent) -> {
            this.supplierProductRemove();
        });
        btnProductView.addActionListener((ActionEvent) -> {
            this.supplierProductView();
        });
    }

    private void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        this.productsChanged = false;
        this.populateFormData();
        if (cbxSupplierID.isVisible()) { // in View mode
            cbxSupplierID.setSelectedItem(supplier.getID());
        }
    }

    private void populateFormData() {
        tbxSupplierID.setText(supplier.getID());
        tbxSupplierName.setText(supplier.getName());
        tbxPhone.setText(supplier.getPhoneNumber());
        tbxEmail.setText(supplier.getEmailAddress());
        tbxWebsite.setText(supplier.getWebsite());
        tbxAddress.setText(supplier.getAddress());
        txtNotes.setText(supplier.getNotes());

        this.loadProducts();
    }

    private void collectFormData() {
        supplier.setID(tbxSupplierID.getText());
        supplier.setName(tbxSupplierName.getText().trim());
        supplier.setPhoneNumber(tbxPhone.getText().replace("-", "").replace(" ", ""));
        supplier.setEmailAddress(tbxEmail.getText().trim());
        supplier.setWebsite(tbxWebsite.getText().trim());
        supplier.setAddress(tbxAddress.getText().trim());
        supplier.setNotes(txtNotes.getText().trim());
    }

    private void supplierProductAdd() {
        List<String> add = SelectorWindow.showProductSelectorDialog(this);
        if (add != null) {
            for (String id : add) {
                boolean dupe = false;
                for (Product p : products) {
                    if (p.getID().equals(id)) {
                        dupe = true;
                        break;
                    }
                }
                if (!dupe) {
                    products.add(database.queryProduct(id));
                    productsChanged = true;
                }
            }
            tableProducts.updateUI();
        }
    }

    private void supplierProductRemove() {
        if (tableProducts.getSelectedRowCount() == 0 || products.isEmpty()) {
            return;
        }
        products.remove(tableProducts.getSelectedRow());
        productsChanged = true;
        tableProducts.updateUI();
    }

    private void supplierProductView() {
        if (tableProducts.getSelectedRowCount() == 0 || products.isEmpty()) {
            return;
        }
        EditProductInfoWindow.showViewProductDialog(this,
                products.get(tableProducts.getSelectedRow()));
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    private void initializeAddMode() {
        headerLabel.setText(Const.ESIW_HEADER_ADD);
        //<editor-fold defaultstate="collapsed" desc="Enable/Disable Components">
        btnDelete.setVisible(false);
        btnSave.setVisible(true);
        btnCancel.setVisible(true);
        btnCancel.setText("Cancel");

        tbxSupplierName.setEditable(true);
        tbxPhone.setEditable(true);
        tbxEmail.setEditable(true);
        tbxWebsite.setEditable(true);
        txtNotes.setEditable(true);

        cbxSupplierID.setEnabled(false);
        cbxSupplierID.setVisible(false);
        //</editor-fold>
        // listeners
        btnSave.addActionListener((ActionEvent) -> {
            this.collectFormData();
            if (database.tryInsertSupplier(supplier, products, this)) {
                SwingUtilities.getWindowAncestor(btnSave).dispose();
            }
        });
        btnCancel.addActionListener((ActionEvent) -> {
            supplier = null;
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });
    }

    private void initializeEditMode() {
        headerLabel.setText(Const.ESIW_HEADER_EDIT);
        //<editor-fold defaultstate="collapsed" desc="Enable/Disable Components">
        btnDelete.setVisible(true);
        btnSave.setVisible(true);
        btnCancel.setVisible(true);
        btnCancel.setText("Cancel");

        tbxSupplierName.setEditable(true);
        tbxPhone.setEditable(true);
        tbxEmail.setEditable(true);
        tbxWebsite.setEditable(true);
        txtNotes.setEditable(true);

        cbxSupplierID.setEnabled(false);
        cbxSupplierID.setVisible(false);
        //</editor-fold>
        // listeners
        btnSave.addActionListener((ActionEvent) -> {
            this.collectFormData();
            if (database.tryUpdateSupplier(supplier, productsChanged ? products : null, this)) {
                SwingUtilities.getWindowAncestor(btnSave).dispose();
            }
        });
        btnCancel.addActionListener((ActionEvent) -> {
            supplier = database.querySupplier(supplier.getID());
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });
        btnDelete.addActionListener((ActionEvent) -> {
            if (database.tryDeleteSupplier(supplier, this)) {
                supplier = null;
                SwingUtilities.getWindowAncestor(btnSave).dispose();
            }
        });
    }

    private void initializeViewMode() {
        headerLabel.setText(Const.ESIW_HEADER_VIEW);
        //<editor-fold defaultstate="collapsed" desc="Enable/Disable Components">
        btnDelete.setVisible(false);
        btnSave.setVisible(false);
        btnCancel.setVisible(true);
        btnCancel.setText("Close");

        tbxSupplierName.setEditable(false);
        tbxPhone.setEditable(false);
        tbxEmail.setEditable(false);
        tbxWebsite.setEditable(false);
        tbxAddress.setEditable(false);
        txtNotes.setEditable(false);

        btnProductAdd.setEnabled(false);
        btnProductRemove.setEnabled(false);
        //</editor-fold>

        // listeners: main
        btnCancel.addActionListener((ActionEvent) -> {
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });

        cbxSupplierID.setEnabled(true);
        cbxSupplierID.setVisible(true);
        List<String> supplierIDs = database.queryListOfSupplierIDs();
        String[] supplierIDsArray = new String[supplierIDs.size()];
        supplierIDs.toArray(supplierIDsArray);
        cbxSupplierID.setModel(new DefaultComboBoxModel<>(supplierIDsArray));
        cbxSupplierID.addActionListener((ActionEvent) -> {
            this.setSupplier(database.querySupplier(cbxSupplierID.getSelectedItem().toString()));
        });
    }

    private void setColorTheme() {
        tableProducts.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tableProducts.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tableProducts.setGridColor(Const.COLOR_TABLE_GRID);
        tableProducts.setFont(Const.FONT_DEFAULT_12);
        tableProducts.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tableProducts.setRowHeight(24);
    }

    private void loadProducts() {
        products = new ArrayList<>();
        List<String> productIDs = database.queryListOfSupplierProductIDs(supplier.getID());
        for (String id : productIDs) {
            products.add(database.queryProduct(id));
        }

        tableProducts.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Product ID", "Product Name"};

            @Override
            public int getRowCount() {
                return products.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    return products.get(rowIndex).getID();
                } else {
                    return products.get(rowIndex).getName();
                }
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }
        });
        tableProducts.updateUI();
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
        cbxSupplierID = new javax.swing.JComboBox<>();
        tabbedPane = new javax.swing.JTabbedPane();
        panel_supplierInfo = new javax.swing.JPanel();
        panel_basic = new javax.swing.JPanel();
        javax.swing.JLabel l_supID = new javax.swing.JLabel();
        tbxSupplierID = new javax.swing.JTextField();
        lblWarnID = new javax.swing.JLabel();
        javax.swing.JLabel l_supName = new javax.swing.JLabel();
        tbxSupplierName = new javax.swing.JFormattedTextField();
        lblWarnName = new javax.swing.JLabel();
        panel_contact = new javax.swing.JPanel();
        javax.swing.JLabel l_phone = new javax.swing.JLabel();
        tbxPhone = new javax.swing.JFormattedTextField();
        javax.swing.JLabel l_email = new javax.swing.JLabel();
        tbxEmail = new javax.swing.JTextField();
        javax.swing.JLabel l_web = new javax.swing.JLabel();
        tbxWebsite = new javax.swing.JTextField();
        tbxAddress = new javax.swing.JTextField();
        javax.swing.JLabel l_addr = new javax.swing.JLabel();
        panel_notes = new javax.swing.JPanel();
        txtNotes_scrollPane = new javax.swing.JScrollPane();
        txtNotes = new javax.swing.JTextArea();
        panel_products = new javax.swing.JPanel();
        tableProducts_scrollPane = new javax.swing.JScrollPane();
        tableProducts = new javax.swing.JTable();
        btnProductView = new javax.swing.JButton();
        btnProductAdd = new javax.swing.JButton();
        btnProductRemove = new javax.swing.JButton();
        panel_commandButtons = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(940, 600));
        setMinimumSize(new java.awt.Dimension(940, 600));
        setPreferredSize(new java.awt.Dimension(940, 600));
        setResizable(false);
        setSize(new java.awt.Dimension(940, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Add/View/Edit Supplier Information");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 8, 16);
        panel_header.add(headerLabel, gridBagConstraints);

        cbxSupplierID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxSupplierID.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 16);
        panel_header.add(cbxSupplierID, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(panel_header, gridBagConstraints);

        tabbedPane.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N

        panel_supplierInfo.setLayout(new java.awt.GridBagLayout());

        panel_basic.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Basic Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_basic.setLayout(new java.awt.GridBagLayout());

        l_supID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_supID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_supID.setText("Supplier ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 20, 4, 4);
        panel_basic.add(l_supID, gridBagConstraints);

        tbxSupplierID.setEditable(false);
        tbxSupplierID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxSupplierID.setMinimumSize(new java.awt.Dimension(140, 22));
        tbxSupplierID.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 2, 4);
        panel_basic.add(tbxSupplierID, gridBagConstraints);

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

        l_supName.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_supName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_supName.setText("Supplier Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 20, 2, 4);
        panel_basic.add(l_supName, gridBagConstraints);

        try {
            tbxSupplierName.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("****************************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        tbxSupplierName.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        tbxSupplierName.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxSupplierName.setMaximumSize(new java.awt.Dimension(188, 22));
        tbxSupplierName.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxSupplierName.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 2, 4);
        panel_basic.add(tbxSupplierName, gridBagConstraints);

        lblWarnName.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblWarnName.setForeground(java.awt.Color.red);
        lblWarnName.setText("The supplier name cannot be blank.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 16, 0);
        panel_basic.add(lblWarnName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 0);
        panel_supplierInfo.add(panel_basic, gridBagConstraints);

        panel_contact.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Contact Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_contact.setLayout(new java.awt.GridBagLayout());

        l_phone.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_phone.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_phone.setText("Phone:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        panel_contact.add(l_phone, gridBagConstraints);

        try {
            tbxPhone.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##########")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        tbxPhone.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        tbxPhone.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxPhone.setMaximumSize(new java.awt.Dimension(188, 22));
        tbxPhone.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxPhone.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
        panel_contact.add(tbxPhone, gridBagConstraints);

        l_email.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_email.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_email.setText("Email:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        panel_contact.add(l_email, gridBagConstraints);

        tbxEmail.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxEmail.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxEmail.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
        panel_contact.add(tbxEmail, gridBagConstraints);

        l_web.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_web.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_web.setText("Website:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        panel_contact.add(l_web, gridBagConstraints);

        tbxWebsite.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxWebsite.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxWebsite.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
        panel_contact.add(tbxWebsite, gridBagConstraints);

        tbxAddress.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxAddress.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxAddress.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
        panel_contact.add(tbxAddress, gridBagConstraints);

        l_addr.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_addr.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_addr.setText("Address:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        panel_contact.add(l_addr, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 0);
        panel_supplierInfo.add(panel_contact, gridBagConstraints);

        panel_notes.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Notes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_notes.setLayout(new java.awt.GridBagLayout());

        txtNotes_scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        txtNotes.setColumns(20);
        txtNotes.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        txtNotes.setLineWrap(true);
        txtNotes.setRows(5);
        txtNotes.setWrapStyleWord(true);
        txtNotes_scrollPane.setViewportView(txtNotes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 4, 4, 4);
        panel_notes.add(txtNotes_scrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 0);
        panel_supplierInfo.add(panel_notes, gridBagConstraints);

        panel_products.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Supplied Products", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_products.setLayout(new java.awt.GridBagLayout());

        tableProducts.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Product ID", "Product Name"
            }
        ));
        tableProducts.setGridColor(new java.awt.Color(204, 204, 204));
        tableProducts.setRowHeight(20);
        tableProducts.getTableHeader().setReorderingAllowed(false);
        tableProducts_scrollPane.setViewportView(tableProducts);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_products.add(tableProducts_scrollPane, gridBagConstraints);

        btnProductView.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnProductView.setText("View Selected...");
        btnProductView.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProductView.setMaximumSize(new java.awt.Dimension(128, 26));
        btnProductView.setMinimumSize(new java.awt.Dimension(128, 26));
        btnProductView.setName(""); // NOI18N
        btnProductView.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_products.add(btnProductView, gridBagConstraints);

        btnProductAdd.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnProductAdd.setText("Add Product...");
        btnProductAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProductAdd.setMaximumSize(new java.awt.Dimension(128, 26));
        btnProductAdd.setMinimumSize(new java.awt.Dimension(128, 26));
        btnProductAdd.setName(""); // NOI18N
        btnProductAdd.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_products.add(btnProductAdd, gridBagConstraints);

        btnProductRemove.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnProductRemove.setText("Remove Selected");
        btnProductRemove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProductRemove.setMaximumSize(new java.awt.Dimension(128, 26));
        btnProductRemove.setMinimumSize(new java.awt.Dimension(128, 26));
        btnProductRemove.setName(""); // NOI18N
        btnProductRemove.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_products.add(btnProductRemove, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 0, 8);
        panel_supplierInfo.add(panel_products, gridBagConstraints);

        btnSave.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnSave.setText("Save");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.setMaximumSize(new java.awt.Dimension(128, 36));
        btnSave.setMinimumSize(new java.awt.Dimension(128, 36));
        btnSave.setName(""); // NOI18N
        btnSave.setPreferredSize(new java.awt.Dimension(96, 28));

        btnDelete.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.setMaximumSize(new java.awt.Dimension(128, 36));
        btnDelete.setMinimumSize(new java.awt.Dimension(128, 36));
        btnDelete.setName(""); // NOI18N
        btnDelete.setPreferredSize(new java.awt.Dimension(96, 28));

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
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 611, Short.MAX_VALUE)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panel_commandButtonsLayout.setVerticalGroup(
            panel_commandButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_commandButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_commandButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        panel_supplierInfo.add(panel_commandButtons, gridBagConstraints);

        tabbedPane.addTab("Supplier Information", panel_supplierInfo);

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
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnProductAdd;
    private javax.swing.JButton btnProductRemove;
    private javax.swing.JButton btnProductView;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbxSupplierID;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel lblWarnID;
    private javax.swing.JLabel lblWarnName;
    private javax.swing.JPanel panel_basic;
    private javax.swing.JPanel panel_commandButtons;
    private javax.swing.JPanel panel_contact;
    private javax.swing.JPanel panel_header;
    private javax.swing.JPanel panel_notes;
    private javax.swing.JPanel panel_products;
    private javax.swing.JPanel panel_supplierInfo;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tableProducts;
    private javax.swing.JScrollPane tableProducts_scrollPane;
    private javax.swing.JTextField tbxAddress;
    private javax.swing.JTextField tbxEmail;
    private javax.swing.JFormattedTextField tbxPhone;
    private javax.swing.JTextField tbxSupplierID;
    private javax.swing.JFormattedTextField tbxSupplierName;
    private javax.swing.JTextField tbxWebsite;
    private javax.swing.JTextArea txtNotes;
    private javax.swing.JScrollPane txtNotes_scrollPane;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    //<editor-fold desc="Static showDialog() Methods">
    /**
     * Show a dialog for adding a new supplier.
     *
     * @param owner The window that calls the dialog.
     * @return A newly created Supplier. Null if the user canceled.
     */
    public static Supplier showNewSupplierDialog(Frame owner) {
        EditSupplierInfoWindow win = new EditSupplierInfoWindow(InfoWindowMode.ADD);
        win.setSupplier(Supplier.createNewSupplier(database.suggestNextSupplierID()));

        Util.createAndShowDialog(owner, "Supplier Information - " + Const.APP_TITLE,
                win.getContentPane(), win.getPreferredSize());
        System.out.println("showNewSupplierDialog() returning: " + win.supplier);
        return win.supplier;
    }

    /**
     * Show a dialog for editing info of an existing supplier.
     *
     * @param owner The window that calls the dialog.
     * @param supplier The Supplier to be edited.
     * @return The same Supplier instance whether the user save their edits or
     * not. Null if the user deleted the Supplier data.
     */
    public static Supplier showEditSupplierDialog(Frame owner, Supplier supplier) {
        EditSupplierInfoWindow win = new EditSupplierInfoWindow(InfoWindowMode.EDIT);
        win.setSupplier(supplier);

        Util.createAndShowDialog(owner, "Supplier Information - " + Const.APP_TITLE,
                win.getContentPane(), win.getPreferredSize());
        System.out.println("showEditSupplierDialog() returning: " + win.supplier);
        return win.supplier;
    }

    /**
     * Show a dialog that allows the user to view info of an existing supplier.
     *
     * @param owner The window that calls the dialog.
     * @param supplier The Supplier to be viewed.
     */
    public static void showViewSupplierDialog(Frame owner, Supplier supplier) {
        EditSupplierInfoWindow win = new EditSupplierInfoWindow(InfoWindowMode.VIEW);
        win.setSupplier(supplier);

        Util.createAndShowDialog(owner, "Supplier Information - " + Const.APP_TITLE,
                win.getContentPane(), win.getPreferredSize());
    }

    //</editor-fold>
}
