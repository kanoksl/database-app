package database.test;

import database.test.gui.GenericTableWindow;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DatabaseUtilities {

    /**
     * Suggest next string in sequence.
     *
     * @param statement The Statement object connected to the database.
     * @param sql SQL command that selects the latest ID.
     * @param prefix String prefix in ID.
     * @param length Length of the ID field.
     * @return The next ID in sequence that can be inserted into the database.
     */
    public static String suggestNextID(Statement statement, String sql, String prefix, int length) {
        try {
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                String latestID = result.getString(1);
                int numNextID = Integer.parseInt(latestID.substring(prefix.length())) + 1;
                return String.format(prefix + "%0" + (length - prefix.length()) + "d", numNextID);
            } else {
                return String.format(prefix + "%0" + length + "d", 1);
            }
        } catch (SQLException | NumberFormatException ex) {
            System.err.println(ex);
            System.err.flush();
            return null;
        }

    }

    /**
     * Query using the given Statement and return the first column of the result
     * as a list of strings.
     *
     * @param statement The Statement object connected to the database.
     * @param sql The SQL query (SELECT) command.
     * @return First column of the query result.
     * @throws SQLException
     */
    public static List<String> querySingleColumnToList(Statement statement, String sql)
            throws SQLException {
        List<String> list = new ArrayList<>();
        ResultSet result = statement.executeQuery(sql);
        while (result.next()) {
            list.add(result.getString(1));
        }
        return list;
    }

    /**
     * Query using the given Statement and display the result in a GUI table
     * window.
     *
     * @param statement The Statement object connected to the database.
     * @param sql The SQL query (SELECT) command.
     * @throws SQLException
     */
    public static void queryToTableWindow(Statement statement, String sql)
            throws SQLException {
//        System.out.println("Begin query: " + sql);
        ResultSet result = statement.executeQuery(sql);
        GenericTableWindow gui
                = new GenericTableWindow(buildTableModel(result));
        gui.setTitle(sql);
        gui.setVisible(true);
    }

    /**
     * Create a TableModel from a database query result.
     *
     * @param resultSet Query result from a database.
     * @return a TableModel for displaying the data in a JTable.
     * @throws SQLException
     */
    public static TableModel buildTableModel(ResultSet resultSet)
            throws SQLException {
        if (resultSet == null) {
            return null;
        }

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //all cells are uneditable
            }
        };

        ResultSetMetaData meta = resultSet.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 0; i < columnCount; i++) {
            model.addColumn(meta.getColumnLabel(i + 1));
        }

        while (resultSet.next()) {
            Object[] rowData = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                rowData[i] = resultSet.getObject(i + 1);
            }
            model.addRow(rowData);
        }

        return model;
    }

    //<editor-fold desc="Data Conversion: Database Query Result --> Program">
    /**
     * Simple conversion from java.sql.Date to java.time.LocalDate
     *
     * @param sqlDate A date in java.sql.Date.
     * @return The same date in java.time.LocalDate.
     */
    public static LocalDate toLocalDate(Date sqlDate) {
        return sqlDate == null ? null : sqlDate.toLocalDate();
    }

    /**
     * Simple conversion from java.sql.Time to java.time.LocalTime
     *
     * @param sqlTime A time in java.sql.Time.
     * @return The same time in java.time.LocalTime.
     */
    public static LocalTime toLocalTime(Time sqlTime) {
        return sqlTime == null ? null : sqlTime.toLocalTime();
    }
    
    /**
     * Convert from a (single-character) string to char.
     *
     * @param string A string (can be longer, but the rest other than the first
     * character will be ignored).
     * @return The first character of the string. '\0' if the string is null.
     */
    public static char toChar(String string) {
        return string == null ? '\0' : string.charAt(0);
    }

    //</editor-fold>
    //<editor-fold desc="Data Conversion: Program --> Database Insert/Update">
    /**
     * Replace an empty string (after trimmed) with null.
     *
     * @param string A string.
     * @return The trimmed string if not empty. Null otherwise.
     */
    public static String nullable(String string) {
        string = string.trim();
        return string.isEmpty() ? null : string;
    }

    /**
     * Convert a java.time.LocalDate to java.sql.Date.
     *
     * @param date A date in java.time.LocalDate.
     * @return The trimmed string if not empty. Null otherwise.
     */
    public static Date nullable(LocalDate date) {
        return date == null ? null : Date.valueOf(date);
    }
    
    /**
     * Convert a java.time.LocalTime to java.sql.Time.
     *
     * @param time A time in java.time.LocalTime.
     * @return The trimmed string if not empty. Null otherwise.
     */
    public static Time nullable(LocalTime time) {
        return time == null ? null : Time.valueOf(time);
    }

    /**
     * Convert a character to string.
     *
     * @param ch A character.
     * @return Null if the character is '\0'. Otherwise a string representation
     * of that character.
     */
    public static String nullable(char ch) {
        return ch == '\0' ? null : String.valueOf(ch);
    }
    //</editor-fold>
}
