<?php
// ---- INÍCIO DA SEÇÃO DA CHAVE DE API ----
// Define a frase secreta para gerar a chave de API esperada
$fraseSecreta = "banana";

// Gera a chave de API esperada (64 bits em hexadecimal a partir da frase)
// SHA256("banana") -> primeiros 8 bytes -> hexadecimal
$hashBinarioCompletoEsperado = hash('sha256', $fraseSecreta, true);
$hashBinario64BitsEsperado = substr($hashBinarioCompletoEsperado, 0, 8); // 8 bytes = 64 bits
$chaveApiCorreta = bin2hex($hashBinario64BitsEsperado); // Deve resultar em '2f4164616e614e61'

// Define o tipo de conteúdo para todas as respostas JSON
header('Content-Type: application/json');

// Verifica se o parâmetro 'apikey' foi enviado na URL
if (!isset($_GET['apikey'])) {
    echo json_encode(['status' => 'error', 'message' => 'Parâmetro "apikey" não fornecido.']);
    exit;
}

$apiKeyFornecida = $_GET['apikey'];

// Compara a chave de API fornecida com a chave correta de forma segura
// hash_equals() é usado para ajudar a prevenir ataques de tempo (timing attacks)
if (!hash_equals($chaveApiCorreta, $apiKeyFornecida)) {
    echo json_encode(['status' => 'error', 'message' => 'Chave de API inválida.']);
    exit;
}
// ---- FIM DA SEÇÃO DA CHAVE DE API ----

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
$comandoRecebido = urldecode(trim($_GET['comando']));

// Evita caracteres perigosos que podem causar injeção de código ou execuções maliciosas
// Esta é uma proteção básica. Para maior segurança, considere uma lista de permissões de comandos.
if (preg_match('/[;&|`]/', $comandoRecebido)) {
    echo json_encode(['status' => 'error', 'message' => 'Comando contém caracteres proibidos (detectado por preg_match).']);
    exit;
}

// Adiciona registro de log do comando recebido (para depuração)
// Certifique-se de que o diretório e o arquivo de log têm permissões de escrita para o usuário do servidor web.
$logFile = '/home/astral/logs/consulta_log.txt'; // ATENÇÃO: Verifique as permissões deste arquivo/diretório
$logMessage = date('Y-m-d H:i:s') . " - Chave API Verificada - Comando recebido: " . $comandoRecebido . "\n";
@file_put_contents($logFile, $logMessage, FILE_APPEND); // O '@' suprime erros de escrita no log, mas pode ser útil removê-lo para depuração.

// Define variáveis para captura de saída e código de retorno
$output = [];
$codigoRetorno = -1; // Inicializa com um valor que indique erro por padrão

// Prepara o comando para execução, escapando argumentos que podem ser interpretados pelo shell.
// ATENÇÃO: escapeshellcmd() é útil, mas a segurança da execução de comandos externos,
// especialmente com sudo e docker, depende de um design de sistema muito cuidadoso.
$comandoSeguroParaExec = escapeshellcmd($comandoRecebido);

// Monta o comando Docker completo
// ALERTA DE SEGURANÇA MÁXIMO: Executar `sudo` e `docker run` com entrada do usuário é extremamente perigoso.
// Certifique-se que o usuário `www-data` no `sudoers` só pode executar este comando docker específico e nada mais.
$fullDockerCommand = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana " . $comandoSeguroParaExec;

// Log do comando exato a ser executado (para auditoria/depuração)
@file_put_contents($logFile, date('Y-m-d H:i:s') . " - Executando Docker: " . $fullDockerCommand . "\n", FILE_APPEND);

// Executa o comando dentro do contêiner Docker
exec($fullDockerCommand, $output, $codigoRetorno);

// Retorno da execução
if ($codigoRetorno === 0) {
    echo json_encode(['status' => 'success', 'output' => implode("\n", $output)]);
} else {
    echo json_encode([
        'status' => 'error',
        'message' => 'Falha ao executar o comando ou comando retornou erro.',
        'output' => implode("\n", $output),
        'return_code' => $codigoRetorno
    ]);
}
?>
