-- phpMyAdmin SQL Dump
-- version 5.2.1deb1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Tempo de geração: 10/05/2025 às 04:08
-- Versão do servidor: 10.11.11-MariaDB-0+deb12u1
-- Versão do PHP: 8.2.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `banco`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `access_attempts`
--

CREATE TABLE `access_attempts` (
  `ip` varchar(45) NOT NULL,
  `attempts` int(11) DEFAULT 0,
  `last_attempt` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `banco`
--

CREATE TABLE `banco` (
  `id` int(11) NOT NULL,
  `jogador` varchar(50) DEFAULT NULL,
  `saldo` decimal(10,2) DEFAULT 500.00,
  `divida` decimal(10,2) DEFAULT 0.00,
  `investimento` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `banco`
--

INSERT INTO `banco` (`id`, `jogador`, `saldo`, `divida`, `investimento`) VALUES
(1, '007amauri', 1748500.88, 0.00, 0.00),
(2, 'DOYZEL', 169.47, 0.00, 0.00),
(3, 'abreww', 525.00, 0.00, 0.00),
(4, 'BerserkerWolf', 500.00, 0.00, 0.00),
(5, 'xxakkdie', 500.00, 0.00, 0.00),
(6, 'Precint', 500.00, 0.00, 0.00);

-- --------------------------------------------------------

--
-- Estrutura para tabela `ban_ip`
--

CREATE TABLE `ban_ip` (
  `id` int(11) NOT NULL,
  `key_genere` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `data` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `carteiras`
--

CREATE TABLE `carteiras` (
  `id` int(11) NOT NULL,
  `jogador_id` int(11) NOT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `chave_privada` text DEFAULT NULL,
  `frase_secreta` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `carteiras`
--

INSERT INTO `carteiras` (`id`, `jogador_id`, `endereco`, `chave_privada`, `frase_secreta`) VALUES
(1, 1, '7PJBFH7sRPDjmQk7n2qQzaFSn4oxsMv8BFS43vKKb3S2', '6512926157e2eef22831afb8ef5e527297569bd04151cf6ddfb4f598caf3dc985edc523172a917ade87f7ea1bf23494ac507d2a108ecfd18fd78c23df4f16ee3', 'puppy day better october elephant provide chat unique harbor magic lumber rib ============================================================================='),
(2, 2, 'GcT1enHwJh67SQrocpi2rgHr8jpx1BiyGjj5dsiSTEoY', 'c69e22b6b8b8e512a2e9a8120243a48dc172ef421d6043ec0ca6a59a8e38edf4e7f40b0f84152996d2fb41a5a58aed402a85759b56112c2cd6f6761527a6751f', 'pistol marble conduct excite salute river kick wish strong waste vague champion ==============================================================================='),
(3, 3, '9THETXUNSsW7vC4BokaNdYfqQAzySMdoDB138awdnYjJ', 'eee613b1ff5e90cc990f140ece0888b07a613802210c52ea4fd420705e6042b57d98f336b7f91f3edc1c67e493834dd436be17d5ff0cd8be18a6d5e5f74d41b9', 'midnight dog manual fiction rare milk theory simple hedgehog phone cannon carbon ================================================================================'),
(4, 4, '6wguAs2LUdaqedmTu3mmWHHVT76pmFKqjmbEk6T6qt5h', 'fd5c1687b9975b36ac364f6f2da2531aad87734f9057a796a53c81dcb8d6fee8584ce9ae05cee945d9f10ee132df5e6ad47e5e2ecae48d8ea5e243d2af599e0c', 'dish industry enlist dish noodle hen fan furnace grass chest mystery galaxy ==========================================================================='),
(5, 5, '5Fnf7yVGzC5mtFrNDs52iUjZ8pGpuHcsjZKUs51LvmiA', 'bf38467c0489b3a61df92a8dd283b78f40f72bece2b561370fdddc037414593b3f3880fbf4d80b8bbaa2e6431aa75857730ae4d5301a1b76172f67bc9b4072fb', 'inject crowd onion story twelve table magnet excess tenant auction quote bleak =============================================================================='),
(20, 6, 'TKJ1KHNVFDS6kGqcGWQfGXY3bQNxpa2mYdRpKCjQ8Jx', '8f8837f1c4c79b0cd56f3161cf75b34a6a16c3294f45b17afb35b36d1c1c5a7e06bdc9042c0b25a69ec1ed1ecb47f32d2896a5859e356e39d99b8b9660d57625', '\\ntruck helmet purity disagree goddess situate sweet true much jump wear retreat\\n');

-- --------------------------------------------------------

--
-- Estrutura para tabela `jogadores`
--

CREATE TABLE `jogadores` (
  `id` int(11) NOT NULL,
  `nome` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `jogadores`
--

INSERT INTO `jogadores` (`id`, `nome`) VALUES
(1, '007amauri'),
(3, 'abreww'),
(4, 'BerserkerWolf'),
(2, 'DOYZEL'),
(6, 'precint'),
(5, 'xxakkdie');

-- --------------------------------------------------------

--
-- Estrutura para tabela `livro_caixa`
--

CREATE TABLE `livro_caixa` (
  `id` int(11) NOT NULL,
  `jogador` varchar(255) NOT NULL,
  `tipo_transacao` varchar(255) NOT NULL,
  `valor` float NOT NULL,
  `moeda` varchar(10) NOT NULL,
  `assinatura` varchar(255) NOT NULL,
  `data_hora` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `livro_caixa`
--

INSERT INTO `livro_caixa` (`id`, `jogador`, `tipo_transacao`, `valor`, `moeda`, `assinatura`, `data_hora`) VALUES
(1, '007amauri', 'transferência', 2, 'SOL', '2EccAmqogfSec4U4MXbCwDVL67ioX2WteZnqCX8zg8aWJtiX9onwz3YaMQdSfSDysmGRPnNpbB6aa1jhQPU4P7aw', '2025-04-26 12:49:56'),
(2, '007amauri', 'compra', 3, 'SOL', '56wnb3FRMQ4U62sD3cgTvTCMHmosu6dCXLnvgbuQpsJX3FSmbsdTbxvXdAdbF4833B2YHovY15inq1T2FaAqA8tq', '2025-04-26 12:51:27'),
(3, '007amauri', 'compra', 3, 'SOL', 'nTMHkNLL5DR8Wi2RSqjrmzstZnK7TQLbKZzP7T7Aj6bNCB4N17XR2gCGfvMwrDuhkRKxDXsziefUsSN6RSNpEQt', '2025-04-26 21:23:11'),
(4, '007amauri', 'transferencia', 0.01, 'SOL', '42bXNCAXGA3Kbdoz8mpQ8tTxvhrCDAxYKjvRC5oysi7ya4E1wv9GxGDNNicEy483xYppUT7u9iHHjyuYdktEfRBB', '2025-05-01 12:10:28'),
(5, '007amauri', 'transferência', 1, 'SOL', '\\nSignature: 3cT34AiA4Jv9BgPZHYE613JXxKwsAEdUnsLfJr2hJvXaM252AvJGnG4Z1Kv3GeX65kgaxZexHWnpG2ShKoPDNWxo\\n', '2025-05-06 16:49:07'),
(6, '007amauri', 'transferência', 0.07, 'SOL', '\\nSignature: 5Ar3CEReZeZojF9Z1iv7CU6ujBKpxThwDnbbcCeW5wSbx3QAbY3KGvYXT8jX9ak4XKGVvj2krpYSgmt9Td6KsMF2\\n', '2025-05-06 17:15:29'),
(7, '007amauri', 'compra', 0.05, 'SOL', '\\nSignature: 26Se8P57pcXJBHLKi5PhQQJfaZ8eQ5WjFggy1hkwTZqurWPeR7bXxJ7m7maBnsuFw7WY8fH74yNo4Pzrsc2qofcj\\n', '2025-05-06 17:50:40'),
(8, 'Precint', 'compra', 1, 'SOL', '\\nSignature: UynzWsijohxzBj2rHjUr3wce6JzkUBAE771AutzWod2nWCovbTgv8kmSDXNNTh7LcucDGHv7ARYwDPAoxkTXvtK\\n', '2025-05-07 19:53:36'),
(9, 'Precint', 'compra', 6, 'SOL', '\\nSignature: 3cHyNGjJkoUjTotgxYBs2BPqetktMvU6fXgmk1YuGnuUNLyx6VqX9u6ztAM6nMKXpRrnmLp2P2WVzVWiYmWnf7fs\\n', '2025-05-07 21:10:46'),
(10, '007amauri', 'compra', 2, 'SOL', '\\nSignature: 4n27AZ5uRH8kMm9UTo2cxqx6JSpXLi5T76wwpsbrb8PF9SLdW7SGB3GmqCDJDhUZx4FitEJqKjcWHtRdtaTYRwDF\\n', '2025-05-09 11:25:59'),
(11, '007amauri', 'transferência', 1, 'SOL', '\\nSignature: 3VaUFsD7Rm4fpn9CpD4ES4eRvrZwySRW7KVsTJKya13G1ys9vRiauBMEuk2FgoGZERQBhXrSqGPEoN2vbfHrXT5x\\n', '2025-05-09 11:29:23'),
(12, '007amauri', 'transferência', 1, 'SOL', '\\nSignature: 4x3B1FgmA4vAfo1iLv64TfJnE7tNDviuHx91wMEaSZvpv623uhvCVFDkM5qUB7yyjekbUXjRk5ZhQJe1ShXjgheF\\n', '2025-05-09 12:07:03'),
(13, '007amauri', 'compra', 1, 'SOL', '\\nSignature: 4WkE5xJfytQHfBtDQJYbgTJHMZSAoV8e6cbRNHe49ac8JYvySHQuRQYBv5vFpKvy65ZoUpw6rUqorjEdYjPEB6Q6\\n', '2025-05-09 12:07:50'),
(14, '007amauri', 'transferência', 0.04, 'SOL', '\\nSignature: 3AK83TAqC9FyUGMZnPJ79rinizgWXmsTm2b4MoCGE7Y3UT37m46TReSWBBS8urX42ZuwpxvBuRsvz9u5AMJfNVFk\\n', '2025-05-09 13:56:49'),
(15, '007amauri', 'compra', 0.05, 'SOL', '\\nSignature: 3xH4aHueYyi9p34yARwdYhYJ9Nvk1sxS7Yw5uk8cS1TCXsU5nEV6drprUZGv3v5pATdXebBuADv1TsFTw9qPpm1Y\\n', '2025-05-09 18:15:44'),
(16, '007amauri', 'compra', 0.08, 'SOL', '\\nSignature: 5QvxmB76HZpZ7Z4XwfcZoceYqFsR3RwH4QFtBCeWeTKRZAa61x4hUMomfqT4nxGbodnkQiQk4M1oGVjp5Di1E3Tn\\n', '2025-05-09 18:16:27'),
(17, '007amauri', 'transferência', 0.08, 'SOL', '\\nSignature: 5p8tk8hQavpMzqjyM42QYveqozAbMSVxadQHTiraQiwe2WYVnszmLJ6X5EkJCEsWrE796hE1qMFwJ8RHZUZYK4vP\\n', '2025-05-09 19:10:03'),
(18, '007amauri', 'compra', 0.1, 'SOL', '\\nSignature: 2BB4Nq1fsqsEqjhhbcUgU4Sy3enVgu7sbb3pSA6NiAzQVnpyXhkuc55GoV6mbESMGsawthf5JRGH2ZBfXBjqwkyN\\n', '2025-05-09 19:10:29');

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `access_attempts`
--
ALTER TABLE `access_attempts`
  ADD PRIMARY KEY (`ip`);

--
-- Índices de tabela `banco`
--
ALTER TABLE `banco`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `jogador` (`jogador`);

--
-- Índices de tabela `ban_ip`
--
ALTER TABLE `ban_ip`
  ADD PRIMARY KEY (`id`);

--
-- Índices de tabela `carteiras`
--
ALTER TABLE `carteiras`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `endereco` (`endereco`),
  ADD KEY `jogador_id` (`jogador_id`);

--
-- Índices de tabela `jogadores`
--
ALTER TABLE `jogadores`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nome` (`nome`);

--
-- Índices de tabela `livro_caixa`
--
ALTER TABLE `livro_caixa`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `banco`
--
ALTER TABLE `banco`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de tabela `ban_ip`
--
ALTER TABLE `ban_ip`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de tabela `carteiras`
--
ALTER TABLE `carteiras`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de tabela `jogadores`
--
ALTER TABLE `jogadores`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de tabela `livro_caixa`
--
ALTER TABLE `livro_caixa`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `carteiras`
--
ALTER TABLE `carteiras`
  ADD CONSTRAINT `carteiras_ibfk_1` FOREIGN KEY (`jogador_id`) REFERENCES `jogadores` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
