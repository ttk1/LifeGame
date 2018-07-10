package net.ttk1.lifegame.task;

import net.ttk1.lifegame.LifeGame;
import net.ttk1.lifegame.field.Field;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class FieldUpdateTask extends BukkitRunnable {
    private static Map<String, FieldUpdateTask> tasks = new HashMap<>();

    private static final int STATUS_STOPPED = 0;
    private static final int STATUS_RUNNING = 1;

    private static final int COMMAND_KEEP_RUNNING = 0;
    private static final int COMMAND_START_RUNNING = 1;
    private static final int COMMAND_STOP_RUNNING = 2;
    private static final int COMMAND_RESET_FIELD = 3;
    private static final int DELAY = 1;


    private LifeGame plugin;
    private Field field;

    private int status = STATUS_STOPPED;
    private int command = COMMAND_KEEP_RUNNING;

    public static FieldUpdateTask getTask(String playerUuid) {
        return tasks.get(playerUuid);
    }
    public static void addTask(String playerUuid, FieldUpdateTask task) {
        tasks.put(playerUuid, task);
    }
    public static void deleteTask(String playerUuid) {
        FieldUpdateTask task = tasks.get(playerUuid);
        if (task != null) {
            task.cancel();
            tasks.remove(playerUuid);
        }
    }

    public FieldUpdateTask(Field field, LifeGame plugin) {
        this.field = field;
        this.plugin = plugin;
    }

    public int getStatus() {
        return status;
    }

    public void start() {
        try {
            status = STATUS_RUNNING;
            runTaskTimer(plugin, DELAY, DELAY);
        } catch (Exception e) {
            command = COMMAND_START_RUNNING;
        }
    }

    public void stop() {
        command = COMMAND_STOP_RUNNING;
    }

    public void reset() {
        command = COMMAND_RESET_FIELD;
    }

    @Override
    public void run() {
        switch (command) {
            case COMMAND_KEEP_RUNNING:
                field.update();
                break;
            case COMMAND_START_RUNNING:
                command = COMMAND_KEEP_RUNNING;
                status = STATUS_RUNNING;
                break;
            case COMMAND_STOP_RUNNING:
                status = STATUS_STOPPED;
                break;
            case COMMAND_RESET_FIELD:
                if (status == STATUS_RUNNING) {
                    command = COMMAND_KEEP_RUNNING;
                } else {
                    command = COMMAND_STOP_RUNNING;
                }
                field.reset();
                break;
        }
    }
}
