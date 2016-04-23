package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.gui.charts_new.BarChart;
import database.test.gui.charts_new.PieChart;
import database.test.gui.charts_new.SalesLineChart;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

public class SaleStatisticWindow
        extends DataDisplayWindow {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();

    private int periodMode;
    private String interestedObject;

    public SaleStatisticWindow() {
        this.initComponents();
        this.setColorTheme();
        this.initListeners();

        this.setLocationRelativeTo(null);
        this.setTitle("Sale Statistics - " + Const.APP_TITLE);
        this.populateComboBoxData();

        tglMonths.setSelected(true);
    }

    //<editor-fold desc="First Tab: Number of Units Sold">
    private LocalDate[] getFilterDate() {
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
        return new LocalDate[]{dateFrom, dateTo};
    }

    @Override
    public void refresh() {
        LocalDate[] dates = getFilterDate();
        LocalDate dateFrom = dates[0];
        LocalDate dateTo = dates[1];

        List<Object[]> productSold = database.queryUnitSoldByProducts(dateFrom, dateTo, false);
        tableProductUnitSold.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Product ID", "Product Name", "Unit Sold"};

            @Override
            public int getRowCount() {
                return productSold.size();
            }

            @Override
            public int getColumnCount() {
                return COLUMNS.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return productSold.get(rowIndex)[columnIndex];
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Integer.class;
                } else {
                    return String.class;
                }
            }
        });

        tableProductUnitSold.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel colm = tableProductUnitSold.getColumnModel();

        colm.getColumn(0).setMinWidth(80);
        colm.getColumn(0).setMaxWidth(80);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(2).setMinWidth(80);
        colm.getColumn(2).setMaxWidth(80);
        colm.getColumn(2).setResizable(false);
        colm.getColumn(2).setCellRenderer(Util.TABLE_CELL_INTEGER);

        tableProductUnitSold.updateUI();

        List<Object[]> categorySold = database.queryUnitSoldByProducts(dateFrom, dateTo, true);
        tableCategoryUnitSold.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Category ID", "Category Name", "Unit Sold"};

            @Override
            public int getRowCount() {
                return categorySold.size();
            }

            @Override
            public int getColumnCount() {
                return COLUMNS.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return categorySold.get(rowIndex)[columnIndex];
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) {
                    return Integer.class;
                } else {
                    return String.class;
                }
            }
        });

        tableCategoryUnitSold.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        colm = tableCategoryUnitSold.getColumnModel();

        colm.getColumn(0).setMinWidth(80);
        colm.getColumn(0).setMaxWidth(80);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(2).setMinWidth(80);
        colm.getColumn(2).setMaxWidth(80);
        colm.getColumn(2).setResizable(false);
        colm.getColumn(2).setCellRenderer(Util.TABLE_CELL_INTEGER);

        tableCategoryUnitSold.updateUI();

        int sumQty = 0;
        for (Object[] row : categorySold) {
            sumQty += (int) row[2];
        }

        if (chkHistoryFiltering.isSelected()) {
            lblStats.setText(String.format("A total of %,d items "
                    + "(%,d products from %,d categories) "
                    + "has been sold between %s and %s.",
                    sumQty, productSold.size(), categorySold.size(), dateFrom, dateTo));
        } else {
            lblStats.setText(String.format("A total of %,d items "
                    + "(%,d products from %,d categories) has been sold.",
                    sumQty, productSold.size(), categorySold.size()));
        }

        this.updateButtonsEnabled();
    }

    private void updateButtonsEnabled() {
        btnViewCategoryChart.setEnabled(tableCategoryUnitSold.getRowCount() != 0);
        btnViewProductChart.setEnabled(tableProductUnitSold.getRowCount() != 0
                && tableProductUnitSold.getSelectedRowCount() > 0
                && tableProductUnitSold.getSelectedRow() < tableProductUnitSold.getRowCount());
    }

    public void viewProductChart() {
        int rowCount = tableProductUnitSold.getRowCount();
        if (rowCount == 0) {
            return;
        }

        String title = "Unit Sold by Products";
        if (chkHistoryFiltering.isSelected()) {
            LocalDate[] dates = getFilterDate();
            LocalDate dateFrom = dates[0];
            LocalDate dateTo = dates[1];
            title += " - Between " + dateFrom + " and " + dateTo;
        }

        BarChart bar = new BarChart(title, "Product", "Unit Sold");
        for (int i : tableProductUnitSold.getSelectedRows()) {
            bar.addData((Number) tableProductUnitSold.getValueAt(i, 2), "",
                    (String) tableProductUnitSold.getValueAt(i, 0));
        }
        bar.createAndShow();
    }

    public void viewCategoryChart() {
        int rowCount = tableCategoryUnitSold.getRowCount();
        if (rowCount == 0) {
            return;
        }

        String title = "Unit Sold by Categories";
        if (chkHistoryFiltering.isSelected()) {
            LocalDate[] dates = getFilterDate();
            LocalDate dateFrom = dates[0];
            LocalDate dateTo = dates[1];
            title += " - Between " + dateFrom + " and " + dateTo;
        }

        PieChart pie = new PieChart(title);
        for (int i = 0; i < rowCount; i++) {
            pie.addData((String) tableCategoryUnitSold.getValueAt(i, 1),
                    (Number) tableCategoryUnitSold.getValueAt(i, 2));
        }
        pie.createAndShow();
    }
    //</editor-fold>

    public static final int PERIOD_MODE_MONTHS = 1;
    public static final int PERIOD_MODE_WEEKS = 2;
    public static final int PERIOD_MODE_DAYS = 3;

    public static final String SQL_CONDITION_CATEGORY_FORMAT = " AND (p.category_id = '%s') ";
    public static final String SQL_CONDITION_PRODUCT_FORMAT = " AND (p.product_id = '%s') ";

    public void statRefresh() {
        LocalDate[] range = getSpecifiedRange();
        periodMode = tglMonths.isSelected() ? PERIOD_MODE_MONTHS
                : tglWeeks.isSelected() ? PERIOD_MODE_WEEKS : PERIOD_MODE_DAYS;

        if (periodMode == PERIOD_MODE_MONTHS || periodMode == PERIOD_MODE_WEEKS) {
            this.loadStatByMonthOrWeek(periodMode == PERIOD_MODE_WEEKS,
                    range[0], range[1], this.getAdditionalPredicate());
        } else {
            this.loadStatByDay(range[0], range[1], this.getAdditionalPredicate());
        }

    }

    private LocalDate[] getSpecifiedRange() {
        LocalDate dateFrom, dateTo;
        if (chkSpecifyRange.isSelected()) {
            try {
                dateFrom = LocalDate.parse(tbxDateFrom1.getText());
            } catch (DateTimeParseException ex) {
                dateFrom = Const.SQL_MINDATE;
                tbxDateFrom1.setText(Const.SQL_MINDATE.toString());
            }
            try {
                dateTo = LocalDate.parse(tbxDateTo1.getText());
            } catch (DateTimeParseException ex) {
                dateTo = Const.SQL_MAXDATE;
                tbxDateTo1.setText(Const.SQL_MAXDATE.toString());
            }
        } else {
            dateFrom = Const.SQL_MINDATE;
            dateTo = Const.SQL_MAXDATE;
        }
        return new LocalDate[]{dateFrom, dateTo};
    }

    private String getAdditionalPredicate() {
        if (rdoProduct.isSelected()) {
            interestedObject = "Product: " + cbxProduct.getSelectedItem().toString();
            return String.format(SQL_CONDITION_PRODUCT_FORMAT, cbxProduct.getSelectedItem().toString());
        } else if (rdoCategory.isSelected()) {
            interestedObject = "Category: " + cbxCategory.getSelectedItem().toString();
            return String.format(SQL_CONDITION_CATEGORY_FORMAT, cbxCategory.getSelectedItem().toString());
        } else {
            interestedObject = "All Products / Categories";
            return "";
        }
    }

    public static final String SUMMARY_FORMAT = "Summary: Total Unit Sold = %,d (Average %,.2f / %s), Total Income = %,.2f %s (Average %,.2f %s / %s).";

    public void loadStatByMonthOrWeek(boolean isWeek, LocalDate dateFrom, LocalDate dateTo, String additionalPredicate) {
        List<Object[]> stats = database.querySaleStatByMonthOrWeek(isWeek, dateFrom, dateTo, additionalPredicate);

        // fill in missing periods
        if (isWeek && !stats.isEmpty()) {
            String first, last;
            int y_first, w_first, y_last, w_last;
            if (chkSpecifyRange.isSelected()) {
                first = database.selectYearWeek(dateFrom);
                last = database.selectYearWeek(dateTo);
                y_first = Integer.parseInt(first.substring(0, 4));
                w_first = Integer.parseInt(first.substring(4));
                y_last = Integer.parseInt(last.substring(0, 4));
                w_last = Integer.parseInt(last.substring(4));
            } else {
                first = (String) stats.get(0)[0];
                last = database.selectYearWeek(LocalDate.now());
                y_first = Integer.parseInt(first.substring(0, 4));
                w_first = Integer.parseInt(first.substring(6));
                y_last = Integer.parseInt(last.substring(0, 4));
                w_last = Integer.parseInt(last.substring(4));
            }
            int idx = 0;
            while (idx < stats.size() && y_first * 100 + w_first <= y_last * 100 + w_last) {
                String yw = String.format("%04d-W%02d", y_first, w_first);
                if (yw.compareTo((String) stats.get(idx)[0]) < 0) {
                    stats.add(idx, new Object[]{yw, 0, 0.00, 0.00, 0.00});
                }
                w_first++;
                if (w_first >= 53) {
                    y_first++;
                    w_first = 1;
                }
                idx++;
            }
            while (y_first * 100 + w_first <= y_last * 100 + w_last) {
                String yw = String.format("%04d-W%02d", y_first, w_first);
                stats.add(new Object[]{yw, 0, 0.00, 0.00, 0.00});
                w_first++;
                if (w_first >= 53) {
                    y_first++;
                    w_first = 1;
                }
                idx++;
            }
        } else if (!stats.isEmpty()) { // month
            String first, last;
            int y_first, m_first, y_last, m_last;
            if (chkSpecifyRange.isSelected()) {
                y_first = dateFrom.getYear();
                m_first = dateFrom.getMonthValue();
                y_last = dateTo.getYear();
                m_last = dateTo.getMonthValue();
            } else {
                first = (String) stats.get(0)[0];
                y_first = Integer.parseInt(first.substring(0, 4));
                m_first = Integer.parseInt(first.substring(5));
                y_last = LocalDate.now().getYear();
                m_last = LocalDate.now().getMonthValue();
            }
            int idx = 0;
            while (idx < stats.size() && y_first * 100 + m_first <= y_last * 100 + m_last) {
                String ym = String.format("%04d-%02d", y_first, m_first);
                if (ym.compareTo((String) stats.get(idx)[0]) < 0) {
                    stats.add(idx, new Object[]{ym, 0, 0.00, 0.00, 0.00});
                }
                m_first++;
                if (m_first > 12) {
                    y_first++;
                    m_first = 1;
                }
                idx++;
            }
            while (y_first * 100 + m_first <= y_last * 100 + m_last) {
                String yw = String.format("%04d-%02d", y_first, m_first);
                stats.add(new Object[]{yw, 0, 0.00, 0.00, 0.00});
                m_first++;
                if (m_first > 12) {
                    y_first++;
                    m_first = 1;
                }
                idx++;
            }
        }

        tableStats.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {isWeek ? "Year-Week" : "Year-Month", "Total Unit Sold", "Avg Unit Sold / Day",
                "Total Income", "Avg Income / Day"};

            @Override
            public int getRowCount() {
                return stats.size();
            }

            @Override
            public int getColumnCount() {
                return COLUMNS.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return stats.get(rowIndex)[columnIndex];
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return String.class;
                } else if (columnIndex == 1) {
                    return Integer.class;
                } else {
                    return Double.class;
                }
            }
        });

        tableStats.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel colm = tableStats.getColumnModel();

        colm.getColumn(0).setMinWidth(80);
        colm.getColumn(0).setMaxWidth(80);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(1).setCellRenderer(Util.TABLE_CELL_INTEGER_ZERO_NONE);
        colm.getColumn(2).setCellRenderer(Util.TABLE_CELL_DOUBLE);
        colm.getColumn(3).setCellRenderer(Util.TABLE_CELL_MONEY);
        colm.getColumn(4).setCellRenderer(Util.TABLE_CELL_MONEY);

        tableStats.updateUI();

        int sum = 0;
        double avgu = 0, tinc = 0, avginc = 0;
        for (int i = 0; i < stats.size(); i++) {
            sum += (int) stats.get(i)[1];
            tinc += (double) stats.get(i)[3];
        }
        avgu = ((double) sum) / stats.size();
        avginc = tinc / stats.size();

        String prd = isWeek ? "Week" : "Month";
        lblSummary.setText(String.format(SUMMARY_FORMAT, sum, avgu, prd,
                tinc, Const.CURRENCY, avginc, Const.CURRENCY, prd));

        btnViewAvgChart.setEnabled(true);
    }

    public void loadStatByDay(LocalDate dateFrom, LocalDate dateTo, String additionalPredicate) {
        List<Object[]> stats = database.querySaleStatByDay(dateFrom, dateTo, additionalPredicate);

        if (!stats.isEmpty()) {
            // fill in missing dates
            if (!chkSpecifyRange.isSelected()) {
                dateFrom = (LocalDate) stats.get(0)[0];
                dateTo = LocalDate.now();
            }
            LocalDate temp = dateFrom;
            int idx = 0;
            while (idx < stats.size() && temp.compareTo(dateTo) <= 0) {
                if (((LocalDate) stats.get(idx)[0]).isAfter(temp)) {
                    Object[] row = new Object[]{LocalDate.parse(temp.toString()), 0, 0.00};
                    stats.add(idx, row);
                }
                temp = temp.plusDays(1);
                idx++;
            }
            while (temp.compareTo(dateTo) <= 0) {
                Object[] row = new Object[]{LocalDate.parse(temp.toString()), 0, 0.00};
                stats.add(idx, row);
                temp = temp.plusDays(1);
                idx++;
            }
        }

        tableStats.setModel(new AbstractTableModel() {
            final String[] COLUMNS = {"Date", "Total Unit Sold", "Total Income"};

            @Override
            public int getRowCount() {
                return stats.size();
            }

            @Override
            public int getColumnCount() {
                return COLUMNS.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return stats.get(rowIndex)[columnIndex];
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return String.class;
                } else if (columnIndex == 1) {
                    return Integer.class;
                } else {
                    return Double.class;
                }
            }
        });

        tableStats.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel colm = tableStats.getColumnModel();

        colm.getColumn(0).setMinWidth(80);
        colm.getColumn(0).setMaxWidth(80);
        colm.getColumn(0).setResizable(false);

        colm.getColumn(1).setCellRenderer(Util.TABLE_CELL_INTEGER_ZERO_NONE);
        colm.getColumn(2).setCellRenderer(Util.TABLE_CELL_MONEY);

        tableStats.updateUI();

        int sum = 0;
        double avgu = 0, tinc = 0, avginc = 0;
        for (int i = 0; i < stats.size(); i++) {
            sum += (int) stats.get(i)[1];
            tinc += (double) stats.get(i)[2];
        }
        avgu = ((double) sum) / stats.size();
        avginc = tinc / stats.size();

        lblSummary.setText(String.format(SUMMARY_FORMAT, sum, avgu, "Day",
                tinc, Const.CURRENCY, avginc, Const.CURRENCY, "Day"));

        btnViewAvgChart.setEnabled(false);
    }

    public void viewTotalChart() {
        int rowCount = tableStats.getRowCount();
        if (rowCount == 0) {
            return;
        }

        String title = "Total Unit Sold and Income";
        if (periodMode == PERIOD_MODE_DAYS) {
            title += " (Daily)";
        } else if (periodMode == PERIOD_MODE_WEEKS) {
            title += " (Weekly)";
        } else if (periodMode == PERIOD_MODE_MONTHS) {
            title += " (Monthly)";
        }
        title += "\n" + interestedObject;
        if (chkSpecifyRange.isSelected()) {
            LocalDate[] dates = getSpecifiedRange();
            LocalDate dateFrom = dates[0];
            LocalDate dateTo = dates[1];
            title += " - Between " + dateFrom + " and " + dateTo;
        }

        SalesLineChart chart = new SalesLineChart(title, "Unit Sold", "Income", "Time Period", "Unit Sold");

        if (periodMode == PERIOD_MODE_DAYS) {
            for (int i = 0; i < tableStats.getRowCount(); i++) {
                chart.addData1((LocalDate) tableStats.getValueAt(i, 0), (Number) tableStats.getValueAt(i, 1));
                chart.addData2((LocalDate) tableStats.getValueAt(i, 0), (Number) tableStats.getValueAt(i, 2));
            }
        } else if (periodMode == PERIOD_MODE_WEEKS) {
            for (int i = 0; i < tableStats.getRowCount(); i++) {
                String yw = (String) tableStats.getValueAt(i, 0);
                LocalDate date = LocalDate.parse(yw + "-6", DateTimeFormatter.ISO_WEEK_DATE);
                chart.addData1(date, (Number) tableStats.getValueAt(i, 1));
                chart.addData2(date, (Number) tableStats.getValueAt(i, 3));
            }
        } else if (periodMode == PERIOD_MODE_MONTHS) {
            for (int i = 0; i < tableStats.getRowCount(); i++) {
                String ym = (String) tableStats.getValueAt(i, 0);
                int y = Integer.parseInt(ym.substring(0, 4));
                int m = Integer.parseInt(ym.substring(5));
                LocalDate date = LocalDate.of(y, m, 1);
                date = LocalDate.of(y, m, date.lengthOfMonth());
                chart.addData1(date, (Number) tableStats.getValueAt(i, 1));
                chart.addData2(date, (Number) tableStats.getValueAt(i, 3));
            }
        }

        chart.createAndShow();
    }

    public void viewAveragesChart() {
        int rowCount = tableStats.getRowCount();
        if (rowCount == 0) {
            return;
        }

        String title = "Average Unit Sold and Income";
        if (periodMode == PERIOD_MODE_WEEKS) {
            title += " (Weekly)";
        } else if (periodMode == PERIOD_MODE_MONTHS) {
            title += " (Monthly)";
        }
        title += "\n" + interestedObject;
        if (chkSpecifyRange.isSelected()) {
            LocalDate[] dates = getSpecifiedRange();
            LocalDate dateFrom = dates[0];
            LocalDate dateTo = dates[1];
            title += " - Between " + dateFrom + " and " + dateTo;
        }

        SalesLineChart chart = new SalesLineChart(title, "Unit Sold", "Income", "Time Period", "Unit Sold");

        if (periodMode == PERIOD_MODE_WEEKS) {
            for (int i = 0; i < tableStats.getRowCount(); i++) {
                String yw = (String) tableStats.getValueAt(i, 0);
                LocalDate date = LocalDate.parse(yw + "-6", DateTimeFormatter.ISO_WEEK_DATE);
                chart.addData1(date, (Number) tableStats.getValueAt(i, 2));
                chart.addData2(date, (Number) tableStats.getValueAt(i, 4));
            }
        } else if (periodMode == PERIOD_MODE_MONTHS) {
            for (int i = 0; i < tableStats.getRowCount(); i++) {
                String ym = (String) tableStats.getValueAt(i, 0);
                int y = Integer.parseInt(ym.substring(0, 4));
                int m = Integer.parseInt(ym.substring(5));
                LocalDate date = LocalDate.of(y, m, 1);
                date = LocalDate.of(y, m, date.lengthOfMonth());
                chart.addData1(date, (Number) tableStats.getValueAt(i, 2));
                chart.addData2(date, (Number) tableStats.getValueAt(i, 4));
            }
        }

        chart.createAndShow();
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    private void setColorTheme() {
        tableCategoryUnitSold.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tableCategoryUnitSold.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tableCategoryUnitSold.setGridColor(Const.COLOR_TABLE_GRID);
        tableCategoryUnitSold.setFont(Const.FONT_DEFAULT_12);
        tableCategoryUnitSold.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tableCategoryUnitSold.setRowHeight(24);

        tableProductUnitSold.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tableProductUnitSold.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tableProductUnitSold.setGridColor(Const.COLOR_TABLE_GRID);
        tableProductUnitSold.setFont(Const.FONT_DEFAULT_12);
        tableProductUnitSold.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tableProductUnitSold.setRowHeight(24);

        tableStats.setSelectionBackground(Const.COLOR_HIGHLIGHT_BG);
        tableStats.setSelectionForeground(Const.COLOR_HIGHLIGHT_FG);
        tableStats.setGridColor(Const.COLOR_TABLE_GRID);
        tableStats.setFont(Const.FONT_DEFAULT_12);
        tableStats.getTableHeader().setFont(Const.FONT_DEFAULT_12);
        tableStats.setRowHeight(24);
    }

    private void initListeners() {
        chkHistoryFiltering.addActionListener((ActionEvent) -> {
            tbxDateFrom.setEnabled(chkHistoryFiltering.isSelected());
            tbxDateTo.setEnabled(chkHistoryFiltering.isSelected());
            this.refresh();
        });
        btnRefresh.addActionListener((ActionEvent) -> {
            this.refresh();
        });
        btnViewProductChart.addActionListener((ActionEvent) -> {
            this.viewProductChart();
        });
        btnViewCategoryChart.addActionListener((ActionEvent) -> {
            this.viewCategoryChart();
        });

        tableProductUnitSold.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            this.updateButtonsEnabled();
        });

        tglMonths.addActionListener((ActionEvent) -> {
            this.statRefresh();
        });
        tglWeeks.addActionListener((ActionEvent) -> {
            this.statRefresh();
        });
        tglDays.addActionListener((ActionEvent) -> {
            this.statRefresh();
        });
        btnStatRefresh.addActionListener((ActionEvent) -> {
            this.statRefresh();
        });

        rdoAll.addActionListener((ActionEvent) -> {
            this.radioButtonSelected();
        });
        rdoCategory.addActionListener((ActionEvent) -> {
            this.radioButtonSelected();
        });
        rdoProduct.addActionListener((ActionEvent) -> {
            this.radioButtonSelected();
        });
        chkSpecifyRange.addActionListener((ActionEvent) -> {
            tbxDateFrom1.setEnabled(chkSpecifyRange.isSelected());
            tbxDateTo1.setEnabled(chkSpecifyRange.isSelected());
            this.statRefresh();
        });
        
        KeyAdapter enterKeyRefresh = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    statRefresh();
                }
            }
        };
        tbxDateFrom1.addKeyListener(enterKeyRefresh);
        tbxDateTo1.addKeyListener(enterKeyRefresh);
        
        cbxCategory.addActionListener((ActionEvent) -> {
            this.statRefresh();
        });
        cbxProduct.addActionListener((ActionEvent) -> {
            this.statRefresh();
        });

        btnViewAvgChart.addActionListener((ActionEvent) -> {
            this.viewAveragesChart();
        });
        btnViewTotalChart.addActionListener((ActionEvent) -> {
            this.viewTotalChart();
        });
    }

    private void radioButtonSelected() {
        cbxProduct.setEnabled(rdoProduct.isSelected());
        cbxCategory.setEnabled(rdoCategory.isSelected());

        this.statRefresh();
    }

    private void populateComboBoxData() {
        List<String> productIDs = database.queryListOfAllProductIDs();
        String[] productIDsArray = new String[productIDs.size()];
        productIDs.toArray(productIDsArray);
        cbxProduct.setModel(new DefaultComboBoxModel<>(productIDsArray));

        List<String> categoryIDs = database.queryListOfCategoryIDs();
        String[] categoryIDsArray = new String[categoryIDs.size()];
        categoryIDs.toArray(categoryIDsArray);
        cbxCategory.setModel(new DefaultComboBoxModel<>(categoryIDsArray));
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

        timePeriodGroup = new javax.swing.ButtonGroup();
        productOrCategoryGroup = new javax.swing.ButtonGroup();
        panel_header = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        tabbedPane = new javax.swing.JTabbedPane();
        panel_unitSold = new javax.swing.JPanel();
        scrollPane_1 = new javax.swing.JScrollPane();
        tableCategoryUnitSold = new javax.swing.JTable();
        scrollPane_2 = new javax.swing.JScrollPane();
        tableProductUnitSold = new javax.swing.JTable();
        panel_bottom = new javax.swing.JPanel();
        lblStats = new javax.swing.JLabel();
        btnViewProductChart = new javax.swing.JButton();
        btnViewCategoryChart = new javax.swing.JButton();
        panel_filter = new javax.swing.JPanel();
        tbxDateFrom = new javax.swing.JFormattedTextField();
        javax.swing.JLabel l_filterFrom = new javax.swing.JLabel();
        javax.swing.JLabel l_filterTo = new javax.swing.JLabel();
        tbxDateTo = new javax.swing.JFormattedTextField();
        chkHistoryFiltering = new javax.swing.JCheckBox();
        btnRefresh = new javax.swing.JButton();
        panel_timeStats = new javax.swing.JPanel();
        scrollPane_3 = new javax.swing.JScrollPane();
        tableStats = new javax.swing.JTable();
        panel_options = new javax.swing.JPanel();
        panel_stat_period = new javax.swing.JPanel();
        tglMonths = new javax.swing.JToggleButton();
        tglWeeks = new javax.swing.JToggleButton();
        tglDays = new javax.swing.JToggleButton();
        panel_stat_date = new javax.swing.JPanel();
        chkSpecifyRange = new javax.swing.JCheckBox();
        javax.swing.JLabel l_filterFrom1 = new javax.swing.JLabel();
        tbxDateFrom1 = new javax.swing.JFormattedTextField();
        javax.swing.JLabel l_filterTo1 = new javax.swing.JLabel();
        tbxDateTo1 = new javax.swing.JFormattedTextField();
        panel_stat_product = new javax.swing.JPanel();
        rdoAll = new javax.swing.JRadioButton();
        rdoProduct = new javax.swing.JRadioButton();
        cbxProduct = new javax.swing.JComboBox<>();
        rdoCategory = new javax.swing.JRadioButton();
        cbxCategory = new javax.swing.JComboBox<>();
        btnStatRefresh = new javax.swing.JButton();
        lblSummary = new javax.swing.JLabel();
        panel_bottom1 = new javax.swing.JPanel();
        btnViewAvgChart = new javax.swing.JButton();
        btnViewTotalChart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(940, 600));
        setSize(new java.awt.Dimension(940, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panel_header.setBackground(new java.awt.Color(255, 255, 255));
        panel_header.setLayout(new java.awt.GridBagLayout());

        headerLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        headerLabel.setText("Sale Statistics");
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

        panel_unitSold.setLayout(new java.awt.GridBagLayout());

        tableCategoryUnitSold.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableCategoryUnitSold.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Category ID", "Category Name", "Unit Sold"
            }
        ));
        tableCategoryUnitSold.setGridColor(new java.awt.Color(204, 204, 204));
        tableCategoryUnitSold.setRowHeight(20);
        tableCategoryUnitSold.getTableHeader().setReorderingAllowed(false);
        scrollPane_1.setViewportView(tableCategoryUnitSold);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 4);
        panel_unitSold.add(scrollPane_1, gridBagConstraints);

        tableProductUnitSold.setAutoCreateRowSorter(true);
        tableProductUnitSold.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableProductUnitSold.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Product ID", "Product Name", "Unit Sold"
            }
        ));
        tableProductUnitSold.setGridColor(new java.awt.Color(204, 204, 204));
        tableProductUnitSold.setRowHeight(20);
        tableProductUnitSold.getTableHeader().setReorderingAllowed(false);
        scrollPane_2.setViewportView(tableProductUnitSold);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 8);
        panel_unitSold.add(scrollPane_2, gridBagConstraints);

        panel_bottom.setLayout(new java.awt.GridBagLayout());

        lblStats.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblStats.setText("A total of 0 items has been sold between 0000-00-00 and 0000-00-00.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 12, 8, 8);
        panel_bottom.add(lblStats, gridBagConstraints);

        btnViewProductChart.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnViewProductChart.setText("View Selected Product Chart...");
        btnViewProductChart.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewProductChart.setMaximumSize(new java.awt.Dimension(200, 26));
        btnViewProductChart.setMinimumSize(new java.awt.Dimension(200, 26));
        btnViewProductChart.setName(""); // NOI18N
        btnViewProductChart.setPreferredSize(new java.awt.Dimension(200, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 8);
        panel_bottom.add(btnViewProductChart, gridBagConstraints);

        btnViewCategoryChart.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnViewCategoryChart.setText("View Category Chart...");
        btnViewCategoryChart.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewCategoryChart.setMaximumSize(new java.awt.Dimension(164, 26));
        btnViewCategoryChart.setMinimumSize(new java.awt.Dimension(164, 26));
        btnViewCategoryChart.setName(""); // NOI18N
        btnViewCategoryChart.setPreferredSize(new java.awt.Dimension(164, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        panel_bottom.add(btnViewCategoryChart, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        panel_unitSold.add(panel_bottom, gridBagConstraints);

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

        chkHistoryFiltering.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkHistoryFiltering.setText("Specify Date Range:");
        chkHistoryFiltering.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        panel_filter.add(chkHistoryFiltering, gridBagConstraints);

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRefresh.setMaximumSize(new java.awt.Dimension(128, 36));
        btnRefresh.setMinimumSize(new java.awt.Dimension(128, 36));
        btnRefresh.setName(""); // NOI18N
        btnRefresh.setPreferredSize(new java.awt.Dimension(96, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 4, 8);
        panel_filter.add(btnRefresh, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 0, 0);
        panel_unitSold.add(panel_filter, gridBagConstraints);

        tabbedPane.addTab("Numbers of Units Sold", panel_unitSold);

        panel_timeStats.setLayout(new java.awt.GridBagLayout());

        tableStats.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tableStats.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Year-Month", "Total Unit Sold", "Avg Unit Sold / Day", "Total Income", "Avg Income / Day"
            }
        ));
        tableStats.setGridColor(new java.awt.Color(204, 204, 204));
        tableStats.setRowHeight(20);
        tableStats.getTableHeader().setReorderingAllowed(false);
        scrollPane_3.setViewportView(tableStats);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 8);
        panel_timeStats.add(scrollPane_3, gridBagConstraints);

        panel_options.setLayout(new java.awt.GridBagLayout());

        panel_stat_period.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Time Period Grouping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_stat_period.setLayout(new java.awt.GridBagLayout());

        timePeriodGroup.add(tglMonths);
        tglMonths.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tglMonths.setText("Months");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        panel_stat_period.add(tglMonths, gridBagConstraints);

        timePeriodGroup.add(tglWeeks);
        tglWeeks.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tglWeeks.setText("Weeks");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        panel_stat_period.add(tglWeeks, gridBagConstraints);

        timePeriodGroup.add(tglDays);
        tglDays.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tglDays.setText("Days");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 8);
        panel_stat_period.add(tglDays, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 0);
        panel_options.add(panel_stat_period, gridBagConstraints);

        panel_stat_date.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Date Range", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_stat_date.setLayout(new java.awt.GridBagLayout());

        chkSpecifyRange.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        chkSpecifyRange.setText("Specify:");
        chkSpecifyRange.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        panel_stat_date.add(chkSpecifyRange, gridBagConstraints);

        l_filterFrom1.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterFrom1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterFrom1.setText("From:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 4);
        panel_stat_date.add(l_filterFrom1, gridBagConstraints);

        tbxDateFrom1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        tbxDateFrom1.setText("2016-01-01");
        tbxDateFrom1.setEnabled(false);
        tbxDateFrom1.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxDateFrom1.setPreferredSize(new java.awt.Dimension(80, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 8);
        panel_stat_date.add(tbxDateFrom1, gridBagConstraints);

        l_filterTo1.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_filterTo1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_filterTo1.setText("To:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 16, 12, 4);
        panel_stat_date.add(l_filterTo1, gridBagConstraints);

        tbxDateTo1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        tbxDateTo1.setText("2016-02-01");
        tbxDateTo1.setEnabled(false);
        tbxDateTo1.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxDateTo1.setPreferredSize(new java.awt.Dimension(80, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 12, 8);
        panel_stat_date.add(tbxDateTo1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        panel_options.add(panel_stat_date, gridBagConstraints);

        panel_stat_product.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Product / Category", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 11))); // NOI18N
        panel_stat_product.setLayout(new java.awt.GridBagLayout());

        productOrCategoryGroup.add(rdoAll);
        rdoAll.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        rdoAll.setSelected(true);
        rdoAll.setText("All Products / Categories");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 4);
        panel_stat_product.add(rdoAll, gridBagConstraints);

        productOrCategoryGroup.add(rdoProduct);
        rdoProduct.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        rdoProduct.setText("Selected Product:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_stat_product.add(rdoProduct, gridBagConstraints);

        cbxProduct.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxProduct.setEnabled(false);
        cbxProduct.setMaximumSize(new java.awt.Dimension(140, 22));
        cbxProduct.setMinimumSize(new java.awt.Dimension(140, 22));
        cbxProduct.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 8, 4);
        panel_stat_product.add(cbxProduct, gridBagConstraints);

        productOrCategoryGroup.add(rdoCategory);
        rdoCategory.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        rdoCategory.setText("Selected Category:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        panel_stat_product.add(rdoCategory, gridBagConstraints);

        cbxCategory.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        cbxCategory.setEnabled(false);
        cbxCategory.setMaximumSize(new java.awt.Dimension(140, 22));
        cbxCategory.setMinimumSize(new java.awt.Dimension(140, 22));
        cbxCategory.setPreferredSize(new java.awt.Dimension(140, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 8, 4);
        panel_stat_product.add(cbxCategory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        panel_options.add(panel_stat_product, gridBagConstraints);

        btnStatRefresh.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnStatRefresh.setText("Refresh");
        btnStatRefresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnStatRefresh.setMaximumSize(new java.awt.Dimension(164, 32));
        btnStatRefresh.setMinimumSize(new java.awt.Dimension(164, 32));
        btnStatRefresh.setName(""); // NOI18N
        btnStatRefresh.setPreferredSize(new java.awt.Dimension(164, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 0);
        panel_options.add(btnStatRefresh, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panel_timeStats.add(panel_options, gridBagConstraints);

        lblSummary.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblSummary.setText("Summary: Total Unit Sold = 0 (Average 0.00 / Month), Total Income = 0.00 # (Average 0.00 # / Month).");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 8);
        panel_timeStats.add(lblSummary, gridBagConstraints);

        panel_bottom1.setLayout(new java.awt.GridBagLayout());

        btnViewAvgChart.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnViewAvgChart.setText("View Averages Chart...");
        btnViewAvgChart.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewAvgChart.setMaximumSize(new java.awt.Dimension(164, 26));
        btnViewAvgChart.setMinimumSize(new java.awt.Dimension(164, 26));
        btnViewAvgChart.setName(""); // NOI18N
        btnViewAvgChart.setPreferredSize(new java.awt.Dimension(164, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 8);
        panel_bottom1.add(btnViewAvgChart, gridBagConstraints);

        btnViewTotalChart.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnViewTotalChart.setText("View Totals Chart...");
        btnViewTotalChart.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnViewTotalChart.setMaximumSize(new java.awt.Dimension(164, 26));
        btnViewTotalChart.setMinimumSize(new java.awt.Dimension(164, 26));
        btnViewTotalChart.setName(""); // NOI18N
        btnViewTotalChart.setPreferredSize(new java.awt.Dimension(164, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 4);
        panel_bottom1.add(btnViewTotalChart, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        panel_timeStats.add(panel_bottom1, gridBagConstraints);

        tabbedPane.addTab("Statistic for Months / Weeks / Days", panel_timeStats);

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
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnStatRefresh;
    private javax.swing.JButton btnViewAvgChart;
    private javax.swing.JButton btnViewCategoryChart;
    private javax.swing.JButton btnViewProductChart;
    private javax.swing.JButton btnViewTotalChart;
    private javax.swing.JComboBox<String> cbxCategory;
    private javax.swing.JComboBox<String> cbxProduct;
    private javax.swing.JCheckBox chkHistoryFiltering;
    private javax.swing.JCheckBox chkSpecifyRange;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel lblStats;
    private javax.swing.JLabel lblSummary;
    private javax.swing.JPanel panel_bottom;
    private javax.swing.JPanel panel_bottom1;
    private javax.swing.JPanel panel_filter;
    private javax.swing.JPanel panel_header;
    private javax.swing.JPanel panel_options;
    private javax.swing.JPanel panel_stat_date;
    private javax.swing.JPanel panel_stat_period;
    private javax.swing.JPanel panel_stat_product;
    private javax.swing.JPanel panel_timeStats;
    private javax.swing.JPanel panel_unitSold;
    private javax.swing.ButtonGroup productOrCategoryGroup;
    private javax.swing.JRadioButton rdoAll;
    private javax.swing.JRadioButton rdoCategory;
    private javax.swing.JRadioButton rdoProduct;
    private javax.swing.JScrollPane scrollPane_1;
    private javax.swing.JScrollPane scrollPane_2;
    private javax.swing.JScrollPane scrollPane_3;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tableCategoryUnitSold;
    private javax.swing.JTable tableProductUnitSold;
    private javax.swing.JTable tableStats;
    private javax.swing.JFormattedTextField tbxDateFrom;
    private javax.swing.JFormattedTextField tbxDateFrom1;
    private javax.swing.JFormattedTextField tbxDateTo;
    private javax.swing.JFormattedTextField tbxDateTo1;
    private javax.swing.JToggleButton tglDays;
    private javax.swing.JToggleButton tglMonths;
    private javax.swing.JToggleButton tglWeeks;
    private javax.swing.ButtonGroup timePeriodGroup;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

}
