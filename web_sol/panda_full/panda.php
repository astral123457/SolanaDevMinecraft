<?php
// Configurações do banco de dados
$host = "192.168.100.170";
$user = "root";
$password = "0073007";
$dbname = "panda_full_db";
$conn = new mysqli($host, $user, $password, $dbname);

if ($conn->connect_error) {
    die("Erro ao conectar: " . $conn->connect_error);
}

// Nome e endereço da moeda
define("TOKEN_NAME", "Panda Full");
define("TOKEN_ADDRESS", "mntjKpj39H1JJD9vR2Brvt2PPFkoLgbixpoNUYQXtu2");

// Função para verificar saldo
function getTokenBalance() {
    $command = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana spl-token balance " . TOKEN_ADDRESS . " 2>&1";
    return shell_exec($command);
}

// Função para realizar transferência
function transferBalance($recipient) {
    $command = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana spl-token transfer " . TOKEN_ADDRESS . " 10 $recipient --fund-recipient --allow-unfunded-recipient 2>&1";
    return shell_exec($command);
}

// Função para verificar intervalo de 8 horas e atividade
function canTransfer($wallet_address, $conn) {
    $query = "SELECT last_transfer, completed_activity FROM transfer_log WHERE wallet_address = '$wallet_address'";
    $result = $conn->query($query);

    if ($result && $result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $last_transfer_time = strtotime($row['last_transfer']);
        $current_time = time();
        $can_transfer = ($current_time - $last_transfer_time >= 8 * 3600); // Verifica 8 horas
        return [$can_transfer, $row['completed_activity']];
    }

    return [true, false]; // Sem registro, pode transferir e a atividade não foi concluída
}

// Função para registrar transferência e atividade
function logTransfer($wallet_address, $conn) {
    $current_time = date("Y-m-d H:i:s");
    $query = "INSERT INTO transfer_log (wallet_address, last_transfer, completed_activity) VALUES ('$wallet_address', '$current_time', TRUE)
              ON DUPLICATE KEY UPDATE last_transfer='$current_time', completed_activity=TRUE";
    $conn->query($query);
}

// Variáveis para saldo e mensagens
$balance_output = "";
$message = "";

// Processar ações
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    if (isset($_POST["view_balance"])) {
        $balance_output = getTokenBalance();
    } elseif (isset($_POST["transfer"])) {
        $recipient = $_POST["recipient"];
        list($can_transfer, $activity_completed) = canTransfer($recipient, $conn);

        if ($activity_completed) {
            $message = "A atividade para esta carteira já foi concluída. Aguarde 8 horas para uma nova transferência.";
        } elseif ($can_transfer) {
            $result = transferBalance($recipient);
            logTransfer($recipient, $conn);
            $message = "Transferência realizada com sucesso!";
        } else {
            $message = "O tempo ainda não está liberado. Você precisa aguardar para realizar a atividade.";
        }
    }
}

// Fechar conexão
$conn->close();
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panda Full</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 0; }
        header { background-color: #4CAF50; color: white; padding: 1rem; text-align: center; }
        main { margin: 2rem auto; padding: 2rem; background: white; border-radius: 10px; max-width: 600px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }
        form { display: flex; flex-direction: column; gap: 1rem; }
        input, button { padding: 0.5rem; font-size: 1rem; border: 1px solid #ccc; border-radius: 5px; }
        button { background-color: #4CAF50; color: white; cursor: pointer; }
        button:hover { background-color: #45a049; }
        .result { margin-top: 1rem; padding: 1rem; background-color: #f9f9f9; border: 1px solid #ddd; border-radius: 5px; }
    </style>
</head>
<body>
    <header>
        <h1>Gerencie a Moeda Panda Full</h1>
    </header>
    <main>
        <!-- Formulário para verificar saldo -->
        <form method="POST">
            <h3>Ver Saldo</h3>
            <button type="submit" name="view_balance">Ver Saldo</button>
        </form>
        
        <?php if (!empty($balance_output)): ?>
        <div class="result">
            <h4>Saldo da Moeda:</h4>
            <pre><?php echo $balance_output; ?></pre>
        </div>
        <?php endif; ?>

        <!-- Formulário para transferência -->
        <form method="POST">
            <h3>Transferir Tokens</h3>
            <label for="recipient">Para (Endereço da Carteira):</label>
            <input type="text" id="recipient" name="recipient" required>
            <button type="submit" name="transfer">Transferir 10 Panda Full</button>
        </form>

        <?php if ($message): ?>
        <div class="result">
            <h4>Mensagem:</h4>
            <pre><?php echo $message; ?></pre>
        </div>
        <?php endif; ?>
    </main>
</body>
</html>