package com.zetcode;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Explosion {
    private int x;
    private int y;
    private int currentPath = 0;
    private double duration = 5;
    private double evry;
    private boolean isFinished;
    private double deathRadius = 40;

    private ArrayList<String> paths = new ArrayList<String>();
    Timer timer = new Timer(true);

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        loadPaths();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getDeathRadius() {
        return deathRadius;
    }

    private void loadPaths()
    {
        File dir = new File("src/resources/explosion");
        if (dir.isDirectory()){

            File[] file = dir.listFiles();

            for (int i=0; i<file.length; i=i+1){
                paths.add(file[i].toPath().toString());
            }
        }
        evry = duration / paths.size();
        TimerTask timerTask = new Taskn((long) evry*100);
        timer.scheduleAtFixedRate(timerTask, 0, paths.size());
    }

    public String currentPath()
    {
        return paths.get(currentPath);
    }

    public void nextPath ()
    {
        if (currentPath == paths.size()-1) {isFinished = true;}
        else
        {
            currentPath++;
        }
    }

    private class Taskn extends TimerTask
    {
        private long delay;
        public Taskn(long delay) {
            this.delay = delay;
        }
        public void run(){
            nextPath();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }
}
