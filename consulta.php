<?php
// Verifica se a solicitação é do método GET
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    // Verifica se o parâmetro 'comando' foi enviado
    if (isset($_GET['comando'])) {
        $enderecoCarteira = escapeshellcmd($_GET['comando']); // Escapa o comando para evitar injeção maliciosa

        // Adiciona registro de log do comando
        file_put_contents('/path/to/log.txt', date('Y-m-d H:i:s') . " Comando recebido: " . $comando . "\n", FILE_APPEND);

        // Captura saída e código de retorno
        $output = [];
        $codigoRetorno = 0;
		
		//$enderecoCarteira = "GcT1enHwJh67SQrocpi2rgHr8jpx1BiyGjj5dsiSTEoY";
		
		$comando = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana solana balance $enderecoCarteira";

        // Executa o comando
        exec($comando, $output, $codigoRetorno);
		

        // Verifica sucesso ou erro na execução do comando
        if ($codigoRetorno === 0) {
            echo json_encode(['status' => 'success', 'output' => implode("\n", $output)]);
        } else {
            echo json_encode([
                'status' => 'error',
                'message' => 'Falha ao executar o comando.',
                'output' => implode("\n", $output)
            ]);
        }
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Parâmetro "comando" não fornecido.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Método inválido. Use GET.']);
}
?>