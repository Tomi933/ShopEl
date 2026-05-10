package UI.admin;

import UI.AppColors;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Діалог входу в адмін-панель із паролем.
 */
public class AdminLoginDialog extends JDialog {

    private static final String PASSWORD = "admin123"; // змінити на свій

    private final AppColors colors;
    private boolean accepted = false;

    private JPasswordField passwordField;
    private JLabel errorLabel;

    public AdminLoginDialog(JFrame parent, AppColors colors) {
        super(parent, "Вхід в адмін-панель", true); // true = modal
        this.colors = colors;
        buildUI();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void buildUI() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(colors.PANEL_BG);
        panel.setBorder(new EmptyBorder(28, 32, 24, 32));

        // Заголовок
        JLabel title = new JLabel("Адмін-панель");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(colors.TEXT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(0, 0, 6, 0));

        JLabel sub = new JLabel("Введіть пароль для доступу");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(colors.TEXT_DIM);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        sub.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(colors.PANEL_BG);
        top.add(title, BorderLayout.NORTH);
        top.add(sub,   BorderLayout.SOUTH);

        // Поле пароля
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBackground(colors.BG);
        passwordField.setForeground(colors.TEXT);
        passwordField.setCaretColor(colors.ACCENT);
        passwordField.setPreferredSize(new Dimension(260, 40));
        passwordField.setBorder(new CompoundBorder(
            new LineBorder(colors.BORDER_COL, 1, true),
            new EmptyBorder(4, 12, 4, 12)
        ));
        // Enter — підтверджує
        passwordField.addActionListener(e -> tryLogin());

        // Помилка
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(180, 50, 50));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setBorder(new EmptyBorder(6, 0, 0, 0));

        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(colors.PANEL_BG);
        fieldPanel.add(passwordField, BorderLayout.CENTER);
        fieldPanel.add(errorLabel,    BorderLayout.SOUTH);

        // Кнопки
        JButton loginBtn = styledButton("Увійти",    colors.ACCENT);
        JButton cancelBtn = styledButton("Скасувати", new Color(100, 110, 140));

        loginBtn.addActionListener(e -> tryLogin());
        cancelBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(colors.PANEL_BG);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        btnPanel.add(loginBtn);
        btnPanel.add(cancelBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(fieldPanel, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    private void tryLogin() {
        String input = new String(passwordField.getPassword());
        if (input.equals(PASSWORD)) {
            accepted = true;
            dispose();
        } else {
            errorLabel.setText("Невірний пароль. Спробуйте ще раз.");
            passwordField.setText("");
            passwordField.requestFocus();
            // Потряхування вікна
            shakeWindow();
        }
    }

    /** Повертає true якщо пароль введено правильно. */
    public boolean isAccepted() {
        return accepted;
    }

    private void shakeWindow() {
        Point origin = getLocation();
        Timer t = new Timer(30, null);
        int[] step = {0};
        int[] offsets = {-8, 8, -6, 6, -4, 4, -2, 2, 0};
        t.addActionListener(e -> {
            if (step[0] < offsets.length) {
                setLocation(origin.x + offsets[step[0]], origin.y);
                step[0]++;
            } else {
                setLocation(origin);
                t.stop();
            }
        });
        t.start();
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }
}