package com.ricky9090.smallworld.policy;

import com.ricky9090.smallworld.SmallInterpreter;
import com.ricky9090.smallworld.task.CachedThreadManager;
import com.ricky9090.smallworld.task.ITaskManager;

public class TaskPolicy {

    public static ITaskManager provideTaskManager(SmallInterpreter interpreter) {
        //return new FoobarManager();
        return new CachedThreadManager();
        //return new SingleThreadManager(interpreter);
    }
}
