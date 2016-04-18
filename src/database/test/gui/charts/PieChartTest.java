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
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PieChartTest extends ApplicationFrame {

    public PieChartTest(String title) {
        super(title);
        setContentPane(createDemoPanel());
    }

    private static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(" Male ", 30);
        dataset.setValue(" Female ", 50);
        return dataset;
    }

    private static JFreeChart createChart(PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Customers", // chart title 
                dataset, // data    
                false, // include legend   
                true,
                false);
        
//        chart.setBackgroundPaint(Color.WHITE);
//        chart.setBorderPaint(Color.WHITE);
        
        
        PiePlot3D plot = (PiePlot3D) chart.getPlot();
//        plot.setStartAngle(270);
        plot.setSectionPaint(" Male ", Color.ORANGE);
        plot.setSectionPaint(" Female ", Color.GREEN);
        plot.setForegroundAlpha(0.60f);
        plot.setInteriorGap(0.02);
        chart.getTitle().setFont(Const.FONT_DEFAULT_24);
//        chart.getLegend().setItemFont(Const.FONT_DEFAULT_12);
//        chart.setBackgroundPaint(Color.WHITE);
//        chart.setBorderVisible(false);
//        chart.setBackgroundImageAlpha(0f);
//        chart.fireChartChanged();
        plot.setBackgroundPaint(Color.decode("#F5F5F5"));
        plot.setOutlineVisible(false);

        
        plot.setLabelFont(Const.FONT_DEFAULT_12);
        plot.setLabelLinkStyle(PieLabelLinkStyle.STANDARD);
        plot.setLabelBackgroundPaint(Color.WHITE);
        
        
        return chart;
    }

    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
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
        
        PieChartTest demo = new PieChartTest("Mobile Sales");
        demo.setSize(1000, 600);
        
        Util.createAndShowDialog(null, "Hello", demo.rootPane, new Dimension(1000,600));
    }
}
