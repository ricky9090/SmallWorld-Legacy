package com.ricky9090.smallworld.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadManager implements ITaskManager {

    final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    @Override
    public void postTask(Runnable task) {
        cachedThreadPool.execute(task);
    }

    @Override
    public void launch() {

    }
}
