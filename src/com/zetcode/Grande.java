package com.zetcode;

public class Grande {
    private int x;
    private int y;
    private double rotation;
    private double shownRotation = rotation;
    private double liefetime = 50;
    private double throwForce = 12;
    private double gravity = 0.9;

    public double getShownRotation() {
        return shownRotation;
    }

    public void setShownRotation(double shownRotation) {
        this.shownRotation = shownRotation;
    }

    public double getThrowForce() {
        return throwForce;
    }

    public void setThrowForce(double throwForce) {
        this.throwForce = throwForce;
    }

    public Grande(int x, int y, double rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public double getLiefetime() {
        return liefetime;
    }

    public void setLiefetime(double liefetime) {
        this.liefetime = liefetime;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
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

    public void UpDateLifeTime()
    {
        this.liefetime -= 1;
    }

    public void updatePostion()
    {
        if (throwForce > 0)
        {
            this.y += throwForce*(-2*Math.sin(Math.toRadians(this.rotation)));
            this.x += throwForce*(2*Math.sin(Math.toRadians(90-this.rotation)));
            throwForce -= gravity;
        }
    }
}
