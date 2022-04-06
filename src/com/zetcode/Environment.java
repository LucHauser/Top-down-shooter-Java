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
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.awt.event.MouseListener;

public class Environment extends JPanel implements ActionListener  {

    private final int B_WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private final int B_HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private final int DOT_SIZE = 25;
    private final int DELAY = 40;

    private Character player;
    private int xPlayerCenterOffset;
    private int yPlayerCenterOffset;
    private boolean inGame = true;
    private boolean ispressed = false;

    private int maxEnemys = 3;
    private int[][] defaultSpawnPoints = {{0,0},{B_WIDTH,0},{0,B_HEIGHT},{B_WIDTH,B_HEIGHT},{0,(int)(B_HEIGHT*0.5)},{(int)(B_WIDTH*0.5),0},{(int)(B_WIDTH*0.5),B_HEIGHT},{B_WIDTH,(int)(B_HEIGHT*0.5)}};
    private int mX;
    private int mY;
    private int counter = 0;
    private int kills = 0;
    private int highscore;
    private boolean savedData = false;
    private double chanceForGrande = 0.25;
    private int throwDistance = 400;
    private Weapon weapon;
    private double enemySpeed = 3;
    private double spawnDistance = 100;
    private int maxNewSpawnpoints = 3;
    private int maxDimonds = 4;
    private CollactbleSpawner collactableSpawner = null;
    private int dimondCollectDistance = 25;
    private int currentDiamonds = 0;
    private int price = 20;
    private int bought;

    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    private ArrayList<Character> enemys = new ArrayList<Character>();
    private ArrayList<Blood> bloodEffects = new ArrayList<Blood>();
    private ArrayList<Grande> grandes = new ArrayList<Grande>();
    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
    private ArrayList<Spawnpoint> spawnPoints = new ArrayList<Spawnpoint>();
    private ArrayList<Collactable> diamonds = new ArrayList<Collactable>();
    private ArrayList<Collactable> munitions = new ArrayList<Collactable>();

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
        addMouseListener(new MapListener());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        initGame();
    }

    private void initGame() {
        player = new Character(B_WIDTH/2, B_HEIGHT/2, 0);
        player.isPlayer = true;
        try {
            xPlayerCenterOffset = (int) (ImageIO.read(new File("src/resources/player.gif")).getWidth()*0.5);
            yPlayerCenterOffset = (int) (ImageIO.read(new File("src/resources/player.gif")).getHeight()*0.5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        weapon = new Weapon();
        collactableSpawner = new CollactbleSpawner(this);
        SetDefault();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
        SpawnEnemys();
    }

    private void GenerateSpawnpoint()
    {
        boolean spawn = true;
        while (spawn)
        {
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            int x = tlr.nextInt(0,B_WIDTH);
            int y = tlr.nextInt(0,B_HEIGHT);

            if (getDistance(x, y, player.x, player.y) >= spawnDistance)
            {
                Spawnpoint newSp = new Spawnpoint(x, y);
                spawnPoints.add(newSp);
                spawn = false;
            }
        }

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

    private void DrawSpawnpoints(Graphics g)
    {
        if (inGame) {
            for (Spawnpoint spawnpoint : spawnPoints) {
                Image img= Toolkit.getDefaultToolkit().getImage("src\\resources\\spawnpoint.png");
                g.drawImage(img, spawnpoint.getX(), spawnpoint.getY(), null);
                if (getDistance(player.x, player.y, spawnpoint.getX(), spawnpoint.getY()) >= spawnDistance)
                {
                    spawnpoint.setCanSpawn(true);
                }
                else
                {
                    spawnpoint.setCanSpawn(false);
                }
            }
        }
    }

    private void DrawWeapon(Graphics g)
    {
        if (inGame && weapon.getImgPath() != null) {
            try {
                BufferedImage originalImage = ImageIO.read(new File(weapon.getImgPath()));
                BufferedImage subImage = rotateImage(originalImage, player.rotation);

                int drawXPos = player.x-(int)(originalImage.getWidth()*0.5) + (int)xPlayerCenterOffset + (int)(weapon.getOffset()*Math.sin(Math.toRadians((-1*player.rotation + weapon.getRotation()))));
                int drawYPos = player.y-(int)(originalImage.getHeight()*0.5) + (int)yPlayerCenterOffset + (int)(weapon.getOffset()*Math.sin(Math.toRadians(90-(-1*player.rotation+ weapon.getRotation()))));
                g.drawImage(subImage, drawXPos, drawYPos, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void DrawGrandes(Graphics g)
    {
        if (inGame) {
            ArrayList<Grande> grandesToDestroy = new ArrayList<Grande>();
            for (Grande grande : grandes) {
                try {
                    if (grande.getThrowForce() > 0)
                    {
                        double newRoation = grande.getShownRotation() + 3;
                        grande.setShownRotation(newRoation);
                    }
                    BufferedImage originalImage = ImageIO.read(new File("src/resources/grande1.png"));
                    grande.setRotation(grande.getRotation()+2);
                    BufferedImage subImage = rotateImage(originalImage, grande.getShownRotation());
                    grande.updatePostion();
                    grande.UpDateLifeTime();
                    g.drawImage(subImage, grande.getX()- (int) (subImage.getWidth()*0.5), grande.getY()- (int) (subImage.getWidth()*0.5), this);

                    if (grande.getLiefetime() <= 0)
                    {
                        SpawnExplosion(grande);
                        grandesToDestroy.add(grande);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (Grande grande : grandesToDestroy) {
                grandes.remove(grande);
            }
        }
    }

    private void DrawExplosions(Graphics g)
    {
        if (inGame) {
            ArrayList<Explosion> explosionsToDestroy = new ArrayList<Explosion>();
            ArrayList<Character> enemysToDestroy = new ArrayList<Character>();
            for (Explosion explosion : explosions) {
                try {
                    BufferedImage originalImage = ImageIO.read(new File(explosion.currentPath()));
                    BufferedImage subImage = rotateImage(originalImage, 0);
                    g.drawImage(subImage, explosion.getX()- (int) (subImage.getWidth()*0.5), explosion.getY()-(int) (subImage.getHeight()*0.5), this);

                    if (explosion.isFinished())
                    {
                        explosionsToDestroy.add(explosion);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (getDistance(player.x+xPlayerCenterOffset, player.y+yPlayerCenterOffset, explosion.getX(), explosion.getY()) < explosion.getDeathRadius())
                {
                    inGame = false;
                }

                for (Character enemy : enemys) {
                    if (getDistance(enemy.x, enemy.y, explosion.getX(), explosion.getY()) < explosion.getDeathRadius())
                    {
                        enemysToDestroy.add(enemy);
                    }
                }
            }
            for (Explosion explosion : explosionsToDestroy) {
                explosions.remove(explosion);
            }
            for (Character enemy : enemysToDestroy) {
                enemys.remove(enemy);
            }
        }
    }

    private Double getDistance(int x1, int y1, int x2, int y2)
    {
        int distX = (int) Math.pow(x1 - x2, 2);
        int distY = (int) Math.pow(y1 - y2, 2);

        return Math.sqrt(distX + distY);
    }

    private void DrawEnemys(Graphics g)
    {
        try {
            checkPlayerCollsison(g);

            for (Character enemy : enemys) {
                enemy.y -= (enemySpeed*Math.sin(Math.toRadians(enemy.rotation-90)));
                enemy.x -= (enemySpeed*Math.sin(Math.toRadians(enemy.rotation)));

                double dx = player.x - enemy.x;
                double dy = player.y - enemy.y;
                enemy.rotation = (Math.toDegrees(Math.atan2(dy, dx))-90);

                BufferedImage originalImage = null;

                if (!enemy.isGrande)
                {
                    originalImage = ImageIO.read(new File("src/resources/enemy.png"));
                }
                else
                {
                    originalImage = ImageIO.read(new File("src/resources/enemyWithGrande.png"));
                }

                BufferedImage subImage = rotateImage(originalImage, enemy.rotation);
                g.drawImage(subImage, enemy.x, enemy.y, this);

                if (enemy.isGrande && !enemy.hasThrown)
                {
                    if (getDistance(player.x, player.y, enemy.x, enemy.y) < throwDistance)
                    {
                        SpawnGrandes(enemy);
                        enemy.hasThrown = true;
                    }
                }
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
            Image bgTile= Toolkit.getDefaultToolkit().getImage("src\\resources\\gTile.jpg");
            for (int i = 0; i < B_WIDTH; i += 250)
            {
                for (int j = 0; j < B_HEIGHT; j += 250)
                {
                    g.drawImage(bgTile, i, j, null);
                }
            }

            DrawSpawnpoints(g);

            try {
                Point mousPos = MouseInfo.getPointerInfo().getLocation();
                mX = (int) mousPos.getX();
                mY = (int) mousPos.getY();

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (screenSize.width/ 2) - B_WIDTH/2;
                int y = (screenSize.height/ 2) - B_HEIGHT/2;
                mX -= x;
                mY -= y-20;
                double dx = mX - player.x - xPlayerCenterOffset;
                double dy = mY - player.y - yPlayerCenterOffset;
                player.rotation = (Math.toDegrees(Math.atan2(dy, dx)));

                BufferedImage originalImage = ImageIO.read(new File("src/resources/player.gif"));
                BufferedImage subImage = rotateImage(originalImage, player.rotation);
                g.drawImage(subImage, player.x, player.y, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DrawEnemys(g);
            DrawGrandes(g);
            DrawBullets(g);
            DrawWeapon(g);
            DrawExplosions(g);

            Image dimondImg= Toolkit.getDefaultToolkit().getImage("src\\resources\\dimond.png");
            ArrayList<Collactable> diamondsToDestroy = new ArrayList<Collactable>();
            for (Collactable diamond : diamonds) {
                g.drawImage(dimondImg, diamond.getX()-(int)(dimondImg.getWidth(null)*0.5), diamond.getY()-(int)(dimondImg.getHeight(null)*0.5), null);
                if (getDistance(player.x+xPlayerCenterOffset, player.y+yPlayerCenterOffset, diamond.getX(), diamond.getY()) <= dimondCollectDistance)
                {
                    diamondsToDestroy.add(diamond);
                    currentDiamonds++;
                }
            }

            for (Collactable dimond : diamondsToDestroy) {
                diamonds.remove(dimond);
            }

            Image munitionImg= Toolkit.getDefaultToolkit().getImage("src\\resources\\munition.png");
            ArrayList<Collactable> munitionToDestroy = new ArrayList<Collactable>();
            for (Collactable munition : munitions) {
                g.drawImage(munitionImg, munition.getX()-(int)(munitionImg.getWidth(null)*0.5), munition.getY()-(int)(munitionImg.getHeight(null)*0.5), null);
                if (getDistance(player.x+xPlayerCenterOffset, player.y+yPlayerCenterOffset, munition.getX(), munition.getY()) <= dimondCollectDistance)
                {
                    diamondsToDestroy.add(munition);
                    currentDiamonds++;
                }
            }

            for (Collactable munition : munitionToDestroy) {
                diamonds.remove(munition);
            }
            checkMunitionCollsison(g);

            String killsText = "kills: " + kills;
            String shotsText = "";
            String diamondsText = "Diamonds: " + currentDiamonds;
            Font small = new Font("Helvetica", Font.BOLD, 14);
            FontMetrics metr = getFontMetrics(small);

            g.setColor(Color.yellow);
            g.setFont(small);
            g.drawString(killsText, (B_WIDTH - metr.stringWidth(killsText)) / 2, 20);
            if (weapon.canShot())
            {
                shotsText = "shots: " + weapon.getCurrentShoot();
                g.setColor(Color.white);
            }
            else
            {
                shotsText = "Press R to reload";
                g.setColor(Color.red);
            }
            g.drawString(shotsText, (B_WIDTH - metr.stringWidth(shotsText)), 20);
            g.setColor(new Color(255, 48, 16));
            g.drawString(diamondsText, (B_WIDTH - metr.stringWidth(diamondsText)), 60);

            if (ispressed)
            {
                Shoot();
            }
            Toolkit.getDefaultToolkit().sync();
        } else {

            gameOver(g);
        }
    }

    public void SpawnCollactable()
    {
        getAndSaveData(2, true);
        if (bought == 1)
        {
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            munitions.add(new Collactable(tlr.nextInt(100, B_WIDTH-100), tlr.nextInt(100, B_HEIGHT-100)));
        }
        else if (diamonds.size() <= maxDimonds)
        {
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            diamonds.add(new Collactable(tlr.nextInt(100, B_WIDTH-100), tlr.nextInt(100, B_HEIGHT-100)));
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
        String score = "SCORE: " + kills;
        if (!savedData)
        {
            savedData = true;
            getAndSaveData(0, false);
            getAndSaveData(1, false);
            getAndSaveData(3, false);
        }
        String dimondsText = "Diamonds: " + currentDiamonds;
        String highscoreText = "HIGHSCORE: " + highscore;
        String restartText = "Press R to restart";
        String exitText = "Press X to exit the game";
        String gunText = "MINI GUN";
        String priceText = price + " Diamonds";
        String buyText = "Press B to buy";
        Font big = new Font("Helvetica", Font.BOLD, 50);
        FontMetrics metr = getFontMetrics(big);
        Font small = new Font("Helvetica", Font.BOLD, 14);
        Font midle = new Font("Helvetica", Font.BOLD, 30);
        FontMetrics metrs = getFontMetrics(small);
        FontMetrics midlemetrs = getFontMetrics(midle);

        g.setColor(Color.white);
        g.setFont(big);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 -260);
        g.setFont(small);
        g.setColor(Color.white);
        g.drawString(restartText, (B_WIDTH - metrs.stringWidth(restartText)) / 2, B_HEIGHT / 2 + 200);
        g.drawString(exitText, (B_WIDTH - metrs.stringWidth(exitText)) / 2, B_HEIGHT / 2 + 250);
        g.setFont(midle);
        g.setColor(Color.white);
        g.drawString(score, (B_WIDTH - midlemetrs.stringWidth(score)) / 2, B_HEIGHT / 2 -180);
        g.drawString(highscoreText, (B_WIDTH - midlemetrs.stringWidth(highscoreText)) / 2, B_HEIGHT / 2 - 140);
        g.drawString(dimondsText, (B_WIDTH - midlemetrs.stringWidth(dimondsText)) / 2, B_HEIGHT / 2 - 100);
        g.setFont(small);
        Image gun= Toolkit.getDefaultToolkit().getImage("src\\resources\\mg.png");
        Image bgGun = Toolkit.getDefaultToolkit().getImage("src\\resources\\bgGun.png");
        g.drawImage(bgGun, (int)(B_WIDTH*0.5-bgGun.getWidth(null)/2), (int)(B_HEIGHT*0.5-bgGun.getHeight(null)/2), bgGun.getWidth(null), bgGun.getHeight(null), null);
        g.drawImage(gun, (int)(B_WIDTH*0.5-gun.getWidth(null)*3/2-30), (int)(B_HEIGHT*0.5-gun.getHeight(null)*3/2), gun.getWidth(null)*3, gun.getHeight(null)*3, null);
        g.setColor(new Color(183, 94, 255));
        g.drawString(gunText, (B_WIDTH - midlemetrs.stringWidth(gunText)+10) / 2, B_HEIGHT / 2 - 30);
        g.setColor(new Color(131, 94, 255));
        g.drawString(priceText, (B_WIDTH - midlemetrs.stringWidth(priceText)+70) / 2, B_HEIGHT / 2 + 45);
        g.setColor(new Color(255, 164, 94));
        g.drawString(buyText, (B_WIDTH - midlemetrs.stringWidth(buyText)+100) / 2, B_HEIGHT / 2 + 100);
    }

    private void SetDefault()
    {
        kills = 0;
        getAndSaveData(1, true);
        getAndSaveData(3, true);
        weapon.reload();;
        player.x = B_WIDTH/2;
        player.x = B_HEIGHT/2;
        bullets.clear();
        enemys.clear();
        grandes.clear();
        explosions.clear();
        spawnPoints.clear();
        diamonds.clear();
        munitions.clear();
        Character fEnemy = new Character(100, B_WIDTH/2, -90);
        enemys.add(fEnemy);
        inGame = true;
        savedData = false;
        maxEnemys = 9;
        getAndSaveData(2, true);
        for (int[] spawnPoint : defaultSpawnPoints) {
            spawnPoints.add(new Spawnpoint(spawnPoint[0], spawnPoint[1]));
        }
        for (int i = 0; i < maxNewSpawnpoints; i++) {
            GenerateSpawnpoint();
        }
    }

    void saveToFile(ArrayList<Integer> values, File stateFile)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(stateFile));
            String newValue = "";
            int cIndex = 0;
            for (Integer value : values) {
                newValue += Integer.valueOf(value).toString();

                if (values.size()-1 != cIndex)
                {
                    newValue += ";";
                }
                cIndex++;
            }
            writer.write(newValue);
            writer.close();
        } catch(IOException e){
            System.out.println("Error: "+e);
        }
    }

    void getAndSaveData(int index, boolean read)
    {
        File stateFile = new File(System.getProperty("user.dir") + "\\src\\resources\\state.txt");
        try{
            if(!stateFile.exists()){
                stateFile.createNewFile();
                BufferedWriter wirter = new BufferedWriter(new FileWriter(stateFile));
                //highscore;diamonds;gun/ammo
                wirter.write("0;0;0;0");
                wirter.close();
            }
        } catch(IOException e){
            System.out.println("Error: "+e);
        }

        ArrayList<Integer> values = new ArrayList<Integer>();

        try {
            values = getValues(stateFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (index) {
            case 0:
                Integer SavedHighscore = values.get(index);
                if (SavedHighscore < kills)
                {
                    values.set(index, kills);
                    highscore = kills;
                }
                else
                {
                    highscore = SavedHighscore;
                }
                break;
            case 1:
                if (read) {currentDiamonds = values.get(index);}
                else{
                    values.set(index, currentDiamonds);
                }
                break;
            case 2:
                if (read) {bought = values.get(index);}
                else{
                    values.set(index, 1);
                }
                break;
            case 3:
                if (values.get(2) == 1)
                {
                    if (read) {weapon.loadShotsToNotReloadble(1, values.get(index));}
                    else{
                        values.set(index, weapon.saveShots(1));
                    }
                }
                break;
            default:
                System.out.println("Error with switch");
                break;
        }
        saveToFile(values, stateFile);
    }

    public ArrayList<Integer> getValues(File file) throws IOException{
        Scanner scanner = new Scanner(file);
        String[] tokens = scanner.nextLine().split(";");
        ArrayList<Integer> values = new ArrayList<Integer>();
        for (String value : tokens) {
            values.add(Integer.parseInt(value));
        }
        scanner.close();
        return values;
    }

    private void Shoot()
    {
        if (weapon.canShot())
        {
            Bullet bullet = new Bullet(player.x + xPlayerCenterOffset, player.y + yPlayerCenterOffset, player.rotation*-1);
            bullets.add(bullet);

            weapon.shot();

            bullet.y += (-weapon.getBulletOffset()*Math.sin(Math.toRadians(bullet.rotation-weapon.getBulletRotationOffset())));
            bullet.x += (weapon.getBulletOffset()*Math.sin(Math.toRadians(90-bullet.rotation+weapon.getBulletRotationOffset())));
        }
    }

    private void SpawnEnemys()
    {
        if (enemys.size() < maxEnemys)
        {
            boolean usableSpawonpoint = false;
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            Spawnpoint sp = null;
            while (!usableSpawonpoint)
            {
                int randomIndex = tlr.nextInt(0, spawnPoints.size());
                sp = spawnPoints.get(randomIndex);
                if (sp.canSpawn())
                {
                    usableSpawonpoint = true;
                }
            }
            Character newEnemy = new Character(sp.getX(), sp.getY(), 0);
            double chance = tlr.nextDouble();
            if (chance < chanceForGrande)
            {
                newEnemy.isGrande = true;
            }
            enemys.add(newEnemy);
            counter++;

            if (counter >= 20)
            {
                counter = 0;
                maxEnemys++;
            }
        }
    }

    private void SpawnGrandes(Character thrower)
    {
        if (thrower.isPlayer)
        {
            Grande newGrande = new Grande(thrower.x+xPlayerCenterOffset, thrower.y+yPlayerCenterOffset, thrower.rotation*-1);
            grandes.add(newGrande);
        }
        else
        {
            Grande newGrande = new Grande(thrower.x, thrower.y, thrower.rotation*-1-90);
            grandes.add(newGrande);
        }
    }

    private void SpawnExplosion(Grande grande)
    {
        Explosion newExplosion = new Explosion(grande.getX(), grande.getY());
        explosions.add(newExplosion);
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

    private void checkMunitionCollsison(Graphics g)
    {
        Sprite playeSprite = new Sprite(player.x+xPlayerCenterOffset, player.y+yPlayerCenterOffset, "src/resources/player.gif");
        Rectangle playerRect = playeSprite.getBounds();
        ArrayList<Collactable> toDeleteMunitions = new ArrayList<Collactable>();
        for (Collactable muniton : munitions) {
            Sprite munitionSprite = new Sprite(muniton.getX(), muniton.getY(), "src/resources/munition.png");

            Rectangle munitionRect = munitionSprite.getBounds();
            if (munitionRect.intersects(playerRect)) {
                toDeleteMunitions.add(muniton);
                weapon.collectMunition(1, 100);
            }
        }

        for (Collactable munition : toDeleteMunitions) {
            munitions.remove(munition);
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
        if (x < 0 || y < 0 || x > B_WIDTH-2*DOT_SIZE || y > B_HEIGHT-2*DOT_SIZE)
        {
            return false;
        }
        return true;
    }

    public void addDimond(Collactable d)
    {
        if (diamonds.size() >= maxDimonds) {return;}
        diamonds.add(d);
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

            if ((key == KeyEvent.VK_X)) {
                System.exit(0);
            }

            if ((key == KeyEvent.VK_G)) {
                SpawnGrandes(player);
            }
            if ((key == KeyEvent.VK_1)) {
                getAndSaveData(3, false);
                weapon.setCurrentWeapon(0);
            }

            if ((key == KeyEvent.VK_2) && bought == 1) {
                weapon.setCurrentWeapon(1);
            }

            if (inGame)
            {
                if ((key == KeyEvent.VK_R)) {
                    weapon.reload();
                }
            }
            else
            {
                if ((key == KeyEvent.VK_R)) {
                    SetDefault();
                }

                getAndSaveData(2, true);
                if ((key == KeyEvent.VK_B) && currentDiamonds >= price && bought == 0)
                {
                    currentDiamonds -= price;
                    getAndSaveData(1, false);
                    getAndSaveData(2, false);
                }
            }
        }
    }

    public class MapListener implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if(weapon.getIsFUllauto()){
                    ispressed = true;
                } else {
                    Shoot();
                }
            }
            if (e.getButton() == MouseEvent.BUTTON3 && inGame) {
                weapon.reload();
            }
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            if (arg0.getButton() == MouseEvent.BUTTON1) {
                if(weapon.getIsFUllauto()){
                    ispressed = false;
                }
            }
        }
    }
}
