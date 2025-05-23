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
import org.bukkit.potion.PotionType;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionData;
import org.bukkit.inventory.meta.PotionMeta; // 🔹 Correto para poções
import org.bukkit.inventory.meta.BlockStateMeta; // 🔹 Necessário para Shulker Box

import org.bukkit.enchantments.Enchantment; // 🔹 Correto para encantamentos
import org.bukkit.inventory.meta.EnchantmentStorageMeta; // 🔹 Necessário para livros encantados
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import java.util.Arrays;
import java.sql.SQLException;



import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Sound;
import org.bukkit.Particle;
import java.util.concurrent.CompletableFuture;

public class Store {
    private final Connection connection;
    private final FileConfiguration config;
    private static Economy economy;
    private JavaPlugin plugin;



    // 🔹 Construtor correto que inicializa 'config' e 'connection'
    public Store(FileConfiguration config, Connection connection) {
        this.config = config;
        this.plugin = plugin;
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
    try (PreparedStatement stmt = connection.prepareStatement("SELECT saldo FROM banco WHERE jogador = ?")) {
        stmt.setString(1, player.getName());

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int saldo = rs.getInt("saldo");
                if (saldo >= price) {
                    try (PreparedStatement updateStmt = connection.prepareStatement("UPDATE banco SET saldo = saldo - ? WHERE jogador = ?")) {
                        updateStmt.setInt(1, price);
                        updateStmt.setString(2, player.getName());
                        updateStmt.executeUpdate();
                    }
                    return true;
                } else {
                    int falta = price - saldo;
                    String lang = getPlayerLanguage(player);
                    player.sendMessage(lang.equals("pt-BR") ?
                        Component.text("💰 Saldo insuficiente para realizar a compra. Falta: ", NamedTextColor.RED)
                        .append(Component.text(falta, NamedTextColor.YELLOW)) :
                        Component.text("💰 Insufficient balance to make the purchase. Missing: ", NamedTextColor.RED)
                        .append(Component.text(falta, NamedTextColor.YELLOW)));
                    return false;
                }
            }
        }
    } catch (Exception e) {
        player.sendMessage(Component.text("⚠ Erro ao acessar o banco de dados.", NamedTextColor.RED));
        e.printStackTrace();
        return false;
    }

    return false;
    }

    // 📌 Compra de Maçã Encantada
    public void buyEnchantedApple(Player player) {
    System.out.println("DEBUG (buyEnchantedApple): Iniciado para " + player.getName()); // <-- Use System.out.println
    int price = config.getInt("store.price.apple");
    System.out.println("DEBUG (buyEnchantedApple): Preço da maçã lido: " + price); // <-- Use System.out.println
    String lang = getPlayerLanguage(player); // Se getPlayerLanguage está em outra classe, o log interno dela precisa ser ajustado
    player.sendMessage("🍳. Lang= " + lang); // Esta mensagem aparece?

    System.out.println("DEBUG (buyEnchantedApple): Chamando processPurchase..."); // <-- Use System.out.println
    if (processPurchase(player, price)) {
        System.out.println("DEBUG (buyEnchantedApple): Compra processada com sucesso. Adicionando item..."); // <-- Use System.out.println
        player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));
        System.out.println("DEBUG (buyEnchantedApple): Item adicionado. Ajustando saldo..."); // <-- Use System.out.println
        //ajustarSaldo(player, "take", price);
        System.out.println("DEBUG (buyEnchantedApple): Saldo ajustado. Enviando mensagem final..."); // <-- Use System.out.println
        player.sendMessage(
            lang.equals("pt-BR") ?
            Component.text("🍎 Você comprou uma Maçã Encantada por $" + price + "!", NamedTextColor.GOLD) :
            lang.equals("es-ES") ?
            Component.text("🍎 ¡Has comprado una Manzana Encantada por $" + price + "!", NamedTextColor.GOLD) :
            Component.text("🍎 You bought an Enchanted Apple for $" + price + "!", NamedTextColor.GOLD)
        );
        System.out.println("DEBUG (buyEnchantedApple): Mensagem final enviada."); // <-- Use System.out.println
    } else {
        System.out.println("DEBUG (buyEnchantedApple): processPurchase retornou false. Mensagem de compra não enviada."); // <-- Use System.out.println
    }
    System.out.println("DEBUG (buyEnchantedApple): Finalizado."); // <-- Use System.out.println
}

    // 📌 Compra de Esmeralda
    public void buyEmerald(Player player) {
        int price = config.getInt("store.price.emerald"); // 🔹 Obtém preço 
        //int price = 1000;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.EMERALD, 1));
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            //player.getInventory().addItem(new ItemStack(Material.NETHERITE_BLOCK, 1));
            player.getInventory().addItem(new ItemStack(Material.ANCIENT_DEBRIS, 10));
            player.getInventory().addItem(new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1));
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
            // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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

