package vg.civcraft.mc.MineScript.computer;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import vg.civcraft.mc.BlockMeta.BlockMetaPlugin;
import vg.civcraft.mc.BlockMeta.BlockType;
import vg.civcraft.mc.BlockMeta.ItemType;
import vg.civcraft.mc.MineScript.MineScriptPlugin;
import vg.civcraft.mc.MineScript.scriptDispatcher.RemoteScriptStarter;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by isaac on 2/15/2015.
 */
public class ComputerManager implements Listener{
    protected RemoteScriptStarter starter;

    Set<Robot> activeRobots = new HashSet<Robot>();

    public ComputerManager() {
        BlockMetaPlugin.getManager().registerItemToBlock(new ItemType(Material.DISPENSER, (short) 0, ""), Robot.type);
        try {
            starter = (RemoteScriptStarter) Naming.lookup("//localhost/ScriptStarter");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void handleInteract(PlayerInteractEvent event) {
        if (event.isCancelled() || event.getItem() == null)
            return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemType clickedWith = new ItemType(event.getItem());
            MineScriptPlugin.logger.info(clickedWith.toString());
            MineScriptPlugin.logger.info(new ItemType(Material.STICK, (short) 0, "").toString());
            if (clickedWith.equals(new ItemType(Material.STICK, (short) 0, ""))) {
                BlockType blockType = new BlockType(event.getClickedBlock());

                if (blockType.getMeta().equals(Robot.type.getMeta()) && blockType.getType() == Robot.type.getType()) {
                    MineScriptPlugin.logger.info("starting computer");

                    try {
                        Robot robot = new Robot(event.getClickedBlock().getLocation(), this);
                        robot.run();
                        activeRobots.add(robot);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
