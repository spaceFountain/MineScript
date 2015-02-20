package vg.civcraft.mc.MineScript.apis;

import vg.civcraft.mc.BlockMeta.BlockType;

import java.rmi.RemoteException;

/**
 * A interface to control an in game robot.
 */
public interface RobotController extends RemoteAPI {
    boolean moveTo(Location location) throws RemoteException;
    boolean move() throws RemoteException;

    void turnTo(Direction direction) throws RemoteException;

    boolean placeBlock(RemoteItemSlot slot) throws RemoteException;
    boolean placeBlock(int id) throws RemoteException;
    BlockType getBlockType(Direction side) throws RemoteException;

    void switchSlots(int a, int b) throws RemoteException;
    void switchSlots(RemoteItemSlot a, RemoteItemSlot b) throws RemoteException;
    void moveItems(RemoteItemSlot from, RemoteItemSlot to, int amount) throws RemoteException;
    void moveItems(int from, int to, int amount) throws RemoteException;

    RemoteItemSlot getSlot(int id) throws RemoteException;
    int getSlotID(RemoteItemSlot slot) throws RemoteException;

    void equip(int id) throws RemoteException;
    void equip(RemoteItemSlot slot) throws RemoteException;
}
