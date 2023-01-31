package pl.koral.apitest.module;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.jbwm.modularize.annotation.Listen;
import pl.jbwm.modularize.annotation.Module;
import pl.jbwm.modularize.manager.Initializable;
import pl.koral.apitest.ApiTest;

import javax.inject.Inject;

@Listen
@Module(name = "Test")
public class Test implements Listener, Initializable {

    @Inject
    private ApiTest apiTest;

    @EventHandler
    public void hello(PlayerJoinEvent ev){
        Bukkit.getLogger().info("Hello");
    }

    public void proofDependencyInjectionWorks(){
        Bukkit.getLogger().info("Dependency injection works.");
    }

    @Override
    public void init() {
        Bukkit.getLogger().info("I am doing something during initialization.");
    }
}
