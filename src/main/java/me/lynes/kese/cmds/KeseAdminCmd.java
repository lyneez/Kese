package me.lynes.kese.cmds;

import me.lynes.kese.Kese;
import me.lynes.kese.vault.KeseVaultEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class KeseAdminCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final KeseVaultEconomy economy = plugin.getEconomy();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("set")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    sender.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                /* NOT:
                 getOfflinePlayer offline mode içinde sorun çıkarmıyor
                 */
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                if (!target.hasPlayedBefore()) {
                    sender.sendMessage("§cBelirtilen oyuncu bulunamadı.");
                    return true;
                }

                if (economy.setBalance(target, amount).type == EconomyResponse.ResponseType.SUCCESS) {
                    sender.sendMessage("§aBaşarıyla " + target.getName() + " isimli oyuncunun altın miktarı " + economy.format(amount) + " yapıldı.");
                } else {
                    sender.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                }

                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("add")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    sender.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                if (!target.hasPlayedBefore()) {
                    sender.sendMessage("§cBelirtilen oyuncu bulunamadı.");
                    return true;
                }

                if (economy.setBalance(target, economy.getBalance(target) + amount).type == EconomyResponse.ResponseType.SUCCESS) {
                    sender.sendMessage("§aBaşarıyla " + target.getName() + " isimli oyuncunun altın miktarına  " + amount + " eklendi.");
                } else {
                    sender.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                }

                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("remove")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    sender.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                if (!target.hasPlayedBefore()) {
                    sender.sendMessage("§cBelirtilen oyuncu bulunamadı.");
                    return true;
                }

                if (economy.setBalance(target, Math.max(0, economy.getBalance(target) - amount)).type == EconomyResponse.ResponseType.SUCCESS) {
                    sender.sendMessage("§aBaşarıyla " + target.getName() + " isimli oyuncunun altın miktarından  " + amount + " çıkarıldı.");
                } else {
                    sender.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                }

                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("balance")) {
            if (args.length == 2) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                if (!target.hasPlayedBefore()) {
                    sender.sendMessage("§cBelirtilen oyuncu bulunamadı.");
                    return true;
                }

                sender.sendMessage("§a" + target.getName() + "§a'in kesesi: " + economy.getBalance(target));
                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }


        sendHelpMessage(sender);


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("set");
            completions.add("add");
            completions.add("remove");
            completions.add("balance");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }

    private void sendHelpMessage(CommandSender player) {
        player.sendMessage("§6.oOo.__________________.[ §e/keseadmin §6].__________________.oOo.");
        player.sendMessage("§3/keseadmin §bset (oyuncu) (miktar) §7Hedef oyuncunun altın miktarını (miktar)'a değiştirir.");
        player.sendMessage("§3/keseadmin §badd (oyuncu) (miktar) §7Hedef oyuncunun altın miktarına (miktar) ekler.");
        player.sendMessage("§3/keseadmin §bremove (oyuncu) (miktar) §7Hedef oyuncunun altın miktarından (miktar) çıkarır.");
        player.sendMessage("§3/keseadmin §bbalance (oyuncu) §7Hedef oyuncunun altın miktarını gösterir.");

    }

}
