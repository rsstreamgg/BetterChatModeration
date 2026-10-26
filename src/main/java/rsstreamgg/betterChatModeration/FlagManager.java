package rsstreamgg.betterChatModeration;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlagManager {
    public enum FLAGS { OFF_PLATFORM, LINKS, IP_ADDRESS }
    public Map<UUID, Map<FLAGS, Integer>> flags = new HashMap<>();

    public void addFlag(Player player, FLAGS flag) {
        flags.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).merge(flag, 1, Integer::sum);
    }

    public void logFlag(Player player, FLAGS flag) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(BetterChatModeration.LOGS_PERMISSION)) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[BCM] &f" + player.getName() + "&7 failed &f" + flag));
            }
        }

        Bukkit.getLogger().warning("[BetterChatModeration] " + player.getName() + " failed " + flag.toString());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[BCM] &7Your message was flagged and blocked due to: &f" + flag));
    }

    public void flagPlayer(Player player, FLAGS flag) {
        if (!player.hasPermission(BetterChatModeration.FLAGS_EXCLUDE_PERMISSION)) {
            addFlag(player, flag);
            logFlag(player, flag);
        }
    }
}