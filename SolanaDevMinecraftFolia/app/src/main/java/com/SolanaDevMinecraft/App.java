package com.SolanaDevMinecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import net.md_5.bungee.api.ChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.List;
import java.util.Locale;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Filter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Sound;
import org.bukkit.Particle;
import java.util.Arrays;
import org.bukkit.OfflinePlayer;
import org.bukkit.BanList;
import java.util.Set;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerBedEnterEvent;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Duration;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;




public class App extends JavaPlugin implements Listener {

    private final Map<Player, Player> tpaRequests = new HashMap<>();
    private final Map<Player, Location> lastLocations = new HashMap<>();
    private final Map<Player, Location> homes = new HashMap<>();
    private final Map<Player, Map<String, Location>> casas = new HashMap<>();
    private final Map<Location, String> lockedChests = new HashMap<>();
    private final Map<Player, String> playerLanguages = new HashMap<>();
    private final Map<Player, String> playerNames = new HashMap<>();
    private final Map<Player, String> playerWallets = new HashMap<>();
    



    private Connection connection;
    private Solana solana;
    private Store store; // Instância da classe Store
    private FileConfiguration config; // Armazena o config.yml
    private static Economy economy;
    private static App plugin;
    private static final Logger logger = Logger.getLogger("SolanaDevMinecraft");
    private static final String PLUGIN_NAME = "SolanaDevMinecraft";
    private static final String LOG_FILE_NAME = "SolanaDevMinecraft.log";
    private static final String LOG_FILE_PATH = "plugins/SolanaDevMinecraft/" + LOG_FILE_NAME;

    private Connection getDatabaseConnection() throws SQLException {
    String url = config.getString("database.url");
    String user = config.getString("database.user");
    String password = config.getString("database.password");

    return DriverManager.getConnection(url, user, password);
}

    @Override
    public void onEnable() {
        plugin = this; // 🔥 Inicializa a instância do plugin
        getServer().getPluginManager().registerEvents(this, this);
        // Salva o config.yml na pasta do plugin, caso ainda não exista
        saveDefaultConfig();
        config = getConfig(); // Inicializa config.yml corretamente
        getLogger().info("Plugin habilitado!");
        
        connectToDatabase();
        
        solana = new Solana(this, config, connection); // Passa config.yml e conexão para Solana
        store = new Store(getConfig(), connection);
// Passa config.yml e conexão para Store

        // 🔹 Cria banco e tabelas automaticamente
        createDatabaseAndTables();

        // Atualiza juros a cada 60 segundos
        // Executa diretamente sem agendamento
updateDebts();

// Executa o processamento de investimentos imediatamente para todos os jogadores conectados
for (Player player : Bukkit.getOnlinePlayers()) {
    processInvestments(player, player.locale().toString());
}



        
}






    @Override
    public void onDisable() {
        getLogger().info("Plugin desabilitado!");
        disconnectFromDatabase();
    }




@EventHandler
@Deprecated
public void aoEntrarNoServidor(PlayerJoinEvent event) {
    Player jogador = event.getPlayer();

    // 🔄 Carrega a casa do jogador
    carregarCasa(jogador, "default");

    // 🎉 Mensagem de boas-vindas
    jogador.sendTitle(ChatColor.GREEN + "Bem-vindo!", ChatColor.WHITE + jogador.getName(), 10, 70, 20);
    jogador.playSound(jogador.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    jogador.sendMessage(ChatColor.GREEN + "🎉 Bem-vindo ao servidor, " + jogador.getName() + "!");

    // Contador seguro para uso em lambda
    AtomicInteger count = new AtomicInteger(0);

    // 🔄 Carregar baús trancados do banco de forma assíncrona
    CompletableFuture.runAsync(() -> {
        try (Connection conn = getDatabaseConnection()) {
            if (conn == null) {
                getLogger().severe("❌ Erro: Conexão com banco de dados não encontrada!");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement("SELECT world, x, y, z, password FROM locked_chests");
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String worldName = rs.getString("world");
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    String password = rs.getString("password");

                    World world = Bukkit.getWorld(worldName);
                    if (world == null) continue; // Evita erro caso o mundo não seja encontrado

                    Location chestLocation = new Location(world, x, y, z);
                    lockedChests.put(chestLocation, password);
                    count.incrementAndGet();
                }
            }

            jogador.sendMessage(ChatColor.YELLOW + "🔒 " + count.get() + " baú(s) trancado(s) restaurado(s)!");
            getLogger().info("✅ Restaurados " + count.get() + " baús trancados para " + jogador.getName());

        } catch (SQLException e) {
            getLogger().severe("❌ Erro ao carregar baús trancados: " + e.getMessage());
        }
    });
}

@EventHandler
public void aoAbrirBau(InventoryOpenEvent event) {
    if (!(event.getPlayer() instanceof Player)) return;

    Player jogador = (Player) event.getPlayer();
    Location chestLocation = event.getInventory().getLocation();

    if (chestLocation != null && lockedChests.containsKey(chestLocation)) {
        event.setCancelled(true); // Impede a abertura do baú
        jogador.playSound(jogador.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        jogador.sendMessage(ChatColor.RED + "❌ Este baú está trancado! Use sua etiqueta de senha para desbloquear.");
    }
}

@EventHandler
public void aoQuebrarBau(BlockBreakEvent event) {
    Block block = event.getBlock();

    if (!(block.getState() instanceof Chest)) return; // Ignora blocos que não são baús

    Location chestLocation = block.getLocation();

    if (lockedChests.containsKey(chestLocation)) {
        event.setCancelled(true); // 🔒 Impede a destruição do baú
        Player jogador = event.getPlayer();
        jogador.playSound(jogador.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        jogador.sendMessage(ChatColor.RED + "❌ Este baú está trancado! Você não pode quebrá-lo.");
    }
}

@EventHandler
public void aoClicarNoBau(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

    Player jogador = event.getPlayer();
    Block block = event.getClickedBlock();
    if (block == null || !(block.getState() instanceof Chest)) return;

    Location chestLocation = block.getLocation();

    // 🔒 Verifica se o baú está trancado
    if (!lockedChests.containsKey(chestLocation)) return;

    // 🔍 Verifica se o jogador está segurando uma etiqueta de senha
    ItemStack itemNaMao = jogador.getInventory().getItemInMainHand();
    if (itemNaMao.getType() != Material.PAPER) {
        jogador.sendMessage(ChatColor.RED + "❌ Você precisa segurar uma etiqueta de senha para destrancar este baú!");
        return;
    }

    // 🔑 Obtém a senha da etiqueta pelo nome do item (`getDisplayName()`)
    String senhaEtiqueta = itemNaMao.getItemMeta().getDisplayName().replace("Senha: ", "").trim();
    String senhaCorreta = lockedChests.get(chestLocation);

    if (senhaEtiqueta.equals(senhaCorreta)) {
        jogador.sendMessage(ChatColor.GREEN + "✅ Baú destrancado! Ele será trancado novamente em 10 segundos.");
        getLogger().info("🔓 Baú destrancado temporariamente por " + jogador.getName());
        lockedChests.remove(chestLocation); // Remove a proteção temporariamente

        // ⏳ Tranca o baú automaticamente após 10 segundos usando `RegionScheduler` do Folia
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
    lockedChests.put(chestLocation, senhaCorreta);
    jogador.sendMessage(ChatColor.RED + "🔒 O baú foi trancado novamente!");
    getLogger().info("🔒 Baú trancado automaticamente.");
}, 200L);


    } else {
        jogador.sendMessage(ChatColor.RED + "❌ Senha incorreta! Tente novamente.");
        getLogger().warning("⚠️ Tentativa de desbloqueio falha por " + jogador.getName());
    }
}






@EventHandler
public void aoDormir(PlayerBedEnterEvent event) {
    if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return; // Apenas define a home se o jogador conseguir dormir

    Player jogador = event.getPlayer();
    Location cama = event.getBed().getLocation(); // Obtém a localização correta da cama
    cama.setY(cama.getY() + 1); // Ajusta a Y para evitar que o jogador fique preso

    // Verifica se o jogador já tem casas registradas
    casas.computeIfAbsent(jogador, k -> new HashMap<>());

    // Salva ou atualiza a casa "default" do jogador
    casas.get(jogador).put("default", cama);

    jogador.sendMessage(ChatColor.GREEN + "🏡 Sua casa principal ('default') foi definida automaticamente na cama!");

    // Opcional: Salvar no banco de dados para persistência
    registrarCasa(jogador, "default", cama);
}




