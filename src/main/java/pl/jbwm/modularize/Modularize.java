package pl.jbwm.modularize;

import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import pl.jbwm.modularize.annotation.Command;
import pl.jbwm.modularize.annotation.Listen;
import pl.jbwm.modularize.manager.ModuleManager;
import pl.jbwm.modularize.manager.impl.ModuleManagerImpl;
import pl.jbwm.modularize.manager.listener.ListenerManager;
import pl.jbwm.modularize.manager.listener.PostReflectListener;

import java.util.Set;

public final class Modularize {

    private final static ListenerManager listenerManager = new ListenerManager();

    public static ModuleManager buildManager(JavaPlugin javaPlugin, Set<Class<?>> classes){
        return new ModuleManagerImpl(javaPlugin, classes, listenerManager);
    }

    public static Set<Class<?>> scanPackage(String packageName){
        Reflections reflections = new Reflections(packageName);

        Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(Command.class);
        allClasses.addAll(reflections.getTypesAnnotatedWith(Listen.class));

        return allClasses;
    }

    public static void addListener(PostReflectListener postReflectListener){
        listenerManager.addListener(postReflectListener);
    }

}
