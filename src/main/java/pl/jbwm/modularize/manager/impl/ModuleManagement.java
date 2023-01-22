package pl.jbwm.modularize.manager.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.jbwm.modularize.annotation.Command;
import pl.jbwm.modularize.manager.ModuleManager;


import java.util.ArrayList;
import java.util.List;

@Command(name = "module", permission = "modularize.admin", usage = "/module reload <nazwa modulu>/all")
public class ModuleManagement implements TabExecutor {

    private final JavaPlugin javaPlugin;
    private final ModuleManager moduleManager;


    public ModuleManagement(JavaPlugin javaPlugin, ModuleManager moduleManager) {
        this.javaPlugin = javaPlugin;
        this.moduleManager = moduleManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 2) return false;

        if (!args[0].equals("reload")) return false;

        javaPlugin.reloadConfig();

        switch (args[1]) {
            case "all" -> moduleManager.reloadModules();

            default -> moduleManager.reloadModule(args[1]);
        }
        sender.sendMessage(ChatColor.GREEN + "Module " + args[1] + " reloaded");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.Command command, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) return List.of("reload");
        else if (args.length >= 2)
            return StringUtil.copyPartialMatches(args[1], javaPlugin.getConfig().getConfigurationSection("modules").getKeys(false).stream().toList(), new ArrayList<>());

        return null;
    }



}
