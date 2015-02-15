package vg.civcraft.mc.MineScript.scriptDispatcher;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by isaac on 2/11/15.
 */
public interface RemoteScriptStarter extends Remote{
    int start(String code, int tenthsOfPercentCPU, long maxRAM)  throws RemoteException;
    RemoteScript getScript(int id) throws RemoteException;
    void scriptCreated(RemoteScript script, int id)  throws RemoteException;
}
