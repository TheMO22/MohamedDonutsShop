package entity;

import core.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DonutsDAO implements DAO<Donuts>
{
    private final DB db = DB.getInstance();

    @Override
    public Optional<Donuts> get(int id)
    {
        String sql = """ 
                SELECT ITEM_ID, ITEM_NAME, DESCRIPTION, PRICE, AVAILABLE, CATEGORY, SEASON
                FROM MENU_ITEM
                WHERE ITEM_ID = ?
                """;

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return Optional.of(mapRow(results));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not find donut with ID " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Donuts> getAll()
    {
        String sql = """
                SELECT ITEM_ID, ITEM_NAME, DESCRIPTION, PRICE, AVAILABLE, CATEGORY, SEASON
                FROM MENU_ITEM
                ORDER BY ITEM_ID
                """;

        List<Donuts> donuts = new ArrayList<>();

        try (PreparedStatement statement = db.getPreparedStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                donuts.add(mapRow(results));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load the donut menu", e);
        }

        return donuts;
    }

    @Override
    public void insert(Donuts donut)
    {
        String sql = """
                INSERT INTO MENU_ITEM
                (ITEM_ID, ITEM_NAME, DESCRIPTION, PRICE, AVAILABLE, CATEGORY, SEASON)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setInt(1, donut.getDonutId());
            statement.setString(2, donut.getDonutName());
            statement.setString(3, donut.getDescription());
            statement.setDouble(4, donut.getPrice());
            statement.setBoolean(5, donut.isAvailable());
            statement.setString(6, donut.getCategory());
            statement.setString(7, donut.getSeason());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not add donut", e);
        }
    }

    @Override
    public void update(Donuts donut)
    {
        String sql = """
                UPDATE MENU_ITEM
                SET ITEM_NAME = ?, DESCRIPTION = ?, PRICE = ?, AVAILABLE = ?, CATEGORY = ?, SEASON = ?
                WHERE ITEM_ID = ?
                """;

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setString(1, donut.getDonutName());
            statement.setString(2, donut.getDescription());
            statement.setDouble(3, donut.getPrice());
            statement.setBoolean(4, donut.isAvailable());
            statement.setString(5, donut.getCategory());
            statement.setString(6, donut.getSeason());
            statement.setInt(7, donut.getDonutId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update donut", e);
        }
    }

    @Override
    public void delete(Donuts donut)
    {
        String sql = "DELETE FROM MENU_ITEM WHERE ITEM_ID = ?";

        try (PreparedStatement statement = db.getPreparedStatement(sql)) {
            statement.setInt(1, donut.getDonutId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete donut", e);
        }
    }

    @Override
    public List<String> getColumnNames()
    {
        return List.of(
                "ITEM_ID",
                "ITEM_NAME",
                "DESCRIPTION",
                "PRICE",
                "AVAILABLE",
                "CATEGORY",
                "SEASON"
        );
    }

    private Donuts mapRow(ResultSet results) throws SQLException
    {
        Donuts donut = new Donuts();
        donut.setDonutId(results.getInt("ITEM_ID"));
        donut.setDonutName(results.getString("ITEM_NAME"));
        donut.setDescription(results.getString("DESCRIPTION"));
        donut.setPrice(results.getDouble("PRICE"));
        donut.setAvailable(results.getBoolean("AVAILABLE"));
        donut.setCategory(results.getString("CATEGORY"));
        donut.setSeason(results.getString("SEASON"));
        return donut;
    }
}