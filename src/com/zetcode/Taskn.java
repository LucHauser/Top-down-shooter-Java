package com.zetcode;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Taskn extends TimerTask {
    public void run(){
        // Robot robot = new Robot();
        // robot.keyPress(KeyEvent.VK_H);
        // board.Update();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        System.out.println("Timer task Done ");
        run();
    }

    public static void main(String args[]) throws Exception{
        TimerTask timerTask = new Taskn(); //reference created for TimerTask class

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10); // 1.task 2.delay 3.period



        Thread.sleep(12000);

        timer.cancel();


    }
}
