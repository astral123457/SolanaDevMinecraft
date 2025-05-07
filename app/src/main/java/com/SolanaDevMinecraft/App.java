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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;


public class App extends JavaPlugin {

    private Connection connection;
    private Solana solana;
    private Store store; // Adiciona a instância da classe Store


    @Override
    public void onEnable() {
        // Salva o config.yml na pasta do plugin, caso ainda não exista
    saveDefaultConfig();
    getLogger().info("Plugin habilitado!");
    connectToDatabase();
    solana = new Solana(getConfig(), connection); // Inicializa a instância da classe Solana // Inicializa a instância da classe Solana
    store = new Store(connection); // Inicializa a instância da classe Store

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
            processInvestments();
        }
    }.runTaskTimer(this, 0L, 6000L); // 6000 ticks = 5 minutos
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin desabilitado!");
        disconnectFromDatabase();
    }

    private void connectToDatabase() {
    try {
        String url = getConfig().getString("database.url");
        String user = getConfig().getString("database.user");
        String password = getConfig().getString("database.password");

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

    @Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("saldo")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            checkBalance(player);
        } else {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("testdb")) {
    if (sender instanceof Player) {
        Player player = (Player) sender;

        // Obtém as configurações do banco de dados
        String url = getConfig().getString("database.url");
        String user = getConfig().getString("database.user");
        String password = getConfig().getString("database.password");

        // Exibe as configurações para o jogador
        player.sendMessage("Url: " + url);
        player.sendMessage("User: " + user);
        player.sendMessage("Password: " + password);

        try {
            // Testa a conexão com o banco de dados
            if (connection == null || connection.isClosed()) {
                player.sendMessage("Erro: Conexão com o banco de dados não foi estabelecida.");
                return true;
            }

            PreparedStatement statement = connection.prepareStatement("SELECT 1");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                player.sendMessage("Conexão com o banco de dados está funcionando!");
            } else {
                player.sendMessage("Erro ao testar a conexão com o banco de dados.");
            }
        } catch (Exception e) {
            player.sendMessage("Erro ao acessar o banco de dados: " + e.getMessage());
            getLogger().severe("Erro ao testar conexão com o banco de dados: " + e.getMessage());
        }
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
    } else if (command.getName().equalsIgnoreCase("createwallet")) {
    if (sender instanceof Player) {
        Player player = (Player) sender;
        solana.createWallet(player);
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
} else if (command.getName().equalsIgnoreCase("buynetheritepickaxe")) {
    if (sender instanceof Player) {
        Player player = (Player) sender;
        store.buyNetheritePickaxe(player);
    }
    return true;
} else if (command.getName().equalsIgnoreCase("buydiamondarmor")) {
    if (sender instanceof Player) {
        Player player = (Player) sender;
        store.buyEnchantedDiamondArmor(player);
    }
    return true;
} else if (command.getName().equalsIgnoreCase("buynetheritearmor")) {
    if (sender instanceof Player) {
        Player player = (Player) sender;
        store.buyEnchantedNetheriteArmor(player);
    }
    return true;
} else if (command.getName().equalsIgnoreCase("soltransfer")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 2) {
                String recipient = args[0];
                try {
                    double amount = Double.parseDouble(args[1]);
                    solana.handleSolTransfer(player, recipient, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("Uso correto: /soltransfer <jogador> <quantidade_SOL>");
                }
            } else {
                player.sendMessage("Uso correto: /soltransfer <jogador> <quantidade_SOL>");
            }
        } else {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
        }
        return true;
    } else if (command.getName().equalsIgnoreCase("solbalance")) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            solana.handleSolBalance(player);
        } else {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
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
                    player.sendMessage("Por favor, insira um valor válido.");
                }
            } else {
                player.sendMessage("Uso correto: /invest <quantidade>");
            }
        } else {
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
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
            player.sendMessage("Empréstimo aprovado! com L do 13 ಠಿ_ಠ Nova dívida: $" + (amount * 1.1));
        } else {
            player.sendMessage("Você ainda não está registrado no banco.");
        }
    } catch (Exception e) {
        player.sendMessage("Erro ao processar empréstimo.");
        getLogger().severe("Erro ao processar empréstimo: " + e.getMessage());
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

private void processInvestments() {
    try {
        PreparedStatement statement = connection.prepareStatement(
            "UPDATE banco SET saldo = saldo + investimento * 1.25, investimento = 0 WHERE investimento > 0"
        );
        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            getLogger().info("Retorno de investimentos processado.");
        }
    } catch (Exception e) {
        getLogger().severe("Erro ao processar investimentos: " + e.getMessage());
    }
}

    private void checkBalance(Player player) {
    try {
        // 🔍 Verifica se o jogador já tem saldo na tabela 'banco'
        PreparedStatement checkStatement = connection.prepareStatement(
            "SELECT saldo FROM banco WHERE jogador = ?"
        );
        checkStatement.setString(1, player.getName());
        ResultSet resultSet = checkStatement.executeQuery();

        if (resultSet.next()) {
            // Se já estiver registrado, mostra o saldo com cor
            double saldo = resultSet.getDouble("saldo");
            player.sendMessage(Component.text("💰 Seu saldo bancário é: $" + saldo)
                .color(TextColor.color(0xFFD700))); // Dourado para destacar o saldo
        } else {
            // 📌 Jogador não está registrado no banco, então adicionamos ele com saldo inicial de 500 moedas
            PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO banco (jogador, saldo) VALUES (?, 500)"
            );
            insertStatement.setString(1, player.getName());
            insertStatement.executeUpdate();

            player.sendMessage(Component.text("✅ Você foi cadastrado no banco! Seu saldo inicial é de 500 moedas.")
                .color(TextColor.color(0x00FF00))); // Verde para indicar sucesso
        }

    } catch (SQLException e) {
        player.sendMessage(Component.text("❌ Erro ao acessar o banco de dados.")
            .color(TextColor.color(0xFF0000))); // Vermelho para indicar erro
        getLogger().severe("Erro ao consultar saldo: " + e.getMessage());
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