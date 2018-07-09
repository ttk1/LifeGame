package net.ttk1.lifegame.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.ttk1.lifegame.LifeGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

@Singleton
public class FieldService {
    private Map<String, Field> fields = new HashMap<>();
    private Map<String, FirstBlock> firstBlocks = new HashMap<>();
    private LifeGame plugin;

    @Inject
    private void setPlugin(JavaPlugin plugin) {
        this.plugin = (LifeGame) plugin;
    }

    public Field getField(String playerUuid) {
        return fields.get(playerUuid);
    }

    public void selectBlock(Player player, Block block) {


        String playerUuid = player.getUniqueId().toString();
        Field field = fields.get(playerUuid);

        if (field != null) {
            if (field.isFieldBlock(block)) {
                // クリックした位置がフィールドの範囲内であれば、セルの生死を反転させる
                field.flip(block);
            } else {
                player.sendMessage("You've already have LifeGame Field!");
            }
        } else if (isFirstBlockSelected(playerUuid)) {
            Block firstBlock = firstBlocks.get(playerUuid).firstBlock;
            if (validateField(firstBlock, block)) {
                field = new Field(playerUuid, firstBlock, block, plugin);
                fields.put(playerUuid, field);
            } else {
                player.sendMessage("World is different!");
            }
            firstBlocks.remove(playerUuid);
        } else {
            FirstBlock firstBlock = new FirstBlock(block);
            firstBlocks.put(playerUuid, firstBlock);
            BlockChangeSender blockChangeSender = new BlockChangeSender(player, block);
            blockChangeSender.runTask(plugin);
        }
    }

    private boolean isFirstBlockSelected(String playerUuid) {
        FirstBlock firstBlock = firstBlocks.get(playerUuid);
        if (firstBlock != null && System.currentTimeMillis() - firstBlock.selectTimeMillis < 1000 * 60) {
            return true;
        } else {
            firstBlocks.remove(playerUuid);
            return false;
        }
    }

    private boolean validateField(Block firstBlock, Block secondBlock) {
        if (!firstBlock.getWorld().equals(secondBlock.getWorld())) {
            return false;
        } else {
            return getVolume(firstBlock.getLocation(), secondBlock.getLocation()) <= Field.MAX_VOLUME;
        }
    }

    private int getVolume(Location location1, Location location2) {
        return abs(location1.getBlockX() - location2.getBlockX()) +
                abs(location1.getBlockY() - location2.getBlockY()) +
                abs(location1.getBlockZ() - location2.getBlockZ());
    }

    public void deleteField(Player player) {
        deleteField(player.getUniqueId().toString());
    }
    public void deleteField(String playerUuid) {
        fields.remove(playerUuid);
    }

    /**
     * 一か所目の選択を保存するためのクラス
     */
    private class FirstBlock {
        private long selectTimeMillis;
        private Block firstBlock;

        private FirstBlock(Block block) {
            this.firstBlock = block;
            this.selectTimeMillis = System.currentTimeMillis();
        }
    }

    /**
     * 変化が上書きされるのを防止するため別スレッドで実行するためのクラス
     */
    private class BlockChangeSender extends BukkitRunnable {
        private Player player;
        private Block block;

        BlockChangeSender(Player player, Block block) {
            this.player = player;
            this.block = block;
        }

        @Override
        public void run() {
            player.sendBlockChange(block.getLocation(), Material.GOLD_BLOCK, (byte) 0x00);
        }

    }
}
