package com.zetcode;


public class Weapon {

    private int x;
    private int y;
    private double rotation;
    private String imgPath = "src/resources/mg.png";

    // public Weapon(int x, int y, double rotation) {
    //     this.x = x;
    //     this.y = y;
    //     this.rotation = rotation;
    // }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getRotation() {
        return rotation;
    }

    public String getImgPath() {
        return imgPath;
    }
}
