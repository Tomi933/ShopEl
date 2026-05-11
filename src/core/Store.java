package core;

import db.ProductDAO;
import java.util.List;
import java.util.ArrayList;

/**
 * Каталог товарів — тепер читає дані з БД через ProductDAO.
 */
public class Store {
    private final String name;
    private final ProductDAO dao;

    public Store(String name) {
        this.name = name;
        this.dao = new ProductDAO();
    }

    public String getName() { return name; }

    public List<Product> getProducts() {
        return dao.getAll();
    }

    public List<Product> getByCategory(String category) {
        if (category.equals("Всі")) return dao.getAll();
        return dao.getByCategory(category);
    }

    public List<String> getCategories() {
        return dao.getCategories();
    }

    public ProductDAO getDAO() { return dao; }
}