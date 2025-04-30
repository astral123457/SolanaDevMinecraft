package com.SolanaDevMinecraft;

import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Solana {

    private final Connection connection;

    public Solana(Connection connection) {
        this.connection = connection;
    }

    // 📌 Método para verificar saldo da carteira Solana
    public double getSolanaBalance(String walletAddress) throws Exception {
        String command = "sudo docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana solana balance " + walletAddress;
        String output = executeCommand(command);
        return Double.parseDouble(output.trim());
    }

    // 📌 Método para transferir Solana para outro jogador
    public String transferSolana(String sender, String recipientWallet, double amount) throws Exception {
        String command = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana solana transfer " +
                recipientWallet + " " + amount + " --keypair /solana-token/wallets/" + sender + "_wallet.json --allow-unfunded-recipient";
        String output = executeCommand(command);

        // 📌 Extraindo a assinatura da transação
        String signature = extractSignature(output);
        if (signature == null) {
            throw new Exception("Falha na transferência. Assinatura não encontrada.");
        }

        return signature;
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
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT walletAddress FROM players WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                walletAddress = rs.getString("walletAddress");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return walletAddress;
    }

    // 📌 Método para o comando /solbalance
    public void handleSolBalance(Player player) {
        String walletAddress = getWalletFromDatabase(player.getName());
        if (walletAddress == null) {
            player.sendMessage("Você ainda não possui uma carteira registrada.");
            return;
        }
        try {
            double balance = getSolanaBalance(walletAddress);
            player.sendMessage("Seu saldo de SOL é: " + balance);
        } catch (Exception e) {
            player.sendMessage("Erro ao verificar saldo: " + e.getMessage());
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
    String walletPath = "/solana-token/wallets/" + playerName + "_wallet.json";

    try {
        // Comando para criar a carteira via Docker
        String createCommand = "sudo -u www-data docker run --rm -v /home/astral/astralcoin/wallets:/solana-token/wallets " +
                "-v /home/astral/astralcoin/solana-data:/root/.config/solana " +
                "heysolana solana-keygen new --no-passphrase --outfile " + walletPath + " --force";

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