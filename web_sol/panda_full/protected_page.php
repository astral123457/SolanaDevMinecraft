<?php
session_start();

if (!isset($_SESSION["user"])) {
    header("Location: login.php"); // Redireciona para o login se não estiver logado
    exit();
}
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Página Protegida</title>
</head>
<body>
    <h1>Bem-vindo à página protegida, <?php echo htmlspecialchars($_SESSION["user"]); ?>!</h1>
    <a href="logout.php">Sair</a>
</body>
</html>