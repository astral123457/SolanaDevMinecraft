# SolanaDevMinecraft
SolanaDevMinecraft


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

### **Resumo**

- **Comandos de Saldo e Carteira**:
  - `/saldo`: Consulta saldo no banco.
  - `/createwallet`: Cria uma carteira Solana.

- **Comandos de Compras**:
  - `/buyapple`, `/buyemerald`, `/buynetheritepickaxe`, etc.: Compram itens específicos.

- **Comandos de Transferência**:
  - `/soltransfer`: Transfere SOL para outro jogador.

Se precisar de mais detalhes ou ajustes, é só avisar! 😊
