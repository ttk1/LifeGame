package net.ttk1.lifegame.eventlistener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.ttk1.lifegame.core.FieldService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/**
 * LifeGameフィールドの範囲選択
 */
@Singleton
public class FieldSelector implements Listener {
    FieldService fieldService;

    @Inject
    private void setFieldService(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @EventHandler
    public void onPlayerInteractEventHandler(PlayerInteractEvent event) {
        // 右クリック & stickを持っていることを確認
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.hasItem() && event.getItem().getType().equals(Material.STICK)) {
                fieldService.selectBlock(event.getPlayer(), event.getClickedBlock());
            }
        }
    }
}
