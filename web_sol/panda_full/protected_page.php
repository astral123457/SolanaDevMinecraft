<?php
session_start();
if (!isset($_SESSION["user"])) {
    header("Location: login.php");
    exit();
}
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Página Protegida</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            margin: 0;
            padding: 0;
            text-align: center;
        }
        .top-bar {
            background: #4CAF50;
            padding: 10px;
        }
        .top-bar a {
            color: white;
            text-decoration: none;
            padding: 10px;
            margin: 5px;
            display: inline-block;
            font-weight: bold;
        }
        .top-bar a:hover {
            background: #66BB6A;
        }
        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
        }
        iframe {
            width: 90%;
            height: 500px;
            border: 1px solid #ccc;
            margin-top: 20px;
        }
        select, button {
            font-size: 16px;
            padding: 8px;
            margin: 5px;
        }
    </style>
</head>
<body>

    <div class="top-bar">
        <a href="../caixa/carteira.php" target="contentFrame">Carteira</a>
        <a href="../caixa/transferi_p.php" target="contentFrame">Trasferencia</a>
        <a href="../caixa/index.php" target="contentFrame">Livro Caixa</a>
        <a href="panda.php" target="contentFrame">Panda banco carteira pai</a>
        <a href="buy.php" target="contentFrame">Paga</a>
        <a href="admin/heysolana.php" target="contentFrame">heysolana</a>
        <a href="registro.php" target="contentFrame">Registro</a>
    </div>

    <div class="container">
        <h1>Bem-vindo, <?php echo htmlspecialchars($_SESSION["user"]); ?>!</h1>
        <p>Use os botões abaixo para carregar uma página no frame:</p>

        <select id="pageSelector">
            <option value="../caixa/carteira.php">Carteira</option>
            <option value="../caixa/transferi_p.php">Trasferencia</option>
            <option value="../caixa/index.php">Livro Caixa</option>
        </select>
        <button onclick="loadPage()">Abrir</button>

        <iframe name="contentFrame" src="default.html"></iframe>

        <br>
        <a href="logout.php">Sair</a>
    </div>

    <script>
        function loadPage() {
            var selectedPage = document.getElementById("pageSelector").value;
            document.getElementsByName("contentFrame")[0].src = selectedPage;
        }
    </script>

</body>
</html>