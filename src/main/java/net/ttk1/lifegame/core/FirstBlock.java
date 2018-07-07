package net.ttk1.lifegame.core;

import org.bukkit.block.Block;

public class FirstBlock {
    public long selectTimeMillis;
    public Block firstBlock;

    public FirstBlock(Block block) {
        this.firstBlock = block;
        this.selectTimeMillis = System.currentTimeMillis();
    }
}
