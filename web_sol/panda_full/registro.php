<?php
// Conexão com o banco de dados
include "connection.php";

$conn = new mysqli($host, $user, $password, $dbname);

if ($conn->connect_error) {
    die("Erro ao conectar ao banco de dados: " . $conn->connect_error);
}

// Processar registro de usuário
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $username = $_POST["username"];
    $key_value = $_POST["key"];
    $password = password_hash($_POST["password"], PASSWORD_DEFAULT); // Hash de segurança

    $stmt = $conn->prepare("INSERT INTO users (user, key_value, password) VALUES (?, ?, ?)");
    $stmt->bind_param("sss", $username, $key_value, $password);

    if ($stmt->execute()) {
        echo "Usuário registrado com sucesso!";
    } else {
        echo "Erro ao registrar: " . $conn->error;
    }
}
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro</title>
    <link rel="stylesheet" href="styles.css">
    <script src="scripts.js" defer></script>
</head>
<body>
    <div class="container">
        <h1>Cadastro</h1>
        <form method="POST">
            <label for="username">Usuário:</label>
            <input type="text" id="username" name="username" required>
            
            <label for="key">Chave:</label>
            <input type="text" id="key" name="key" required>
            
            <label for="password">Senha:</label>
            <input type="password" id="password" name="password" required>
            
            <button type="submit">Registrar</button>
        </form>
    </div>
</body>
</html>