package database.test;

import database.test.gui.GenericTableWindow;
import java.sql.Date;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
    public static void queryToTable(Statement statement, String sql)
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
        DefaultTableModel model = new DefaultTableModel();

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

    public static LocalDate toLocalDate(Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }
        return sqlDate.toLocalDate();
    }
}
