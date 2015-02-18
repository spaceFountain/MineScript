package vg.civcraft.mc.MineScript;

import org.bukkit.plugin.java.JavaPlugin;
import vg.civcraft.mc.MineScript.computer.ComputerManager;

import java.util.logging.Logger;

/**
 * Created by isaac on 2/8/15.
 */
public class MineScriptPlugin extends JavaPlugin{
    public static MineScriptPlugin self;
    public static Logger logger;
    private ComputerManager computerManager;

    @Override
    public void onEnable() {
        self = this;
        logger = this.getLogger();
        computerManager = new ComputerManager();
    }
}
