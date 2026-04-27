import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, int qty) {
        for (CartItem item : items) {
            if (item.getProduct().getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + qty);
                return;
            }
        }
        items.add(new CartItem(product, qty));
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotal() {
        double sum = 0;
        for (CartItem item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }

    public int getItemCount() {
        return items.size();
    }
}