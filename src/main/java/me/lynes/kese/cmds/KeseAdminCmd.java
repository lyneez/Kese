package me.lynes.kese.cmds;

import me.lynes.kese.Kese;
import me.lynes.kese.vault.KeseVaultEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class KeseAdminCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final KeseVaultEconomy economy = plugin.getEconomy();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, "Bu komudu sadece oyuncular kullanabilir.");
            return true;
        }

        Player player = (Player) sender;

        //SET KOMUDU
        if (args.length > 0 && args[0].equalsIgnoreCase("set")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    player.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage("§cBelirtilen oyuncu bulunamadı ya da çevrimiçi değil.");
                    return true;
                }

                if (economy.setBalance(target, amount).type == EconomyResponse.ResponseType.SUCCESS) {
                    player.sendMessage("§aBaşarıyla " + target.getName() + " isimli oyuncunun altın miktarı " + economy.format(amount) + " yapıldı.");
                } else {
                    player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                }

            } else {
                sendHelpMessage(player);
            }
        } else {
            sendHelpMessage(player);
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("set");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§6.oOo.__________________.[ §e/keseadmin §6].__________________.oOo.");
        player.sendMessage("§3/keseadmin §bset (oyuncu) (miktar) §7Hedef oyuncunun altın miktarını (miktar)'a değiştirir.");
    }


}
