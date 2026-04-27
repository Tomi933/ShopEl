package core;
import java.util.ArrayList;
import java.util.List;

public class Store {
    private List<Product> products = new ArrayList<>();
    private String name;

    public Store(String name) {
        this.name = name;
        initProducts();
    }

    private void initProducts() {
        products.add(new Product("Ноутбук ASUS", 25999.99, 10, "Електроніка", "images/1.jpg"));
        products.add(new Product("Смартфон Samsung", 14999.00, 25, "Електроніка", "images/2.jpg"));
        products.add(new Product("Навушники Sony", 3499.50, 30, "Електроніка", "images/3.jpg"));
        products.add(new Product("Клавіатура Logitech", 1899.00, 20, "Периферія", "images/4.jpg"));
        products.add(new Product("Мишка Razer", 1299.00, 35, "Периферія", "images/5.jpg"));
        products.add(new Product("Монітор LG 27\"", 8499.00, 8, "Периферія", "images/6.jpg"));
        products.add(new Product("Футболка базова", 399.00, 50, "Одяг", "images/7.jpg"));
        products.add(new Product("Джинси Levi's", 1799.00, 20, "Одяг", "images/8.jpg"));
        products.add(new Product("Кросівки Nike", 2999.00, 15, "Взуття", "images/9.jpg"));
        products.add(new Product("Рюкзак Osprey", 2499.00, 12, "Аксесуари", "images/10.jpg"));
        products.add(new Product("Кава Lavazza 1кг", 459.00, 40, "Продукти", "images/11.jpg"));
        products.add(new Product("Шоколад Lindt", 129.00, 60, "Продукти", "images/12.jpg"));
    }

    public List<Product> getProducts() { return products; }
    public String getName() { return name; }

    public List<Product> getByCategory(String category) {
        if (category.equals("Всі")) return products;
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.getCategory().equals(category)) result.add(p);
        }
        return result;
    }

    public List<String> getCategories() {
        List<String> cats = new ArrayList<>();
        cats.add("Всі");
        for (Product p : products) {
            if (!cats.contains(p.getCategory())) cats.add(p.getCategory());
        }
        return cats;
    }
}