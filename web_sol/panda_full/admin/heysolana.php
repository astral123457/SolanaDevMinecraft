<?php
session_start();

if (!isset($_SESSION["user"])) {
    header("Location: ./login.php"); // Redireciona para o login se não estiver logado
    exit();
}
?>

<?php
// Caminho do diretório correto
$workingDir = '/home/astral/astralcoin';

// Comando para verificar saldo
function getBalance($walletAddress) {
    $command = "sudo docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana solana balance $walletAddress 2>&1";
    return shell_exec($command);
}

// Comando para transferir saldo
function transferBalance($sender, $recipient, $amount) {
    $command = "sudo docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana spl-token transfer $sender $amount $recipient --fund-recipient --allow-unfunded-recipient 2>&1";
    return shell_exec($command);
}

// Captura os dados do formulário
$action = $_POST['action'] ?? '';
$balance = null;
$result = null;

if ($action == 'getBalance') {
    $walletAddress = $_POST['walletAddress'] ?? '';
    $balance = getBalance($walletAddress);
} elseif ($action == 'transfer') {
    $sender = $_POST['sender'] ?? '';
    $recipient = $_POST['recipient'] ?? '';
    $amount = $_POST['amount'] ?? '';
    $result = transferBalance($sender, $recipient, $amount);
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HeySolana</title>
	<link rel="stylesheet" href="logout.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            margin: 0;
            padding: 0;
        }
        header {
            background-color: #4CAF50;
            color: white;
            padding: 1rem;
            text-align: center;
        }
        main {
            margin: 2rem auto;
            padding: 2rem;
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            max-width: 600px;
        }
        form {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        input[type="text"], input[type="number"], button {
            padding: 0.5rem;
            font-size: 1rem;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        button {
            background-color: #4CAF50;
            color: white;
            cursor: pointer;
        }
        button:hover {
            background-color: #45a049;
        }
        .result {
            margin-top: 1rem;
            padding: 1rem;
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
		
		
    </style>
</head>
<body>
    <header>
        <h1>HeySolana</h1><button class="Btn">
  
  <div class="sign"><svg viewBox="0 0 512 512"><path d="M377.9 105.9L500.7 228.7c7.2 7.2 11.3 17.1 11.3 27.3s-4.1 20.1-11.3 27.3L377.9 406.1c-6.4 6.4-15 9.9-24 9.9c-18.7 0-33.9-15.2-33.9-33.9l0-62.1-128 0c-17.7 0-32-14.3-32-32l0-64c0-17.7 14.3-32 32-32l128 0 0-62.1c0-18.7 15.2-33.9 33.9-33.9c9 0 17.6 3.6 24 9.9zM160 96L96 96c-17.7 0-32 14.3-32 32l0 256c0 17.7 14.3 32 32 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32l-64 0c-53 0-96-43-96-96L0 128C0 75 43 32 96 32l64 0c17.7 0 32 14.3 32 32s-14.3 32-32 32z"></path></svg></div>
  
  <div class="text"><a href="../logout.php" style="color: white;">Sair</a></div>
</button>
        <p>Gerencie sua carteira Solana com facilidade!</p>
    </header>
    <main>
        <!-- Formulário para verificar saldo -->
        <form method="POST" action="heysolana.php">
            <h3>Verificar Saldo</h3>
            <label for="walletAddress">Endereço da Carteira:</label>
            <input type="text" id="walletAddress" name="walletAddress" required>
            <input type="hidden" name="action" value="getBalance">
            <button type="submit">Verificar Saldo</button>
        </form>

        <!-- Formulário para realizar transferência -->
        <form method="POST" action="heysolana.php">
            <h3>Transferência</h3>
            <label for="sender">De (Endereço):</label>
            <input type="text" id="sender" name="sender" required>
            <label for="recipient">Para (Endereço):</label>
            <input type="text" id="recipient" name="recipient" required>
            <label for="amount">Quantidade:</label>
            <input type="number" id="amount" name="amount" required>
            <input type="hidden" name="action" value="transfer">
            <button type="submit">Transferir</button>
        </form>

        <!-- Exibição dos resultados -->
        <div class="result">
            <?php
            if (!is_null($balance)) {
                echo "<h4>Saldo:</h4><pre>$balance</pre>";
            }
            if (!is_null($result)) {
                echo "<h4>Resultado da Transferência:</h4><pre>$result</pre>";
            }
            ?>
        </div>
    </main>
</body>
</html>