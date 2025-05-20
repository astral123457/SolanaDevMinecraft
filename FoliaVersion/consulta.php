<?php
// ---- Configuração do Banco de Dados ----
$host = 'localhost';
$dbname = 'banco';
$username = 'root';
$password = '0073007';

try {
    // Conectar ao banco de dados
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Criar tabelas automaticamente
    $pdo->exec("
        CREATE TABLE IF NOT EXISTS ban_ip (
            id INT AUTO_INCREMENT PRIMARY KEY,
            key_genere VARCHAR(255),
            name VARCHAR(255),
            ip VARCHAR(45) UNIQUE,
            data TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS access_attempts (
            ip VARCHAR(45) PRIMARY KEY,
            attempts INT DEFAULT 0,
            last_attempt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        );
    ");
} catch (PDOException $e) {
    error_log("Erro ao conectar ao banco de dados: " . $e->getMessage());
    die(json_encode(['status' => 'error', 'message' => 'Error connecting to database.']));
}

// ---- Captura o IP do usuário ----
$ipUsuario = $_SERVER['REMOTE_ADDR'];
header('Content-Type: application/json');

// ---- Verificação do IP banido ----
$stmt = $pdo->prepare("SELECT * FROM ban_ip WHERE ip = :ip");
$stmt->execute(['ip' => $ipUsuario]);
if ($stmt->fetch()) {
    echo json_encode(['status' => 'error', 'message' => 'Your IP is banned due to invalid attempts.']);
    exit;
}

// ---- Autenticação da API ----
$fraseSecreta = "banana";
$hashBinarioCompletoEsperado = hash('sha256', $fraseSecreta, true);
$hashBinario64BitsEsperado = substr($hashBinarioCompletoEsperado, 0, 8);
$chaveApiCorreta = bin2hex($hashBinario64BitsEsperado);

if (!isset($_GET['apikey']) || !hash_equals($chaveApiCorreta, $_GET['apikey'])) {
    // Atualiza tentativas de acesso
    $stmt = $pdo->prepare("INSERT INTO access_attempts (ip, attempts, last_attempt) 
                           VALUES (:ip, 1, NOW()) 
                           ON DUPLICATE KEY UPDATE attempts = attempts + 1, last_attempt = NOW()");
    $stmt->execute(['ip' => $ipUsuario]);

    // Verifica se deve banir o IP após 2 falhas
    $stmt = $pdo->prepare("SELECT attempts FROM access_attempts WHERE ip = :ip");
    $stmt->execute(['ip' => $ipUsuario]);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($result && $result['attempts'] >= 2) {
        $stmtBan = $pdo->prepare("INSERT INTO ban_ip (key_genere, name, ip) VALUES ('API_SECURITY', 'Tentativas Excessivas', :ip)");
        $stmtBan->execute(['ip' => $ipUsuario]);

        echo json_encode(['status' => 'error', 'message' => 'Your IP has been banned for excessive attempts.']);
        exit;
    }

    echo json_encode(['status' => 'error', 'message' => 'Invalid API key.']);
    exit;
}

// ---- Processamento do Comando ----
if ($_SERVER['REQUEST_METHOD'] !== 'GET' || !isset($_GET['comando'])) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid method or command not provided.']);
    exit;
}

$comandoRecebido = urldecode(trim($_GET['comando']));

// Proteção contra caracteres perigosos no comando
if (preg_match('/[;&|`]/', $comandoRecebido)) {
    echo json_encode(['status' => 'error', 'message' => 'Command contains prohibited characters.']);
    exit;
}

// ---- Registrar o comando no log ----
$logFile = '/home/astral/logs/consulta_log.txt';
$logEntry = date('Y-m-d H:i:s') . " - Comando recebido: " . $comandoRecebido . "\n";

if (!file_put_contents($logFile, $logEntry, FILE_APPEND)) {
    error_log("Falha ao escrever no log: $logFile");
}

// ---- Execução do comando via Docker ----
$comandoSeguroParaExec = escapeshellcmd($comandoRecebido);
//$fullDockerCommand = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana " . $comandoSeguroParaExec;
$fullDockerCommand = "sudo -u www-data docker run --rm -v /root/solana:/solana-token -v /root/solana/solana-data:/root/.config/solana heysolana " . $comandoSeguroParaExec;

exec($fullDockerCommand, $output, $codigoRetorno);

if ($codigoRetorno === 0) {
    echo json_encode(['status' => 'success', 'output' => implode("\n", $output)]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Erro ao executar comando.', 'output' => implode("\n", $output), 'return_code' => $codigoRetorno]);
}

// ---- Limpeza automática de IPs banidos após 24 horas ----
$pdo->exec("DELETE FROM ban_ip WHERE data < NOW() - INTERVAL 24 HOUR");

?>
