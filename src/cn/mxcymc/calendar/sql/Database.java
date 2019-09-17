package cn.mxcymc.calendar.sql;

import cn.mxcymc.calendar.Main;
import static cn.mxcymc.calendar.Main.config;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Database {

    public static FileConfiguration db;
    private final Main plugin;
    public File file = new File("plugins/Calendar", "database.yml");
    private Format format;
    private String user;
    private String password;
    private String url;

    public Database(Main m) throws SQLException, IOException, UnsupportedOperationException {
        this.plugin = m;
        if (config.getString("database").equalsIgnoreCase("file")) {
            format = Format.FILE;
            if (!file.exists()) {
                file.createNewFile();
            }
            db = YamlConfiguration.loadConfiguration(file);
        } else if (config.getString("database").equalsIgnoreCase("sql") || config.getString("database").equalsIgnoreCase("sqlite")) {
            format = Format.SQL;
        } else if (config.getString("database").equalsIgnoreCase("mysql")) {
            format = Format.MYSQL;
            this.user = config.getString("mysql.user");
            this.password = config.getString("mysql.password");
            this.url = "jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port") + "/" + config.getString("mysql.database");
            //如果数据库不存在则创建
            Connection con = DriverManager.getConnection("jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port") + "/mysql", user, password);
            Statement s = con.createStatement();
            ResultSet result = s.executeQuery("SHOW DATABASES LIKE \"" + config.getString("mysql.database") + "\"");
            boolean check = false;
            while (result.next()) {
                check = true;
            }
            if (!check) {
                s.executeUpdate("CREATE DATABASE `" + config.getString("mysql.database") + "`");
            }
            s.close();
            con.close();
            //如果表不存在则创建
            con = DriverManager.getConnection(url, user, password);
            s = con.createStatement();
            ResultSet rs = s.executeQuery("SHOW TABLES LIKE \"calendar\"");
            check = false;
            while (rs.next()) {
                check = true;
            }
            if (!check) {
                s.executeUpdate("CREATE TABLE `calendar` (`id` int(10) unsigned NOT NULL AUTO_INCREMENT, `player` varchar(255) DEFAULT NULL, `month` varchar(255) DEFAULT NULL, `week` varchar(255) DEFAULT NULL, `saveweek` int(10) DEFAULT NULL, `savemonth` int(10) DEFAULT NULL, `history` text, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=803 DEFAULT CHARSET=utf8");
            }
            s.close();
            con.close();
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public String getString(String name, String col) {
        if (format != null) {
            switch (format) {
                case FILE:
                    String a = db.getString(name + "." + col);
                    if (a != null) {
                        return a;
                    }
                    return "";
                case MYSQL:
                    try {
                        try (Connection con = DriverManager.getConnection(url, user, password); Statement s = con.createStatement()) {
                            ResultSet rs = s.executeQuery("SELECT * FROM `calendar` WHERE `player` = '" + name + "'");
                            while (rs.next()) {
                                String b = rs.getString(col);
                                if (b != null) {
                                    return b;
                                }
                            }
                        }
                    } catch (SQLException ex) {
                    }
                    return "";
                case SQL:
                    break;
                default:
                    break;
            }
        }
        return "";
    }

    public void set(String name, String col, String value) {
        if (format != null) {
            switch (format) {
                case FILE:
                    db.set(name + "." + col, value);
                    try {
                        db.save(file);
                    } catch (IOException ex) {
                    }
                    break;
                case MYSQL:
                    try {
                        try (Connection con = DriverManager.getConnection(url, user, password); Statement s = con.createStatement()) {
                            ResultSet rs = s.executeQuery("SELECT * FROM `calendar` WHERE `player` = '" + name + "'");
                            if (!rs.next()) {
                                s.executeUpdate("INSERT INTO `calendar` (`player`,`" + col + "`) VALUES ('" + name + "','" + value.replace(",", "\\,") + "')");
                            } else {
                                s.executeUpdate("UPDATE `calendar` SET `" + col + "` = '" + value.replace(",", "\\,") + "' WHERE `player` = '" + name + "'");
                            }
                        }
                    } catch (SQLException ex) {
                    }
                case SQL:
                    break;
                default:
                    break;
            }
        }
    }

    public int getInt(String name, String col) {
        if (format != null) {
            switch (format) {
                case FILE:
                    return db.getInt(name + "." + col);
                case MYSQL:
                    try {
                        try (Connection con = DriverManager.getConnection(url, user, password); Statement s = con.createStatement()) {
                            ResultSet rs = s.executeQuery("SELECT * FROM `calendar` WHERE `player` = '" + name + "'");
                            while (rs.next()) {
                                return rs.getInt(col);
                            }
                        }
                    } catch (SQLException ex) {
                    }
                    return 0;
                case SQL:
                    break;
                default:
                    break;
            }
        }
        return 0;
    }
}
