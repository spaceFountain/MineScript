package vg.civcraft.mc.MineScript.apis;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.Serializable;

/**
 * Created by isaac on 2/14/15.
 */
public enum  Direction implements Serializable {
    UP,
    DOWN,
    EAST,
    WEST,
    NORTH,
    SOUTH;
    public BlockFace toFace() {
        BlockFace face = BlockFace.DOWN;
        switch (this) {
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
        return  face;
    }
    public static Direction fromFace(BlockFace face){
        switch (face) {
            case DOWN:
                return DOWN;
            case UP:
                return UP;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
        }
        return DOWN;
    }
}
