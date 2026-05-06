package UI.cart;

import UI.AppColors;
import core.Cart;
import core.CartItem;
import core.Product;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;

/**
 * Таблиця кошика: модель, колонки, рендер фото, оновлення даних.
 */
public class CartTable {

    private final AppColors colors;
    private final Cart cart;

    private DefaultTableModel model;
    private JTable table;

    public CartTable(AppColors colors, Cart cart) {
        this.colors = colors;
        this.cart = cart;
    }

    public JScrollPane build() {
        String[] cols = {"Фото", "Товар", "Категорія", "Ціна", "К-сть", "Сума"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int c) { return c == 0 ? ImageIcon.class : Object.class; }
        };

        table = new JTable(model);
        styleTable();
        table.setRowHeight(70);

        int[] widths = {80, 200, 120, 110, 70, 120};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(0).setCellRenderer(new CartTableRenderer(colors));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(colors.BORDER_COL, 1, true));
        scroll.getViewport().setBackground(colors.CARD_BG);
        return scroll;
    }

    /** Перечитує дані кошика і оновлює таблицю. */
    public void refresh() {
        model.setRowCount(0);
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            model.addRow(new Object[]{
                loadThumb(p),
                p.getName(),
                p.getCategory(),
                String.format("%.2f грн", p.getPrice()),
                item.getQuantity(),
                String.format("%.2f грн", item.getSubtotal())
            });
        }
    }

    /** Повертає індекс вибраного рядка (-1 якщо нічого не вибрано). */
    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    private ImageIcon loadThumb(Product p) {
        try {
            URL url = getClass().getResource("/" + p.getImagePath());
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void styleTable() {
        table.setBackground(colors.CARD_BG);
        table.setForeground(colors.TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setGridColor(colors.BORDER_COL);
        table.setSelectionBackground(new Color(220, 228, 255));
        table.setSelectionForeground(colors.TEXT);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(colors.BG);
        table.getTableHeader().setForeground(colors.TEXT_DIM);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, colors.BORDER_COL));
        table.setIntercellSpacing(new Dimension(0, 1));
    }
}
