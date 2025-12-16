package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tableId;       
    private List<Product> items;   
    private Date timestamp;       

    public Order(String tableId) {
        this.tableId = tableId;
        this.items = new ArrayList<>();
        this.timestamp = new Date();
    }

    public void addItem(Product p) {
        items.add(p);
    }

    public String getTableId() { return tableId; }
    public List<Product> getItems() { return items; }
    public Date getTimestamp() { return timestamp; }

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("桌號: ").append(tableId).append("\n");
        sb.append("時間: ").append(timestamp).append("\n");
        sb.append("餐點: \n");
        int total = 0;
        for (Product p : items) {
            sb.append(" - ").append(p.getName()).append(" $").append(p.getPrice()).append("\n");
            total += p.getPrice();
        }
        sb.append("總金額: $").append(total);
        return sb.toString();
    }
}