public void buyAllTools(Player player) {
    int totalPrice = config.getInt("store.price.buyAllTools");
    //int totalPrice = 5000; // Defina o preço total para todas as ferramentas
    if (processPurchase(player, totalPrice)) {
        List<Material> tools = List.of(
            Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL,
            Material.DIAMOND_HOE, Material.DIAMOND_SWORD
        );
        // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", totalPrice);

        for (Material tool : tools) {
            ItemStack toolItem = new ItemStack(tool, 1);
            player.getInventory().addItem(toolItem);
        }

        String lang = getPlayerLanguage(player);
        player.sendMessage(
            lang.equals("pt-BR") ? Component.text("🛠️ Você comprou todas as ferramentas por $" + totalPrice + "!", NamedTextColor.GOLD) :
            lang.equals("es-ES") ? Component.text("🛠️ ¡Has comprado todas las herramientas por $" + totalPrice + "!", NamedTextColor.GOLD) :
            Component.text("🛠️ You bought all tools for $" + totalPrice + "!", NamedTextColor.GOLD)
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

        // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", totalPrice);

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

    // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
    int price = config.getInt("store.price.buySimpleMap", 100); // 🔹 Obtém preço do config.yml, fallback de 100

    // Processa a compra e ajusta saldo
    if (!processPurchase(player, price)) return;
    //ajustarSaldo(player, "take", price);

    // Lista de comandos ajustados
    List<String> commands = Arrays.asList(
        "minecraft:enchant " + player.getName() + " mending 1",
        "minecraft:enchant " + player.getName() + " efficiency 5",
        "minecraft:enchant " + player.getName() + " fortune 3",
        "minecraft:enchant " + player.getName() + " unbreaking 3",
        "minecraft:enchant " + player.getName() + " featherfall 4",
        "minecraft:enchant " + player.getName() + " frostwalker 2",
        "minecraft:enchant " + player.getName() + " projectileprotection 4",
        "minecraft:enchant " + player.getName() + " soulspeed 3",
        "minecraft:enchant " + player.getName() + " swiftsneak 3",
        "minecraft:enchant " + player.getName() + " respiration 3",
        "minecraft:enchant " + player.getName() + " thorns 3",
        "minecraft:give " + player.getName() + " filled_map 1"
    );

    // 🔹 Executa comandos no console de forma segura
    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }, 20L); // Executa após 20 ticks (~1 segundo)

    // 🔹 Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    String message = lang.equals("pt-BR") ?
        "🗺️ Você comprou um mapa simples por $" + price + "!" :
        lang.equals("es-ES") ?
        "🗺️ ¡Has comprado un mapa simple por $" + price + "!" :
        "🗺️ You bought a simple map for $" + price + "!";

    player.sendMessage(Component.text(message).color(NamedTextColor.GRAY));
}

public void buySimpleCompass(Player player) {
    int price = config.getInt("store.price.buySimpleCompass", 150);

    // Processa a compra e ajusta saldo
    if (!processPurchase(player, price)) return;
    //ajustarSaldo(player, "take", price);

    // Lista de comandos ajustados
    List<String> commands = Arrays.asList(
        "minecraft:enchant " + player.getName() + " looting 3",
        "minecraft:enchant " + player.getName() + " mending 1",
        "minecraft:enchant " + player.getName() + " smite 5",
        "minecraft:enchant " + player.getName() + " knockback 2",
        "minecraft:enchant " + player.getName() + " fire_aspect 2",
        "minecraft:enchant " + player.getName() + " unbreaking 3",
        "minecraft:give " + player.getName() + " recovery_compass 1"
    );

    // Executa os comandos de forma agendada para evitar conflitos no Folia
    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }, 20L); // Executa após 20 ticks (~1 segundo)

    // Mensagem para o jogador
    String lang = getPlayerLanguage(player);
    String message = lang.equals("pt-BR") ?
        "🧭 Você comprou uma bússola simples por $" + price + "!" :
        lang.equals("es-ES") ?
        "🧭 ¡Has comprado una brújula simple por $" + price + "!" :
        "🧭 You bought a simple compass for $" + price + "!";

    player.sendMessage(Component.text(message).color(NamedTextColor.GRAY));
}

