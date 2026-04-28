package UI;

import core.Store;
import main.Dispatcher;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Верхня панель навігації: логотип, пошук, категорії, кнопки, бейдж кошика.
 */
public class HeaderPanel {

    private final AppColors colors;
    private final Store store;
    private final Dispatcher dispatcher;
    private final CardLayout cardLayout;
    private final JPanel cardContainer;

    private JTextField searchField;
    private JComboBox<String> categoryBox;
    private JLabel cartBadge;

    public HeaderPanel(AppColors colors, Store store, Dispatcher dispatcher, CardLayout cardLayout, JPanel cardContainer) {
        this.colors = colors;
        this.store = store;
        this.dispatcher = dispatcher;
        this.cardLayout = cardLayout;
        this.cardContainer = cardContainer;
    }

    public JPanel build() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(colors.PANEL_BG);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, colors.BORDER_COL),
            new EmptyBorder(12, 24, 12, 24)
        ));
        header.add(buildLogo(), BorderLayout.WEST);
        header.add(buildSearchBar(), BorderLayout.CENTER);
        header.add(buildNavButtons(), BorderLayout.EAST);
        return header;
    }

    private JLabel buildLogo() {
        JLabel logo = new JLabel("[Магазин] " + store.getName());
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(colors.ACCENT);
        return logo;
    }

    private JPanel buildSearchBar() {
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 36));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBackground(colors.BG);
        searchField.setForeground(colors.TEXT);
        searchField.setCaretColor(colors.ACCENT);
        searchField.setBorder(new CompoundBorder(
            new LineBorder(colors.BORDER_COL, 1, true),
            new EmptyBorder(4, 12, 4, 12)
        ));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { dispatcher.dispatch("filter-changed"); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { dispatcher.dispatch("filter-changed"); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { dispatcher.dispatch("filter-changed"); }
        });

        categoryBox = new JComboBox<>(store.getCategories().toArray(new String[0]));
        categoryBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryBox.setBackground(colors.PANEL_BG);
        categoryBox.setForeground(colors.TEXT);
        categoryBox.setPreferredSize(new Dimension(160, 36));
        categoryBox.addActionListener(e -> dispatcher.dispatch("filter-changed"));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(colors.PANEL_BG);
        JLabel icon = new JLabel("[ Пошук ]");
        icon.setForeground(colors.TEXT_DIM);
        panel.add(icon);
        panel.add(searchField);
        panel.add(categoryBox);
        return panel;
    }

    private JPanel buildNavButtons() {
        JButton catalogBtn = navButton("Каталог");
        catalogBtn.addActionListener(e -> cardLayout.show(cardContainer, "catalog"));

        JButton cartBtn = navButton("Кошик");
        cartBtn.addActionListener(e -> cardLayout.show(cardContainer, "cart"));

        cartBadge = new JLabel("0");
        cartBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        cartBadge.setForeground(Color.WHITE);
        cartBadge.setOpaque(true);
        cartBadge.setBackground(colors.ACCENT2);
        cartBadge.setBorder(new EmptyBorder(1, 5, 1, 5));
        cartBadge.setVisible(false);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        nav.setBackground(colors.PANEL_BG);
        nav.add(catalogBtn);
        nav.add(cartBtn);
        nav.add(cartBadge);
        return nav;
    }

    private JButton navButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(colors.PANEL_BG);
        btn.setForeground(colors.TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(colors.ACCENT); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(colors.TEXT); }
        });
        return btn;
    }

    // ── Getters для StoreApp ────────────────────────────────────
    public String getSearchText() { return searchField.getText(); }
    public String getSelectedCategory() { return (String) categoryBox.getSelectedItem(); }
    public void   setCategoryItem(String cat) { categoryBox.setSelectedItem(cat); }
    public void   refreshBadge(int count) {
        cartBadge.setText(String.valueOf(count));
        cartBadge.setVisible(count > 0);
    }
}