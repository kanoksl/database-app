package database.test.data;

public class Product {

    private String id;
    private String name;
    private String description;
    private int stockQuantity;
    private boolean selling;
    private String categoryID;
    
    private double currentPrice = 10;

    /**
     * Create a new Product object from existing data.
     *
     * @param id            CHAR(8), NOT NULL
     * @param name          VARCHAR(64), NOT NULL
     * @param description   TEXT
     * @param stockQuantity INT, NOT NULL
     * @param selling       BIT, NOT NULL: 0 = not selling, 1 = selling
     * @param categoryID    CHAR(8)
     */
    public Product(String id, String name, String description,
                   int stockQuantity, boolean selling, String categoryID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.selling = selling;
        this.categoryID = categoryID;
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
        return new Product(id, null, null, 0, true, null);
    }
}
