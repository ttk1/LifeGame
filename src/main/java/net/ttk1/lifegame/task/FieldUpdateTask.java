package net.ttk1.lifegame.task;

import net.ttk1.lifegame.LifeGame;
import net.ttk1.lifegame.core.Field;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class FieldUpdateTask extends BukkitRunnable {
    private static Map<String, FieldUpdateTask> fields = new HashMap<>();
    private LifeGame plugin;
    private Field field;
    private int delay = 20;

    // 0 -> stopped
    // 1 -> running
    private int status = 0;

    // 0 -> do nothing
    // 1 -> stop
    // 2 -> reset
    private int command = 0;

    public static FieldUpdateTask getTask(String playerUuid) {
        return fields.get(playerUuid);
    }
    public static void addTask(String playerUuid, FieldUpdateTask task) {
        fields.put(playerUuid, task);
    }
    public static void deleteTask(String playerUuid) {
        fields.remove(playerUuid);
    }

    public int getStatus() {
        return status;
    }

    public int getDelay() {
        return delay;
    }

    public int setDelay() {
        return delay;
    }

    public FieldUpdateTask(Field field, LifeGame plugin) {
        this.field = field;
        this.plugin = plugin;
    }

    public void start() {
        try {
            status = 1;
            runTaskLater(plugin, delay);
        } catch (Exception e) {
            // do nothing
        }
    }

    public void stop() {
        command = 1;
    }

    public void reset() {
        command = 2;
    }

    @Override
    public void run() {
        switch (command) {
            case 0:
                field.update();
                break;
            case 1:
                command = 0;
                status = 0;
            case 2:
                command = 0;
                field.reset();
                break;
            default:
                return;
        }
        runTaskLater(plugin, delay);
    }
}
