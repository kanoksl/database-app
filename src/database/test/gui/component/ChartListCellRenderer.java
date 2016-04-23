package database.test.gui.component;

import database.test.gui.Const;
import database.test.gui.charts.Chart;
import database.test.gui.charts_new.AbstractChart;
import javax.swing.*;
import java.awt.*;

public class ChartListCellRenderer extends JPanel implements ListCellRenderer {

    private final JLabel imageLabel;

    private final JPanel textPanel;

    private final JLabel titleLabel;
    private final JLabel infoLabel;

    // width/height of the album artwork thumbnails
    private static final int ARTWORK_SIZE = 48;

    public ChartListCellRenderer() {

        imageLabel = new JLabel();
        textPanel = new JPanel();
        titleLabel = new JLabel();
        infoLabel = new JLabel();

        //setMinimumSize(new java.awt.Dimension(118, 52));
        //setPreferredSize(new java.awt.Dimension(400, 52));
        this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setMinimumSize(new Dimension(ARTWORK_SIZE, ARTWORK_SIZE));
        imageLabel.setPreferredSize(new Dimension(ARTWORK_SIZE, ARTWORK_SIZE));

        textPanel.setLayout(new java.awt.GridLayout(2, 0, 0, 4));
        textPanel.setOpaque(true);

        titleLabel.setFont(Const.FONT_DEFAULT_12_BOLD);
        titleLabel.setText("Song Title");
        titleLabel.setOpaque(true);
        textPanel.add(titleLabel);

        infoLabel.setFont(Const.FONT_DEFAULT_12);
        infoLabel.setText("Artist");
        infoLabel.setOpaque(true);
        textPanel.add(infoLabel);

        this.add(imageLabel);
        this.add(textPanel);

        this.setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected,
            boolean cellHasFocus) {
        
        AbstractChart chart = (AbstractChart) value;
        titleLabel.setText(chart.getTitle());
        infoLabel.setText(chart.getSQL());

        if (isSelected) {
            // if this cell is selected, set bg and fg
            this.adjustColors(Const.COLOR_HIGHLIGHT_BG, Const.COLOR_HIGHLIGHT_FG,
                    this, textPanel, titleLabel, infoLabel);
            imageLabel.setIcon(chart.getIconSelected());
        } else {
            // if not selected, set to default colors
            this.adjustColors(list.getBackground(), list.getForeground(),
                    this, textPanel, titleLabel, infoLabel);
            imageLabel.setIcon(chart.getIcon());
            // set the info text color
            infoLabel.setForeground(Color.GRAY);
        }

        return this;
    }

    private void adjustColors(Color bg, Color fg, Component... components) {
        for (Component c : components) {
            c.setForeground(fg);
            c.setBackground(bg);
        }
    }

}
