package UI.cart;

import UI.AppColors;
import core.Cart;
import main.Dispatcher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Нижня панель кошика: підсумок, кнопки видалення / очищення / оформлення.
 */
public class CartBottomBar {

    private final AppColors colors;
    private final Cart cart;
    private final Dispatcher dispatcher;
    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel cardContainer;
    private final Runnable onCheckout;

    private JLabel totalLabel;

    public CartBottomBar(AppColors colors, Cart cart, Dispatcher dispatcher, JFrame frame, CardLayout cardLayout, JPanel cardContainer, Runnable onCheckout) {
        this.colors = colors;
        this.cart = cart;
        this.dispatcher = dispatcher;
        this.frame = frame;
        this.cardLayout = cardLayout;
        this.cardContainer = cardContainer;
        this.onCheckout = onCheckout;
    }

    public JPanel build() {
        totalLabel = new JLabel("Разом: 0.00 грн");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(colors.SUCCESS);

        JButton removeBtn = styledButton("Видалити", colors.ACCENT2);
        JButton clearBtn = styledButton("Очистити", new Color(180, 50, 50));
        JButton backBtn = styledButton("< Назад до каталогу", new Color(100, 110, 140));
        JButton checkoutBtn = styledButton("Оформити замовлення", colors.SUCCESS);

        removeBtn.addActionListener(e -> dispatcher.dispatch("cart-remove-selected"));

        clearBtn.addActionListener(e -> {
            if (cart.getItemCount() == 0) return;
            if (JOptionPane.showConfirmDialog(frame, "Очистити кошик?", "Підтвердження", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                cart.clear();
                dispatcher.dispatch("cart-changed");
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(cardContainer, "catalog"));
        checkoutBtn.addActionListener(e -> onCheckout.run());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(colors.BG);
        left.add(backBtn);
        left.add(removeBtn);
        left.add(clearBtn);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(colors.BG);
        right.add(totalLabel);
        right.add(Box.createHorizontalStrut(16));
        right.add(checkoutBtn);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(colors.BG);
        bar.setBorder(new EmptyBorder(16, 0, 0, 0));
        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    public void refreshTotal(double total) {
        totalLabel.setText(String.format("Разом: %.2f грн", total));
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }
}
