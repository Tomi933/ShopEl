package UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Рядок статусу внизу вікна: поточне повідомлення + версія.
 */
public class StatusBar {

    private final AppColors colors;
    private final String storeName;
    private JLabel statusLabel;

    public StatusBar(AppColors colors, String storeName) {
        this.colors = colors;
        this.storeName = storeName;
    }

    public JPanel build() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(248, 249, 255));
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, colors.BORDER_COL),
            new EmptyBorder(5, 20, 5, 20)
        ));

        statusLabel = new JLabel("Ласкаво просимо до " + storeName + "!");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(colors.TEXT_DIM);

        JLabel ver = new JLabel("v3.0");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(colors.TEXT_DIM);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(ver, BorderLayout.EAST);
        return bar;
    }

    public void showToast(String msg) {
        statusLabel.setForeground(colors.SUCCESS);
        statusLabel.setText(msg);
        Timer t = new Timer(3000, e -> {
            statusLabel.setForeground(colors.TEXT_DIM);
            statusLabel.setText("Ласкаво просимо до " + storeName + "!");
        });
        t.setRepeats(false);
        t.start();
    }
}