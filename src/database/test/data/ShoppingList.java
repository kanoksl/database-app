package database.test.data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class ShoppingList {

    private Customer customer;
    private final List<LineItem> itemList = new ArrayList<>();

    private double totalPrice = 0;
    private int totalQuantity = 0;
    private double discountPercent = 0;
    private LocalDate checkoutDate = null;
    private LocalTime checkoutTime = null;

    //<editor-fold desc="List Methods (Add, Remove, Get, Clear)">
    /**
     * Add the specified product to the shopping list. The product should be
     * queried from the database to make sure that it exists. The actual added
     * amount is limited to the product's stock quantity.
     *
     * @param product A product to add.
     * @param quantity Amount to add.
     * @return An array of two integers. First one is the amount actually added.
     * The second one is the index of the added product in the list.
     */
    public int[] addItem(Product product, int quantity) {
        // search for the existing line with the same ID
        LineItem line = null;
        String productID = product.getID();
        int idx = 0;

        for (LineItem existingLine : itemList) {
            if (existingLine.product_id.equals(productID)) {
                line = existingLine;
                break;
            }
            idx++;
        }

        // if the list already contain this product
        if (line != null) {
            int max = product.getStockQuantity();
            if (line.quantity >= max) {
                return new int[]{0, idx};
            }
            int actualQty = (line.quantity + quantity > max) ? max - line.quantity : quantity;
            line.quantity += actualQty;
            totalQuantity += actualQty;
            totalPrice += line.unitPrice * actualQty;
            return new int[]{actualQty, idx};
        } else {
            // add data to the shopping list
            line = new LineItem(productID, product.getName(),
                    quantity, product.getCurrentPrice());
            itemList.add(line);
            totalQuantity += quantity;
            totalPrice += line.subtotal();
            return new int[]{quantity, idx};
        }
    }

    public void removeItemAt(int idx) {
        LineItem line = itemList.get(idx);
        totalQuantity -= line.quantity;
        totalPrice -= line.subtotal();
        itemList.remove(idx);
    }

    public LineItem getItemAt(int idx) {
        return itemList.get(idx);
    }

    public boolean isEmpty() {
        return itemList.isEmpty();
    }

    public void clear() {
        itemList.clear();
        totalPrice = 0;
        totalQuantity = 0;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters: Customer, Checkout DateTime">
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public LocalTime getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(LocalTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Getters/Setters: Price-Related Fields">
    public double getTotalPrice() {
        return totalPrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getDiscountAmount() {
        double amount = discountPercent * 0.01 * totalPrice;
        return ((double) Math.round(amount * 4)) / 4;
    }

    public double getTotalAfterDiscount() {
        return totalPrice - getDiscountAmount();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Table Model">
    /**
     * Get a model that represent the data in the shopping list.
     *
     * @return A TableModel to be used by JTable.
     */
    public TableModel getTableModel() {
        TableModel model = new AbstractTableModel() {
            private final String[] COLUMNS = {"Product ID", "Product Name",
                "Quantity", "Unit Price", "Subtotal"};

            @Override
            public int getRowCount() {
                return itemList.size();
            }

            @Override
            public int getColumnCount() {
                return COLUMNS.length;
            }

            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                LineItem line = itemList.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return " " + line.getProductID();
                    case 1:
                        return " " + line.getProductName();
                    case 2:
                        return line.getQuantityString();
                    case 3:
                        return line.getUnitPriceString();
                    case 4:
                        return line.getSubtotalString();
                }
                return null;
            }
        };
        return model;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "ShoppingList {"
                + "\n  customer_id = " + customer.getID()
                + ", \n  itemList = " + itemList
                + ", \n  totalPrice = " + totalPrice
                + ", \n  totalQuantity = " + totalQuantity
                + ", \n  discountPercent = " + discountPercent
                + "\n}";
    }

    public static class LineItem {

        private final String product_id;
        private final String product_name;
        private int quantity;
        private double unitPrice;

        public LineItem(String product_id, String product_name,
                int quantity, double unitPrice) {
            this.product_id = product_id;
            this.product_name = product_name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public double subtotal() {
            return unitPrice * quantity;
        }

        public String getProductID() {
            return product_id;
        }

        public String getProductName() {
            return product_name;
        }

        public String getQuantityString() {
            return String.format("%,d ", quantity);
        }

        public String getUnitPriceString() {
            return String.format("%,.2f ", unitPrice);
        }

        public String getSubtotalString() {
            return String.format("%,.2f ", unitPrice * quantity);
        }

        @Override
        public String toString() {
            return "LineItem(" + product_id + ", " + quantity + ")";
        }

    }

}
