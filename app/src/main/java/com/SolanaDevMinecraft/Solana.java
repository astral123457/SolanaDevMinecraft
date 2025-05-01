package com.SolanaDevMinecraft;

import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

import org.bukkit.configuration.file.FileConfiguration;



public class Solana {

    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(Solana.class.getName());

    public Solana(Connection connection) {
        this.config = config;
        this.connection = connection;
    }


    private FileConfiguration config;

public Solana(FileConfiguration config, Connection connection) {
    this.config = config;
    this.connection = connection;
}

    // 📌 Método para verificar saldo da carteira Solana
    public double getSolanaBalance(String walletAddress) throws Exception {
    // Obtém o host do arquivo de configuração
    String host = config.getString("docker.host");

    // Constrói a URL para a requisição HTTP
    String url = String.format("http://%s/consulta.php?comando=%s", host, walletAddress);

    // Faz a requisição HTTP
    String response = executeHttpGet(url);

    // Processa a resposta JSON
    if (response.contains("\"status\":\"success\"")) {
        // Extrai o campo "output" do JSON e remove " SOL"
        String output = response.split("\"output\":\"")[1].split("\"")[0].replace(" SOL", "").trim();
        return Double.parseDouble(output); // Converte o saldo para um valor numérico
    } else {
        throw new Exception("Erro ao obter saldo: " + response);
    }
}