public void buySimpleFishingRod(Player player) {
    int price = config.getInt("store.price.buySimpleFishingRod", 200); // 🔹 Obtém preço do config.yml, com fallback de 200
    if (!processPurchase(player, price)) return; // 🔹 Interrompe se a compra falhar
    // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
    // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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
    // Ajusta o saldo do jogador após a compra
        //ajustarSaldo(player, "take", price);

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


// 📌 Método para ajustar o saldo do jogador do sql do plugin EssentialsX (nao e necessario mas tenta mater os dados iguais do sql e do mysql)
//fallback
    public void ajustarSaldo(Player player, String tipo, double valor) {
    System.out.println("DEBUG (ajustarSaldo): Iniciado para " + player.getName() + ", tipo: " + tipo + ", quantia: " + valor);

    // *** NOVA LINHA DE DEBUG: Verificar se 'plugin' é nulo ***
    if (this.plugin == null) {
        System.err.println("ERROR (ajustarSaldo): Instância do plugin é NULA! Não é possível agendar a tarefa.");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
 player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
                            player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.05);
        // Saia do método para evitar um NullPointerException
        return;
    }
    System.out.println("DEBUG (ajustarSaldo): Instância do plugin está OK.");

    final String playerName = player.getName(); // Captura o nome do jogador

    try {
        // Bloco try-catch para capturar exceções do próprio runTaskLater
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> { // Use 'this.plugin' para clareza
            try {
                System.out.println("DEBUG (ajustarSaldo - Main Thread): Executando comando eco para " + playerName + "...");
                if (tipo.equalsIgnoreCase("give")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + playerName + " " + valor);
                    System.out.println("DEBUG (ajustarSaldo - Main Thread): Executado 'eco give " + playerName + " " + valor + "'");
                } else if (tipo.equalsIgnoreCase("take")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco take " + playerName + " " + valor);
                    System.out.println("DEBUG (ajustarSaldo - Main Thread): Executado 'eco take " + playerName + " " + valor + "'");
                } else if (tipo.equalsIgnoreCase("set")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco set " + playerName + " " + valor);
                    System.out.println("DEBUG (ajustarSaldo - Main Thread): Executado 'eco set " + playerName + " " + valor + "'");
                } else {
                    Player onlinePlayer = Bukkit.getPlayer(playerName);
                    if (onlinePlayer != null && onlinePlayer.isOnline()) {
                        onlinePlayer.sendMessage("Comando inválido! Use 'give' ou 'take' ou set.");
                    }
                    System.out.println("DEBUG (ajustarSaldo - Main Thread): Tipo de ajuste inválido para " + playerName + ": " + tipo);
                }
                System.out.println("DEBUG (ajustarSaldo - Main Thread): Comando eco despachado com sucesso.");
            } catch (Exception e) {
                System.err.println("ERROR (ajustarSaldo - Main Thread - Inner): Erro ao despachar comando eco para " + playerName);
                e.printStackTrace(); // Imprime o stack trace completo da exceção interna!
            }
        }, 0L); // 0L significa executar na próxima tick disponível

        System.out.println("DEBUG (ajustarSaldo): Chamada para agendador da thread principal finalizada.");

    } catch (Exception e) {
        // Este catch pegará exceções se o próprio agendamento falhar (muito raro, mas possível)
        System.err.println("ERROR (ajustarSaldo - Outer): Exceção ao agendar tarefa com Bukkit.getScheduler()!");
        e.printStackTrace(); // Imprime o stack trace completo da exceção de agendamento!
    }
}

public void transferirtokengamer(Player player, String recipient, double amount) {
    try (PreparedStatement stmtJogador = connection.prepareStatement("UPDATE banco SET saldo = saldo - ? WHERE jogador = ?");
         PreparedStatement stmtDestinatario = connection.prepareStatement("UPDATE banco SET saldo = saldo + ? WHERE jogador = ?")) {

        stmtJogador.setDouble(1, amount);
        stmtJogador.setString(2, player.getName()); // Corrigido: Usar o nome do jogador
        stmtJogador.executeUpdate();

        stmtDestinatario.setDouble(1, amount);
        stmtDestinatario.setString(2, recipient); // Já está correto
        stmtDestinatario.executeUpdate();

        // Comandos do Bukkit para manter a sincronização com o sistema do jogo
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + recipient + " " + amount);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco take " + player.getName() + " " + amount); // Corrigido

    } catch (SQLException e) {
        System.out.println("⚠ Erro ao atualizar o banco de dados: " + e.getMessage());
        e.printStackTrace();
    }
}



}// 🔹 Fim da classe Store