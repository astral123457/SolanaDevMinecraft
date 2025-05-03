<?php
session_start(); // Inicia a sessão

// Conexão com o banco de dados
include 'connection.php';

$conn = new mysqli($host, $user, $password, $dbname);

if ($conn->connect_error) {
    die("Erro ao conectar ao banco de dados: " . $conn->connect_error);
}

// Inicializamos a mensagem vazia
$error_message = "";

// Processar login
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $username = $_POST["username"];
    $password = $_POST["password"];

    $stmt = $conn->prepare("SELECT password FROM users WHERE user = ?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->bind_result($hashed_password);
    $stmt->fetch();

    if (password_verify($password, $hashed_password)) {
        $_SESSION["user"] = $username;
        header("Location: protected_page.php");
        exit();
    } else {
        $error_message = "Usuário ou senha inválidos"; // Define a mensagem de erro
    }
}
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background: linear-gradient(135deg, #4CAF50, #81C784);
        }

        .container {
            background: #fff;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.2);
            max-width: 400px;
            width: 100%;
        }

        h1 {
            text-align: center;
            margin-bottom: 1rem;
            color: #4CAF50;
            font-size: 1.8rem;
        }

        form {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }

        label {
            font-weight: bold;
            margin-bottom: 0.5rem;
        }

        input {
            padding: 0.8rem;
            font-size: 1rem;
            border: 1px solid #ccc;
            border-radius: 5px;
            transition: all 0.3s ease-in-out;
        }

        input:focus {
            border-color: #4CAF50;
            outline: none;
            box-shadow: 0px 0px 5px rgba(76, 175, 80, 0.5);
        }

        button {
            background: #4CAF50;
            color: white;
            padding: 0.8rem;
            font-size: 1rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background 0.3s ease-in-out;
        }

        button:hover {
            background: #81C784;
        }

        .cloud-message {
            margin-top: 1rem;
            padding: 1rem 1.5rem;
            background: #ffcc80;
            color: #333;
            border-radius: 20px;
            box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
            font-size: 1rem;
            font-weight: bold;
            text-align: center;
            position: relative;
        }

        .cloud-message:after {
            content: '';
            position: absolute;
            bottom: -10px;
            left: 50%;
            transform: translateX(-50%);
            width: 0;
            height: 0;
            border-left: 10px solid transparent;
            border-right: 10px solid transparent;
            border-top: 10px solid #ffcc80;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Login</h1>
        <form method="POST">
            <label for="username">Usuário:</label>
            <input type="text" id="username" name="username" required>
            
            <label for="password">Senha:</label>
            <input type="password" id="password" name="password" required>
            
            <button type="submit">Entrar</button>
        </form>

        <!-- Exibir mensagem de erro, se existir -->
        <?php if (!empty($error_message)): ?>
        <div class="cloud-message">
            <?php echo htmlspecialchars($error_message); ?>
        </div>
        <?php endif; ?>
    </div>
</body>
</html>