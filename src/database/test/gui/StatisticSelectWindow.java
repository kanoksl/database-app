package database.test.gui;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.SQLStrings;
import database.test.gui.charts_new.*;
import database.test.gui.component.ChartListCellRenderer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

public class StatisticSelectWindow
        extends javax.swing.JFrame {

    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();

    private final List<AbstractSQLChart> chartList = new ArrayList<>();

    public StatisticSelectWindow() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Statistics - " + Const.APP_TITLE);

        this.loadCharts();

        list.setCellRenderer(new ChartListCellRenderer());
        list.setModel(new AbstractListModel<AbstractSQLChart>() {
            @Override
            public int getSize() {
                return chartList.size();
            }

            @Override
            public AbstractSQLChart getElementAt(int index) {
                return chartList.get(index);
            }
        });
        list.updateUI();

        btnShowChart.addActionListener((ActionEvent) -> {
            AbstractSQLChart c = (AbstractSQLChart) list.getSelectedValue();
            if (c != null) {
                c.build();
                c.show();
            }
        });
    }

    public void loadCharts() {
        // TODO: add more charts
        chartList.add(new PieChartFromSQL("Numbers of Registered Customers by Genders",
                SQLStrings.SQL_STATS_CUSTOMER_COUNT_BY_GENDER));
        chartList.add(new BarChartFromSQL("Numbers of Registered Customers by Age Range",
                SQLStrings.SQL_STATS_CUSTOMER_COUNT_BY_AGE, "Age Ranges", "Number of Customers"));

        chartList.add(new PieChartFromSQL("Numbers of Currently Selling Products by Categories",
                SQLStrings.SQL_STATS_CATEGORY_PRODUCT_COUNTS));
        chartList.add(new PieChartFromSQL("Stock Item Counts by Product Categories",
                SQLStrings.SQL_STATS_CATEGORY_STOCK_AMOUNTS));

        chartList.add(new PieChartFromSQL("Numbers of Products From Each Suppliers",
                SQLStrings.SQL_STATS_SUPPLIER_PRODUCT_COUNTS));
    }

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

        list_scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        panel_buttons = new javax.swing.JPanel();
        btnShowChart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(480, 480));
        setPreferredSize(new java.awt.Dimension(480, 480));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        list_scrollPane.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        getContentPane().add(list_scrollPane, gridBagConstraints);

        panel_buttons.setLayout(new java.awt.GridBagLayout());

        btnShowChart.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnShowChart.setText("Show");
        btnShowChart.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnShowChart.setMaximumSize(new java.awt.Dimension(96, 32));
        btnShowChart.setMinimumSize(new java.awt.Dimension(96, 32));
        btnShowChart.setPreferredSize(new java.awt.Dimension(96, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        panel_buttons.add(btnShowChart, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 8, 8);
        getContentPane().add(panel_buttons, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnShowChart;
    private javax.swing.JList list;
    private javax.swing.JScrollPane list_scrollPane;
    private javax.swing.JPanel panel_buttons;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
