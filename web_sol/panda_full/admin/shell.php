<?php
session_start();

if (!isset($_SESSION["user"])) {
    header("Location: ./login.php"); // Redireciona para o login se não estiver logado
    exit();
}
?>
<?php
$command = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana ls 2>&1";
$output = shell_exec($command);
echo "<pre>Comando executado: $command</pre>";
echo "<pre>Saída do comando:\n$output</pre>";
?>