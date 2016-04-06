package database.test;

import database.test.gui.GenericTableWindow;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class DatabaseManager {

    private Connection connection = null;
    private Statement statement = null;

    private final String host;
    private final String port;
    private final String database;
    private String username;
    private String password;

    private boolean connected = false;

    public DatabaseManager(String host, String port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    //<editor-fold desc="Database Connection: Username/Password, Connect, Disconnect">
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(String.format(
                    "jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=false",
                    host, port, database, username, password));
            statement = connection.createStatement();
            connected = true;
        } catch (ClassNotFoundException ex) {
            System.err.println(ex);
            System.err.flush();
        } catch (SQLException ex) {
            System.err.println("Error connecting to the database :\n\t" + ex);
            System.err.flush();
        }
        return connected;
    }

    public boolean disconnect() {
        try {
            connection.close();
            connected = false;
        } catch (SQLException ex) {
            System.err.println("Error disconnecting the database :\n\t" + ex);
            System.err.flush();
        }
        return !connected;
    }

    public boolean isConnected() {
        return connected;
    }
    //</editor-fold>

    /**
     * Get the current user name from the database system.
     *
     * @return A result from 'SELECT CURRENT_USER()' command.
     */
    public String queryCurrentUser() {
        try {
            ResultSet result = statement.executeQuery("SELECT CURRENT_USER();");
            result.next();
            return result.getString(1);
        } catch (SQLException ex) {
            System.err.println(ex);
            return null;
        }

    }

    //<editor-fold desc="Static Methods / Utilities">
    /**
     * Query the given database and display the result in a GUI table window.
     *
     * @param database The database to be queried.
     * @param sql The SQL query (SELECT) command.
     * @throws java.sql.SQLException
     */
    public static void queryToTable(DatabaseManager database, String sql)
            throws SQLException {
        queryToTable(database.statement, sql);
    }

    /**
     * Query using the given Statement and display the result in a GUI table
     * window.
     *
     * @param statement The Statement object connected to the database.
     * @param sql The SQL query (SELECT) command.
     * @throws java.sql.SQLException
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
     * @throws java.sql.SQLException
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

    //</editor-fold>
}
