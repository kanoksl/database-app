package database.test.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;

/**
 * Utility methods used by GUI classes.
 */
public class Util {
    
    /**
     * Create and display a JDialog with specified properties. (Note: the dialog
     * is displayed at the center of the screen and is fixed-size).
     * @param owner The parent frame.
     * @param title Title of the dialog window.
     * @param content Component inside the dialog.
     * @param size Size of the dialog window.
     * @return The created (displayed, and disposed) JDialog.
     */
    public static JDialog createAndShowDialog(Frame owner, String title, Component content, Dimension size) {
        JDialog dia = new JDialog(owner, title, true);
        dia.getContentPane().add(content);
        dia.setSize(size);
        dia.setMaximumSize(size);
        dia.setMinimumSize(size);
        dia.setPreferredSize(size);
        dia.setResizable(false);
        dia.setLocationRelativeTo(null);
        dia.pack();
        dia.setVisible(true);
        dia.dispose();
        return dia;
    }
    
}
