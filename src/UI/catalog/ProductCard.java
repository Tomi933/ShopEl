package UI.catalog;

import UI.AppColors;
import core.Cart;
import core.Product;
import main.Dispatcher;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Картка одного товару: фото, назва, категорія, ціна, спінер, кнопка.
 */
public class ProductCard {

    private final AppColors colors;
    private final Cart cart;
    private final Dispatcher dispatcher;
    private final Consumer<String> onToast;

    public ProductCard(AppColors colors, Cart cart, Dispatcher dispatcher, Consumer<String> onToast) {
        this.colors = colors;
        this.cart = cart;
        this.dispatcher = dispatcher;
        this.onToast = onToast;
    }

    public JPanel build(Product p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(colors.CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(colors.BORDER_COL, 1, true),
            new EmptyBorder(0, 0, 12, 0)
        ));
        card.setPreferredSize(new Dimension(220, 300));

        JPanel info = buildInfo(p);
        JPanel btnRow = buildBtnRow(p, info);

        info.add(btnRow);
        card.add(buildImage(p), BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);

        addHoverEffect(card, info, btnRow);
        return card;
    }

    // ── private builders ───────────────────────────────────────

    private JLabel buildImage(Product p) {
        JLabel img = new JLabel();
        img.setHorizontalAlignment(SwingConstants.CENTER);
        img.setPreferredSize(new Dimension(220, 160));
        img.setBackground(new Color(245, 247, 255));
        img.setOpaque(true);
        try {
            URL url = getClass().getResource("/" + p.getImagePath());
            if (url != null) {
                Image scaled = new ImageIcon(url).getImage().getScaledInstance(160, 150, Image.SCALE_SMOOTH);
                img.setIcon(new ImageIcon(scaled));
            } else {
                img.setText("[ фото ]");
                img.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        } catch (Exception ex) {
            img.setText("[ фото ]");
            img.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        return img;
    }

    private JPanel buildInfo(Product p) {
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(colors.CARD_BG);
        info.setBorder(new EmptyBorder(10, 14, 0, 14));

        JLabel name = new JLabel("<html><b>" + p.getName() + "</b></html>");
        name.setFont(new Font("Segoe UI", Font.BOLD, 13));
        name.setForeground(colors.TEXT);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cat = new JLabel(p.getCategory() + "  •  " + p.getQuantity() + " шт.");
        cat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cat.setForeground(colors.TEXT_DIM);
        cat.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel price = new JLabel(String.format("%.2f грн", p.getPrice()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 16));
        price.setForeground(colors.ACCENT);
        price.setAlignmentX(Component.LEFT_ALIGNMENT);
        price.setBorder(new EmptyBorder(6, 0, 8, 0));

        info.add(name);
        info.add(Box.createVerticalStrut(3));
        info.add(cat);
        info.add(price);
        return info;
    }

    private JPanel buildBtnRow(Product p, JPanel info) {
        SpinnerNumberModel spinModel = new SpinnerNumberModel(1, 1, 99, 1);
        JSpinner spinner = new JSpinner(spinModel);
        spinner.setMaximumSize(new Dimension(65, 28));
        spinner.setPreferredSize(new Dimension(65, 28));
        styleSpinner(spinner);

        JButton addBtn = new JButton("До кошика");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.setBackground(colors.ACCENT);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        addBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { addBtn.setBackground(colors.ACCENT.darker()); }
            public void mouseExited(MouseEvent e) { addBtn.setBackground(colors.ACCENT); }
        });
        addBtn.addActionListener(e -> {
            cart.addProduct(p, (int) spinner.getValue());
            dispatcher.dispatch("cart-changed");
            onToast.accept(p.getName() + " додано до кошика");
        });

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setBackground(colors.CARD_BG);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(spinner);
        row.add(addBtn);
        return row;
    }

    private void addHoverEffect(JPanel card, JPanel info, JPanel btnRow) {
        MouseAdapter hover = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(colors.CARD_HOVER);
                info.setBackground(colors.CARD_HOVER);
                btnRow.setBackground(colors.CARD_HOVER);
                card.setBorder(new CompoundBorder(
                    new LineBorder(colors.ACCENT, 1, true),
                    new EmptyBorder(0, 0, 12, 0)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(colors.CARD_BG);
                info.setBackground(colors.CARD_BG);
                btnRow.setBackground(colors.CARD_BG);
                card.setBorder(new CompoundBorder(
                    new LineBorder(colors.BORDER_COL, 1, true),
                    new EmptyBorder(0, 0, 12, 0)
                ));
            }
        };
        card.addMouseListener(hover);
        info.addMouseListener(hover);
    }

    private void styleSpinner(JSpinner s) {
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JTextField tf = ((JSpinner.DefaultEditor) s.getEditor()).getTextField();
        tf.setBackground(colors.BG);
        tf.setForeground(colors.TEXT);
        tf.setBorder(new EmptyBorder(2, 4, 2, 4));
    }
}