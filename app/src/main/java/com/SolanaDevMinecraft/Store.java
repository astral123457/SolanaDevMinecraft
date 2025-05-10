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
import org.bukkit.inventory.meta.BlockStateMeta; // 🔹 Necessário para Shulker Box

import org.bukkit.enchantments.Enchantment; // 🔹 Correto para encantamentos
import org.bukkit.inventory.meta.EnchantmentStorageMeta; // 🔹 Necessário para livros encantados
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.BlockState;


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
                    player.sendMessage(lang.equals("pt-BR") ?
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
        String lang = getPlayerLanguage(player);
        player.sendMessage("🍳. Lang= " + lang);
        //int price = 500;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));

            
           player.sendMessage(
            lang.equals("pt-BR") ? 
           Component.text("🍎 Você comprou uma Maçã Encantada por $" + price + "!", NamedTextColor.GOLD) :
           lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("💎 Você comprou uma Esmeralda por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("🏆 Você comprou um Bloco de Ouro por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("💎 Você comprou um Bloco de Diamante por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("💎 Você comprou um Bloco de Esmeralda por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("💎 Você comprou um Bloco de Netherite por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("🏆 Você comprou um Bloco de Ferro por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("💎 Você comprou um Bloco de Lápis por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("💎 Você comprou um Bloco de Redstone por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("💎 Você comprou um Bloco de Quartzo por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("💎 Você comprou um Bloco de Argila por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
                lang.equals("pt-BR") ? 
                Component.text("💎 Você comprou um Bloco de Areia por $" + price + "!", NamedTextColor.GOLD) :
                lang.equals("es-ES") ? 
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
            lang.equals("pt-BR") ? Component.text("🧪 Você comprou todas as poções por $" + totalPrice + "!", NamedTextColor.GREEN) :
            lang.equals("es-ES") ? Component.text("🧪 ¡Has comprado todas las pociones por $" + totalPrice + "!", NamedTextColor.GREEN) :
            Component.text("🧪 You bought all potions for $" + totalPrice + "!", NamedTextColor.GREEN)
        );
    }
}

@SuppressWarnings("deprecation")
public void buyEnchantmentShulkerBox(Player player) {
    int totalPrice = config.getInt("store.price.enchantmentshulkerbox", 5000); // 🔹 Obtém preço do config.yml, com fallback de 10000
    if (!processPurchase(player, totalPrice)) return; // 🔹 Interrompe se a compra falhar

    // 🔹 Criar a Shulker Box verde
    ItemStack shulkerBox = new ItemStack(Material.GREEN_SHULKER_BOX);
    BlockStateMeta meta = (BlockStateMeta) shulkerBox.getItemMeta();
    ShulkerBox shulker = (ShulkerBox) meta.getBlockState();

    // 🔹 Adicionar os itens na Shulker Box
    shulker.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 64));
    shulker.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 6));
    shulker.getInventory().addItem(new ItemStack(Material.EMERALD, 10));
    shulker.getInventory().addItem(new ItemStack(Material.WOLF_SPAWN_EGG, 1));
    shulker.getInventory().addItem(new ItemStack(Material.SHEEP_SPAWN_EGG, 1));
    shulker.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2));
    shulker.getInventory().addItem(new ItemStack(Material.FLOWER_POT, 10));

    // 🔹 Criar uma espada de Netherite encantada
    ItemStack sword = new ItemStack(Material.NETHERITE_SWORD, 1);
    sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
    sword.addUnsafeEnchantment(Enchantment.getByName("UNBREAKING"), 3);
    sword.addUnsafeEnchantment(Enchantment.MENDING, 1);
    sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
    sword.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);

    // 🔹 Adicionar a espada encantada na Shulker Box
    shulker.getInventory().addItem(sword);

    // 🔹 Salvar e aplicar as mudanças na Shulker Box
    meta.setBlockState(shulker);
    shulkerBox.setItemMeta(meta);

    // 🔹 Adicionar a Shulker Box ao inventário do jogador
    player.getInventory().addItem(shulkerBox);

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    player.sendMessage(
        Component.text("📦 ").append(
            lang.equals("pt-BR") ? Component.text("Você comprou uma Shulker Box encantada cheia de tesouros por $" + totalPrice + "!", NamedTextColor.GOLD) :
            lang.equals("es-ES") ? Component.text("¡Has comprado una Shulker Box encantada llena de tesoros por $" + totalPrice + "!", NamedTextColor.GOLD) :
            Component.text("You bought an enchanted Shulker Box full of treasures for $" + totalPrice + "!", NamedTextColor.GOLD)
        )
    );
}



    public void buyAllEnchantmentBooks(Player player) {
    int totalPrice = config.getInt("store.price.buyAllEnchantmentBooks", 8000); // 🔹 Obtém preço do config.yml, com fallback de 8000
    if (!processPurchase(player, totalPrice)) return; // 🔹 Interrompe se a compra falhar

    // 🔹 Lista de comandos para dar cada livro encantado separadamente
    List<String> commands = List.of(
        String.format("give %s enchanted_book{StoredEnchantments:[{id:\"sharpness\",lvl:5}]}", player.getName()),        // Afiação V
        String.format("give %s enchanted_book{StoredEnchantments:[{id:\"smite\",lvl:5}]}", player.getName()),            // Julgamento V
        String.format("give %s enchanted_book{StoredEnchantments:[{id:\"bane_of_arthropods\",lvl:5}]}", player.getName()), // Ruína dos Artrópodes V
        String.format("give %s enchanted_book{StoredEnchantments:[{id:\"knockback\",lvl:2}]}", player.getName()),         // Repulsão II
        String.format("give %s enchanted_book{StoredEnchantments:[{id:\"fire_aspect\",lvl:2}]}", player.getName()),       // Aspecto Flamejante II
        String.format("give %s enchanted_book{StoredEnchantments:[{id:\"looting\",lvl:3}]}", player.getName()),          // Pilhagem III
        String.format("give %s enchanted_book{StoredEnchantments:[{id:\"unbreaking\",lvl:3}]}", player.getName()),       // Inquebrável III
        String.format("give %s enchanted_book{StoredEnchantments:[{id:\"mending\",lvl:1}]}", player.getName())           // Remendo I
    );

    // 🔹 Executa cada comando para dar os livros ao jogador
    for (String command : commands) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    player.sendMessage(
        Component.text("📚 ").append(
            lang.equals("pt-BR") ? Component.text("Você comprou todos os livros de encantamento no nível máximo por $" + totalPrice + "!", NamedTextColor.GOLD) :
            lang.equals("es-ES") ? Component.text("¡Has comprado todos los libros encantados en el nivel máximo por $" + totalPrice + "!", NamedTextColor.GOLD) :
            Component.text("You bought all max-level enchantment books for $" + totalPrice + "!", NamedTextColor.GOLD)
        )
    );
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
            lang.equals("pt-BR") ? Component.text("🍽️ Você comprou todos os alimentos por $" + totalPrice + "!", NamedTextColor.GOLD) :
            lang.equals("es-ES") ? Component.text("🍽️ ¡Has comprado todos los alimentos por $" + totalPrice + "!", NamedTextColor.GOLD) :
            Component.text("🍽️ You bought all food items for $" + totalPrice + "!", NamedTextColor.GOLD)
        );
    }
}


