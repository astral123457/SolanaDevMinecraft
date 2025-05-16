package solanadevminecraft.solanadevminecraftastral.solanadevminecraft;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import org.bukkit.Bukkit;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;




public class SolanaDevMinecraft extends JavaPlugin implements Listener {

    private Connection connection;
    private Solana solana;
    private Store store; // Instância da classe Store
    private FileConfiguration config; // Armazena o config.yml





    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        // Salva o config.yml na pasta do plugin, caso ainda não exista
        saveDefaultConfig();
        config = getConfig(); // Inicializa config.yml corretamente
        getLogger().info("Plugin habilitado!");

        connectToDatabase();

        solana = new Solana(config, connection); // Passa config.yml e conexão para Solana
        store = new Store(config, connection); // Passa config.yml e conexão para Store

        // 🔹 Cria banco e tabelas automaticamente
        createDatabaseAndTables();

        // Atualiza juros a cada 60 segundos
        new BukkitRunnable() {
            @Override
            public void run() {
                updateDebts();
            }
        }.runTaskTimer(this, 0L, 1200L); // 1200 ticks = 60 segundos

        // Retorno de investimentos a cada 5 minutos
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) { // Itera sobre os jogadores conectados
                    processInvestments(player, player.getLocale()); // Usa o idioma do jogador (Spigot 1.16+)
                }
            }
        }.runTaskTimer(this, 0L, 6000L); // 6000 ticks = 5 minutos
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
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String lang = store.getPlayerLanguage(player); // Obtém o idioma do jogador
                checkBalance(player);
            } else {
                sender.sendMessage("Este comando só pode ser usado por jogadores.");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("loan")) {
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
            PreparedStatement getSaldoStmt = connection.prepareStatement(
                    "SELECT saldo, investimento FROM banco WHERE investimento > 0"
            );
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

                    String message = ChatColor.AQUA + "💰 Retorno de investimentos processado! \n" +
                            ChatColor.YELLOW + "Novo saldo: $" + String.format("%.2f", saldoAtualizado);

                    player.sendMessage(message);
                }
            }
        } catch (Exception e) {
            getLogger().severe("Erro ao processar investimentos: " + e.getMessage());
            player.sendMessage(ChatColor.RED + "❌ Erro ao processar investimentos.");
        }
    }

    private void checkBalance(Player player) {
        try {
            // 🔍 Verifica o saldo do jogador na tabela 'banco'
            PreparedStatement checkStatement = connection.prepareStatement(
                    "SELECT saldo FROM banco WHERE jogador = ?"
            );
            checkStatement.setString(1, player.getName());
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                double saldoBanco = resultSet.getDouble("saldo");

                // Atualiza o saldo no banco de dados
                PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE banco SET saldo = ? WHERE jogador = ?"
                );
                updateStatement.setDouble(1, saldoBanco);
                updateStatement.setString(2, player.getName());
                updateStatement.executeUpdate();

                // Define o saldo igual ao do banco
                ajustarSaldo(player, "set", saldoBanco);

                // Exibe mensagem personalizada com o saldo atualizado
                String lang = store.getPlayerLanguage(player);
                if (lang.equals("pt-BR")) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "💰 Seu saldo bancário foi atualizado: \n"
                            + ChatColor.YELLOW + "$" + saldoBanco);
                } else if (lang.equals("es-ES")) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "💰 Su saldo bancario ha sido actualizado: \n"
                            + ChatColor.YELLOW + "$" + saldoBanco);
                } else {
                    player.sendMessage(ChatColor.DARK_PURPLE + "💰 Your bank balance has been updated: \n"
                            + ChatColor.YELLOW + "$" + saldoBanco);
                }
            } else {
                // Se o jogador não estiver registrado, adicionamos ele com saldo inicial de 500 moedas
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO banco (jogador, saldo) VALUES (?, 500)"
                );
                insertStatement.setString(1, player.getName());
                insertStatement.executeUpdate();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " 500");

                String lang = store.getPlayerLanguage(player);
                if (lang.equals("pt-BR")) {
                    player.sendMessage(ChatColor.GREEN + "✅ Você foi cadastrado no banco! Seu saldo inicial é de 500 moedas.");
                } else if (lang.equals("es-ES")) {
                    player.sendMessage(ChatColor.GREEN + "✅ ¡Te has registrado en el banco! Tu saldo inicial es de 500 monedas.");
                } else {
                    player.sendMessage(ChatColor.GREEN + "✅ You have been registered in the bank! Your initial balance is 500 coins.");
                }
            }

        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "❌ Erro ao acessar o banco de dados.");
            getLogger().severe("Erro ao consultar saldo: " + e.getMessage());
        }
    }


    public void ajustarSaldo(Player player, String tipo, double valor) {
        if (tipo.equalsIgnoreCase("give")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " " + valor);
        } else if (tipo.equalsIgnoreCase("take")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco take " + player.getName() + " " + valor);
        }  else if (tipo.equalsIgnoreCase("set")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco set " + player.getName() + " " + valor);
        } else {
            player.sendMessage("Comando inválido! Use 'give' ou 'take' ou set.");
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