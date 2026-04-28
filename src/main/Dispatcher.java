package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Простий event-bus: підписка на події та їх відправка.
 * Точка входу в застосунок.
 */
public class Dispatcher {

    private final Map<String, List<Runnable>> listeners = new HashMap<>();

    public void on(String event, Runnable handler) {
        listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(handler);
    }

    public void dispatch(String event) {
        List<Runnable> handlers = listeners.get(event);
        if (handlers != null) {
            for (Runnable h : handlers) h.run();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new StoreApp().launch();
        });
    }
}