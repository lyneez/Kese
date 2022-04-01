package me.lynes.kese.cmds;

import me.lynes.kese.Kese;
import me.lynes.kese.vault.KeseVaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.logging.Level;

public class KeseCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final KeseVaultEconomy economy = plugin.getEconomy();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, "Bu komudu sadece oyuncular kullanabilir.");
            return true;
        }


        Player player = (Player) sender;

        //KOY KOMUDU
        if (args.length > 0 && args[0].equalsIgnoreCase("koy")) {
            if (args.length == 2) {
                double amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    player.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                String formatted = economy.format(amount);
                int z = 0;
                HashMap<Integer, ItemStack> hm = player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, (int) amount));
                if (hm.isEmpty()) {
                    if (!economy.depositPlayer(player, amount).transactionSuccess()) {
                        player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        return true;
                    }

                    player.sendMessage("§6§lKese §fenvanterinizdeki " + formatted + " altın keseye koyuldu.");
                    player.sendMessage("Yeni altın miktarı §6" + economy.format(economy.getBalance(player)) + " §6Altın");
                    player.sendMessage("§a§l+ §a" + formatted);

                    return true;
                } else {
                    for (Map.Entry<Integer, ItemStack> entry : hm.entrySet()) {
                        ItemStack value = entry.getValue();
                        z += value.getAmount();
                    }

                    if (!economy.depositPlayer(player, amount - z).transactionSuccess()) {
                        player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        return true;
                    }

                    formatted = economy.format(amount - z);
                    player.sendMessage("§6§lKese §fenvanterinizdeki " + formatted + " altın keseye koyuldu.");
                    player.sendMessage("Yeni altın miktarı §6" + economy.format(economy.getBalance(player)) + " §6Altın");
                    player.sendMessage("§a§l+ §a" + formatted);

                    return true;
                }

            } else {
                player.sendMessage("§cMiktar sayı olmalıdır.");
                return true;
            }
        }

        //AL KOMUDU
        if (args.length > 0 && args[0].equalsIgnoreCase("al")) {
            if (args.length == 2) {
                double amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                if (amount < 0) {
                    player.sendMessage("§cMiktar sayı olmalıdır.");
                    return true;
                }

                String formatted = economy.format(amount);

                if (economy.has(player, amount)) {
                    double bal = economy.getBalance(player);
                    if (!economy.withdrawPlayer(player, amount).transactionSuccess()) {
                        player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        return true;
                    }
                    player.sendMessage("§6§lKese §f" + economy.format(bal) + " altın aldın.");
                    player.sendMessage("Yeni altın miktarı §6" + economy.format(economy.getBalance(player)) + " §6Altın");
                    player.sendMessage("§c§l- §c" + formatted);
                    HashMap<Integer, ItemStack> map = player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, (int) amount));
                    if (!map.isEmpty() && map.get(0).getAmount() != 0) {
                        player.sendMessage("§cEnvanterinde yer kalmadığı için altınlar yere düştü!");

                        if (map.get(0).getAmount() <= 64) {
                            player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, map.get(0).getAmount()));
                        } else {
                            for (int i = map.get(0).getAmount(); i >= 64; i = i - 64) {
                                player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, 64));
                            }

                            if (map.get(0).getAmount() % 64 != 0) {
                                player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, map.get(0).getAmount() % 64));
                            }
                        }
                    }
                    return true;
                } else {
                    player.sendMessage("§cKesenizde yeterli miktarda altın yok.");
                    return true;
                }
            } else {
                player.sendMessage("§cMiktar sayı olmalıdır.");
                return true;
            }
        }

        if (args.length > 0) {
            sendHelpMessage(player);
        }

        if (args.length == 0) {
            player.sendMessage("§6§lKese §f" + economy.format(economy.getBalance(player)) + " altınınız var.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("al");
            completions.add("koy");
            completions.add("yardım");
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