    // 📌 Método auxiliar para executar requisições HTTP GET
private String executeHttpGet(String urlString) throws Exception {
    URL url = new URL(urlString);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }
}

   public String transferSolana(String sender, String recipientWallet, double amount) throws Exception {
    // URL da página PHP
    String host = config.getString("docker.host");
    String baseUrl = "http://" + host + "/transfer.php";

    // Constrói os parâmetros para a requisição GET
    String params = String.format("sender=%s&recipientWallet=%s&amount=%.2f",
        URLEncoder.encode(sender, "UTF-8"),
        URLEncoder.encode(recipientWallet, "UTF-8"),
        amount
    );

    // Faz a requisição HTTP GET
    HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "?" + params).openConnection();
    connection.setRequestMethod("GET");

    // Lê a resposta
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        // Processa a resposta JSON
        String jsonResponse = response.toString();
        if (jsonResponse.contains("\"status\":\"success\"")) {
            return jsonResponse.split("\"signature\":\"")[1].split("\"")[0];
        } else {
            throw new Exception("Erro ao transferir SOL: " + jsonResponse);
        }
    }
}

    // 📌 Método para registrar transações no banco de dados
    public void registerTransaction(String player, String transactionType, double amount, String currency, String signature) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO livro_caixa (jogador, tipo_transacao, valor, moeda, assinatura) VALUES (?, ?, ?, ?, ?)"
            );
            statement.setString(1, player);
            statement.setString(2, transactionType);
            statement.setDouble(3, amount);
            statement.setString(4, currency);
            statement.setString(5, signature);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📌 Método auxiliar para executar comandos no sistema
    private String executeCommand(String command) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(command.split(" "));
        builder.redirectErrorStream(true);
        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString().trim();
        }
    }

    // 📌 Método auxiliar para extrair a assinatura da transação
    private String extractSignature(String output) {
        String[] lines = output.split("\n");
        for (String line : lines) {
            if (line.startsWith("Signature: ")) {
                return line.substring(10).trim();
            }
        }
        return null;
    }

    // 📌 Método para obter o endereço da carteira do banco de dados
    public String getWalletFromDatabase(String username) {
    String walletAddress = null;
    Connection manualConnection = null;

    try {
        LOGGER.info("Conectando ao banco de dados para buscar a carteira do usuário: " + username);

        // Obtém as configurações do banco de dados do config.yml
        String url = config.getString("database.url");
        String user = config.getString("database.user");
        String password = config.getString("database.password");

        // Loga as configurações para depuração (não logue a senha em produção)
        LOGGER.info("URL do banco de dados: " + url);
        LOGGER.info("Usuário do banco de dados: " + user);

        // Estabelece a conexão com o banco de dados
        manualConnection = DriverManager.getConnection(url, user, password);

        // Consulta para buscar o endereço da carteira com base no nome do jogador
        String query = "SELECT c.endereco FROM carteiras c " +
                       "JOIN jogadores j ON c.jogador_id = j.id " +
                       "WHERE j.nome = ?";
        PreparedStatement stmt = manualConnection.prepareStatement(query);
        stmt.setString(1, username);
        LOGGER.info("Executando consulta SQL: " + query.replace("?", "'" + username + "'"));
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            walletAddress = rs.getString("endereco");
            LOGGER.info("Carteira encontrada para o usuário " + username + ": " + walletAddress);
        } else {
            LOGGER.warning("Nenhuma carteira encontrada para o usuário: " + username);
        }
    } catch (Exception e) {
        LOGGER.severe("Erro ao buscar carteira no banco de dados para o usuário " + username + ": " + e.getMessage());
    } finally {
        if (manualConnection != null) {
            try {
                manualConnection.close();
            } catch (Exception e) {
                LOGGER.severe("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
            }
        }
    }

    return walletAddress;
}

    public void logWalletAddress(Player player) {
    String username = player.getName();
    String walletAddress = getWalletFromDatabase(username);

    if (walletAddress != null) {
        // Loga o endereço da carteira no console do servidor
        System.out.println("Endereço da carteira para " + username + ": " + walletAddress);
    } else {
        System.out.println("Nenhuma carteira encontrada para o jogador: " + username);
    }
}

    // 📌 Método para o comando /solbalance
    public void handleSolBalance(Player player) {

    if (player == null) {
    LOGGER.severe("O objeto Player é nulo.");
    return;
    }

    String playerName = player.getName();
    if (playerName == null || playerName.isEmpty()) {
        LOGGER.severe("O nome do jogador é nulo ou vazio.");
        player.sendMessage("Erro: Não foi possível identificar o jogador.");
        return;
    }
    // Loga o nome do jogador
    player.sendMessage("Obtendo o saldo de SOL para o jogador: " + player.getName());

    // Obtém o endereço da carteira do banco de dados
    String walletAddress = getWalletFromDatabase(player.getName());
    if (walletAddress == null) {
        LOGGER.warning("Nenhuma carteira encontrada para o jogador: " + player.getName());
        player.sendMessage("Você ainda não possui uma carteira registrada.");
        return;
    }

    player.sendMessage("Carteira SOL: " + walletAddress);
    LOGGER.info("Endereço da carteira encontrado: " + walletAddress);

    try {
        // Obtém o saldo da carteira
        double balance = getSolanaBalance(walletAddress);
        LOGGER.info("Saldo obtido para a carteira " + walletAddress + ": " + balance + " SOL");
        player.sendMessage("Seu saldo de SOL é: " + balance);
    } catch (Exception e) {
        LOGGER.severe("Erro ao verificar saldo para a carteira " + walletAddress + ": " + e.getMessage());
        player.sendMessage("Erro ao verificar saldo: " + e.getMessage());
        e.printStackTrace();
    }
    }

    // 📌 Método para o comando /soltransfer
    public void handleSolTransfer(Player player, String recipient, double amount) {
        String recipientWallet = getWalletFromDatabase(recipient);
        if (recipientWallet == null) {
            player.sendMessage("O jogador " + recipient + " não possui uma carteira registrada.");
            return;
        }
        try {
            String signature = transferSolana(player.getName(), recipientWallet, amount);
            registerTransaction(player.getName(), "transferência", amount, "SOL", signature);
            player.sendMessage("Transferência de " + amount + " SOL para " + recipient + " concluída com sucesso! Assinatura: " + signature);
        } catch (Exception e) {
            player.sendMessage("Erro ao transferir SOL: " + e.getMessage());
        }
    }

    // 📌 Método para comprar moedas do jogo usando Solana com base em uma taxa fixa
