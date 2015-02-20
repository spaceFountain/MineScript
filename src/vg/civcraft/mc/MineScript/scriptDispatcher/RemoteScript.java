package vg.civcraft.mc.MineScript.scriptDispatcher;

import vg.civcraft.mc.MineScript.apis.RemoteAPI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by isaac on 2/11/15.
 */
public interface RemoteScript extends Remote {
    void start() throws RemoteException;
    void stop() throws RemoteException;
    void exit() throws RemoteException;
    void addAPI(RemoteAPI library, String name) throws RemoteException;
    void removeAPI(String name) throws RemoteException;
    void registerEvent() throws RemoteException;
    void addAPI(Class library) throws RemoteException;
}
