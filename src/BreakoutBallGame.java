import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutBallGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout Ball Game");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks;
    private Timer timer;
    private int delay;
    private int paddleX = 310;
    private int ballPosX = 120;
    private int ballPosY = 350;
    private int ballDirX = -1;
    private int ballDirY = -2;
    private MapGenerator map;
    private int level = 1;
    private final int maxLevel = 3;

    public GamePanel() {
        map = new MapGenerator(level + 2, 7);
        totalBricks = (level + 2) * 7;
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        delay = Math.max(3, 10 - level * 2); // Decrease delay as level increases
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);
        map.draw((Graphics2D) g);
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score: " + score, 560, 30);
        g.drawString("Level: " + level, 10, 30);
        g.setColor(Color.green);
        g.fillRect(paddleX, 550, 100, 8);
        g.setColor(Color.yellow);
        g.fillOval(ballPosX, ballPosY, 20, 20);
        
        if (level > maxLevel) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.green);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Congratulations! You Completed the Game!", 70, 300);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }
        
        if (ballPosY > 570) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over, Score: " + score, 190, 300);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }
        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(paddleX, 550, 100, 8))) {
                ballDirY = -ballDirY;
            }

            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        Rectangle brickRect = new Rectangle(brickX, brickY, map.brickWidth, map.brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballDirX = -ballDirX;
                            } else {
                                ballDirY = -ballDirY;
                            }
                        }
                    }
                }
            }

            ballPosX += ballDirX;
            ballPosY += ballDirY;

            if (ballPosX < 0 || ballPosX > 670) ballDirX = -ballDirX;
            if (ballPosY < 0) ballDirY = -ballDirY;

            if (totalBricks <= 0 && level <= maxLevel) {
                level++;
                if (level <= maxLevel) {
                    map = new MapGenerator(level + 2, 7);
                    totalBricks = (level + 2) * 7;
                    ballPosX = 120;
                    ballPosY = 350;
                    delay = Math.max(3, 10 - level * 2);
                    timer.setDelay(delay);
                    repaint();
                }
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) paddleX = Math.min(paddleX + 20, 600);
        if (e.getKeyCode() == KeyEvent.VK_LEFT) paddleX = Math.max(paddleX - 20, 10);
        if (e.getKeyCode() == KeyEvent.VK_ENTER && !play) {
            if (level > maxLevel) level = 1;
            play = true;
            ballPosX = 120;
            ballPosY = 350;
            score = 0;
            totalBricks = (level + 2) * 7;
            delay = Math.max(3, 10 - level * 2);
            timer.setDelay(delay);
            map = new MapGenerator(level + 2, 7);
            repaint();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}

class MapGenerator {
    public int[][] map;
    public int brickWidth;
    public int brickHeight;

    public MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int[] rowArr : map) java.util.Arrays.fill(rowArr, 1);
        brickWidth = 540 / col;
        brickHeight = 150 / row;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                if (map[i][j] > 0) {
                    g.setColor(Color.white);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    g.setColor(Color.black);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
    }
    public void setBrickValue(int value, int row, int col) { 
      map[row][col] = value; }
}
