package database.test.gui.charts_new;

import database.test.gui.Const;
import java.sql.SQLException;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

public class NewBarChart extends AbstractChart {
    
    private DefaultCategoryDataset dataset;

    private String categoryLabel;
    private String valueLabel;

    public NewBarChart(String title, String sql, 
            String categoryLabel, String valueLabel) {
        super.setTitle(title);
        super.setSQL(sql);
        this.dataset = new DefaultCategoryDataset();
        this.categoryLabel = categoryLabel;
        this.valueLabel = valueLabel;
    }

    public void addData(Number value, Comparable rowKey, Comparable colKey) {
        dataset.addValue(value, rowKey, colKey);
    }

    @Override
    public void build() {
        try {
            List<Object[]> data = super.queryData(2);
            for (Object[] row : data) {
                String key = (String) row[0];
                Number val = (Number) row[1];
                this.addData(val, "Customer", key);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
            this.addData(0, "ERROR", "ERROR");
        }
        
        JFreeChart chart = ChartFactory.createBarChart3D(super.getTitle(), categoryLabel, valueLabel,
                dataset, PlotOrientation.VERTICAL, false, true, false);

        // chart appearance
        chart.getTitle().setFont(Const.FONT_DEFAULT_24);
        chart.getTitle().setPadding(0, 0, 8, 0);
        chart.setPadding(new RectangleInsets(8, 0, 8, 0));

        // plot appearance
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setForegroundAlpha(0.60f);
        plot.getDomainAxis().setLabelFont(Const.FONT_DEFAULT_16);
        plot.getDomainAxis().setTickLabelFont(Const.FONT_DEFAULT_12);
        plot.getRangeAxis().setLabelFont(Const.FONT_DEFAULT_16);
        plot.getRangeAxis().setTickLabelFont(Const.FONT_DEFAULT_12);
        plot.setBackgroundPaint(Const.COLOR_BG_LIGHTGRAY);
        plot.setOutlineVisible(false);

        // legend appearance
//        LegendTitle legend = chart.getLegend();
//        legend.setItemFont(Const.FONT_DEFAULT_12);
//        legend.setBorder(0, 0, 0, 0);
//        legend.setItemLabelPadding(new RectangleInsets(2, 4, 2, 4));
//        legend.setLegendItemGraphicPadding(new RectangleInsets(2, 4, 2, 0));
        
        super.setChart(chart);
    }
    
}
