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

// import org.w3c.dom.events.MouseEvent;
import java.awt.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.awt.Rectangle;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Environment extends JPanel implements ActionListener  {

    private final int B_WIDTH = 500;
    private final int B_HEIGHT = 500;
    private final int DOT_SIZE = 25;
    private final int DELAY = 40;

    private Character player;
    private boolean inGame = true;

    private int maxEnemys = 3;
    private int[][] spawnPoints = {{-0,-0},{500,-0},{0,500},{500,500},{0,250},{250,0},{250,500},{500,250}};
    private int mX;
    private int mY;
    private int counter = 0;
    private int kills = 0;
    private int maxShoots = 20;
    private int shoots = maxShoots;
    private int highscore;
    private boolean highscoreCheck = false;

    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    private ArrayList<Character> enemys = new ArrayList<Character>();
    private ArrayList<Blood> bloodEffects = new ArrayList<Blood>();

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
        // MapListener mapListener = new MapListener();
        addMouseListener(new MapListener());
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
            checkPlayerCollsison(g);

            for (Character enemy : enemys) {
                enemy.y -= (2*Math.sin(Math.toRadians(enemy.rotation-90)));
                enemy.x -= (2*Math.sin(Math.toRadians(enemy.rotation)));

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

        ArrayList<Blood> bloodEffectsToDestroy = new ArrayList<Blood>();

        for (Blood blood : bloodEffects) {
            blood.UpDateLifeTime();
            if (blood.getLiefetime() <= 0)
            {
                bloodEffectsToDestroy.add(blood);
            }
            else
            {
                Image bloodImage = Toolkit.getDefaultToolkit().getImage("src/resources/blood.png");
                g.drawImage(bloodImage, blood.getX(), blood.getY(), null);
            }
        }

        for (Blood blood : bloodEffectsToDestroy) {
            bloodEffects.remove(blood);
        }
    }

    private void doDrawing(Graphics g) {
        if (inGame) {
            Image bg = Toolkit.getDefaultToolkit().getImage("src/resources/ground.jpg");
            g.drawImage(bg, 0, 0, null);

            try {
                Point mousPos = MouseInfo.getPointerInfo().getLocation();
                mX = (int) mousPos.getX();
                mY = (int) mousPos.getY();

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (screenSize.width/ 2) - B_WIDTH/2;
                int y = (screenSize.height/ 2) - B_HEIGHT/2;
                mX -= x;
                mY -= y -20;
                double dx = mX - player.x - 30;
                double dy = mY - player.y - 25;
                player.rotation = (Math.toDegrees(Math.atan2(dy, dx)));

                BufferedImage originalImage = ImageIO.read(new File("src/resources/player.gif"));
                BufferedImage subImage = rotateImage(originalImage, player.rotation);
                g.drawImage(subImage, player.x, player.y, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DrawEnemys(g);
            DrawBullets(g);

            String killsText = "kills: " + kills;
            String shotsText = "";
            Font small = new Font("Helvetica", Font.BOLD, 14);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.yellow);
            g.setFont(small);
            g.drawString(killsText, (B_WIDTH - metr.stringWidth(killsText)) / 2, 20);
            if (shoots > 0)
            {
                shotsText = "shots: " + shoots;
                g.setColor(Color.white);
            }
            else
            {
                shotsText = "Press R to reload";
                g.setColor(Color.red);
            }
            g.drawString(shotsText, (B_WIDTH - metr.stringWidth(shotsText)), 20);

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
        String score = "score: " + kills;
        if (!this.highscoreCheck)
        {
            this.highscoreCheck = true;
            highscore = SaveScore(kills);
        }
        String highscoreText = "highscore: " + highscore;
        String restartText = "Press R to restart";
        Font big = new Font("Helvetica", Font.BOLD, 50);
        FontMetrics metr = getFontMetrics(big);
        Font small = new Font("Helvetica", Font.BOLD, 14);
        Font midle = new Font("Helvetica", Font.BOLD, 30);
        FontMetrics metrs = getFontMetrics(small);
        FontMetrics midlemetrs = getFontMetrics(midle);

        g.setColor(Color.white);
        setBackground(Color.red);
        g.setFont(big);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 -100);
        g.setFont(small);
        g.setColor(Color.black);
        g.drawString(restartText, (B_WIDTH - metrs.stringWidth(restartText)) / 2, B_HEIGHT / 2 + 100);
        g.setFont(midle);
        g.setColor(Color.white);
        g.drawString(score, (B_WIDTH - midlemetrs.stringWidth(score)) / 2, B_HEIGHT / 2 -30);
        g.drawString(highscoreText, (B_WIDTH - midlemetrs.stringWidth(highscoreText)) / 2, B_HEIGHT / 2 + 20);
        g.setFont(small);
    }

    private void restart()
    {
        kills = 0;
        shoots = maxShoots;
        player.x = B_WIDTH/2;
        player.x = B_HEIGHT/2;
        bullets.clear();
        enemys.clear();
        initGame();
        inGame = true;
        highscoreCheck = false;
    }

    int SaveScore(Integer score)
    {
        File scoreFile = new File(System.getProperty("user.dir") + "\\src\\resources\\score.txt");
        try{
            if(!scoreFile.exists()){
                scoreFile.createNewFile();
                BufferedWriter wirter = new BufferedWriter(new FileWriter(scoreFile));
                wirter.write("0");
                wirter.close();
            }
        } catch(IOException e){
            System.out.println("Error: "+e);
        }

        BufferedReader reader = null;
        BufferedWriter wirter = null;

        try{
            reader = new BufferedReader(new FileReader(scoreFile));
            String currentLine = reader.readLine();

            wirter = new BufferedWriter(new FileWriter(scoreFile));

            Integer highscore = Integer.parseInt(currentLine);

            if (highscore > score)
            {
                wirter.write(highscore.toString());
                return highscore;
            } else {
                wirter.write(score.toString());
                return score;
            }
        } catch(IOException e){
            System.out.println("Error: "+e);
        } finally
        {
            try {
                reader.close();
                wirter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private void Shoot()
    {
        if (shoots > 0)
        {
            int xOffest = 60/2;
            int yOffset = 50/2;

            Bullet bullet = new Bullet(player.x + xOffest, player.y + yOffset, player.rotation*-1);
            bullets.add(bullet);
            shoots--;

            bullet.y += (-24*Math.sin(Math.toRadians(bullet.rotation-30)));
            bullet.x += (24*Math.sin(Math.toRadians(90-bullet.rotation+30)));
        }
    }

    private void SpawnEnemys()
    {
        if (enemys.size() < maxEnemys)
        {
            int[] randomPoint = spawnPoints[new Random().nextInt(spawnPoints.length)];
            Character newEnemy = new Character(randomPoint[0], randomPoint[1], 0);
            enemys.add(newEnemy);
            counter++;

            if (counter >= 20)
            {
                counter = 0;
                maxEnemys++;
            }
        }
    }

    private boolean CheckIfOutOfMap(int x, int y, int offset)
    {
        if (x < 0 + offset || x > B_WIDTH -offset || y < 0 + offset || y > B_HEIGHT - offset)
        {
            return true;
        }
        return false;
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

    private void checkPlayerCollsison(Graphics g)
    {
        Sprite playeSprite = new Sprite(player.x, player.y, "src/resources/player.gif");
        Rectangle playerRect = playeSprite.getBounds();
        playerRect.setSize(playerRect.width/2, playerRect.height/2);

        for (Character enemy : enemys) {
            Sprite enemySprite = new Sprite(enemy.x, enemy.y, "src/resources/enemy.png");

            Rectangle enemyRect = enemySprite.getBounds();
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

        for (Character enemy : toDeleteEnemys) {
            Blood newbloodParticle = new Blood(enemy.x, enemy.y, 5);
            bloodEffects.add(newbloodParticle);
        }

        for (Bullet bullet : bullets) {
            if (CheckIfOutOfMap(bullet.x, bullet.y, 0))
            {
                toDeleteBullets.add(bullet);
            }
        }

        for (Bullet bullet : toDeleteBullets) {
            bullets.remove(bullet);
        }

        for (Character enemy : toDeleteEnemys) {
            enemys.remove(enemy);
        }
    }

    private boolean canPlayerMoveToThisPosition(int x, int y)
    {
        if (x < 0 || y < 0 || x > 450 || y > 450)
        {
            return false;
        }
        return true;
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_A)) {
                if (canPlayerMoveToThisPosition(player.x - DOT_SIZE, player.y)) {player.x -= DOT_SIZE;}
            }

            if ((key == KeyEvent.VK_D)) {
                if (canPlayerMoveToThisPosition(player.x + DOT_SIZE, player.y)) {player.x += DOT_SIZE;}
            }

            if ((key == KeyEvent.VK_W)) {
                if (canPlayerMoveToThisPosition(player.x, player.y - DOT_SIZE)) {player.y -= DOT_SIZE;}
            }

            if ((key == KeyEvent.VK_S)) {
                if (canPlayerMoveToThisPosition(player.x, player.y + DOT_SIZE)) {player.y += DOT_SIZE;}
            }

            if ((key == KeyEvent.VK_SPACE)) {
                Shoot();
            }

            if ((key == KeyEvent.VK_X)) {
                System.exit(0);
            }

            if (inGame)
            {
                if ((key == KeyEvent.VK_R)) {
                    shoots = maxShoots;
                }
            }
            else
            {
                if ((key == KeyEvent.VK_R)) {
                    restart();
                }
            }
        }
    }

    public class MapListener implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                Shoot();
            }
            if (e.getButton() == MouseEvent.BUTTON3 && inGame) {
                shoots = maxShoots;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
        }
    }
}
