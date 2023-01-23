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
        <groupId>com.github.jedwabnydev</groupId>
        <artifactId>Modularize</artifactId>
        <version>YOUR-VERSION-BASED-ON-TAG</version>
    </dependency>
</dependencies>
```
## Usage


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


## Modularization and config
if class is annotated with @Module **AND** @Listen or @Command, it becomes a module.

Annotated class is added to config.yml to path 'modules.<module_name>'. You don't have to add it manually, it will be added manually with value **true**.

![image](https://user-images.githubusercontent.com/53827110/214066217-d96d08bd-0172-4dc6-b4d1-3f8caf3d1629.png)


## Built in module management
Reloading specific module

![image](https://user-images.githubusercontent.com/53827110/214068063-487183e8-cf55-4fa8-8db6-be4909616d68.png)

Reloading all modules at once.

![image](https://user-images.githubusercontent.com/53827110/214068162-f608f07c-698f-47de-95ec-606e2a568717.png)

**Note**: Reloading also affect default config.yml -> If you disabled specific module in config.yml change will be affected, and module enabled/disabled.