package database.test.gui.charts_new;

import database.test.gui.Const;
import database.test.gui.Util;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;

public class PieChart {

    private DefaultPieDataset dataset;
    String title;

    public PieChart(String title) {
        this.title = title;
        this.dataset = new DefaultPieDataset();
    }

    public void addData(String key, Number value) {
        dataset.setValue(key, value);
    }

    public void createAndShow() {
        JFreeChart chart = ChartFactory.createPieChart3D(title,
                dataset, true, true, false);

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

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMaximumDrawWidth(1920);
        chartPanel.setMaximumDrawHeight(1200);

        Util.createAndShowWindow("Statistic Report", chartPanel, new Dimension(1000, 600));
    }
}
