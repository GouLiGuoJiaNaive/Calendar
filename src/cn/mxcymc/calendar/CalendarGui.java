package cn.mxcymc.calendar;

import cn.mxcymc.calendar.listener.PlayerSignInEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lk.vexview.api.VexViewAPI;
import lk.vexview.gui.VexGui;
import lk.vexview.gui.components.VexButton;
import lk.vexview.gui.components.VexHoverText;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class CalendarGui {

    private final Main plugin;
    private final String player;
    private int lookmonth;
    private int lookyear;
    private int savemonth;
    private int saveweek;
    private int week;
    private int month;
    private int year;
    public List<String> history;
    public List<String> month_list;
    public List<String> week_list;

    public CalendarGui(Main m, Player p) {
        this.plugin = m;
        this.player = p.getName();
        Calendar instance = Calendar.getInstance();
        this.year = instance.get(Calendar.YEAR);
        this.month = instance.get(Calendar.MONTH) + 1;
        this.week = instance.get(Calendar.WEEK_OF_YEAR);
        this.lookyear = this.year;
        this.lookmonth = this.month;
        this.savemonth = plugin.database.getInt(this.player, "savemonth");
        if (this.savemonth == 0) {
            this.savemonth = this.month;
            plugin.database.set(this.player, "savemonth", String.valueOf(this.savemonth));
        }
        this.saveweek = plugin.database.getInt(this.player, "saveweek");
        if (this.saveweek == 0) {
            this.saveweek = this.week;
            plugin.database.set(this.player, "saveweek", String.valueOf(this.saveweek));
        }
        this.month_list = new ArrayList<>();
        String get = plugin.database.getString(this.player, "month").replace("[", "").replace("]", "").replace(" ", "");
        //player.sendMessage(getString);
        if (!get.equals("")) {
            String[] split = get.split(",");
            month_list.addAll(Arrays.asList(split));
        }
        this.week_list = new ArrayList<>();
        get = plugin.database.getString(this.player, "week").replace("[", "").replace("]", "").replace(" ", "");
        if (!get.equals("")) {
            String[] split = get.split(",");
            week_list.addAll(Arrays.asList(split));
        }
        this.history = new ArrayList<>();
        get = plugin.database.getString(this.player, "history").replace("[", "").replace("]", "").replace(" ", "");
        if (!get.equals("")) {
            String[] split = get.split(",");
            history.addAll(Arrays.asList(split));
        }
    }

    public static CalendarGui getCalendarGui(String name) {
        if (Main.getMap().containsKey(name)) {
            CalendarGui calendarGui = Main.getMap().get(name);
            return calendarGui;
        }
        return null;
    }

    public void openGui(Player p) {
        VexGui gui = new VexGui("[local]calendar/background.png", -1, -1, 600, 400, 600 / 2, 400 / 2);
        Calendar instance = Calendar.getInstance();
        this.year = instance.get(Calendar.YEAR);
        this.month = instance.get(Calendar.MONTH) + 1;
        this.week = instance.get(Calendar.WEEK_OF_YEAR);
        if (this.savemonth < this.month) {
            refreshMonth();
        }
        if (this.saveweek < this.week) {
            refreshWeek();
        }
        Calendar today = Calendar.getInstance();
        int today_day = today.get(Calendar.DAY_OF_MONTH);
        int today_month = today.get(Calendar.MONTH) + 1;
        int today_year = today.get(Calendar.YEAR);
        int today_week = today.get(Calendar.DAY_OF_WEEK);
        instance.set(lookyear, lookmonth - 1, 1);
        int startDay = instance.get(Calendar.DAY_OF_WEEK);
        int maxDayInMonth = maxDayInMonth(lookyear, lookmonth);
        int count = startDay - 1;
        for (int i = 0; i < 42; i++) {
            int row = i / 7;
            int col = i % 7;
            if (i < count) {
                final int name = maxDayInMonth(lookyear, lookmonth - 1) - count + i + 1;
                VexButton a = new VexButton("date" + i, "\u00a77" + name, "[local]calendar/blank.png", "[local]calendar/blank.png", 66 + 24 * col, 16 + 24 * row, 24, 24);
                gui.addComponent(a);
            } else if (i >= count + maxDayInMonth) {
                final int name = i - count - maxDayInMonth + 1;
                VexButton a = new VexButton("date" + i, "\u00a77" + name, "[local]calendar/blank.png", "[local]calendar/blank.png", 66 + 24 * col, 16 + 24 * row, 24, 24);
                gui.addComponent(a);
            } else {
                VexButton a;
                String color = col == 0 || col == 6 ? "\u00a7c" : "\u00a78";
                final int name = i - count + 1;
                if (this.lookmonth == today_month && name == today_day && this.lookyear == today_year) {
                    a = new VexButton("date" + i, color + name, "[local]calendar/red.png", "[local]calendar/green.png", 66 + 24 * col, 16 + 24 * row, 24, 24, (Player p1) -> {
                        if (!month_list.contains(String.valueOf(name))) {
                            sign(name);
                        }
                    }, new VexHoverText(Arrays.asList("\u00a76" + parseDate(this.lookyear, this.lookmonth - 1, name), Main.Message(month_list.contains(String.valueOf(name)) ? "signed" : "sign"))));
                } else if (this.lookmonth == today_month && name < today_day) {
                    a = new VexButton("date" + i, color + name, "[local]calendar/blank.png", "[local]calendar/green.png", 66 + 24 * col, 16 + 24 * row, 24, 24, (Player p1) -> {
                        if (!month_list.contains(String.valueOf(name))) {
                            if (today_day - name > today_week - 1) {
                                replenishMonth(name);
                            } else {
                                replenish(name);
                            }
                        }
                    }, new VexHoverText(Arrays.asList("\u00a76" + parseDate(this.lookyear, this.lookmonth - 1, name), Main.Message(month_list.contains(String.valueOf(name)) ? "signed" : plugin.getReplenish().hasCard(player) ? "replenish" : "cannot_replenish"))));
                } else if (this.lookmonth < today_month || this.lookyear < today_year) {
                    instance.set(lookyear, today_month - 1, 1);
                    int nextstartDay = instance.get(Calendar.DAY_OF_WEEK);
                    int nextcount = nextstartDay - 1;
                    if (nextcount > 0 && this.lookmonth == today_month - 1 && today_day + nextcount <= 7 && name > maxDayInMonth - nextcount) {
                        a = new VexButton("date" + i, color + name, "[local]calendar/blank.png", "[local]calendar/green.png", 66 + 24 * col, 16 + 24 * row, 24, 24, (Player p1) -> {
                            if (!week_list.contains(String.valueOf(name))) {
                                replenishWeek(name);
                            }
                        }, new VexHoverText(Arrays.asList("\u00a76" + parseDate(this.lookyear, this.lookmonth - 1, name), Main.Message(month_list.contains(String.valueOf(name)) ? "signed" : plugin.getReplenish().hasCard(player) ? "replenish" : "cannot_replenish"))));

                    } else {
                        a = new VexButton("date" + i, color + name, "[local]calendar/blank.png", "[local]calendar/blank.png", 66 + 24 * col, 16 + 24 * row, 24, 24, new VexHoverText(Arrays.asList("\u00a76" + parseDate(this.lookyear, this.lookmonth - 1, name), Main.Message("sign_disabled"))));
                    }
                } else {
                    a = new VexButton("date" + i, color + name, "[local]calendar/blank.png", "[local]calendar/blank.png", 66 + 24 * col, 16 + 24 * row, 24, 24, new VexHoverText(Arrays.asList("\u00a76" + parseDate(this.lookyear, this.lookmonth - 1, name), Main.Message("sign_not_open"))));
                }
                gui.addComponent(a);

                if (ishistory(this.lookyear, this.lookmonth - 1, name)) {
                    gui.addComponent(new VexButton("success" + i, "", "[local]calendar/check.png", "[local]calendar/check.png", 80 + 24 * col, 30 + 24 * row, 10, 10));
                }
            }
        }
        VexButton a = new VexButton("gui3" + 2, "<<", "[local]calendar/blank.png", "[local]calendar/green.png", 132 / 2, 320 / 2, 24, 24, (Player p1) -> {
            if (this.lookyear > 0) {
                this.lookyear--;
            }
            openGui(p1);
        }, new VexHoverText(Arrays.asList(Main.Message("sub_year"))));
        VexButton b = new VexButton("gui3" + 3, "<<", "[local]calendar/blank.png", "[local]calendar/green.png", 180 / 2, 320 / 2, 24, 24, (Player p1) -> {
            if (this.lookmonth == 1) {
                this.lookyear--;
                this.lookmonth = 12;
            } else {
                this.lookmonth--;
            }
            openGui(p1);
        }, new VexHoverText(Arrays.asList(Main.Message("sub_month"))));
        VexButton c = new VexButton("gui3" + 4, ">>", "[local]calendar/blank.png", "[local]calendar/green.png", 372 / 2, 320 / 2, 24, 24, (Player p1) -> {
            if (this.lookmonth == 12) {
                this.lookyear++;
                this.lookmonth = 1;
            } else {
                this.lookmonth++;
            }
            openGui(p1);
        }, new VexHoverText(Arrays.asList(Main.Message("add_month"))));
        VexButton d = new VexButton("gui3" + 5, ">>", "[local]calendar/blank.png", "[local]calendar/green.png", 420 / 2, 320 / 2, 24, 24, (Player p1) -> {
            this.lookyear++;
            openGui(p1);
        }, new VexHoverText(Arrays.asList(Main.Message("add_year"))));
        VexButton text = new VexButton("gui3" + 6, "\u00a76" + parseMonth(this.lookyear, this.lookmonth - 1), "[local]calendar/blank.png", "[local]calendar/blank.png", 228 / 2, 320 / 2, 144 / 2, 24);
        gui.addComponent(a);
        gui.addComponent(b);
        gui.addComponent(c);
        gui.addComponent(d);
        gui.addComponent(text);
        VexViewAPI.openGui(p, gui);
    }

    public static int maxDayInMonth(int year, int month) {
        int max = 30;
        if (month == 0 | month == 1 | month == 3 | month == 5 | month == 7 | month == 8 | month == 10 | month == 12) {
            max = 31;
        }
        if (month == 2) {
            max = 28;
        }
        if (month == 2 & (year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
            max = 29;
        }
        return max;
    }

    private void sign(int name) {
        PlayerSignInEvent event = new PlayerSignInEvent(Bukkit.getPlayer(player), String.valueOf(name), this.week_list, this.month_list, false);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        addhistory(this.lookyear, this.lookmonth - 1, name);
        month_list.add(String.valueOf(name));
        week_list.add(String.valueOf(name));
        Collections.sort(month_list);
        Collections.sort(week_list);
        plugin.database.set(this.player, "month", month_list.toString());
        plugin.database.set(this.player, "week", week_list.toString());
        check();
    }

    private void replenish(int name) {
        PlayerSignInEvent event = new PlayerSignInEvent(Bukkit.getPlayer(player), String.valueOf(name), this.week_list, this.month_list, true);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getReplenish().hasCard(Bukkit.getPlayer(player))) {
            addhistory(this.lookyear, this.lookmonth - 1, name);
            plugin.getReplenish().consumeCard(Bukkit.getPlayer(player));
            month_list.add(String.valueOf(name));
            week_list.add(String.valueOf(name));
            Collections.sort(month_list);
            Collections.sort(week_list);
            plugin.database.set(this.player, "month", month_list.toString());
            plugin.database.set(this.player, "week", week_list.toString());
            check();
        }
    }

    private void replenishMonth(int name) {
        PlayerSignInEvent event = new PlayerSignInEvent(Bukkit.getPlayer(player), String.valueOf(name), this.week_list, this.month_list, true);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getReplenish().hasCard(Bukkit.getPlayer(player))) {
            addhistory(this.lookyear, this.lookmonth - 1, name);
            plugin.getReplenish().consumeCard(Bukkit.getPlayer(player));
            month_list.add(String.valueOf(name));
            Collections.sort(month_list);
            plugin.database.set(this.player, "month", month_list.toString());
            plugin.database.set(this.player, "week", week_list.toString());
            check();
        }
    }

    private void replenishWeek(int name) {
        PlayerSignInEvent event = new PlayerSignInEvent(Bukkit.getPlayer(player), String.valueOf(name), this.week_list, this.month_list, true);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if (plugin.getReplenish().hasCard(Bukkit.getPlayer(player))) {
            addhistory(this.lookyear, this.lookmonth - 1, name);
            plugin.getReplenish().consumeCard(Bukkit.getPlayer(player));
            week_list.add(String.valueOf(name));
            Collections.sort(week_list);
            plugin.database.set(this.player, "month", month_list.toString());
            plugin.database.set(this.player, "week", week_list.toString());
            check();
        }
    }

    private void check() {
        HashMap<String, List<String>> weeks = Main.week();
        openGui(Bukkit.getPlayer(player));
        if (weeks.containsKey(String.valueOf(week_list.size()))) {
            List<String> get = weeks.get(String.valueOf(week_list.size()));
            get.forEach((cmd) -> {
                if (cmd.contains("[CONSOLE]")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("[CONSOLE]", "").replace("%player%", this.player));
                } else if (cmd.contains("[OP]")) {
                    if (!Bukkit.getPlayer(player).isOp()) {
                        Bukkit.getPlayer(player).setOp(true);
                        Bukkit.dispatchCommand(Bukkit.getPlayer(player), cmd.replace("[OP]", "").replace("%player%", this.player));
                        Bukkit.getPlayer(player).setOp(false);
                    } else {
                        Bukkit.dispatchCommand(Bukkit.getPlayer(player), cmd.replace("[OP]", "").replace("%player%", this.player));
                    }
                } else {
                    Bukkit.dispatchCommand(Bukkit.getPlayer(player), cmd.replace("%player%", this.player));
                }
            });
        }
        HashMap<String, List<String>> months = Main.month();
        if (months.containsKey(String.valueOf(month_list.size()))) {
            List<String> get = months.get(String.valueOf(month_list.size()));
            get.forEach((cmd) -> {
                if (cmd.contains("[CONSOLE]")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("[CONSOLE]", "").replace("%player%", this.player));
                } else if (cmd.contains("[OP]")) {
                    if (!Bukkit.getPlayer(player).isOp()) {
                        Bukkit.getPlayer(player).setOp(true);
                        Bukkit.dispatchCommand(Bukkit.getPlayer(player), cmd.replace("[OP]", "").replace("%player%", this.player));
                        Bukkit.getPlayer(player).setOp(false);
                    } else {
                        Bukkit.dispatchCommand(Bukkit.getPlayer(player), cmd.replace("[OP]", "").replace("%player%", this.player));
                    }
                } else {
                    Bukkit.dispatchCommand(Bukkit.getPlayer(player), cmd.replace("%player%", this.player));
                }
            });
        }
        List<String> get = Main.config.getStringList("commands.daily");
        get.forEach((cmd) -> {
            if (cmd.contains("[CONSOLE]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("[CONSOLE]", "").replace("%player%", this.player));
            } else if (cmd.contains("[OP]")) {
                if (!Bukkit.getPlayer(player).isOp()) {
                    Bukkit.getPlayer(player).setOp(true);
                    Bukkit.dispatchCommand(Bukkit.getPlayer(player), cmd.replace("[OP]", "").replace("%player%", this.player));
                    Bukkit.getPlayer(player).setOp(false);
                } else {
                    Bukkit.dispatchCommand(Bukkit.getPlayer(player), cmd.replace("[OP]", "").replace("%player%", this.player));
                }
            } else {
                Bukkit.dispatchCommand(Bukkit.getPlayer(player), cmd.replace("%player%", this.player));
            }
        });
        Bukkit.getPlayer(player).sendMessage(Main.Message("total_week").replace("{0}", String.valueOf(week_list.size())));
        Bukkit.getPlayer(player).sendMessage(Main.Message("total_month").replace("{0}", String.valueOf(month_list.size())));
    }

    private void refreshMonth() {
        this.savemonth = this.month;
        month_list = new ArrayList<>();
        plugin.database.set(this.player, "month", month_list.toString());
        plugin.database.set(this.player, "savemonth", String.valueOf(this.savemonth));
    }

    private void refreshWeek() {
        this.saveweek = this.week;
        week_list = new ArrayList<>();
        plugin.database.set(this.player, "week", week_list.toString());
        plugin.database.set(this.player, "saveweek", String.valueOf(this.saveweek));
    }

    private String parseDate(int lookyear, int i, int name) {
        Calendar instance = Calendar.getInstance();
        instance.set(lookyear, i, name);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Main.config.getString("date_format"));
        return simpleDateFormat.format(instance.getTime());
    }

    private String parseMonth(int lookyear, int i) {
        Calendar instance = Calendar.getInstance();
        instance.set(lookyear, i, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Main.config.getString("month_format"));
        return simpleDateFormat.format(instance.getTime());
    }

    private void addhistory(int year, int month, int date) {
        Calendar instance = Calendar.getInstance();
        instance.set(year, month, date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        this.history.add(simpleDateFormat.format(instance.getTime()));
        Collections.sort(history);
        plugin.database.set(this.player, "history", this.history.toString());
    }

    private boolean ishistory(int year, int month, int date) {
        Calendar instance = Calendar.getInstance();
        instance.set(year, month, date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String format = simpleDateFormat.format(instance.getTime());
        return history.toString().contains(format);
    }

}
