<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transferência Solana</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f9;
            color: #333;
        }
        .container {
            max-width: 600px;
            margin: 50px auto;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
            color: #0056b3;
        }
        label {
            display: block;
            margin: 10px 0 5px;
        }
        input {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            width: 100%;
            padding: 10px;
            background-color: #0056b3;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
        }
        button:hover {
            background-color: #003f7f;
        }
        .result {
            margin-top: 20px;
            padding: 15px;
            background: #e7f4e4;
            color: #2d6a4f;
            border-radius: 5px;
            display: none;
        }
        .error {
            background: #fddede;
            color: #d93025;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Transferência Solana</h1>
        <form id="transferForm">
            <label for="sender">Carteira Remetente:</label>
            <input type="text" id="sender" name="sender" placeholder="Ex.: DOYZEL" required>
            
            <label for="recipientWallet">Carteira Destinatário:</label>
            <input type="text" id="recipientWallet" name="recipientWallet" placeholder="Ex.: 7PJBFH7sR..." required>
            
            <label for="amount">Quantidade (SOL):</label>
            <input type="number" step="0.01" id="amount" name="amount" placeholder="Ex.: 0.05" required>
            
            <button type="button" id="sendRequest">Enviar Transferência</button>
        </form>
        <div id="result" class="result"></div>
    </div>

    <script>
        document.getElementById("sendRequest").addEventListener("click", function() {
            const sender = document.getElementById("sender").value;
            const recipientWallet = document.getElementById("recipientWallet").value;
            const amount = document.getElementById("amount").value;
            const resultDiv = document.getElementById("result");

            // URL para o endpoint
            const url = `http://192.168.100.170/transfer.php?sender=${sender}&recipientWallet=${recipientWallet}&amount=${amount}`;

            // Fazendo a requisição GET com JavaScript
            fetch(url)
                .then(response => response.json())
                .then(data => {
                    if (data.status === "success") {
                        resultDiv.textContent = `Sucesso! Assinatura da transação: ${data.signature}`;
                        resultDiv.className = "result";
                        resultDiv.style.display = "block";
                    } else {
                        resultDiv.textContent = `Erro: ${data.message}`;
                        resultDiv.className = "result error";
                        resultDiv.style.display = "block";
                    }
                })
                .catch(error => {
                    resultDiv.textContent = "Erro na conexão com o servidor.";
                    resultDiv.className = "result error";
                    resultDiv.style.display = "block";
                });
        });
    </script>
</body>
</html>