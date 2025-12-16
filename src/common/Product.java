package common;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private int price;
    private String category; 

    public Product(String id, String name, int price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    // Getter
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getCategory() { return category; }
    
    @Override
    public String toString() {
        return name + " ($" + price + ")";
    }
}