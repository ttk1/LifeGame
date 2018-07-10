package net.ttk1.lifegame.field;

import net.ttk1.lifegame.LifeGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class Field {
    public static final Material LIVE_BLOCK_TYPE = Material.REDSTONE_BLOCK;
    public static final Material DEAD_BLOCK_TYPE = Material.DIAMOND_BLOCK;
    public static final int MAX_VOLUME = 1000;
    public static final int MIN_POPULATION_TO_LIVE = 2;
    public static final int MAX_POPULATION_TO_LIVE = 3;
    public static final int MIN_POPULATION_TO_REPRODUCE = 3;
    public static final int MAX_POPULATION_TO_REPRODUCE = 3;

    private int x1,x2,y1,y2,z1,z2;
    private String playerUuid;
    private LifeGame plugin;
    private World world;

    public Field(String playerUuid, Block firstBlock, Block secondBlock, LifeGame plugin) {
        this.x1 = firstBlock.getLocation().getBlockX();
        this.y1 = firstBlock.getLocation().getBlockY();
        this.z1 = firstBlock.getLocation().getBlockZ();

        this.x2 = secondBlock.getLocation().getBlockX();
        this.y2 = secondBlock.getLocation().getBlockY();
        this.z2 = secondBlock.getLocation().getBlockZ();

        this.playerUuid = playerUuid;
        this.world = firstBlock.getWorld();
        this.plugin = plugin;

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

    public boolean isLiveCell(Block block) {
        return block.getType().equals(LIVE_BLOCK_TYPE);
    }

    private boolean isLiveCellNextStep(Block block) {
        int liveNeighbors = countLiveNeighbors(block);

        if (isLiveCell(block)) {
            return MIN_POPULATION_TO_LIVE <= liveNeighbors &&
                    liveNeighbors <= MAX_POPULATION_TO_LIVE;
        } else {
            return MIN_POPULATION_TO_REPRODUCE <= liveNeighbors &&
                    liveNeighbors <= MAX_POPULATION_TO_REPRODUCE;
        }
    }

    private int countLiveNeighbors(Block block) {
        int count = 0;
        List<Integer> offset = getRange(-1, 1);
        for (int x: offset) {
            for (int y: offset) {
                for (int z: offset) {
                    if ((x != 0 || y != 0 || z != 0) &&
                            isFieldBlock(block) &&
                            isLiveCell(block.getRelative(x, y, z))) {
                        count += 1;
                    }
                }
            }
        }
        return count;
    }

    public synchronized void flip(Location location) {
        flip(location.getBlock());
    }

    public synchronized void flip(Block block) {
        if (block.getType().equals(LIVE_BLOCK_TYPE)) {
            block.setType(DEAD_BLOCK_TYPE);
        } else {
            block.setType(LIVE_BLOCK_TYPE);
        }
    }

    public synchronized void update() {
        for (int x: getRange(x1, x2)) {
            for (int y: getRange(y1, y2)) {
                for (int z: getRange(z1, z2)) {
                    Block block = world.getBlockAt(x, y, z);
                    if (isLiveCellNextStep(block)) {
                        block.setMetadata(playerUuid, new FixedMetadataValue(plugin,true));
                    } else {
                        block.setMetadata(playerUuid, new FixedMetadataValue(plugin,false));
                    }
                }
            }
        }

        for (int x: getRange(x1, x2)) {
            for (int y: getRange(y1, y2)) {
                for (int z: getRange(z1, z2)) {
                    Block block = world.getBlockAt(x, y, z);
                    try {
                        if (block.getMetadata(playerUuid).get(0).asBoolean()) {
                            block.setType(LIVE_BLOCK_TYPE);
                        } else {
                            block.setType(DEAD_BLOCK_TYPE);
                        }
                        block.removeMetadata(playerUuid,plugin);
                    } catch (Exception e) {
                        block.setType(DEAD_BLOCK_TYPE);
                    }
                }
            }
        }
    }

    public synchronized void reset() {
        initField();
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
