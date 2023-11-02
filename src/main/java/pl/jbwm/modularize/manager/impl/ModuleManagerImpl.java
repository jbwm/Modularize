package pl.jbwm.modularize.manager.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.jbwm.modularize.Modularize;
import pl.jbwm.modularize.annotation.Command;
import pl.jbwm.modularize.annotation.Listen;
import pl.jbwm.modularize.annotation.Module;
import pl.jbwm.modularize.annotation.Qualifier;
import pl.jbwm.modularize.manager.Initializable;
import pl.jbwm.modularize.manager.ModuleManager;
import pl.jbwm.modularize.manager.Reloadable;
import pl.jbwm.modularize.manager.listener.ListenerManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;

public class ModuleManagerImpl implements ModuleManager {

    private final Map<String, Listener> listeners = new HashMap<>();
    private final Map<String, Pair<TabExecutor, PluginCommand>> commands = new HashMap<>();

    private final Logger log;

    private final JavaPlugin plugin;

    private final Set<Class<?>> classes;

    private Set<Object> initializedClasses = new HashSet<>();

    private final ListenerManager listenerManager;


    public ModuleManagerImpl(JavaPlugin plugin, Set<Class<?>> classes, ListenerManager listenerManager) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
        this.classes = classes;
        this.listenerManager = listenerManager;

        ModuleManagement moduleManagement = new ModuleManagement(plugin, this);
        registerCommand(moduleManagement);

