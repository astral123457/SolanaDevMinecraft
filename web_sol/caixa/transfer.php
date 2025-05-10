<?php
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    // Verifica se os parâmetros necessários foram enviados
    if (isset($_GET['sender']) && isset($_GET['recipientWallet']) && isset($_GET['amount'])) {
        // Validação dos parâmetros
        if (!is_numeric($_GET['amount']) || $_GET['amount'] <= 0) {
            echo json_encode(['status' => 'error', 'message' => 'O valor "amount" deve ser um número positivo.']);
            exit;
        }

        if (empty($_GET['sender']) || empty($_GET['recipientWallet'])) {
            echo json_encode(['status' => 'error', 'message' => 'Os campos "sender" e "recipientWallet" não podem estar vazios.']);
            exit;
        }

        // Sanitização dos parâmetros
        $sender = escapeshellarg(trim($_GET['sender']));
        $recipientWallet = escapeshellarg(trim($_GET['recipientWallet']));
        $amount = escapeshellarg(trim($_GET['amount']));

        // Configurações do Docker
        $basePath = '/home/astral/astralcoin'; // Substitua pelo valor correto
        $solanaCommand = 'heysolana'; // Substitua pelo valor correto

        // Constrói o comando Docker
        $command = sprintf(
            'sudo docker run --rm -v %s:/solana-token -v %s/solana-data:/root/.config/solana %s solana transfer %s %s --keypair /solana-token/wallets/%s_wallet.json --allow-unfunded-recipient',
            escapeshellarg($basePath),
            escapeshellarg($basePath),
            escapeshellarg($solanaCommand),
            $recipientWallet,
            $amount,
            trim($_GET['sender'])
        );

        // Registro do comando para auditoria
        file_put_contents('/var/log/solana_transfer.log', date('Y-m-d H:i:s') . " Comando executado: $command\n", FILE_APPEND);

        // Executa o comando e captura a saída
        $output = [];
        $returnCode = 0;
        exec($command, $output, $returnCode);

        // Verifica o resultado do comando
        if ($returnCode === 0) {
            // Extrai a assinatura da transação
            $signature = null;
            foreach ($output as $line) {
                if (strpos($line, 'Signature: ') === 0) {
                    $signature = trim(substr($line, 10));
                    break;
                }
            }

            if ($signature) {
                echo json_encode(['status' => 'success', 'signature' => $signature]);
            } else {
                echo json_encode(['status' => 'error', 'message' => 'Assinatura não encontrada na saída.']);
            }
        } else {
            echo json_encode([
                'status' => 'error',
                'message' => 'Erro ao executar o comando.',
                'output' => $output,
                'returnCode' => $returnCode
            ]);
        }
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Parâmetros "sender", "recipientWallet" e "amount" são obrigatórios.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Método inválido. Use GET.']);
}


?>