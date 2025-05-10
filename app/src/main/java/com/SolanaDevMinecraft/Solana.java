package com.SolanaDevMinecraft;

import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Logger;
import org.json.JSONObject;


import org.bukkit.configuration.file.FileConfiguration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import net.md_5.bungee.api.ChatColor;


import java.util.Locale;
import org.bukkit.entity.Player;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Arrays;


import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Base64;
import java.util.regex.*;
import java.util.List;



class WalletInfo {
    String walletAddress;
    String secretPhrase;
    String privateKeyHex;

    public WalletInfo(String walletAddress, String secretPhrase, String privateKeyHex) {
        this.walletAddress = walletAddress;
        this.secretPhrase = secretPhrase;
        this.privateKeyHex = privateKeyHex;
    }
}



public class Solana {

    private final Connection connection;
    private final FileConfiguration config;
    
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Solana.class.getName());

    // 🔹 Construtor correto
    public Solana(FileConfiguration config, Connection connection) {
        this.config = config; // Inicializa corretamente
        this.connection = connection;
    }

@SuppressWarnings("deprecation")
public String getPlayerLanguage(Player player) {
    String locale = player.getLocale(); // Obtém o idioma do jogador como String
    List<String> supportedLanguages = config.getStringList("language.supported"); // Obtém a lista de idiomas do config.yml

    // Se o idioma do jogador estiver na lista de suportados, usa ele. Caso contrário, usa o padrão do config.
    return supportedLanguages.contains(locale) ? locale : config.getString("language.default", "pt-BR"); 
}

    // 📌 Método para verificar saldo da carteira Solana
    public double getSolanaBalance(String walletAddress) throws Exception {
    String host = config.getString("docker.host");
    String apiwebkey = config.getString("docker.api_web_key");
    String comando = "solana balance " + walletAddress;

    String url = String.format("http://%s/consulta.php?apikey=%s&comando=%s", host, apiwebkey, URLEncoder.encode(comando, "UTF-8"));

    String response = executeHttpGet(url);

    if (response.contains("\"status\":\"success\"")) {
        String output = response.split("\"output\":\"")[1].split("\"")[0].replace(" SOL", "").trim();
        return Double.parseDouble(output);
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
    String host = config.getString("docker.host");
    String apiwebkey = config.getString("docker.api_web_key");

    if (Double.isNaN(amount) || Double.isInfinite(amount)) {
        throw new IllegalArgumentException("Valor inválido para transferência: " + amount);
    }

    // Agora declaramos a variável `comando` fora do bloco if/else
    String comando = String.format("solana transfer %s %s --keypair /solana-token/wallets/%s_wallet.json --allow-unfunded-recipient",
        recipientWallet,
        String.valueOf(amount),  // Garante conversão correta para String
        sender
    );

    String url = String.format("http://%s/consulta.php?apikey=%s&comando=%s", host, apiwebkey, URLEncoder.encode(comando, "UTF-8"));

    String response = executeHttpGet(url);

    // Adicionando log de depuração
    LOGGER.info("[DEBUG SOLANA TRANSFER RESPONSE]: " + response);

    if (response.contains("\"status\":\"success\"")) {
        String output = response.split("\"output\":\"")[1].split("\"")[0].trim();
        return output;
    } else {
        throw new Exception("Erro ao transferir SOL: " + response);
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
        String[] lines = output.split(" ");
        for (String line : lines) {
            if (line.startsWith("Minecraft-Sigmaboy: ")) {
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

        // Estabelece a conexão com o banco de dados
        manualConnection = DriverManager.getConnection(url, user, password);

        // Consulta para buscar a carteira vinculada ao jogador
        String query = "SELECT c.endereco FROM carteiras c JOIN jogadores j ON c.jogador_id = j.id WHERE LOWER(j.nome) = LOWER(?)";
        PreparedStatement stmt = manualConnection.prepareStatement(query);
        stmt.setString(1, username.trim());

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            walletAddress = rs.getString("endereco");
            LOGGER.info("Carteira encontrada para o usuário " + username + ": " + walletAddress);
        } else {
            LOGGER.warning("Nenhuma carteira encontrada para o usuário: " + username);
        }
    } catch (Exception e) {
        LOGGER.severe("Erro ao buscar carteira no banco: " + e.getMessage());
    } finally {
        try {
            if (manualConnection != null) manualConnection.close();
        } catch (Exception e) {
            LOGGER.severe("Erro ao fechar conexão: " + e.getMessage());
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
    player.sendMessage(Component.text("🔍 Obtendo o saldo de SOL para o jogador: ")
    .color(TextColor.color(0x800080)) // Roxo
    .append(Component.text(player.getName()).color(TextColor.color(0xFFD700))) // Amarelo para o nome
);

    // Obtém o endereço da carteira do banco de dados
    String walletAddress = getWalletFromDatabase(player.getName());
    if (walletAddress == null) {
        LOGGER.warning("Nenhuma carteira encontrada para o jogador: " + player.getName());
        String lang = getPlayerLanguage(player);

        if (lang.equals("pt-BR")) {
            player.sendMessage(Component.text("💳 Você ainda não possui uma carteira registrada.")
                .color(TextColor.color(0xFF0000))); // Vermelho
        } else if (lang.equals("es-ES")) {
            player.sendMessage(Component.text("💳 Aún no tienes una billetera registrada.")
                .color(TextColor.color(0xFF0000))); // Vermelho
        } else {
            player.sendMessage(Component.text("💳 You do not have a registered wallet yet.")
                .color(TextColor.color(0xFF0000))); // Vermelho
        }
        return;
    }

    player.sendMessage(
    Component.text("💳 Carteira SOL: ")
        .color(TextColor.color(0xFFD700)) // Dourado para destaque
        .append(
            Component.text(walletAddress)
                .color(TextColor.color(0x00FFFF)) // Azul para o endereço da carteira
                .clickEvent(ClickEvent.suggestCommand(walletAddress)) // Sugere o texto para copiar manualmente
        )
);

    LOGGER.info("Endereço da carteira encontrado: " + walletAddress);

    try {
        // Obtém o saldo da carteira
        double balance = getSolanaBalance(walletAddress);
        LOGGER.info("Saldo obtido para a carteira " + walletAddress + ": " + balance + " SOL");
       

String lang = getPlayerLanguage(player);

        if (lang.equals("pt-BR")) {
            player.sendMessage(Component.text("💰 Seu saldo de SOL é: ")
                .color(TextColor.color(0x800080))); // Roxo
        } else if (lang.equals("es-ES")) {
            player.sendMessage(Component.text("💰 Tu saldo de SOL es: ")
                .color(TextColor.color(0x800080))); // Roxo
            } else {
            player.sendMessage(Component.text("💰 Your SOL balance is: ")
                .color(TextColor.color(0x800080))); // Roxo
        } 
        player.sendMessage(Component.text(" " + balance + " SOL")
                .color(TextColor.color(0xFFD700)));

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
          

String lang = getPlayerLanguage(player);

        if (lang.equals("pt-BR")) {
    player.sendMessage(Component.text("💸 Transferência de ")
        .color(TextColor.color(0x00FF00)) // Verde
        .append(Component.text(amount + " SOL ").color(TextColor.color(0xFFD700))) // Dourado
        .append(Component.text("para ").color(TextColor.color(0x00FF00))) // Verde
        .append(Component.text(recipient).color(TextColor.color(0x00FFFF))) // Azul Claro
        .append(Component.text(" concluída com sucesso! Assinatura: ").color(TextColor.color(0x00FF00))) // Verde
        .append(Component.text(signature).color(TextColor.color(0xFFFF00)))); // Amarelo - Fechamento correto
} else if (lang.equals("es-ES")) {
    player.sendMessage(Component.text("💸 Transferencia de ")
        .color(TextColor.color(0x00FF00)) // Verde
        .append(Component.text(amount + " SOL ").color(TextColor.color(0xFFD700))) // Dourado
        .append(Component.text("a ").color(TextColor.color(0x00FF00))) // Verde
        .append(Component.text(recipient).color(TextColor.color(0x00FFFF))) // Azul Claro
        .append(Component.text(" completada con éxito! Firma: ").color(TextColor.color(0x00FF00))) // Verde
        .append(Component.text(signature).color(TextColor.color(0xFFFF00)))); // Amarelo - Fechamento correto
    }
    else {
    player.sendMessage(Component.text("💸 Transfer of ")
        .color(TextColor.color(0x00FF00)) // Verde
        .append(Component.text(amount + " SOL ").color(TextColor.color(0xFFD700))) // Dourado
        .append(Component.text("to ").color(TextColor.color(0x00FF00))) // Verde
        .append(Component.text(recipient).color(TextColor.color(0x00FFFF))) // Azul Claro
        .append(Component.text(" completed successfully! Signature: ").color(TextColor.color(0x00FF00))) // Verde
        .append(Component.text(signature).color(TextColor.color(0xFFFF00)))); // Amarelo - Fechamento correto
}
 

        } catch (Exception e) {
            player.sendMessage("Erro ao transferir SOL: " + e.getMessage());
        }
    }

    // 📌 Método para comprar moedas do jogo usando Solana com base em uma taxa fixa
public void buyGameCurrency(Player player, double solAmount) {
    int conversionRate = config.getInt("store.value_of-in-game_currency"); // Obtém corretamente o valor numérico // 1 SOL = 1000 moedas do jogo
    //int conversionRate = 1000; // 1 SOL = 1000 moedas do jogo
    int gameCurrencyAmount = (int) (solAmount * conversionRate);
    String lang = getPlayerLanguage(player);

    String playerWallet = getWalletFromDatabase(player.getName());
    if (playerWallet == null) {
        
        
        if (lang.equals("pt-BR")) {
            player.sendMessage("❌ Você ainda não possui uma carteira registrada.");
            player.sendMessage(Component.text("💳 Crie uma carteira usando /createwallet.")
                .color(TextColor.color(0xFF0000))); // Vermelho
        } else if (lang.equals("es-ES")) {
            player.sendMessage("❌ Aún no tienes una billetera registrada.");
            player.sendMessage(Component.text("💳 Crea una billetera usando /createwallet.")
                .color(TextColor.color(0xFF0000))); // Vermelho
            } else {
            player.sendMessage("❌ You do not yet have a registered wallet.");
            player.sendMessage(Component.text("💳 Create a wallet using /createwallet.")
                .color(TextColor.color(0xFF0000))); // Vermelho
        }
        return;
    }

    try {
        // 🔹 Verifica saldo da carteira do jogador antes da compra
        double solBalance = getSolanaBalance(playerWallet);
        if (solBalance < solAmount) {
            if (lang.equals("pt-BR")) {
                player.sendMessage("💰 Saldo insuficiente de SOL. Saldo atual: " + solBalance);
            } else if (lang.equals("es-ES")) {
                player.sendMessage("💰 Saldo insuficiente de SOL. Saldo actual: " + solBalance);
                }
            else {
                player.sendMessage("💰 Insufficient SOL balance. Current balance: " + solBalance);
            }
            return;
        }

        // 🔹 Executa transferência para a carteira da loja/banco
        String host = config.getString("docker.host");
        String apiwebkey = config.getString("docker.api_web_key");
        String bank = config.getString("docker.wallet_bank_store_admin");


        DecimalFormat df = new DecimalFormat("0.##"); // Remove zeros desnecessários
        String formattedAmount = String.format("%.2f", solAmount).replace(",", ".");

String comando = String.format(
    "solana transfer %s %s --keypair /solana-token/wallets/%s_wallet.json --allow-unfunded-recipient",
    bank,  
    formattedAmount,  // Agora está como String e não será tratado como double
    player.getName().replace(" ", "_").toLowerCase()
);


        String url = String.format("http://%s/consulta.php?apikey=%s&comando=%s", host, apiwebkey, URLEncoder.encode(comando, "UTF-8"));

        String response = executeHttpGet(url);

        // Adicionando log de depuração
        LOGGER.info("[DEBUG SOLANA BUY RESPONSE]: " + response);

        // 🔹 Processa resposta da API
        if (response.contains("\"status\":\"success\"")) {
            String signature = response.split("\"output\":\"")[1].split("\"")[0].trim();

            // 🔹 Atualiza saldo do jogador no banco de dados
            try (PreparedStatement updateStatement = this.connection.prepareStatement(
                "UPDATE banco SET saldo = saldo + ? WHERE jogador = ?"
            )) {
                updateStatement.setInt(1, gameCurrencyAmount);
                updateStatement.setString(2, player.getName());
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    // 🔹 Registra a transação no livro caixa
                    registerTransaction(player.getName(), "compra", solAmount, "SOL", signature);
                    if (lang.equals("pt-BR")) {
                        player.sendMessage(Component.text("✅ Compra realizada com sucesso! ")
                        .color(TextColor.color(0x00FF00)) // Verde
                        .append(Component.text("Você recebeu " + gameCurrencyAmount + " moedas.")
                        .color(TextColor.color(0xFFD700))));
                        
                        player.sendMessage(Component.text("💸 Transação registrada com assinatura: ")
                        .color(TextColor.color(0x00FFFF)) // Azul Claro
                        .append(Component.text(signature).color(TextColor.color(0xFFFF00))));
                    } else if (lang.equals("es-ES")) {
                        player.sendMessage(Component.text("✅ Compra realizada con éxito! ")
                        .color(TextColor.color(0x00FF00)) // Verde
                        .append(Component.text("Recibiste " + gameCurrencyAmount + " monedas.")
                        .color(TextColor.color(0xFFD700))));

                        player.sendMessage(Component.text("💸 Transacción registrada con firma: ")
                        .color(TextColor.color(0x00FFFF)) // Azul Claro
                        .append(Component.text(signature).color(TextColor.color(0xFFFF00))));

                        } else {
                        player.sendMessage(Component.text("✅ Purchase completed successfully! ")
                        .color(TextColor.color(0x00FF00))
                        .append(Component.text("You received " + gameCurrencyAmount + " coins.")
                        .color(TextColor.color(0xFFD700))));
                        
                        player.sendMessage(Component.text("💸 Transaction registered with signature: ")
                        .color(TextColor.color(0x00FFFF))
                        .append(Component.text(signature).color(TextColor.color(0xFFFF00))));
                    }


                } else {
                    player.sendMessage("⚠ Erro ao atualizar seu saldo no banco.");
                }
            }
        } else {
            throw new Exception("❌ Erro ao transferir SOL: " + response);
        }
    } catch (Exception e) {
        player.sendMessage("⚠ Erro ao processar a compra: " + e.getMessage());
        e.printStackTrace();
    }
}


// 📌 Método para criar uma carteira Solana para o jogador
 public void createWallet(Player player) {
    String playerName = player.getName().replace(" ", "_").toLowerCase();
    String walletPath = String.format("wallets/%s_wallet.json", playerName);
    PreparedStatement statement = null; // ✅ Declarado uma vez
    String lang = getPlayerLanguage(player);

    try {
        String host = config.getString("docker.host");
        String apiwebkey = config.getString("docker.api_web_key");

        // 🔹 Gera a carteira via API
        String comandoGerar = String.format("solana-keygen new --no-passphrase --outfile %s --force", walletPath);
        String urlGerar = String.format("http://%s/consulta.php?apikey=%s&comando=%s", host, apiwebkey, URLEncoder.encode(comandoGerar, "UTF-8"));
        

        String responseGerar = executeHttpGet(urlGerar);
        if (!responseGerar.contains("\"status\":\"success\"")) {
            throw new Exception("❌ Erro ao criar carteira: " + responseGerar);
        }

        // 🔍 Extraindo informações da carteira




        String walletData = new String(responseGerar);

        WalletInfo walletInfo = extractWalletInfo(walletData);

        String walletAddress = walletInfo.walletAddress;
        String secretPhrase = walletInfo.secretPhrase;


        // 🔹 Lendo a chave privada da carteira gerada
        String comandoLer = String.format("cat %s", walletPath);
        String urlLer = String.format("http://%s/consulta.php?apikey=%s&comando=%s", host, apiwebkey, URLEncoder.encode(comandoLer, "UTF-8"));

        String responseLer = executeHttpGet(urlLer);
        if (!responseLer.contains("\"status\":\"success\"")) {
            throw new Exception("❌ Erro ao ler carteira: " + responseLer);
        }

        String privateKeyHex = convertPrivateKeyToHex(responseLer);

        //player.sendMessage(Component.text("? PrivateKeyHex 2: " + (privateKeyHex != null ? privateKeyHex : "NULO")));

        // ✅ Depuração dos dados extraídos


        // 🔹 Verifica se o jogador já está cadastrado
        PreparedStatement checkPlayer = connection.prepareStatement("SELECT id FROM jogadores WHERE nome = ?");
        checkPlayer.setString(1, playerName);
        ResultSet rs = checkPlayer.executeQuery();
        int jogadorId;

        if (!rs.next()) {
            // 🔹 Criando novo jogador
            PreparedStatement createPlayer = connection.prepareStatement(
                "INSERT INTO jogadores (nome) VALUES (?)", Statement.RETURN_GENERATED_KEYS
            );
            createPlayer.setString(1, playerName);
            createPlayer.executeUpdate();

            ResultSet generatedKeys = createPlayer.getGeneratedKeys();
            if (generatedKeys.next()) {
                jogadorId = generatedKeys.getInt(1);
            } else {
                throw new Exception("❌ Erro ao registrar novo jogador!");
            }
        } else {
            jogadorId = rs.getInt("id");
        }

        // 🔹 Salvando a carteira no banco
        statement = connection.prepareStatement(
            "INSERT INTO carteiras (jogador_id, endereco, chave_privada, frase_secreta) VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE endereco = ?, chave_privada = ?, frase_secreta = ?"
        );
        statement.setInt(1, jogadorId);
        statement.setString(2, walletAddress);
        statement.setString(3, privateKeyHex);
        statement.setString(4, secretPhrase);
        statement.setString(5, walletAddress);
        statement.setString(6, privateKeyHex);
        statement.setString(7, secretPhrase);
        statement.executeUpdate();

        // 🔹 Feedback ao jogador
        

        if (lang.equals("pt-BR")) {
            player.sendMessage(Component.text("✅ Carteira criada com sucesso! Endereço: " + walletAddress).color(TextColor.color(0x00FF00)));
            player.sendMessage(Component.text("🛡️ Guarde sua frase secreta com segurança!").color(TextColor.color(0xFFD700)));
            player.sendMessage(Component.text("✅ SecretPhrase: " + (secretPhrase != null ? secretPhrase : "NULO")));
            } else if (lang.equals("es-ES")) {
                player.sendMessage(Component.text("✅ Billetera creada con éxito! Dirección: " + walletAddress).color(TextColor.color(0x00FF00)));
                player.sendMessage(Component.text("🛡️ ¡Guarda tu frase secreta a salvo!").color(TextColor.color(0xFFD700)));
                player.sendMessage(Component.text("✅ Frase secreta: " + (secretPhrase != null ? secretPhrase : "NULO")));
                }else { 
                player.sendMessage(Component.text("✅ Wallet created successfully! Address: " + walletAddress).color(TextColor.color(0x00FF00)));
                player.sendMessage(Component.text("🛡️ Keep your secret phrase safe!").color(TextColor.color(0xFFD700)));
                player.sendMessage(Component.text("✅ SecretPhrase: " + (secretPhrase != null ? secretPhrase : "NULO")));
            }

    } catch (Exception e) {
        player.sendMessage(Component.text("⚠ Erro ao criar a carteira: " + e.getMessage())
                .color(TextColor.color(0xFF0000)));
        e.printStackTrace();
    } finally {
        if (statement != null) {
            try {
                statement.close(); // ✅ Fecha corretamente
            } catch (SQLException e) {
                LOGGER.warning("❌ Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}


// 📌 Método auxiliar para extrair o endereço da carteira do comando de saída
    private WalletInfo extractWalletInfo(String walletData) {
    try {
        // 🔍 Remove qualquer cabeçalho inicial da carteira
        if (walletData.contains("pubkey: ")) {
            walletData = walletData.substring(walletData.indexOf("pubkey: "));
        }

        // 🔍 Regex para capturar `walletAddress`
        Pattern patternAddress = Pattern.compile("pubkey: ([A-Za-z0-9]+)");
        Matcher matcherAddress = patternAddress.matcher(walletData);

        // 🔍 Regex atualizado para capturar a frase secreta corretamente
        Pattern patternPhrase = Pattern.compile("Save this seed phrase to recover your new keypair:\\s*([^\n\r=]+)");
        Matcher matcherPhrase = patternPhrase.matcher(walletData);

        // 🎯 Extração correta dos valores
        String walletAddress = matcherAddress.find() ? matcherAddress.group(1).trim() : null;
        String secretPhrase = matcherPhrase.find() ? matcherPhrase.group(1).replaceAll("^[\\n\\r]+|[\\n\\r]+$", "").trim() : null;

        // 🔹 Log para depuração
        LOGGER.info("[DEBUG] Endereço da Carteira: " + walletAddress);
        LOGGER.info("[DEBUG] Frase Secreta: " + secretPhrase);

        return new WalletInfo(walletAddress, secretPhrase, null);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null; // 🚨 Retorna `null` se não encontrar os valores corretamente
}



    // Lendo chave privada do arquivo JSON gerado pela Solana
public static String convertPrivateKeyToHex(String jsonResponse) {
        try {
            // 🔍 Aplica Regex para extrair apenas os números dentro de "output"
            Pattern pattern = Pattern.compile("\"output\":\"\\[(.*?)\\]\"");
            Matcher matcher = pattern.matcher(jsonResponse);

            if (!matcher.find()) {
                System.err.println("[ERRO] Campo 'output' não encontrado ou mal formatado!");
                return null;
            }

            // 🔍 Captura os números extraídos da chave privada
            String numbersOnly = matcher.group(1).trim();
            System.out.println("[DEBUG] Números Extraídos: " + numbersOnly);

            // 🔹 Divide os números separados por vírgula e converte para um array de bytes
            String[] numberStrings = numbersOnly.split(",");
            byte[] secretKeyArray = new byte[numberStrings.length];

            for (int i = 0; i < numberStrings.length; i++) {
                secretKeyArray[i] = (byte) Integer.parseInt(numberStrings[i].trim());
            }

            // 🔹 Converte os bytes para hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : secretKeyArray) {
                hexString.append(String.format("%02x", b));
            }

            System.out.println("[DEBUG] Chave privada em HEX: " + hexString.toString());
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
