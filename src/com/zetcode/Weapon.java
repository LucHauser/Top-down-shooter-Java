package com.zetcode;

public class Weapon {

    private int currentWeapon = 0;
    private double[] rotation = {0, 48};
    private int[] offset = {0, 24};
    private String[] imgPath = {null, "src/resources/mg.png"};
    private int[] maxShoots = {20, 200};
    private int[] currentShoot = {20, 200};
    private int[] bulletOffset = {24, 40};
    private double[] bulletRotationOffset = {35, 23};
    private boolean[] isFUllauto = {false, true};
    private boolean[] isReloadable = {true, false};

    public int getCurrentWeapon(){
        return currentWeapon;
    }

    public void setCurrentWeapon(int currentWeapon) {
        this.currentWeapon = currentWeapon;
        if (isReloadable[currentWeapon]){currentShoot[currentWeapon] = maxShoots[currentWeapon];}
    }

    public int getOffset() {
        return offset[currentWeapon];
    }

    public double getRotation() {
        return rotation[currentWeapon];
    }

    public String getImgPath() {
        return imgPath[currentWeapon];
    }

    public int getMaxShoots() {
        return maxShoots[currentWeapon];
    }

    public int getCurrentShoot() {
        return currentShoot[currentWeapon];
    }

    public void setCurrentShoot(int currentShoot) {
        this.currentShoot[currentWeapon] = currentShoot;
    }

    public void loadShotsToNotReloadble(int index, int shots)
    {
        currentShoot[index] = shots;
    }

    public int saveShots(int index)
    {
        return currentShoot[index];
    }

    public int getBulletOffset() {
        return bulletOffset[currentWeapon];
    }

    public double getBulletRotationOffset() {
        return bulletRotationOffset[currentWeapon];
    }

    public boolean getIsFUllauto() {
        return isFUllauto[currentWeapon];
    }

    public void shot()
    {
        if (canShot()){currentShoot[currentWeapon]--;}
    }

    public boolean canShot()
    {
        if (currentShoot[currentWeapon] > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void reload()
    {
        if (isReloadable[currentWeapon])
        {
            currentShoot[currentWeapon] = maxShoots[currentWeapon];
        }
    }

    public void collectMunition(int index, int plus)
    {
        currentShoot[index] += plus;
    }
}
