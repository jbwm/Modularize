package pl.koral.apitest.module;

@Listen
public class HealthyTest implements Listener, Healthy {


    @EventHandler
    public void onExtremlyOverloadingThings(PlayerInteractEvent e) {
        //spawn 2 milion armorstand for cosmetic
    }


    @Override
    public void ifUnhealthy() {
        //delete 2 milion armorstand
        HandlerList.unregisterAll(this);
        Bukkit.broadcast("Lags on server! removing cosmetics!","permission.admin.info");
    }

    @Override
    public void ifBackToHealth() {
        Bukkit.getPluginManager().registerEvents(this, BackroomsCore.getInstance());
        Bukkit.broadcast("Everything back to normal","permission.admin.info");
    }
}
