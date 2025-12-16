package common;

import java.io.Serializable;

public class Table implements Serializable {
    private static final long serialVersionUID = 1L; 

    private String id;   
    private int x, y;      
    private int width, height; 
    private boolean isOccupied; 

    public Table(String id, int x, int y, int width, int height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isOccupied = false;
    }

    public String getId() { return id; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }
    
    @Override
    public String toString() {
        return "Table{" + "id='" + id + '\'' + ", status=" + (isOccupied ? "Busy" : "Free") + '}';
    }
}