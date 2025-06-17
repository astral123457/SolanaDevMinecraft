package com.SolanaDevMinecraftPaperFabric;

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

public class Store {
    private final Connection connection;
    private final FileConfiguration config;
    private static Economy economy;
    private final JavaPlugin plugin;

    // 🔹 Construtor correto que inicializa 'config' e 'connection'
    public Store(JavaPlugin plugin, FileConfiguration config, Connection connection) {
        this.plugin = plugin;
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
        int price = config.getInt("store.price.apple"); // 🔹 Obtém preço 
        String lang = getPlayerLanguage(player);
        player.sendMessage("🍳. Lang= " + lang);
        //int price = 500;
        if (processPurchase(player, price)) {
            player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));

        // Ajusta o saldo do jogador após a compra
        ajustarSaldo(player, "take", price);

            
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
            // Ajusta o saldo do jogador após a compra
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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

    public void buyBootRelic(Player player) {
    int price = config.getInt("store.price.boot_relic", 40000);

    if (!processPurchase(player, price)) return;

    ItemStack bootRelic = new ItemStack(Material.NETHERITE_BOOTS);
    ItemMeta meta = bootRelic.getItemMeta();

    if (meta != null) {
        meta.setUnbreakable(true);
        meta.displayName(Component.text("👢 Relíquia Meow Cat das Botas Celestiais").color(NamedTextColor.LIGHT_PURPLE));
        meta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);   // Passos profundos
        meta.addEnchant(Enchantment.MENDING, 1, true);         // Reparação
        meta.addEnchant(Enchantment.DURABILITY, 3, true);  // 🔥 Resistência extra
        //meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);   // Maldição de vínculo

        bootRelic.setItemMeta(meta);
    }

    plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.getInventory().addItem(bootRelic);
        });

    String message = switch (getPlayerLanguage(player)) {
        case "pt-BR" -> "👢 Você comprou a Relíquia Meow Cat das Botas Celestiais por $" + price + "!";
        case "es-ES" -> "👢 ¡Has comprado las Botas Reliquia Meow Cat Celestiales por $" + price + "!";
        default -> "👢 You bought the Meow Cat Celestial Boots Relic for $" + price + "!";
    };

    player.sendMessage(Component.text(message).color(NamedTextColor.LIGHT_PURPLE));
}

public void buyShulkerKit(Player player) {
    int price = config.getInt("store.price.shulker_kit", 50000);

    if (!processPurchase(player, price)) return;

    // Lista com todas as cores de shulker box
    Material[] shulkerColors = {
        Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX,
        Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX,
        Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX,
        Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX,
        Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX,
        Material.BLACK_SHULKER_BOX
    };

    for (Material shulker : shulkerColors) {
        ItemStack item = new ItemStack(shulker);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("📦 Shulker Colorida").color(NamedTextColor.GOLD));
            item.setItemMeta(meta);
        }
        player.getInventory().addItem(item);
    }

    String message = switch (getPlayerLanguage(player)) {
        case "pt-BR" -> "📦 Você comprou o Kit Shulker Colorida por $" + price + "!";
        case "es-ES" -> "📦 ¡Has comprado el Kit Shulker Colorida por $" + price + "!";
        default -> "📦 You bought the Colorful Shulker Kit for $" + price + "!";
    };

    player.sendMessage(Component.text(message).color(NamedTextColor.GOLD));
}

public void buyThorAxe(Player player) {
    int price = config.getInt("store.price.thor_axe", 60000);

    if (!processPurchase(player, price)) return;

    ItemStack thorAxe = new ItemStack(Material.NETHERITE_AXE);
    ItemMeta meta = thorAxe.getItemMeta();

    if (meta != null) {
        meta.setUnbreakable(true);
        meta.displayName(Component.text("⚡ Machado de Thor: Stormbreaker").color(NamedTextColor.AQUA));

        meta.addEnchant(Enchantment.MENDING, 1, true);         // Reparação
        meta.addEnchant(Enchantment.DURABILITY, 3, true);  // 🔥 Resistência extra
        meta.addEnchant(Enchantment.KNOCKBACK, 2, true);       // Repulsão
        //meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);   // Maldição de vínculo

        thorAxe.setItemMeta(meta);
    }

    plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
        player.getInventory().addItem(thorAxe);
    });

    String message = switch (getPlayerLanguage(player)) {
        case "pt-BR" -> "⚡ Você empunha agora o Machado de Thor por $" + price + "!";
        case "es-ES" -> "⚡ ¡Has adquirido el Hacha de Thor por $" + price + "!";
        default -> "⚡ You now wield Thor's Axe for $" + price + "!";
    };

    player.sendMessage(Component.text(message).color(NamedTextColor.AQUA));
}


