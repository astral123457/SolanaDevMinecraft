package com.SolanaDevMinecraft;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
    private final Connection connection;

    public PlayerJoinListener(Connection connection) {
        this.connection = connection;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        registerPlayer(player);
    }

    private void registerPlayer(Player player) {
        String playerName = player.getName().replace(" ", "_").toLowerCase();
        try {
            PreparedStatement checkStatement = connection.prepareStatement(
                "SELECT id FROM jogadores WHERE nome = ?"
            );
            checkStatement.setString(1, playerName);
            ResultSet resultSet = checkStatement.executeQuery();

            if (!resultSet.next()) {
                PreparedStatement insertPlayer = connection.prepareStatement(
                    "INSERT INTO jogadores (nome) VALUES (?)"
                );
                insertPlayer.setString(1, playerName);
                insertPlayer.executeUpdate();

                PreparedStatement insertBank = connection.prepareStatement(
                    "INSERT INTO banco (jogador, saldo) VALUES (?, 500)"
                );
                insertBank.setString(1, playerName);
                insertBank.executeUpdate();

                player.sendMessage("✅ Você foi cadastrado no banco com 500 moedas!");
            } else {
                player.sendMessage("⚠ Você já está cadastrado!");
            }
        } catch (SQLException e) {
            player.sendMessage("❌ Erro ao registrar jogador: " + e.getMessage());
            e.printStackTrace();
        }
    }
}