package vg.civcraft.mc.MineScript.scriptDispatcher;

import vg.civcraft.mc.MineScript.RMIUtility.MethodCall;
import vg.civcraft.mc.MineScript.apis.RemoteAPI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by isaac on 2/11/15.
 */
public class Script extends UnicastRemoteObject implements RemoteScript {
    ScriptRunner runner;
    String code;
    public Script(String code, ScriptRunner runner) throws RemoteException {
        super(0);
        this.runner = runner;
        this.code = code;
        runner.queue.scheduleCall(new MethodCall(runner, "loadCode", code));
    }

    @Override
    public void start() throws RemoteException {
        System.out.println("start called");
        runner.queue.scheduleCall(new MethodCall(runner, "runScript"));
    }

    @Override
    @Deprecated
    public void stop() throws RemoteException {

    }
    @Override
    public void exit() throws RemoteException {
        runner.queue.scheduleCall(new MethodCall(System.class, "exit", new Class[]{int.class}, 0));
    }

    @Override
    public void addAPI(RemoteAPI library, String name) throws RemoteException {
        System.out.println("allowAndBind "+name);
        runner.queue.scheduleCall(new MethodCall(runner, "allowAndBind", new Class[]{RemoteAPI.class, String.class},
                library, name));
    }

    @Override
    public void addAPI(Class library) throws RemoteException {
        runner.queue.scheduleCall(new MethodCall(runner, "allow", library));
    }

    @Override
    @Deprecated
    public void removeAPI(String name) throws RemoteException {

    }

    @Override
    @Deprecated
    public void registerEvent() throws RemoteException {

    }
}
