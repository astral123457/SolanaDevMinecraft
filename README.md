# SolanaDevMinecraft
SolanaDevMinecraft

Api consulta.php:
testes ssh
 
curl -X GET "http://192.168.100.170/consulta.php?comando=solana%20balance%207PJBFH7sRPDjmQk7n2qQzaFSn4oxsMv8BFS43vKKb3S2"


curl -X GET "http://192.168.100.170/consulta.php?comando=solana%20transfer%207PJBFH7sRPDjmQk7n2qQzaFSn4oxsMv8BFS43vKKb3S2%200.05%20--keypair%20/solana-token/wallets/BerserkerWolf_wallet.json%20--allow-unfunded-recipient"

![image](https://github.com/user-attachments/assets/fd46a787-5de7-442d-80bd-166e90498ee0)

Se quiser conferir se os fundos foram corretamente transferidos %20 = espaço para funcionar web!

curl -X GET "http://192.168.100.170/consulta.php?comando=solana%20confirm%20-v%205pbKpF54ZhMfkxmfwxsjACM7VJLqLv4U1syPLrvoWaHQMY4ogfVdz7TKuUUNJFLuiJdRtFtACWvHceg3m12mR6vk"


Ou pode usar um explorador da Solana como:

🔍 Solana Explorer https://explorer.solana.com/?cluster=devnet 

https://explorer.solana.com/tx/5pbKpF54ZhMfkxmfwxsjACM7VJLqLv4U1syPLrvoWaHQMY4ogfVdz7TKuUUNJFLuiJdRtFtACWvHceg3m12mR6vk?cluster=devnet




Aqui está um fluxo básico em forma de diagrama para os comandos do seu plugin:

---

### **Fluxo de Comandos**

```plaintext
                          [Jogador Executa Comando]
                                    |
                                    v
        +---------------------------+---------------------------+
        |                           |                           |
    /saldo                     /createwallet              /buycurrency
    Verifica saldo no banco    Cria carteira Solana       Compra moedas do jogo
        |                           |                           |
        v                           v                           v
[Consulta saldo no banco]   [Cria carteira no Docker]   [Verifica saldo de SOL]
        |                           |                           |
        v                           v                           v
[Retorna saldo ao jogador] [Salva carteira no banco]   [Deduz SOL e adiciona moedas]
                                    |                           |
                                    v                           v
                          [Retorna endereço da carteira] [Confirma compra ao jogador]
```

---

### **Fluxo de Compras na Loja**

```plaintext
                          [Jogador Executa Comando de Compra]
                                    |
                                    v
        +---------------------------+---------------------------+
        |                           |                           |
  /buyapple                   /buyemerald              /buynetheritepickaxe
  Compra Maçã Encantada       Compra Esmeralda         Compra Picareta de Netherite
        |                           |                           |
        v                           v                           v
[Verifica saldo no banco]   [Verifica saldo no banco]   [Verifica saldo no banco]
        |                           |                           |
        v                           v                           v
[Deduz saldo do jogador]   [Deduz saldo do jogador]   [Deduz saldo do jogador]
        |                           |                           |
        v                           v                           v
[Adiciona item ao jogador] [Adiciona item ao jogador] [Adiciona item ao jogador]
        |                           |                           |
        v                           v                           v
[Confirma compra ao jogador] [Confirma compra ao jogador] [Confirma compra ao jogador]
```

---

### **Fluxo de Transferência de Solana**

```plaintext
                          [Jogador Executa /soltransfer]
                                    |
                                    v
                        [Verifica carteira do remetente]
                                    |
                                    v
                        [Verifica saldo de SOL do remetente]
                                    |
                                    v
                        [Executa transferência via Docker]
                                    |
                                    v
                        [Registra transação no banco]
                                    |
                                    v
                        [Confirma transferência ao jogador]
```

---

### **Fluxo de Criação de Carteira**

```plaintext
                          [Jogador Executa /createwallet]
                                    |
                                    v
                        [Verifica se já existe uma carteira]
                                    |
                                    v
                        [Cria carteira via Docker]
                                    |
                                    v
                        [Salva endereço da carteira no banco]
                                    |
                                    v
                        [Retorna endereço ao jogador]
```

---

# Agora, crie o banco chamado banco e sua tabela principal:


USE banco;

CREATE TABLE banco (
    id INT PRIMARY KEY AUTO_INCREMENT,
    jogador VARCHAR(50) UNIQUE,
    saldo DECIMAL(10,2) DEFAULT 500,
    divida DECIMAL(10,2) DEFAULT 0,
    investimento DECIMAL(10,2) DEFAULT 0
);

CREATE TABLE jogadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE carteiras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    jogador_id INT NOT NULL,
    endereco VARCHAR(100) UNIQUE NOT NULL,
    chave_privada TEXT NOT NULL,
    frase_secreta TEXT NOT NULL,
    FOREIGN KEY (jogador_id) REFERENCES jogadores(id) ON DELETE CASCADE
);

