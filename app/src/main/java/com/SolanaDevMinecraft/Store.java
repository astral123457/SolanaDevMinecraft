package com.SolanaDevMinecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Store {

    private final Connection connection;

    public Store(Connection connection) {
        this.connection = connection;
    }

    // 📌 Método genérico para verificar saldo e processar compras
    private boolean processPurchase(Player player, int price) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT saldo FROM banco WHERE jogador = ?");
            stmt.setString(1, player.getName());
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt("saldo") >= price) {
                PreparedStatement updateStmt = connection.prepareStatement("UPDATE banco SET saldo = saldo - ? WHERE jogador = ?");
                updateStmt.setInt(1, price);
                updateStmt.setString(2, player.getName());
                updateStmt.executeUpdate();
                return true;
            } else {
                player.sendMessage("Saldo insuficiente para realizar a compra.");
                return false;
            }
        } catch (Exception e) {
            player.sendMessage("Erro ao acessar o banco de dados.");
            e.printStackTrace();
            return false;
        }
    }

    // 📌 Compra de Maçã Encantada
    public void buyEnchantedApple(Player player) {
        int price = 500;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));
            player.sendMessage("Você comprou uma Maçã Encantada por $" + price + "!");
        }
    }

    // 📌 Compra de Esmeralda
    public void buyEmerald(Player player) {
        int price = 250;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.EMERALD, 1));
            player.sendMessage("Você comprou uma Esmeralda por $" + price + "!");
        }
    }
}