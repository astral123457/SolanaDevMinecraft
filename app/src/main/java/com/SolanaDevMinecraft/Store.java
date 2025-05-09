package com.SolanaDevMinecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionType;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionData;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta; // 🔹 Correto para poções

import org.bukkit.enchantments.Enchantment; // 🔹 Correto para encantamentos
import org.bukkit.inventory.meta.EnchantmentStorageMeta; // 🔹 Necessário para livros encantados



import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Store {
    private final Connection connection;
    private final FileConfiguration config;

    // 🔹 Construtor correto que inicializa 'config' e 'connection'
    public Store(FileConfiguration config, Connection connection) {
        this.config = config;
        this.connection = connection;
    }

    @SuppressWarnings("deprecation")
    public String getPlayerLanguage(Player player) {
        String locale = player.getLocale(); // Obtém o idioma do jogador como String
        List<String> supportedLanguages = config.getStringList("language.supported"); // Obtém a lista de idiomas do config.yml

        return supportedLanguages.contains(locale) ? locale : config.getString("language.default", "pt-BR");
    }

    // 📌 Método genérico para verificar saldo e processar compras
    private boolean processPurchase(Player player, int price) {
        try (
            PreparedStatement stmt = connection.prepareStatement("SELECT saldo FROM banco WHERE jogador = ?");
        ) {
            stmt.setString(1, player.getName());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt("saldo") >= price) {
                    try (PreparedStatement updateStmt = connection.prepareStatement("UPDATE banco SET saldo = saldo - ? WHERE jogador = ?")) {
                        updateStmt.setInt(1, price);
                        updateStmt.setString(2, player.getName());
                        updateStmt.executeUpdate();
                    }
                    return true;
                } else {
                    String lang = getPlayerLanguage(player);
                    player.sendMessage(lang.equals("pt") ?
                        Component.text("💰 Saldo insuficiente para realizar a compra.", NamedTextColor.RED) :
                        Component.text("💰 Insufficient balance to make the purchase.", NamedTextColor.RED));
                    return false;
                }
            }
        } catch (Exception e) {
            player.sendMessage(Component.text("⚠ Erro ao acessar o banco de dados.", NamedTextColor.RED));
            e.printStackTrace();
            return false;
        }
    }

    // 📌 Compra de Maçã Encantada
    public void buyEnchantedApple(Player player) {
        int price = config.getInt("store.price.apple"); // 🔹 Obtém preço 
        //int price = 500;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));

            String lang = getPlayerLanguage(player);
           player.sendMessage(
            lang.equals("pt") ? 
           Component.text("🍎 Você comprou uma Maçã Encantada por $" + price + "!", NamedTextColor.GOLD) :
           lang.equals("es") ? 
           Component.text("🍎 ¡Has comprado una Manzana Encantada por $" + price + "!", NamedTextColor.GOLD) :
           Component.text("🍎 You bought an Enchanted Apple for $" + price + "!", NamedTextColor.GOLD)
           );
        }
    }

    // 📌 Compra de Esmeralda
    public void buyEmerald(Player player) {
        int price = config.getInt("store.price.emerald"); // 🔹 Obtém preço 
        //int price = 1000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.EMERALD, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("💎 Você comprou uma Esmeralda por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("💎 ¡Has comprado una Esmeralda por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("💎 You bought an Emerald for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    // 📌 Compra de Bloco de Ouro

    public void buyGoldBlock(Player player) {
        int price = config.getInt("store.price.buyGoldBlock"); // 🔹 Obtém preço 
        //int price = 10000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("🏆 Você comprou um Bloco de Ouro por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("🏆 ¡Has comprado un Bloque de Oro por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("🏆 You bought a Gold Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    // 📌 Compra de Bloco de Diamante

    public void buyDiamondBlock(Player player) {
        //int price = 20000;
        int price = config.getInt("store.price.buyDiamondBlock"); // 🔹 Obtém preço 
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND_BLOCK, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("💎 Você comprou um Bloco de Diamante por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("💎 ¡Has comprado un Bloque de Diamante por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("💎 You bought a Diamond Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }


    // 📌 Compra de Bloco de Esmeralda
    public void buyEmeraldBlock(Player player) {
        
        int price = config.getInt("store.price.buyEmeraldBlock"); // 🔹 Obtém preço 
        //int price = 50000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.EMERALD_BLOCK, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("💎 Você comprou um Bloco de Esmeralda por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("💎 ¡Has comprado un Bloque de Esmeralda por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("💎 You bought an Emerald Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    // 📌 Compra de Bloco de Netherite
    public void buyNetheriteBlock(Player player) {
        int price = config.getInt("store.price.buyNetheriteBlock");
        //int price = 100000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.NETHERITE_BLOCK, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("💎 Você comprou um Bloco de Netherite por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("💎 ¡Has comprado un Bloque de Netherite por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("💎 You bought a Netherite Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    // 📌 Compra de Bloco de Ferro
    public void buyIronBlock(Player player) {
        int price = config.getInt("store.price.buyIronBlock");
        //int price = 5000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.IRON_BLOCK, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("🏆 Você comprou um Bloco de Ferro por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("🏆 ¡Has comprado un Bloque de Hierro por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("🏆 You bought an Iron Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    // 📌 Compra de Bloco de Lapis
    public void buyLapisBlock(Player player) {
        int price = config.getInt("store.price.lapis");
        //int price = 1000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.LAPIS_BLOCK, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("💎 Você comprou um Bloco de Lápis por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("💎 ¡Has comprado un Bloque de Lápiz por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("💎 You bought a Lapis Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    // 📌 Compra de Bloco de Redstone
    public void buyRedstoneBlock(Player player) {
        int price = config.getInt("store.price.redstone");
        //int price = 1000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.REDSTONE_BLOCK, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("💎 Você comprou um Bloco de Redstone por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("💎 ¡Has comprado un Bloque de Redstone por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("💎 You bought a Redstone Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    // 📌 Compra de Bloco de Quartzo
    public void buyQuartzBlock(Player player) {
        int price = config.getInt("store.price.quartz");
        //int price = 1000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.QUARTZ_BLOCK, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("💎 Você comprou um Bloco de Quartzo por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("💎 ¡Has comprado un Bloque de Quartzo por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("💎 You bought a Quartz Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    // 📌 Compra de Bloco de Argila
    public void buyClayBlock(Player player) {
        int price = config.getInt("store.price.clay");
        //int price = 1000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.CLAY, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("💎 Você comprou um Bloco de Argila por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("💎 ¡Has comprado un Bloque de Argila por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("💎 You bought a Clay Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    // 📌 Compra de Bloco de Areia
    public void buySandBlock(Player player) {
        int price = config.getInt("store.price.buySandBlock");
        //int price = 1000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.SAND, 1));

            String lang = getPlayerLanguage(player);
            player.sendMessage(
                lang.equals("pt") ? 
                Component.text("💎 Você comprou um Bloco de Areia por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es") ? 
                Component.text("💎 ¡Has comprado un Bloque de Areia por $" + price + "!", NamedTextColor.GOLD) :
                Component.text("💎 You bought a Sand Block for $" + price + "!", NamedTextColor.GOLD)
            );
        }
    }

    public void buyAllPotions(Player player) {
    int totalPrice = config.getInt("store.price.buyAllPotions");
    //int totalPrice = 3000; // Preço total para todas as poções
    if (processPurchase(player, totalPrice)) {
        List<PotionType> potionTypes = List.of(
            PotionType.SPEED, PotionType.FIRE_RESISTANCE, PotionType.INSTANT_HEAL,
            PotionType.JUMP, PotionType.REGEN, PotionType.STRENGTH, PotionType.WATER_BREATHING,
            PotionType.NIGHT_VISION
        );

        for (PotionType potionType : potionTypes) {
            ItemStack potionItem = new ItemStack(Material.POTION, 1);
            PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
            if (meta != null) {
                meta.setBasePotionData(new PotionData(potionType));
                meta.displayName(Component.text("🧪 Poção de " + potionType.name(), NamedTextColor.AQUA));
                potionItem.setItemMeta(meta);
            }
            player.getInventory().addItem(potionItem);

            // 🔹 Efeito especial ao jogador ao comprar as poções
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1)); // Brilho por 10 segundos
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 1)); // Leve flutuação por 5 segundos
        }

        String lang = getPlayerLanguage(player);
        player.sendMessage(
            lang.equals("pt") ? Component.text("🧪 Você comprou todas as poções por $" + totalPrice + "!", NamedTextColor.GREEN) :
            lang.equals("es") ? Component.text("🧪 ¡Has comprado todas las pociones por $" + totalPrice + "!", NamedTextColor.GREEN) :
            Component.text("🧪 You bought all potions for $" + totalPrice + "!", NamedTextColor.GREEN)
        );
    }
}



    public void buyAllEnchantmentBooks(Player player) {
    int totalPrice = config.getInt("store.price.buyAllEnchantmentBooks");
    //int totalPrice = 5000; // Defina o preço total para todos os livros
    if (processPurchase(player, totalPrice)) {
        // Lista de encantamentos disponíveis
        List<Enchantment> enchantments = List.of(
            Enchantment.PROTECTION_ENVIRONMENTAL,
            Enchantment.DAMAGE_ALL,
            Enchantment.FIRE_ASPECT,
            Enchantment.LOOT_BONUS_BLOCKS,
            Enchantment.LOOT_BONUS_MOBS
        );

        // Adiciona todos os livros encantados ao inventário do jogador
        for (Enchantment enchantment : enchantments) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            if (meta != null) {
                meta.addStoredEnchant(enchantment, 1, true);
                book.setItemMeta(meta);
            }
            player.getInventory().addItem(book);
        }

        String lang = getPlayerLanguage(player);
        player.sendMessage(
            lang.equals("pt") ? Component.text("📚 Você comprou todos os livros de encantamento por $" + totalPrice + "!", NamedTextColor.GOLD) :
            lang.equals("es") ? Component.text("📚 ¡Has comprado todos los libros encantados por $" + totalPrice + "!", NamedTextColor.GOLD) :
            Component.text("📚 You bought all enchantment books for $" + totalPrice + "!", NamedTextColor.GOLD)
        );
    }
}


public void buyAllFood(Player player) {
    int totalPrice = config.getInt("store.price.buyAllFood");
    //int totalPrice = 2000; // Defina o preço total para todos os alimentos
    if (processPurchase(player, totalPrice)) {
        List<Material> foodItems = List.of(
            Material.APPLE, Material.BREAD, Material.COOKED_BEEF, Material.COOKED_CHICKEN,
            Material.COOKED_MUTTON, Material.COOKED_PORKCHOP, Material.COOKED_RABBIT,
            Material.CARROT, Material.POTATO, Material.BAKED_POTATO, Material.GOLDEN_CARROT,
            Material.BEETROOT, Material.BEETROOT_SOUP, Material.MUSHROOM_STEW,
            Material.MELON_SLICE, Material.PUMPKIN_PIE, Material.COOKIE
        );

        for (Material food : foodItems) {
            ItemStack foodItem = new ItemStack(food, 5); // Adiciona 5 unidades de cada comida ao inventário
            player.getInventory().addItem(foodItem);
        }

        String lang = getPlayerLanguage(player);
        player.sendMessage(
            lang.equals("pt") ? Component.text("🍽️ Você comprou todos os alimentos por $" + totalPrice + "!", NamedTextColor.GOLD) :
            lang.equals("es") ? Component.text("🍽️ ¡Has comprado todos los alimentos por $" + totalPrice + "!", NamedTextColor.GOLD) :
            Component.text("🍽️ You bought all food items for $" + totalPrice + "!", NamedTextColor.GOLD)
        );
    }
}


public void buySimpleBook(Player player) {
    int price = config.getInt("store.price.buySimpleBook");
    //int price = 50; // Preço do livro comum
    if (processPurchase(player, price)) {
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("📖 Livro Simples", NamedTextColor.GRAY));
            meta.lore(List.of(Component.text("Um livro para suas anotações!", NamedTextColor.DARK_GRAY)));
            book.setItemMeta(meta);
        }

        player.getInventory().addItem(book);

        String lang = getPlayerLanguage(player);
        player.sendMessage(
            lang.equals("pt") ? Component.text("📖 Você comprou um livro simples por $" + price + "!", NamedTextColor.GRAY) :
            lang.equals("es") ? Component.text("📖 ¡Has comprado un libro simple por $" + price + "!", NamedTextColor.GRAY) :
            Component.text("📖 You bought a simple book for $" + price + "!", NamedTextColor.GRAY)
        );
    }
}

    public void buySpinningWand(Player player) {
    int price = config.getInt("store.price.buySpinningWand");
    //int price = 800; // Defina o preço da varinha
    if (processPurchase(player, price)) {
        ItemStack wand = new ItemStack(Material.STICK, 1); // Usando um bastão como base da varinha
        ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
    // ✅ Corrigido para usar Component.text() em vez de setDisplayName(String)
    meta.displayName(Component.text("✨ Varinha Giratória que vibra ✨"));

    // ✅ Corrigido para usar setLore(List<Component>) com Adventure API
    meta.lore(List.of(
        Component.text("Gira objetos ao redor!").color(NamedTextColor.AQUA),
        Component.text("Poder mágico incrível!").color(NamedTextColor.LIGHT_PURPLE)
    ));

    wand.setItemMeta(meta);
}


        player.getInventory().addItem(wand); // Adiciona a varinha ao inventário do jogador

        String lang = getPlayerLanguage(player);
        player.sendMessage(
            lang.equals("pt") ? Component.text("✨ Você comprou uma Varinha Giratória por $" + price + "!", NamedTextColor.LIGHT_PURPLE) :
            lang.equals("es") ? Component.text("✨ ¡Has comprado una Varita Giratoria por $" + price + "!", NamedTextColor.LIGHT_PURPLE) :
            Component.text("✨ You bought a Spinning Wand for $" + price + "!", NamedTextColor.LIGHT_PURPLE)
        );
    }
}


   
    

    
}