<?php
// Verifica se a solicitação é do método GET
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    echo json_encode(['status' => 'error', 'message' => 'Método inválido. Use GET.']);
    exit;
}

// Verifica se o parâmetro 'comando' foi enviado
if (!isset($_GET['comando'])) {
    echo json_encode(['status' => 'error', 'message' => 'Parâmetro "comando" não fornecido.']);
    exit;
}

// Decodifica caracteres da URL e remove espaços extras
$comando = urldecode(trim($_GET['comando']));

// Evita caracteres perigosos que podem causar injeção de código ou execuções maliciosas
if (preg_match('/[;&|`]/', $comando)) {
    echo json_encode(['status' => 'error', 'message' => 'Comando contém caracteres proibidos.']);
    exit;
}

// Adiciona registro de log do comando recebido (para depuração)
file_put_contents('/home/astral/logs/consulta_log.txt', date('Y-m-d H:i:s') . " Comando recebido: " . $comando . "\n", FILE_APPEND);

// Define variáveis para captura de saída e código de retorno
$output = [];
$codigoRetorno = 0;

// Executa o comando dentro do contêiner Docker
exec("sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana $comando", $output, $codigoRetorno);

// Retorno da execução
if ($codigoRetorno === 0) {
    echo json_encode(['status' => 'success', 'output' => implode("\n", $output)]);
} else {
    echo json_encode([
        'status' => 'error',
        'message' => 'Falha ao executar o comando.',
        'output' => implode("\n", $output)
    ]);
}
?>