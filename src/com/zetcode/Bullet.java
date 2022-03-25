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
        this.y += (-2*Math.sin(Math.toRadians(this.rotation)));
        // System.out.println(Math.sin(Math.toRadians(90)));
        this.y += (-2*Math.sin(Math.toRadians(this.rotation)));
        // if (rotation == 0)
        // {
        //     this.x += 2 *bulletSpeed;
        //     this.y += 0;
        // }
        // else if (rotation == 45)
        // {
        //     this.x += 1 *bulletSpeed;
        //     this.y += 1 *bulletSpeed;
        // }
        // else if (rotation == 90)
        // {
        //     this.x += 0;
        //     this.y += 2 *bulletSpeed;
        // }
        // else if (rotation == 135)
        // {
        //     this.x -= 1 *bulletSpeed;
        //     this.y += 1 *bulletSpeed;
        // }
        // else if (rotation == 180)
        // {
        //     this.x -= 2 *bulletSpeed;
        //     this.y += 0 *bulletSpeed;
        // }
        // else if (rotation == 225)
        // {
        //     this.x -= 1 *bulletSpeed;
        //     this.y -= 1 *bulletSpeed;
        // }
        // else if (rotation == 270)
        // {
        //     this.x += 0;
        //     this.y -= 2 *bulletSpeed;
        // }
        // else if (rotation == 315)
        // {
        //     this.x += 1 *bulletSpeed;
        //     this.y -= 1 *bulletSpeed;
        // }
    }
}
