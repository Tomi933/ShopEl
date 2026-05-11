package main;

import UI.AppColors;
import UI.HeaderPanel;
import UI.StatusBar;
import UI.catalog.CatalogPage;
import UI.cart.CartPage;
import UI.admin.AdminPage;
import core.Cart;
import core.CartItem;
import core.Product;
import core.Store;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Головний оркестратор застосунку.
 * Збирає всі панелі, підписується на події, містить логіку checkout.
 */
public class StoreApp {

    private final AppColors colors = new AppColors();
    private final Store store = new Store("TechShop UA");
    private final Cart cart = new Cart();
    private final Dispatcher dispatcher = new Dispatcher();

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardContainer;

    private HeaderPanel header;
    private CatalogPage catalog;
    private CartPage cartPage;
    private AdminPage adminPage;
    private StatusBar statusBar;

    // ══════════════════════════════════════════════════════════
    public void launch() {
        frame = new JFrame(store.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 760);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(colors.BG);

        // ── Ініціалізація компонентів ──
        statusBar = new StatusBar(colors, store.getName());
        header = new HeaderPanel(colors, store, dispatcher, getCardLayout(), getCardContainer());

        catalog = new CatalogPage(colors, store, cart, dispatcher, frame,
                msg -> statusBar.showToast(msg));

        cartPage = new CartPage(colors, cart, dispatcher, frame,
                getCardLayout(), getCardContainer(), this::checkout);

        // ── Збираємо CardLayout ──
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(colors.BG);
        cardContainer.add(catalog.build(),  "catalog");
        cardContainer.add(cartPage.build(), "cart");

        // ── Оновлюємо header і cartPage з реальними посиланнями ──
        header = new HeaderPanel(colors, store, dispatcher, cardLayout, cardContainer);
        cartPage = new CartPage(colors, cart, dispatcher, frame, cardLayout, cardContainer, this::checkout);
        catalog = new CatalogPage(colors, store, cart, dispatcher, frame, msg -> statusBar.showToast(msg));
        adminPage = new AdminPage(colors, store, dispatcher);

        cardContainer.removeAll();
        cardContainer.add(catalog.build(), "catalog");
        cardContainer.add(cartPage.build(), "cart");
        cardContainer.add(adminPage.build(), "admin");

        frame.add(header.build(), BorderLayout.NORTH);
        frame.add(cardContainer, BorderLayout.CENTER);
        frame.add(statusBar.build(), BorderLayout.SOUTH);

        // ── Підписки на події ──
        dispatcher.on("cart-changed", this::onCartChanged);
        dispatcher.on("filter-changed", this::applyFilter);
        dispatcher.on("products-changed", this::onProductsChanged);

        // ── Адаптивна сітка при зміні розміру ──
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { applyFilter(); }
        });

        catalog.loadGrid(store.getProducts());
        frame.setVisible(true);
    }

    // ── Обробники подій ────────────────────────────────────────

    private void onCartChanged() {
        cartPage.refresh();
        header.refreshBadge(cart.getItemCount());
    }

    /** Після змін в БД — перезавантажуємо каталог з актуальними даними. */
    private void onProductsChanged() {
        applyFilter();
    }

    private void applyFilter() {
        String cat = header.getSelectedCategory();
        String search = header.getSearchText().toLowerCase().trim();
        List<Product> filtered = store.getByCategory(cat);
        filtered.removeIf(p -> !search.isEmpty() && !p.getName().toLowerCase().contains(search));
        catalog.loadGrid(filtered);
    }

    // ── Checkout ───────────────────────────────────────────────

    private void checkout() {
        if (cart.getItemCount() == 0) {
            JOptionPane.showMessageDialog(frame, "Ваш кошик порожній!", "Помилка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════\n");
        sb.append("           ЗАМОВЛЕННЯ\n");
        sb.append("═══════════════════════════════════\n");
        for (CartItem item : cart.getItems()) {
            sb.append(String.format("%-26s %8.2f грн%n",
                item.getProduct().getName() + " x" + item.getQuantity(),
                item.getSubtotal()));
        }
        sb.append("───────────────────────────────────\n");
        sb.append(String.format("РАЗОМ:%30.2f грн%n", cart.getTotal()));
        sb.append("═══════════════════════════════════\n");
        sb.append("Дякуємо за покупку!");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(colors.CARD_BG);
        area.setForeground(colors.TEXT);
        area.setBorder(new javax.swing.border.EmptyBorder(12, 12, 12, 12));

        int res = JOptionPane.showConfirmDialog(frame, new JScrollPane(area),
                "Підтвердження замовлення",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            // ── Оновлюємо кількість в БД ──
            for (CartItem item : cart.getItems()) {
                Product p = item.getProduct();
                int newQty = p.getQuantity() - item.getQuantity();
                store.getDAO().updateQuantity(p.getId(), Math.max(0, newQty));
            }

            cart.clear();
            dispatcher.dispatch("cart-changed");
            dispatcher.dispatch("products-changed"); // оновлює каталог (картки/кольори)
            cardLayout.show(cardContainer, "catalog");
            statusBar.showToast("Замовлення успішно оформлено!");
        }
    }

    // ── Тимчасові заглушки для ініціалізації ──────────────────
    // (потрібні бо header/cartPage отримують cardLayout до його створення)
    private CardLayout getCardLayout()    { return cardLayout != null ? cardLayout : new CardLayout(); }
    private JPanel getCardContainer() { return cardContainer != null ? cardContainer : new JPanel(); }
}