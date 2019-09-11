package cn.mxcymc.calendar;

import cn.mxcymc.calendar.listener.PlayerSignInEvent;
import java.util.Calendar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CalendarEvents implements Listener{
    public Main plugin;
    
    public CalendarEvents(Main m){
        this.plugin=m;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if (!Main.config.getBoolean("open_gui_on_join"))return;
        Player player = e.getPlayer();
        this.plugin.openGui(player);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Action action = e.getAction();
        if(action==Action.RIGHT_CLICK_AIR||action==Action.RIGHT_CLICK_BLOCK){
            Player player = e.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (plugin.getReplenish().isCard(item))this.plugin.openGui(player);
        }
    }
    
    @EventHandler
    public void onPlayerSignInEvent(PlayerSignInEvent e){
    }
    
}
