package com.zetcode;

import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Sprite {

    private String path = "";
    private int x, y;
    private Image image;
    boolean visible;
    private int width, height;



    public Sprite(int x, int y, String path) {
        this.path = path;
        ImageIcon im = new ImageIcon(path);
        image = im.getImage();
        visible = true;
        width = image.getWidth(null);
        height = image.getHeight(null);
        this.x = x;
        this.y = y;
    }

    //rest of your getters/setters

    //this will be used for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }


    // public void move() {
    //     //move code goes here
    // }
}
