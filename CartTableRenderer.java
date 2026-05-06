package UI.cart;

import UI.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Рендер для першої колонки кошика — відображає мініатюру товару.
 */
public class CartTableRenderer extends DefaultTableCellRenderer {

    private final AppColors colors;

    public CartTableRenderer(AppColors colors) {
        this.colors = colors;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(isSelected ? new Color(220, 228, 255) : colors.CARD_BG);
        if (value instanceof ImageIcon) lbl.setIcon((ImageIcon) value);
        return lbl;
    }
}