public void buyGameCurrency(Player player, double solAmount) {
    // Define a taxa de conversão: 1 SOL = 1000 moedas
    int conversionRate = 1000;
    int gameCurrencyAmount = (int) (solAmount * conversionRate);

    String playerWallet = getWalletFromDatabase(player.getName());
    if (playerWallet == null) {
        player.sendMessage("Você ainda não possui uma carteira registrada.");
        return;
    }

    try {
        // Verifica o saldo de SOL do jogador
        double solBalance = getSolanaBalance(playerWallet);
        if (solBalance < solAmount) {
            player.sendMessage("Saldo insuficiente de SOL. Saldo atual: " + solBalance);
            return;
        }

        // Comando para transferência via Docker
        String transferCommand = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana solana transfer dadhcDXHiHDrWkT2Z4pSZyF6HWmHwQMG3HtGciwccVP " +
                solAmount + " --keypair /solana-token/wallets/" + player.getName() + "_wallet.json --allow-unfunded-recipient";

        String transferOutput = executeCommand(transferCommand);

        // Extraindo a assinatura da transação
        String signature = extractSignature(transferOutput);
        if (signature == null) {
            player.sendMessage("Não foi possível obter a assinatura da transação. Consulte um administrador.");
            return;
        }

        // Atualiza o saldo do jogador no banco de dados
        PreparedStatement updateStatement = connection.prepareStatement(
            "UPDATE banco SET saldo = saldo + ? WHERE jogador = ?"
        );
        updateStatement.setInt(1, gameCurrencyAmount);
        updateStatement.setString(2, player.getName());
        int rowsUpdated = updateStatement.executeUpdate();

        if (rowsUpdated > 0) {
            // Registra a transação no livro caixa
            registerTransaction(player.getName(), "compra", solAmount, "SOL", signature);
            player.sendMessage("Você comprou " + gameCurrencyAmount + " moedas por " + solAmount + " SOL!");
            player.sendMessage("Transação registrada com assinatura: " + signature);
        } else {
            player.sendMessage("Erro ao atualizar seu saldo no banco.");
        }
    } catch (Exception e) {
        player.sendMessage("Erro ao processar a compra: " + e.getMessage());
        e.printStackTrace();
    }
}


// 📌 Método para criar uma carteira Solana para o jogador
public void createWallet(Player player) {
    String playerName = player.getName();

    try {
        // Obtém os valores do arquivo de configuração
        String basePath = config.getString("docker.base_path");
        String solanaCommand = config.getString("docker.solana_command");
        String walletPath = String.format("%s/wallets/%s_wallet.json", basePath, playerName);

        // Constrói o comando dinamicamente
        String createCommand = String.format(
            "sudo docker run --rm -v %s:/solana-token/wallets -v %s/solana-data:/root/.config/solana %s solana-keygen new --no-passphrase --outfile %s --force",
            basePath, basePath, solanaCommand, walletPath
        );

        // Executa o comando para criar a carteira
        String output = executeCommand(createCommand);

        // Captura o endereço público (pubkey)
        String walletAddress = extractWalletAddress(output);
        if (walletAddress == null) {
            player.sendMessage("Erro ao criar a carteira. Consulte um administrador.");
            return;
        }

        // Salva a carteira no banco de dados
        PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO players (username, walletAddress) VALUES (?, ?) ON DUPLICATE KEY UPDATE walletAddress = ?"
        );
        statement.setString(1, playerName);
        statement.setString(2, walletAddress);
        statement.setString(3, walletAddress);
        statement.executeUpdate();

        player.sendMessage("Carteira criada com sucesso! Endereço: " + walletAddress);
    } catch (Exception e) {
        player.sendMessage("Erro ao criar a carteira: " + e.getMessage());
        e.printStackTrace();
    }
}

// 📌 Método auxiliar para extrair o endereço da carteira do comando de saída
private String extractWalletAddress(String output) {
    String[] lines = output.split("\n");
    for (String line : lines) {
        if (line.contains("pubkey:")) {
            return line.split(": ")[1].trim();
        }
    }
    return null;
}

}