package com.zetcode;

public class Weapon {

    private int currentWeapon = 0;
    private double[] rotation = {0, 48};
    private int[] offset = {0, 24};
    private String[] imgPath = {null, "src/resources/mg.png"};
    private int[] maxShoots = {20, 200};
    private int currentShoot = 200;
    private int[] bulletOffset = {24, 40};
    private double[] bulletRotationOffset = {35, 23};
    private boolean[] isFUllauto = {false, true};

    public int getCurrentWeapon(){
        return currentWeapon;
    }

    public void setCurrentWeapon(int currentWeapon) {
        this.currentWeapon = currentWeapon;
        currentShoot = maxShoots[currentWeapon];
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
        return currentShoot;
    }

    public void setCurrentShoot(int currentShoot) {
        this.currentShoot = currentShoot;
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
}
