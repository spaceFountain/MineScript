package vg.civcraft.mc.MineScript.scriptDispatcher;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by isaac on 2/11/15.
 */
public class ScriptStarter extends UnicastRemoteObject implements RemoteScriptStarter {
    public static final int port = 1099;
    public volatile RemoteScript[] scripts;
    public volatile Set<ScriptManager> managers;
    private long maxRAM; // todo: implement these options
    private int maxScripts;
    private int nextID = 0;

    /**
     * Creates a script starter class and begins listening for requests
     * <p>
     *     Designed to be called only from the main function of ScriptStarter. Where the amount of ram and the max
     *     number of scripts can be specified as arguments.
     * </p>
     * @param maxRAM maxRAM will eventually specify the max amount of ram the ScriptStarter will allow to be assigned.
     * @param maxScripts maxScripts specifies the maximum number of scripts that may run at once. If more than this value
     *                   is added they will be ignored and -1 will be returned
     */
    public ScriptStarter(long maxRAM, int maxScripts)  throws RemoteException {
        super(0);
        scripts = new RemoteScript[maxScripts];
        managers = new HashSet<ScriptManager>();
        this.maxScripts = maxScripts;
        this.maxRAM = maxRAM;

    }

    //
    public static void main(String[] args) throws MalformedURLException, RemoteException {
        long maxRAM;
        int maxScripts;
        try {
            maxRAM = Long.parseLong(args[0]);
            maxScripts = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("Script starter takes a <maxRAM> and <maxScripts>");
            e.printStackTrace();
            return;
        }

        ScriptStarter starter = new ScriptStarter(maxRAM, maxScripts);


        try {
            LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        Naming.rebind("//localhost/ScriptStarter", starter);
    }

    @Override
    public int start(String code, int tenthsOfPercentCPU, long maxRAM)  throws RemoteException {
        if (managers.size() + 1 >= maxScripts) {
            return -1; //if we're past the limit don't create a new manager
        }

        ScriptManager.start(code, tenthsOfPercentCPU, maxRAM, "ScriptStarter", port, nextID++);

        return nextID-1;
    }

    @Override
    public RemoteScript getScript(int id) throws RemoteException {
        System.out.println("getting "+id);
        return scripts[id];
    }

    @Override
    public void scriptCreated(RemoteScript script, int id) throws RemoteException {
        System.out.println("setting "+id);
        scripts[id] = script;
    }
}
