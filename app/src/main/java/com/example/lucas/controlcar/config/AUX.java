package com.example.lucas.controlcar.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lucas on 01/11/17.
 */

public abstract class AUX {
    protected BlockingQueue<ObdCommandJob> jobsQueue = new LinkedBlockingQueue<>();
    // Run the executeQueue in a different thread to lighten the UI thread
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                executeQueue();
            } catch (InterruptedException e) {
                t.interrupt();
            }
        }
    });

    abstract protected void executeQueue() throws InterruptedException;
}
