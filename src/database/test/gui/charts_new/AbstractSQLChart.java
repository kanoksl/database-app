package database.test.gui.charts_new;

import database.test.ApplicationMain;
import database.test.gui.Util;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.List;
import javax.swing.ImageIcon;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public abstract class AbstractSQLChart {

    private JFreeChart chart;

    private String title;
    private String sql;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSQL() {
        return sql;
    }

    public void setSQL(String sql) {
        this.sql = sql;
    }

    public JFreeChart getChart() {
        return chart;
    }

    public void setChart(JFreeChart chart) {
        this.chart = chart;
    }

    public List<Object[]> queryData(int columnCount)
            throws SQLException {
        return ApplicationMain.getDatabaseInstance().query(sql, columnCount);
    }

    public abstract ImageIcon getIcon();

    public abstract ImageIcon getIconSelected();

    public abstract void build();

    public void show() {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMaximumDrawWidth(1920);
        chartPanel.setMaximumDrawHeight(1200);

//        Util.createAndShowDialog(null, "Statistic Report", chartPanel, new Dimension(1000, 600), true);
        Util.createAndShowWindow("Statistic Report", chartPanel, new Dimension(1000, 600));
    }

}
