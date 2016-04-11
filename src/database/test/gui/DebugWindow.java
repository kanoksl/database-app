package database.test.gui;

import database.test.DatabaseManager;
import database.test.data.Customer;
import database.test.gui.component.TextLineNumber;
import java.sql.*;
import javax.swing.JTextArea;

public class DebugWindow 
        extends javax.swing.JFrame {

    private DatabaseManager dbmanager = null;
    
    private Connection connection = null;
    private Statement statement = null;

    private boolean isConnected = false;

    public DebugWindow() {
        this.initComponents();
        this.initListeners();

        TextLineNumber lineNumber1 = new TextLineNumber(txtArea_connection);
        txtArea_connection_scrollPane.setRowHeaderView(lineNumber1);
        TextLineNumber lineNumber2 = new TextLineNumber(txtArea_sql);
        txtArea_sql_scrollPane.setRowHeaderView(lineNumber2);
        TextLineNumber lineNumber3 = new TextLineNumber(txtArea_log);
        txtArea_log_scrollPane.setRowHeaderView(lineNumber3);

        splitPane.setDividerLocation(0.5);
        
        other_btnTestInsertCustomer.addActionListener((ActionEvent) -> {
            Customer c = Customer.createNewCustomer(dbmanager.getNextCustomerID());
            System.out.println("c.id = " + c.getId());
            c.setFirstName("FIRSTNAME");
            dbmanager.insertCustomer(c);
        });
    }

    //<editor-fold desc="Database Code">
    private void connectDatabase() {
        if (isConnected) {
            this.disconnectDatabase();
        }
        this.log(txtArea_connection, "Connecting to the database...");

        String hostport = tbxHostPort.getText();
        String database = tbxDatabase.getText();

        String username = tbxUsername.getText();
        String password = new String(tbxPassword.getPassword());

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(String.format(
                    "jdbc:mysql://%s/%s?user=%s&password=%s&useSSL=false",
                    hostport, database, username, password));
            statement = connection.createStatement();
            isConnected = true;

            this.log(txtArea_connection, "Connection established. The database is '"
                    + database + "' on " + hostport + ".\n  The current user is '" + this.getCurrentUser() + "'");

        } catch (ClassNotFoundException | SQLException ex) {
            this.log(txtArea_connection, "Error connecting to the database:\n\t" + ex.toString());
        }
        
        dbmanager = new DatabaseManager(hostport.substring(0, hostport.indexOf(":")), hostport.substring(hostport.indexOf(":") + 1), database);
        dbmanager.setUsername(username);
        dbmanager.setPassword(password);
        dbmanager.connect();
    }

    private void disconnectDatabase() {
        this.log(txtArea_connection, "Disconnecting from the database...");
        try {
            connection.close();
            isConnected = false;
            this.log(txtArea_connection, "Disconnected successfully.");
        } catch (SQLException ex) {
            this.log(txtArea_connection, "Error disconnecting from the database:\n\t" + ex.toString());
        }
    }

    private String getCurrentUser() {
        try {
            ResultSet result = statement.executeQuery("SELECT CURRENT_USER();");
            result.next();
            return result.getString(1);
        } catch (SQLException ex) {
            this.log(txtArea_connection, "Error getting current user:\n\t" + ex.toString());
            return null;
        }
    }

    private void executeSQL() {
        if (!isConnected) {
            this.log(txtArea_log, "Cannot execute SQL command. Connect to a database first.");
            return;
        }
        String sql = txtArea_sql.getText();

        try {
            statement.execute(sql);
            this.log(txtArea_log, "Execution successful for the command:"
                    + "\n--------------------\n"
                    + sql
                    + "\n--------------------");
        } catch (SQLException ex) {
            this.log(txtArea_log, "Error executing the command:"
                    + "\n--------------------\n"
                    + sql
                    + "\n--------------------\n"
                    + "Error message: " + ex.toString());
        }
    }

    private void queryToTable() {
        if (!isConnected) {
            this.log(txtArea_log, "Cannot execute SQL command. Connect to a database first.");
            return;
        }
        String sql = txtArea_sql.getText();

        try {
            DatabaseManager.queryToTable(statement, sql);
            this.log(txtArea_log, "Query to table successful for the command:"
                    + "\n--------------------\n"
                    + sql
                    + "\n--------------------");
        } catch (SQLException ex) {
            this.log(txtArea_log, "Error querying to table:"
                    + "\n--------------------\n"
                    + sql
                    + "\n--------------------\n"
                    + "Error message: " + ex.toString());
        }
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GUI Code: Custom Initialization and Methods">
    private void initListeners() {
        btnConnect.addActionListener((ActionEvent) -> {
            this.connectDatabase();
        });
        btnDisconnect.addActionListener((ActionEvent) -> {
            this.disconnectDatabase();
        });

        btnExecute.addActionListener((ActionEvent) -> {
            this.executeSQL();
        });
        btnQueryToTable.addActionListener((ActionEvent) -> {
            this.queryToTable();
        });
    }

    private void log(JTextArea textArea, String text) {
        textArea.setText(textArea.getText().concat(text + "\n\n"));
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GUI Code: Automatically Generated by NetBeans">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = new javax.swing.JTabbedPane();
        tab1_connection = new javax.swing.JPanel();
        javax.swing.JLabel l_host = new javax.swing.JLabel();
        tbxHostPort = new javax.swing.JTextField();
        javax.swing.JLabel l_database = new javax.swing.JLabel();
        tbxDatabase = new javax.swing.JTextField();
        javax.swing.JLabel l_user = new javax.swing.JLabel();
        tbxUsername = new javax.swing.JTextField();
        javax.swing.JLabel l_pass = new javax.swing.JLabel();
        tbxPassword = new javax.swing.JPasswordField();
        btnConnect = new javax.swing.JButton();
        btnDisconnect = new javax.swing.JButton();
        txtArea_connection_scrollPane = new javax.swing.JScrollPane();
        txtArea_connection = new javax.swing.JTextArea();
        javax.swing.JLabel placeholder1 = new javax.swing.JLabel();
        tab2_execute = new javax.swing.JPanel();
        splitPane = new javax.swing.JSplitPane();
        txtArea_sql_scrollPane = new javax.swing.JScrollPane();
        txtArea_sql = new javax.swing.JTextArea();
        txtArea_log_scrollPane = new javax.swing.JScrollPane();
        txtArea_log = new javax.swing.JTextArea();
        btnExecute = new javax.swing.JButton();
        btnQueryToTable = new javax.swing.JButton();
        tab3_others = new javax.swing.JPanel();
        other_btnTestInsertCustomer = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Debug");
        setMinimumSize(new java.awt.Dimension(800, 540));
        setPreferredSize(new java.awt.Dimension(800, 540));
        setSize(new java.awt.Dimension(800, 540));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        tab1_connection.setLayout(new java.awt.GridBagLayout());

        l_host.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_host.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_host.setText("Host/Port:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        tab1_connection.add(l_host, gridBagConstraints);

        tbxHostPort.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxHostPort.setText("localhost:3306");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        tab1_connection.add(tbxHostPort, gridBagConstraints);

        l_database.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_database.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_database.setText("Database:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        tab1_connection.add(l_database, gridBagConstraints);

        tbxDatabase.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxDatabase.setText("retaildb_v1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        tab1_connection.add(tbxDatabase, gridBagConstraints);

        l_user.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_user.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_user.setText("Username:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 4);
        tab1_connection.add(l_user, gridBagConstraints);

        tbxUsername.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxUsername.setText("root");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        tab1_connection.add(tbxUsername, gridBagConstraints);

        l_pass.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        l_pass.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        l_pass.setText("Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        tab1_connection.add(l_pass, gridBagConstraints);

        tbxPassword.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbxPassword.setText("admin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        tab1_connection.add(tbxPassword, gridBagConstraints);

        btnConnect.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnConnect.setText("Connect");
        btnConnect.setMaximumSize(new java.awt.Dimension(128, 36));
        btnConnect.setMinimumSize(new java.awt.Dimension(128, 36));
        btnConnect.setName(""); // NOI18N
        btnConnect.setPreferredSize(new java.awt.Dimension(96, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        tab1_connection.add(btnConnect, gridBagConstraints);

        btnDisconnect.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnDisconnect.setText("Disconnect");
        btnDisconnect.setMaximumSize(new java.awt.Dimension(128, 36));
        btnDisconnect.setMinimumSize(new java.awt.Dimension(128, 36));
        btnDisconnect.setName(""); // NOI18N
        btnDisconnect.setPreferredSize(new java.awt.Dimension(96, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        tab1_connection.add(btnDisconnect, gridBagConstraints);

        txtArea_connection.setColumns(20);
        txtArea_connection.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        txtArea_connection.setRows(5);
        txtArea_connection.setMargin(new java.awt.Insets(2, 8, 2, 2));
        txtArea_connection_scrollPane.setViewportView(txtArea_connection);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 0, 0);
        tab1_connection.add(txtArea_connection_scrollPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 8, 0);
        tab1_connection.add(placeholder1, gridBagConstraints);

        tabbedPane.addTab("Database Connection", tab1_connection);

        tab2_execute.setLayout(new java.awt.GridBagLayout());

        splitPane.setDividerLocation(400);

        txtArea_sql.setColumns(20);
        txtArea_sql.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        txtArea_sql.setRows(5);
        txtArea_sql.setMargin(new java.awt.Insets(2, 8, 2, 2));
        txtArea_sql_scrollPane.setViewportView(txtArea_sql);

        splitPane.setLeftComponent(txtArea_sql_scrollPane);

        txtArea_log.setEditable(false);
        txtArea_log.setBackground(new java.awt.Color(247, 247, 247));
        txtArea_log.setColumns(20);
        txtArea_log.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        txtArea_log.setRows(5);
        txtArea_log.setMargin(new java.awt.Insets(2, 8, 2, 2));
        txtArea_log_scrollPane.setViewportView(txtArea_log);

        splitPane.setRightComponent(txtArea_log_scrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        tab2_execute.add(splitPane, gridBagConstraints);

        btnExecute.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnExecute.setText("Execute");
        btnExecute.setMaximumSize(new java.awt.Dimension(128, 36));
        btnExecute.setMinimumSize(new java.awt.Dimension(128, 36));
        btnExecute.setName(""); // NOI18N
        btnExecute.setPreferredSize(new java.awt.Dimension(128, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 4);
        tab2_execute.add(btnExecute, gridBagConstraints);

        btnQueryToTable.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        btnQueryToTable.setText("Query to Table...");
        btnQueryToTable.setMaximumSize(new java.awt.Dimension(128, 36));
        btnQueryToTable.setMinimumSize(new java.awt.Dimension(128, 36));
        btnQueryToTable.setName(""); // NOI18N
        btnQueryToTable.setPreferredSize(new java.awt.Dimension(128, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        tab2_execute.add(btnQueryToTable, gridBagConstraints);

        tabbedPane.addTab("Execute Commands", tab2_execute);

        tab3_others.setLayout(new java.awt.GridBagLayout());

        other_btnTestInsertCustomer.setText("Test DatabaseManager.insertCustomer()");
        tab3_others.add(other_btnTestInsertCustomer, new java.awt.GridBagConstraints());

        tabbedPane.addTab("Other Specific Functions", tab3_others);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(tabbedPane, gridBagConstraints);

        jMenu1.setText("File");
        menuBar.add(jMenu1);

        jMenu2.setText("Edit");
        menuBar.add(jMenu2);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnDisconnect;
    private javax.swing.JButton btnExecute;
    private javax.swing.JButton btnQueryToTable;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton other_btnTestInsertCustomer;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JPanel tab1_connection;
    private javax.swing.JPanel tab2_execute;
    private javax.swing.JPanel tab3_others;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField tbxDatabase;
    private javax.swing.JTextField tbxHostPort;
    private javax.swing.JPasswordField tbxPassword;
    private javax.swing.JTextField tbxUsername;
    private javax.swing.JTextArea txtArea_connection;
    private javax.swing.JScrollPane txtArea_connection_scrollPane;
    private javax.swing.JTextArea txtArea_log;
    private javax.swing.JScrollPane txtArea_log_scrollPane;
    private javax.swing.JTextArea txtArea_sql;
    private javax.swing.JScrollPane txtArea_sql_scrollPane;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>
}
