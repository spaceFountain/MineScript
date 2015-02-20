package vg.civcraft.mc.MineScript.computer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.DirectionalContainer;
import vg.civcraft.mc.BlockMeta.BlockMetaPlugin;
import vg.civcraft.mc.BlockMeta.BlockType;
import vg.civcraft.mc.BlockMeta.ItemType;
import vg.civcraft.mc.MineScript.MineScriptPlugin;
import vg.civcraft.mc.MineScript.apis.Direction;
import vg.civcraft.mc.MineScript.apis.Logger;
import vg.civcraft.mc.MineScript.apis.RemoteItemSlot;
import vg.civcraft.mc.MineScript.apis.RobotController;
import vg.civcraft.mc.MineScript.scriptDispatcher.RemoteScript;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by isaac on 2/8/15.
 */
public class Robot extends UnicastRemoteObject implements Listener, RobotController {
    private Location location;
    private RemoteScript runningScript;
    private HardDrive hardDrive;
    private ComputerManager manager;
    private Block block;
    private int scriptID = -1;
    private RemoteScript script = null;
    private Direction direction = Direction.DOWN;

    protected static BlockType type = new BlockType(Material.DISPENSER);

    public Robot(Location location, ComputerManager manager) throws RemoteException {
        super(0);
        this.location = location;
        this.manager = manager;
        this.block = location.getBlock();
    }

    private boolean correctType() {
        return location.getBlock().equals(new BlockType(block));
    }

    /**
     * Gets the inventory currently associated with a robot
     * @return an Inventory currently associated with the robot
     */
    private Inventory getInventory() {
        assert correctType();

        BlockState state = block.getState();
        assert state instanceof Dispenser;

        Dispenser dispenser = (Dispenser) state;
        return dispenser.getInventory();
    }

    /**
     * A helper utility method to get the first book meta in a inventory
     * @param inventory a valid inventory
     * @return the fist BookMeta from a WRITTEN_BOOK in the inventory.
     */
    private static BookMeta getFirstBook(Inventory inventory) {
        for (ItemStack item: inventory.getContents()) {
            if (item != null && item.getType() == Material.WRITTEN_BOOK && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof  BookMeta) {
                    return (BookMeta) meta;
                }
            }
        }
        return null;
    }

    /**
     * simple function to get the current blocking the robot is facing
     * @return the Block the robot is facing
     */
    private Block getBlockInFront(){
        BlockFace face = BlockFace.DOWN;
        switch (direction) {
            case DOWN:
                face = BlockFace.DOWN;
                break;
            case UP:
                face = BlockFace.UP;
                break;
            case WEST:
                face = BlockFace.WEST;
                break;
            case EAST:
                face = BlockFace.EAST;
                break;
            case NORTH:
                face = BlockFace.NORTH;
                break;
            case SOUTH:
                face = BlockFace.SOUTH;
                break;
        }
        return block.getRelative(face);
    }

    /**
     * called to request the code to execute
     * @return the code to be executed currently found by adding together all the pages in the first book found
     */
    private String getCode() {
        Inventory inventory = getInventory();
        BookMeta book = getFirstBook(inventory);
        if (book == null)
            return "";
        else {
            StringBuilder builder = new StringBuilder();
            for (String page: book.getPages()) {
                builder.append(page).append('\n');
            }
            MineScriptPlugin.logger.info(builder.toString());
            return builder.toString();
        }
    }

    /**
     * Called to handle adding APIs and starting a script
     */
    private void scriptStarted() {
        try {
            script.addAPI(this, "robot");
            script.addAPI(new Logger(MineScriptPlugin.logger), "logger");
            script.addAPI(Direction.class);
            script.addAPI(RemoteItemSlot.class);
            script.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * called to start the computer running
     */
    public void run() { //todo add a player that started the bot. All actions will be done in his or hers name.
        try {
            scriptID = manager.starter.start(getCode().toString(), 1000, 2000);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (int attempt = 0; attempt < 10; ++attempt) {
                        try {
                            MineScriptPlugin.logger.info("on attempt "+ attempt);
                            script = manager.starter.getScript(scriptID);
                            if (script != null) {
                                MineScriptPlugin.logger.info("success on "+ attempt);
                                scriptStarted();
                                return;
                            }
                            Thread.sleep(1000);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }





    @Override
    public boolean moveTo(vg.civcraft.mc.MineScript.apis.Location location) throws RemoteException {
        return false;
    }

    @Override
    public boolean move() throws RemoteException {
        return false;
    }

    @Override
    public void turnTo(final Direction newDirection) throws RemoteException {
        Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<Object>(){
            @Override
            public Object call() {
                direction = newDirection;
                byte data = 0;
                switch (direction) {
                    case DOWN:
                        data = 0;
                        break;
                    case UP:
                        data = 1;
                        break;
                    case WEST:
                        data = 4;
                        break;
                    case EAST:
                        data = 5;
                        break;
                    case NORTH:
                        data = 2;
                        break;
                    case SOUTH:
                        data = 3;
                        break;
                }
                block.setData(data);
                return null;
            }
        });
    }

    @Override
    public boolean placeBlock(final RemoteItemSlot slot) throws RemoteException{
        try {
            return Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<Boolean>(){
                @Override
                public Boolean call() throws RemoteException {
                    Block placingInto = getBlockInFront();
                    if (slot.getType().equals(new ItemType(Material.AIR, (short)0, "")))
                        return false;
                    BlockMetaPlugin.getManager().getBlockForItem(slot.getType()).changeBlockTo(placingInto);
                    return true;
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean placeBlock(final int id) throws RemoteException{
        try {
            return Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<Boolean>(){
                @Override
                public Boolean call() {
                    Block placingInto = getBlockInFront();
                    ItemStack item = getInventory().getItem(id);
                    if (item == null || item.getType() == Material.AIR)
                        return false;

                    BlockMetaPlugin.getManager().getBlockForItem(new ItemType(item)).changeBlockTo(placingInto);
                    return true;
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public BlockType getBlockType(Direction side) throws RemoteException{
        return null;
    }

    @Override
    public void switchSlots(int a, int b) throws RemoteException{

    }

    @Override
    public void switchSlots(RemoteItemSlot a, RemoteItemSlot b) throws RemoteException{

    }

    @Override
    public void moveItems(RemoteItemSlot from, RemoteItemSlot to, int amount) throws RemoteException{

    }

    @Override
    public void moveItems(int from, int to, int amount) throws RemoteException{

    }

    @Override
    public RemoteItemSlot getSlot(int id) throws RemoteException{
        return null;
    }

    @Override
    public int getSlotID(RemoteItemSlot slot) throws RemoteException{
        return 0;
    }

    @Override
    public void equip(int id) throws RemoteException {

    }

    @Override
    public void equip(RemoteItemSlot slot) {

    }
}
