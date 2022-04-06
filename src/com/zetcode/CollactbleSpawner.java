package com.zetcode;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class CollactbleSpawner {

    private int[] spawnRange = {5,25};
    private Environment env;
    private ThreadLocalRandom tlr = ThreadLocalRandom.current();
    Timer timer = new Timer(true);

    public CollactbleSpawner(Environment environment) {
        this.env = environment;
        SpawnDimond();
    }
    void SpawnDimond()
    {
        TimerTask timerTask = new Taskn(tlr.nextInt(spawnRange[0], spawnRange[1]) *1000);
        timer.scheduleAtFixedRate(timerTask, 1, 1);
    }
    private class Taskn extends TimerTask
    {
        private long delay;
        public Taskn(long delay) {
            this.delay = delay;
        }
        public void run(){
            env.SpawnCollactable();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }
}
