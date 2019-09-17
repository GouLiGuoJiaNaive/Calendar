package cn.mxcymc.calendar;

import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {

    private final Main plugin;
    private final YamlConfiguration yaml;

    public Lang(Main aThis, String lang) {
        this.plugin = aThis;
        yaml = YamlConfiguration.loadConfiguration(new File("plugins/Calendar/message", lang + ".yml"));
    }

    public String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', yaml.getString(key));
    }
}
