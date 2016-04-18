package database.test.gui;

import database.test.data.ShoppingList;
import database.test.data.ShoppingList.LineItem;
import java.time.format.DateTimeFormatter;

public class ReceiptWindow
        extends javax.swing.JFrame {

    public ReceiptWindow() {
        this.initComponents();

        txtReceipt.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        txtReceipt.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);

        btnDone.addActionListener((ActionEvent) -> {
            this.setVisible(false);
        });
    }

    public void setReceipt(String receipt) {
        txtReceipt.setText(receipt);
        txtReceipt.setSelectionStart(0);
        txtReceipt.setSelectionEnd(0);
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Automatically Generated by NetBeans">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        txtReceipt_scrollPane = new javax.swing.JScrollPane();
        txtReceipt = new javax.swing.JTextArea();
        btnDone = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(400, 460));
        setMinimumSize(new java.awt.Dimension(400, 460));
        setPreferredSize(new java.awt.Dimension(400, 460));
        setResizable(false);
        setSize(new java.awt.Dimension(400, 460));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        txtReceipt_scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        txtReceipt.setEditable(false);
        txtReceipt.setColumns(20);
        txtReceipt.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        txtReceipt.setRows(5);
        txtReceipt.setText("receipt_receipt_receipt_receipt_receipt_receipt_//");
        txtReceipt.setMargin(new java.awt.Insets(4, 4, 4, 4));
        txtReceipt.setPreferredSize(null);
        txtReceipt_scrollPane.setViewportView(txtReceipt);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(txtReceipt_scrollPane, gridBagConstraints);

        btnDone.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnDone.setText("Done");
        btnDone.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDone.setPreferredSize(new java.awt.Dimension(73, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 1, 1);
        getContentPane().add(btnDone, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDone;
    private javax.swing.JTextArea txtReceipt;
    private javax.swing.JScrollPane txtReceipt_scrollPane;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    public static String generateReceipt(ShoppingList shoppingList) {
        StringBuilder sb = new StringBuilder();
        final int width = 50; // characters

        String date = shoppingList.getCheckoutDate().toString();
        String time = shoppingList.getCheckoutTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        final int nameLen = Const.STORE_NAME.length();
        final int namePos = (width + nameLen) / 2;
        final int nameTrail = width - namePos;
        
        sb.append(String.format("\n%" + namePos + "s%" + nameTrail + "s", Const.STORE_NAME, "")).append("\n");
        sb.append("                   Sale Receipt                   \n\n");
        sb.append(String.format("%33s%17s", date + " " + time, "")).append("\n\n");
        sb.append("--------------------------------------------------\n\n");
        
        for(int i = 0; i < shoppingList.size(); i++) {
            LineItem item = shoppingList.getItemAt(i);
            String qty = item.getQuantityString();
            String name = item.getProductName();
            name = name.substring(0, Math.min(name.length(), 30));
            String subtt = String.format("%,.2f", item.subtotal());
            sb.append(String.format("%3s%-37s%10s\n", qty, name, subtt));
        }
        
        sb.append("\n");
        sb.append("--------------------------------------------------\n\n");
        sb.append("   Total    : ").append(String.format("%,.2f", shoppingList.getTotalPrice())).append("\n");
        sb.append("   Discount : ").append(String.format("%,.2f", shoppingList.getDiscountAmount())).append("\n");
        
        
        return sb.toString();
    }

}
