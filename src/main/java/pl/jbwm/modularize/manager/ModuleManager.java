package pl.jbwm.modularize.manager;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Set;

public interface ModuleManager {


    /**
     * Register all modules
     */
    void registerAll(String... qualifiers);

    /**
     * Reload specific module by name
     */
    void reloadModule(String name);


    /**
     * Reload all modules
     */
    void reloadModules();

    /**
     * Get active listeners
     */
    Map<String, Listener> getListeners();

    /**
     * Get active Commands
     */
    Map<String, Pair<TabExecutor, PluginCommand>> getCommands();

    /*
     * Get classes that are initialized
     */
    Set<Object> getInitializedClasses();

}
