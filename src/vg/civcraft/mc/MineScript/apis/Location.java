package vg.civcraft.mc.MineScript.apis;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A simple 3 int location
 */
public class Location implements Serializable{
    private int x;
    private int y;
    private int z;
    public Location(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location(org.bukkit.Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }



    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location) {
            Location other = (Location) obj;
            return x == other.x && y == other.y && z == other.z;
        }
        return false;
    }
}