CREATE TABLE livro_caixa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    jogador VARCHAR(255) NOT NULL,
    tipo_transacao VARCHAR(255) NOT NULL, -- Ex: "compra", "transferencia"
    valor FLOAT NOT NULL,
    moeda VARCHAR(10) NOT NULL, -- Ex: "SOL", "moedas"
    assinatura VARCHAR(255) NOT NULL, -- Signature gerada na transação
    data_hora DATETIME DEFAULT CURRENT_TIMESTAMP
);

### **Resumo**

- **Comandos de Saldo e Carteira**:
  - `/saldo`: Consulta saldo no banco.
  - `/createwallet`: Cria uma carteira Solana.

- **Comandos de Compras**:
  - `/buyapple`, `/buyemerald`, `/buynetheritepickaxe`, etc.: Compram itens específicos.

- **Comandos de Transferência**:
  - `/soltransfer`: Transfere SOL para outro jogador.

Se precisar de mais detalhes ou ajustes, é só avisar! 😊

este video em ingles me ajudou a criar e configurar uma dock para solana https://www.youtube.com/watch?v=L4ASwqLZVV0 
um pai ensinando as finhas economia e banco digital Ti e tecnologias

caso queira criar usuario astral e pasta astral e usuario www 
1. Criar um Usuário no Linux
Para criar o usuário que terá permissões específicas:
sudo adduser www


- O comando pedirá que você configure senha e outras informações básicas.
- Certifique-se de que o usuário tenha acesso apenas às pastas e comandos necessários.


2. Criar um Grupo e Adicionar o Usuário
Crie um grupo chamado dockerusers (ou outro nome que preferir) e adicione o usuário www:
sudo groupadd dockerusers
sudo usermod -aG dockerusers www


- Isso adicionará o usuário ao grupo e permitirá que ele execute comandos relacionados ao Docker (se configurado).


3. Permitir Acesso ao Docker
Garanta que o grupo dockerusers tenha permissão para usar o Docker sem precisar de sudo:
sudo usermod -aG docker www


Depois disso, reinicie para garantir que as permissões sejam aplicadas:
newgrp docker



4. Configurar as Pastas
Garanta que as pastas necessárias para o comando Docker estejam acessíveis ao usuário www:
sudo mkdir -p /home/astral/astralcoin /home/astral/astralcoin/solana-data
sudo chown -R www:www /home/astral/astralcoin


Isso configura permissões de leitura/escrita para o usuário www.

5. Executar o Comando Docker
Verifique se o Docker está instalado. Caso contrário, instale-o com:
sudo apt update
sudo apt install docker.io


Depois, você pode executar o comando com o usuário www:
sudo docker run --rm \
  -v /home/astral/astralcoin:/solana-token \
  -v /home/astral/astralcoin/solana-data:/root/.config/solana \
  heysolana solana balance



6. Configurar Permissões no Usuário www
Se precisar que o usuário www tenha acesso a grupos adicionais, você pode editá-lo diretamente:
sudo usermod -aG www-data www


O grupo www-data é comumente usado em configurações web, mas verifique a necessidade antes de adicionar.

7. Documentar e Simplificar
Para o tutorial:
- Descreva cada passo detalhadamente.
- Inclua explicações sobre segurança (limitar permissões, evitar acessos desnecessários, etc.).
- Caso o script exija ajustes adicionais, como variáveis de ambiente, explique como configurá-las.

Com essas etapas, você terá um guia funcional para configurar o ambiente no Linux e executar o comando Docker no contexto do seu projeto. Precisa de ajuda para formatar isso em texto claro ou agregar mais detalhes? 🚀

