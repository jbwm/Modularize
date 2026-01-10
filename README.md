# Modularize

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![Paper](https://img.shields.io/badge/Paper-1.19.3+-green.svg)](https://papermc.io/)

**Modularize** is a framework for Minecraft plugin development that enables you to split your plugin into flexible, manageable modules. It provides automatic registration of listeners and commands, module lifecycle management, health monitoring, and  more.

## Features

- **Automatic Registration**: Automatically register listeners and commands using annotations—no manual registration needed
- **Module System**: Organize your plugin into independent, configurable modules
- **Hot Reloading**: Reload individual modules or all modules at runtime without restarting the server
- **Health Monitoring**: Built-in server health monitoring with customizable TPS thresholds
- **Class Blocking**: Prevent usage of problematic classes (e.g., `PlayerInteractEvent`) in modularized code
- **Lifecycle Hooks**: `Initializable`, `Reloadable`, and `Healthy` interfaces for fine-grained control
- **Zero Configuration**: No need to manually edit `plugin.yml`—everything is handled automatically
- **Dependency Injection Ready**: Works seamlessly with dependency injection frameworks like Guice

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [Core Concepts](#core-concepts)
- [Annotations](#annotations)
- [Lifecycle Interfaces](#lifecycle-interfaces)
- [Module Management](#module-management)
- [Health Monitoring](#health-monitoring)
- [Class Blocking](#class-blocking)
- [Configuration](#configuration)
- [Examples](#examples)
- [Requirements](#requirements)

## Installation

### Maven

Add the JitPack repository and dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.jbwm</groupId>
        <artifactId>Modularize</artifactId>
        <version>1.5.0</version>
    </dependency>
</dependencies>
```

## Quick Start

### Basic Setup

1. **Scan your package** for modularized classes:
```java
Set<Class<?>> modularizedClasses = Modularize.scanPackage("com.yourplugin.modules");
```

2. **Build the module manager**:
```java
ModuleManager moduleManager = Modularize.buildManager(this, modularizedClasses);
```

3. **Register all modules**:
```java
moduleManager.registerAll();
```

That's it! Your listeners and commands are now automatically registered.

### Example Module

```java
@Listen
@Module(name = "MyModule")
public class MyModule implements Listener, Initializable {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome!");
    }
    
    @Override
    public void init() {
        // Initialize your module here
        getLogger().info("MyModule initialized!");
    }
}
```

## Core Concepts

### Modules

A **module** is a class annotated with `@Module` that can optionally be annotated with `@Listen` or `@Command`. Modules are:
- Automatically registered when you call `registerAll()`
- Configurable via `config.yml` (enabled/disabled)
- Reloadable individually or all at once
- Isolated units of functionality

### Module Manager

The `ModuleManager` is the central component that:
- Registers listeners and commands
- Manages module lifecycle
- Handles reloading
- Monitors server health
- Enforces class blocking rules

## Annotations

### @Module

Marks a class as a module. **Required** for module functionality.

```java
@Module(name = "ModuleName")
public class MyModule {
    // Module code
}
```

**Parameters:**
- `name` (required): Unique identifier for the module (used in config.yml)

### @Listen

Automatically registers the class as a Bukkit `Listener`. The class must implement `Listener`.

```java
@Listen
@Module(name = "MyListener")
public class MyListener implements Listener {
    
    @EventHandler
    public void onEvent(SomeEvent event) {
        // Handle event
    }
}
```

**Note:** You still need to implement the `Listener` interface and use `@EventHandler` annotations.

### @Command

Automatically registers a command. The class must implement `TabExecutor`.

```java
@Command(
    name = "mycommand",
    usage = "/mycommand [args]",
    permission = "myplugin.use",
    permissionMessage = "You don't have permission!",
    aliases = {"mc", "mycmd"}
)
@Module(name = "CommandModule")
public class MyCommand implements TabExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Command logic
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Tab completion logic
        return Collections.emptyList();
    }
}
```

**Parameters:**
- `name` (required): Command name
- `usage` (required): Command usage string
- `permission` (optional): Required permission node
- `permissionMessage` (optional): Message shown when permission is denied
- `aliases` (optional): Command aliases

**Important:** 
- `CommandExecutor` is **not supported**. Use `TabExecutor` instead.
- You don't need to register commands in `plugin.yml` - Modularize handles it automatically.

## Lifecycle Interfaces

### Initializable

Implement this interface to perform initialization tasks when the module is loaded (e.g., loading data from config, setting up inventories).

```java
@Module(name = "MyModule")
public class MyModule implements Initializable {
    
    @Override
    public void init() {
        // Called during module initialization
        loadConfig();
        setupInventories();
    }
}
```

### Reloadable

Implement this interface to handle module reloading. The `reload()` method is called when the module is reloaded.

```java
@Module(name = "MyModule")
public class MyModule implements Reloadable {
    
    @Override
    public void reload() {
        // Called when module is reloaded
        reloadConfig();
        refreshData();
    }
}
```

### Healthy

Implement this interface to react to server health changes. Requires health monitoring to be initialized first.

```java
@Module(name = "MyModule")
public class MyModule implements Healthy {
    
    @Override
    public void ifUnhealthy() {
        // Called when server becomes unhealthy (TPS drops)
        disableExpensiveOperations();
    }
    
    @Override
    public void ifBackToHealth() {
        // Called when server health is restored
        enableExpensiveOperations();
    }
}
```

## Module Management

### Registering Modules

```java
// Register all modules
moduleManager.registerAll();

// Register modules with specific qualifiers (if using @Qualifier)
moduleManager.registerAll("production", "enabled");
```

### Reloading Modules

```java
// Reload a specific module by name
moduleManager.reloadModule("MyModule");

// Reload all modules
moduleManager.reloadModules();
```

**Note:** Reloading reads from `config.yml`, so modules disabled in the config will be disabled after reload.

### Getting Active Components

```java
// Get all active listeners
Map<String, Listener> listeners = moduleManager.getListeners();

// Get all active commands
Map<String, Pair<TabExecutor, PluginCommand>> commands = moduleManager.getCommands();

// Get all initialized class instances
Set<Object> initializedClasses = moduleManager.getInitializedClasses();
```

## Health Monitoring

Modularize includes a built-in server health monitor that tracks TPS (Ticks Per Second) and notifies modules when the server becomes unhealthy.

### Basic Setup

```java
moduleManager.registerAll();
moduleManager.initializeHealthMonitor();
```

### Advanced Configuration

```java
moduleManager.initializeHealthMonitor(
    30 * 20L,  // checkInterval: Check every 30 seconds (in ticks)
    10,        // historySize: Require 10 consecutive low TPS readings
    19.0,      // TPStreshold: Consider unhealthy if TPS < 19.0
    60 * 20L   // startDelay: Wait 60 seconds after server start (recommended: ≥200 ticks)
);
```

**Parameters:**
- `checkInterval`: How often (in ticks) to check server health
- `historySize`: How many consecutive low TPS readings are required to trigger unhealthy state
- `TPStreshold`: TPS threshold below which readings are considered low
- `startDelay`: Delay from server start before monitoring begins (recommended: at least 200 ticks)

## Class Blocking

For large projects with multiple developers, you can block the use of specific classes in modularized code. This is useful for preventing usage of problematic classes like `PlayerInteractEvent` (which fires for both hands, pressure plates, etc.).

### Usage

```java
Set<Class<?>> modularizedClasses = Modularize.scanPackage("com.yourplugin.modules");

ModuleManager moduleManager = Modularize.buildManager(
    this,
    modularizedClasses,
    CheckType.EQUALS,                    // Match type: EQUALS or CONTAINS
    ErrorType.RUN_TIME_EXCEPTION,        // Error type: INFO or RUN_TIME_EXCEPTION
    "org.bukkit.event.player.PlayerInteractEvent"  // Blocked classes
);

moduleManager.registerAll();
```

### Check Types

- `CheckType.EQUALS`: Exact class name match (e.g., `org.bukkit.event.player.PlayerInteractEvent`)
- `CheckType.CONTAINS`: Partial class name match (e.g., `PlayerInteractEvent`)

### Error Types

- `ErrorType.INFO`: Print a warning message to console
- `ErrorType.RUN_TIME_EXCEPTION`: Throw a `RuntimeException` and prevent plugin startup

### Workaround: Custom Events

Create your own events **outside** the scanned package (before or at the same level as your modules package):

```
com.yourplugin
├── modules/          ← Scanned package
│   └── MyModule.java
└── events/           ← Custom events (outside scanned package)
    └── PlayerClickedBlockEvent.java
```

This way, you can use your custom events in modules while blocking the problematic Bukkit events.

## Configuration

### Automatic Config Generation

When a class is annotated with `@Module` **and** `@Listen` or `@Command`, it becomes a module and is automatically added to `config.yml`:

```yaml
modules:
  MyModule: true
  AnotherModule: true
```

You don't need to manually add modules to the config—Modularize handles it automatically.

### Enabling/Disabling Modules

Simply change the value in `config.yml`:

```yaml
modules:
  MyModule: true      # Module is enabled
  AnotherModule: false # Module is disabled
```

Disabled modules won't be registered, and reloading will respect these settings.

### Module vs Non-Module Classes

- **Module**: Class annotated with `@Module` **AND** (`@Listen` OR `@Command`)
- **Non-Module**: Class annotated with only `@Listen` or `@Command` (without `@Module`)

Non-module classes are still registered but:
- Won't appear in `config.yml`
- Can't be reloaded individually
- Can't use lifecycle interfaces

## Examples

Complete code examples are available in the [`example/`](example/) directory of this repository.

### Example: Basic Module

```java
@Listen
@Module(name = "WelcomeModule")
public class WelcomeModule implements Listener, Initializable {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Welcome to the server!");
    }
    
    @Override
    public void init() {
        getLogger().info("WelcomeModule initialized!");
    }
}
```

### Example: Command Module

```java
@Command(
    name = "hello",
    usage = "/hello [player]",
    permission = "myplugin.hello"
)
@Module(name = "HelloCommand")
public class HelloCommand implements TabExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                sender.sendMessage("Hello, " + target.getName() + "!");
            } else {
                sender.sendMessage("Player not found!");
            }
        } else {
            sender.sendMessage("Hello!");
        }
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
```

### Example: Reloadable Module

```java
@Listen
@Module(name = "ConfigModule")
public class ConfigModule implements Listener, Reloadable {
    
    private String message;
    
    @Override
    public void reload() {
        // Reload configuration
        reloadConfig();
        message = getConfig().getString("message", "Default message");
        getLogger().info("ConfigModule reloaded!");
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.getPlayer().sendMessage(message);
    }
}
```

### Example: Health-Aware Module

```java
@Listen
@Module(name = "PerformanceModule")
public class PerformanceModule implements Listener, Healthy {
    
    private boolean expensiveOperationsEnabled = true;
    
    @Override
    public void ifUnhealthy() {
        expensiveOperationsEnabled = false;
        getLogger().warning("Server unhealthy - disabling expensive operations");
    }
    
    @Override
    public void ifBackToHealth() {
        expensiveOperationsEnabled = true;
        getLogger().info("Server healthy - re-enabling expensive operations");
    }
    
    @EventHandler
    public void onSomeEvent(SomeEvent event) {
        if (expensiveOperationsEnabled) {
            // Perform expensive operation
        }
    }
}
```

## Requirements

- **Java**: 17 or higher
- **Minecraft Server**: Paper/Spigot 1.19.3+ (or compatible)
- **Maven**: 3.8+ (for building)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built for the Minecraft plugin development community
- Uses [Reflections](https://github.com/ronmamo/reflections) for class scanning
- Compatible with dependency injection frameworks like Guice

---

**Made for Minecraft plugin developers**
