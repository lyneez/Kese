package me.lynes.kese.utils;

import me.lynes.kese.Kese;
import org.bukkit.OfflinePlayer;

public class PlayerUtil {
    public static OfflinePlayer getOfflinePlayer(String player) {
        OfflinePlayer[] players = Kese.getInstance().getServer().getOfflinePlayers();

        for (OfflinePlayer op : players) {
            if (player != null && player.equals(op.getName())) {
                return op;
            }
        }

        return null;
    }

}
