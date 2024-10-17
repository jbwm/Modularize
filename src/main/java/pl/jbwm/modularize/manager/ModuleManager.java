package pl.jbwm.modularize.manager;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
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

    /**
     * Get classes that are initialized
     */
    Set<Object> getInitializedClasses();


    /**
     * Initialize health monitor with default parameters
     */
    void initializeHealthMonitor();


    /**
     * Initialize health monitor with specified parameters
     *
     * @param checkInterval     - how often in ticks to check the health of the server
     * @param historySize       - how many times tps on the server must be below treshold
     * @param TPStreshold       - below how much tps must be to be included in the history
     * @param startDelay        - delay from server start (recommended not less than 200 ticks)
     */
    void initializeHealthMonitor(long checkInterval, int historySize, double TPStreshold, long startDelay);

}
