<?php
// connection.php

// Configuração do banco de dados
$host = "debian.tail561849.ts.net"; // Seu hostname MariaDB na tailnet
$user = "root";                    // Usuário do seu banco de dados
$password = "0073007";             // Senha do seu banco de dados
$dbname = "panda_full_db";         // Nome do seu banco de dados

// Caminhos para os certificados do CLIENTE e CA.
// Estes são os certificados autoassinados que você gerou.
$ssl_ca = "/etc/mysql/ssl/ca.pem";
$ssl_cert = "/etc/mysql/ssl/server-cert.pem";
$ssl_key = "/etc/mysql/ssl/server-key.pem";

try {
    // Configuração da conexão com SSL
    // PDO::MYSQL_ATTR_SSL_VERIFY_SERVER_CERT => true é crucial para validar o certificado do servidor
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $password, [
        PDO::MYSQL_ATTR_SSL_CA => $ssl_ca,
        PDO::MYSQL_ATTR_SSL_CERT => $ssl_cert,
        PDO::MYSQL_ATTR_SSL_KEY => $ssl_key,
        PDO::MYSQL_ATTR_SSL_VERIFY_SERVER_CERT => true, // Garante que o PHP valide o certificado do servidor
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION      // Lança exceções em caso de erro
    ]);

    // Opcional: Para verificar que a conexão foi segura, você pode fazer uma consulta leve
    // e verificar o status SSL. Não é necessário para a conexão em si.
    // $ssl_status = $pdo->query("SHOW STATUS LIKE 'Ssl_cipher'")->fetch(PDO::FETCH_ASSOC);
    // if ($ssl_status && $ssl_status['Value'] !== '') {
    //     error_log("Conexão MariaDB: SSL ativo com cifra: " . $ssl_status['Value']);
    // } else {
    //     error_log("Conexão MariaDB: SSL não ativo ou falhou na negociação.");
    // }

} catch (PDOException $e) {
    // É uma boa prática não expor mensagens de erro detalhadas do banco de dados
    // diretamente no ambiente de produção. Use error_log para registrar.
    error_log("Erro na conexão PDO ao banco de dados: " . $e->getMessage());
    die(json_encode(['status' => 'error', 'message' => 'Erro interno do servidor ao conectar ao banco de dados.']));
}