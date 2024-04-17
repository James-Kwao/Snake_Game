import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GamePanel extends JPanel {
    int tileSize = 24;
    int xSpeed, ySpeed;
    Tile snakeHead, food;
    Random random;
    Timer animator;
    ArrayList<Tile> snakeBody;
    Ellipse2D.Float eye;
    boolean isLeft, isRight, isTop, isDown, isGameOver, isPause;

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
            if (!isPause)
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
            if (!isPause || !isGameOver) {
                isTop = true;
                isDown = isLeft = isRight = false;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN && ySpeed != -1) {
            if (!isPause || !isGameOver) {
                isDown = true;
                isTop = isLeft = isRight = false;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT && xSpeed != 1) {
            if (!isPause || !isGameOver) {
                isLeft = true;
                isTop = isDown = isRight = false;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && xSpeed != -1) {
            if (!isPause || !isGameOver) {
                isRight = true;
                isTop = isDown = isLeft = false;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!isGameOver) {
                isPause = !isPause;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
//            if (isGameOver) {
            snakeBody.removeAll(Collections.unmodifiableList(snakeBody));
            food = new Tile(random.nextInt(MainFrame.SIZE / tileSize), random.nextInt(MainFrame.SIZE / tileSize));
            snakeHead = new Tile(random.nextInt(MainFrame.SIZE / tileSize), random.nextInt(MainFrame.SIZE / tileSize));
            snakeBody.add(snakeHead);
            isDown = isTop = isLeft = isRight = isGameOver = isPause = false;
            xSpeed = ySpeed = 0;
            repaint();
            animator.restart();
//            }
        }
    }

    public boolean hasCollided(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void placeFood() {
        food = new Tile(random.nextInt(MainFrame.SIZE / tileSize), random.nextInt(MainFrame.SIZE / tileSize));
    }

    public void gameOver() {
        if (snakeBody.size() > 3)
            for (int i = 4; i < snakeBody.size(); i++)
                if (hasCollided(snakeHead, snakeBody.get(i))) {
                    isGameOver = true;
                    isDown = isTop = isLeft = isRight = false;
                    animator.stop();
                    break;
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
        if (!isGameOver)
            if (isRight) {
                eye = new Ellipse2D.Float(snakeBody.getFirst().x + 15, snakeHead.y + 5, 5, 5);
                g2.fill(eye);
                eye = new Ellipse2D.Float(snakeBody.getFirst().x + 15, snakeHead.y + 15, 5, 5);
                g2.fill(eye);
            } else if (isLeft) {
                eye = new Ellipse2D.Float(snakeBody.getFirst().x + 5, snakeHead.y + 5, 5, 5);
                g2.fill(eye);
                eye = new Ellipse2D.Float(snakeBody.getFirst().x + 5, snakeHead.y + 15, 5, 5);
                g2.fill(eye);
            } else if (isTop) {
                eye = new Ellipse2D.Float(snakeBody.getFirst().x + 5, snakeHead.y + 5, 5, 5);
                g2.fill(eye);
                eye = new Ellipse2D.Float(snakeBody.getFirst().x + 15, snakeHead.y + 5, 5, 5);
                g2.fill(eye);
            } else if (isDown) {
                eye = new Ellipse2D.Float(snakeBody.getFirst().x + 5, snakeHead.y + 15, 5, 5);
                g2.fill(eye);
                eye = new Ellipse2D.Float(snakeBody.getFirst().x + 15, snakeHead.y + 15, 5, 5);
                g2.fill(eye);
            }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //draw snake body
        for (Tile tile : snakeBody) {
            Rectangle2D.Float snakePart = new Rectangle2D.Float(tile.x, tile.y, tileSize, tileSize);
            g2.setColor(Color.green);
            g2.fill(snakePart);
            g2.setColor(Color.orange);
            g2.draw(snakePart);
        }

        if (!isPause) {
            //draw food
            g2.setColor(Color.red);
            g2.fill(new Ellipse2D.Float(food.x, food.y, tileSize, tileSize));

            //draw snake eyes
            g2.setColor(Color.BLUE.brighter());
            drawEye(g2);
        }

        //draw score
        g2.setFont(new Font("Bell MT", Font.PLAIN, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + (snakeBody.size() - 1) * 3, 15, 20);

        //draw game over
        g2.setFont(new Font("Bradley Hand ITC", Font.BOLD | Font.ITALIC, 80));
        g2.setColor(new Color(0x6A0086));

        if (isGameOver)
            g2.drawString(
                    "Game Over",
                    MainFrame.SIZE / 2 - g2.getFontMetrics().stringWidth("Game Over") / 2,
                    MainFrame.SIZE / 2 + g2.getFontMetrics().getHeight() / 3
            );

        if (isPause) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Curlz MT", Font.BOLD | Font.ITALIC, 120));
            g2.drawString(
                    "PAUSED",
                    MainFrame.SIZE / 2 - g2.getFontMetrics().stringWidth("PAUSED") / 2,
                    MainFrame.SIZE / 2 + g2.getFontMetrics().getHeight() / 3);
        }
    }

    public class Tile {
        int x, y;

        public Tile(int x, int y) {
            this.x = x * tileSize;
            this.y = y * tileSize;
        }
    }
}
