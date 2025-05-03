<?php
// Configurações do banco de dados
$host = "192.168.100.170";
$user = "root";
$password = "0073007";
$dbname = "panda_full_db";

// Conexão com o banco de dados
$conn = new mysqli($host, $user, $password);

if ($conn->connect_error) {
    die("Erro na conexão: " . $conn->connect_error);
}

// Criar banco de dados se não existir
$sql = "CREATE DATABASE IF NOT EXISTS $dbname";
$conn->query($sql);

// Selecionar banco de dados
$conn->select_db($dbname);

// Criar tabela transfer_log se não existir
$sql = "CREATE TABLE IF NOT EXISTS transfer_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    wallet_address VARCHAR(255) NOT NULL,
    last_transfer DATETIME NOT NULL
)";
$conn->query($sql);

// Criar tabela user_activity se não existir
$sql = "CREATE TABLE IF NOT EXISTS user_activity (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    activity_done BOOLEAN DEFAULT FALSE,
    last_transfer DATETIME DEFAULT NULL
)";
$conn->query($sql);

// Fechar conexão
$conn->close();
?>