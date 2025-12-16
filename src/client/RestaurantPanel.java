package client;

import common.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RestaurantPanel extends JPanel {
    
    private List<Table> tables;
    private boolean isEditMode = false; 
    private Table selectedTable = null; 

    private int lastX, lastY;

    public RestaurantPanel() {
        this.tables = new ArrayList<>();
        setBackground(new Color(245, 245, 220)); 
        
        //預設的桌子
        tables.add(new Table("T1", 50, 50, 80, 80));
        tables.add(new Table("T2", 160, 50, 120, 80));
        tables.add(new Table("T3", 310, 50, 120, 100));
        tables.add(new Table("T4", 460, 50, 120, 100));
        tables.add(new Table("T5", 50, 160, 80, 80));
        tables.add(new Table("T6", 50, 270, 80, 80));

        //核心互動:滑鼠監聽
        MouseAdapter ma = new MouseAdapter() {
        	
            @Override
            public void mousePressed(MouseEvent e) {
            	//點擊的桌子
                selectedTable = findTableAt(e.getX(), e.getY());

                if (selectedTable != null) {
                    lastX = e.getX();
                    lastY = e.getY();
                    
                    //若是編輯模式，則支援右鍵刪除
                    if (isEditMode) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            int confirm = JOptionPane.showConfirmDialog(RestaurantPanel.this,
                                    "確定刪除 " + selectedTable.getId() + " 嗎?", "刪除", JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                tables.remove(selectedTable);
                                selectedTable = null;
                            }
                        }
                    } else {
                    	//若是服務模式，則開起點餐視窗
                        openOrderDialog();
                    }
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
            	//僅在編輯模式且非右鍵時觸發
                if (isEditMode && selectedTable != null && !SwingUtilities.isRightMouseButton(e)) {
                    
                    int dx = e.getX() - lastX;
                    int dy = e.getY() - lastY;

                    int newX = selectedTable.getX() + dx;
                    int newY = selectedTable.getY() + dy;

                    //碰撞偵測
                    if (!checkCollision(selectedTable, newX, newY)) {
                        selectedTable.setX(newX);
                        selectedTable.setY(newY);
                    }

                    lastX = e.getX();
                    lastY = e.getY();

                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedTable = null; 
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Table t : tables) {
            drawTable(g2d, t);
        }

        //讓編輯模式較能分辨
        if (isEditMode) {
            g2d.setColor(Color.RED);
            g2d.drawString("=== (ADMIN) ===", 10, 20);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    private void drawTable(Graphics2D g2, Table t) {
        g2.setColor(t.isOccupied() ? new Color(255, 100, 100) : new Color(144, 238, 144));
        g2.fillRoundRect(t.getX(), t.getY(), t.getWidth(), t.getHeight(), 20, 20);

        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(t.getX(), t.getY(), t.getWidth(), t.getHeight(), 20, 20);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics metrics = g2.getFontMetrics();
        int tx = t.getX() + (t.getWidth() - metrics.stringWidth(t.getId())) / 2;
        int ty = t.getY() + ((t.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.drawString(t.getId(), tx, ty);
    }

    private Table findTableAt(int x, int y) {
        for (int i = tables.size() - 1; i >= 0; i--) {
            Table t = tables.get(i);
            if (x >= t.getX() && x <= t.getX() + t.getWidth() &&
                y >= t.getY() && y <= t.getY() + t.getHeight()) {
                return t;
            }
        }
        return null;
    }

    //碰撞偵測實作方法
    private boolean checkCollision(Table current, int newX, int newY) {
        Rectangle newRect = new Rectangle(newX, newY, current.getWidth(), current.getHeight());

        for (Table t : tables) {
            if (t == current) continue;//排除自己

            Rectangle otherRect = new Rectangle(t.getX(), t.getY(), t.getWidth(), t.getHeight());
            // 利用 Java 的 Rectangle 類別判斷是否重疊
            if (newRect.intersects(otherRect)) {
                return true; //碰撞
            }
        }
        return false; 
    }

    private void openOrderDialog() {
        System.out.println("開啟點餐視窗: " + selectedTable.getId());
        
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            JFrame parent = (JFrame) window;
            new OrderDialog(parent, selectedTable).setVisible(true);
        } else {
            System.err.println("錯誤：找不到父視窗 JFrame");
        }
    }

    //新增桌子與自動尋位功能實作
    public void addTable(String tableId, int width, int height) {
    	//先檢查重複桌號
        for (Table t : tables) {
            if (t.getId().equals(tableId)) {
                JOptionPane.showMessageDialog(this, "錯誤：桌號 " + tableId + " 已存在！");
                return;
            }
        }

        int gap = 20;

        //尋找不重疊的位置
        Point freePos = findFreePosition(width, height, gap);

        if (freePos != null) {
            Table newTable = new Table(tableId, freePos.x, freePos.y, width, height);
            tables.add(newTable);
            repaint();
            System.out.println("已新增桌子：" + tableId + " (" + width + "x" + height + ")");
        } else {
            JOptionPane.showMessageDialog(this, "畫布空間已滿，無法新增桌子！");
        }
    }

    //尋找不重疊位置實作
    private Point findFreePosition(int w, int h, int gap) {
        int panelW = getWidth() > 0 ? getWidth() : 800;
        int panelH = getHeight() > 0 ? getHeight() : 600;

        int startX = 50;
        int startY = 50;

        //// 雙層迴圈掃描畫布
        for (int y = startY; y < panelH - h; y += (h + gap)) {
            for (int x = startX; x < panelW - w; x += (w + gap)) {
                
                if (!isRegionOccupied(x, y, w, h)) {
                    return new Point(x, y); //找到可用空間
                }
            }
        }
        return null; // 找遍全螢幕都沒位置
    }
    
    //區域佔用檢查方法
    private boolean isRegionOccupied(int x, int y, int w, int h) {
        Rectangle newRect = new Rectangle(x, y, w, h);

        for (Table t : tables) {
            Rectangle existingRect = new Rectangle(t.getX(), t.getY(), t.getWidth(), t.getHeight());
            if (newRect.intersects(existingRect)) {
                return true; 
            }
        }
        return false; 
    }
    
    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        repaint();
    }
}