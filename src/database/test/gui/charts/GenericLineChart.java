package database.test.gui.charts;

import database.test.gui.Const;
import database.test.gui.Util;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

public class GenericLineChart {

    private JFreeChart chart;
    private DefaultCategoryDataset  dataset;
    
    private String title;
    private String categoryLabel;
    private String valueLabel;

    public GenericLineChart(String title, String categoryLabel, String valueLabel) {
        this.dataset = new DefaultCategoryDataset ();
        this.title = title;
        this.categoryLabel = categoryLabel;
        this.valueLabel = valueLabel;
    }

    public void addData(double value, Comparable rowKey, Comparable colKey) {
        dataset.addValue(value, rowKey, colKey);
    }

    public void createChart() {
        chart = ChartFactory.createLineChart(title, categoryLabel, valueLabel, 
                dataset, PlotOrientation.VERTICAL, true, true, false);
        
        // chart appearance
        chart.getTitle().setFont(Const.FONT_DEFAULT_24);
        chart.getTitle().setPadding(0, 0, 8, 0);
        chart.setPadding(new RectangleInsets(8, 0,8, 0));

        // plot appearance
        Plot plot = chart.getPlot();

        plot.setForegroundAlpha(0.60f);

        plot.setBackgroundPaint(Const.COLOR_BG_LIGHTGRAY);
        plot.setOutlineVisible(false);

        // legend appearance
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(Const.FONT_DEFAULT_12);
        legend.setBorder(0, 0, 0, 0);
        legend.setItemLabelPadding(new RectangleInsets(2, 4, 2, 4));
        legend.setLegendItemGraphicPadding(new RectangleInsets(2, 4, 2, 0));
    }

    public void show() {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMaximumDrawWidth(1920);
        chartPanel.setMaximumDrawHeight(1200);

        Util.createAndShowDialog(null, "Pie Chart: " + title,
                chartPanel, new Dimension(1000, 600), true);
    }

    public static void main(String[] args) {
        //<editor-fold defaultstate="collapsed" desc="Setting GUI Look and Feel">
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Error setting look and feel:\n\t" + ex);
        }
        //</editor-fold>

        GenericLineChart lin = new GenericLineChart("Example", "i", "j");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 10; j++) {
                lin.addData(Math.random(), i, j);
            }
        }
        lin.createChart();
        lin.show();
    }
}
