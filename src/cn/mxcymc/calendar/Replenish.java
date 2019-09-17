package cn.mxcymc.calendar;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Replenish {

    private final String name;
    private final Main plugin;
    private final List<String> lore;
    private final Material material;
    private final ItemStack itemStack;

    public Replenish(Main aThis) {
        plugin = aThis;
        name = ChatColor.translateAlternateColorCodes('&', Main.config.getString("replenish_card.name"));
        material = Material.getMaterial(Main.config.getString("replenish_card.material"));
        lore = Main.config.getStringList("replenish_card.lore");
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
        }
        itemStack = new ItemStack(material, 1, (short) Main.config.getInt("replenish_card.data"));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

    public void giveCard(Player p) {
        p.getInventory().addItem(itemStack);
    }

    public boolean hasCard(Player p) {
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (this.isCard(item)) {
                return true;
            }
        }
        return p.getInventory().contains(itemStack);
    }

    public boolean isCard(ItemStack item) {
        if (item != null && item.getType() == material && item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.hasDisplayName() && itemMeta.hasLore()) {
                if (itemMeta.getDisplayName().equals(name) && itemMeta.getLore().equals(lore)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void consumeCard(Player p) {
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (this.isCard(item)) {
                int amount = p.getInventory().getItem(i).getAmount();
                p.getInventory().getItem(i).setAmount(amount - 1);
                return;
            }
        }
    }

    public boolean hasCard(String player) {
        return hasCard(Bukkit.getPlayer(player));
    }

    public void consumeCard(String player) {
        consumeCard(Bukkit.getPlayer(player));
    }
}
