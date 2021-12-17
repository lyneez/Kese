package me.lynes.kese;

import org.bukkit.ChatColor;
import org.intellij.lang.annotations.Language;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class Database {
    private final Kese plugin = Kese.getInstance();
    private Connection connection;

    public void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Failed to load SQLite JDBC class", ex);
        }

        File database = new File(plugin.getDataFolder(), "database.db");

        try {
            database.getParentFile().mkdirs();
            database.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "File write error: database.db");
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + database);
    }

    public void setup() throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS economy (" +
                    "`uuid` varchar(32) NOT NULL, `balance` double(1000) NOT NULL, PRIMARY KEY (`uuid`));");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isOpen() {
        if (connection == null) {
            return false;
        }

        try {
            return !connection.isClosed();
        } catch (SQLException exception) {
            return false;
        }
    }

    public PreparedStatement statement(@Language("SQL") String query, Initializer initializer) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        initializer.apply(statement);
        return statement;
    }

    public ResultSet result(@Language("SQL") String query, Initializer initializer) throws SQLException {
        return statement(query, initializer).executeQuery();
    }

    public void report(SQLException exception) {
        plugin.getLogger().log(Level.SEVERE, ChatColor.RED + "Unhandled exception: " + exception.getMessage(), exception);
    }

    public interface Initializer {
        void apply(PreparedStatement s) throws SQLException;
    }
}

