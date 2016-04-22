package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.data.Customer;
import database.test.data.ShoppingList;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class SaleHistoryWindow
        extends DataDisplayWindow {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();
    
    private List<Object[]> shoppingHistory = new ArrayList<>();

    public SaleHistoryWindow() {
        this.initComponents();
        this.initListeners();
        this.setColorTheme();

        chkHistoryFiltering.setSelected(false);
        
        this.setTitle("Sale Records - " + Const.APP_TITLE);
        this.setLocationRelativeTo(null);
        this.updateButtonsEnabled();
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    /**
     * Query the category list from the database, and update the table model and
     * also its UI.
     */
    @Override
    public void refresh() {
        LocalDate dateFrom, dateTo;
        if (chkHistoryFiltering.isSelected()) {
            try {
                dateFrom = LocalDate.parse(tbxDateFrom.getText());
            } catch (DateTimeParseException ex) {
                dateFrom = Const.SQL_MINDATE;
                tbxDateFrom.setText(Const.SQL_MINDATE.toString());
            }
            try {
                dateTo = LocalDate.parse(tbxDateTo.getText());
            } catch (DateTimeParseException ex) {
                dateTo = Const.SQL_MAXDATE;
                tbxDateTo.setText(Const.SQL_MAXDATE.toString());
            }
        } else {
            dateFrom = Const.SQL_MINDATE;
            dateTo = Const.SQL_MAXDATE;
        }
        
        shoppingHistory = database.querySaleHistory(dateFrom, dateTo);
        tableSale.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Sale ID", "Date", "Time", "Customer ID",
                "Items", "Discount", "Total"};

            @Override
            public int getRowCount() {
                return shoppingHistory.size();
            }

            @Override
            public int getColumnCount() {
                return COLUMNS.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object[] row = shoppingHistory.get(rowIndex);
                return row[columnIndex];
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Integer.class;
                if (columnIndex == 5 || columnIndex == 6) return Double.class;
                else return String.class;
            }
        });

        // setting column  sizes
        tableSale.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel colm = tableSale.getColumnModel();

        colm.getColumn(0).setMinWidth(100);
        colm.getColumn(0).setMaxWidth(100);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(1).setMinWidth(70);
        colm.getColumn(1).setMaxWidth(70);
        colm.getColumn(1).setResizable(false);

        colm.getColumn(2).setMinWidth(50);
        colm.getColumn(2).setMaxWidth(50);
        colm.getColumn(2).setResizable(false);

        colm.getColumn(3).setMinWidth(80);
        colm.getColumn(3).setMaxWidth(80);
        colm.getColumn(3).setResizable(false);
        
        colm.getColumn(4).setMinWidth(50);
        colm.getColumn(4).setMaxWidth(50);
        colm.getColumn(4).setResizable(false);
        
        colm.getColumn(5).setMinWidth(50);
        colm.getColumn(5).setMaxWidth(5);
        colm.getColumn(5).setResizable(false);
        
        // set alignments
        colm.getColumn(2).setCellRenderer(Util.TABLE_CELL_TIME);
        colm.getColumn(4).setCellRenderer(Util.TABLE_CELL_INTEGER);
        colm.getColumn(5).setCellRenderer(Util.TABLE_CELL_PERCENT);
        colm.getColumn(6).setCellRenderer(Util.TABLE_CELL_MONEY);

        tableSale.updateUI();
        this.updateButtonsEnabled();
    }

    private void updateButtonsEnabled() {
//        if (tableCustomers.getSelectedRowCount() > 0) {
//            Customer selected = customerList.get(tableCustomers.getSelectedRow());
//            if (selected.getID().equals(Const.UNREGISTERED_CUSTOMER_ID)
//                    || selected.getID().equals(Const.DELETED_CUSTOMER_ID)) {
//                btnEditCustomer.setEnabled(false);
//                btnDeleteCustomer.setEnabled(false);
//            } else {
//                btnEditCustomer.setEnabled(true);
//                btnDeleteCustomer.setEnabled(true);
//            }
//            btnViewCustomer.setEnabled(true);
//        } else {
//            btnEditCustomer.setEnabled(false);
//            btnDeleteCustomer.setEnabled(false);
//            btnViewCustomer.setEnabled(false);
//        }
    }

    private void setColorTheme() {
        tableSale.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tableSale.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tableSale.setGridColor(Const.COLOR_TABLE_GRID);
        tableSale.setFont(Const.FONT_DEFAULT_12);
        tableSale.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tableSale.setRowHeight(24);
        
        tableDetails.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tableDetails.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tableDetails.setGridColor(Const.COLOR_TABLE_GRID);
        tableDetails.setFont(Const.FONT_DEFAULT_12);
        tableDetails.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tableDetails.setRowHeight(24);

        tbxSearch.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        tbxSearch.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);
    }

    private void initListeners() {
        btnRefresh.addActionListener((ActionEvent) -> {
            this.refresh();
        });
//        btnNewCustomer.addActionListener((ActionEvent) -> {
//            this.customerAdd();
//        });
//        btnViewCustomer.addActionListener((ActionEvent) -> {
//            this.customerView();
//        });
//        btnEditCustomer.addActionListener((ActionEvent) -> {
//            this.customerEdit();
//        });
//        btnDeleteCustomer.addActionListener((ActionEvent) -> {
//            this.customerDelete();
//        });
//
//        tableCustomers.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
//            this.updateButtonsEnabled();
//        });
        
        tbxSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    refresh();
                }
            }
        });
        
        chkHistoryFiltering.addActionListener((ActionEvent) -> {
            tbxDateFrom.setEnabled(chkHistoryFiltering.isSelected());
            tbxDateTo.setEnabled(chkHistoryFiltering.isSelected());
            this.refresh();
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
        tbxSearch = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();
        btnNewCustomer = new javax.swing.JButton();
        btnEditCustomer = new javax.swing.JButton();
        btnDeleteCustomer = new javax.swing.JButton();
        btnViewCustomer = new javax.swing.JButton();
        panel_shoppingHistory = new javax.swing.JPanel();
        panel_top = new javax.swing.JPanel();
        tbxDateFrom = new javax.swing.JFormattedTextField();
        javax.swing.JLabel l_filterFrom = new javax.swing.JLabel();
        javax.swing.JLabel l_filterTo = new javax.swing.JLabel();
        tbxDateTo = new javax.swing.JFormattedTextField();
        chkHistoryFiltering = new javax.swing.JCheckBox();
        panel_bottom = new javax.swing.JPanel();
        lblStats = new javax.swing.JLabel();
        tableSale_scrollPane = new javax.swing.JScrollPane();
        tableSale = new javax.swing.JTable();
        tableDetails_scrollPane = new javax.swing.JScrollPane();
        tableDetails = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(600, 340));
        setSize(new java.awt.Dimension(900, 540));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Sale Records");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 8, 16);
        panel_header.add(headerLabel, gridBagConstraints);

        tbxSearch.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxSearch.setToolTipText("Search customers by first name or last name.");
        tbxSearch.setPreferredSize(new java.awt.Dimension(128, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_header.add(tbxSearch, gridBagConstraints);

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.setMaximumSize(new java.awt.Dimension(128, 36));
        btnRefresh.setMinimumSize(new java.awt.Dimension(128, 36));
        btnRefresh.setName(""); // NOI18N
        btnRefresh.setPreferredSize(new java.awt.Dimension(96, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        panel_header.add(btnRefresh, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(panel_header, gridBagConstraints);

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

        panel_shoppingHistory.setLayout(new java.awt.GridBagLayout());

        panel_top.setLayout(new java.awt.GridBagLayout());

        tbxDateFrom.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        tbxDateFrom.setText("2016-01-01");
        tbxDateFrom.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxDateFrom.setPreferredSize(new java.awt.Dimension(80, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_top.add(tbxDateFrom, gridBagConstraints);

        l_filterFrom.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterFrom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterFrom.setText("From:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
        panel_top.add(l_filterFrom, gridBagConstraints);

        l_filterTo.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterTo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterTo.setText("To:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 16, 4, 4);
        panel_top.add(l_filterTo, gridBagConstraints);

        tbxDateTo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        tbxDateTo.setText("2016-02-01");
        tbxDateTo.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxDateTo.setPreferredSize(new java.awt.Dimension(80, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
        panel_top.add(tbxDateTo, gridBagConstraints);

        chkHistoryFiltering.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkHistoryFiltering.setSelected(true);
        chkHistoryFiltering.setText("Enable Filtering:");
        chkHistoryFiltering.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
        panel_top.add(chkHistoryFiltering, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        panel_shoppingHistory.add(panel_top, gridBagConstraints);

        panel_bottom.setLayout(new java.awt.GridBagLayout());

        lblStats.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblStats.setText("0 Records Found, Total amount 0 ฿, \nSelect a sale record on the left side to see more detail on the right.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        panel_bottom.add(lblStats, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        panel_shoppingHistory.add(panel_bottom, gridBagConstraints);

        tableSale_scrollPane.setMaximumSize(new java.awt.Dimension(550, 32767));
        tableSale_scrollPane.setMinimumSize(new java.awt.Dimension(450, 23));
        tableSale_scrollPane.setPreferredSize(new java.awt.Dimension(450, 402));

        tableSale.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableSale.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Sale ID", "Date", "Time", "Customer", "Items", "Discount", "Total"
            }
        ));
        tableSale.setGridColor(new java.awt.Color(204, 204, 204));
        tableSale.setMinimumSize(new java.awt.Dimension(405, 80));
        tableSale.setRowHeight(20);
        tableSale_scrollPane.setViewportView(tableSale);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        panel_shoppingHistory.add(tableSale_scrollPane, gridBagConstraints);

        tableDetails.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableDetails.setModel(new javax.swing.table.DefaultTableModel(
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
        tableDetails.setGridColor(new java.awt.Color(204, 204, 204));
        tableDetails.setRowHeight(20);
        tableDetails_scrollPane.setViewportView(tableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        panel_shoppingHistory.add(tableDetails_scrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(panel_shoppingHistory, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteCustomer;
    private javax.swing.JButton btnEditCustomer;
    private javax.swing.JButton btnNewCustomer;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnViewCustomer;
    private javax.swing.JCheckBox chkHistoryFiltering;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel lblStats;
    private javax.swing.JPanel panel_bottom;
    private javax.swing.JPanel panel_header;
    private javax.swing.JPanel panel_shoppingHistory;
    private javax.swing.JPanel panel_top;
    private javax.swing.JTable tableDetails;
    private javax.swing.JScrollPane tableDetails_scrollPane;
    private javax.swing.JTable tableSale;
    private javax.swing.JScrollPane tableSale_scrollPane;
    private javax.swing.JFormattedTextField tbxDateFrom;
    private javax.swing.JFormattedTextField tbxDateTo;
    private javax.swing.JTextField tbxSearch;
    // End of variables declaration//GEN-END:variables

    //</editor-fold>
}
