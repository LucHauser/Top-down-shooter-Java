package com.zetcode;

public class Bullet {
    public int x = 0;
    public int y = 0;
    public boolean u;
    public boolean d;
    public boolean l;
    public boolean r;
    public Board board;

    public Bullet(int x, int y, boolean u, boolean d, boolean l, boolean r)
    {
        this.x = x;
        this.y = y;
        this.u = u;
        this.l = l;
        this.r = r;
        // this.board = board;
        // Taskn task = new Taskn();
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getX()
    {
        return this.x;
    }
}
