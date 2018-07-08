package net.ttk1.lifegame.core;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private final static Material LIVE_BLOCK_TYPE = Material.REDSTONE_BLOCK;
    private final static Material DEAD_BLOCK_TYPE = Material.DIAMOND_BLOCK;

    private int x1,x2,y1,y2,z1,z2;
    private World world;

    public Field(String playerUuid, Block firstBlock, Block secondBlock) {
        this.x1 = firstBlock.getLocation().getBlockX();
        this.y1 = firstBlock.getLocation().getBlockY();
        this.z1 = firstBlock.getLocation().getBlockZ();

        this.x2 = secondBlock.getLocation().getBlockX();
        this.y2 = secondBlock.getLocation().getBlockY();
        this.z2 = secondBlock.getLocation().getBlockZ();

        this.world = firstBlock.getWorld();

        initField();
    }

    private void initField() {
        for (int x: getRange(x1, x2)) {
            for (int y: getRange(y1, y2)) {
                for (int z: getRange(z1, z2)) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(DEAD_BLOCK_TYPE);
                }
            }
        }
    }

    public boolean isFieldBlock(Block block) {
        return isFieldBlock(block.getLocation());
    }
    public boolean isFieldBlock(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return (x1 <= x && x <= x2 || x2 <= x && x <= x1) &&
                (y1 <= y && y <= y2 || y2 <= y && y <= y1) &&
                (z1 <= z && z <= z2 || z2 <= z && z <= z1);
    }

    public boolean isLiveCell(Location location) {
        return isLiveCell(location.getBlock());
    }
    public boolean isLiveCell(Block block) {
        return block.getType().equals(LIVE_BLOCK_TYPE);
    }

    public void flip(Location location) {
        flip(location.getBlock());
    }
    public void flip(Block block) {
        if (block.getType().equals(LIVE_BLOCK_TYPE)) {
            block.setType(DEAD_BLOCK_TYPE);
        } else {
            block.setType(LIVE_BLOCK_TYPE);
        }
    }

    private List<Integer> getRange(int a, int b) {
        List<Integer> range = new ArrayList<>();
        if (a < b) {
            for (int i = a; i <= b; i++) {
                range.add(i);
            }
        } else {
            for (int i = b; i <=a; i++) {
                range.add(i);
            }
        }
        return range;
    }
}
