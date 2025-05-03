<?php
// Configurações do banco de dados
$host = "192.168.100.170";
$user = "root";
$password = "0073007";
$dbname = "panda_full_db";

$conn = new mysqli($host, $user, $password, $dbname);

if ($conn->connect_error) {
    die("Erro ao conectar: " . $conn->connect_error);
}

$message = "";

// Registrar atividade
if ($_SERVER["REQUEST_METHOD"] === "POST" && isset($_POST["mark_activity"])) {
    $user_id = $_POST["user_id"];
    $query = "INSERT INTO user_activity (user_id, activity_done) VALUES ('$user_id', TRUE)
              ON DUPLICATE KEY UPDATE activity_done = TRUE";
    if ($conn->query($query) === TRUE) {
        $message = "Atividade registrada com sucesso!";
    } else {
        $message = "Erro ao registrar atividade: " . $conn->error;
    }
}

// Fechar conexão
$conn->close();
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrar Atividade</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 0; }
        header { background-color: #4CAF50; color: white; padding: 1rem; text-align: center; }
        main { margin: 2rem auto; padding: 2rem; background: white; border-radius: 10px; max-width: 600px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }
        form { display: flex; flex-direction: column; gap: 1rem; }
        input, button { padding: 0.5rem; font-size: 1rem; border: 1px solid #ccc; border-radius: 5px; }
        button { background-color: #4CAF50; color: white; cursor: pointer; }
        button:hover { background-color: #45a049; }
        .message { margin-top: 1rem; padding: 1rem; background-color: #f9f9f9; border: 1px solid #ddd; border-radius: 5px; }
    </style>
</head>
<body>
    <header>
        <h1>Registrar Atividade</h1>
    </header>
    <main>
        <form method="POST">
            <label for="user_id">ID do Usuário:</label>
            <input type="text" id="user_id" name="user_id" required>
            <button type="submit" name="mark_activity">Registrar Atividade</button>
        </form>
        
        <div class="message">
            <?php if ($message) echo $message; ?>
        </div>
    </main>
</body>
</html>