public void venderEscadas(Player jogador) {
    int quantidade = 33; // Quantidade fixa de escadas vendidas
    int precoPorEscada = config.getInt("store.self.stairs", 10); // Obtém o preço do config.yml
    int total = precoPorEscada * quantidade;

    // Lista de todos os tipos de escadas no Minecraft
    Material[] escadas = {
    Material.OAK_STAIRS, Material.SPRUCE_STAIRS, Material.BIRCH_STAIRS, Material.JUNGLE_STAIRS,
    Material.ACACIA_STAIRS, Material.DARK_OAK_STAIRS, Material.MANGROVE_STAIRS, Material.CHERRY_STAIRS,
    Material.BAMBOO_STAIRS, Material.CRIMSON_STAIRS, Material.WARPED_STAIRS, Material.BAMBOO_MOSAIC_STAIRS,
    Material.COBBLESTONE_STAIRS, Material.MOSSY_COBBLESTONE_STAIRS, Material.STONE_STAIRS,
    Material.GRANITE_STAIRS, Material.POLISHED_GRANITE_STAIRS, Material.DIORITE_STAIRS,
    Material.POLISHED_DIORITE_STAIRS, Material.ANDESITE_STAIRS, Material.POLISHED_ANDESITE_STAIRS,
    Material.BLACKSTONE_STAIRS, Material.POLISHED_BLACKSTONE_STAIRS, Material.POLISHED_BLACKSTONE_BRICK_STAIRS,
    Material.NETHER_BRICK_STAIRS, Material.RED_NETHER_BRICK_STAIRS, Material.END_STONE_BRICK_STAIRS,
    Material.PURPUR_STAIRS, Material.QUARTZ_STAIRS, Material.SMOOTH_QUARTZ_STAIRS,
    Material.PRISMARINE_STAIRS, Material.PRISMARINE_BRICK_STAIRS, Material.DARK_PRISMARINE_STAIRS,
    Material.SANDSTONE_STAIRS, Material.SMOOTH_SANDSTONE_STAIRS, Material.RED_SANDSTONE_STAIRS,
    Material.SMOOTH_RED_SANDSTONE_STAIRS, Material.BRICK_STAIRS, Material.MUD_BRICK_STAIRS,
    Material.LADDER // Escada de mão
};

    boolean temEscadas = false;

    for (Material tipoEscada : escadas) {
        ItemStack escada = new ItemStack(tipoEscada, quantidade);
        if (jogador.getInventory().containsAtLeast(escada, quantidade)) {
            // Remove as escadas do inventário
            jogador.getInventory().removeItem(escada);
            temEscadas = true;
        }
    }

    if (temEscadas) {
        // Converte o dinheiro para Solana (opcional)
        int taxaConversao = config.getInt("store.value_of_in_game_currency", 1000);
        double solanaRecebida = (double) total / taxaConversao;

        // Adiciona dinheiro ao banco do jogador (moeda do jogo)
        boolean vendaEfetuada = processPurchase(jogador, -total); // Usa valor negativo para adicionar dinheiro

        if (vendaEfetuada) {
            jogador.sendMessage(Component.text("💰 Você vendeu 33 escadas por $" + total + " (" + solanaRecebida + " SOL)!").color(NamedTextColor.GREEN));
        } else {
            jogador.sendMessage(Component.text("⚠ Erro ao vender escadas. Verifique seu saldo no banco.").color(NamedTextColor.RED));
        }
    } else {
        jogador.sendMessage(Component.text("❌ Você não tem 33 escadas para vender!").color(NamedTextColor.RED));
    }
}

