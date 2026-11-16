package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Embedded Derby connection and one-time schema initialization.
 */
public class DB {
    private static final String DB_URL =  "jdbc:derby:MohamedDonutShopDB;create=true;" + "user=MohamedDonutShopDB;password=MohamedDonutShopDB";

    private static DB instance;
    private final Connection connection;

    private DB() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);

        try {
            initializeSchema();
        } catch (SQLException ex) {
            try {
                connection.close();
            } catch (SQLException closeEx) {
                ex.addSuppressed(closeEx);
            }
            throw ex;
        }
    }

    public static synchronized DB getInstance() {
        if (instance == null) {
            try {
                instance = new DB();
            } catch (SQLException ex) {
                throw new IllegalStateException(
                        "Unable to initialize the Derby database", ex);
            }
        }
        return instance;
    }

    private boolean tableExists(String tableName) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(
                null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    private void initializeSchema() throws SQLException {
        if (!tableExists("MENU_ITEM")) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("""
                    CREATE TABLE MENU_ITEM (
                        ITEM_ID INT NOT NULL PRIMARY KEY,
                        ITEM_NAME VARCHAR(60) NOT NULL,
                        DESCRIPTION VARCHAR(200),
                        PRICE DECIMAL(7,2) NOT NULL,
                        AVAILABLE BOOLEAN NOT NULL,
                        CATEGORY VARCHAR(30) NOT NULL,
                        SEASON VARCHAR(20)
                    )
                    """);
            }
        }

        if (!tableExists("CUSTOMER_ORDER")) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("""
                    CREATE TABLE CUSTOMER_ORDER (
                        TRANSACTION_ID INT NOT NULL PRIMARY KEY,
                        CUSTOMER_NAME VARCHAR(100) NOT NULL,
                        ORDER_DATE TIMESTAMP NOT NULL
                    )
                    """);
            }
        }

        if (!tableExists("ORDER_ITEM")) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("""
                    CREATE TABLE ORDER_ITEM (
                        ORDER_ITEM_ID INT NOT NULL PRIMARY KEY,
                        TRANSACTION_ID INT NOT NULL,
                        ITEM_ID INT NOT NULL,
                        QUANTITY INT NOT NULL,
                        PRICE_AT_ORDER DECIMAL(7,2) NOT NULL,
                        ICING VARCHAR(40) NOT NULL,
                        FILLING VARCHAR(40) NOT NULL,
                        SPRINKLES BOOLEAN NOT NULL,
                        FOREIGN KEY (TRANSACTION_ID)
                            REFERENCES CUSTOMER_ORDER(TRANSACTION_ID),
                        FOREIGN KEY (ITEM_ID)
                            REFERENCES MENU_ITEM(ITEM_ID)
                    )
                    """);
            }
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }

    // The caller must close the ResultSet and its Statement.
    public ResultSet executeQuery(String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    public PreparedStatement getPreparedStatement(String sql)
            throws SQLException {
        return connection.prepareStatement(sql);
    }
}