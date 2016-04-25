package database.test.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Utility methods used by GUI classes.
 */
public class Util {

    public static DefaultTableCellRenderer TABLE_CELL_MONEY
            = new DefaultTableCellRenderer() {
        @Override
        public int getHorizontalAlignment() {
            return JLabel.RIGHT;
        }

        @Override
        protected void setValue(Object value) {
            setText(String.format("%,.2f " + Const.CURRENCY + " ", (double) value));
        }
    };

    public static DefaultTableCellRenderer TABLE_CELL_PERCENT
            = new DefaultTableCellRenderer() {
        @Override
        public int getHorizontalAlignment() {
            return JLabel.RIGHT;
        }

        @Override
        protected void setValue(Object value) {
            setText(String.format("%,.2f %% ", (double) value));
        }
    };

    public static DefaultTableCellRenderer TABLE_CELL_TIME
            = new DefaultTableCellRenderer() {
        @Override
        protected void setValue(Object value) {
            setText(((LocalTime) value).format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    };

    public static DefaultTableCellRenderer TABLE_CELL_STOCK_QUANTITY
            = new DefaultTableCellRenderer() {
        @Override
        public int getHorizontalAlignment() {
            return JLabel.RIGHT;
        }

        @Override
        protected void setValue(Object value) {
            if (((int) value) == 0) {
                setText("out of stock");
            } else {
                setText(String.format("%,d ", (int) value));
            }
        }
    };

    public static DefaultTableCellRenderer TABLE_CELL_INTEGER
            = new DefaultTableCellRenderer() {
        @Override
        public int getHorizontalAlignment() {
            return JLabel.RIGHT;
        }

        @Override
        protected void setValue(Object value) {
            setText(String.format("%,d ", (int) value));
        }
    };

    public static DefaultTableCellRenderer TABLE_CELL_INTEGER_ZERO_NONE
            = new DefaultTableCellRenderer() {
        @Override
        public int getHorizontalAlignment() {
            return JLabel.RIGHT;
        }

        @Override
        protected void setValue(Object value) {
            if ((int) value == 0) {
                setText("none");
            } else {
                setText(String.format("%,d ", (int) value));
            }
        }
    };

    public static DefaultTableCellRenderer TABLE_CELL_DOUBLE
            = new DefaultTableCellRenderer() {
        @Override
        public int getHorizontalAlignment() {
            return JLabel.RIGHT;
        }

        @Override
        protected void setValue(Object value) {
            setText(String.format("%,.2f ", (double) value));
        }
    };

    public static DefaultTableCellRenderer TABLE_CELL_BOLDTEXT
            = new DefaultTableCellRenderer() {
        @Override
        public Font getFont() {
            return Const.FONT_DEFAULT_12_BOLD;
        }
    };

    /**
     * Create and display a JDialog with specified properties. (Note: the dialog
     * is displayed at the center of the screen and is fixed-size).
     *
     * @param owner The parent frame.
     * @param title Title of the dialog window.
     * @param content Component inside the dialog.
     * @param size Size of the dialog window.
     * @return The created (displayed, and disposed) JDialog.
     */
    public static JDialog createAndShowDialog(Frame owner, String title, Component content, Dimension size) {
        return createAndShowDialog(owner, title, content, size, false);
    }

    public static JDialog createAndShowDialog(Frame owner, String title, Component content, Dimension size, boolean resizable) {
        JDialog dia = new JDialog(owner, title, true);
        dia.getContentPane().add(content);
        dia.setSize(size);
        dia.setMaximumSize(size);
        dia.setMinimumSize(size);
        dia.setPreferredSize(size);
        dia.setResizable(resizable);
        dia.setLocationRelativeTo(null);
        dia.pack();
        dia.setVisible(true);
        dia.dispose();
        return dia;
    }

    public static JFrame createAndShowWindow(String title, Component content, Dimension size) {
        JFrame frame = new JFrame(title);
        frame.getContentPane().add(content);
        frame.setSize(size);
        frame.setPreferredSize(size);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    public static String formatPhoneNumber(String phone) {
        if (phone == null) {
            return "";
        }
        int len = phone.length();
        if (len == 10 || len == 9) {
            return phone.substring(0, len - 7) + "-" + phone.substring(len - 7, len - 4) + "-" + phone.substring(len - 4);
        } else {
            return phone;
        }
    }

}
