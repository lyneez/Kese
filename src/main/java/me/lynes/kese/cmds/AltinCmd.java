package me.lynes.kese.cmds;

import me.lynes.kese.Kese;
import me.lynes.kese.vault.KeseVaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class AltinCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final KeseVaultEconomy economy = plugin.getEconomy();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, "Bu komudu sadece oyuncular kullanabilir.");
            return true;
        }

        Player player = (Player) sender;

        //GONDER KOMUDU
        if (args.length > 0 && args[0].equalsIgnoreCase("gonder")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 1) {
                    player.sendMessage("§cMiktar birden küçük olamaz.");
                    return true;
                }

                if (economy.getBalance(player) >= amount) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        player.sendMessage("§cBelirtilen oyuncu bulunamadı ya da çevrimiçi değil.");
                        return true;
                    }


                    if (target == player) {
                        player.sendMessage("§cKendine altın gönderemezsin!");
                        return true;
                    }

                    if(!economy.withdrawPlayer(player, amount).transactionSuccess()) {
                        player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        return true;
                    }

                    if(!economy.depositPlayer(target, amount).transactionSuccess()) {
                        player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        return true;
                    }


                    String pattern = "0.00";
                    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
                    decimalFormat.applyPattern(pattern);

                    String formatted = decimalFormat.format(amount);

                    player.sendMessage("Yeni altın miktarı §6" + economy.getBalance(player) + " §6Altın");
                    player.sendMessage("§c§l- §c" + formatted);
                    target.sendMessage("Yeni altın miktarı §6" + economy.getBalance(target) + " §6Altın");
                    target.sendMessage("§a§l+ §a" + formatted);
                    return true;
                } else {
                    player.sendMessage("§cKesenizde yeterli miktarda altın yok.");
                    return true;
                }

            } else {
                sendHelpMessage(player);
                return true;
            }
        } else {
            sendHelpMessage(player);
        }


        return true;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1){
            completions.add("gonder");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }


    private void sendHelpMessage(Player player) {
        player.sendMessage("§6.oOo.__________________.[ §e/kese §6].__________________.oOo.");
        player.sendMessage("§3/kese §7Kesenizde bulunan altını gösterir.");
        player.sendMessage("§3/kese §bkoy (miktar) §7Keseye altın koyar.");
        player.sendMessage("§3/kese §bal (miktar) §7Keseden altın alır.");
        player.sendMessage("§3/altin §bgonder (oyuncu) (miktar) §7Hedef oyuncuya altın gönderir.");
    }


}
