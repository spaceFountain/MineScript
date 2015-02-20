package vg.civcraft.mc.MineScript.apis;

import vg.civcraft.mc.BlockMeta.ItemType;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by isaac on 2/14/15.
 */
public interface RemoteItemSlot extends Remote {
    ItemType getType() throws RemoteException;
    boolean addItems(ItemSlot from, int amount) throws RemoteException;
}
