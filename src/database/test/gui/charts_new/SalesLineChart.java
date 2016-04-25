package database.test.gui.charts_new;

import database.test.gui.Const;
import database.test.gui.Util;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.time.LocalDate;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class SalesLineChart {

    private JFreeChart chart;
    private TimeSeries series1;
    private TimeSeries series2;

    private String title;
    private String timeLabel;
    private String valueLabel;
    
    private String axis2label;

    public SalesLineChart(String title, String series1Title, String series2Title,
            String timeLabel, String valueLabel, String axis2label) {
        this.series1 = new TimeSeries(series1Title);
        this.series2 = new TimeSeries(series2Title);
        this.title = title;
        this.timeLabel = timeLabel;
        this.valueLabel = valueLabel;
        this.axis2label = axis2label;
    }

    public void addData1(LocalDate date, Number value) {
        RegularTimePeriod period = new Day(date.getDayOfMonth(),
                date.getMonthValue(), date.getYear());
        series1.add(period, value);
    }

    public void addData2(LocalDate date, Number value) {
        RegularTimePeriod period = new Day(date.getDayOfMonth(),
                date.getMonthValue(), date.getYear());
        series2.add(period, value);
    }

    public void createAndShow() {
        TimeSeriesCollection dataset1 = new TimeSeriesCollection(series1);
        TimeSeriesCollection dataset2 = new TimeSeriesCollection(series2);

        chart = ChartFactory.createTimeSeriesChart(title, timeLabel, valueLabel,
                dataset1, true, true, false);

        // chart appearance
        chart.getTitle().setFont(Const.FONT_DEFAULT_24);
        chart.getTitle().setPadding(0, 0, 8, 0);
        chart.setPadding(new RectangleInsets(8, 0, 8, 0));

        // plot appearance
        XYPlot plot = chart.getXYPlot();
        plot.setForegroundAlpha(0.80f);
        plot.setBackgroundPaint(Const.COLOR_BG_LIGHTGRAY);
        plot.setOutlineVisible(false);
        plot.getDomainAxis().setLabelFont(Const.FONT_DEFAULT_16);
        plot.getDomainAxis().setTickLabelFont(Const.FONT_DEFAULT_12);
        plot.getRangeAxis().setLabelFont(Const.FONT_DEFAULT_16);
        plot.getRangeAxis().setTickLabelFont(Const.FONT_DEFAULT_12);

        NumberAxis axis2 = new NumberAxis(axis2label);
        axis2.setLabelFont(Const.FONT_DEFAULT_16);
        axis2.setTickLabelFont(Const.FONT_DEFAULT_12);
        axis2.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(1, axis2);
        plot.setDataset(1, dataset2);
        plot.mapDatasetToRangeAxis(1, 1);

        StandardXYItemRenderer renderer1 = new StandardXYItemRenderer();
        renderer1.setSeriesPaint(0, Color.BLUE);
        renderer1.setBaseStroke(new BasicStroke(4));
        renderer1.setBaseShapesVisible(true);
        plot.setRenderer(0, renderer1);

        StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setSeriesPaint(0, Color.RED);
        renderer2.setBaseStroke(new BasicStroke(6));
        renderer2.setBaseShapesVisible(true);
        plot.setRenderer(1, renderer2);

        // legend appearance
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(Const.FONT_DEFAULT_12);
        legend.setBorder(0, 0, 0, 0);
        legend.setItemLabelPadding(new RectangleInsets(2, 4, 2, 4));
        legend.setLegendItemGraphicPadding(new RectangleInsets(2, 4, 2, 0));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMaximumDrawWidth(1920);
        chartPanel.setMaximumDrawHeight(1200);

        Util.createAndShowWindow("Statistic Report", chartPanel, new Dimension(1000, 600));
    }

}
