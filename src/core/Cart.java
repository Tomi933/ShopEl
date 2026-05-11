package core;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public int addProduct(Product product, int qty) {
        int inStock = product.getQuantity();
 
        int alreadyInCart = 0;
        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                alreadyInCart = item.getQuantity();
                break;
            }
        }
 
        int canAdd = Math.min(qty, inStock - alreadyInCart);
        if (canAdd <= 0) return 0;
 
        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + canAdd);
                return canAdd;
            }
        }
        items.add(new CartItem(product, canAdd));
        return canAdd;
    }
 
    public void addProduct(Product product) {
        addProduct(product, 1);
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