    @EventHandler
public void onPlayerTeleport(PlayerTeleportEvent event) {
    lastLocations.put(event.getPlayer(), event.getFrom());
}

@EventHandler
public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    
    if (player == null) {
        getLogger().warning("❌ O evento de morte ocorreu, mas o jogador é nulo!");
        return;
    }

    Location deathLocation = player.getLocation();
    if (deathLocation == null) {
        getLogger().warning("❌ A localização do jogador no momento da morte é nula!");
        return;
    }

    lastLocations.put(player, deathLocation);
    getLogger().info("🛠️ Localização da morte armazenada para: " + player.getName() +
                     " | X: " + deathLocation.getX() + " Y: " + deathLocation.getY() +
                     " Z: " + deathLocation.getZ());
}





    private void connectToDatabase() {
        try {
            String url = config.getString("database.url");
            String user = config.getString("database.user");
            String password = config.getString("database.password");

            getLogger().info("Tentando conectar ao banco de dados com URL: " + url);
            connection = DriverManager.getConnection(url, user, password);
            getLogger().info("Conectado ao banco de dados!");
        } catch (Exception e) {
            getLogger().severe("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void disconnectFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                getLogger().info("Conexão com o banco de dados encerrada.");
            }
        } catch (Exception e) {
            getLogger().severe("Erro ao encerrar conexão com o banco de dados: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public String getPlayerLanguage(Player player) {
        String locale = player.getLocale(); // Obtém o idioma do jogador como String
        List<String> supportedLanguages = config.getStringList("language.supported"); // Obtém a lista de idiomas do config.yml

        // Se o idioma do jogador estiver na lista de suportados, usa ele. Caso contrário, usa o padrão do config.
        return supportedLanguages.contains(locale) ? locale : config.getString("language.default", "pt-BR");
    }


    private void ensureConnection() {
    try {
        if (connection == null || connection.isClosed()) {
            getLogger().warning("Conexão com o banco de dados perdida, tentando reconectar...");
            connectToDatabase();
        }
    } catch (SQLException e) {
        getLogger().severe("Erro ao verificar a conexão com o banco de dados: " + e.getMessage());
    }
}




@Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    ensureConnection(); // Verifica a conexão antes de processar o comando
        

    
    if (command.getName().equalsIgnoreCase("saldo")) {
    if (!(sender instanceof Player)) {
        sender.sendMessage("Este comando só pode ser usado por jogadores.");
        return true;
    }

    Player player = (Player) sender;
    String lang = store.getPlayerLanguage(player);

    checkBalance(player); // Executa a verificação de saldo

    return true;
}
 else if (command.getName().equalsIgnoreCase("loan")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    double amount = Double.parseDouble(args[0]);
                    giveLoan(player, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("Por favor, insira um valor válido.");
                }
            } else {
                player.sendMessage("Uso correto: /loan <quantidade>");
            }
        } else {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("createWallet")) {
    if (sender instanceof Player) {
        Player player = (Player) sender;
        String lang = store.getPlayerLanguage(player); // Obtém o idioma do jogador
                    if (lang.equals("pt-BR")) {
                        player.sendMessage(Component.text("⚡ Aguarde! ", NamedTextColor.GOLD)
                        .append(Component.text("Pode levar 5 segundos...", NamedTextColor.GREEN))
                        .append(Component.text("\n🌐 Conectando ao banco Solana...", NamedTextColor.AQUA)));
                        } else if (lang.equals("es-ES")) {
                            player.sendMessage(Component.text("⚡ ¡Espere! ", NamedTextColor.GOLD)
                            .append(Component.text("Puede tardar 5 segundos...", NamedTextColor.GREEN))
                            .append(Component.text("\n🌐 Conectando al banco Solana...", NamedTextColor.AQUA)));
                            } else { // Inglês como padrão
                            player.sendMessage(Component.text("⚡ Please wait! ", NamedTextColor.GOLD)
                            .append(Component.text("This may take 5 seconds...", NamedTextColor.GREEN))
                            .append(Component.text("\n🌐 Connecting to Solana bank...", NamedTextColor.AQUA)));
                        }
        solana.createWallet(player);
    } else {
        sender.sendMessage("Este comando so pode ser usado por jogadores.");

    }
    return true;
} else if (command.getName().equalsIgnoreCase("paydebt")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    double amount = Double.parseDouble(args[0]);
                    payDebt(player, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("Por favor, insira um valor válido.");
                }
            } else {
                player.sendMessage("Uso correto: /paydebt <quantidade>");
            }
        } else {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
        }
        return true;
   } else if (command.getName().equalsIgnoreCase("buycurrency")) {
    if (sender instanceof Player) {
        Player player = (Player) sender;
        if (args.length == 1) {
            try {
                String lang = store.getPlayerLanguage(player); // Obtém o idioma do jogador
               if (lang.equals("pt-BR")) {
                        player.sendMessage(Component.text("⚡ Aguarde! ", NamedTextColor.GOLD)
                        .append(Component.text("Pode levar 5 segundos...", NamedTextColor.GREEN))
                        .append(Component.text("\n🌐 Conectando ao banco Solana...", NamedTextColor.AQUA)));
                        } else if (lang.equals("es-ES")) {
                            player.sendMessage(Component.text("⚡ ¡Espere! ", NamedTextColor.GOLD)
                            .append(Component.text("Puede tardar 5 segundos...", NamedTextColor.GREEN))
                            .append(Component.text("\n🌐 Conectando al banco Solana...", NamedTextColor.AQUA)));
                            } else { // Inglês como padrão
                            player.sendMessage(Component.text("⚡ Please wait! ", NamedTextColor.GOLD)
                            .append(Component.text("This may take 5 seconds...", NamedTextColor.GREEN))
                            .append(Component.text("\n🌐 Connecting to Solana bank...", NamedTextColor.AQUA)));
                        }
                double solAmount = Double.parseDouble(args[0]);
                solana.buyGameCurrency(player, solAmount);
            } catch (NumberFormatException e) {
                player.sendMessage("Uso correto: /buycurrency <quantidade_SOL>");
            }
        } else {
            player.sendMessage("Uso correto: /buycurrency <quantidade_SOL>");
        }
    } else {
        sender.sendMessage("Este comando só pode ser usado por jogadores.");
    }
    return true;
} else if (command.getName().equalsIgnoreCase("buyapple")) {
    if (sender instanceof Player) {
        Player player = (Player) sender;
        store.buyEnchantedApple(player);
    }
    return true;
} else if (command.getName().equalsIgnoreCase("refundsolana")) {
            if (args.length < 1) {

                sender.sendMessage(ChatColor.RED + "Uso correto: /refundsolana <signature>");
                return false;
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;


            String lang = store.getPlayerLanguage(player); // Obtém o idioma do jogador
            if (lang.equals("pt-BR")) {
                player.sendMessage(ChatColor.GOLD + "⚡ Aguarde! " +
                        ChatColor.GREEN + "Pode levar 5 segundos..." +
                        ChatColor.AQUA + "\n🌐 Conectando ao banco Solana...");
            } else if (lang.equals("es-ES")) {
                player.sendMessage(ChatColor.GOLD + "⚡ ¡Espere! " +
                        ChatColor.GREEN + "Puede tardar 5 segundos..." +
                        ChatColor.AQUA + "\n🌐 Conectando al banco Solana...");
            } else { // Inglês como padrão
                player.sendMessage(ChatColor.GOLD + "⚡ Please wait! " +
                        ChatColor.GREEN + "This may take 5 seconds..." +
                        ChatColor.AQUA + "\n🌐 Connecting to Solana bank...");
            }

            String transactionSignature = args[0]; // Obtém a assinatura da transação
            solana.refundSolana(player, transactionSignature); // Chama a função de reembolso com a assinatura
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("buyemerald")) {
    if (sender instanceof Player) {
        Player player = (Player) sender;
        store.buyEmerald(player);
    }
    return true;
} else if (command.getName().equalsIgnoreCase("soltransfer")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 2) {
                String recipient = args[0];
                try {
                    String lang = store.getPlayerLanguage(player); // Obtém o idioma do jogador
                    if (lang.equals("pt-BR")) {
                        player.sendMessage(Component.text("⚡ Aguarde! ", NamedTextColor.GOLD)
                        .append(Component.text("Pode levar 5 segundos...", NamedTextColor.GREEN))
                        .append(Component.text("\n🌐 Conectando ao banco Solana...", NamedTextColor.AQUA)));
                        } else if (lang.equals("es-ES")) {
                            player.sendMessage(Component.text("⚡ ¡Espere! ", NamedTextColor.GOLD)
                            .append(Component.text("Puede tardar 5 segundos...", NamedTextColor.GREEN))
                            .append(Component.text("\n🌐 Conectando al banco Solana...", NamedTextColor.AQUA)));
                            } else { // Inglês como padrão
                            player.sendMessage(Component.text("⚡ Please wait! ", NamedTextColor.GOLD)
                            .append(Component.text("This may take 5 seconds...", NamedTextColor.GREEN))
                            .append(Component.text("\n🌐 Connecting to Solana bank...", NamedTextColor.AQUA)));
                        }

                    double amount = Double.parseDouble(args[1]);
                    solana.transferSolana(player, recipient, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("Uso correto: /soltransfer <jogador> <quantidade_SOL>");
                }
            } else {
                player.sendMessage("Uso correto: /soltransfer <jogador> <quantidade_SOL>");
            }
        } else {
            sender.sendMessage("Este comando so pode ser usado por jogadores.");
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("transferirtokengamer")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 2) {
                String recipient = args[0];
                try {
                    String lang = store.getPlayerLanguage(player); // Obtém o idioma do jogador

                    double amount = Double.parseDouble(args[1]);
                    store.transferirtokengamer(player, recipient, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("Uso correto: /transferirtokengamer <jogador> <quantidade_SOL>");
                }
            } else {
                player.sendMessage("Uso correto: /transferirtokengamer <jogador> <quantidade_SOL>");
            }
        } else {
            sender.sendMessage("Este comando so pode ser usado por jogadores.");
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("solbalance")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            solana.handleSolBalance(player);
        } else {
            sender.sendMessage("Este comando so pode ser usado por jogadores.");
        }
        return true;
    
    } else if (command.getName().equalsIgnoreCase("buySpinningWand")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buySpinningWand(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buyiron")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyIronBlock(player);
        }
        return true;
    }
     else if (command.getName().equalsIgnoreCase("buyEmeraldBlock")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyEmeraldBlock(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buygold")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyGoldBlock(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buydiamond")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyDiamondBlock(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buyLapis")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyLapisBlock(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buyQuartz")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyQuartzBlock(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buyClay")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyClayBlock(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buySimpleMap")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buySimpleMap(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buySimpleCompass")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buySimpleCompass(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buySimpleFishingRod")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buySimpleFishingRod(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buyAxolotlBucket")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyAxolotlBucket(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buyRedstone")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyRedstoneBlock(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buySandBlock")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buySandBlock(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buyAllTools")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyAllTools(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buyAllFood")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyAllFood(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buySimpleBook")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buySimpleBook(player);
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("buynetherite")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            store.buyNetheriteBlock(player);
        }
        return true;
    } if (command.getName().equalsIgnoreCase("eco")) {
    if (args.length < 2) {
        sender.sendMessage("❌ Uso incorreto! Formato: /eco [give/take/set/balance] [jogador] [valor]");
        return true;
    }

    String action = args[0].toLowerCase();
    String playerName = args[1];
    
    // Verifica se o comando é para mostrar o saldo
    Player player = Bukkit.getPlayerExact(playerName);
if (player != null) {
    checkBalance(player);
} else {
    sender.sendMessage("❌ Jogador não encontrado!");
}


    // Comandos give/take/set exigem um terceiro argumento
    if (args.length < 3) {
        sender.sendMessage("❌ Uso incorreto! Formato: /eco [give/take/set] [jogador] [valor]");
        return true;
    }

    double amount = Double.parseDouble(args[2]);
    
    sender.sendMessage("🥽 :" + action + " " + playerName + " " + amount);
    return true;
}

 if (!sender.hasPermission("eco.admin")) {
        sender.sendMessage("❌ Você não tem permissão para executar este comando.");
        return true;
    } else if (command.getName().equalsIgnoreCase("ban")) {
        if (args.length < 2) {
            sender.sendMessage("❌ Uso incorreto! Formato: /ban [jogador] [motivo]");
            return true;
        }

        String playerName = args[0];
        String motivo = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

        if (target != null) {
            target.banPlayer(motivo);
            sender.sendMessage("✅ O jogador " + playerName + " foi banido! Motivo: " + motivo);
            Bukkit.getServer().broadcast(Component.text(playerName + " foi banido do servidor! Motivo: " + motivo));
        } else {
            sender.sendMessage("❌ Jogador não encontrado.");
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("unban")) {
        if (args.length < 1) {
            sender.sendMessage("❌ Uso incorreto! Formato: /unban [jogador]");
            return true;
        }

        String playerName = args[0];
        Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);
        sender.sendMessage("✅ O jogador " + playerName + " foi desbanido!");
        return true;
    } else if (command.getName().equalsIgnoreCase("back")) {
    CompletableFuture.runAsync(() -> {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage("❌ Apenas jogadores podem usar este comando.");
                return;
            }

            Player player = (Player) sender;
            
            // Debug para verificar se o jogador está registrado
            getLogger().info("🔍 Comando /back executado por: " + player.getName());

            if (!lastLocations.containsKey(player) || lastLocations.get(player) == null) {
                player.sendMessage("❌ Nenhuma posição anterior encontrada.");
                getLogger().warning("⚠️ Tentativa de /back sem localização armazenada para " + player.getName());
                return;
            }

            Location backLocation = lastLocations.remove(player);

            // Debug para verificar a localização antes do teleporte
            getLogger().info("🚀 Teleportando " + player.getName() + " para última posição: " +
                    "X: " + backLocation.getX() + ", Y: " + backLocation.getY() + ", Z: " + backLocation.getZ());

            player.teleportAsync(backLocation);
            player.sendMessage("🚀 Você voltou para sua última posição!");

        } catch (Exception e) {
            getLogger().severe("❌ Erro ao executar o comando /back: " + e.getMessage());
            e.printStackTrace();
        }
    });
    return true;
} else if (command.getName().equalsIgnoreCase("tpa")) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("❌ Apenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("❌ Uso incorreto! Formato: /tpa [jogador]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("❌ Jogador não encontrado ou offline.");
            return true;
        }

        tpaRequests.put(target, player);
        player.sendMessage("✉️ Pedido de teleporte enviado para " + target.getName() + ".");
        target.sendMessage("📩 " + player.getName() + " deseja se teleportar até você! Use `/tpaccept` para aceitar ou `/tpdeny` para negar.");

        return true;
    } else if (command.getName().equalsIgnoreCase("tpaccept")) {
        if (!(sender instanceof Player)) return true;
        Player target = (Player) sender;

        if (!tpaRequests.containsKey(target)) {
            target.sendMessage("❌ Nenhum pedido de teleporte pendente.");
            return true;
        }

        Player requester = tpaRequests.remove(target);
        requester.teleportAsync(target.getLocation());
        requester.sendMessage("✅ Teleporte aceito! Você foi movido até " + target.getName() + ".");
        target.sendMessage("✅ Teleporte realizado com sucesso.");

        return true;
    } else if (command.getName().equalsIgnoreCase("tpdeny")) {
        if (!(sender instanceof Player)) return true;
        Player target = (Player) sender;

        if (!tpaRequests.containsKey(target)) {
            target.sendMessage("❌ Nenhum pedido de teleporte pendente.");
            return true;
        }

        Player requester = tpaRequests.remove(target);
        requester.sendMessage("❌ Pedido de teleporte recusado por " + target.getName() + ".");
        target.sendMessage("❌ Você recusou o pedido de teleporte.");

        return true;
    } // 🔹 Correção do comando /sethome
    else if (command.getName().equalsIgnoreCase("sethome")) {
    if (!(sender instanceof Player)) {
        sender.sendMessage("❌ Este comando só pode ser usado por um jogador!");
        return true;
    }

    Player jogador = (Player) sender; // Faz o cast para Player
    
    if (args.length == 0) {
        jogador.sendMessage("❌ Use `/sethome <nome>` para definir uma casa!");
        return true;
    }

    String nomeCasa = args[0]; // Obtém o nome da casa do primeiro argumento
    Location local = jogador.getLocation(); // Obtém a localização atual do jogador

    // Registra a casa com o nome fornecido
    registrarCasa(jogador, nomeCasa, local);

    jogador.sendMessage("🏡 Casa '" + nomeCasa + "' foi definida! Use `/home " + nomeCasa + "` para voltar.");
    return true;
}


