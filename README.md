# Modularize
Enhanced plugin development. Split your plugin into flexible modules.


## Using API in your plugin

### Maven
```xml
<!-- Repository -->
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<!-- Dependency-->
<dependencies>
    <dependency>
        <groupId>com.github.jbwm</groupId>
        <artifactId>Modularize</artifactId>
        <version>1.4.0</version>
    </dependency>
</dependencies>
```
# Usage

### There are two ways you can use module manager

1. Normal


Register module manager on plugin startup
![image](https://user-images.githubusercontent.com/53827110/215810667-4aadff2d-b61b-4eb4-9ff6-dfdaf3fd5356.png)

2. With blocked classes

Let's assume you have a large project and a large number of people working on it.
You are aware that events such as "PlayerInteractEvent" work on hand, offhand, pressure plate etc.
This causes a lot of problems and forces you to use the same ifs everywhere.
In this example you block the use of PlayerInteractEvent (as EQUALS - class name) and Run Time Exception when it is called

This only works for classes that are modularized, which means you can easily use this event (in this case) before pl.koral.apitest.module for example in pl.koral.apitest.myEvents where you will call your own events such as PlayerClickedBlockEvent - then you no longer have to check in each separate InteractEvent whether the block is null, whether it uses one hand, and whether it is a pressure plate

![modulemanager](https://github.com/user-attachments/assets/143f06da-7ad7-43cc-9e20-861a18d1b2df)


**@Module** annotation register class as Module.

**@Listen** annotation automatically register class as Listener.

**@Command** annotation register command. It has few optional and required parameters.
![image](https://user-images.githubusercontent.com/53827110/214066927-eb2d0836-2c83-4e41-8b87-b653781a6593.png)

Nothing else is required, you don't have to register command or listener manually in main class. You don't need also to fill plugin.yml

Don't forget that **TabExecutor** or **Listener** interfaces **are still required.**

CommandExecutor is currently **not supported**, instead use TabExecutor which force usage of tab completer.

### Initializable
Use interface initializable on class, if you want to do something during initialization. (Load inventories, load data from config and so on.)
![image](https://user-images.githubusercontent.com/53827110/214067158-4d4f6cad-9123-4693-9676-4927931d5e23.png)

### Reloadable
Use interface reloadable on class, if you want to do something when module is reloaded.
![image](https://user-images.githubusercontent.com/53827110/214067307-9fe2cf38-7218-4650-add2-0d3e75918bd5.png)

### Healthy
Use interface healthy on class, if you want to do something when server is unhealthy

![moduleHealthy](https://github.com/user-attachments/assets/d84e7a03-b4b7-435f-bc82-19c5e1ccfab8)


(To use this interface, you need to first initialize health monitor)
```
moduleManager.registerAll();
moduleManager.initializeHealthMonitor();
```
Or you can give your own parameters 

```
* @param checkInterval     - how often in ticks to check the health of the server
* @param historySize       - how many times tps on the server must be below treshold
* @param TPStreshold       - below how much tps must be to be included in the history
* @param startDelay        - delay from server start (recommended not less than 200 ticks)

moduleManager.initializeHealthMonitor(checkInterval, historySize, TPStreshold, startDelay);
```

## Modularization and config
if class is annotated with @Module **AND** @Listen or @Command, it becomes a module.
(but if you dont want modules, you can use only @Listen or @Command witout it)

Annotated class is added to config.yml to path 'modules.<module_name>'. You don't have to add it manually, it will be added manually with value **true**.

![image](https://user-images.githubusercontent.com/53827110/214066217-d96d08bd-0172-4dc6-b4d1-3f8caf3d1629.png)


## Built in module management
Reloading specific module

![image](https://user-images.githubusercontent.com/53827110/214068063-487183e8-cf55-4fa8-8db6-be4909616d68.png)

Reloading all modules at once.

![image](https://user-images.githubusercontent.com/53827110/214068162-f608f07c-698f-47de-95ec-606e2a568717.png)

**Note**: Reloading also affect default config.yml -> If you disabled specific module in config.yml change will be affected, and module enabled/disabled.

## Code samples
Code samples are available in example directory in project
