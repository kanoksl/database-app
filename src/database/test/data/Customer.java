package database.test.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Customer {

    private String id;
    private String firstName;
    private String lastName;
    private char gender;
    private LocalDate birthDay;
    private LocalDate registeredDate;
    private String phoneNumber;
    private String emailAddress;

    /**
     * Create a new Customer object from existing data.
     *
     * @param id CHAR(8), NOT NULL
     * @param firstName VARCHAR(32), NOT NULL
     * @param lastName VARCHAR(32)
     * @param gender CHAR(1): 'M', 'F', NULL
     * @param birthDay DATE
     * @param registeredDate DATE, NOT NULL
     * @param phoneNumber VARCHAR(10)
     * @param emailAddress VARCHAR(255)
     */
    public Customer(String id, String firstName, String lastName, char gender,
            LocalDate birthDay, LocalDate registeredDate,
            String phoneNumber, String emailAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDay = birthDay;
        this.registeredDate = registeredDate;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters: Standard (With Some Input Validation)">
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        switch (gender) {
            case 'M':
            case 'm':
                this.gender = 'M';
                break;
            case 'F':
            case 'f':
                this.gender = 'F';
                break;
            default:
                this.gender = '\0';
                break;
        }
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
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

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Getters/Setters: Additional (With Calculation/Formatting)">
    /**
     * Get the full name of the customer.
     *
     * @return The first name and last name, with gender prefix.
     */
    public String getDisplayName() {
        String prefix = (gender == 'M') ? "Mr. " : (gender == 'F') ? "Ms. " : "";
        if (lastName == null) {
            return prefix + firstName;
        } else {
            return prefix + firstName + " " + lastName;
        }
    }

    public long getDaysSinceRegistered() {
        return ChronoUnit.DAYS.between(registeredDate, LocalDate.now());
    }

    /**
     * Get the formatted registration date and the days since registered.
     *
     * @return Example: "Registered: January 1, 2016 (100 days ago)"
     */
    public String getRegistrationInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, YYYY");
        long days = getDaysSinceRegistered();
        return String.format("Registered: %s (%s)", registeredDate.format(formatter),
                (days == 0) ? "today" : days + " days ago");
    }
    //</editor-fold>

    public boolean isValid() {
        return (id != null) && (firstName != null);
    }

    @Override
    public String toString() {
        return "Customer {"
                + "\n  customer_id = " + id
                + ", \n  firstName = " + firstName
                + ", \n  lastName = " + lastName
                + ", \n  gender = " + gender
                + ", \n  birthDay = " + birthDay
                + ", \n  registeredDate = " + registeredDate
                + ", \n  phoneNumber = " + phoneNumber
                + ", \n  emailAddress = " + emailAddress
                + "\n}";
    }

    /**
     * Create a new Customer object without any data except the given ID and
     * registration date (automatically set to now).
     *
     * @param id CHAR(8) NOT NULL
     * @return A Customer with all other fields as null.
     */
    public static Customer createNewCustomer(String id) {
        return new Customer(id, null, null, '\0', null, LocalDate.now(), null, null);
    }
}