else if (command.getName().equalsIgnoreCase("home")) {
    if (!(sender instanceof Player)) {
        sender.sendMessage("❌ Este comando só pode ser usado por um jogador!");
        return true;
    }

    CompletableFuture.runAsync(() -> {
        try {
            Player player = (Player) sender;
            String nomeCasa = (args.length > 0) ? args[0] : "default"; // Se não informar, usa 'default'

            // Verifica no MySQL se a casa existe
            try (Connection conn = getDatabaseConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT world, x, y, z FROM homes WHERE player_uuid = ? AND home_name = ?")) {
                
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setString(2, nomeCasa);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Casa encontrada, adiciona ao mapa `casas`
                    World mundo = Bukkit.getWorld(rs.getString("world"));
                    Location homeLocation = new Location(mundo, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
                    
                    // Atualiza no mapa
                    casas.computeIfAbsent(player, k -> new HashMap<>()).put(nomeCasa, homeLocation);

                    getLogger().info("🚀 Teleportando " + player.getName() + " para sua casa '" + nomeCasa + "': " +
                            "X: " + homeLocation.getX() + ", Y: " + homeLocation.getY() + ", Z: " + homeLocation.getZ());

                    player.teleportAsync(homeLocation)
                            .thenRun(() -> player.sendMessage("🏡 Bem-vindo à sua casa '" + nomeCasa + "'!"))
                            .exceptionally(e -> {
                                getLogger().severe("❌ Erro ao teleportar " + player.getName() + ": " + e.getMessage());
                                e.printStackTrace();
                                return null;
                            });

                } else {
                    player.sendMessage("❌ Casa '" + nomeCasa + "' não encontrada!");
                }

            } catch (SQLException e) {
                getLogger().severe("❌ Erro ao consultar casa no MySQL: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            getLogger().severe("❌ Exceção ao executar /home: " + e.getMessage());
            e.printStackTrace();
        }
    });

    return true;
}

else if (command.getName().equalsIgnoreCase("homereset")) {
    if (!(sender instanceof Player) || !sender.hasPermission("home.admin")) { // Apenas admins podem executar
        sender.sendMessage("❌ Você não tem permissão para resetar as casas!");
        return true;
    }

    CompletableFuture.runAsync(() -> {
        try (Connection conn = getDatabaseConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("TRUNCATE TABLE homes"); // Remove todos os registros da tabela
            casas.clear(); // Limpa o cache de casas armazenado no plugin

            sender.sendMessage(ChatColor.RED + "⚠️ TODAS as casas foram resetadas pelo administrador!");
            getLogger().warning("⚠️ O administrador " + sender.getName() + " resetou todas as casas!");

        } catch (SQLException e) {
            sender.sendMessage(ChatColor.RED + "❌ Erro ao resetar as casas!");
            getLogger().severe("❌ Erro ao executar TRUNCATE TABLE homes: " + e.getMessage());
            e.printStackTrace();
        }
    });

    return true;
}

    else if (command.getName().equalsIgnoreCase("lockchest")) {
    if (!(sender instanceof Player)) {
        sender.sendMessage(ChatColor.RED + "❌ Este comando só pode ser usado por um jogador!");
        return true;
    }

    Player p = (Player) sender;
    getLogger().info("🔍 Comando /lockchest executado por: " + p.getName());

    if (args.length < 1) {
        sender.sendMessage(ChatColor.RED + "❌ Uso incorreto! Formato: /lockchest [senha]");
        return true;
    }

    Block block;
    try {
        block = p.getTargetBlockExact(5);
        if (block == null || !(block.getState() instanceof Chest)) {
            sender.sendMessage(ChatColor.RED + "❌ Você precisa olhar para um baú!");
            return true;
        }
    } catch (Exception e) {
        getLogger().severe("❌ Erro ao identificar o bloco: " + e.getMessage());
        e.printStackTrace();
        sender.sendMessage(ChatColor.RED + "❌ Ocorreu um erro ao verificar o baú.");
        return true;
    }

    String password = args[0];
    Location chestLocation = block.getLocation();

    // 🛠 Execução assíncrona para evitar travamento do servidor
    CompletableFuture.runAsync(() -> {
        try (Connection conn = getDatabaseConnection()) {
            if (conn == null) {
                getLogger().severe("❌ Erro: Conexão com banco de dados não encontrada!");
                sender.sendMessage(ChatColor.RED + "❌ Erro interno! Não foi possível conectar ao banco de dados.");
                return;
            }

            // 🔍 Verificar se o baú já está trancado
            try (PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM locked_chests WHERE world = ? AND x = ? AND y = ? AND z = ?"
            )) {
                checkStmt.setString(1, chestLocation.getWorld().getName());
                checkStmt.setDouble(2, chestLocation.getX());
                checkStmt.setDouble(3, chestLocation.getY());
                checkStmt.setDouble(4, chestLocation.getZ());

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    sender.sendMessage(ChatColor.RED + "❌ Este baú já está trancado!");
                    getLogger().warning("⚠️ Tentativa de trancar um baú já trancado: " + chestLocation);
                    return;
                }
            }

            // 🔒 Inserção no banco de dados com prevenção de duplicação
            try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO locked_chests (world, x, y, z, password) VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE password = VALUES(password)"
            )) {
                stmt.setString(1, chestLocation.getWorld().getName());
                stmt.setDouble(2, chestLocation.getX());
                stmt.setDouble(3, chestLocation.getY());
                stmt.setDouble(4, chestLocation.getZ());
                stmt.setString(5, password);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    lockedChests.put(chestLocation, password);
                    getLogger().info("🔒 Baú trancado no banco e memória: " + chestLocation);
                    sender.sendMessage(ChatColor.GREEN + "🔒 Baú trancado com sucesso!");
                } else {
                    sender.sendMessage(ChatColor.RED + "❌ Falha ao trancar o baú!");
                    getLogger().warning("⚠️ Nenhuma linha foi inserida ao trancar o baú.");
                }
            }
        } catch (SQLException e) {
            getLogger().severe("❌ Erro ao salvar baú no banco: " + e.getMessage());
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "❌ Erro ao registrar a tranca do baú!");
        }
    });

    return true;
}

    // 🔹 Correção do comando /unlockchest
    else if (command.getName().equalsIgnoreCase("unlockchest")) {
    if (!(sender instanceof Player)) {
        sender.sendMessage("❌ Este comando só pode ser usado por um jogador!");
        return true;
    }

    Player p = (Player) sender;
    Block block = p.getTargetBlockExact(5);
    if (block == null || !(block.getState() instanceof Chest)) {
        sender.sendMessage("❌ Você precisa olhar para um baú!");
        return true;
    }

    Location chestLocation = block.getLocation();

    if (args.length < 1) {
        sender.sendMessage("❌ Uso incorreto! Formato: /unlockchest [senha]");
        return true;
    }

    String enteredPassword = args[0];

    // 🔄 Verificar senha antes de destrancar
    CompletableFuture.runAsync(() -> {
        try (Connection conn = getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT password FROM locked_chests WHERE world = ? AND x = ? AND y = ? AND z = ?"
             )) {

            stmt.setString(1, chestLocation.getWorld().getName());
            stmt.setDouble(2, chestLocation.getX());
            stmt.setDouble(3, chestLocation.getY());
            stmt.setDouble(4, chestLocation.getZ());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String correctPassword = rs.getString("password");

                if (enteredPassword.equals(correctPassword)) {
                    try (PreparedStatement deleteStmt = conn.prepareStatement(
                        "DELETE FROM locked_chests WHERE world = ? AND x = ? AND y = ? AND z = ?"
                    )) {
                        deleteStmt.setString(1, chestLocation.getWorld().getName());
                        deleteStmt.setDouble(2, chestLocation.getX());
                        deleteStmt.setDouble(3, chestLocation.getY());
                        deleteStmt.setDouble(4, chestLocation.getZ());
                        deleteStmt.executeUpdate();
                    }

                    lockedChests.remove(chestLocation);
                    p.sendMessage(ChatColor.GREEN + "✅ Baú destrancado com sucesso!");
                    getLogger().info("🔓 Baú destrancado em " + chestLocation + " por " + p.getName());

                } else {
                    p.sendMessage(ChatColor.RED + "❌ Senha incorreta! Tente novamente.");
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    getLogger().warning("⚠️ Tentativa de destrancar baú com senha incorreta por: " + p.getName());
                }
            } else {
                p.sendMessage(ChatColor.RED + "❌ Este baú não está trancado.");
            }

        } catch (SQLException e) {
            getLogger().severe("❌ Erro ao verificar senha do baú: " + e.getMessage());
            e.printStackTrace();
        }
    });

    return true;
}
   



 else if (command.getName().equalsIgnoreCase("unban-ip")) {
        if (args.length < 1) {
            sender.sendMessage("❌ Uso incorreto! Formato: /unban-ip [endereço IP]");
            return true;
        }

        String ipAddress = args[0];

        // 🔹 Desbanir o IP do sistema de bans do Bukkit
        Bukkit.getBanList(BanList.Type.IP).pardon(ipAddress);

        sender.sendMessage("✅ O IP " + ipAddress + " foi desbanido com sucesso!");
        return true;
    } else if (command.getName().equalsIgnoreCase("list-bans")) {
        Set<BanEntry> bannedPlayers = Bukkit.getBanList(BanList.Type.NAME).getBanEntries();
        Set<BanEntry> bannedIps = Bukkit.getBanList(BanList.Type.IP).getBanEntries();

        if (bannedPlayers.isEmpty() && bannedIps.isEmpty()) {
            sender.sendMessage("✅ Nenhum jogador ou IP banido no momento.");
            return true;
        }

        sender.sendMessage("🚨 Lista de jogadores banidos:");
        for (BanEntry ban : bannedPlayers) {
            sender.sendMessage("🔴 " + ban.getTarget() + " | Motivo: " + ban.getReason());
        }

        sender.sendMessage("🚨 Lista de IPs banidos:");
        for (BanEntry ban : bannedIps) {
            sender.sendMessage("🔴 " + ban.getTarget() + " | Motivo: " + ban.getReason());
        }

        return true;
    } else if (command.getName().equalsIgnoreCase("invest")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    double amount = Double.parseDouble(args[0]);
                    invest(player, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("Por favor, insira um valor valido.");
                }
            } else {
                player.sendMessage("Uso correto: /invest <quantidade>");
            }
        } else {
            sender.sendMessage("Este comando so pode ser usado por jogadores.");
        }
        return true;
    }
    return false;
}

