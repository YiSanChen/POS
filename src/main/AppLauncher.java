package main;

import client.PosClientFrame;
import server.KitchenServerFrame;

import javax.swing.*;
import java.awt.*;

public class AppLauncher {
    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("智慧餐飲系統啟動器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 200);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
        frame.setLocationRelativeTo(null);

        JButton btnClient = new JButton("啟動前台 POS (Client)");
        JButton btnServer = new JButton("啟動廚房後台 (Server)");

        btnClient.setPreferredSize(new Dimension(200, 30));
        btnServer.setPreferredSize(new Dimension(200, 30));

        btnClient.addActionListener(e -> {
            frame.dispose(); 
            new PosClientFrame().setVisible(true);
        });

        btnServer.addActionListener(e -> {
            frame.dispose(); 
            new KitchenServerFrame().setVisible(true); 
        });

        frame.add(btnClient);
        frame.add(btnServer);
        
        frame.setVisible(true);
    }
}