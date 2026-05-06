package UI.catalog;

import UI.AppColors;
import core.Cart;
import core.Product;
import core.Store;
import main.Dispatcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Оркестратор сторінки каталогу.
 * Збирає BannerPanel + LeftPanel + сітку ProductCard.
 */
public class CatalogPage {

    private final AppColors colors;
    private final Store store;
    private final Cart cart;
    private final Dispatcher dispatcher;
    private final JFrame frame;
    private final Consumer<String> onToast;

    private JPanel gridPanel;

    public CatalogPage(AppColors colors, Store store, Cart cart, Dispatcher dispatcher, JFrame frame, Consumer<String> onToast) {
        this.colors = colors;
        this.store = store;
        this.cart = cart;
        this.dispatcher = dispatcher;
        this.frame = frame;
        this.onToast = onToast;
    }

    public JPanel build() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(colors.BG);

        // Банер
        BannerPanel banner = new BannerPanel(colors, "images/banner.png");
        page.add(banner.build(), BorderLayout.NORTH);

        // Ліва панель + сітка
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(colors.BG);

        LeftPanel left = new LeftPanel(colors, store, dispatcher, cat -> {
            dispatcher.on("category-override", () -> {});
            loadGrid(store.getByCategory(cat));
        });
        body.add(left.build(), BorderLayout.WEST);

        gridPanel = new JPanel();
        gridPanel.setBackground(colors.BG);
        gridPanel.setBorder(new EmptyBorder(20, 16, 20, 20));

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(colors.BG);

        body.add(scroll, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    /** Перемальовує сітку товарів (адаптивна кількість колонок). */
    public void loadGrid(List<Product> products) {
        gridPanel.removeAll();

        int cols = Math.max(1, (frame.getWidth() - 230) / 240);
        gridPanel.setLayout(new GridLayout(0, cols, 16, 16));

        ProductCard cardFactory = new ProductCard(colors, cart, dispatcher, onToast);
        for (Product p : products) {
            gridPanel.add(cardFactory.build(p));
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}