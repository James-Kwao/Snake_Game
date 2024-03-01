import javax.swing.*;

public class MainFrame {
    static int SIZE = 600;

    public static void main(String[] args) {
        GamePanel gamePanel = new GamePanel();
        JFrame frame = new JFrame("Snake Game");
        frame.setSize(SIZE, SIZE);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
        gamePanel.requestFocus();
    }
}
