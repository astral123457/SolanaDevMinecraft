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

// Obtém os parâmetros de filtro
$startDate = $_GET['start_date'] ?? null;
$endDate = $_GET['end_date'] ?? null;

$query = "SELECT * FROM livro_caixa";
if ($startDate && $endDate) {
    $query .= " WHERE data_hora BETWEEN '$startDate' AND '$endDate'";
}
$query .= " ORDER BY data_hora DESC";

$result = $conn->query($query);
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Livro Caixa</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <h1>Livro Caixa</h1>
        <form method="GET" action="index.php">
            <label for="start_date">Data inicial:</label>
            <input type="date" id="start_date" name="start_date" required>
            <label for="end_date">Data final:</label>
            <input type="date" id="end_date" name="end_date" required>
            <button type="submit">Filtrar</button>
        </form>
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
                        echo "<tr>";
                        echo "<td>{$row['id']}</td>";
                        echo "<td>{$row['jogador']}</td>";
                        echo "<td>{$row['tipo_transacao']}</td>";
                        echo "<td>{$row['valor']}</td>";
                        echo "<td>{$row['moeda']}</td>";
                        echo "<td>{$row['assinatura']}</td>";
                        echo "<td>{$row['data_hora']}</td>";
                        echo "</tr>";
                    }
                } else {
                    echo "<tr><td colspan='7'>Nenhum registro encontrado.</td></tr>";
                }
                ?>
            </tbody>
        </table>
    </div>
<!-- Adicionando o JavaScript pelo link -->
    <script src="jaJA.js"></script>

</body>
</html>

<?php
$conn->close();
?>