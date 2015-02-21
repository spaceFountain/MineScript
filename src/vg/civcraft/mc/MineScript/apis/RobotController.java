package vg.civcraft.mc.MineScript.apis;

import vg.civcraft.mc.BlockMeta.BlockType;
import vg.civcraft.mc.BlockMeta.ItemType;

import java.rmi.RemoteException;

/**
 * A interface to control an in game robot.
 */
public interface RobotController extends RemoteAPI {
    boolean moveTo(Location location) throws RemoteException;
    boolean move() throws RemoteException;

    void turnTo(Direction direction) throws RemoteException;

    boolean placeBlock(int id) throws RemoteException;
    BlockType getBlockType(Direction side) throws RemoteException;

    void switchSlots(int a, int b) throws RemoteException;
    void moveItems(int from, int to, int amount) throws RemoteException;

    void equip(int slotID) throws RemoteException;

    int getAmount(int slotID) throws RemoteException;
    ItemType getType(int slotID) throws RemoteException;
}
