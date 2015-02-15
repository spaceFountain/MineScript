package vg.civcraft.mc.MineScript.apis;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;

/**
 * Created by isaac on 2/14/15.
 */
public class Logger extends UnicastRemoteObject implements RemoteLogger {
    java.util.logging.Logger logger;
    public Logger(java.util.logging.Logger logger) throws RemoteException {
        super(0);
        this.logger = logger;
    }

    @Override
    public void log(Level l, String message) {
        logger.log(l, message);
    }

    @Override
    public void severe(String message) {
        log(Level.SEVERE, message);
    }

    @Override
    public void warning(String message) {
        log(Level.WARNING, message);
    }

    @Override
    public void info(String message) {
        log(Level.INFO, message);
    }

    @Override
    public void config(String message) {
        log(Level.SEVERE, message);
    }

    @Override
    public void fine(String message) {
        log(Level.FINE, message);
    }

    @Override
    public void finer(String message) {
        log(Level.FINER, message);
    }

    @Override
    public void finest(String message) {
        log(Level.FINEST, message);
    }
}
