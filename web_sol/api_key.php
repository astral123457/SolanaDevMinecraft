<?php

// A frase de entrada
$frase = "banana";

// 1. Gerar o hash SHA-256 da frase.
// O terceiro parâmetro 'true' faz com que a função retorne dados binários brutos.
$hashBinarioCompleto = hash('sha256', $frase, true);

// 2. Truncar o hash para os primeiros 64 bits (8 bytes).
$hashBinario64Bits = substr($hashBinarioCompleto, 0, 8);

// 3. Converter os 8 bytes (64 bits) para uma string hexadecimal.
$chaveHex64Bits = bin2hex($hashBinario64Bits);

// Exibir a chave gerada
echo "Frase original: " . $frase . "\n";
echo "Chave hexadecimal de 64 bits gerada: " . $chaveHex64Bits . "\n";

// Para verificar o comprimento em bits (16 caracteres hexadecimais * 4 bits/caractere = 64 bits)
// echo "Comprimento da chave em caracteres hex: " . strlen($chaveHex64Bits) . "\n";
// echo "Comprimento da chave em bits: " . strlen($chaveHex64Bits) * 4 . "\n";

?>