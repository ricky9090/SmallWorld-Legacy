package com.ricky9090.smallworld.task;

public class FoobarManager implements ITaskManager {

    @Override
    public void postTask(Runnable task) {
        new Thread(task).start();
    }

    @Override
    public void launch() {

    }
}