public void buySimpleBook(Player player) {
    int price = config.getInt("store.price.buySimpleBook", 50); // 🔹 Obtém preço do config.yml, com fallback de 50
    if (!processPurchase(player, price)) return; // 🔹 Interrompe se a compra falhar

    // 🔹 Executa o comando para dar um livro ao jogador
    String command = String.format("give %s minecraft:book 1", player.getName());
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); // 🔹 Executa o comando como console

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    player.sendMessage(
        Component.text("📖 ").append(
            lang.equals("pt-BR") ? Component.text("Você comprou um livro simples por $" + price + "!", NamedTextColor.GRAY) :
            lang.equals("es-ES") ? Component.text("¡Has comprado un libro simple por $" + price + "!", NamedTextColor.GRAY) :
            Component.text("You bought a simple book for $" + price + "!", NamedTextColor.GRAY)
        )
    );
}

public void buySimpleMap(Player player) {
    int price = config.getInt("store.price.buySimpleMap", 100); // 🔹 Obtém preço do config.yml, com fallback de 100
    if (!processPurchase(player, price)) return; // 🔹 Interrompe se a compra falhar

    // 🔹 Executa o comando para dar um mapa ao jogador
    String command = String.format("give %s minecraft:filled_map 1", player.getName());
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); // Executa o comando como console

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    player.sendMessage(
        Component.text("🗺️ ").append(
            lang.equals("pt-BR") ? Component.text("Você comprou um mapa simples por $" + price + "!", NamedTextColor.GRAY) :
            lang.equals("es-ES") ? Component.text("¡Has comprado un mapa simple por $" + price + "!", NamedTextColor.GRAY) :
            Component.text("You bought a simple map for $" + price + "!", NamedTextColor.GRAY)
        )
    );
}

