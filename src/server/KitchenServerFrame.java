package server;

import common.Order;
import common.Product;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.LinkedHashMap; 


public class KitchenServerFrame extends JFrame {

    private JPanel pendingPanel;   
    private JPanel completedPanel; 
    private JLabel lblStatus;
    private static final int PORT = 8888;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public KitchenServerFrame() {
        setTitle("廚房 KDS 顯示系統 (Server)");
        setSize(900, 600); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //北邊狀態列
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(50, 50, 50));
        lblStatus = new JLabel("等待前台連線...");
        lblStatus.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        lblStatus.setForeground(Color.RED);
        topPanel.add(lblStatus);
        add(topPanel, BorderLayout.NORTH);

        //中左待處理區
        pendingPanel = new JPanel();
        pendingPanel.setLayout(new BoxLayout(pendingPanel, BoxLayout.Y_AXIS)); 
        JScrollPane scrollPending = new JScrollPane(pendingPanel);
        scrollPending.setBorder(BorderFactory.createTitledBorder(
                null, "待製作訂單 (Pending)", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Microsoft JhengHei", Font.BOLD, 18), Color.RED));

        //中右已完成區
        completedPanel = new JPanel();
        completedPanel.setLayout(new BoxLayout(completedPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollCompleted = new JScrollPane(completedPanel);
        scrollCompleted.setBorder(BorderFactory.createTitledBorder(
                null, "已出餐紀錄 (Completed)", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Microsoft JhengHei", Font.BOLD, 18), new Color(0, 128, 0)));

        //將畫面左右分割
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPending, scrollCompleted);
        splitPane.setDividerLocation(450); 
        splitPane.setResizeWeight(0.5); 
        add(splitPane, BorderLayout.CENTER);

        //南邊控制區
        JPanel bottomPanel = new JPanel();
        JButton btnClearHistory = new JButton("清空已完成紀錄");
        btnClearHistory.addActionListener(e -> {
            completedPanel.removeAll();
            completedPanel.revalidate();
            completedPanel.repaint();
        });
        bottomPanel.add(btnClearHistory);
        add(bottomPanel, BorderLayout.SOUTH);

        //啟動伺服器監聽
        startServer();
    }

    //啟動網路
    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server 啟動，Port: " + PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();//持續等待前台聯線
                    handleClient(clientSocket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    //成功連線後
    private void handleClient(Socket socket) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            Object obj = ois.readObject();

            //判斷前台給的是訂單還是登入
            if (obj instanceof Order) {
            	//是訂單，就加入中左Pending區
                Order order = (Order) obj;
                SwingUtilities.invokeLater(() -> addOrderToPending(order));
            } 
            else if (obj instanceof String) {
            	//是登入訊號，更改北邊狀態列為後臺已連線
                String msg = (String) obj;
                if ("LOGIN".equals(msg)) {
                    SwingUtilities.invokeLater(() -> {
                        lblStatus.setText("前台 POS 已連線");
                        lblStatus.setForeground(Color.GREEN);
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //建立訂單卡片
    private void addOrderToPending(Order order) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(255, 255, 224));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.ORANGE, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        //標題區桌號 + 時間
        String timeStr = sdf.format(order.getTimestamp());
        JLabel lblTitle = new JLabel("桌號: " + order.getTableId() + " (" + timeStr + ")");
        lblTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 20));
        lblTitle.setForeground(new Color(139, 69, 19));
        lblTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        card.add(lblTitle, BorderLayout.NORTH);

        //使用 Map 計算餐點數量
        Map<String, Integer> productCount = new LinkedHashMap<>();

        for (Product p : order.getItems()) {
            String name = p.getName();
            productCount.put(name, productCount.getOrDefault(name, 0) + 1);
        }

        //建立 JList
        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        productCount.forEach((name, count) -> {
            String displayText = String.format("%-10s  x%d", name, count); 
            listModel.addElement(displayText);
        });
        JList<String> itemList = new JList<>(listModel);
        itemList.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        itemList.setForeground(Color.BLUE); 
        itemList.setBackground(new Color(255, 255, 240));
        
        //放入 ScrollPane
        JScrollPane scrollContent = new JScrollPane(itemList);
        scrollContent.setBorder(BorderFactory.createEtchedBorder());
        scrollContent.setPreferredSize(new Dimension(200, 100)); 
        card.add(scrollContent, BorderLayout.CENTER);

        // 完成按鈕
        JButton btnDone = new JButton("完成");
        btnDone.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        btnDone.setBackground(new Color(60, 179, 113));
        btnDone.setForeground(Color.WHITE);
        
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        btnPanel.add(btnDone, BorderLayout.CENTER);

        //按鈕事件
        btnDone.addActionListener(e -> {
            pendingPanel.remove(card);
            pendingPanel.revalidate(); 
            pendingPanel.repaint();    
            addOrderToCompleted(order);
        });
        
        card.add(btnPanel, BorderLayout.SOUTH);

        pendingPanel.add(card);
        pendingPanel.add(Box.createVerticalStrut(10));
        pendingPanel.revalidate(); 
        pendingPanel.repaint();
    }
    
    // 將訂單移到右側(完成訂單)
    private void addOrderToCompleted(Order order) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(230, 230, 230));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        //用html標籤
        String timeStr = sdf.format(order.getTimestamp());
        JLabel lblInfo = new JLabel("<html><strike>桌號: " + order.getTableId() + "</strike> <font color='gray'>(" + timeStr + ")</font></html>");
        lblInfo.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        
        card.add(lblInfo, BorderLayout.CENTER);
        
        completedPanel.add(card);
        completedPanel.add(Box.createVerticalStrut(5));
        
        completedPanel.revalidate();
        completedPanel.repaint();
    }
}