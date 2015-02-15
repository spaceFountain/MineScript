package vg.civcraft.mc.MineScript.scriptDispatcher;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;

/**
 * The main class to handle managing and interfacing with a script.
 */
public class ScriptManager {
    private Script script;
    private long maxRam;
    private ScriptRunner runner;
    int id;
    int tenthsOfPercentCPU;
    RemoteScriptStarter starter;
    public boolean sleeping = false;

    private ScriptManager(String code, int tenthsOfPercentCPU, long maxRam, String callback, int port, int id) {
        runner = new ScriptRunner();
        runner.start();

        try {
            script = new Script(code, runner);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.tenthsOfPercentCPU = tenthsOfPercentCPU;
        this.maxRam = maxRam;
        this.id = id;
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", port);
            RemoteScriptStarter starter = (RemoteScriptStarter) registry.lookup(callback);

            starter.scriptCreated(script, id);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    private void throttleCPU() {
        long CPUDelta = getCPUTimeDelta();
        long timeDelta = getTimeDelta();
        if (CPUDelta == 0) {
            return; //this would mean no time has passed since the last time we check.
        }

        int targetSleep = (int) ((1000 * CPUDelta) / tenthsOfPercentCPU - timeDelta);
        if (targetSleep > 0) {
            /*int priority = runner.getPriority();
            --priority;
            if (Thread.MIN_PRIORITY < priority && priority < Thread.MAX_PRIORITY ) {
                //runner.setPriority(priority);
            }*/
            try {
                runner.suspend(); //should be safe as the main thread never uses locks
                Thread.sleep(targetSleep / 1000000 + 1, targetSleep % 1000000); // Probably will end up sleeping much more
                runner.resume();
            } catch (InterruptedException e) {

            }
        } else {
            /*int priority = runner.getPriority();
            /++priority;
            if (Thread.MIN_PRIORITY > priority || priority > Thread.MAX_PRIORITY) {
                //runner.setPriority(priority); //don't want to bump this up too much
            }*/
        }


    }

    private long lastCPUTime = 0;
    private long getCPUTimeDelta (){
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();

        if (!bean.isThreadCpuTimeSupported())
            throw new IllegalStateException("most be run on a machine with cpu time");

        long time = bean.getThreadCpuTime(runner.getId());
        if (time < 0) {
            return 0;
        }

        long CPUDelta = time - lastCPUTime;
        lastCPUTime = time;
        return CPUDelta;
    }

    private long lastTime = System.nanoTime();
    private long getTimeDelta() {
        long time = System.nanoTime();
        long delta = time - lastTime;
        lastTime = time;
        return delta;
    }


    private void throttleRAM() {

    }

    private void loop() {
        throttleCPU();
        throttleRAM();
    }

    // <code> <tenthsOfPercentCPU> <maxRam> <callback> <port> <id>
    public static void main(String[] args) {
        System.out.println("launched new process");
        String callback, code;
        int id, tenthsOfPercentCPU, port;
        long maxRam;

        if (args.length < 6) {
            throw new IllegalArgumentException("ScriptManager can only be started through ScriptManager.start()");
        }

        code = args[0];
        callback = args[3];
        try {
            tenthsOfPercentCPU = Integer.parseInt(args[1]);

            maxRam = Long.parseLong(args[2]);
            port = Integer.parseInt(args[4]);
            id = Integer.parseInt(args[5]);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("ScriptManager can only be started through ScriptManager.start()");
        }
        ScriptManager manager = new ScriptManager(code, tenthsOfPercentCPU, maxRam, callback, port, id);

        while (true) {
            manager.loop();

            try {
                Thread.sleep(1); //I'm not sure how long to sleep for.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void start(String code, int tenthsOfPercentCPU, long maxRam, String callback, int port, int id) {
        long RAMAsK = maxRam/1024;
        RAMAsK += 1;
        if (RAMAsK < 2048) {
            RAMAsK = 2048;
        }
        System.out.println("starting "+id);
        ProcessBuilder builder = new ProcessBuilder();
        builder. redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        builder.command("java", "-cp", System.getProperty("java.class.path"), "-Xmx" + Long.toString(RAMAsK) + "k",
                "vg.civcraft.mc.MineScript.scriptDispatcher.ScriptManager", code, Integer.toString(tenthsOfPercentCPU), Long.toString(maxRam), callback,
                Integer.toString(port), Integer.toString(id));
        try {
            Process process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
