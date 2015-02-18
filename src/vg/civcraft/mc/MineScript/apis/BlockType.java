package vg.civcraft.mc.MineScript.apis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import vg.civcraft.mc.MineScript.MineScriptPlugin;

/**
 * Stores a a complete type for a block in MineScript including possibly an extra int to manage more blocks than Minecrat normally can.
 */
public class BlockType {
    private int type;
    private byte data;
    private int meta = -1;
    private static String META_KEY = "BlockType";
    public BlockType(Material t, byte d) {
        type = t.getId();
        data = d;
    }

    public BlockType(Material type, byte data, int meta) {
        this.type = type.getId();
        this.data = data;
        this.meta = meta;
    }

    public BlockType(Block block) {
        type = block.getType().getId();
        data = block.getData();

        if (block.hasMetadata(META_KEY)) { // probably we will want to use something else in production
            meta = block.getMetadata(META_KEY).get(0).asInt(); // todo use a better system for storing this int.
        }
    }

    public BlockType(BlockState state) {
        type = state.getType().getId();
        data = state.getRawData();

        if (state.hasMetadata(META_KEY)) {
            meta = state.getMetadata(META_KEY).get(0).asInt();
        }
    }

    public BlockType(Material type) {
        this(type, (byte) 0);
    }

    public void place(Location location) {
        location.getBlock().setType(Material.getMaterial(type));
        location.getBlock().setData(data);
        if (meta != -1)
            location.getBlock().setMetadata(META_KEY, new MetadataValue() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockType) {
            BlockType other = (BlockType) obj;

            return type == other.type && data == other.data && meta == other.meta;
        } else if (obj instanceof Block) {
            BlockType other = new BlockType((Block) obj); // todo do the check without converting

            return type == other.type && data == other.data && meta == other.meta;
        }  else if (obj instanceof BlockState) {
            BlockType other = new BlockType((BlockState) obj); // todo do the check without converting

            return type == other.type && data == other.data && meta == other.meta;
        }

        return false;
    }

}
