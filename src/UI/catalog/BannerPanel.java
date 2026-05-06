package UI.catalog;

import UI.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

/**
 * Банер у верхній частині каталогу: фонове зображення + текст + CTA кнопка.
 */
public class BannerPanel {

    private final AppColors colors;
    private final String bannerImagePath;

    public BannerPanel(AppColors colors, String bannerImagePath) {
        this.colors = colors;
        this.bannerImagePath = bannerImagePath;
    }

    public JPanel build() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    URL url = getClass().getResource("/" + bannerImagePath);
                    if (url != null) {
                        Image img = new ImageIcon(url).getImage();
                        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    }
                } catch (Exception ignored) {}
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(20, 30, 80, 160),
                    getWidth(), 0, new Color(80, 40, 120, 140)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        banner.setPreferredSize(new Dimension(0, 160));
        banner.setBackground(new Color(30, 40, 100));
        banner.setBorder(new EmptyBorder(0, 40, 0, 40));

        JLabel headline = new JLabel("Найкращі товари за найкращими цінами");
        headline.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headline.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Безкоштовна доставка від 1000 грн  •  Гарантія якості  •  Повернення 30 днів");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(200, 210, 255));

        JButton shopBtn = new JButton("Переглянути всі товари >");
        shopBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        shopBtn.setBackground(colors.ACCENT2);
        shopBtn.setForeground(Color.WHITE);
        shopBtn.setFocusPainted(false);
        shopBtn.setBorderPainted(false);
        shopBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        shopBtn.setBorder(new EmptyBorder(8, 20, 8, 20));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        text.setBorder(new EmptyBorder(30, 0, 30, 0));
        text.add(headline);
        text.add(Box.createVerticalStrut(8));
        text.add(sub);
        text.add(Box.createVerticalStrut(14));
        text.add(shopBtn);

        banner.add(text, BorderLayout.CENTER);
        return banner;
    }
}