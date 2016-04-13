package database.test.gui;

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

    public void showReceipt(String receipt) {
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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
}