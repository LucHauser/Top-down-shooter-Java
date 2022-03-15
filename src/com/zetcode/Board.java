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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;


public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 500;
    private final int B_HEIGHT = 500;
    private final int DOT_SIZE = 25;
    // private final int ALL_DOTS = 500;
    private final int RAND_POS = 29;
    private final int DELAY =40;

    private Player player;
    private int apple_x;
    private int apple_y;

    private boolean inGame = true;

    private Image apple;
    private Image playerImg;
    private Image bulletImg;

    private boolean isFirst = true;


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

        ImageIcon iih = new ImageIcon("src/resources/player1.gif");
        playerImg = iih.getImage();

        ImageIcon iib = new ImageIcon("src/resources/dot.png");
        bulletImg = iib.getImage();
    }

    private void initGame() {

        player = new Player(250, 250, false, false, false, true);

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

            BufferedImage img = imageToBufferedImage(playerImg);
            g.drawImage(playerImg, player.x, player.y, this);
            rotate(img, 0);
            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }
    }


    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bi = new BufferedImage(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public static BufferedImage rotate(BufferedImage image, double angle) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int) Math.floor(h * cos + w * sin);
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(neww, newh);
        Graphics2D g = result.createGraphics();
        g.translate((neww - w) / 2, (newh - h) / 2);
        g.rotate(angle, w / 2, h / 2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }

    private static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
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
        Bullet bullet = new Bullet(player.x + offsetX, player.y + offsetY, player.u, player.d, player.l, player.r);
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

            if ((key == KeyEvent.VK_LEFT)) {
                player.x -= DOT_SIZE;
            }

            if ((key == KeyEvent.VK_RIGHT)) {
                player.x += DOT_SIZE;
            }

            if ((key == KeyEvent.VK_UP)) {
                player.y -= DOT_SIZE;
            }

            if ((key == KeyEvent.VK_DOWN)) {
                player.y += DOT_SIZE;
            }

            if ((key == KeyEvent.VK_SPACE)) {
                Shoot();
            }
        }
    }
}
