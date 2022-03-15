package com.zetcode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 500;
    private final int B_HEIGHT = 500;
    private final int DOT_SIZE = 25;
    private final int RAND_POS = 29;
    private final int DELAY = 40;

    private Player player;
    private int apple_x;
    private int apple_y;

    private boolean inGame = true;

    private Image apple;
    private Image bulletImg;


    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();

    public Board() {

        initBoard();
        final Timer timer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                repaint();
            }
        });
        timer.start();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iib = new ImageIcon("src/resources/bullet.png");
        bulletImg = iib.getImage();
    }

    private void initGame() {

        player = new Player(250, 250, 0);

        locateApple();

        // timer = new Timer(DELAY, this);
        // timer.start();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
        DrawBullet(g);

        for (Bullet bullet : bullets) {
            if (bullet.l) {
                bullet.x -= DOT_SIZE;
            }

            if (bullet.r) {
                bullet.x += DOT_SIZE;
            }

            if (bullet.u) {
                bullet.y += DOT_SIZE;
            }

            if ((bullet.d)) {
                bullet.y -= DOT_SIZE;
            }
        }
    }

    private void DrawBullet(Graphics g)
    {
        if (inGame) {
            for (Bullet bullet : bullets) {
                g.drawImage(bulletImg, bullet.x, bullet.y, this);
            }
        }
    }
    private void doDrawing(Graphics g) {
        if (inGame) {
            Image bg = Toolkit.getDefaultToolkit().getImage("src/resources/ground.jpg");
            g.drawImage(bg, 0, 0, null);
            g.drawImage(apple, apple_x, apple_y, this);

            try {

                BufferedImage originalImage = ImageIO.read(new File("src/resources/player1.gif"));
                BufferedImage subImage = rotateImage(originalImage, player.rotation);
                g.drawImage(subImage, player.x, player.y, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
    }

    public static BufferedImage rotateImage(BufferedImage imageToRotate, double angle) {
        int widthOfImage = imageToRotate.getWidth();
        int heightOfImage = imageToRotate.getHeight();

        BufferedImage newImageFromBuffer = new BufferedImage(widthOfImage, heightOfImage, java.awt.Transparency.TRANSLUCENT);

        Graphics2D graphics2D = newImageFromBuffer.createGraphics();

        graphics2D.rotate(Math.toRadians(angle), widthOfImage / 2, heightOfImage / 2);
        graphics2D.drawImage(imageToRotate, null, 0, 0);

        return newImageFromBuffer;
    }

    private void gameOver(Graphics g) {

        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    // private void checkApple() {

    //     if ((x[0] == apple_x) && (y[0] == apple_y)) {

    //         dots++;
    //         locateApple();
    //     }
    // }

    // private void movePlayer() {

    //     if (leftDirection) {
    //         x[0] -= DOT_SIZE;
    //     }

    //     if (rightDirection) {
    //         x[0] += DOT_SIZE;
    //     }

    //     if (upDirection) {
    //         y[0] -= DOT_SIZE;
    //     }

    //     if (downDirection) {
    //         y[0] += DOT_SIZE;
    //     }
    // }

    // private void checkCollision() {

    //     for (int z = dots; z > 0; z--) {

    //     if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
    //     inGame = false;
    //     }
    //     }

    //     if (y[0] >= B_HEIGHT) {
    //         inGame = false;
    //     }

    //     if (y[0] < 0) {
    //         inGame = false;
    //     }

    //     if (x[0] >= B_WIDTH) {
    //         inGame = false;
    //     }

    //     if (x[0] < 0) {
    //         inGame = false;
    //     }

    //     if (!inGame) {
    //         timer.stop();
    //     }
    // }

    private void Shoot()
    {
        System.out.println("Shot");
        int offsetX = 50;
        int offsetY = 33;
        Bullet bullet = new Bullet(player.x + offsetX, player.y + offsetY, false, false, false, true);
        bullets.add(bullet);

    }

    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            // checkApple();
            // checkCollision();
            repaint();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_A)) {
                player.x -= DOT_SIZE;
            }

            if ((key == KeyEvent.VK_D)) {
                player.x += DOT_SIZE;
            }

            if ((key == KeyEvent.VK_W)) {
                player.y -= DOT_SIZE;
            }

            if ((key == KeyEvent.VK_S)) {
                player.y += DOT_SIZE;
            }

            if ((key == KeyEvent.VK_SPACE)) {
                Shoot();
            }

            if ((key == KeyEvent.VK_UP)) {
                player.rotation = -90;
            }

            if ((key == KeyEvent.VK_DOWN)) {
                player.rotation = 90;
            }

            if ((key == KeyEvent.VK_RIGHT)) {
                player.rotation = 0;
            }

            if ((key == KeyEvent.VK_LEFT)) {
                player.rotation = 180;
            }
        }
    }
}
