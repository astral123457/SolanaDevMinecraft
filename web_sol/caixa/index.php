<?php
// Conexão com o banco de dados
$host = "localhost";
$user = "root";
$password = "0073007";
$database = "banco";

$conn = new mysqli($host, $user, $password, $database);

// Verifica conexão
if ($conn->connect_error) {
    die("Erro na conexão com o banco de dados: " . $conn->connect_error);
}

// Obtém a data de hoje
$hoje = date("Y-m-d");

// Obtém os parâmetros de filtro do usuário (caso existam)
$startDate = $_GET['start_date'] ?? null;
$endDate = $_GET['end_date'] ?? null;

// Consulta base para obter transações do dia
$query = "SELECT * FROM livro_caixa WHERE DATE(data_hora) = '$hoje'";

// Se o usuário fizer uma filtragem manual, altera a consulta
if ($startDate && $endDate) {
    $query = "SELECT * FROM livro_caixa WHERE data_hora BETWEEN '$startDate' AND '$endDate'";
}

// Ordena por data, mostrando as mais recentes primeiro
$query .= " ORDER BY data_hora DESC";

$result = $conn->query($query);
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Livro Caixa</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <h1>📜 Livro Caixa</h1>

        <!-- Formulário para filtro de datas -->
        <form method="GET" action="index.php">
            <label for="start_date">Data inicial:</label>
            <input type="date" id="start_date" name="start_date" required>
            
            <label for="end_date">Data final:</label>
            <input type="date" id="end_date" name="end_date" required>
            
            <button type="submit">🔎 Filtrar</button>
        </form>

        <!-- Tabela de transações -->
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Jogador</th>
                    <th>Tipo de Transação</th>
                    <th>Valor</th>
                    <th>Moeda</th>
                    <th>Assinatura</th>
                    <th>Data e Hora</th>
                </tr>
            </thead>
            <tbody>
                <?php
                if ($result->num_rows > 0) {
                    while ($row = $result->fetch_assoc()) {
                        // Define a classe de cor conforme o tipo de transação
                        $classeCor = "";
                        if ($row['tipo_transacao'] === 'reembolso') {
                            $classeCor = "reembolso";
                        } elseif ($row['tipo_transacao'] === 'transferencia') {
                            $classeCor = "transferencia";
                        } elseif ($row['tipo_transacao'] === 'compra') {
                            $classeCor = "compra";
                        }

                        echo "<tr class='$classeCor'>";
                        echo "<td>{$row['id']}</td>";
                        echo "<td>{$row['jogador']}</td>";
                        echo "<td>{$row['tipo_transacao']}</td>";
                        echo "<td>{$row['valor']}</td>";
                        echo "<td>{$row['moeda']}</td>";
                        echo "<td><a href='https://explorer.solana.com/tx/{$row['assinatura']}?cluster=devnet' target='_blank'>{$row['assinatura']}</a></td>";
                        echo "<td>{$row['data_hora']}</td>";
                        echo "</tr>";
                    }
                } else {
                    echo "<tr><td colspan='7'>Nenhuma transação encontrada para hoje.</td></tr>";
                }
                ?>
            </tbody>
        </table>
    </div>

    <!-- JavaScript -->
    <script src="jaJA.js"></script>

</body>
</html>

<?php
$conn->close();
?>