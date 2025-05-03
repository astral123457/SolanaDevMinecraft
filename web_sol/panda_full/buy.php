<?php
define("RECEIVING_WALLET", "dadhcDXHiHDrWkT2Z4pSZyF6HWmHwQMG3HtGciwccVP");

function receivePayment($payer_address, $amount) {
    $command = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana spl-token transfer $payer_address $amount " . RECEIVING_WALLET . " --fund-recipient --allow-unfunded-recipient 2>&1";
    return shell_exec($command);
}

// Registrar compra
$message = "";
if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST["pay"])) {
    $payer_address = $_POST["payer_address"];
    $amount = $_POST["amount"];

    $result = receivePayment($payer_address, $amount);
    $message = "Pagamento recebido: $result";
}
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pagamento - Panda Full</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; }
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
        <h1>Pague com Panda Full</h1>
    </header>
    <main>
        <form method="POST">
            <label for="payer_address">Sua Carteira:</label>
            <input type="text" id="payer_address" name="payer_address" required>
            <label for="amount">Quantidade:</label>
            <input type="number" id="amount" name="amount" required>
            <button type="submit" name="pay">Pagar</button>
        </form>
        
        <div class="result">
            <?php if ($message) echo $message; ?>
        </div>
    </main>
</body>
</html>