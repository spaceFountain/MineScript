package vg.civcraft.mc.MineScript.computer;

import net.minecraft.server.v1_7_R4.Item;
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

    protected static BlockType type = new BlockType(Material.DISPENSER, (byte) 0, "robot");

    public Robot(Location location, ComputerManager manager) throws RemoteException {
        super(0);
        this.location = location;
        this.manager = manager;
        this.block = location.getBlock();
        direction = Direction.fromFace(((DirectionalContainer) ((Dispenser) block.getState()).getData()).getFacing());
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
        return block.getRelative(direction.toFace());
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
                    for (int attempt = 0; attempt < 20; ++attempt) {
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
        try {
            boolean result = Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<Boolean>(){
                @Override
                public Boolean call() {
                    Block newBlock = getBlockInFront();
                    Location newLocation = newBlock.getLocation();
                    MineScriptPlugin.logger.info("from " + location + " to " + newLocation);

                    if (!newBlock.isEmpty() && !newBlock.isLiquid())
                        return false;

                    if (newLocation.clone().subtract(0, 1, 0).getBlock().isEmpty() &&
                            location.clone().subtract(0, 1, 0).getBlock().isEmpty()) // the robot can jump and lean over edges
                        return  false;

                    ItemStack[] items = getInventory().getContents(); // this will be removed as we break the block
                    getInventory().clear(); // we're removing everything so it doesn't go all over the floor when we break
                    block.setType(Material.AIR);
                    block.setData((byte) 0); // this might happen on it's own

                    BlockMetaPlugin.getManager().onBlockBreak(location); // so that it can remove the old entries

                    block = newBlock;
                    location = newLocation;

                    type.changeBlockTo(block);
                    getInventory().setContents(items);

                    BlockState blockState = block.getState();
                    DirectionalContainer directionalContainer = (DirectionalContainer) blockState.getData();
                    directionalContainer.setFacingDirection(direction.toFace());
                    blockState.setData(directionalContainer);
                    blockState.update();

                    MineScriptPlugin.logger.info(block.toString());

                    return true;
                }
            }).get();
            Thread.sleep(1000); //todo make this configurable
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void turnTo(final Direction newDirection) throws RemoteException {
        try {
            Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<Object>(){
                @Override
                public Object call() {
                    direction = newDirection;

                    BlockState blockState = block.getState();
                    DirectionalContainer directionalContainer = (DirectionalContainer) blockState.getData();
                    directionalContainer.setFacingDirection(direction.toFace());
                    blockState.setData(directionalContainer);
                    blockState.update();
                    return null;
                }
            }).get(); // even though we don't care about the value we tell it to get the value so that the calling thread waits
            Thread.sleep(1000); //todo make this configurable
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean placeBlock(final int id) throws RemoteException{
        try {
            boolean result = Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<Boolean>(){
                @Override
                public Boolean call() {
                    Block placingInto = getBlockInFront();
                    ItemStack item = getInventory().getItem(id);
                    if (item == null || item.getType() == Material.AIR)
                        return false;

                    if (!placingInto.isEmpty() && !placingInto.isLiquid())
                        return false;

                    BlockType type = BlockMetaPlugin.getManager().getBlockForItem(new ItemType(item));
                    if (type != null) {
                        type.changeBlockTo(block);
                    } else {
                        placingInto.setType(item.getType());
                        placingInto.setData((byte) item.getDurability());
                    }
                    return true;
                }
            }).get();
            Thread.sleep(1000); //todo make this configurable
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public BlockType getBlockType(final Direction side) throws RemoteException{
        try {
            BlockType result = Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<BlockType>(){
                @Override
                public BlockType call() {
                    return new BlockType(block.getRelative(side.toFace()));
                }
            }).get();

            Thread.sleep(1000); //todo make this configurable
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void switchSlots(final int a, final int b) throws RemoteException{
        try {
            Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<Object>(){
                @Override
                public Object call() {
                    ItemStack[] inventory = getInventory().getContents();
                    if (a > inventory.length || b > inventory.length)
                        return null; //maybe throw an exception so they know what happened.

                    ItemStack temp = inventory[a];
                    inventory[a] = inventory[b];
                    inventory[b] = inventory[a];
                    return null;
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moveItems(final int a, final int b, final int amount) throws RemoteException{
        try {
            Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<Object>(){
                @Override
                public Object call() {
                    ItemStack[] inventory = getInventory().getContents();
                    if (a > inventory.length || b > inventory.length)
                        return null; //maybe throw an exception so they know what happened.

                    ItemStack source = inventory[a];
                    ItemStack destination = inventory[b];

                    if (source == null || destination == null || !source.isSimilar(destination)) //we can only move between items of the same type
                        return null; // todo throw something

                    if (source.getAmount() < amount) //can't move more items than we have
                        return null;

                    if (destination.getAmount() + amount > destination.getMaxStackSize())
                        return null;

                    source.setAmount(source.getAmount() - amount);
                    destination.setAmount(destination.getAmount() + amount);

                    return null;
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void equip(int id) throws RemoteException {

    }

    @Override
    public int getAmount(final int slotID) throws RemoteException {
        try {
            return Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<Integer>() {
                @Override
                public Integer call() {
                    ItemStack[] inventory = getInventory().getContents();
                    if (slotID > inventory.length)
                        return null; //maybe throw an exception so they know what happened.
                    ItemStack item = inventory[slotID];
                    if (item == null) //this is the same as material=air
                        return 0;
                    if (item.getType() == Material.AIR) {
                        return 0;
                    }

                    return item.getAmount();
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public ItemType getType(final int slotID) throws RemoteException {
        try {
            return Bukkit.getScheduler().callSyncMethod(MineScriptPlugin.self, new Callable<ItemType>(){
                @Override
                public ItemType call() {
                    ItemStack[] inventory = getInventory().getContents();
                    if (slotID > inventory.length)
                        return null; //maybe throw an exception so they know what happened.
                    ItemStack item = inventory[slotID];
                    if (item == null) //this is the same as material=air
                        return new ItemType(Material.AIR, (short) 0, "");

                    return new ItemType(item);
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
