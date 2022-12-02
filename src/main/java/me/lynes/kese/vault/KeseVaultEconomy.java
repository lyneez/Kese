package me.lynes.kese.vault;

import me.lynes.kese.Database;
import me.lynes.kese.Kese;
import me.lynes.kese.utils.PlayerUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KeseVaultEconomy implements Economy {
    private static final EconomyResponse NOT_IMPLEMENTED = new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Not implemented");
    private final Kese plugin = Kese.getInstance();
    private final Database db = plugin.getDatabase();

    @Override
    public boolean isEnabled() {
        return db.isOpen();
    }

    @Override
    public String getName() {
        return "Kese";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat format;
        try {
            format = new DecimalFormat(Kese.getInstance().getConfig().getString("decimal_format"), symbols);
        } catch (NullPointerException | IllegalArgumentException ex) {
            format = new DecimalFormat("0.0#", symbols);
        }

        return format.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public boolean hasAccount(String player) {
        if (plugin.getServer().getOnlineMode()) {
            OfflinePlayer target = PlayerUtil.getOfflinePlayer(player);
            if (target != null) {
                try (PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM economy WHERE uuid = ?")) {
                    statement.setString(1, target.getUniqueId().toString());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        return resultSet.next();
                    } catch (SQLException exception) {
                        db.report(exception);
                    }
                } catch (SQLException exception) {
                    db.report(exception);
                }
            }
        }

        try (PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM economy WHERE uuid = ?")) {
            statement.setString(1, getUUID(player));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            } catch (SQLException exception) {
                db.report(exception);
            }
        } catch (SQLException exception) {
            db.report(exception);
        }


        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        try (PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM economy WHERE uuid = ?")) {
            statement.setString(1, offlinePlayer.getUniqueId().toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            } catch (SQLException exception) {
                db.report(exception);
            }
        } catch (SQLException exception) {
            db.report(exception);
        }

        return false;
    }

    @Override
    public boolean hasAccount(String player, String world) {
        return hasAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String world) {
        return hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String player) {
        if (plugin.getServer().getOnlineMode()) {
            OfflinePlayer target = PlayerUtil.getOfflinePlayer(player);
            if (target != null) {
                try (PreparedStatement statement = db.getConnection().prepareStatement("SELECT balance FROM economy WHERE uuid = ?")) {
                    statement.setString(1, target.getUniqueId().toString());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getDouble(1);
                        } else {
                            return 0;
                        }
                    } catch (SQLException exception) {
                        db.report(exception);
                    }
                } catch (SQLException exception) {
                    db.report(exception);
                }
            }
        }

        try (PreparedStatement statement = db.getConnection().prepareStatement("SELECT balance FROM economy WHERE uuid = ?")) {
            statement.setString(1, getUUID(player));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                } else {
                    return 0;
                }
            } catch (SQLException exception) {
                db.report(exception);
            }
        } catch (SQLException exception) {
            db.report(exception);
        }
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        try (PreparedStatement statement = db.getConnection().prepareStatement("SELECT balance FROM economy WHERE uuid = ?")) {
            statement.setString(1, offlinePlayer.getUniqueId().toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                } else {
                    return 0;
                }
            } catch (SQLException exception) {
                db.report(exception);
            }
        } catch (SQLException exception) {
            db.report(exception);
        }

        return 0;
    }

    @Override
    public double getBalance(String player, String world) {
        return getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String world) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return getBalance(offlinePlayer) >= amount;
    }

    @Override
    public boolean has(String player, String world, double amount) {
        return has(player, amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String world, double amount) {
        return has(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String player, double amount) {
        return setBalance(player, getBalance(player) - amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        return setBalance(offlinePlayer, getBalance(offlinePlayer) - amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String player, String world, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
        return withdrawPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String player, double amount) {
        return setBalance(player, getBalance(player) + amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        return setBalance(offlinePlayer, getBalance(offlinePlayer) + amount);
    }

    @Override
    public EconomyResponse depositPlayer(String player, String world, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
        return depositPlayer(offlinePlayer, amount);
    }

    @Override
    public EconomyResponse createBank(String bank, String player) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse createBank(String bank, OfflinePlayer offlinePlayer) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse deleteBank(String bank) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse bankBalance(String bank) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse bankHas(String bank, double amount) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse bankWithdraw(String bank, double amount) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse bankDeposit(String bank, double amount) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse isBankOwner(String bank, String player) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer offlinePlayer) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse isBankMember(String bank, String player) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer offlinePlayer) {
        return NOT_IMPLEMENTED;
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String player) {
        if (plugin.getServer().getOnlineMode()) {
            OfflinePlayer target = PlayerUtil.getOfflinePlayer(player);
            if (target != null) {
                try (PreparedStatement statement = this
                        .db.getConnection()
                        .prepareStatement("INSERT OR IGNORE INTO economy VALUES (?, ?)")
                ) {
                    statement.setString(1, target.getUniqueId().toString());
                    statement.setDouble(2, 0);
                    return statement.executeUpdate() > 0;
                } catch (SQLException exception) {
                    db.report(exception);
                }
            }
        }

        try (PreparedStatement statement = this
                .db.getConnection()
                .prepareStatement("INSERT OR IGNORE INTO economy VALUES (?, ?)")
        ) {
            statement.setString(1, getUUID(player));
            statement.setDouble(2, 0);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            db.report(exception);
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        try (PreparedStatement statement = this
                .db.getConnection()
                .prepareStatement("INSERT OR IGNORE INTO economy VALUES (?, ?)")
        ) {
            statement.setString(1, offlinePlayer.getUniqueId().toString());
            statement.setDouble(2, 0);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            db.report(exception);
        }

        return false;
    }

    @Override
    public boolean createPlayerAccount(String player, String world) {
        return createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String world) {
        return createPlayerAccount(offlinePlayer);
    }

    public EconomyResponse setBalance(OfflinePlayer offlinePlayer, double balance) {
        createPlayerAccount(offlinePlayer);

        try (PreparedStatement statement = db
                .getConnection()
                .prepareStatement("UPDATE economy SET balance = ? WHERE uuid = ?")) {
            statement.setDouble(1, balance);
            statement.setString(2, offlinePlayer.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            db.report(exception);
            return new EconomyResponse(balance, balance, EconomyResponse.ResponseType.FAILURE, "SQLException");
        }

        return new EconomyResponse(balance, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse setBalance(String player, double balance) {
        if (plugin.getServer().getOnlineMode()) {
            OfflinePlayer target = PlayerUtil.getOfflinePlayer(player);
            if (target != null) {
                return setBalance(target, balance);
            }
        }

        createPlayerAccount(player);

        try (PreparedStatement statement = db
                .getConnection()
                .prepareStatement("UPDATE economy SET balance = ? WHERE uuid = ?")) {
            statement.setDouble(1, balance);
            statement.setString(2, getUUID(player));
            statement.executeUpdate();
        } catch (SQLException exception) {
            db.report(exception);
            return new EconomyResponse(balance, balance, EconomyResponse.ResponseType.FAILURE, "SQLException");
        }

        return new EconomyResponse(balance, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public String getUUID(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(UTF_8)).toString();
    }

}

