package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.data.Customer;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ManageCustomersWindow
        extends javax.swing.JFrame {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();
    private List<Customer> customerList;

    public ManageCustomersWindow() {
        this.initComponents();
        this.initListeners();
        this.setColorTheme();

        this.setTitle("Manage Customers - " + Const.APP_TITLE);
        this.setLocationRelativeTo(null);
        this.updateButtonsEnabled();
    }

    private void customerAdd() {
        Customer c = EditCustomerInfoWindow.showNewCustomerDialog(this);
        if (c != null) {
            customerList.add(c);
            tableCustomers.updateUI();
            tableCustomers.setRowSelectionInterval(customerList.size(), customerList.size());
        }
    }

    private void customerView() {
        EditCustomerInfoWindow.showViewCustomerDialog(this,
                customerList.get(tableCustomers.getSelectedRow()));
    }

    private void customerEdit() {
        int row = tableCustomers.getSelectedRow();
        Customer selected = customerList.get(row);
        Customer copy = database.queryCustomer(selected.getID());
        copy = EditCustomerInfoWindow.showEditCustomerDialog(this, copy);
        if (copy != null) {
            customerList.set(row, copy);
            tableCustomers.updateUI();
        }
    }

    private void customerDelete() {
        int row = tableCustomers.getSelectedRow();
        boolean deleted = this.databaseDeleteCustomer(customerList.get(row));
        if (deleted) {
            customerList.remove(row);
            tableCustomers.updateUI();
        }
    }

    private boolean databaseDeleteCustomer(Customer customer) {
        boolean result = false;
        int proceed = JOptionPane.showConfirmDialog(this,
                "Delete the following customer's information from the database?\n>> "
                + customer.getID() + " - " + customer.getDisplayName(),
                "Manage Customers", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null);
        if (proceed == JOptionPane.OK_OPTION) {
            try {
                database.deleteCustomer(customer); // actual delete operation
                JOptionPane.showMessageDialog(this,
                        "The customer's information was successfully deleted.",
                        "Manage Customers", JOptionPane.INFORMATION_MESSAGE);
                result = true;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting the customer's information:\n" + ex.getMessage(),
                        "Manage Customers", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    /**
     * Query the category list from the database, and update the table model and
     * also its UI.
     */
    public void refresh() {
        customerList = database.queryAllCustomers();
        TableModel model = Customer.createTableModel(customerList);
        tableCustomers.setModel(model);

        // setting column headers and sizes
        tableCustomers.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        final int width_id = 80, width_gender = 50, width_date = 80, width_phone = 90;
        TableColumnModel colm = tableCustomers.getColumnModel();

        colm.getColumn(0).setMinWidth(width_id);
        colm.getColumn(0).setMaxWidth(width_id);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(3).setMinWidth(width_gender);
        colm.getColumn(3).setMaxWidth(width_gender);
        colm.getColumn(3).setResizable(false);

        colm.getColumn(4).setMinWidth(width_date);
        colm.getColumn(4).setMaxWidth(width_date);
        colm.getColumn(4).setResizable(false);
        colm.getColumn(5).setMinWidth(width_date);
        colm.getColumn(5).setMaxWidth(width_date);
        colm.getColumn(5).setResizable(false);

        colm.getColumn(6).setMinWidth(width_phone);
        colm.getColumn(6).setMaxWidth(width_phone);
        colm.getColumn(6).setResizable(false);

        tableCustomers.updateUI();

        // select the last row
        int rowIndex = Math.max(0, tableCustomers.getRowCount() - 1);
        tableCustomers.setRowSelectionInterval(rowIndex, rowIndex);
    }

    private void updateButtonsEnabled() {
        if (tableCustomers.getSelectedRowCount() > 0) {
            Customer selected = customerList.get(tableCustomers.getSelectedRow());
            if (selected.getID().equals(Const.UNREGISTERED_CUSTOMER_ID)
                    || selected.getID().equals(Const.DELETED_CUSTOMER_ID)) {
                btnEditCustomer.setEnabled(false);
                btnDeleteCustomer.setEnabled(false);
            } else {
                btnEditCustomer.setEnabled(true);
                btnDeleteCustomer.setEnabled(true);
            }
            btnViewCustomer.setEnabled(true);
        } else {
            btnEditCustomer.setEnabled(false);
            btnDeleteCustomer.setEnabled(false);
            btnViewCustomer.setEnabled(false);
        }
    }

    private void setColorTheme() {
        tableCustomers.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tableCustomers.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tableCustomers.setGridColor(Const.COLOR_TABLE_GRID);
        tableCustomers.setFont(Const.FONT_DEFAULT_12);
        tableCustomers.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tableCustomers.setRowHeight(24);
    }

    private void initListeners() {
        btnNewCustomer.addActionListener((ActionEvent) -> {
            this.customerAdd();
        });
        btnViewCustomer.addActionListener((ActionEvent) -> {
            this.customerView();
        });
        btnEditCustomer.addActionListener((ActionEvent) -> {
            this.customerEdit();
        });
        btnDeleteCustomer.addActionListener((ActionEvent) -> {
            this.customerDelete();
        });

        tableCustomers.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            this.updateButtonsEnabled();
        });
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
        tableCustomers_scrollPane = new javax.swing.JScrollPane();
        tableCustomers = new javax.swing.JTable();
        btnNewCustomer = new javax.swing.JButton();
        btnEditCustomer = new javax.swing.JButton();
        btnDeleteCustomer = new javax.swing.JButton();
        btnViewCustomer = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(600, 340));
        setPreferredSize(new java.awt.Dimension(900, 540));
        setSize(new java.awt.Dimension(900, 540));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Registered Customers");
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
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(panel_header, gridBagConstraints);

        tableCustomers.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableCustomers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Customer ID", "First Name", "Last Name", "Gender", "Birthday", "Registered", "Phone", "Email"
            }
        ));
        tableCustomers.setGridColor(new java.awt.Color(204, 204, 204));
        tableCustomers.setRowHeight(20);
        tableCustomers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableCustomers_scrollPane.setViewportView(tableCustomers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        getContentPane().add(tableCustomers_scrollPane, gridBagConstraints);

        btnNewCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnNewCustomer.setText("New Customer...");
        btnNewCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNewCustomer.setMaximumSize(new java.awt.Dimension(128, 26));
        btnNewCustomer.setMinimumSize(new java.awt.Dimension(128, 26));
        btnNewCustomer.setName(""); // NOI18N
        btnNewCustomer.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 4);
        getContentPane().add(btnNewCustomer, gridBagConstraints);

        btnEditCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnEditCustomer.setText("Edit Selected...");
        btnEditCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditCustomer.setMaximumSize(new java.awt.Dimension(128, 26));
        btnEditCustomer.setMinimumSize(new java.awt.Dimension(128, 26));
        btnEditCustomer.setName(""); // NOI18N
        btnEditCustomer.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        getContentPane().add(btnEditCustomer, gridBagConstraints);

        btnDeleteCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnDeleteCustomer.setText("Delete Selected");
        btnDeleteCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteCustomer.setMaximumSize(new java.awt.Dimension(128, 26));
        btnDeleteCustomer.setMinimumSize(new java.awt.Dimension(128, 26));
        btnDeleteCustomer.setName(""); // NOI18N
        btnDeleteCustomer.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 8);
        getContentPane().add(btnDeleteCustomer, gridBagConstraints);

        btnViewCustomer.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnViewCustomer.setText("View Selected...");
        btnViewCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewCustomer.setMaximumSize(new java.awt.Dimension(128, 26));
        btnViewCustomer.setMinimumSize(new java.awt.Dimension(128, 26));
        btnViewCustomer.setName(""); // NOI18N
        btnViewCustomer.setPreferredSize(new java.awt.Dimension(128, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        getContentPane().add(btnViewCustomer, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteCustomer;
    private javax.swing.JButton btnEditCustomer;
    private javax.swing.JButton btnNewCustomer;
    private javax.swing.JButton btnViewCustomer;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JPanel panel_header;
    private javax.swing.JTable tableCustomers;
    private javax.swing.JScrollPane tableCustomers_scrollPane;
    // End of variables declaration//GEN-END:variables

    //</editor-fold>
}