        if(!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();



        File configFile = new File(plugin.getDataFolder() + "/config.yml");

        if(!configFile.exists()) {
            try {
                configFile.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        plugin.getConfig().set("modules.Core", true);
        plugin.saveConfig();
    }

    @Override
    public void registerAll(String... qualifiers) {
        for (var clazz : classes) {
            try {

                if (clazz.isAnnotationPresent(Qualifier.class)) {
                    String qualifier = clazz.getAnnotation(Qualifier.class).qualifierName();

                    if(!Arrays.stream(qualifiers).toList().contains(qualifier))
                        continue;
                    }

                Object instance = this.getInstance(clazz);
                listenerManager.callPostReflectListener(instance);


                initializedClasses.add(instance);

                if(instance instanceof Initializable initializable)
                    initializable.init();

                if (clazz.isAnnotationPresent(Listen.class) && clazz.isAnnotationPresent(Command.class)) {
                    registerCommand((TabExecutor) instance);
                    registerListener((Listener) instance);

                } else if (clazz.isAnnotationPresent(Command.class)) registerCommand((TabExecutor) instance);
                else if (clazz.isAnnotationPresent(Listen.class)) registerListener((Listener) instance);


                if (instance instanceof Reloadable managed)
                    managed.reload();
            } catch (Exception e) {
                log.warning("Error occured during initiation of class: " + clazz.getName());
                e.printStackTrace();
            }
        }
    }


    //TODO:
    @Override
    public void reloadModules() {
        classes.forEach(clazz -> this.reloadModule(clazz.getName()));
    }

    @Override
    public void reloadModule(String name) {


        Object toReinitialize = null;


        Listener listener = listeners.keySet().stream().map(listeners::get)
                .filter(l -> l.getClass().isAnnotationPresent(Module.class) && l.getClass().getAnnotation(Module.class).name().equals(name))
                .findFirst()
                .orElse(null);

        if (listener != null) {
            toReinitialize = listener;
            initializedClasses.remove(listener);

            unregisterListener(listener);
            listeners.remove(listener.getClass().getName());
        }

        TabExecutor command = commands.keySet().stream().map(c -> commands.get(c).getKey())
                .filter(l -> l.getClass().isAnnotationPresent(Module.class) && l.getClass().getAnnotation(Module.class).name().equals(name))
                .findFirst()
                .orElse(null);

        if (command != null) {
            toReinitialize = command;
            initializedClasses.remove(command);

            this.unregisterCommand(commands.get(command.getClass().getName()).getValue());
            commands.remove(command.getClass().getName());
        }


        if(toReinitialize != null)
            this.registerSingle(toReinitialize);
    }

    @Override
    public Map<String, Listener> getListeners() {
        return listeners;
    }

    @Override
    public Map<String, Pair<TabExecutor, PluginCommand>> getCommands() {
        return commands;
    }

    @Override
    public Set<Object> getInitializedClasses() {
        return initializedClasses;
    }

    private Object getInstance(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Cannot init class " + clazz.getName());
    }

    private void registerCommand(TabExecutor tabExecutor) {
        if (commands.containsKey(tabExecutor.getClass().getName()))
            return;

        var annotation = tabExecutor.getClass().getAnnotation(Command.class);
        Qualifier qualifier = tabExecutor.getClass().getAnnotation(Qualifier.class);
        String qualifierInfo = "";
        if(qualifier != null) qualifierInfo = "with qualifier "+ qualifier.qualifierName()+" ";

        if (!isEnabled(tabExecutor.getClass())) tabExecutor = new DisabledExecutor();

        PluginCommand command = getPluginCommand(tabExecutor, annotation.name(), annotation.usage(), annotation.permission(), annotation.permissionMessage(), annotation.aliases());


        Bukkit.getServer().getCommandMap().register(plugin.getName(), command);

        if (isEnabled(tabExecutor.getClass())) {
            Bukkit.getConsoleSender().sendMessage(String.format(ChatColor.GRAY + "Registering command "+qualifierInfo + ChatColor.YELLOW + "%s", tabExecutor.getClass().getSimpleName()));
            commands.put(tabExecutor.getClass().getName(), Pair.of(tabExecutor, command));
        }


    }

    private PluginCommand getPluginCommand(TabExecutor tabexecutor, String name, String usage, String permission, String permissionMessage, String... aliases) {
        try {
            var c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            PluginCommand cmd = c.newInstance(name, plugin);
            if (!permission.isEmpty())
                cmd.setPermission(permission);
            cmd.setUsage(usage);
            cmd.setExecutor(tabexecutor);
            cmd.setTabCompleter(tabexecutor);

            if(!permissionMessage.isEmpty())
                cmd.setPermissionMessage(permissionMessage);
            else
                cmd.setPermissionMessage(ChatColor.RED + "You don't have permissions to execute that command!");


            if (aliases.length != 0)
                cmd.setAliases(Arrays.stream(aliases).toList());
            return cmd;
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Cannot load command: " + name);
    }

    private void registerListener(Listener listener) {
        if (!isEnabled(listener.getClass()) || listeners.containsKey(listener.getClass().getName())) return;
        Qualifier qualifier = listener.getClass().getAnnotation(Qualifier.class);
        String qualifierInfo = "";
        if(qualifier != null) qualifierInfo = "with qualifier "+ qualifier.qualifierName()+" ";

        Bukkit.getConsoleSender().sendMessage(String.format("§7Registering listener "+qualifierInfo+"§6%s", listener.getClass().getSimpleName()));
        getServer().getPluginManager().registerEvents(listener, plugin);
        listeners.put(listener.getClass().getName(), listener);
    }

    private void unregisterCommand(PluginCommand command) {
        Bukkit.getConsoleSender().sendMessage(String.format("§7unregistering command §6%s", command.getClass().getSimpleName()));
        Bukkit.getServer().getCommandMap().getKnownCommands().remove(command.getName());
        command.getAliases().forEach(a -> Bukkit.getServer().getCommandMap().getKnownCommands().remove(a));
        command.unregister(Bukkit.getCommandMap());
    }

    private void unregisterListener(Listener listener) {
        Bukkit.getConsoleSender().sendMessage(String.format("§7unregistering listener §7%s", listener.getClass().getSimpleName()));
        HandlerList.unregisterAll(listener);
    }

    private boolean isEnabled(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Module.class)) {
            Module module = clazz.getAnnotation(Module.class);

            boolean present = plugin.getConfig().getConfigurationSection("modules").getKeys(false).stream()
                    .anyMatch(m -> m.equals(module.name()));

            if (!present) {
                Bukkit.getConsoleSender().sendMessage(String.format("value of module %s not present, marking it true as default", module.name()));
                plugin.getConfig().set("modules" + "." + module.name(), true);
                plugin.saveConfig();
            }


            return plugin.getConfig().getBoolean("modules" + "." + module.name());
        }
        return true;
    }

    private void registerSingle(Object instance){
            try {
                if(instance instanceof Initializable initializable)
                    initializable.init();

                if (instance.getClass().isAnnotationPresent(Listen.class) && instance.getClass().isAnnotationPresent(Command.class)) {
                    registerCommand((TabExecutor) instance);
                    registerListener((Listener) instance);

                } else if (instance.getClass().isAnnotationPresent(Command.class)) registerCommand((TabExecutor) instance);
                else if (instance.getClass().isAnnotationPresent(Listen.class)) registerListener((Listener) instance);


                if (instance instanceof Reloadable managed)
                    managed.reload();
            } catch (Exception e) {
                log.warning("Error occured during initiation of class: " + instance.getClass().getName());
                e.printStackTrace();
            }

    }

    public class DisabledExecutor implements TabExecutor {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
            sender.sendMessage(ChatColor.RED + "This command is currently disabled.");
            return true;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.Command command, @NotNull String alias, @NotNull String[] args) {
            return null;
        }
    }

}
