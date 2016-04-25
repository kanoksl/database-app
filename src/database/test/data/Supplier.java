package database.test.data;

import database.test.gui.Util;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class Supplier {

    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String emailAddress;
    private String website;
    private String notes;

    /**
     * Create a new Supplier object from existing data.
     *
     * @param id CHAR(8), NOT NULL
     * @param name VARCHAR(64), NOT NULL
     * @param address VARCHAR(255)
     * @param phoneNumber VARCHAR(10)
     * @param emailAddress VARCHAR(255)
     * @param website VARCHAR(255)
     * @param notes TEXT
     */
    public Supplier(String id, String name, String address,
            String phoneNumber, String emailAddress, String website, String notes) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.website = website;
        this.notes = notes;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "Supplier {"
                + "\n  supplier_id = " + id
                + ", \n  supplier_name = " + name
                + ", \n  supplier_address = " + address
                + ", \n  supplier_phone = " + phoneNumber
                + ", \n  supplier_email = " + emailAddress
                + ", \n  supplier_website = " + website
                + ", \n  notes = " + notes
                + "\n}";
    }

    public String shortDescription() {
        return String.format("%s : %s", id, name);
    }

    //<editor-fold defaultstate="collapsed" desc="GUI Code: Table Model">
    public static final String[] TABLE_COLUMNS = {
        "Supplier ID", "Name", "Address", "Phone", "Email", "Website"};

    public static TableModel createTableModel(List<Supplier> list) {
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
                Supplier s = list.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return s.getID();
                    case 1:
                        return s.getName();
                    case 2:
                        return s.getAddress();
                    case 3:
                        return Util.formatPhoneNumber(s.phoneNumber);
                    case 4:
                        return s.getEmailAddress();
                    case 5:
                        return s.getWebsite();
                }
                return null;
            }
        };
    }
    //</editor-fold>

    /**
     * Create a new Supplier object without any data except the given ID.
     *
     * @param id CHAR(8), NOT NULL
     * @return A Product with all other fields as null.
     */
    public static Supplier createNewSupplier(String id) {
        return new Supplier(id, null, null, null, null, null, null);
    }
}
