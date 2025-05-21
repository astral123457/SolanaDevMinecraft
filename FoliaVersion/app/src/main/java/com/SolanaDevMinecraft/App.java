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



public class App extends JavaPlugin implements Listener {

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
    public void aoEntrarNoServidor(PlayerJoinEvent event) {
        Player jogador = event.getPlayer();
        checkBalance(jogador);
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
        if (args.length < 3) {
            System.out.println("❌ Uso incorreto! Formato: /eco [give/take/set] [jogador] [valor]");
            return true;
        }

        String action = args[0].toLowerCase();
        String playerName = args[1];
        double amount;

        
            System.out.println("🥽 :" + action + " " + playerName + " " + args[2]);
       
            return true;
       
    } if (!sender.hasPermission("eco.admin")) {
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
    System.out.println("🔄 Iniciando verificação de saldo para " + player.getName());

    CompletableFuture.runAsync(() -> {
        try {
            // 🔍 Buscar saldo na Solana
            double balance = solana.getSolBalance(player.getName());
            System.out.println("✅ Saldo obtido: " + balance + " SOL");
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
                    System.out.println("📢 Enviando mensagem de saldo para " + player.getName());
                    
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
                    
                    System.out.println("🎵 Som de dinheiro tocado para " + player.getName());

                    // ✨ Cria um efeito de partículas douradas
                    player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, 
                            player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.05);
                    System.out.println("✨ Efeito de partículas criado para " + player.getName());
                }
            }, 0L);

        } catch (Exception e) {
            System.out.println("❌ Erro ao verificar saldo para " + player.getName() + ": " + e.getMessage());

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

    System.out.println("✅ Verificação de saldo concluída para " + player.getName());
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

            getLogger().info("✅ Banco de dados e tabelas criados/verificados!");

        } catch (SQLException e) {
            getLogger().severe("Erro ao criar banco de dados/tabelas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    

}