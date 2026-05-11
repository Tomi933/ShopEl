package db;

import core.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object для таблиці products.
 * Всі операції з БД: читання, додавання, оновлення, видалення.
 */
public class ProductDAO {

    private final Connection conn;

    public ProductDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    // ── READ ───────────────────────────────────────────────────

    /** Повертає всі товари з БД. */
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY category, name";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Помилка читання товарів: " + e.getMessage());
        }
        return list;
    }

    /** Повертає товари за категорією. */
    public List<Product> getByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ? ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("Помилка фільтрації: " + e.getMessage());
        }
        return list;
    }

    /** Повертає унікальні категорії. */
    public List<String> getCategories() {
        List<String> cats = new ArrayList<>();
        cats.add("Всі");
        String sql = "SELECT DISTINCT category FROM products ORDER BY category";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) cats.add(rs.getString("category"));
        } catch (SQLException e) {
            System.err.println("Помилка читання категорій: " + e.getMessage());
        }
        return cats;
    }

    // ── CREATE ─────────────────────────────────────────────────

    /** Додає новий товар. Повертає true якщо успішно. */
    public boolean add(String name, double price, int quantity,
                       String category, String imagePath) {
        String sql = """
            INSERT INTO products(name, price, quantity, category, imagePath)
            VALUES(?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setDouble(2, price);
            ps.setInt(3, quantity);
            ps.setString(4, category.trim());
            ps.setString(5, imagePath.trim());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Помилка додавання товару: " + e.getMessage());
            return false;
        }
    }

    // ── UPDATE ─────────────────────────────────────────────────

    /** Оновлює всі поля товару за id. Повертає true якщо успішно. */
    public boolean update(int id, String name, double price,
                          int quantity, String category, String imagePath) {
        String sql = """
            UPDATE products
            SET name=?, price=?, quantity=?, category=?, imagePath=?
            WHERE id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setDouble(2, price);
            ps.setInt(3, quantity);
            ps.setString(4, category.trim());
            ps.setString(5, imagePath.trim());
            ps.setInt(6, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Помилка оновлення товару: " + e.getMessage());
            return false;
        }
    }

    /** Оновлює тільки кількість товару. */
    public boolean updateQuantity(int id, int quantity) {
        String sql = "UPDATE products SET quantity=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Помилка оновлення кількості: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ─────────────────────────────────────────────────

    /** Видаляє товар за id. Повертає true якщо успішно. */
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Помилка видалення товару: " + e.getMessage());
            return false;
        }
    }

    // ── MAPPER ─────────────────────────────────────────────────

    private Product map(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getInt("quantity"),
            rs.getString("category"),
            rs.getString("imagePath")
        );
    }
}