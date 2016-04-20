package database.test.data;

import database.test.gui.Const;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class Product {

    private String id;
    private String name;
    private String description;
    private int stockQuantity;
    private boolean selling;
    private String categoryID;

    private double currentPrice;
    private boolean priceChanged;

    /**
     * Create a new Product object from existing data.
     *
     * @param id CHAR(8), NOT NULL
     * @param name VARCHAR(64), NOT NULL
     * @param description TEXT
     * @param stockQuantity INT, NOT NULL
     * @param selling BIT, NOT NULL: 0 = not selling, 1 = selling
     * @param categoryID CHAR(8)
     * @param currentPrice
     */
    public Product(String id, String name, String description,
            int stockQuantity, boolean selling, String categoryID, double currentPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.selling = selling;
        this.categoryID = categoryID;
        this.currentPrice = currentPrice;
        this.priceChanged = false;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters: Standard (With Some Input Validation)">
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = Math.max(0, stockQuantity);
    }

    public boolean isSelling() {
        return selling;
    }

    public void setSelling(boolean selling) {
        this.selling = selling;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public boolean isPriceChanged() {
        return priceChanged;
    }

    public void setPriceChanged(boolean priceChanged) {
        this.priceChanged = priceChanged;
    }
    
    
    //</editor-fold>

    public String getCurrentPriceString() {
        return String.format("%,.2f " + Const.CURRENCY, currentPrice);
    }

    @Override
    public String toString() {
        return "Product { "
                + "\n  product_id = " + id
                + ", \n  product_name = " + name
                + ", \n  product_description = " + description
                + ", \n  stock_quantity = " + stockQuantity
                + ", \n  selling_status = " + selling
                + ", \n  category_id = " + categoryID
                + ", \n  # product_price = " + currentPrice
                + ", \n  # price_changed = " + priceChanged
                + "\n}";
    }

    public String shortDescription() {
        return String.format("%s : %s", id, name);
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Table Model">
    public static final String[] TABLE_COLUMNS = {
            "Product ID", "Product Name", "Category ID", "Stock Quantity", "Current Price"};

    public static TableModel createTableModel(List<Product> list) {
        return new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return list.size();
            }

            @Override
            public int getColumnCount() {
                return TABLE_COLUMNS.length;
            }

            @Override
            public String getColumnName(int column) {
                return TABLE_COLUMNS[column];
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Product p = list.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return p.getID();
                    case 1:
                        return p.getName();
                    case 2:
                        return p.getCategoryID();
                    case 3:
                        return p.getStockQuantity();
                    case 4:
                        return p.getCurrentPrice();
                }
                return null;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class;
                if (columnIndex == 4) return Double.class;
                else return String.class;
            }

            
        };
    }
    //</editor-fold>

    /**
     * Create a new Product object without any data except the given ID. (The
     * stock quantity is automatically set to 0, and the selling status is set
     * to 'selling').
     *
     * @param id CHAR(8), NOT NULL
     * @return A Product with all other fields as null.
     */
    public static Product createNewProduct(String id) {
        return new Product(id, null, null, 0, true, null, 0);
    }
}
