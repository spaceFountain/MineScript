package vg.civcraft.mc.MineScript.apis;

import java.rmi.RemoteException;
import java.util.logging.Level;

/**
 * Created by isaac on 2/14/15.
 */
public interface RemoteLogger extends RemoteAPI {
    void log(Level l, String message) throws RemoteException;
    void severe(String message) throws RemoteException;
    void warning(String message) throws RemoteException;
    void info(String message) throws RemoteException;
    void config(String message) throws RemoteException;
    void fine(String message) throws RemoteException;
    void finer(String message) throws RemoteException;
    void finest(String message) throws RemoteException;
}
