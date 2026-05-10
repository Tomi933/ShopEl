package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Підключення до SQLite бази даних.
 * Створює файл shop.db і таблицю products якщо їх ще немає.
 */
public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:shop.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        connect();
        initTable();
    }

    /** Singleton — одне підключення на весь застосунок. */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("БД підключена: " + URL);
        } catch (SQLException e) {
            System.err.println("Помилка підключення до БД: " + e.getMessage());
        }
    }

    private void initTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS products (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                name      TEXT    NOT NULL,
                price     REAL    NOT NULL,
                quantity  INTEGER NOT NULL,
                category  TEXT    NOT NULL,
                imagePath TEXT    NOT NULL DEFAULT 'images/1.jpg'
            );
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            seedIfEmpty();
        } catch (SQLException e) {
            System.err.println("Помилка створення таблиці: " + e.getMessage());
        }
    }

    /** Заповнює таблицю початковими даними якщо вона порожня. */
    private void seedIfEmpty() throws SQLException {
        var rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM products");
        if (rs.getInt(1) > 0) return;

        String[] inserts = {
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Ноутбук ASUS',25999.99,10,'Електроніка','images/1.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Смартфон Samsung',14999.00,25,'Електроніка','images/2.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Навушники Sony',3499.50,30,'Електроніка','images/3.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Клавіатура Logitech',1899.00,20,'Периферія','images/4.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Мишка Razer',1299.00,35,'Периферія','images/5.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Монітор LG 27',8499.00,8,'Периферія','images/6.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Футболка базова',399.00,50,'Одяг','images/7.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Джинси Levis',1799.00,20,'Одяг','images/8.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Кросівки Nike',2999.00,15,'Взуття','images/9.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Рюкзак Osprey',2499.00,12,'Аксесуари','images/10.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Кава Lavazza 1кг',459.00,40,'Продукти','images/11.jpg')",
            "INSERT INTO products(name,price,quantity,category,imagePath) VALUES('Шоколад Lindt',129.00,60,'Продукти','images/12.jpg')"
        };

        Statement stmt = connection.createStatement();
        for (String sql : inserts) stmt.execute(sql);
        System.out.println("Початкові дані завантажено.");
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.err.println("Помилка закриття БД: " + e.getMessage());
        }
    }
}