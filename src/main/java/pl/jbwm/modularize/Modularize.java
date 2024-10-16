package pl.jbwm.modularize;

import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import pl.jbwm.modularize.annotation.Command;
import pl.jbwm.modularize.annotation.Listen;
import pl.jbwm.modularize.manager.Initializable;
import pl.jbwm.modularize.manager.ModuleManager;
import pl.jbwm.modularize.manager.enums.CheckType;
import pl.jbwm.modularize.manager.enums.ErrorType;
import pl.jbwm.modularize.manager.impl.ModuleManagerImpl;
import pl.jbwm.modularize.manager.listener.ListenerManager;
import pl.jbwm.modularize.manager.listener.PostReflectListener;

import java.util.HashSet;
import java.util.Set;

public final class Modularize {

    private static final ListenerManager listenerManager = new ListenerManager();

    /**
     * Simple manager, that register listeners and commands
     *
     * @param javaPlugin - your plugin instance
     * @param classes    - modularizedClasses
     */
    public static ModuleManager buildManager(JavaPlugin javaPlugin, Set<Class<?>> classes) {
        return new ModuleManagerImpl(javaPlugin, classes, listenerManager);
    }

    /**
     * Still simple manager, but this time, before registering listener, check for classes that you don't want to use in your project.
     * Perfect example is PlayerInteractEvent which works on the right hand, left hand, pressure plates etc. so for every event you need to use unnecessary if statements
     * You can simply create your own event from blocked class outside scaned package, for example:
     * <p>
     * Set<Class<?>> modularizedClasses = Modularize.scanPackage("pl.koral.mcskyblockcore.spigot.modules");
     * <p>
     * and your event need to be BEFORE or on PAR with modules package -> "pl.koral.mcskyblockcore.spigot" or "pl.koral.mcskyblockcore.spigot.customEvents"
     * <p>
     * for event you can look into example directory "myEvents"
     *
     * @param javaPlugin      - your plugin instance
     * @param classes         - modularizedClasses
     * @param checkType       - EQUALS (for example: org.bukkit.event.block.BlockBreakEvent) / CONTAINS (for example: BlockBreakEvent)
     * @param errorType       - INFO (Print error with class name in console) / RUN_TIME_EXCEPTION (Throw new Run Time Exception with class name)
     * @param disabledClasses - Array of classes that u don't want to use
     */
    public static ModuleManager buildManager(JavaPlugin javaPlugin, Set<Class<?>> classes, CheckType checkType, ErrorType errorType, String... disabledClasses) {
        return new ModuleManagerImpl(javaPlugin, classes, listenerManager, checkType, errorType, disabledClasses);
    }


    public static Set<Class<?>> scanPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);

        Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(Command.class);

        allClasses.addAll(reflections.getSubTypesOf(Initializable.class));

        allClasses.addAll(reflections.getTypesAnnotatedWith(Listen.class));

        return allClasses;
    }

    public static void addListener(PostReflectListener postReflectListener) {
        listenerManager.addListener(postReflectListener);
    }

}
