package net.ttk1.spigotplugintemplate;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import com.google.inject.name.Names;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PluginModule extends AbstractModule {
    private final JavaPlugin plugin;

    public PluginModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure(){
        bind(JavaPlugin.class).toInstance(plugin);
        bind(Logger.class).annotatedWith(Names.named("lifegame")).toInstance(plugin.getLogger());
        bind(Configuration.class).toInstance(plugin.getConfig());
    }
}