private void giveLoan(Player player, double amount) {
    try {
        PreparedStatement statement = connection.prepareStatement(
            "UPDATE banco SET divida = divida + ?, saldo = saldo + ? WHERE jogador = ?"
        );
        statement.setDouble(1, amount * 1.1); // Adiciona juros de 10%
        statement.setDouble(2, amount);
        statement.setString(3, player.getName());
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            player.sendMessage("Emprestimo aprovado! com banco nova divida: $" + (amount * 1.1));
        } else {
            player.sendMessage("Voce ainda nao esta registrado no banco.");
        }
    } catch (Exception e) {
        player.sendMessage("Erro ao processar emprestimo.");
        getLogger().severe("Erro ao processar emprestimo: " + e.getMessage());
    }
}

private Location getLocationFromString(String locString) {
    try {
        if (locString == null || !locString.startsWith("Location{") || !locString.endsWith("}")) {
            return null; // Retorna null se a string não estiver no formato esperado
        }

        String[] data = locString.replace("Location{", "").replace("}", "").split(",");
        if (data.length < 6) {
            return null; // Retorna null se a string não tiver todas as informações necessárias
        }

        World world = Bukkit.getWorld(data[0].split("=")[1]);
        if (world == null) {
            return null; // Retorna null se o mundo não existir
        }

        double x = Double.parseDouble(data[1].split("=")[1]);
        double y = Double.parseDouble(data[2].split("=")[1]);
        double z = Double.parseDouble(data[3].split("=")[1]);
        float pitch = Float.parseFloat(data[4].split("=")[1]);
        float yaw = Float.parseFloat(data[5].split("=")[1]);

        return new Location(world, x, y, z, yaw, pitch);

    } catch (Exception e) {
        getLogger().severe("❌ Erro ao converter string para localização: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

public void carregarCasa(Player jogador, String nome) {
    try (Connection conn = getDatabaseConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT world, x, y, z FROM homes WHERE player_uuid = ? AND home_name = ?")) {
        
        stmt.setString(1, jogador.getUniqueId().toString());
        stmt.setString(2, nome);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            World mundo = Bukkit.getWorld(rs.getString("world"));
            Location cama = new Location(mundo, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));

            // Armazena no mapa diretamente
            homes.put(jogador, cama);

            jogador.sendMessage(ChatColor.GREEN + "🏡 Sua casa '" + nome + "' foi carregada com sucesso!");
        } else {
            jogador.sendMessage(ChatColor.RED + "❌ Casa '" + nome + "' não encontrada!");
        }
    } catch (SQLException e) {
        jogador.sendMessage(ChatColor.RED + "❌ Erro ao carregar a casa do banco!");
        e.printStackTrace();
    }
}

public void registrarCasa(Player jogador, String nome, Location local) {
    try (Connection conn = getDatabaseConnection();
         PreparedStatement stmt = conn.prepareStatement(
             "INSERT INTO homes (player_uuid, home_name, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?) " +
             "ON DUPLICATE KEY UPDATE world = VALUES(world), x = VALUES(x), y = VALUES(y), z = VALUES(z)"
         )) {

        stmt.setString(1, jogador.getUniqueId().toString());
        stmt.setString(2, nome);
        stmt.setString(3, local.getWorld().getName());
        stmt.setDouble(4, local.getX());
        stmt.setDouble(5, local.getY());
        stmt.setDouble(6, local.getZ());

        stmt.executeUpdate();

        // Atualiza no mapa de casas, garantindo múltiplas casas por jogador
        casas.computeIfAbsent(jogador, k -> new HashMap<>()).put(nome, local);

        jogador.sendMessage(ChatColor.GREEN + "🏡 Casa '" + nome + "' foi registrada ou atualizada com sucesso!");
    } catch (SQLException e) {
        jogador.sendMessage(ChatColor.RED + "❌ Erro ao registrar ou atualizar a casa no banco!");
        e.printStackTrace();
    }
}


private void payDebt(Player player, double amount) {
    try {
        PreparedStatement statement = connection.prepareStatement(
            "UPDATE banco SET divida = GREATEST(divida - ?, 0), saldo = saldo - ? WHERE jogador = ?"
        );
        statement.setDouble(1, amount);
        statement.setDouble(2, amount);
        statement.setString(3, player.getName());
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            // Agora, define o saldo igual ao do banco
            ajustarSaldo(player, "take", amount);
            player.sendMessage("Pagamento de $" + amount + " realizado com sucesso.");
        } else {
            player.sendMessage("Você ainda não está registrado no banco.");
        }
    } catch (Exception e) {
        player.sendMessage("Erro ao processar pagamento da dívida.");
        getLogger().severe("Erro ao processar pagamento da dívida: " + e.getMessage());
    }
}

