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
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Rectangle;

public class Environment extends JPanel implements ActionListener {

    private final int B_WIDTH = 500;
    private final int B_HEIGHT = 500;
    private final int DOT_SIZE = 25;
    private final int DELAY = 40;

    private Character player;
    private boolean inGame = true;

    private int maxEnemys = 3;
    private int[][] spawnPoints = {{-100,-100},{550,-50},{510,550},{550,550}};
    private int[] hardrange = {5, 10, 13, 15};
    private int kills = 0;

    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    private ArrayList<Character> enemys = new ArrayList<Character>();

    public Environment() {

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
        initGame();
    }

    private void initGame() {
        player = new Character(B_WIDTH/2, B_HEIGHT/2, 0);
        Character fEnemy = new Character(100, B_HEIGHT/2, -90);

        enemys.add(fEnemy);
        Character sEnemy = new Character(400, B_HEIGHT/2, 180);
        enemys.add(sEnemy);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
        SpawnEnemys();
    }

    private void DrawBullets(Graphics g)
    {
        if (inGame) {
            for (Bullet bullet : bullets) {
                try {

                    BufferedImage originalImage = ImageIO.read(new File("src/resources/round.png"));
                    BufferedImage subImage = rotateImage(originalImage, bullet.rotation);
                    bullet.updatePostion();
                    g.drawImage(subImage, bullet.x += bullet.xOffset, bullet.y += bullet.yOffet, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            checkBulletCollison();
        }
    }

    private void DrawEnemys(Graphics g)
    {
        try {
            checkPlayerCollsison();

            for (Character enemy : enemys) {
                if (enemy.x < player.x)
                {
                    enemy.x += 1;
                }
                if (enemy.x > player.x)
                {
                    enemy.x -= 1;
                }
                if (enemy.y < player.y)
                {
                    enemy.y += 1;
                }
                if (enemy.y > player.y)
                {
                    enemy.y -= 1;
                }
                double dx = player.x - enemy.x;
                double dy = player.y - enemy.y;
                enemy.rotation = (Math.toDegrees(Math.atan2(dy, dx))-90);

                BufferedImage originalImage = ImageIO.read(new File("src/resources/enemy.png"));
                BufferedImage subImage = rotateImage(originalImage, enemy.rotation);
                g.drawImage(subImage, enemy.x, enemy.y, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            Image bg = Toolkit.getDefaultToolkit().getImage("src/resources/ground.jpg");
            g.drawImage(bg, 0, 0, null);

            try {

                BufferedImage originalImage = ImageIO.read(new File("src/resources/player.gif"));
                BufferedImage subImage = rotateImage(originalImage, player.rotation);
                g.drawImage(subImage, player.x, player.y, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String killsText = "kills: " + kills;
            Font small = new Font("Helvetica", Font.BOLD, 14);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.yellow);
            g.setFont(small);
            g.drawString(killsText, (B_WIDTH - metr.stringWidth(killsText)) / 2, 20);
            DrawEnemys(g);
            DrawBullets(g);
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
        setBackground(Color.red);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void Shoot()
    {
        int xOffest = 0;
        int yOffset = 0;

        if (player.rotation == 0)
        {
            xOffest = 50;
            yOffset = 36;
        }
        else if (player.rotation == 45)
        {
            xOffest = 34;
            yOffset = 49;
        }
        else if (player.rotation == 90)
        {
            xOffest = 14;
            yOffset = 48;
        }
        else if (player.rotation == 135)
        {
            xOffest = 2;
            yOffset = 27;
        }
        else if (player.rotation == 180)
        {
            xOffest = 3;
            yOffset = 8;
        }
        else if (player.rotation == 225)
        {
            xOffest = 22;
            yOffset = 0;
        }
        else if (player.rotation == 270)
        {
            xOffest = 40;
            yOffset = -2;
        }
        else if (player.rotation == 315)
        {
            xOffest = 56;
            yOffset = 12;
        }
        Bullet bullet = new Bullet(player.x + xOffest, player.y + yOffset, player.rotation);
        bullets.add(bullet);
    }

    private void SpawnEnemys()
    {
        if (enemys.size() < maxEnemys)
        {
            int[] randomPoint = spawnPoints[new Random().nextInt(spawnPoints.length)];
            Character newEnemy = new Character(randomPoint[0], randomPoint[1], 0);
            enemys.add(newEnemy);

            if (kills > hardrange[0] && hardrange != null)
            {
                maxEnemys++;
                hardrange = removeFirstElement(hardrange);
            }
        }
    }

    public static int[] removeFirstElement(int[] arr) {
        int newArr[] = new int[arr.length - 1];
        for (int i = 1; i < arr.length; i++) {
            newArr[i-1] = arr[i];
        }
        return newArr;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {
            repaint();
        }
    }

    private void checkPlayerCollsison()
    {
        for (Character enemy : enemys) {
            Sprite enemySprite = new Sprite(enemy.x, enemy.y, "src/resources/enemy.png");
            Sprite playeSprite = new Sprite(player.x, player.y, "src/resources/player.gif");

            Rectangle enemyRect = enemySprite.getBounds();
            Rectangle playerRect = playeSprite.getBounds();
            enemyRect.setSize(enemyRect.width/2, enemyRect.height/2);
            if (enemyRect.intersects(playerRect)) {
                inGame = false;
            }
        }
    }

    private void checkBulletCollison()
    {
        ArrayList<Bullet> toDeleteBullets = new ArrayList<Bullet>();
        ArrayList<Character> toDeleteEnemys = new ArrayList<Character>();

        for (Bullet bullet : bullets) {
            for (Character enemy : enemys) {
                Sprite enemySprite = new Sprite(enemy.x, enemy.y, "src/resources/enemy.png");
                Sprite bulletSprite = new Sprite(bullet.x, bullet.y, "src/resources/round.png");

                Rectangle enemyRect = enemySprite.getBounds();
                Rectangle bulletrect = bulletSprite.getBounds();

                if (enemyRect.intersects(bulletrect)) {
                    toDeleteEnemys.add(enemy);
                    toDeleteBullets.add(bullet);
                    kills++;
                }
            }
        }

        for (Bullet bullet : toDeleteBullets) {
            bullets.remove(bullet);
        }

        for (Character enemy : toDeleteEnemys) {
            enemys.remove(enemy);
        }
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

            if ((key == KeyEvent.VK_RIGHT)) {
                if (player.rotation == 315)
                {
                    player.rotation = 0;
                }
                else{
                    player.rotation += 45;
                }
            }

            if ((key == KeyEvent.VK_LEFT)) {
                if (player.rotation ==  0)
                {
                    player.rotation = 315;
                }
                else{
                    player.rotation -= 45;
                }
            }

            if ((key == KeyEvent.VK_X)) {
                System.exit(0);
            }
        }
    }
}
