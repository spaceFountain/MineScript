package vg.civcraft.mc.MineScript.apis;

import java.rmi.RemoteException;

/**
 * A interface to control an in game robot.
 */
public interface RobotController extends RemoteAPI {
    boolean moveTo(Location location) throws RemoteException;
    boolean move() throws RemoteException;

    void turnTo(Direction direction) throws RemoteException;

    boolean placeBlock(RemoteItemSlot slot);
    boolean placeBlock(int id);
    BlockType getBlockType(Direction side);

    void switchSlots(int a, int b);
    void switchSlots(RemoteItemSlot a, RemoteItemSlot b);
    void moveItems(RemoteItemSlot from, RemoteItemSlot to, int amount);
    void moveItems(int from, int to, int amount);

    RemoteItemSlot getSlot(int id);
    int getSlotID(RemoteItemSlot slot);

    void equip(int id);
    void equip(RemoteItemSlot slot);
}