public void buySimpleCompass(Player player) {
    int price = config.getInt("store.price.buySimpleCompass", 150); // 🔹 Obtém preço do config.yml, com fallback de 150
    if (!processPurchase(player, price)) return; // 🔹 Interrompe se a compra falhar

    // 🔹 Executa o comando para dar uma bússola ao jogador
    //String command = String.format("give %s minecraft:compass 1", player.getName());
    String command = String.format("/give 007amauri minecraft:recovery_compass 1", player.getName());
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); // Executa o comando como console

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    player.sendMessage(
        Component.text("🧭 ").append(
            lang.equals("pt-BR") ? Component.text("Você comprou uma bússola simples por $" + price + "!", NamedTextColor.GRAY) :
            lang.equals("es-ES") ? Component.text("¡Has comprado una brújula simple por $" + price + "!", NamedTextColor.GRAY) :
            Component.text("You bought a simple compass for $" + price + "!", NamedTextColor.GRAY)
        )
    );
}

public void buySimpleFishingRod(Player player) {
    int price = config.getInt("store.price.buySimpleFishingRod", 200); // 🔹 Obtém preço do config.yml, com fallback de 200
    if (!processPurchase(player, price)) return; // 🔹 Interrompe se a compra falhar

    // 🔹 Executa o comando para dar uma vara de pesca encantada ao jogador
    String command = String.format(
        "give %s minecraft:fishing_rod 1", 
        player.getName()
    );
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); // 🔹 Executa o comando como console

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    player.sendMessage(
        Component.text("🎣 ").append(
            lang.equals("pt-BR") ? Component.text("Você comprou uma Vara de Pesca com Isca por $" + price + "!", NamedTextColor.GRAY) :
            lang.equals("es-ES") ? Component.text("¡Has comprado una Caña de Pescar con Cebo por $" + price + "!", NamedTextColor.GRAY) :
            Component.text("You bought a Fishing Rod with Bait for $" + price + "!", NamedTextColor.GRAY)
        )
    );
}

public void buySpinningWand(Player player) {
    int price = config.getInt("store.price.buySpinningWand", 800); // 🔹 Obtém do config.yml, com fallback de 800
    if (!processPurchase(player, price)) return; // 🔹 Interrompe se a compra falhar

    // 🔹 Executa o comando para dar um Debug Stick ao jogador
    String command = String.format("give %s minecraft:debug_stick 1", player.getName());
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); // 🔹 Executa o comando como console

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    player.sendMessage(Component.text("✨ ").append(
        lang.equals("pt-BR") ? Component.text("Você comprou um Debug Stick por $" + price + "!", NamedTextColor.LIGHT_PURPLE) :
        lang.equals("es-ES") ? Component.text("¡Has comprado un Debug Stick por $" + price + "!", NamedTextColor.LIGHT_PURPLE) :
        Component.text("You bought a Debug Stick for $" + price + "!", NamedTextColor.LIGHT_PURPLE)
    ));
}


public void buyAxolotlBucket(Player player) {
    int price = config.getInt("store.price.axolotl_bucket", 500); // 🔹 Obtém preço do config.yml, com fallback de 500
    if (!processPurchase(player, price)) return; // 🔹 Interrompe se a compra falhar

    // 🔹 Executa o comando para dar um balde com Axolote ao jogador
    String command = String.format("give %s minecraft:axolotl_bucket 1", player.getName());
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); // 🔹 Executa o comando como console

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    player.sendMessage(
        Component.text("🪣 ").append(
            lang.equals("pt-BR") ? Component.text("Você comprou um balde com Axolote por $" + price + "!", NamedTextColor.AQUA) :
            lang.equals("es-ES") ? Component.text("¡Has comprado un cubo con Axolote por $" + price + "!", NamedTextColor.AQUA) :
            Component.text("You bought an Axolotl Bucket for $" + price + "!", NamedTextColor.AQUA)
        )
    );
}
    
    public void buyEnchantedPickaxe(Player player) {
    int price = config.getInt("store.price.enchanted_pickaxe", 5000); // 🔹 Obtém preço do config.yml, com fallback de 5000
    if (!processPurchase(player, price)) return; // 🔹 Interrompe se a compra falhar

    // 🔹 Executa o comando para dar a picareta encantada ao jogador
    String command = String.format(
    "minecraft:give %s diamond_pickaxe 1 0 {Enchantments:[{id:\"efficiency\",lvl:5},{id:\"unbreaking\",lvl:3},{id:\"fortune\",lvl:3}]}", 
    player.getName()
);
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); // Executa o comando como console

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    player.sendMessage(
        Component.text("⛏️ ").append(
            lang.equals("pt-BR") ? Component.text("Você comprou uma Picareta Encantada por $" + price + "!", NamedTextColor.GOLD) :
            lang.equals("es-ES") ? Component.text("¡Has comprado un Pico Encantado por $" + price + "!", NamedTextColor.GOLD) :
            Component.text("You bought an Enchanted Pickaxe for $" + price + "!", NamedTextColor.GOLD)
        )
    );
}


}// 🔹 Fim da classe Store