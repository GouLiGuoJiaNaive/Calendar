package cn.mxcymc.calendar;

import cn.mxcymc.calendar.sql.Database;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static final File FILE = new File("plugins/Calendar", "config.yml");
    public Database database;
    private static HashMap<String, CalendarGui> map;
    public static FileConfiguration config;
    public static Lang lang;
    private Replenish card;

    @Override
    public void onEnable() {
        map = new HashMap<>();
        if (!FILE.exists()) {
            this.saveDefaultConfig();
        }
        config = this.getConfig();
        lang = new Lang(this, config.getString("language"));
        card = new Replenish(this);
        if (!(new File("plugins/Calendar/message", "zh_cn.yml")).exists()) {
            this.saveResource("message/zh_cn.yml", true);
        }
        try {
            database = new Database(this);
        } catch (UnsupportedOperationException e) {
            this.getLogger().log(Level.SEVERE, ChatColor.stripColor(Message("fail_to_load")));
            this.onDisable();
        } catch (SQLException ex) {
            this.getLogger().log(Level.SEVERE, ChatColor.stripColor(Message("fail_to_connect")));
            this.onDisable();
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, ChatColor.stripColor(Message("fail_to_creat_file")));
            this.onDisable();
        }
        Bukkit.getPluginManager().registerEvents(new CalendarEvents(this), this);
    }

    @Override
    public void onDisable() {
        config = null;
        lang = null;
        map = null;
    }

    public void reload() {
        onDisable();
        onEnable();
    }

    public static HashMap<String, CalendarGui> getMap() {
        return Main.map;
    }

    public Replenish getReplenish() {
        return this.card;
    }

    public static String Message(String path) {
        return lang.getMessage(path);
    }

    public void openGui(Player p) {
        if (Main.map.containsKey(p.getName())) {
            CalendarGui calendarGui = Main.map.get(p.getName());
            calendarGui.openGui(p);
        } else {
            CalendarGui calendarGui = new CalendarGui(this, p);
            calendarGui.openGui(p);
            Main.map.put(p.getName(), calendarGui);
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, org.bukkit.command.Command cmnd, String string, String[] strings) {
        if (cmnd.getName().equalsIgnoreCase("calendar")) {
            if (strings.length == 0 && cs instanceof Player) {
                Player p = (Player) cs;
                this.openGui(p);
                return true;
            }
            if (strings.length == 1 && strings[0].equals("reload")) {
                if (cs instanceof Player && cs.hasPermission("calender.admin") || cs instanceof ConsoleCommandSender) {
                    this.reload();
                }
                return true;
            }
            if (strings.length == 1 && strings[0].equals("give")) {
                if (cs instanceof Player && cs.hasPermission("calender.admin")) {
                    Player p = (Player) cs;
                    this.card.giveCard(p);
                    p.sendMessage("给予" + p.getName() + "一张" + ChatColor.translateAlternateColorCodes('&', config.getString("replenish_card.name")));
                }
                return true;
            }
            if (strings.length == 2 && strings[0].equals("give")) {
                Player player = Bukkit.getPlayer(strings[1]);
                if (cs instanceof Player && cs.hasPermission("calender.admin") || cs instanceof ConsoleCommandSender) {
                    if (player.isOnline()) {
                        this.card.giveCard(player);
                        player.sendMessage("给予" + player.getName() + "一张" + ChatColor.translateAlternateColorCodes('&', config.getString("replenish_card.name")));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static HashMap<String, List<String>> week() {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        Set<String> keys = config.getKeys(true);
        keys.stream().filter((key) -> (key.contains("commands.week."))).forEachOrdered((key) -> {
            hashMap.put(key.replace("commands.week.", ""), config.getStringList(key));
        });
        return hashMap;
    }

    public static HashMap<String, List<String>> month() {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        Set<String> keys = config.getKeys(true);
        keys.stream().filter((key) -> (key.contains("commands.month."))).forEachOrdered((key) -> {
            hashMap.put(key.replace("commands.month.", ""), config.getStringList(key));
        });
        return hashMap;
    }
}
