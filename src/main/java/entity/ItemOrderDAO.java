package entity;

import core.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemOrderDAO implements DAO<ItemOrder>
{
    private final DB db = DB.getInstance();

    @Override
    public Optional<ItemOrder> get(int id)
    {
        String sql = """
                SELECT ORDER_ITEM_ID, TRANSACTION_ID, ITEM_ID, QUANTITY, PRICE_AT_ORDER, ICING, FILLING, SPRINKLES
                FROM ORDER_ITEM
                WHERE ORDER_ITEM_ID = ?
                """;

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return Optional.of(mapRow(results));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not find order item with ID " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public List<ItemOrder> getAll()
    {
        String sql = """
                SELECT ORDER_ITEM_ID, TRANSACTION_ID, ITEM_ID, QUANTITY,
                       PRICE_AT_ORDER, ICING, FILLING, SPRINKLES
                FROM ORDER_ITEM
                ORDER BY ORDER_ITEM_ID
                """;

        List<ItemOrder> orderItems = new ArrayList<>();

        try (PreparedStatement statement = db.getPreparedStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                orderItems.add(mapRow(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load order items", e);
        }

        return orderItems;
    }

    @Override
    public void insert(ItemOrder itemOrder)
    {
        String sql = """
                INSERT INTO ORDER_ITEM
                (ORDER_ITEM_ID, TRANSACTION_ID, ITEM_ID, QUANTITY,PRICE_AT_ORDER, ICING, FILLING, SPRINKLES)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setInt(1, itemOrder.getOrderItemId());
            statement.setInt(2, itemOrder.getTransactionId());
            statement.setInt(3, itemOrder.getDonutId());
            statement.setInt(4, itemOrder.getQuantity());
            statement.setDouble(5, itemOrder.getPriceOfItem());
            statement.setString(6, itemOrder.getIcing());
            statement.setString(7, itemOrder.getFilling());
            statement.setBoolean(8, itemOrder.isSprinkles());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not add order item", e);
        }
    }

    @Override
    public void update(ItemOrder itemOrder)
    {
        String sql = """
                UPDATE ORDER_ITEM
                SET TRANSACTION_ID = ?, ITEM_ID = ?, QUANTITY = ?, PRICE_AT_ORDER = ?, ICING = ?, FILLING = ?, SPRINKLES = ?
                WHERE ORDER_ITEM_ID = ?
                """;

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setInt(1, itemOrder.getTransactionId());
            statement.setInt(2, itemOrder.getDonutId());
            statement.setInt(3, itemOrder.getQuantity());
            statement.setDouble(4, itemOrder.getPriceOfItem());
            statement.setString(5, itemOrder.getIcing());
            statement.setString(6, itemOrder.getFilling());
            statement.setBoolean(7, itemOrder.isSprinkles());
            statement.setInt(8, itemOrder.getOrderItemId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update order item", e);
        }
    }

    @Override
    public void delete(ItemOrder itemOrder)
    {
        String sql = "DELETE FROM ORDER_ITEM WHERE ORDER_ITEM_ID = ?";

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setInt(1, itemOrder.getOrderItemId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete order item", e);
        }
    }

    @Override
    public List<String> getColumnNames()
    {
        return List.of(
                "ORDER_ITEM_ID",
                "TRANSACTION_ID",
                "ITEM_ID",
                "QUANTITY",
                "PRICE_AT_ORDER",
                "ICING",
                "FILLING",
                "SPRINKLES"
        );
    }

    private ItemOrder mapRow(ResultSet results) throws SQLException
    {
        ItemOrder itemOrder = new ItemOrder();
        itemOrder.setOrderItemId(results.getInt("ORDER_ITEM_ID"));
        itemOrder.setTransactionId(results.getInt("TRANSACTION_ID"));
        itemOrder.setDonutId(results.getInt("ITEM_ID"));
        itemOrder.setQuantity(results.getInt("QUANTITY"));
        itemOrder.setPriceOfItem(results.getDouble("PRICE_AT_ORDER"));
        itemOrder.setIcing(results.getString("ICING"));
        itemOrder.setFilling(results.getString("FILLING"));
        itemOrder.setSprinkles(results.getBoolean("SPRINKLES"));
        return itemOrder;
    }
}