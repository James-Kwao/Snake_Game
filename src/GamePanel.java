import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel {
    int tileSize = 24;
    int xSpeed, ySpeed;
    Tile snakeHead, food;
    Random random;
    Timer animator;
    ArrayList<Tile> snakeBody;
    Ellipse2D.Float eye;
    boolean isLeft, isRight, isTop, isDown, isGameOver;

    GamePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(MainFrame.SIZE, MainFrame.SIZE));
        setFocusable(true);

        random = new Random();
        snakeHead = new Tile(random.nextInt(MainFrame.SIZE / tileSize), random.nextInt(MainFrame.SIZE / tileSize));
        eye = new Ellipse2D.Float();
        snakeBody = new ArrayList<>();
        snakeBody.add(snakeHead);

        food = new Tile(random.nextInt(MainFrame.SIZE / tileSize), random.nextInt(MainFrame.SIZE / tileSize));
        isDown = isTop = isLeft = isRight = isGameOver = false;
        animator = new Timer(100, e -> {
            move();
            gameOver();
            repaint();
        });
        animator.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                keyPressedListener(e);
            }
        });

    }

    public void keyPressedListener(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && ySpeed != 1) {
            if (!animator.isRunning() && !isGameOver) animator.restart();
            isTop = true;
            isDown = isLeft = isRight = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN && ySpeed != -1) {
            if (!animator.isRunning() && !isGameOver) animator.restart();
            isDown = true;
            isTop = isLeft = isRight = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT && xSpeed != 1) {
            if (!animator.isRunning() && !isGameOver) animator.restart();
            isLeft = true;
            isTop = isDown = isRight = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && xSpeed != -1) {
            if (!animator.isRunning() && !isGameOver) animator.restart();
            isRight = true;
            isTop = isDown = isLeft = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!isGameOver)
                if (animator.isRunning()) animator.stop();
                else animator.restart();
        }
    }

    public boolean hasCollided(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void placeFood() {
        food = new Tile(random.nextInt(MainFrame.SIZE / tileSize), random.nextInt(MainFrame.SIZE / tileSize));
    }

    public void gameOver() {
        for (int i = 4; i < snakeBody.size(); i++) {
            if (hasCollided(snakeHead, snakeBody.get(i))) {
                isGameOver = true;
                isDown = isTop = isLeft = isRight = false;
                animator.stop();
                break;
            }
        }
    }

    public void move() {
        //checks if snake collide with the wall
        if (snakeHead.x + tileSize > MainFrame.SIZE) snakeHead.x -= snakeHead.x;
        if (snakeHead.y + tileSize > MainFrame.SIZE) snakeHead.y -= snakeHead.y;
        if (snakeHead.x < 0) snakeHead.x = MainFrame.SIZE - (MainFrame.SIZE % tileSize);
        if (snakeHead.y < 0) snakeHead.y = MainFrame.SIZE - (MainFrame.SIZE % tileSize);

        moveSnakeHead();

        if (hasCollided(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        snakeHead.x += xSpeed * tileSize;
        snakeHead.y += ySpeed * tileSize;
    }

    public void moveSnakeHead() {
        //move the snake per key pressed
        if (!isGameOver) {
            if (isTop) {
                xSpeed = 0;
                ySpeed = -1;
            }
            if (isDown) {
                xSpeed = 0;
                ySpeed = 1;
            }
            if (isLeft) {
                xSpeed = -1;
                ySpeed = 0;
            }
            if (isRight) {
                xSpeed = 1;
                ySpeed = 0;
            }
        }
    }

    private void drawEye(Graphics2D g2) {
        if (isRight) {
            eye = new Ellipse2D.Float(snakeBody.get(0).x + 15, snakeHead.y + 5, 5, 5);
            g2.fill(eye);
            eye = new Ellipse2D.Float(snakeBody.get(0).x + 15, snakeHead.y + 15, 5, 5);
            g2.fill(eye);
        } else if (isLeft) {
            eye = new Ellipse2D.Float(snakeBody.get(0).x + 5, snakeHead.y + 5, 5, 5);
            g2.fill(eye);
            eye = new Ellipse2D.Float(snakeBody.get(0).x + 5, snakeHead.y + 15, 5, 5);
            g2.fill(eye);
        } else if (isTop) {
            eye = new Ellipse2D.Float(snakeBody.get(0).x + 5, snakeHead.y + 5, 5, 5);
            g2.fill(eye);
            eye = new Ellipse2D.Float(snakeBody.get(0).x + 15, snakeHead.y + 5, 5, 5);
            g2.fill(eye);
        } else if (isDown) {
            eye = new Ellipse2D.Float(snakeBody.get(0).x + 5, snakeHead.y + 15, 5, 5);
            g2.fill(eye);
            eye = new Ellipse2D.Float(snakeBody.get(0).x + 15, snakeHead.y + 15, 5, 5);
            g2.fill(eye);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //draw food
        g2.setColor(Color.red);
        g2.fill(new Ellipse2D.Float(food.x, food.y, tileSize, tileSize));

        //draw snake body
        for (Tile tile : snakeBody) {
            Rectangle2D.Float snakePart = new Rectangle2D.Float(tile.x, tile.y, tileSize, tileSize);
            g2.setColor(Color.orange);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 20));
            g2.draw(snakePart);
            g2.setColor(Color.green);
            g2.setStroke(new BasicStroke(1));
            g2.fill(snakePart);
        }
        //draw snake eyes
        g2.setColor(Color.BLUE.brighter());
        drawEye(g2);

        //draw score
        g2.setFont(new Font("Bell MT", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + (snakeBody.size() - 1) * 3, 15, 20);
    }

    private class Tile {
        int x, y;

        public Tile(int x, int y) {
            this.x = x * tileSize;
            this.y = y * tileSize;
        }
    }
}
