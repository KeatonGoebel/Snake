
//
// Snake Game
//

package snake;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        // Creating window
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Snake");

        // Creating gamepanel within window
        GamePanel gamePanel = new GamePanel();
        gamePanel.setBounds(0,0,gamePanel.screenWidth, gamePanel.screenHeight);
        gamePanel.setOpaque(true);

        // Creating start button
        JButton startButton = new JButton("Play Game");
        startButton.setBounds(gamePanel.screenWidth / 2 - 100,gamePanel.screenHeight / 2 - 25,200,50);
        startButton.setBackground(Color.red);
        startButton.setForeground(Color.black);
        startButton.setBorderPainted(false);
        startButton.setOpaque(true);

        // Creating layered layout
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight));
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(startButton, JLayeredPane.PALETTE_LAYER);

        // Action Listeners
        startButton.addActionListener(e -> {
            gamePanel.startGameThread();
            layeredPane.remove(startButton);
            layeredPane.repaint();
        });

        window.setContentPane(layeredPane);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.revalidate();
        gamePanel.repaint();
        layeredPane.revalidate();
        layeredPane.repaint();
        window.revalidate();
        window.repaint();

    }
}