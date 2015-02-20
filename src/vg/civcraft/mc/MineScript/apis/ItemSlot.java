package vg.civcraft.mc.MineScript.apis;

import vg.civcraft.mc.BlockMeta.ItemType;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by isaac on 2/19/15.
 */
public class ItemSlot extends UnicastRemoteObject implements RemoteItemSlot{

    public ItemSlot() throws RemoteException {
        super(0);
    }

    @Override
    public ItemType getType() throws RemoteException {
        return null;
    }

    @Override
    public boolean addItems(ItemSlot from, int amount) throws RemoteException {
        return false;
    }
}
