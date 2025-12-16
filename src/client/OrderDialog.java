package client;

import common.Order;
import common.Product;
import common.Table;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class OrderDialog extends JDialog {

    private Table table;
    private Order currentOrder;
    private DefaultListModel<String> cartListModel; 
    private JLabel lblTotal; 

    public OrderDialog(JFrame parent, Table table) {
        super(parent, "點餐 - 桌號: " + table.getId(), true); 
        this.table = table;
        this.currentOrder = new Order(table.getId()); 

        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        //菜單區
        JPanel menuPanel = new JPanel(new GridLayout(0, 2, 10, 10)); 
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        MenuService menuService = new MenuService();
        List<Product> products = menuService.loadMenu();

        for (Product p : products) {
            JButton btn = new JButton("<html><center>" + p.getName() + "<br>$" + p.getPrice() + "</center></html>");
            btn.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
            
            btn.addActionListener(e -> addToCart(p));
            
            menuPanel.add(btn);
        }
        
        //購物車區
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Cart"));
        cartPanel.setPreferredSize(new Dimension(200, 0));

        cartListModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartListModel);
        cartPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);

        // 下方總金額與送出按鈕
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        lblTotal = new JLabel("Total: $0", SwingConstants.CENTER);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton btnSubmit = new JButton("送出訂單");
        btnSubmit.setBackground(new Color(255, 69, 0)); 
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        
        btnSubmit.addActionListener(e -> submitOrder());

        bottomPanel.add(lblTotal);
        bottomPanel.add(btnSubmit);
        cartPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(new JScrollPane(menuPanel), BorderLayout.CENTER); 
        add(cartPanel, BorderLayout.EAST);
    }

    // 加入購物車邏輯
    private void addToCart(Product p) {
        currentOrder.addItem(p);
        cartListModel.addElement(p.getName() + "  $" + p.getPrice());
        updateTotal();
    }

    // 更新總金額
    private void updateTotal() {
        int total = currentOrder.getItems().stream().mapToInt(Product::getPrice).sum();
        lblTotal.setText("Total: $" + total);
    }

    // 送出訂單邏輯
    private void submitOrder() {
        if (currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "購物車是空的！");
            return;
        }

        // 啟動執行緒傳送資料
        new Thread(() -> {
            try (Socket socket = new Socket("127.0.0.1", 8888);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

                oos.writeObject(currentOrder);
                oos.flush();

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "訂單已傳送給廚房！");
                    dispose(); 
                });

            } catch (java.net.ConnectException ce) {
                System.out.println(">> (Client) 連線嘗試失敗：Server 尚未啟動");
                
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(this, "無法連線到廚房系統，請確認 Server 是否開啟！", "連線錯誤", JOptionPane.ERROR_MESSAGE)
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
        }).start();
    }
}