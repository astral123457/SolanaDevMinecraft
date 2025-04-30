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

    // 📌 Compra de Picareta de Netherite Encantada
    public void buyNetheritePickaxe(Player player) {
        int price = 2000;
        if (processPurchase(player, price)) {
            ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE, 1);
            ItemMeta meta = pickaxe.getItemMeta();
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DIG_SPEED, 5, true);
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 3, true);
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS, 3, true);
            pickaxe.setItemMeta(meta);
            player.getInventory().addItem(pickaxe);
            player.sendMessage("Você comprou uma Picareta de Netherite Encantada por $" + price + "!");
        }
    }

    // 📌 Compra de Machado de Diamante Encantado
    public void buyEnchantedAxe(Player player) {
        int price = 1000;
        if (processPurchase(player, price)) {
            ItemStack axe = new ItemStack(Material.DIAMOND_AXE, 1);
            ItemMeta meta = axe.getItemMeta();
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DIG_SPEED, 5, true);
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 3, true);
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS, 3, true);
            axe.setItemMeta(meta);
            player.getInventory().addItem(axe);
            player.sendMessage("Você comprou um Machado de Diamante Encantado por $" + price + "!");
        }
    }

    // 📌 Compra de Armadura Completa de Diamante Encantada
    public void buyEnchantedDiamondArmor(Player player) {
        int price = 5000;
        if (processPurchase(player, price)) {
            ItemStack[] armor = {
                createEnchantedItem(Material.DIAMOND_HELMET),
                createEnchantedItem(Material.DIAMOND_CHESTPLATE),
                createEnchantedItem(Material.DIAMOND_LEGGINGS),
                createEnchantedItem(Material.DIAMOND_BOOTS)
            };
            player.getInventory().addItem(armor);
            player.sendMessage("Você comprou uma Armadura Completa de Diamante Encantada por $" + price + "!");
        }
    }

    // 📌 Compra de Armadura Completa de Netherite Encantada
    public void buyEnchantedNetheriteArmor(Player player) {
        int price = 10000;
        if (processPurchase(player, price)) {
            ItemStack[] armor = {
                createEnchantedItem(Material.NETHERITE_HELMET),
                createEnchantedItem(Material.NETHERITE_CHESTPLATE),
                createEnchantedItem(Material.NETHERITE_LEGGINGS),
                createEnchantedItem(Material.NETHERITE_BOOTS)
            };
            player.getInventory().addItem(armor);
            player.sendMessage("Você comprou uma Armadura Completa de Netherite Encantada por $" + price + "!");
        }
    }

    // 📌 Método auxiliar para criar itens encantados
    private ItemStack createEnchantedItem(Material material) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(org.bukkit.enchantments.Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 3, true);
        item.setItemMeta(meta);
        return item;
    }
}