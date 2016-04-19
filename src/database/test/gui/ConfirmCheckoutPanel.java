package database.test.gui;

import database.test.data.ShoppingList;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.JFormattedTextField;
import javax.swing.event.ChangeEvent;

public class ConfirmCheckoutPanel
        extends javax.swing.JPanel {

    private CheckoutListener checkoutListener = null;
    private ShoppingList shoppingList = null;

    public ConfirmCheckoutPanel(CheckoutListener checkoutListener, ShoppingList shoppingList) {
        this.initComponents();
        this.setColorTheme();

        this.checkoutListener = checkoutListener;
        this.shoppingList = shoppingList;

        // show the customer name and the total price
        lblCustomer.setText(shoppingList.getCustomer().getDisplayName());
        lblTotalPrice.setText(String.format("%,.02f " + Const.CURRENCY,
                shoppingList.getTotalPrice()));

        this.calculateDiscount();
        this.calculateChange();

        //<editor-fold defaultstate="collapsed" desc="Initialize listeners">
        btnConfirm.addActionListener((ActionEvent) -> {
            this.confirm();
        });
        btnCancel.addActionListener((ActionEvent) -> {
            this.cancel();
        });
        spnDiscount.addChangeListener((ChangeEvent e) -> {
            this.calculateDiscount();
            this.calculateChange();
        });
        spnPaid.addChangeListener((ChangeEvent e) -> {
            this.calculateChange();
        });
        //</editor-fold>
    }

    private void calculateDiscount() {
        double discountPercent = (double) spnDiscount.getValue();
        shoppingList.setDiscountPercent(discountPercent);
        lblDiscountAmount.setText(String.format("%,.02f " + Const.CURRENCY,
                shoppingList.getDiscountAmount()));
        lblTotalAfter.setText(String.format("%,.02f " + Const.CURRENCY,
                shoppingList.getTotalAfterDiscount()));
    }

    private void calculateChange() {
        double paid = (double) spnPaid.getValue();
        double change = paid - shoppingList.getTotalAfterDiscount();
        lblChange.setText(String.format("%,.02f " + Const.CURRENCY, change));
        btnConfirm.setEnabled(change >= 0);
    }

    private void confirm() {
        // set the checkout date/time of the shopping list
        shoppingList.setCheckoutDate(LocalDate.now());
        shoppingList.setCheckoutTime(LocalTime.now());
        shoppingList.setAmountPaid((double) spnPaid.getValue());

        if (checkoutListener != null) {
            checkoutListener.checkoutConfirmed();
        }
    }

    private void cancel() {
        if (checkoutListener != null) {
            checkoutListener.checkoutCanceled();
        }
    }

    private void setColorTheme() {
        this.setBackground(Const.COLOR_CHECKOUT_WINDOW_BG);
        panel_totalAfter.setBackground(Const.COLOR_CHECKOUT_WINDOW_ACCENT);
        panel_paid.setBackground(Const.COLOR_CHECKOUT_WINDOW_ACCENT);
        panel_buttons.setBackground(Const.COLOR_CHECKOUT_WINDOW_ACCENT);

        JFormattedTextField spnDiscountText
                = (JFormattedTextField) spnDiscount.getEditor().getComponent(0);
        JFormattedTextField spnPaidText
                = (JFormattedTextField) spnPaid.getEditor().getComponent(0);
        spnDiscountText.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        spnDiscountText.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);
        spnPaidText.setSelectionColor(Const.COLOR_HIGHLIGHT_BG);
        spnPaidText.setSelectedTextColor(Const.COLOR_HIGHLIGHT_FG);
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

        lblCustomer = new javax.swing.JLabel();
        javax.swing.JLabel l_total = new javax.swing.JLabel();
        lblTotalPrice = new javax.swing.JLabel();
        javax.swing.JLabel l_discPer = new javax.swing.JLabel();
        spnDiscount = new javax.swing.JSpinner();
        javax.swing.JLabel l_discAmt = new javax.swing.JLabel();
        lblDiscountAmount = new javax.swing.JLabel();
        javax.swing.JSeparator sep_1 = new javax.swing.JSeparator();
        panel_totalAfter = new javax.swing.JPanel();
        lblTotalAfter = new javax.swing.JLabel();
        javax.swing.JLabel l_totalAfter = new javax.swing.JLabel();
        javax.swing.JSeparator sep_2 = new javax.swing.JSeparator();
        panel_paid = new javax.swing.JPanel();
        javax.swing.JLabel l_amtPaid = new javax.swing.JLabel();
        javax.swing.JLabel l_change = new javax.swing.JLabel();
        spnPaid = new javax.swing.JSpinner();
        lblChange = new javax.swing.JLabel();
        javax.swing.JSeparator sep_3 = new javax.swing.JSeparator();
        panel_buttons = new javax.swing.JPanel();
        btnConfirm = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(350, 410));
        setMinimumSize(new java.awt.Dimension(350, 410));
        setPreferredSize(new java.awt.Dimension(350, 410));
        setLayout(new java.awt.GridBagLayout());

        lblCustomer.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCustomer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCustomer.setText("Unregistered Customer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        add(lblCustomer, gridBagConstraints);

        l_total.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        l_total.setText("Total Price:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 17, 4);
        add(l_total, gridBagConstraints);

        lblTotalPrice.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblTotalPrice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalPrice.setText("0.00 ฿");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 14, 20);
        add(lblTotalPrice, gridBagConstraints);

        l_discPer.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        l_discPer.setText("Discount Percent:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 4, 4);
        add(l_discPer, gridBagConstraints);

        spnDiscount.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        spnDiscount.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 100.0d, 1.0d));
        spnDiscount.setEditor(new javax.swing.JSpinner.NumberEditor(spnDiscount, "0.00"));
        spnDiscount.setMinimumSize(new java.awt.Dimension(64, 26));
        spnDiscount.setPreferredSize(new java.awt.Dimension(96, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 20);
        add(spnDiscount, gridBagConstraints);

        l_discAmt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        l_discAmt.setText("Discount Amount:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 3, 4);
        add(l_discAmt, gridBagConstraints);

        lblDiscountAmount.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblDiscountAmount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDiscountAmount.setText("0.00 ฿");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 20);
        add(lblDiscountAmount, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        add(sep_1, gridBagConstraints);

        panel_totalAfter.setBackground(new java.awt.Color(255, 255, 255));
        panel_totalAfter.setLayout(new java.awt.GridBagLayout());

        lblTotalAfter.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        lblTotalAfter.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalAfter.setText("100,000.00 ฿");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 12, 20);
        panel_totalAfter.add(lblTotalAfter, gridBagConstraints);

        l_totalAfter.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        l_totalAfter.setText("Total After Discount:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 20, 0, 20);
        panel_totalAfter.add(l_totalAfter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(panel_totalAfter, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        add(sep_2, gridBagConstraints);

        panel_paid.setBackground(new java.awt.Color(204, 204, 204));
        panel_paid.setLayout(new java.awt.GridBagLayout());

        l_amtPaid.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        l_amtPaid.setText("Amount Paid:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(18, 20, 4, 4);
        panel_paid.add(l_amtPaid, gridBagConstraints);

        l_change.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        l_change.setText("Change:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 15, 4);
        panel_paid.add(l_change, gridBagConstraints);

        spnPaid.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        spnPaid.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, null, 100.0d));
        spnPaid.setEditor(new javax.swing.JSpinner.NumberEditor(spnPaid, "0.00"));
        spnPaid.setMinimumSize(new java.awt.Dimension(64, 26));
        spnPaid.setPreferredSize(new java.awt.Dimension(128, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(18, 4, 4, 20);
        panel_paid.add(spnPaid, gridBagConstraints);

        lblChange.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblChange.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblChange.setText("0.00 ฿");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 12, 20);
        panel_paid.add(lblChange, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(panel_paid, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(sep_3, gridBagConstraints);

        panel_buttons.setBackground(new java.awt.Color(204, 204, 204));

        btnConfirm.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnConfirm.setText("Confirm");
        btnConfirm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfirm.setPreferredSize(new java.awt.Dimension(128, 32));
        panel_buttons.add(btnConfirm);

        btnCancel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.setPreferredSize(new java.awt.Dimension(128, 32));
        panel_buttons.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(panel_buttons, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JLabel lblChange;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblDiscountAmount;
    private javax.swing.JLabel lblTotalAfter;
    private javax.swing.JLabel lblTotalPrice;
    private javax.swing.JPanel panel_buttons;
    private javax.swing.JPanel panel_paid;
    private javax.swing.JPanel panel_totalAfter;
    private javax.swing.JSpinner spnDiscount;
    private javax.swing.JSpinner spnPaid;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

    public interface CheckoutListener {

        void checkoutConfirmed();

        void checkoutCanceled();
    }
}
