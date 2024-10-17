package pl.koral.apitest;

import com.google.inject.Injector;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.jbwm.modularize.Modularize;
import pl.jbwm.modularize.manager.ModuleManager;
import pl.koral.apitest.module.Test;

import javax.inject.Inject;
import java.util.Set;

public final class ApiTest extends JavaPlugin {

    @Inject
    private Test injectedTestClass;

    @Override
    public void onEnable() {
        //Here's example for simple use
        {
            //Get all classes under apitest.module
            Set<Class<?>> modularizedClasses = Modularize.scanPackage("pl.koral.apitest.module");

            //Build module manager
            ModuleManager moduleManager = Modularize.buildManager(this, modularizedClasses);
            //Register all modules
            moduleManager.registerAll();

            //If you are not a fan of dependency injection, you don't have to use it
            //Create Guice AbstractModule
            Injector injector = new SimpleBinderModule(this).createInjector();

            //Make classes capable of guice eco system.
            modularizedClasses.forEach(injector::injectMembers);


            injectedTestClass.proofDependencyInjectionWorks();
        }
        //End of example

        //Here's example for Modularize with blocked classes as paramenter
        {
            //Get all classes under apitest.module
            Set<Class<?>> modularizedClasses = Modularize.scanPackage("pl.koral.apitest.myEvents.gameplay");

            //Build module manager with blocked classes
            //So now, if somebody use PlayerInteractEvent in this project a RunTimeException will be thrown when the plugin starts blocking the plugin from launching ()
            //for further information look in package pl.jbwm.modularize#buildManager;
            ModuleManager moduleManager = Modularize.buildManager(this,classes,CheckType.EQUALS,ErrorType.RUN_TIME_EXCEPTION,"org.bukkit.event.player.PlayerInteractEvent");

            //Register all modules
            moduleManager.registerAll();

            //Heres example for health monitor
            {
                moduleManager.initializeHealthMonitor(30 * 20L,10,19.0,60 * 20L);
                //Goto HealthyTest.java for use example
            }


            //If you are not a fan of dependency injection, you don't have to use it
            //Create Guice AbstractModule
            Injector injector = new SimpleBinderModule(this).createInjector();

            //Make classes capable of guice eco system.
            modularizedClasses.forEach(injector::injectMembers);


            injectedTestClass.proofDependencyInjectionWorks();
        }
        //End of example


    }

    public void proofDependencyInjectionWorks(){
        Bukkit.getLogger().info("Dependency injection works (ApiTestClass)");
    }
}
