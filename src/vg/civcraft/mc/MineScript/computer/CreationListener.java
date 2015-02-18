package vg.civcraft.mc.MineScript.computer;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import vg.civcraft.mc.MineScript.MineScriptPlugin;
import vg.civcraft.mc.MineScript.apis.BlockType;

/**
 * Created by isaac on 2/15/2015.
 */
public class CreationListener implements Listener {
    BlockType computerType;
    ComputerManager list;
    public CreationListener(BlockType computerType, ComputerManager list) {
        this.computerType = computerType;
        this.list = list;
        Bukkit.getPluginManager().registerEvents(this, MineScriptPlugin.self); // register that we exist
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void handlePlaced(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;

        if (computerType == event.getBlock()) {
            MineScriptPlugin.logger.info("created computer");
            list.add(new Computer(event.getBlock().getLocation()));
        }

    }
}
