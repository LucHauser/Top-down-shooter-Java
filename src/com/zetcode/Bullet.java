package com.zetcode;

public class Bullet {
    public int x = 0;
    public int y = 0;
    public double rotation;
    public double xOffset = 0;
    public double yOffet = 0;

    private double bulletSpeed = 3;

    public Bullet(int x, int y, double rotation)
    {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }
    public void updatePostion()
    {
        this.y += bulletSpeed*(-2*Math.sin(Math.toRadians(this.rotation)));
        this.x += bulletSpeed*(2*Math.sin(Math.toRadians(90-this.rotation)));
    }
}
