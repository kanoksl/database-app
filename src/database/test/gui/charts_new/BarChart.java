package database.test.gui.charts_new;

import database.test.gui.Const;
import database.test.gui.Util;
import java.awt.Dimension;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

public class BarChart {

    private DefaultCategoryDataset dataset;

    private String title;
    private String categoryLabel;
    private String valueLabel;

    public BarChart(String title,
            String categoryLabel, String valueLabel) {
        this.title = title;
        this.categoryLabel = categoryLabel;
        this.valueLabel = valueLabel;
        this.dataset = new DefaultCategoryDataset();
    }

    public void addData(Number value, Comparable rowKey, Comparable colKey) {
        dataset.addValue(value, rowKey, colKey);
    }

    public void createAndShow() {
        JFreeChart chart = ChartFactory.createBarChart3D(title, categoryLabel, valueLabel,
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
        ((BarRenderer) plot.getRenderer()).setSeriesPaint(0, java.awt.Color.gray);

        // legend appearance
//        LegendTitle legend = chart.getLegend();
//        legend.setItemFont(Const.FONT_DEFAULT_12);
//        legend.setBorder(0, 0, 0, 0);
//        legend.setItemLabelPadding(new RectangleInsets(2, 4, 2, 4));
//        legend.setLegendItemGraphicPadding(new RectangleInsets(2, 4, 2, 0));
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMaximumDrawWidth(1920);
        chartPanel.setMaximumDrawHeight(1200);

        Util.createAndShowWindow("Statistic Report", chartPanel, new Dimension(1000, 600));
    }
}
