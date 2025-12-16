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
        setLocationRelativeTo(null);

        //初始化畫布(RestaurantPanel.java)
        restaurantPanel = new RestaurantPanel();
        
        //工具列
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JToggleButton tglEditMode = new JToggleButton("管理員模式");
        JButton btnAddTable = new JButton("➕ 新增桌子");
        btnAddTable.setEnabled(false);//預設為使用者模式(無法加入新桌)
        
        //切換模式(管理者可加入新桌)
        tglEditMode.addActionListener(e -> {
            boolean isEdit = tglEditMode.isSelected();
            restaurantPanel.setEditMode(isEdit);
            btnAddTable.setEnabled(isEdit);
        });
        
        //管理者加入新桌子
        btnAddTable.addActionListener(e -> {
            JTextField txtId = new JTextField(10);
            
            String[] sizeOptions = {
                "小桌 (2人) - 80x80", 
                "中桌 (4人) - 120x80", 
                "大桌 (6人) - 120x100" 
            };
            JComboBox<String> cmbSize = new JComboBox<>(sizeOptions);

            JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            inputPanel.add(new JLabel("請輸入新桌號 (例如: T5):"));
            inputPanel.add(txtId);
            inputPanel.add(new JLabel("請選擇桌子大小:"));
            inputPanel.add(cmbSize);

            int result = JOptionPane.showConfirmDialog(
                this, 
                inputPanel, 
                "新增桌子", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String inputId = txtId.getText().trim();
                
                if (!inputId.isEmpty()) {
                    int selectedIndex = cmbSize.getSelectedIndex();
                    int w = 80, h = 80;

                    switch (selectedIndex) {
                        case 0: 
                            w = 80; h = 80;
                            break;
                        case 1: 
                            w = 120; h = 80;
                            break;
                        case 2:
                            w = 120; h = 100;
                            break;
                    }

                    //採用RestaurantPanel裡的加入桌子方法
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
        
        checkServerConnection();
    }
    
    //背景連線
    private void checkServerConnection() {
        new Thread(() -> {
            try (Socket socket = new Socket("127.0.0.1", 8888);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                
            	//發送給後臺登入訊號
                oos.writeObject("LOGIN");
                oos.flush();
                System.out.println(">> 已發送連線訊號給 Server");

            } catch (java.net.ConnectException e) {
                System.out.println(">> Server 未啟動，無法自動連線");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}