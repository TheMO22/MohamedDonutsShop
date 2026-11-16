package entity;

import core.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAO implements DAO<Transaction>
{
    private final DB db = DB.getInstance();
    @Override
    public Optional<Transaction> get(int id)
    {
        String sql = """
                SELECT TRANSACTION_ID, CUSTOMER_NAME, ORDER_DATE
                FROM CUSTOMER_ORDER
                WHERE TRANSACTION_ID = ?
                """;

        try (PreparedStatement statement = db.getPreparedStatement(sql))
        {
            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next())
                {
                    return Optional.of(mapRow(results));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException( "Could not find transaction with ID " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Transaction> getAll()
    {
        String sql = """
                SELECT TRANSACTION_ID, CUSTOMER_NAME, ORDER_DATE
                FROM CUSTOMER_ORDER
                ORDER BY TRANSACTION_ID
                """;

        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement statement = db.getPreparedStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                transactions.add(mapRow(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load transactions", e);
        }

        return transactions;
    }

    @Override
    public void insert(Transaction transaction)
    {
        String sql = """
                INSERT INTO CUSTOMER_ORDER
                (TRANSACTION_ID, CUSTOMER_NAME, ORDER_DATE)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setInt(1, transaction.getTransactionId());
            statement.setString(2, transaction.getCustomerName());
            statement.setTimestamp(3, transaction.getOrderDate());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not add transaction", e);
        }
    }

    @Override
    public void update(Transaction transaction)
    {
        String sql = """
                UPDATE CUSTOMER_ORDER
                SET CUSTOMER_NAME = ?, ORDER_DATE = ?
                WHERE TRANSACTION_ID = ?
                """;

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setString(1, transaction.getCustomerName());
            statement.setTimestamp(2, transaction.getOrderDate());
            statement.setInt(3, transaction.getTransactionId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update transaction", e);
        }
    }

    @Override
    public void delete(Transaction transaction)
    {
        String sql =  "DELETE FROM CUSTOMER_ORDER WHERE TRANSACTION_ID = ?";
        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setInt(1, transaction.getTransactionId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete transaction", e);
        }
    }

    @Override
    public List<String> getColumnNames()
    {
        return List.of(
                "TRANSACTION_ID",
                "CUSTOMER_NAME",
                "ORDER_DATE"
                );
    }

    private Transaction mapRow(ResultSet results) throws SQLException
    {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(results.getInt("TRANSACTION_ID"));
        transaction.setCustomerName(results.getString("CUSTOMER_NAME"));
        transaction.setOrderDate(results.getTimestamp("ORDER_DATE"));
        return transaction;
    }

}
