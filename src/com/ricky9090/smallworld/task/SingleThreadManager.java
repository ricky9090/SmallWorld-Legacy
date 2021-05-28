package com.ricky9090.smallworld.task;

import com.ricky9090.smallworld.SmallInterpreter;

import java.util.LinkedList;
import java.util.List;

public class SingleThreadManager extends Thread implements ITaskManager {

    public static final Object taskLock = new Object();
    private boolean running = true;

    private final List<Runnable> taskQueue = new LinkedList<>();

    private final SmallInterpreter theInterpreter;

    public SingleThreadManager(SmallInterpreter interpreter) {
        theInterpreter = interpreter;
    }

    @Override
    public void postTask(Runnable task) {
        synchronized (taskLock) {
            taskQueue.add(task);
            //System.out.println("posted a task");
            taskLock.notifyAll();
        }
    }

    private Runnable pullOneTask() {
        synchronized (taskLock) {
            try {
                if (taskQueue.size() == 0) {
                    //System.out.println("wait for task");
                    taskLock.wait();  // block
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("pull one task");
            return taskQueue.remove(0);
        }
    }



    @Override
    public void run() {
        while(running) {
            Runnable task = pullOneTask();  // may block
            //System.out.println("run task");
            task.run();
        }
    }

    @Override
    public void launch() {
        start();
    }
}
