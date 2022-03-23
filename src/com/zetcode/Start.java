package com.zetcode;

import java.awt.EventQueue;
import javax.swing.JFrame;

import java.awt.Toolkit;
import java.awt.Dimension;

public class Start extends JFrame {

    public Start() {

        initUI();
    }

    private void initUI() {

        add(new Environment());
        setUndecorated(true);

        setResizable(false);
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        setLocation(x, y);

        setTitle("TopdownShooter");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JFrame ex = new Start();
            ex.setVisible(true);
        });
    }
}
