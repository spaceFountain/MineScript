package vg.civcraft.mc.MineScript.computer;

import org.bukkit.Material;
import vg.civcraft.mc.MineScript.apis.BlockType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by isaac on 2/15/2015.
 */
public class ComputerManager {
    Set<Computer> activeComputers = new HashSet<Computer>();
    CreationListener creationListener;

    public ComputerManager() {
        creationListener = new CreationListener(new BlockType(Material.DISPENSER), this);
    }

    public void add(Computer toAdd) {
        activeComputers.add(toAdd);
    }
}