private void invest(Player player, double amount) {
    try {
        PreparedStatement statement = connection.prepareStatement(
            "UPDATE banco SET investimento = investimento + ?, saldo = saldo - ? WHERE jogador = ?"
        );
        statement.setDouble(1, amount);
        statement.setDouble(2, amount);
        statement.setString(3, player.getName());
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            // Agora, define o saldo igual ao do banco
            ajustarSaldo(player, "take", amount);
            player.sendMessage("Investimento de $" + amount + " realizado com sucesso.");
        } else {
            player.sendMessage("Você ainda não está registrado no banco.");
        }
    } catch (Exception e) {
        player.sendMessage("Erro ao processar investimento.");
        getLogger().severe("Erro ao processar investimento: " + e.getMessage());
    }
}

private void updateDebts() {
    try {
        PreparedStatement statement = connection.prepareStatement(
            "UPDATE banco SET divida = divida * 1.02 WHERE divida > 0"
        );
        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            getLogger().info("Juros aplicados às dívidas.");
        }
    } catch (Exception e) {
        getLogger().severe("Erro ao atualizar dívidas: " + e.getMessage());
    }
}

private void processInvestments(Player player, String lang) {
    try {
        // Obtendo saldo antes do update
        PreparedStatement getSaldoStmt = connection.prepareStatement("SELECT saldo, investimento FROM banco WHERE investimento > 0");
        ResultSet resultSet = getSaldoStmt.executeQuery();

        if (resultSet.next()) {
            double saldoAtual = resultSet.getDouble("saldo");
            double investimento = resultSet.getDouble("investimento");
            double saldoAtualizado = saldoAtual + (investimento * 1.25);

            // Agora, define o saldo igual ao do banco
            ajustarSaldo(player, "set", saldoAtualizado);

            // Atualizando saldo e zerando investimento
            PreparedStatement updateStmt = connection.prepareStatement(
                "UPDATE banco SET saldo = saldo + investimento * 1.25, investimento = 0 WHERE investimento > 0"
            );
            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                getLogger().info("Retorno de investimentos processado.");

                Component message = Component.text("💰 Retorno de investimentos processado! \nNovo saldo: $ ")
                        .color(TextColor.color(0x00FFFF)) // Azul Claro
                        .append(Component.text(String.format("%.2f", saldoAtualizado)).color(TextColor.color(0xFFFF00))); // Formatação decimal

                player.sendMessage(message);
            }
        }
    } catch (Exception e) {
        getLogger().severe("Erro ao processar investimentos: " + e.getMessage());
    }
}

    private void checkBalance(Player player) {
    getLogger().info("🔄 Iniciando verificação de saldo para " + player.getName());

    CompletableFuture.runAsync(() -> {
        try {
            // 🔍 Buscar saldo na Solana
            double balance = solana.getSolBalance(player.getName());
            getLogger().info("✅ Saldo obtido: " + balance + " SOL");
            player.sendMessage(
    Component.text("💰 ").color(TextColor.color(0xFFFF00)) // Ícone de dinheiro (Amarelo)
    .append(Component.text(balance + " ").color(TextColor.color(0xFFFF00))) // Saldo (Roxo)
    .append(Component.text("PAN").color(TextColor.color(0xFFFFFF))) // Branco
    .append(Component.text("DA").color(TextColor.color(0x800080))) // Verde
    .append(Component.text("COIN").color(TextColor.color(0x00FF00))) // Vermelho
);
player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
 player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK,
                            player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.05);


            // ✅ Exibir saldo com ícones e efeitos
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    getLogger().info("📢 Enviando mensagem de saldo para " + player.getName());
                    
                    String lang = store.getPlayerLanguage(player);
                    Component message;

                    if (lang.equals("pt-BR")) {
                        message = Component.text("💰 Seu saldo atual de SOL é: ")
                                .color(TextColor.color(0x800080))
                                .append(Component.text(" " + String.format("%.4f SOL", balance))
                                .color(TextColor.color(0xFFFF00)));
                    } else if (lang.equals("es-ES")) {
                        message = Component.text("💰 Tu saldo actual de SOL es: ")
                                .color(TextColor.color(0x800080))
                                .append(Component.text(" " + String.format("%.4f SOL", balance))
                                .color(TextColor.color(0xFFFF00)));
                    } else {
                        message = Component.text("💰 Your current SOL balance is: ")
                                .color(TextColor.color(0x800080))
                                .append(Component.text(" " + String.format("%.4f SOL", balance))
                                .color(TextColor.color(0xFFFF00)));
                    }
                    player.sendMessage(message);

                    // 🎵 Toca um som de dinheiro
                    
                    getLogger().info("🎵 Som de dinheiro tocado para " + player.getName());

                    // ✨ Cria um efeito de partículas douradas
                    player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, 
                            player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.05);
                    getLogger().info("✨ Efeito de partículas criado para " + player.getName());
                }
            }, 0L);

        } catch (Exception e) {
            getLogger().info("❌ Erro ao verificar saldo para " + player.getName() + ": " + e.getMessage());

            // ❌ Mensagem de erro com ícone e cor
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    String lang = store.getPlayerLanguage(player);
                    Component errorMessage;

                    if (lang.equals("pt-BR")) {
                        errorMessage = Component.text("❌ Ocorreu um erro ao verificar seu saldo. Tente novamente mais tarde.")
                                .color(TextColor.color(0xFF0000));
                    } else if (lang.equals("es-ES")) {
                        errorMessage = Component.text("❌ Ocurrió un error al verificar tu saldo. Inténtalo de nuevo más tarde.")
                                .color(TextColor.color(0xFF0000));
                    } else {
                        errorMessage = Component.text("❌ An error occurred while checking your balance. Please try again later.")
                                .color(TextColor.color(0xFF0000));
                    }
                    player.sendMessage(errorMessage);
                }
            }, 0L);
            e.printStackTrace();
        }
    });

    getLogger().info("✅ Verificação de saldo concluída para " + player.getName());
}


