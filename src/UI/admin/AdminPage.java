package UI.admin;

import UI.AppColors;
import core.Product;
import core.Store;
import db.ProductDAO;
import main.Dispatcher;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Адмін-панель: перегляд, додавання, редагування, видалення товарів.
 */
public class AdminPage {

    private final AppColors colors;
    private final Store store;
    private final ProductDAO dao;
    private final Dispatcher dispatcher;

    private DefaultTableModel tableModel;
    private JTable table;
    private List<Product> currentProducts;

    // Поля форми
    private JTextField fieldName;
    private JTextField fieldPrice;
    private JTextField fieldQuantity;
    private JTextField fieldCategory;
    private JTextField fieldImage;
    private JLabel formTitle;
    private JButton saveBtn;

    private int editingId = -1; // -1 = режим додавання

    public AdminPage(AppColors colors, Store store, Dispatcher dispatcher) {
        this.colors = colors;
        this.store = store;
        this.dao = store.getDAO();
        this.dispatcher = dispatcher;
    }

    public JPanel build() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(colors.BG);
        page.setBorder(new EmptyBorder(24, 28, 24, 28));

        page.add(buildTitle(), BorderLayout.NORTH);
        page.add(buildCenter(), BorderLayout.CENTER);
        return page;
    }

    // ── Заголовок ──────────────────────────────────────────────

    private JPanel buildTitle() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(colors.BG);
        p.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Керування товарами");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(colors.TEXT);

        JButton refreshBtn = styledButton("Оновити", colors.ACCENT);
        refreshBtn.addActionListener(e -> refreshTable());

        p.add(title, BorderLayout.WEST);
        p.add(refreshBtn, BorderLayout.EAST);
        return p;
    }

    // ── Центр: таблиця зліва + форма справа ───────────────────

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildTablePanel(), buildFormPanel());
        split.setDividerLocation(600);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(colors.BG);
        return split;
    }

    // ── Таблиця ────────────────────────────────────────────────

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(colors.BG);
        panel.setBorder(new EmptyBorder(0, 0, 0, 12));

        String[] cols = {"ID", "Назва", "Ціна", "К-сть", "Категорія", "Зображення"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setBackground(colors.CARD_BG);
        table.setForeground(colors.TEXT);
        table.setGridColor(colors.BORDER_COL);
        table.setSelectionBackground(new Color(220, 228, 255));
        table.setSelectionForeground(colors.TEXT);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(colors.BG);
        table.getTableHeader().setForeground(colors.TEXT_DIM);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setIntercellSpacing(new Dimension(0, 1));

        // Ширини колонок
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Клік по рядку — завантажує дані у форму для редагування
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadRowIntoForm();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(colors.BORDER_COL, 1, true));
        scroll.getViewport().setBackground(colors.CARD_BG);

        // Кнопка видалення
        JButton deleteBtn = styledButton("Видалити вибраний товар", new Color(180, 50, 50));
        deleteBtn.addActionListener(e -> deleteSelected());

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(deleteBtn, BorderLayout.SOUTH);

        refreshTable();
        return panel;
    }

    // ── Форма ──────────────────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(colors.PANEL_BG);
        panel.setBorder(new CompoundBorder(
            new LineBorder(colors.BORDER_COL, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        formTitle = new JLabel("Додати новий товар");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(colors.TEXT);
        formTitle.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(colors.PANEL_BG);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);
        gc.weightx = 1.0;

        fieldName = formField();
        fieldPrice = formField();
        fieldQuantity = formField();
        fieldCategory = formField();
        fieldImage = formField();
        fieldImage.setText("images/1.jpg");

        addFormRow(fields, gc, 0, "Назва товару", fieldName);
        addFormRow(fields, gc, 1, "Ціна (грн)", fieldPrice);
        addFormRow(fields, gc, 2, "Кількість", fieldQuantity);
        addFormRow(fields, gc, 3, "Категорія", fieldCategory);
        addFormRow(fields, gc, 4, "Шлях до фото", fieldImage);

        // Кнопки
        saveBtn = styledButton("Зберегти товар", colors.SUCCESS);
        saveBtn.addActionListener(e -> saveProduct());

        JButton clearBtn = styledButton("Очистити форму", new Color(100, 110, 140));
        clearBtn.addActionListener(e -> clearForm());

        JPanel btns = new JPanel(new GridLayout(1, 2, 8, 0));
        btns.setBackground(colors.PANEL_BG);
        btns.setBorder(new EmptyBorder(16, 0, 0, 0));
        btns.add(saveBtn);
        btns.add(clearBtn);

        panel.add(formTitle, BorderLayout.NORTH);
        panel.add(fields, BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    // ── Логіка ─────────────────────────────────────────────────

    private void refreshTable() {
        tableModel.setRowCount(0);
        currentProducts = dao.getAll();
        for (Product p : currentProducts) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                String.format("%.2f", p.getPrice()),
                p.getQuantity(),
                p.getCategory(),
                p.getImagePath()
            });
        }
    }

    private void loadRowIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Product p = currentProducts.get(row);
        editingId = p.getId();
        formTitle.setText("Редагувати товар  [ID: " + editingId + "]");
        saveBtn.setText("Оновити товар");
        fieldName.setText(p.getName());
        fieldPrice.setText(String.valueOf(p.getPrice()));
        fieldQuantity.setText(String.valueOf(p.getQuantity()));
        fieldCategory.setText(p.getCategory());
        fieldImage.setText(p.getImagePath());
    }

    private void saveProduct() {
        String name = fieldName.getText().trim();
        String priceStr = fieldPrice.getText().trim();
        String qtyStr = fieldQuantity.getText().trim();
        String category = fieldCategory.getText().trim();
        String imagePath = fieldImage.getText().trim();

        // Валідація
        if (name.isEmpty() || category.isEmpty()) {
            showError("Назва та категорія обов'язкові!");
            return;
        }
        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceStr.replace(",", "."));
            quantity = Integer.parseInt(qtyStr);
            if (price <= 0 || quantity < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Ціна і кількість мають бути числами більше 0!");
            return;
        }

        boolean ok;
        if (editingId == -1) {
            ok = dao.add(name, price, quantity, category, imagePath);
        } else {
            ok = dao.update(editingId, name, price, quantity, category, imagePath);
        }

        if (ok) {
            showSuccess(editingId == -1 ? "Товар додано!" : "Товар оновлено!");
            dispatcher.dispatch("products-changed");
            clearForm();
            refreshTable();
        } else {
            showError("Помилка збереження. Перевір консоль.");
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Оберіть товар для видалення!");
            return;
        }
        Product p = currentProducts.get(row);
        int confirm = JOptionPane.showConfirmDialog(null,
            "Видалити товар \"" + p.getName() + "\"?",
            "Підтвердження", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(p.getId())) {
                showSuccess("Товар видалено!");
                dispatcher.dispatch("products-changed");
                clearForm();
                refreshTable();
            }
        }
    }

    private void clearForm() {
        editingId = -1;
        formTitle.setText("Додати новий товар");
        saveBtn.setText("Зберегти товар");
        fieldName.setText("");
        fieldPrice.setText("");
        fieldQuantity.setText("");
        fieldCategory.setText("");
        fieldImage.setText("images/1.jpg");
        table.clearSelection();
    }

    // ── Helpers ────────────────────────────────────────────────

    private void addFormRow(JPanel panel, GridBagConstraints gc, int row, String label, JTextField field) {
        gc.gridy = row * 2;
        gc.gridx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(colors.TEXT_DIM);
        panel.add(lbl, gc);

        gc.gridy = row * 2 + 1;
        panel.add(field, gc);
    }

    private JTextField formField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(colors.BG);
        f.setForeground(colors.TEXT);
        f.setCaretColor(colors.ACCENT);
        f.setPreferredSize(new Dimension(0, 36));
        f.setBorder(new CompoundBorder(
            new LineBorder(colors.BORDER_COL, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        return f;
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
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Помилка", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Успіх", JOptionPane.INFORMATION_MESSAGE);
    }
}