package database.test.gui.charts;

import database.test.gui.Const;
import database.test.gui.Util;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;

public class GenericPieChart extends Chart {

    private JFreeChart chart;
    private DefaultPieDataset dataset;

    public GenericPieChart(String title, String sql) {
        super(title, sql);
        this.dataset = new DefaultPieDataset();
    }

    public void addData(String key, Number value) {
        dataset.setValue(key, value);
    }

    public void createChart() {
        chart = ChartFactory.createPieChart3D(super.getTitle(), dataset, true, true, false);

        // chart appearance
        chart.getTitle().setFont(Const.FONT_DEFAULT_24);
        chart.getTitle().setPadding(0, 0, 8, 0);
        chart.setPadding(new RectangleInsets(8, 0, 8, 0));

        // plot appearance
        PiePlot3D plot = (PiePlot3D) chart.getPlot();

        plot.setForegroundAlpha(0.60f);
        plot.setInteriorGap(0.02);

        plot.setBackgroundPaint(Const.COLOR_BG_LIGHTGRAY);
        plot.setOutlineVisible(false);
        
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}\n{1} ({2})", 
                new DecimalFormat("0"), new DecimalFormat("0.00%")));
        plot.setInteriorGap(0.05);
        plot.setLabelFont(Const.FONT_DEFAULT_12);
        plot.setLabelLinkStyle(PieLabelLinkStyle.STANDARD);
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setLabelPadding(new RectangleInsets(2, 4, 2, 4));

        // legend appearance
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(Const.FONT_DEFAULT_12);
        legend.setBorder(0, 0, 0, 0);
        legend.setItemLabelPadding(new RectangleInsets(2, 4, 2, 4));
        legend.setLegendItemGraphicPadding(new RectangleInsets(2, 4, 2, 0));
    }

    @Override
    public void show() {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMaximumDrawWidth(1920);
        chartPanel.setMaximumDrawHeight(1200);

        Util.createAndShowDialog(null, "Pie Chart: " + super.getTitle(),
                chartPanel, new Dimension(1000, 600), true);
    }

//    public static void buildAndShowGraph(String title, List<Object[]> data) {
//        GenericPieChart pie = new GenericPieChart(title);
//        for (Object[] row : data) {
//            String key = (String) row[0];
//            Number val = (Number) row[1];
//            pie.addData(key, val);
//            System.out.println(key + " " + val);
//        }
//        pie.createChart();
//        pie.show();
//    }
//
//    public static void main(String[] args) {
//        //<editor-fold defaultstate="collapsed" desc="Setting GUI Look and Feel">
//        try {
//            javax.swing.UIManager.setLookAndFeel(
//                    javax.swing.UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException | InstantiationException |
//                IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            System.err.println("Error setting look and feel:\n\t" + ex);
//        }
//        //</editor-fold>
//
//        GenericPieChart pie = new GenericPieChart("Example");
//        for (int i = 0; i < 10; i++) {
//            pie.addData(String.format("Data #%02d", i), Math.random());
//        }
//        pie.createChart();
//        pie.show();
//    }
}
