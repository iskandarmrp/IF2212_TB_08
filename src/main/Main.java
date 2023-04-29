package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Loadingscreen
        LoadFrame openingLoad = new LoadFrame(); // creates a new MyFrame object
        UtilityTool.delay(4);
        openingLoad.dispose();

        JFrame window = new JFrame(); // creates a new MyFrame object
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // set the default close operation (exit when it is
                                                               // closed)
        window.setResizable(false); // set the frame to be resizable
        window.setTitle("Sim-Plicity"); // set the title of the frame
        ImageIcon smallLogo = new ImageIcon("assets/logo/small-logo.png"); // create an ImageIcon object from an image file
        window.setIconImage(smallLogo.getImage());

        GamePanel gamePanel = new GamePanel(); // creates a new GamePanel object
        window.add(gamePanel); // add the panel to the frame

        window.pack(); // set the frame to be the size of the panel

        window.setLocationRelativeTo(null); // set the frame to be centered
        window.setVisible(true); // set the frame to be visible

        gamePanel.setupGame();
        gamePanel.startGameThread();

    }

    // -------------------------------------------------------------------------------

    
}
