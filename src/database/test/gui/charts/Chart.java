package database.test.gui.charts;

import database.test.ApplicationMain;
import database.test.DatabaseManager;
import database.test.SQLStrings;
import database.test.gui.Util;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public abstract class Chart {
    
    private static DatabaseManager database = ApplicationMain.getDatabaseInstance();

    public static final int PIE_CHART = 0;
    public static final int BAR_CHART = 1;
    public static final int LINE_CHART = 2;
    
    private JFreeChart chart;
    
    private String title;
    private String sql;
    
    public Chart(String title, String sql) {
        this.title = title;
        this.sql = sql;
    }

    
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
    
    

    public void show() {
        try {
            List<Object[]> data = database.query(sql, 2);
            
//            GenericPieChart.buildAndShowGraph(title, data);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void show(Chart c) {
        JFreeChart chart = c.chart;
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMaximumDrawWidth(1920);
        chartPanel.setMaximumDrawHeight(1200);

        Util.createAndShowDialog(null, "Chart: " + c.title,
                chartPanel, new Dimension(1000, 600), true);
    }

}
