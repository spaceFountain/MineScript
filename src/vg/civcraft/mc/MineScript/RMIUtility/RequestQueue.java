package vg.civcraft.mc.MineScript.RMIUtility;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by isaac on 2/12/15.
 */
public class RequestQueue {
    volatile private List<MethodCall> methods = new LinkedList<MethodCall>();
    volatile private Thread thread = null;

    public synchronized void scheduleCall(MethodCall call) {
        methods.add(call);
        if (thread != null) {
            thread.interrupt();
        }
    }

    public void waitAndInvoke() {
        try {
            thread = Thread.currentThread();
            Thread.sleep(500);
        } catch (InterruptedException e) {} //just continue if we get interrupted
        invoke();
    }

    public synchronized void invoke() {
        for (MethodCall call: methods) {
            call.invoke();
        }
        methods.clear();
        try {
            thread = Thread.currentThread();
            Thread.sleep(1000);
        } catch (InterruptedException e) {} //just continue if we get interrupted
    }

}
