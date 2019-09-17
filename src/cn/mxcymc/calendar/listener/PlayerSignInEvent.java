package cn.mxcymc.calendar.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSignInEvent extends Event {

    private static final HandlerList HANDERLIST = new HandlerList();
    private final Player player;
    private final List<String> month_list;
    private final List<String> week_list;
    private final boolean is;
    private boolean cancel;

    public PlayerSignInEvent(Player player, String name, List<String> week, List<String> month, boolean is) {
        super();
        this.player = player;

        this.week_list = new ArrayList<>();
        this.week_list.addAll(week);
        this.week_list.add(name);
        Collections.sort(week_list);

        this.month_list = new ArrayList<>();
        this.month_list.addAll(month);
        this.month_list.add(name);
        Collections.sort(month_list);

        this.is = is;
        this.cancel = false;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDERLIST;
    }

    public static HandlerList getHandlerList() {
        return HANDERLIST;
    }

    /**
     * 玩家是否为补签
     *
     * @return true/false
     */
    public boolean isReplenish() {
        return this.is;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    /**
     * 是否取消事件
     *
     * @param cancel true/false
     */
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * 获取签到玩家
     *
     * @return
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * 获取签到周累计
     *
     * @return 签到周累计
     */
    public List<String> getWeekList() {
        return this.week_list;
    }

    /**
     * 获取签到月累计
     *
     * @return 签到月累计
     */
    public List<String> getMonthList() {
        return this.month_list;
    }
}
