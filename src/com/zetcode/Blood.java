package com.zetcode;

public class Blood {
    private int x;
    private int y;
    private double liefetime;

    public Blood(int x, int y, double liefetime) {
        this.x = x;
        this.y = y;
        this.liefetime = liefetime;
    }

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

    public double getLiefetime() {
        return liefetime;
    }

    public void setLiefetime(double liefetime) {
        this.liefetime = liefetime;
    }

    public void UpDateLifeTime()
    {
        this.liefetime -= 1;
    }

}
