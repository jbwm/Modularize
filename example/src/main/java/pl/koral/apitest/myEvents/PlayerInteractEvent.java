package pl.koral.apitest.myEvents;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class PlayerInteractEvent implements Listener{



    @EventHandler
    public void onInteract(org.bukkit.event.player.PlayerInteractEvent e){
        if(e.getHand() == EquipmentSlot.OFF_HAND) {
            Player player = e.getPlayer();
            Block clickedBlock = e.getClickedBlock();
            Bukkit.getPluginManager().callEvent(new PlayerRightClickEvent(player,clickedBlock));
        }
    }
}


class PlayerRightClickEvent extends Event {


    private final Player player;

    private final Block clickedBlock;

    public PlayerRightClickEvent(Player player,Block clickedBlock) {
        this.player = player;
        this.clickedBlock = clickedBlock;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }


    public Player getPlayer() {
        return player;
    }

    @Nullable
    public Block getClickedBlock() {
        return clickedBlock;
    }
}
