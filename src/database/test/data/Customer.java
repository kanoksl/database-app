package database.test.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Kanoksilp
 */
public class Customer {

    public static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd");

    private String id;
    private String firstName;
    private String lastName;
    private char gender;
    private Date birthDay;
    private Date registeredDate;
    private String phoneNumber;
    private String emailAddress;

    // new Customer from existing data
    public Customer(String id, String firstName, String lastName, char gender,
            Date birthDay, Date registeredDate, String phoneNumber, String emailAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDay = birthDay;
        this.registeredDate = registeredDate;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Getters/Setters">
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
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
    //<editor-fold defaultstate="collapsed" desc="Additional Getters (With Calculation/Formatting)">
    public String getDisplayName() {
        String prefix = (gender == 'M') ? "Mr. " : (gender == 'F') ? "Ms. " : "";
        if (lastName == null) {
            return prefix + firstName;
        } else {
            return prefix + firstName + " " + lastName;
        }
    }

    public long getDaysSinceRegistered() {
        long today = Calendar.getInstance().getTime().getTime();
        long regDate = registeredDate.getTime();
        long diffTime = today - regDate;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);

        return diffDays;
    }

    public String getRegisterationInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, YYYY");
        long days = getDaysSinceRegistered();
        return String.format("Registered: %s (%s)", sdf.format(registeredDate),
                (days == 0) ? "today" : days + " days ago");
    }
    //</editor-fold>

    public String getSQLFormattedBirthDay() {
        return SQL_DATE_FORMAT.format(birthDay);
    }

    public String getSQLFormattedRegisteredDate() {
        return SQL_DATE_FORMAT.format(registeredDate);
    }

    public boolean isValid() {
        return (id != null) && (firstName != null);
    }

    public static Customer createNewCustomer(String id) {
        return new Customer(id, null, null, '\0', null, Calendar.getInstance().getTime(), null, null);
    }
}
