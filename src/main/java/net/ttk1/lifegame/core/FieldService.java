package net.ttk1.lifegame.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class FieldService {
    private Map<String, Field> fields = new HashMap<>();
    private Map<String, FirstBlock> firstBlocks = new HashMap<>();
    private Server server;

    @Inject
    private void setServer(Server server) {
        this.server = server;
    }

    public Field getField(Player player) {
        return getField(player.getUniqueId().toString());
    }

    public Field getField(String playerUuid) {
        return fields.get(playerUuid);
    }

    public void selectBlock(Player player, Block block) {
        String playerUuid = player.getUniqueId().toString();

        if (fields.containsKey(playerUuid) && fields.get(playerUuid) != null) {
            server.getPlayer(UUID.fromString(playerUuid)).sendMessage("You've already have LifeGame Field!");
        } else if (isFirstBlockSelected(playerUuid)) {
            Block firstBlock = firstBlocks.get(playerUuid).firstBlock;
            if (firstBlock.getWorld().getName().equals(block.getWorld().getName())) {
                Field field = new Field(playerUuid, firstBlock, block);
                fields.put(playerUuid, field);
            } else {
                server.getPlayer(UUID.fromString(playerUuid)).sendMessage("World is different!");
            }
            firstBlocks.remove(playerUuid);
        } else {
            FirstBlock firstBlock = new FirstBlock(block);
            firstBlocks.put(playerUuid, firstBlock);
            BlockChangeSender blockChangeSender = new BlockChangeSender(player, block);
            blockChangeSender.start();
        }
    }

    public boolean isFirstBlockSelected(Player player) {
        return isFirstBlockSelected(player.getUniqueId().toString());
    }

    public boolean isFirstBlockSelected(String playerUuid) {
        if (firstBlocks.containsKey(playerUuid)) {
            FirstBlock firstBlock = firstBlocks.get(playerUuid);
            if (firstBlock != null || System.currentTimeMillis() - firstBlock.selectTimeMillis < 1000 * 60) {
                return true;
            } else {
                firstBlocks.remove(playerUuid);
                return false;
            }
        } else {
            return false;
        }
    }

    public void deleteField(Player player) {
        deleteField(player.getUniqueId().toString());
    }
    public void deleteField(String playerUuid) {
        fields.remove(playerUuid);
    }

    /**
     * 変化が上書きされるのを防止するため別スレッドで実行する
     */
    private class BlockChangeSender extends Thread {
        private Player player;
        private Block block;

        BlockChangeSender(Player player, Block block) {
            this.player = player;
            this.block = block;
        }

        @Override
        public void run() {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            player.sendBlockChange(block.getLocation(), Material.DIAMOND_BLOCK, (byte) 0x00);
        }

    }
}
