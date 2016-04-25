package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.data.Customer;
import database.test.gui.Const.InfoWindowMode;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class EditCustomerInfoWindow
        extends javax.swing.JFrame {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();
    private Customer customer = null;

    private List<Object[]> shoppingHistory = null;

    /**
     * Creates new EditCustomerInfoWindow. Note: to use this window, use the
     * static showDialog methods instead.
     */
    private EditCustomerInfoWindow(InfoWindowMode mode) {
        this.initComponents();
        this.initDateComboBoxes();
        this.setColorTheme();

        lblWarnID.setText(" ");
        lblWarnName.setText(" ");
        tbxFirstName.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                tbxFirstName.setCaretPosition(tbxFirstName.getText().trim().length());
            }
        });
        tbxLastName.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                tbxLastName.setCaretPosition(tbxLastName.getText().trim().length());
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
    }

    private void setCustomer(Customer customer) {
        this.customer = customer;
        this.populateFormData();
        if (cbxCustomerID.isVisible()) { // in View mode
            cbxCustomerID.setSelectedItem(customer.getID());
            this.refreshShoppingHistory();
        }
    }

    /**
     * Customer instance variable --> GUI
     */
    private void populateFormData() {
        tbxCustomerID.setText(customer.getID());
        lblRegisteredDate.setText(customer.getRegistrationInfo());
        tbxFirstName.setText(customer.getFirstName());
        tbxLastName.setText(customer.getLastName());
        switch (customer.getGender()) {
            case 'M':
                rdbMale.setSelected(true);
                break;
            case 'F':
                rdbFemale.setSelected(true);
                break;
            default:
                rdbNull.setSelected(true);
                break;
        }
        this.setBirthDayToGUI(customer.getBirthDay());
        tbxPhone.setText(customer.getPhoneNumber());
        tbxEmail.setText(customer.getEmailAddress());
    }

    /**
     * GUI --> Customer instance variable
     */
    private void collectFormData() {
        customer.setID(tbxCustomerID.getText());
        customer.setFirstName(tbxFirstName.getText().trim());
        customer.setLastName(tbxLastName.getText().trim());
        customer.setGender(rdbMale.isSelected()
                ? 'M' : rdbFemale.isSelected() ? 'F' : '\0');
        customer.setBirthDay(this.getBirthDayFromGUI());
        customer.setPhoneNumber(tbxPhone.getText().replace("-", "").replace(" ", ""));
        customer.setEmailAddress(tbxEmail.getText().trim());
        // display warnings
//        if (customer.getFirstName() == null || customer.getFirstName().isEmpty()) {
//            lblWarnName.setText(Const.ECIW_WARNING_NAME);
//        }
    }

    private void refreshShoppingHistory() {
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
        shoppingHistory = database.queryCustomerShoppingHistory(customer.getID(), dateFrom, dateTo);
        tableSale.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Date/Time", "Items", "Discount", "Total"};

            @Override
            public int getRowCount() {
                return shoppingHistory.size();
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object[] row = shoppingHistory.get(rowIndex);
                if (columnIndex == 0) {
                    LocalDate date = (LocalDate) row[0];
                    LocalTime time = (LocalTime) row[1];
                    return date.toString() + " " + time.format(DateTimeFormatter.ofPattern("HH:mm"));
                } else if (columnIndex == 1) {
                    return String.format("%,d ", (Integer) row[2]);
                } else if (columnIndex == 2) {
                    return String.format("%.2f %% ", (Double) row[3]);
                } else if (columnIndex == 3) {
                    return String.format("%,.2f " + Const.CURRENCY + " ", (Double) row[4]);
                } else {
                    return null;
                }
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }
        });

        // setting column  sizes
        tableSale.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        final int width_datetime = 120, width_numbers = 60;
        TableColumnModel colm = tableSale.getColumnModel();

        colm.getColumn(0).setMinWidth(width_datetime);
        colm.getColumn(0).setMaxWidth(width_datetime);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(1).setMinWidth(50);
        colm.getColumn(1).setMaxWidth(50);
        colm.getColumn(1).setResizable(false);

        colm.getColumn(2).setMinWidth(width_numbers);
        colm.getColumn(2).setMaxWidth(width_numbers);
        colm.getColumn(2).setResizable(false);

        // set alignments
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        colm.getColumn(1).setCellRenderer(rightRenderer);
        colm.getColumn(2).setCellRenderer(rightRenderer);
        colm.getColumn(3).setCellRenderer(rightRenderer);

        tableSale.updateUI();

        double sum = 0;
        for (Object[] row : shoppingHistory) {
            sum += (Double) row[4];
        }

        lblStats.setText(String.format("%,d records found. Total amount %,.2f ฿. "
                + "Select a sale record on the left side to see more detail on the right.",
                shoppingHistory.size(), sum));

        if (shoppingHistory.size() > 0) {
            tableSale.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    private void showSaleDetail() {
        int row = tableSale.getSelectedRow();
        if (row < 0 || row > shoppingHistory.size()) {
            tableDetails.setModel(new DefaultTableModel(new String[]{
                "Product ID", "Product Name", "Quantity", "Unit Price", "Subtotal"}, 0));
            tableDetails.updateUI();
            return;
        }
        String saleID = (String) shoppingHistory.get(row)[5];
        List<Object[]> details = database.querySingleSaleDetail(saleID);
        tableDetails.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Product ID", "Product Name", "Quantity", "Unit Price", "Subtotal"};

            @Override
            public int getRowCount() {
                return details.size();
            }

            @Override
            public int getColumnCount() {
                return 5;
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
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Date ComboBoxes">
    private void initDateComboBoxes() {
        int currentYear = java.time.Year.now().getValue();

        String[] years = new String[100];
        for (int i = 0, y = currentYear; i < years.length; i++) {
            years[i] = String.valueOf(y - i);
        }

        cbxBirthdayYear.setModel(new DefaultComboBoxModel<>(years));
        this.updateDayComboBox();

        cbxBirthdayMonth.addActionListener((ActionEvent) -> {
            this.updateDayComboBox();
        });
        cbxBirthdayYear.addActionListener((ActionEvent) -> {
            this.updateDayComboBox();
        });

        chkUnknownBirthday.addActionListener((ActionEvent) -> {
            boolean notUnknown = !chkUnknownBirthday.isSelected();
            cbxBirthdayDay.setEnabled(notUnknown);
            cbxBirthdayMonth.setEnabled(notUnknown);
            cbxBirthdayYear.setEnabled(notUnknown);
        });
    }

    private void updateDayComboBox() {
        int year = Integer.parseInt(cbxBirthdayYear.getSelectedItem().toString());
        int month = cbxBirthdayMonth.getSelectedIndex() + 1;
        int dayCount = LocalDate.of(year, month, 1).lengthOfMonth();

        String[] days = new String[dayCount];
        for (int i = 1; i <= dayCount; i++) {
            days[i - 1] = String.format("%02d", i);
        }

        int currentSelect = cbxBirthdayDay.getSelectedIndex();
        cbxBirthdayDay.setModel(new DefaultComboBoxModel<>(days));
        cbxBirthdayDay.setSelectedIndex(Math.min(currentSelect, dayCount - 1));
    }

    private LocalDate getBirthDayFromGUI() {
        if (chkUnknownBirthday.isSelected()) {
            return null;
        }
        int year = Integer.parseInt(cbxBirthdayYear.getSelectedItem().toString());
        int month = cbxBirthdayMonth.getSelectedIndex() + 1;
        int day = cbxBirthdayDay.getSelectedIndex() + 1;
        return LocalDate.of(year, month, day);
    }

    private void setBirthDayToGUI(LocalDate date) {
        if (date == null) {
            chkUnknownBirthday.setSelected(true);
            return;
        }
        chkUnknownBirthday.setSelected(false);
        int currentYear = LocalDate.now().getYear();
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        cbxBirthdayDay.setSelectedIndex(day - 1);
        cbxBirthdayMonth.setSelectedIndex(month - 1);
        cbxBirthdayYear.setSelectedIndex(currentYear - year);
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    private void initializeAddMode() {
        headerLabel.setText(Const.ECIW_HEADER_ADD);
        //<editor-fold defaultstate="collapsed" desc="Enable/Disable Components">
        btnDelete.setVisible(false);
        btnSave.setVisible(true);
        btnCancel.setVisible(true);
        btnCancel.setText("Cancel");

        tbxCustomerID.setEditable(false); // don't allow manually setting an ID
        tbxFirstName.setEnabled(true);
        tbxLastName.setEnabled(true);
        rdbMale.setEnabled(true);
        rdbFemale.setEnabled(true);
        rdbNull.setEnabled(true);
        cbxBirthdayDay.setEnabled(true);
        cbxBirthdayMonth.setEnabled(true);
        cbxBirthdayYear.setEnabled(true);
        chkUnknownBirthday.setEnabled(true);
        chkUnknownBirthday.setSelected(false);
        tbxPhone.setEnabled(true);
        tbxEmail.setEnabled(true);

        cbxCustomerID.setEnabled(false);
        cbxCustomerID.setVisible(false);

        tabbedPane.remove(1); // hide the shopping history
        //</editor-fold>
        // listeners
        btnSave.addActionListener((ActionEvent) -> {
            this.collectFormData();
            if (database.tryInsertCustomer(customer, this)) {
                SwingUtilities.getWindowAncestor(btnSave).dispose();
            }
        });
        btnCancel.addActionListener((ActionEvent) -> {
            customer = null;
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });
    }

    private void initializeEditMode() {
        headerLabel.setText(Const.ECIW_HEADER_EDIT);
        //<editor-fold defaultstate="collapsed" desc="Enable/Disable Components">
        btnDelete.setVisible(true);
        btnSave.setVisible(true);
        btnCancel.setVisible(true);
        btnCancel.setText("Cancel");

        tbxCustomerID.setEditable(false);
        tbxFirstName.setEnabled(true);
        tbxLastName.setEnabled(true);
        rdbMale.setEnabled(true);
        rdbFemale.setEnabled(true);
        rdbNull.setEnabled(true);
        cbxBirthdayDay.setEnabled(true);
        cbxBirthdayMonth.setEnabled(true);
        cbxBirthdayYear.setEnabled(true);
        chkUnknownBirthday.setEnabled(true);
        chkUnknownBirthday.setSelected(false);
        tbxPhone.setEnabled(true);
        tbxEmail.setEnabled(true);

        cbxCustomerID.setEnabled(false);
        cbxCustomerID.setVisible(false);

        tabbedPane.remove(1); // hide the shopping history
        //</editor-fold>
        // listeners
        btnSave.addActionListener((ActionEvent) -> {
            this.collectFormData();
            if (database.tryUpdateCustomer(customer, this)) {
                SwingUtilities.getWindowAncestor(btnSave).dispose();
            }
        });
        btnCancel.addActionListener((ActionEvent) -> {
            customer = database.queryCustomer(customer.getID());
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });
        btnDelete.addActionListener((ActionEvent) -> {
            if (database.tryDeleteCustomer(customer, this)) {
                customer = null;
                SwingUtilities.getWindowAncestor(btnSave).dispose();
            }
        });
    }

    private void initializeViewMode() {
        headerLabel.setText(Const.ECIW_HEADER_VIEW);
        //<editor-fold defaultstate="collapsed" desc="Enable/Disable Components">
        btnDelete.setVisible(false);
        btnSave.setVisible(false);
        btnCancel.setVisible(true);
        btnCancel.setText("Close");

        tbxCustomerID.setEditable(false);
        tbxFirstName.setEditable(false);
        tbxLastName.setEditable(false);
        rdbMale.setEnabled(false);
        rdbFemale.setEnabled(false);
        rdbNull.setEnabled(false);
        cbxBirthdayDay.setEnabled(false);
        cbxBirthdayMonth.setEnabled(false);
        cbxBirthdayYear.setEnabled(false);
        chkUnknownBirthday.setEnabled(false);
        tbxPhone.setEditable(false);
        tbxEmail.setEditable(false);
        //</editor-fold>

        chkHistoryFiltering.setSelected(false);
        tbxDateFrom.setEnabled(false);
        tbxDateTo.setEnabled(false);

        // listeners: main
        btnCancel.addActionListener((ActionEvent) -> {
            SwingUtilities.getWindowAncestor(btnCancel).dispose();
        });
        // listeners: shopping history
        chkHistoryFiltering.addActionListener((ActionEvent) -> {
            tbxDateFrom.setEnabled(chkHistoryFiltering.isSelected());
            tbxDateTo.setEnabled(chkHistoryFiltering.isSelected());
            this.refreshShoppingHistory();
        });
        btnFilterRefresh.addActionListener((ActionEvent) -> {
            this.refreshShoppingHistory();
        });

        tableSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel selectionModel = tableSale.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            this.showSaleDetail();
        });

        cbxCustomerID.setEnabled(true);
        cbxCustomerID.setVisible(true);
        List<String> customerIDs = database.queryListOfCustomerIDs();
        String[] customerIDsArray = new String[customerIDs.size()];
        customerIDs.toArray(customerIDsArray);
        cbxCustomerID.setModel(new DefaultComboBoxModel<>(customerIDsArray));
        cbxCustomerID.addActionListener((ActionEvent) -> {
            this.setCustomer(database.queryCustomer(cbxCustomerID.getSelectedItem().toString()));
        });
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

        genderButtonGroup = new javax.swing.ButtonGroup();
        panel_header = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        cbxCustomerID = new javax.swing.JComboBox<>();
        tabbedPane = new javax.swing.JTabbedPane();
        panel_customerInfo = new javax.swing.JPanel();
        javax.swing.JLabel l_cusID = new javax.swing.JLabel();
        tbxCustomerID = new javax.swing.JTextField();
        lblWarnID = new javax.swing.JLabel();
        lblRegisteredDate = new javax.swing.JLabel();
        javax.swing.JLabel l_basic = new javax.swing.JLabel();
        javax.swing.JLabel l_first = new javax.swing.JLabel();
        lblWarnName = new javax.swing.JLabel();
        javax.swing.JLabel l_last = new javax.swing.JLabel();
        javax.swing.JLabel l_additional = new javax.swing.JLabel();
        javax.swing.JLabel l_gender = new javax.swing.JLabel();
        panel_gender = new javax.swing.JPanel();
        rdbMale = new javax.swing.JRadioButton();
        rdbFemale = new javax.swing.JRadioButton();
        rdbNull = new javax.swing.JRadioButton();
        javax.swing.JLabel l_birthday = new javax.swing.JLabel();
        panel_birthday = new javax.swing.JPanel();
        cbxBirthdayDay = new javax.swing.JComboBox<>();
        cbxBirthdayMonth = new javax.swing.JComboBox<>();
        cbxBirthdayYear = new javax.swing.JComboBox<>();
        chkUnknownBirthday = new javax.swing.JCheckBox();
        javax.swing.JLabel l_contact = new javax.swing.JLabel();
        javax.swing.JLabel l_phone = new javax.swing.JLabel();
        tbxPhone = new javax.swing.JFormattedTextField();
        javax.swing.JLabel l_email = new javax.swing.JLabel();
        tbxEmail = new javax.swing.JTextField();
        panel_commandButtons = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        tbxFirstName = new javax.swing.JFormattedTextField();
        tbxLastName = new javax.swing.JFormattedTextField();
        panel_shoppingHistory = new javax.swing.JPanel();
        tableSale_scrollPane = new javax.swing.JScrollPane();
        tableSale = new javax.swing.JTable();
        panel_top = new javax.swing.JPanel();
        tbxDateFrom = new javax.swing.JFormattedTextField();
        javax.swing.JLabel l_filterFrom = new javax.swing.JLabel();
        javax.swing.JLabel l_filterTo = new javax.swing.JLabel();
        tbxDateTo = new javax.swing.JFormattedTextField();
        btnFilterRefresh = new javax.swing.JButton();
        chkHistoryFiltering = new javax.swing.JCheckBox();
        panel_bottom = new javax.swing.JPanel();
        lblStats = new javax.swing.JLabel();
        tableDetails_scrollPane = new javax.swing.JScrollPane();
        tableDetails = new javax.swing.JTable();

        setMaximumSize(new java.awt.Dimension(830, 540));
        setMinimumSize(new java.awt.Dimension(830, 540));
        setPreferredSize(new java.awt.Dimension(830, 540));
        setResizable(false);
        setSize(new java.awt.Dimension(830, 540));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Add/View/Edit Customer Information");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 8, 16);
        panel_header.add(headerLabel, gridBagConstraints);

        cbxCustomerID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxCustomerID.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 16);
        panel_header.add(cbxCustomerID, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        getContentPane().add(panel_header, gridBagConstraints);

        tabbedPane.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N

        panel_customerInfo.setLayout(new java.awt.GridBagLayout());

        l_cusID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_cusID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_cusID.setText("Customer ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 6, 4);
        panel_customerInfo.add(l_cusID, gridBagConstraints);

        tbxCustomerID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxCustomerID.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxCustomerID.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        panel_customerInfo.add(tbxCustomerID, gridBagConstraints);

        lblWarnID.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblWarnID.setForeground(java.awt.Color.red);
        lblWarnID.setText("This ID is already used.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 6, 16);
        panel_customerInfo.add(lblWarnID, gridBagConstraints);

        lblRegisteredDate.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblRegisteredDate.setText("Registered: 31 January 2015");
        lblRegisteredDate.setPreferredSize(new java.awt.Dimension(188, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 20, 8);
        panel_customerInfo.add(lblRegisteredDate, gridBagConstraints);

        l_basic.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        l_basic.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_basic.setText("Basic Information:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 16, 4, 4);
        panel_customerInfo.add(l_basic, gridBagConstraints);

        l_first.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_first.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_first.setText("First Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_customerInfo.add(l_first, gridBagConstraints);

        lblWarnName.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblWarnName.setForeground(java.awt.Color.red);
        lblWarnName.setText("The first name cannot be blank.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 16);
        panel_customerInfo.add(lblWarnName, gridBagConstraints);

        l_last.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_last.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_last.setText("Last Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_customerInfo.add(l_last, gridBagConstraints);

        l_additional.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        l_additional.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_additional.setText("Additional Information:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 16, 4, 4);
        panel_customerInfo.add(l_additional, gridBagConstraints);

        l_gender.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_gender.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_gender.setText("Gender:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 4, 4);
        panel_customerInfo.add(l_gender, gridBagConstraints);

        panel_gender.setLayout(new java.awt.GridBagLayout());

        genderButtonGroup.add(rdbMale);
        rdbMale.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        rdbMale.setText("Male");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        panel_gender.add(rdbMale, gridBagConstraints);

        genderButtonGroup.add(rdbFemale);
        rdbFemale.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        rdbFemale.setText("Female");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        panel_gender.add(rdbFemale, gridBagConstraints);

        genderButtonGroup.add(rdbNull);
        rdbNull.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        rdbNull.setSelected(true);
        rdbNull.setText("Unknown");
        rdbNull.setPreferredSize(new java.awt.Dimension(80, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panel_gender.add(rdbNull, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 4, 8);
        panel_customerInfo.add(panel_gender, gridBagConstraints);

        l_birthday.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_birthday.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_birthday.setText("Birthday:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_customerInfo.add(l_birthday, gridBagConstraints);

        panel_birthday.setLayout(new java.awt.GridBagLayout());

        cbxBirthdayDay.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxBirthdayDay.setMaximumRowCount(12);
        cbxBirthdayDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03" }));
        cbxBirthdayDay.setPreferredSize(new java.awt.Dimension(40, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        panel_birthday.add(cbxBirthdayDay, gridBagConstraints);

        cbxBirthdayMonth.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxBirthdayMonth.setMaximumRowCount(12);
        cbxBirthdayMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        cbxBirthdayMonth.setPreferredSize(new java.awt.Dimension(80, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        panel_birthday.add(cbxBirthdayMonth, gridBagConstraints);

        cbxBirthdayYear.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxBirthdayYear.setMaximumRowCount(12);
        cbxBirthdayYear.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2016", "2015", "2014" }));
        cbxBirthdayYear.setPreferredSize(new java.awt.Dimension(60, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        panel_birthday.add(cbxBirthdayYear, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        panel_customerInfo.add(panel_birthday, gridBagConstraints);

        chkUnknownBirthday.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkUnknownBirthday.setText("Unknown Birthday");
        chkUnknownBirthday.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        chkUnknownBirthday.setPreferredSize(new java.awt.Dimension(188, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 8);
        panel_customerInfo.add(chkUnknownBirthday, gridBagConstraints);

        l_contact.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        l_contact.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_contact.setText("Contact Information:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(16, 16, 4, 4);
        panel_customerInfo.add(l_contact, gridBagConstraints);

        l_phone.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_phone.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_phone.setText("Phone:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(16, 4, 4, 4);
        panel_customerInfo.add(l_phone, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 4, 4, 8);
        panel_customerInfo.add(tbxPhone, gridBagConstraints);

        l_email.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_email.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_email.setText("Email:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_customerInfo.add(l_email, gridBagConstraints);

        tbxEmail.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxEmail.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxEmail.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        panel_customerInfo.add(tbxEmail, gridBagConstraints);

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 502, Short.MAX_VALUE)
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
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panel_customerInfo.add(panel_commandButtons, gridBagConstraints);

        try {
            tbxFirstName.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        tbxFirstName.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        tbxFirstName.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxFirstName.setMaximumSize(new java.awt.Dimension(188, 22));
        tbxFirstName.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxFirstName.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        panel_customerInfo.add(tbxFirstName, gridBagConstraints);

        try {
            tbxLastName.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        tbxLastName.setFocusLostBehavior(javax.swing.JFormattedTextField.PERSIST);
        tbxLastName.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxLastName.setMaximumSize(new java.awt.Dimension(188, 22));
        tbxLastName.setMinimumSize(new java.awt.Dimension(188, 22));
        tbxLastName.setPreferredSize(new java.awt.Dimension(188, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 8);
        panel_customerInfo.add(tbxLastName, gridBagConstraints);

        tabbedPane.addTab("Customer Information", panel_customerInfo);

        panel_shoppingHistory.setLayout(new java.awt.GridBagLayout());

        tableSale.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableSale.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date/Time", "Items", "Discount", "Total"
            }
        ));
        tableSale.setGridColor(new java.awt.Color(204, 204, 204));
        tableSale.setRowHeight(20);
        tableSale.getTableHeader().setReorderingAllowed(false);
        tableSale_scrollPane.setViewportView(tableSale);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        panel_shoppingHistory.add(tableSale_scrollPane, gridBagConstraints);

        panel_top.setLayout(new java.awt.GridBagLayout());

        tbxDateFrom.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        tbxDateFrom.setText("2016-01-01");
        tbxDateFrom.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxDateFrom.setPreferredSize(new java.awt.Dimension(80, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        panel_top.add(tbxDateFrom, gridBagConstraints);

        l_filterFrom.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterFrom.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterFrom.setText("From:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 4);
        panel_top.add(l_filterFrom, gridBagConstraints);

        l_filterTo.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterTo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterTo.setText("To:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 16, 0, 4);
        panel_top.add(l_filterTo, gridBagConstraints);

        tbxDateTo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        tbxDateTo.setText("2016-02-01");
        tbxDateTo.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxDateTo.setPreferredSize(new java.awt.Dimension(80, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        panel_top.add(tbxDateTo, gridBagConstraints);

        btnFilterRefresh.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnFilterRefresh.setText("Refresh");
        btnFilterRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFilterRefresh.setMaximumSize(new java.awt.Dimension(128, 36));
        btnFilterRefresh.setMinimumSize(new java.awt.Dimension(128, 36));
        btnFilterRefresh.setName(""); // NOI18N
        btnFilterRefresh.setPreferredSize(new java.awt.Dimension(96, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 0, 8);
        panel_top.add(btnFilterRefresh, gridBagConstraints);

        chkHistoryFiltering.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkHistoryFiltering.setSelected(true);
        chkHistoryFiltering.setText("Enable Filtering:");
        chkHistoryFiltering.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 8);
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        panel_shoppingHistory.add(tableDetails_scrollPane, gridBagConstraints);

        tabbedPane.addTab("Shopping History", panel_shoppingHistory);

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
    private javax.swing.JButton btnFilterRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbxBirthdayDay;
    private javax.swing.JComboBox<String> cbxBirthdayMonth;
    private javax.swing.JComboBox<String> cbxBirthdayYear;
    private javax.swing.JComboBox<String> cbxCustomerID;
    private javax.swing.JCheckBox chkHistoryFiltering;
    private javax.swing.JCheckBox chkUnknownBirthday;
    private javax.swing.ButtonGroup genderButtonGroup;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel lblRegisteredDate;
    private javax.swing.JLabel lblStats;
    private javax.swing.JLabel lblWarnID;
    private javax.swing.JLabel lblWarnName;
    private javax.swing.JPanel panel_birthday;
    private javax.swing.JPanel panel_bottom;
    private javax.swing.JPanel panel_commandButtons;
    private javax.swing.JPanel panel_customerInfo;
    private javax.swing.JPanel panel_gender;
    private javax.swing.JPanel panel_header;
    private javax.swing.JPanel panel_shoppingHistory;
    private javax.swing.JPanel panel_top;
    private javax.swing.JRadioButton rdbFemale;
    private javax.swing.JRadioButton rdbMale;
    private javax.swing.JRadioButton rdbNull;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tableDetails;
    private javax.swing.JScrollPane tableDetails_scrollPane;
    private javax.swing.JTable tableSale;
    private javax.swing.JScrollPane tableSale_scrollPane;
    private javax.swing.JTextField tbxCustomerID;
    private javax.swing.JFormattedTextField tbxDateFrom;
    private javax.swing.JFormattedTextField tbxDateTo;
    private javax.swing.JTextField tbxEmail;
    private javax.swing.JFormattedTextField tbxFirstName;
    private javax.swing.JFormattedTextField tbxLastName;
    private javax.swing.JFormattedTextField tbxPhone;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    //<editor-fold desc="Static showDialog() Methods">
    /**
     * Show a dialog for registering a new customer.
     *
     * @param owner The window that calls the dialog.
     * @return A newly created Customer. Null if the user canceled.
     */
    public static Customer showNewCustomerDialog(Frame owner) {
        EditCustomerInfoWindow win = new EditCustomerInfoWindow(InfoWindowMode.ADD);
        win.setCustomer(Customer.createNewCustomer(database.suggestNextCustomerID()));
        win.chkUnknownBirthday.setSelected(false);

        Util.createAndShowDialog(owner, "Customer Information - " + Const.APP_TITLE,
                win.getContentPane(), win.getPreferredSize());
        System.out.println("showNewCustomerDialog() returning: " + win.customer);
        return win.customer;
    }

    /**
     * Show a dialog for editing info of an existing customer.
     *
     * @param owner The window that calls the dialog.
     * @param customer The Customer to be edited.
     * @return The same Customer instance whether the user save their edits or
     * not. Null if the user deleted the Customer data.
     */
    public static Customer showEditCustomerDialog(Frame owner, Customer customer) {
        EditCustomerInfoWindow win = new EditCustomerInfoWindow(InfoWindowMode.EDIT);
        win.setCustomer(customer);

        Util.createAndShowDialog(owner, "Customer Information - " + Const.APP_TITLE,
                win.getContentPane(), win.getPreferredSize());
        System.out.println("showEditCustomerDialog() returning: " + win.customer);
        return win.customer;
    }

    /**
     * Show a dialog that allows the user to view info of an existing customer,
     * including their shopping history.
     *
     * @param owner The window that calls the dialog.
     * @param customer The Customer to be viewed.
     */
    public static void showViewCustomerDialog(Frame owner, Customer customer) {
        EditCustomerInfoWindow win = new EditCustomerInfoWindow(InfoWindowMode.VIEW);
        win.setCustomer(customer);
        win.refreshShoppingHistory();

        Util.createAndShowDialog(owner, "Customer Information - " + Const.APP_TITLE,
                win.getContentPane(), win.getPreferredSize());
    }

    //</editor-fold>
}
