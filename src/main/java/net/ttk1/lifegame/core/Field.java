package net.ttk1.lifegame.core;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Field {
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
                    block.setType(Material.DIAMOND_BLOCK);
                }
            }
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