public void buyNetherRelic(Player player) {
    // 🔹 Obtém o preço da relíquia do config.yml, com fallback de 25000
    int price = config.getInt("store.price.nether_relic", 25000);

    // 🔹 Verifica se o jogador tem saldo suficiente e processa a compra
    if (!processPurchase(player, price)) return;

    // 🔹 Cria o capacete especial da Relíquia do Nether
    ItemStack netherRelic = new ItemStack(Material.GOLDEN_HELMET);
    ItemMeta meta = netherRelic.getItemMeta();
    
    if (meta != null) {
        meta.setUnbreakable(true); // 🔥 Torna o capacete indestrutível
        meta.displayName(Component.text("Relíquia ELmo Arcanjo Uriel").color(NamedTextColor.GOLD)); // 🔥 Define nome personalizado
        
        // 🔹 Adiciona encantamentos essenciais
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true); // 🔥 Proteção contra fogo máxima
        meta.addEnchant(Enchantment.MENDING, 1, true); // 🔥 Reparação automática
        meta.addEnchant(Enchantment.DURABILITY, 3, true);  // 🔥 Resistência extra
        
        netherRelic.setItemMeta(meta);
    }

    // 🔹 Entrega o item dentro da região global para evitar problemas no Folia
    plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
        player.getInventory().addItem(netherRelic);
    });

    // 🔹 Define mensagem conforme o idioma do jogador
    String lang = getPlayerLanguage(player);
    String message = switch (lang) {
        case "pt-BR" -> "🔥 Você comprou a Relíquia do Nether por $" + price + "!";
        case "es-ES" -> "🔥 ¡Has comprado la Reliquia del Nether por $" + price + "!";
        default -> "🔥 You bought the Nether Relic for $" + price + "!";
    };

    // 🔹 Envia mensagem ao jogador
    player.sendMessage(Component.text(message).color(NamedTextColor.GOLD));
}



public void buyWingRelic(Player player) {
    // 🔹 Obtém o preço da relíquia das asas no config.yml, com fallback de 50000
    int price = config.getInt("store.price.wing_relic", 50000);

    // 🔹 Verifica se o jogador tem saldo suficiente e processa a compra
    if (!processPurchase(player, price)) return;

    // 🔹 Cria o item da Relíquia das Asas
    ItemStack wingRelic = new ItemStack(Material.ELYTRA);
    ItemMeta meta = wingRelic.getItemMeta();

    if (meta != null) {
        meta.setUnbreakable(true); // 🔥 Torna indestrutível
        meta.displayName(Component.text("🚀 Relíquia Amauris gênero de borboletas").color(NamedTextColor.GOLD)); // 🔥 Define nome personalizado

        // 🔹 Adiciona encantamentos essenciais
        meta.addEnchant(Enchantment.MENDING, 1, true); // 🔥 Reparação automática
        meta.addEnchant(Enchantment.DURABILITY, 3, true);  // 🔥 Resistência extra
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true); // 🔥 Maldição de vínculo (não pode ser removida)

        wingRelic.setItemMeta(meta);
    }

    // 🔹 Entrega o item dentro da região global para evitar problemas no Folia
    plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
        player.getInventory().addItem(wingRelic);
    });

    // 🔹 Define mensagem conforme o idioma do jogador
    String message = switch (getPlayerLanguage(player)) {
        case "pt-BR" -> "🚀 Você comprou a Asa Relíquia Amauris gênero de borboletas por $" + price + "!";
        case "es-ES" -> "🚀 ¡Has comprado las Alas Reliquia Amauris género de borboletas por $" + price + "!";
        default -> "🚀 You bought the Amauris Wing Relic for $" + price + "!";
    };

    // 🔹 Envia mensagem ao jogador
    player.sendMessage(Component.text(message).color(NamedTextColor.GOLD));
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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", totalPrice);

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
        ajustarSaldo(player, "take", totalPrice);

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
        ajustarSaldo(player, "take", price);

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
    // Ajusta o saldo do jogador após a compra
        ajustarSaldo(player, "take", price);

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
    // 🔹 Executa o comando para dar um mapa ao jogador

    for (String command : commands) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    

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
    int price = config.getInt("store.price.buySimpleCompass", 150);
    if (!processPurchase(player, price)) return;
    // Ajusta o saldo do jogador após a compra
        ajustarSaldo(player, "take", price);

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

    for (String command : commands) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
        ajustarSaldo(player, "take", price);

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
    public static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> serviceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (serviceProvider != null) {
            economy = serviceProvider.getProvider();
        }
        return economy != null;
    }

    public static void ajustarSaldo(Player player, String tipo, double valor) {
       if (economy == null && !setupEconomy()) {
        player.sendMessage("Sistema de economia não está configurado!");
        return;
    }

        switch (tipo.toLowerCase()) {
            case "give":
                economy.depositPlayer(player, valor);
                break;
            case "take":
                economy.withdrawPlayer(player, valor);
                break;
            case "set":
                double saldoAtual = economy.getBalance(player);
                economy.withdrawPlayer(player, saldoAtual);
                economy.depositPlayer(player, valor);

                break;
            default:
                player.sendMessage("Comando inválido! Use 'give', 'take' ou 'set'.");
                break;
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