public void ajustarSaldo(Player player, String tipo, double valor) {
    System.out.println("DEBUG (ajustarSaldo): Iniciado para " + player.getName() + ", tipo: " + tipo + ", quantia: " + valor);

    // *** NOVA LINHA DE DEBUG: Verificar se 'plugin' é nulo ***
    if (this.plugin == null) {
        System.err.println("ERROR (ajustarSaldo): Instância do plugin é NULA! Não é possível agendar a tarefa.");
        
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


    
    private void createDatabaseAndTables() {
        try (Statement statement = connection.createStatement()) {
            // 🔍 Criar o banco se ele não existir
            statement.execute("CREATE DATABASE IF NOT EXISTS banco;");
            statement.execute("USE banco;");

            // 🔹 Criar tabelas automaticamente
            statement.execute("CREATE TABLE IF NOT EXISTS banco ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "jogador VARCHAR(50) UNIQUE, "
                + "saldo DECIMAL(10,2) DEFAULT 500, "
                + "divida DECIMAL(10,2) DEFAULT 0, "
                + "investimento DECIMAL(10,2) DEFAULT 0"
                + ");");

            statement.execute("CREATE TABLE IF NOT EXISTS jogadores ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "nome VARCHAR(50) UNIQUE NOT NULL"
                + ");");

            statement.execute("CREATE TABLE IF NOT EXISTS carteiras ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "jogador_id INT NOT NULL, "
                + "endereco VARCHAR(100) UNIQUE NOT NULL, "
                + "chave_privada TEXT NOT NULL, "
                + "frase_secreta TEXT NOT NULL, "
                + "FOREIGN KEY (jogador_id) REFERENCES jogadores(id) ON DELETE CASCADE"
                + ");");

            statement.execute("CREATE TABLE IF NOT EXISTS livro_caixa ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "jogador VARCHAR(255) NOT NULL, "
                + "tipo_transacao VARCHAR(255) NOT NULL, "
                + "valor FLOAT NOT NULL, "
                + "moeda VARCHAR(10) NOT NULL, "
                + "assinatura VARCHAR(255) NOT NULL, "
                + "data_hora DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ");");

            statement.execute("CREATE TABLE IF NOT EXISTS locked_chests ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "x DOUBLE NOT NULL, "
                + "y DOUBLE NOT NULL, "
                + "z DOUBLE NOT NULL, "
                + "world VARCHAR(64) NOT NULL, "
                + "password VARCHAR(255) NOT NULL, "
                + "UNIQUE(world, x, y, z)" // 🔄 Evita registros duplicados de baús na mesma localização
                + ");");

            statement.execute("CREATE TABLE IF NOT EXISTS homes ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "player_uuid VARCHAR(36) NOT NULL, "
                + "home_name VARCHAR(50) NOT NULL, "
                + "world VARCHAR(64) NOT NULL, "
                + "x DOUBLE NOT NULL, "
                + "y DOUBLE NOT NULL, "
                + "z DOUBLE NOT NULL, "
                + "UNIQUE(player_uuid, home_name)" // Garante que cada jogador pode ter várias casas, mas impede nomes duplicados
                + ");");


            getLogger().info("✅ Banco de dados e tabelas criados/verificados!");

        } catch (SQLException e) {
            getLogger().severe("Erro ao criar banco de dados/tabelas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    

}