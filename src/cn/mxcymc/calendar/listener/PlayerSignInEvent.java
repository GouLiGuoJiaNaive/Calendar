package cn.mxcymc.calendar.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSignInEvent extends Event{
    private static final HandlerList HANDERLIST = new HandlerList();
    private final Player player;
    private final List<String> month_list;
    private final List<String> week_list;
    private final boolean is;
    private boolean cancel;
    
    public PlayerSignInEvent(Player player, String name, List<String> week,List<String> month,boolean is) {
	super();
	this.player = player;
        
        this.week_list=new ArrayList<>();
        this.week_list.addAll(week);
        this.week_list.add(name);
        Collections.sort(week_list);
        
        this.month_list=new ArrayList<>();
        this.month_list.addAll(month);
        this.month_list.add(name);
        Collections.sort(month_list);
        
        this.is=is;
        this.cancel=false;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDERLIST;
    }
    
    public static HandlerList getHandlerList() {
        return HANDERLIST;
    }
    
    public boolean isReplenish(){
        return this.is;
    }
    
    public boolean isCancelled(){
        return this.cancel;
    }
    
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    public Player getPlayer(){
        return this.player;
    }
    
    public List<String> getWeekList(){
        return this.week_list;
    }
    
    public List<String> getMonthList(){
        return this.month_list;
    }
}
