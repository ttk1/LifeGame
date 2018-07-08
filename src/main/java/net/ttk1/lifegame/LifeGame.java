package net.ttk1.lifegame;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import net.ttk1.lifegame.core.FieldService;
import net.ttk1.lifegame.eventlistener.FieldSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class LifeGame extends JavaPlugin {
    private Logger logger;
    private Configuration config;
    private FieldSelector fieldSelector;
    private FieldService fieldService;

    @Inject
    private void setLogger(@Named("lifegame") Logger logger) {
        this.logger = logger;
    }

    @Inject
    private void setConfig(Configuration config) {
        this.config = config;
    }

    @Inject
    private void setFieldSelector(FieldSelector fieldSelector) {
        this.fieldSelector = fieldSelector;
    }

    @Inject
    private void setFieldService(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    public LifeGame() {
    }

    @Override
    public void onEnable() {
        initConfig();

        // guice
        PluginModule module = new PluginModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        // event listeners
        getServer().getPluginManager().registerEvents(fieldSelector, this);

        logger.info("LifeGame enabled");
    }

    @Override
    public void onDisable() {
        logger.info("LifeGame disabled");
    }

    private void initConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("config.yml found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (command.getName().equalsIgnoreCase("lg") ||
                command.getName().equalsIgnoreCase("lifegame")) {
                if (args.length > 0) {
                    switch (args[0]) {
                        case "delete":
                            fieldService.deleteField((Player) sender);
                    }
                }
            }
        }
        return true;
    }
}
