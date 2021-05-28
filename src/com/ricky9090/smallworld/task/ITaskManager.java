package com.ricky9090.smallworld.task;

public interface ITaskManager {

    void postTask(Runnable task);

    void launch();
}
