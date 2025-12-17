package client;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PosClientFrame extends JFrame {

    private RestaurantPanel restaurantPanel;

    public PosClientFrame() {
        setTitle("前台 POS 系統 (Client)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- 設定視窗位置靠左 (讓 Demo 更好看) ---
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(0, (screenSize.height - this.getHeight()) / 2);

        restaurantPanel = new RestaurantPanel();

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JToggleButton tglEditMode = new JToggleButton("管理員模式");
        JButton btnAddTable = new JButton("➕ 新增桌子");
        btnAddTable.setEnabled(false); 

        tglEditMode.addActionListener(e -> {
            boolean isEdit = tglEditMode.isSelected();
            restaurantPanel.setEditMode(isEdit);
            btnAddTable.setEnabled(isEdit);
        });

        // 新增桌子按鈕邏輯
        btnAddTable.addActionListener(e -> {
            JTextField txtId = new JTextField(10);
            String[] sizeOptions = { "小桌 (2人) - 80x80", "中桌 (4人) - 120x80", "大桌 (6人) - 120x100" };
            JComboBox<String> cmbSize = new JComboBox<>(sizeOptions);

            JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            inputPanel.add(new JLabel("請輸入新桌號 (例如: T5):"));
            inputPanel.add(txtId);
            inputPanel.add(new JLabel("請選擇桌子大小:"));
            inputPanel.add(cmbSize);

            int result = JOptionPane.showConfirmDialog(this, inputPanel, "新增桌子", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String inputId = txtId.getText().trim();
                if (!inputId.isEmpty()) {
                    int selectedIndex = cmbSize.getSelectedIndex();
                    int w = 80, h = 80;
                    switch (selectedIndex) {
                        case 0: w = 80; h = 80; break;
                        case 1: w = 120; h = 80; break;
                        case 2: w = 120; h = 100; break;
                    }
                    restaurantPanel.addTable(inputId, w, h);
                } else {
                    JOptionPane.showMessageDialog(this, "桌號不能為空！");
                }
            }
        });

        toolBar.add(tglEditMode);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(btnAddTable);

        add(toolBar, BorderLayout.NORTH);
        add(restaurantPanel, BorderLayout.CENTER);
        
        // ⭐ 修改處 1: 啟動時執行連線檢查
        keepConnectionAlive();
    }
    
    // ⭐ 修改處 2: 建立長連線的方法
    private void keepConnectionAlive() {
        new Thread(() -> {
            try {
                // 1. 連線到 Server (假設在 localhost)
                Socket socket = new Socket("127.0.0.1", 8888);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                
                // 2. 發送 "LOGIN" 字串，這就是 Server 在等的「通關密語」
                oos.writeObject("LOGIN");
                oos.flush();
                System.out.println(">> 已發送連線訊號給 Server (長連線建立)");

                // 3. 關鍵：這裡千萬不要寫 socket.close()！
                // 我們要讓這個連線一直「活著」，直到這個視窗被關閉為止。
                // 當視窗關閉時，Java 會自動斷開 Socket，Server 就會收到斷線通知並變紅燈。

            } catch (java.net.ConnectException e) {
                System.out.println(">> Server 未啟動，無法建立狀態連線 (紅燈)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}