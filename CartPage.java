package UI.cart;

import UI.AppColors;
import core.Cart;
import main.Dispatcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Оркестратор сторінки кошика.
 * Збирає CartTable + CartBottomBar, делегує їм всю логіку.
 */
public class CartPage {

    private final AppColors colors;
    private final Cart cart;
    private CartTable cartTable;
    private CartBottomBar bottomBar;

    public CartPage(AppColors colors, Cart cart, Dispatcher dispatcher, JFrame frame, CardLayout cardLayout, JPanel cardContainer, Runnable onCheckout) {
        this.colors = colors;
        this.cart = cart;
        cartTable = new CartTable(colors, cart);
        bottomBar = new CartBottomBar(colors, cart, dispatcher, frame, cardLayout, cardContainer, onCheckout);

        // видалення вибраного рядка
        dispatcher.on("cart-remove-selected", () -> {
            int row = cartTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(frame, "Оберіть рядок!", "Увага", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cart.removeItem(row);
            dispatcher.dispatch("cart-changed");
        });
    }

    public JPanel build() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(colors.BG);
        page.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel title = new JLabel("Ваш кошик");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(colors.TEXT);
        title.setBorder(new EmptyBorder(0, 0, 16, 0));

        page.add(title, BorderLayout.NORTH);
        page.add(cartTable.build(), BorderLayout.CENTER);
        page.add(bottomBar.build(), BorderLayout.SOUTH);
        return page;
    }

    /** Викликається з StoreApp при події cart-changed. */
    public void refresh() {
        cartTable.refresh();
        bottomBar.refreshTotal(cart.getTotal());
    }
}