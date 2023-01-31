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
        //Get all classes under apitest.module
        Set<Class<?>> modularizedClasses = Modularize.scanPackage("pl.koral.apitest.module");

        //Build module manager
        ModuleManager moduleManager = Modularize.buildManager(this, modularizedClasses);
        //Register all modules
        moduleManager.registerAll();

        //Create Guice AbstractModule
        Injector injector = new SimpleBinderModule(this).createInjector();

        //Make classes capable of guice eco system.
        modularizedClasses.forEach(injector::injectMembers);


        injectedTestClass.proofDependencyInjectionWorks();


    }

    public void proofDependencyInjectionWorks(){
        Bukkit.getLogger().info("Dependency injection works (ApiTestClass)");
    }
}
