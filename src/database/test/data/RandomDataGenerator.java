package database.test.data;

import database.test.DatabaseManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomDataGenerator {

    private static DatabaseManager database
            = new DatabaseManager("localhost", "3306", "retaildb_v1");

    private static final double TIMES_BOUGHT_MEAN = 3;
    private static final double TIMES_BOUGHT_SD = 2.4;

    private static final double NUM_PRODUCT_MEAN = 1.8;
    private static final double NUM_PRODUCT_SD = 1;

    private static final double SALE_QTY_MEAN = 1.6;
    private static final double SALE_QTY_SD = 0.8;

    public static void generateSaleRecords() {
        List<ShoppingList> sales = new LinkedList<>();

        Random r = new Random();

        List<Customer> cusList = database.queryAllCustomers();
//        cusList.removeIf((Customer t) -> t.getID().equals("C0000000") || t.getID().equals("CDELETED"));

        List<Product> proList = database.queryAllProducts();
        int proCount = proList.size();

        for (Customer c : cusList) {
            LocalDate regDate = c.getRegisteredDate();
            int regAge = (int) c.getDaysSinceRegistered();

            int timesBought;
            if (c.getID().equals("C0000000")) {
                timesBought = 100 + r.nextInt(20);
            } else if (c.getID().equals("CDELETED")) {
                timesBought = r.nextInt(15);
            } else {
                timesBought = 1 + Math.abs((int) ((r.nextGaussian() * TIMES_BOUGHT_SD) + TIMES_BOUGHT_MEAN));
            }
            System.out.println("TB: " + timesBought + " << " + c.shortDescription());

            for (int i = 0; i < timesBought; i++) {
                ShoppingList shop = new ShoppingList();
                // random date
                LocalDate buyDate;
                if (i == 0) {
                    buyDate = regDate;
                } else {
                    int diff = 1 + r.nextInt(regAge);
                    buyDate = regDate.plusDays(diff);
                }
                // random time
                int hh = 9 + r.nextInt(12);
                int mm = r.nextInt(60);
                LocalTime buyTime = LocalTime.of(hh, mm);
                // random number of products
                int numProduct = 1 + Math.abs((int) ((r.nextGaussian() * NUM_PRODUCT_SD) + NUM_PRODUCT_MEAN));

                if (c.getID().equals("C0000000") || c.getID().equals("CDELETED")) {
                    shop.setDiscountPercent(5);
                } else {
                    shop.setDiscountPercent(0);
                }
                shop.setCheckoutDate(buyDate);
                shop.setCheckoutTime(buyTime);
                shop.setCustomer(c);
                System.out.println("  -> " + buyDate + " " + buyTime + " NP: " + numProduct);

                for (int j = 0; j < numProduct; j++) {
                    int pID = 1 + r.nextInt(proCount);
                    String productID = String.format("P%07d", pID);
                    int qty = 1 + Math.abs((int) ((r.nextGaussian() * SALE_QTY_SD) + SALE_QTY_MEAN));
                    System.out.println("    --> P.ID = " + productID + " ; QTY = " + qty);
                    shop.forceAdd(productID, qty);
                }

                sales.add(shop);
            }
        }

        sales.sort((ShoppingList o1, ShoppingList o2) -> {
            if (o1.getCheckoutDate().isEqual(o2.getCheckoutDate())) {
                return o1.getCheckoutTime().compareTo(o2.getCheckoutTime());
            } else {
                return o1.getCheckoutDate().compareTo(o2.getCheckoutDate());
            }
        });

        int count = 0;
        for (ShoppingList s : sales) {
            try {
                database.insertSaleRecord(s);
                System.out.println("Inserted sale #" + (count++));
            } catch (SQLException ex) {
                System.err.println("Error inserting sale: " + ex.getMessage());
            }
        }
        System.out.println("Done inserting test data. Count = " + count);
    }

    public static void main(String[] args) {
        database.setUsername("root");
        database.setPassword("admin");
        database.connect();

        generateSaleRecords();
    }
}
