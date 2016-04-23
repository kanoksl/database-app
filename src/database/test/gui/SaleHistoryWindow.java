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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class SaleHistoryWindow
        extends DataDisplayWindow {

    public static final String SALE_INFO_FORMAT = "<html>\n"
            + "<b>Sale ID:</b> %s, <b>Date/Time:</b> %s %s<br/>\n"
            + "<b>Customer:</b> %s, %s<br/>\n"
            + "<br/>\n"
            + "<b>Items Bought:</b> %,d, <b>Discount Percent:</b> %.2f %%, <b>Total After Discount:</b> %.2f %s<br/>\n"
            + "</html>";

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();

    private List<Object[]> shoppingHistory = new ArrayList<>();
    private Customer currentCustomer = null;

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
                if (columnIndex == 4) {
                    return Integer.class;
                }
                if (columnIndex == 5 || columnIndex == 6) {
                    return Double.class;
                } else {
                    return String.class;
                }
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

        lblStats.setText(String.format("%,d records found. "
                + "Select a sale record on the left side to see more detail on the right.",
                shoppingHistory.size()));
    }

    private void showSaleDetail() {
        int row = tableSale.getSelectedRow();
        if (row < 0 || row > shoppingHistory.size()) {
            tableDetails.setModel(new DefaultTableModel(new String[]{
                "Product ID", "Product Name", "Quantity", "Unit Price", "Subtotal"}, 0));
            tableDetails.updateUI();
            return;
        }
        String saleID = (String) shoppingHistory.get(row)[0];
        List<Object[]> details = database.querySingleSaleDetail(saleID);
        tableDetails.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Product ID", "Product Name",
                "Quantity", "Unit Price", "Subtotal"};

            @Override
            public int getRowCount() {
                return details.size();
            }

            @Override
            public int getColumnCount() {
                return COLUMNS.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object[] row = details.get(rowIndex);
                return row[columnIndex];
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Integer.class;
                }
                if (columnIndex == 3 || columnIndex == 4) {
                    return Double.class;
                } else {
                    return String.class;
                }
            }
        });
        tableDetails.updateUI();

        tableDetails.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel colm = tableDetails.getColumnModel();

        colm.getColumn(0).setMinWidth(80);
        colm.getColumn(0).setMaxWidth(80);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(2).setMinWidth(50);
        colm.getColumn(2).setMaxWidth(50);
        colm.getColumn(2).setResizable(false);

        colm.getColumn(2).setCellRenderer(Util.TABLE_CELL_INTEGER);
        colm.getColumn(3).setCellRenderer(Util.TABLE_CELL_MONEY);
        colm.getColumn(4).setCellRenderer(Util.TABLE_CELL_MONEY);

        Object[] info = shoppingHistory.get(row);
        LocalDate date = (LocalDate) info[1];
        LocalTime time = (LocalTime) info[2];
        String customerID = (String) info[3];
        int items = (int) info[4];
        double discount = (double) info[5];
        double total = (double) info[6];

        currentCustomer = database.queryCustomer(customerID);
        String customerName = (currentCustomer == null) ? "-" : currentCustomer.getDisplayName();

        lblSaleInfo.setText(String.format(SALE_INFO_FORMAT,
                saleID, date, time.format(DateTimeFormatter.ofPattern("HH:mm")),
                customerID, customerName, items, discount, total, Const.CURRENCY));
    }

    private void updateButtonsEnabled() {
        btnDeleteRecord.setEnabled(shoppingHistory.isEmpty()
                || tableSale.getSelectedRow() < 0
                || tableSale.getSelectedRow() >= shoppingHistory.size());
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

    }

    private void initListeners() {
        btnRefresh.addActionListener((ActionEvent) -> {
            this.refresh();
        });

        tableSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel selectionModel = tableSale.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            this.showSaleDetail();
        });

        tbxDateFrom.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    refresh();
                }
            }
        });
        tbxDateTo.addKeyListener(new KeyAdapter() {
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

        btnCustomerView.addActionListener((ActionEvent) -> {
            if (currentCustomer != null) {
                EditCustomerInfoWindow.showViewCustomerDialog(this, currentCustomer);
            }
        });
        btnProductView.addActionListener((ActionEvent) -> {
            int row = tableDetails.getSelectedRow();
            if (row < 0 || row > tableDetails.getModel().getRowCount()) {
                return;
            }
            String productID = (String) tableDetails.getValueAt(row, 0);
            EditProductInfoWindow.showViewProductDialog(this, database.queryProduct(productID));
        });
        btnDeleteRecord.addActionListener((ActionEvent) -> {
            int row = tableSale.getSelectedRow();
            if (row < 0 || row > shoppingHistory.size()) {
                return;
            }
            String saleID = (String) shoppingHistory.get(row)[0];
            boolean deleted = database.tryDeleteSaleRecord(saleID, this);
            if (deleted) {
                shoppingHistory.remove(row);
                ((AbstractTableModel) tableSale.getModel()).fireTableDataChanged();
            }
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
        panel_filter = new javax.swing.JPanel();
        tbxDateFrom = new javax.swing.JFormattedTextField();
        javax.swing.JLabel l_filterFrom = new javax.swing.JLabel();
        javax.swing.JLabel l_filterTo = new javax.swing.JLabel();
        tbxDateTo = new javax.swing.JFormattedTextField();
        chkHistoryFiltering = new javax.swing.JCheckBox();
        btnRefresh = new javax.swing.JButton();
        panel_shoppingHistory = new javax.swing.JPanel();
        panel_bottom = new javax.swing.JPanel();
        lblStats = new javax.swing.JLabel();
        tableSale_scrollPane = new javax.swing.JScrollPane();
        tableSale = new javax.swing.JTable();
        panel_details = new javax.swing.JPanel();
        tableDetails_scrollPane = new javax.swing.JScrollPane();
        tableDetails = new javax.swing.JTable();
        lblSaleInfo = new javax.swing.JLabel();
        btnProductView = new javax.swing.JButton();
        btnCustomerView = new javax.swing.JButton();
        btnDeleteRecord = new javax.swing.JButton();

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

        panel_filter.setBackground(new java.awt.Color(255, 255, 255));
        panel_filter.setLayout(new java.awt.GridBagLayout());

        tbxDateFrom.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        tbxDateFrom.setText("2016-01-01");
        tbxDateFrom.setEnabled(false);
        tbxDateFrom.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxDateFrom.setPreferredSize(new java.awt.Dimension(80, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_filter.add(tbxDateFrom, gridBagConstraints);

        l_filterFrom.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterFrom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterFrom.setText("From:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
        panel_filter.add(l_filterFrom, gridBagConstraints);

        l_filterTo.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterTo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterTo.setText("To:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 16, 4, 4);
        panel_filter.add(l_filterTo, gridBagConstraints);

        tbxDateTo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        tbxDateTo.setText("2016-02-01");
        tbxDateTo.setEnabled(false);
        tbxDateTo.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxDateTo.setPreferredSize(new java.awt.Dimension(80, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_filter.add(tbxDateTo, gridBagConstraints);

        chkHistoryFiltering.setBackground(new java.awt.Color(255, 255, 255));
        chkHistoryFiltering.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkHistoryFiltering.setText("Enable Filtering:");
        chkHistoryFiltering.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_filter.add(chkHistoryFiltering, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        panel_header.add(panel_filter, gridBagConstraints);

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

        panel_shoppingHistory.setLayout(new java.awt.GridBagLayout());

        panel_bottom.setLayout(new java.awt.GridBagLayout());

        lblStats.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblStats.setText("0 records found.\nSelect a sale record on the left side to see more detail on the right.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        panel_bottom.add(lblStats, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
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
        tableSale.getTableHeader().setReorderingAllowed(false);
        tableSale_scrollPane.setViewportView(tableSale);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 0);
        panel_shoppingHistory.add(tableSale_scrollPane, gridBagConstraints);

        panel_details.setLayout(new java.awt.GridBagLayout());

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
        tableDetails.getTableHeader().setReorderingAllowed(false);
        tableDetails_scrollPane.setViewportView(tableDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        panel_details.add(tableDetails_scrollPane, gridBagConstraints);

        lblSaleInfo.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblSaleInfo.setText("<html>\n<b>Sale ID:</b> SL0000000000, <b>Date/Time:</b> 0000-00-00 00:00<br/>\n<b>Customer:</b> C0000000, __. ______ _______<br/>\n<br/>\n<b>Items Bought:</b> 0, <b>Discount Percent:</b> 0%, <b>Total After Discount:</b> 0.00 #<br/>\n</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        panel_details.add(lblSaleInfo, gridBagConstraints);

        btnProductView.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnProductView.setText("View Selected Product...");
        btnProductView.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProductView.setMaximumSize(new java.awt.Dimension(164, 26));
        btnProductView.setMinimumSize(new java.awt.Dimension(164, 26));
        btnProductView.setName(""); // NOI18N
        btnProductView.setPreferredSize(new java.awt.Dimension(164, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        panel_details.add(btnProductView, gridBagConstraints);

        btnCustomerView.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnCustomerView.setText("View Customer Info...");
        btnCustomerView.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCustomerView.setMaximumSize(new java.awt.Dimension(164, 26));
        btnCustomerView.setMinimumSize(new java.awt.Dimension(164, 26));
        btnCustomerView.setName(""); // NOI18N
        btnCustomerView.setPreferredSize(new java.awt.Dimension(164, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        panel_details.add(btnCustomerView, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 8);
        panel_shoppingHistory.add(panel_details, gridBagConstraints);

        btnDeleteRecord.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnDeleteRecord.setText("Delete Selected Record");
        btnDeleteRecord.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteRecord.setMaximumSize(new java.awt.Dimension(164, 26));
        btnDeleteRecord.setMinimumSize(new java.awt.Dimension(164, 26));
        btnDeleteRecord.setName(""); // NOI18N
        btnDeleteRecord.setPreferredSize(new java.awt.Dimension(164, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        panel_shoppingHistory.add(btnDeleteRecord, gridBagConstraints);

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
    private javax.swing.JButton btnCustomerView;
    private javax.swing.JButton btnDeleteRecord;
    private javax.swing.JButton btnProductView;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JCheckBox chkHistoryFiltering;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel lblSaleInfo;
    private javax.swing.JLabel lblStats;
    private javax.swing.JPanel panel_bottom;
    private javax.swing.JPanel panel_details;
    private javax.swing.JPanel panel_filter;
    private javax.swing.JPanel panel_header;
    private javax.swing.JPanel panel_shoppingHistory;
    private javax.swing.JTable tableDetails;
    private javax.swing.JScrollPane tableDetails_scrollPane;
    private javax.swing.JTable tableSale;
    private javax.swing.JScrollPane tableSale_scrollPane;
    private javax.swing.JFormattedTextField tbxDateFrom;
    private javax.swing.JFormattedTextField tbxDateTo;
    // End of variables declaration//GEN-END:variables

    //</editor-fold>
}
