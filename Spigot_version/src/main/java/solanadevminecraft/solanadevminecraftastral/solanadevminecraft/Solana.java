package solanadevminecraft.solanadevminecraftastral.solanadevminecraft;


import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;
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

    public void transferSolana(Player sender, String recipient, double amount) {
        // Obtém as carteiras do remetente e do destinatário
        String senderWallet = getWalletFromDatabase(sender.getName());
        String recipientWallet = getWalletFromDatabase(recipient);

        if (senderWallet == null) {
            sender.sendMessage(ChatColor.RED + "❌ Você não possui uma carteira registrada.");
            return;
        }

        if (recipientWallet == null) {
            sender.sendMessage(ChatColor.RED + "❌ O jogador " + recipient + " não possui uma carteira registrada.");
            return;
        }

// Verifica saldo antes da transferência
        double senderBalance;
        try {
            senderBalance = getSolanaBalance(senderWallet);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "⚠ Erro ao obter saldo da carteira: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (senderBalance < amount) {
            sender.sendMessage(ChatColor.RED + "❌ Saldo insuficiente para transferência. Saldo atual: " + senderBalance);
            return;
        }

        try {
            // Executa a transferência via API
            String host = config.getString("docker.host");
            String apiwebkey = config.getString("docker.api_web_key");

            DecimalFormat df = new DecimalFormat("0.##"); // Remove zeros desnecessários
            String formattedAmount = String.format("%.2f", amount).replace(",", ".");

            String comando = String.format("solana transfer %s %s --keypair /solana-token/wallets/%s_wallet.json --allow-unfunded-recipient",
                    recipientWallet, formattedAmount, sender.getName().replace(" ", "_").toLowerCase());// sempre corrigir %20 epaco e + coisa de url

            String url = String.format("http://%s/consulta.php?apikey=%s&comando=%s", host, apiwebkey, URLEncoder.encode(comando, "UTF-8")
            );
            String response = executeHttpGet(url);

            if (response != null && response.contains("\"output\":\"")) {
                String signature = response.split("\"output\":\"")[1].split("\"")[0].trim();
                signature = response.replaceFirst("(?s).*Signature: ", "").trim();
                signature = signature.replaceAll("\\n", ""); // Remove todas as quebras de linha
                signature = signature.replaceAll("\"}", ""); // Remove o fechamento JSON
                signature = signature.replace("\\n", "").replace("\\r", "");
                signature = signature.trim(); // Garante que espaços extras sejam removidos

                // Registra a transação no banco de dados
                try (PreparedStatement stmt = this.connection.prepareStatement(
                        "INSERT INTO livro_caixa (jogador, tipo_transacao, valor, moeda, assinatura) VALUES (?, ?, ?, ?, ?)"
                )) {

                    stmt.setString(1, sender.getName());
                    stmt.setString(2, "transferência");
                    stmt.setDouble(3, amount);
                    stmt.setString(4, "SOL");
                    stmt.setString(5, signature);
                    stmt.executeUpdate();
                }


                sendTransactionMessage(sender, recipient, amount, signature);
            } else {
                throw new Exception("❌ Erro ao transferir SOL: " + response);
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "⚠ Erro ao processar a transferência: " + e.getMessage()); // Vermelho
            e.printStackTrace();
        }
    }

    // Método auxiliar para mensagens personalizadas ao jogador
    private void sendTransactionMessage(Player sender, String recipient, double amount, String signature) {
        sender.sendMessage(ChatColor.GREEN + "💸 Transferência concluída! " +
                ChatColor.GOLD + amount + " SOL para " +
                ChatColor.AQUA + recipient +
                ChatColor.GREEN + ". Assinatura: " +
                ChatColor.YELLOW + signature);
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
        player.sendMessage(ChatColor.DARK_PURPLE + "🔍 Obtendo o saldo de SOL para o jogador: " + ChatColor.GOLD + player.getName());

// Obtém o endereço da carteira do banco de dados
        String walletAddress = getWalletFromDatabase(player.getName());
        if (walletAddress == null) {
            LOGGER.warning("Nenhuma carteira encontrada para o jogador: " + player.getName());
            String lang = getPlayerLanguage(player);

            if (lang.equals("pt-BR")) {
                player.sendMessage(ChatColor.RED + "💳 Você ainda não possui uma carteira registrada.");
            } else if (lang.equals("es-ES")) {
                player.sendMessage(ChatColor.RED + "💳 Aún no tienes una billetera registrada.");
            } else {
                player.sendMessage(ChatColor.RED + "💳 You do not have a registered wallet yet.");
            }
            return;
        }

        player.sendMessage(ChatColor.GOLD + "💳 Carteira SOL: " + ChatColor.AQUA + walletAddress);

        LOGGER.info("Endereço da carteira encontrado: " + walletAddress);

        try {
            // Obtém o saldo da carteira
            double balance = getSolanaBalance(walletAddress);
            LOGGER.info("Saldo obtido para a carteira " + walletAddress + ": " + balance + " SOL");


            String lang = getPlayerLanguage(player);

            if (lang.equals("pt-BR")) {
                player.sendMessage(ChatColor.DARK_PURPLE + "💰 Seu saldo de SOL é: ");
            } else if (lang.equals("es-ES")) {
                player.sendMessage(ChatColor.DARK_PURPLE + "💰 Tu saldo de SOL es: ");
            } else {
                player.sendMessage(ChatColor.DARK_PURPLE + "💰 Your SOL balance is: ");
            }
            player.sendMessage(ChatColor.GOLD + " " + balance + " SOL");

        } catch (Exception e) {
            LOGGER.severe("Erro ao verificar saldo para a carteira " + walletAddress + ": " + e.getMessage());
            player.sendMessage("Erro ao verificar saldo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 📌 Método para comprar moedas do jogo usando Solana com base em uma taxa fixa
    public void buyGameCurrency(Player player, double solAmount) {
        int conversionRate = config.getInt("store.value_of_in_game_currency", 1000); // Obtém corretamente o valor numérico // 1 SOL = 1000 moedas do jogo
        //int conversionRate = 1000; // 1 SOL = 1000 moedas do jogo
        int gameCurrencyAmount = (int) (solAmount * conversionRate);
        String lang = getPlayerLanguage(player);

        String playerWallet = getWalletFromDatabase(player.getName());
        if (playerWallet == null) {


            if (lang.equals("pt-BR")) {
                player.sendMessage(ChatColor.RED + "❌ Você ainda não possui uma carteira registrada.");
                player.sendMessage(ChatColor.RED + "💳 Crie uma carteira usando /createwallet.");
            } else if (lang.equals("es-ES")) {
                player.sendMessage(ChatColor.RED + "❌ Aún no tienes una billetera registrada.");
                player.sendMessage(ChatColor.RED + "💳 Crea una billetera usando /createwallet.");
            } else {
                player.sendMessage(ChatColor.RED + "❌ You do not yet have a registered wallet.");
                player.sendMessage(ChatColor.RED + "💳 Create a wallet using /createwallet.");
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
                signature = response.replaceFirst("(?s).*Signature: ", "").trim();
                signature = signature.replaceAll("\\n", ""); // Remove todas as quebras de linha
                signature = signature.replaceAll("\"}", ""); // Remove o fechamento JSON
                signature = signature.replace("\\n", "").replace("\\r", "");
                signature = signature.trim(); // Garante que espaços extras sejam removidos

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
                        ajustarSaldo(player, "give", gameCurrencyAmount);
                        if (lang.equals("pt-BR")) {
                            player.sendMessage(ChatColor.GREEN + "✅ Compra realizada com sucesso! " +
                                    ChatColor.GOLD + "Você recebeu " + gameCurrencyAmount + " moedas.");

                            player.sendMessage(ChatColor.AQUA + "💸 Transação registrada com assinatura: " +
                                    ChatColor.YELLOW + signature);
                        } else if (lang.equals("es-ES")) {
                            player.sendMessage(ChatColor.GREEN + "✅ Compra realizada con éxito! " +
                                    ChatColor.GOLD + "Recibiste " + gameCurrencyAmount + " monedas.");

                            player.sendMessage(ChatColor.AQUA + "💸 Transacción registrada con firma: " +
                                    ChatColor.YELLOW + signature);
                        } else {
                            player.sendMessage(ChatColor.GREEN + "✅ Purchase completed successfully! " +
                                    ChatColor.GOLD + "You received " + gameCurrencyAmount + " coins.");

                            player.sendMessage(ChatColor.AQUA + "💸 Transaction registered with signature: " +
                                    ChatColor.YELLOW + signature);
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

    public boolean hasWallet(Player player) {
        String username = player.getName();
        boolean exists = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String url = config.getString("database.url");
            String user = config.getString("database.user");
            String password = config.getString("database.password");

            connection = DriverManager.getConnection(url, user, password);

            String query = "SELECT endereco FROM carteiras WHERE jogador_id = (SELECT id FROM jogadores WHERE nome = ?)";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, username.trim());

            rs = stmt.executeQuery();

            exists = rs.next(); // Se existir um resultado, significa que a carteira já está registrada
        } catch (Exception e) {
            LOGGER.severe("Erro ao verificar carteira no banco: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                LOGGER.severe("Erro ao fechar conexão: " + e.getMessage());
            }
        }

        return exists;
    }




    // 📌 Método para criar uma carteira Solana para o jogador
    public void createWallet(Player player) {
        String playerName = player.getName().replace(" ", "_").toLowerCase();
        String walletPath = String.format("wallets/%s_wallet.json", playerName);
        PreparedStatement statement = null; // ✅ Declarado uma vez
        String lang = getPlayerLanguage(player);

        boolean hasWallet = hasWallet(player);



        if (hasWallet) { // ✅ Correto, pois `hasWallet(player)` retorna `true` ou `false`
            if (lang.equals("pt-BR")) {
                player.sendMessage(ChatColor.RED + "❌ Você já possui uma carteira registrada.");
            } else if (lang.equals("es-ES")) {
                player.sendMessage(ChatColor.RED + "❌ Ya tienes una billetera registrada.");
            } else {
                player.sendMessage(ChatColor.RED + "❌ You already have a registered wallet.");
            }
            return;
        }



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
                player.sendMessage(ChatColor.GREEN + "✅ Carteira criada com sucesso! Endereço: " + walletAddress);
                player.sendMessage(ChatColor.GOLD + "🛡️ Guarde sua frase secreta com segurança!");
                player.sendMessage(ChatColor.GREEN + "✅ SecretPhrase: " + (secretPhrase != null ? secretPhrase : "NULO"));
            } else if (lang.equals("es-ES")) {
                player.sendMessage(ChatColor.GREEN + "✅ Billetera creada con éxito! Dirección: " + walletAddress);
                player.sendMessage(ChatColor.GOLD + "🛡️ ¡Guarda tu frase secreta a salvo!");
                player.sendMessage(ChatColor.GREEN + "✅ Frase secreta: " + (secretPhrase != null ? secretPhrase : "NULO"));
            } else {
                player.sendMessage(ChatColor.GREEN + "✅ Wallet created successfully! Address: " + walletAddress);
                player.sendMessage(ChatColor.GOLD + "🛡️ Keep your secret phrase safe!");
                player.sendMessage(ChatColor.GREEN + "✅ SecretPhrase: " + (secretPhrase != null ? secretPhrase : "NULO"));
            }

        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "⚠ Erro ao criar a carteira: " + e.getMessage());
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

    // 📌 Método para ajustar o saldo do jogador do sql do plugin EssentialsX (nao e necessario mas tenta mater os dados iguais do sql e do mysql)

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

    // Método único para pegar a carteira e devolver SOL com juros
    public void refundSolana(Player player, String signature) {
        String lang = getPlayerLanguage(player);
        try {
            // 🔹 Verificar se já houve devolução para essa assinatura
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM livro_caixa WHERE assinatura = ? AND tipo_transacao = 'reembolso'");
            stmt.setString(1, signature);
            ResultSet rs = stmt.executeQuery();
            if (rs != null && rs.next() && rs.getInt(1) > 0) {
                if (lang.equals("pt-BR")) {
                    player.sendMessage(ChatColor.RED + "❌ Esse reembolso já foi processado anteriormente!");
                } else if (lang.equals("es-ES")) {
                    player.sendMessage(ChatColor.RED + "❌ Este reembolso ya ha sido procesado anteriormente!");
                } else {
                    player.sendMessage(ChatColor.RED + "❌ This refund has already been processed before!");
                }
                return;
            } else {
                System.out.println("Nenhum resultado encontrado!");
            }



            // 🔹 Verificar se a transação original foi do tipo "compra"

            stmt = connection.prepareStatement("SELECT tipo_transacao FROM livro_caixa WHERE assinatura = ?");
            stmt.setString(1, signature);
            rs = stmt.executeQuery();

            if (rs.next()) {

                String tipoTransacao = rs.getString("tipo_transacao");
                if (!tipoTransacao.equals("compra")) {
                    if (lang.equals("pt-BR")) {
                        player.sendMessage(ChatColor.RED + "❌ Apenas compras podem ser reembolsadas!");
                    } else if (lang.equals("es-ES")) {
                        player.sendMessage(ChatColor.RED + "❌ ¡Solo las compras pueden ser reembolsadas!");
                    } else {
                        player.sendMessage(ChatColor.RED + "❌ Only purchases can be refunded!");
                    }
                    return;
                }
            }

            // 🔹 Buscar transação original
            stmt = connection.prepareStatement(
                    "SELECT jogador, valor FROM livro_caixa WHERE assinatura = ?"
            );
            stmt.setString(1, signature);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                player.sendMessage(ChatColor.RED + "❌ Transação não encontrada!");
                return;
            }

            String playerName = rs.getString("jogador");
            double originalAmount = rs.getDouble("valor");

            // 🔹 Verificar se o valor da transação é menor que o mínimo
            double minSolAmount = 0.05; // Mínimo de 0.05 SOL
            if (originalAmount < minSolAmount) {
                player.sendMessage(ChatColor.RED + "❌ O valor da devolução é muito baixo! O mínimo permitido é " + minSolAmount + " SOL.");
                return;
            }

            // 🔹 Buscar saldo atual do jogador
            stmt = connection.prepareStatement(
                    "SELECT saldo FROM banco WHERE jogador = ?"
            );
            stmt.setString(1, playerName);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                player.sendMessage(ChatColor.RED + "❌ Saldo do jogador não encontrado no banco!");
                return;
            }

            double saldoAtual = rs.getDouble("saldo"); // Obtém saldo antes da alteração

            // 🔹 Buscar carteira do jogador
            stmt = connection.prepareStatement(
                    "SELECT endereco FROM carteiras WHERE jogador_id = (SELECT id FROM jogadores WHERE nome = ?)"
            );
            stmt.setString(1, playerName);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                player.sendMessage(ChatColor.RED + "❌ Carteira do jogador não encontrada!");
                return;
            }

            String playerWallet = rs.getString("endereco");

            // 🔹 Calcular valor com juros
            double interestRate = config.getDouble("store.interest_rate", 0.05);
            double refundAmount = originalAmount * (1 - interestRate);

            // 🔹 Converter valor de SOL para moedas do jogo
            int moedasParaRemover = (int) (refundAmount * 1000);

            // 🔹 Atualizar saldo do jogador no banco de dados
            stmt = connection.prepareStatement(
                    "UPDATE banco SET saldo = saldo - ? WHERE jogador = ?"
            );
            stmt.setDouble(1, moedasParaRemover);
            stmt.setString(2, playerName);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                player.sendMessage(ChatColor.RED + "❌ Erro ao atualizar saldo no banco.");
                return;
            }

            // 🔹 Executar transferência via API
            String host = config.getString("docker.host");
            String apiwebkey = config.getString("docker.api_web_key");
            String wallet_bank = config.getString("docker.wallet_bank_store_admin");

            String formattedAmount = String.format("%.2f", refundAmount).replace(",", ".");

            String comando = String.format(
                    "solana transfer %s %s --keypair /solana-token/%s.json --allow-unfunded-recipient",
                    playerWallet, formattedAmount, wallet_bank
            );

            String url = String.format("http://%s/consulta.php?apikey=%s&comando=%s", host, apiwebkey, URLEncoder.encode(comando, "UTF-8"));

            String response = executeHttpGet(url);

            System.out.println("[DEBUG SOLANA REFUND RESPONSE]: " + response);

            // 🔹 Registrar devolução no banco e enviar mensagem ao jogador
            if (response.contains("\"status\":\"success\"")) {
                stmt = connection.prepareStatement(
                        "INSERT INTO livro_caixa (jogador, tipo_transacao, valor, moeda, assinatura) VALUES (?, ?, ?, ?, ?)"
                );
                stmt.setString(1, playerName);
                stmt.setString(2, "reembolso");
                stmt.setDouble(3, refundAmount);
                stmt.setString(4, "SOL");
                stmt.setString(5, signature);
                stmt.executeUpdate();

                // 🔹 Aviso sobre o reembolso e taxa
                player.sendMessage(ChatColor.YELLOW + "🔹 Ao solicitar um reembolso, há uma taxa de juros de " + (interestRate * 100) + "% aplicada.");
                player.sendMessage(ChatColor.RED + "📉 Isso significa que você receberá " + refundAmount + " SOL em vez do valor total.");
                player.sendMessage(ChatColor.GOLD + "💰 Essa taxa garante a estabilidade do sistema e evita prejuízos à casa.");

                // 🔹 Mensagem no chat do jogo mostrando saldo atualizado
                double novoSaldo = saldoAtual - moedasParaRemover;
                player.sendMessage(ChatColor.GREEN + "✅ Sua devolução foi concluída com sucesso!");
                player.sendMessage(ChatColor.GOLD + "💰 Valor recebido: " + refundAmount + " SOL");
                player.sendMessage(ChatColor.AQUA + "💳 Seu novo saldo: " + novoSaldo + " moedas.");

            } else {
                player.sendMessage(ChatColor.RED + "❌ Falha na devolução: " + response);
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "⚠ Erro ao processar a devolução: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void transferirMoeda(Player player, String destinatario, double valor) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + destinatario + " " + valor);
        player.sendMessage("Você transferiu " + valor + " moedas para " + destinatario);
    }

    public void transferirMoedaBanco(String jogador, String destinatario, double valor) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + destinatario + " " + valor);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco take " + jogador + " " + valor);
    }

}
