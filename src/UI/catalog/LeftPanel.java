package UI.catalog;

import UI.AppColors;
import core.Store;
import main.Dispatcher;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Ліва панель каталогу: список категорій + блок акції.
 */
public class LeftPanel {

    private final AppColors colors;
    private final Store store;
    private final Consumer<String> onCategorySelect;

    private JPanel panel;

    public LeftPanel(AppColors colors, Store store, Dispatcher dispatcher, Consumer<String> onCategorySelect) {
        this.colors = colors;
        this.store = store;
        this.onCategorySelect = onCategorySelect;
    }

    public JPanel build() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(colors.PANEL_BG);
        panel.setPreferredSize(new Dimension(190, 0));
        panel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 0, 1, colors.BORDER_COL),
            new EmptyBorder(20, 16, 20, 16)
        ));

        JLabel catTitle = new JLabel("КАТЕГОРІЇ");
        catTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        catTitle.setForeground(colors.TEXT_DIM);
        catTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(catTitle);
        panel.add(Box.createVerticalStrut(10));

        for (String cat : store.getCategories()) {
            JButton btn = categoryButton(cat);
            btn.addActionListener(e -> {
                onCategorySelect.accept(cat);
                highlightButton(btn);
            });
            panel.add(btn);
            panel.add(Box.createVerticalStrut(4));
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(buildPromo());
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void highlightButton(JButton active) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JButton b) {
                b.setBackground(colors.PANEL_BG);
                b.setForeground(colors.TEXT);
            }
        }
        active.setBackground(new Color(235, 240, 255));
        active.setForeground(colors.ACCENT);
    }

    private JPanel buildPromo() {
        JPanel promo = new JPanel();
        promo.setLayout(new BoxLayout(promo, BoxLayout.Y_AXIS));
        promo.setBackground(new Color(255, 245, 235));
        promo.setBorder(new CompoundBorder(
            new LineBorder(new Color(255, 200, 150), 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
        promo.setMaximumSize(new Dimension(180, 100));
        promo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel promoTitle = new JLabel("* Акція тижня");
        promoTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        promoTitle.setForeground(new Color(200, 80, 0));

        JLabel promoText = new JLabel("<html>Знижка 15% на<br>всю електроніку!</html>");
        promoText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        promoText.setForeground(colors.TEXT);

        promo.add(promoTitle);
        promo.add(Box.createVerticalStrut(4));
        promo.add(promoText);
        return promo;
    }

    private JButton categoryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(colors.PANEL_BG);
        btn.setForeground(colors.TEXT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 10, 6, 10));
        btn.setMaximumSize(new Dimension(180, 34));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!btn.getForeground().equals(colors.ACCENT))
                    btn.setBackground(new Color(240, 243, 255));
            }
            public void mouseExited(MouseEvent e) {
                if (!btn.getForeground().equals(colors.ACCENT))
                    btn.setBackground(colors.PANEL_BG);
            }
        });
        return btn;
    }
}