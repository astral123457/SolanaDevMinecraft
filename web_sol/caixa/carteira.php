<?php
session_start();

if (!isset($_SESSION["user"])) {
    header("Location: login.php"); // Redireciona para o login se não estiver logado
    exit();
}

// Conexão com o banco de dados
$host = "localhost";
$user = "root";
$password = "0073007";
$database = "banco";

$conn = new mysqli($host, $user, $password, $database);

// Verifica a conexão
if ($conn->connect_error) {
    die("Erro na conexão com o banco de dados: " . $conn->connect_error);
}

// Obtém todos os jogadores para o dropdown
$jogadoresQuery = "SELECT id, nome FROM jogadores";
$jogadoresResult = $conn->query($jogadoresQuery);

// Inicializa as variáveis
$selectedJogadorId = $_POST['jogador_id'] ?? null;
$carteirasResult = null;
$saldo = null;

if ($selectedJogadorId) {
    // Consulta para obter as carteiras do jogador selecionado
    $carteirasQuery = "SELECT c.id, j.nome AS jogador, c.endereco, c.chave_privada, c.frase_secreta 
                       FROM carteiras c
                       INNER JOIN jogadores j ON c.jogador_id = j.id
                       WHERE c.jogador_id = $selectedJogadorId
                       ORDER BY c.id ASC";
    $carteirasResult = $conn->query($carteirasQuery);

    // Obtem o endereço da carteira para consultar o saldo
    if ($carteirasResult && $carteirasResult->num_rows > 0) {
        $row = $carteirasResult->fetch_assoc(); // Pega a primeira carteira associada ao jogador
        $enderecoCarteira = $row['endereco'];

        // Comando para consultar o saldo da carteira usando Docker
        $comandoSaldo = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana solana balance $enderecoCarteira";

        // Executa o comando
        exec($comandoSaldo, $output, $status);

        // Captura o saldo retornado
        if ($status === 0) {
            $saldo = implode("\n", $output); // Concatena o array de saída em uma string
        } else {
            $saldo = "Erro ao obter o saldo da carteira.";
        }
    }
}
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carteiras dos Jogadores</title>
    <link rel="stylesheet" href="carteira.css">
	<style>
        .campo-oculto {
            position: relative;
            display: inline-block;
        }
		
		.container {
    margin: auto;
    max-width: 800px;
	width: 800px;
    height: auto;

	background: linear-gradient(to right, purple, blue, green);
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

        .conteudo {
            visibility: hidden;
            background-color: #f1f1f1;
            padding: 10px;
            position: absolute;
            z-index: 1;
            border: 1px solid #ddd;
        }

        .campo-oculto:hover .conteudo {
            visibility: visible;
        }
		
		td {
    background-color: lime;
}

label {
    color: black; /* Define a cor do texto */
    text-shadow: 
        -1px -1px 0 white,  
        1px -1px 0 white,  
        -1px 1px 0 white,  
        1px 1px 0 white; /* Cria um efeito de borda ao redor da fonte */
}

    </style>
	<link rel="icon" href="https://faucet.solana.com/favicon.ico" type="image/x-icon" sizes="32x32">
</head>
<body>
    <div class="container">
	
        <h1><img alt="Solana Logo" loading="lazy" width="242" height="36" decoding="async" data-nimg="1" style="color:transparent" src="https://faucet.solana.com/_next/static/media/solanaLogo.74d35f7a.svg"> <fonte color=white> Carteiras dos Jogadores</h1>
		<div class="text"><a href="../logout.php" style="color: white;">Sair</a></div>
        
        <!-- Dropdown para selecionar jogador -->
        <form method="POST" action="carteira.php">
            <label for="jogador_id">Selecione o jogador:</label>
            <select id="jogador_id" name="jogador_id" required>
                <option value="" disabled selected>Escolha um jogador</option>
                <?php
                if ($jogadoresResult->num_rows > 0) {
                    while ($jogador = $jogadoresResult->fetch_assoc()) {
                        $selected = $selectedJogadorId == $jogador['id'] ? 'selected' : '';
                        echo "<option value='{$jogador['id']}' $selected>{$jogador['nome']}</option>";
                    }
                }
                ?>
            </select>
            <button type="submit">Mostrar Carteiras</button>
        </form>

        <?php if ($carteirasResult && $carteirasResult->num_rows > 0): ?>
            <!-- Tabela com todas as informações -->
			
			
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Jogador</th>
                        <th>Endereço</th>
                        <th>Saldo (SOL)</th>
                    </tr>
                </thead>
                <tbody>
                    <?php
                    $carteirasResult->data_seek(0); // Reseta o ponteiro da consulta
                    while ($row = $carteirasResult->fetch_assoc()) {
                        echo "<tr>";
                        echo "<td>{$row['id']}</td>";
                        echo "<td>{$row['jogador']}</td>";
                        echo "<td>{$row['endereco']}</td>";
                        echo "<td>" . ($row['endereco'] === $enderecoCarteira ? $saldo : '-') . "</td>";
                        echo "</tr>";
               
                    ?>
                </tbody>
            </table>
			
			<table>
            <thead>
                <tr>

                    <th>Chave Privada</th>
                </tr>
            </thead>
            <tbody>
                <tr><td>
				
				<div class="campo-oculto">
        Aqui ***************************************
        <div class="conteudo" id="texto">
            <?php echo $row['chave_privada']; ?>
        </div>
    </div><button onclick="copiarTexto()"> Copiar</button>
	</td></tr>
				
            </tbody>
        </table>
		<table>
            <thead>
                <tr>


                    <th>Frase Secreta</th>
                </tr>
            </thead>
            <tbody>
			<tr><td><div class="campo-oculto">
        Aqui ***************************************
        <div class="conteudo" id="texto">
            <?php
                    echo "{$row['frase_secreta']}";
                }
                ?>
        </div>
    </div><button onclick="copiarTexto()"> Copiar</button>
                </td></tr>
            </tbody>
        </table>
        <?php elseif ($selectedJogadorId): ?>
            <p>Nenhuma carteira encontrada para o jogador selecionado.</p>
        <?php endif; ?>
    </div>
</body>
</html>

<?php
$conn->close();
?>