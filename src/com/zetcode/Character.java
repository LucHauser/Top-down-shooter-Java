package com.zetcode;

public class Character {
    public int x = 0;
    public int y = 0;
    public double rotation;
    public boolean isPlayer;
    public boolean isGrande;
    public boolean hasThrown;

    public Character(int x, int y, double rotation)
    {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

}
