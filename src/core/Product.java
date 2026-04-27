package core;
public class Product {
    private String name;
    private double price;
    private int quantity;
    private String category;
    private String imagePath;
    
    public Product(String name, double price, int quantity, String category, String imagePath) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getCategory() { return category; }
    public String getImagePath() { return imagePath; }
    
    public void setQuantity(int quantity) { this.quantity = quantity; }
    

    @Override
    public String toString() {
        return name + " - " + String.format("%.2f", price) + " грн (" + quantity + " шт.)";
    }

	
}