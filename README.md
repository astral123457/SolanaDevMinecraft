# SolanaDevMinecraft 1.19.x to 1.20.x to 1.21.x Paper i.e. it also works with modern server fabric and mods
To configure everything first, I recommend watching this video in English, it helped me create and configure a dock for Solana https://www.youtube.com/watch?v=L4ASwqLZVV0

# Dependencia mod do Fabric server https://fabricmc.net/use/server/

          Cardboard-1.21.4.jar (Cardboard-1.19.x or 1.20.x or 1.21.x)	
          
          fabric-api-0.119.21.21.4.jar 	 
          
          iCommon-Fabric-bundle1.jar (1.19 a 1.21.5)

SolanaDevMinecraft

The Solana Dev token has no real value and is used only for fun and economic experimentation. It is not subject to regulations or financial laws, functioning solely as a fictional asset within a testing environment and playful interactions. Its purpose is to provide entertainment and learning about economic concepts without involving real financial transactions.

this dev coin can be obtained every 8 hours, 2 drops of values from 1 to 5
https://faucet.solana.com/


![image](https://github.com/user-attachments/assets/1a40a6c9-a8fa-44f8-bf96-59b5109b015e)


Example URL:
http://your_server/consulta.php?apikey=2f4164616e614e61&command=your_command_here

Security Enhancements (Recommended):

Real API Key: Instead of deriving from "banana", generate a long, random API key (e.g. 32 or 64 random hexadecimal characters) and store it directly in the script or in a configuration file.
PHP

// At the beginning of the script:
// $correctApiKey = 'your_random_very_long_and_secure_key_here';


![image](https://github.com/user-attachments/assets/3808718f-a04c-48f1-b4cf-7cde0e76b7f7)


Api consulta.php:
tests ssh
 
      curl -X GET "http://192.168.100.170/consulta.php?apikey=b493d48364afe44d&comando=solana%20balance%207PJBFH7sRPDjmQk7n2qQzaFSn4oxsMv8BFS43vKKb3S2"

Transfer from one player to another the wallet of the person who will receive it and the name of the player who already has a wallet with money!

      curl -X GET "http://192.168.100.170/consulta.php?comando=solana%20transfer%207PJBFH7sRPDjmQk7n2qQzaFSn4oxsMv8BFS43vKKb3S2%200.05%20--keypair%20/solana-token/wallets/BerserkerWolf_wallet.json%20--allow-unfunded-recipient"

![image](https://github.com/user-attachments/assets/fd46a787-5de7-442d-80bd-166e90498ee0)

If you want to check if the funds were correctly transferred %20 = space to run web!

      curl -X GET "http://192.168.100.170/consulta.php?apikey=b493d48364afe44d&comando=solana%20confirm%20-v%205pbKpF54ZhMfkxmfwxsjACM7VJLqLv4U1syPLrvoWaHQMY4ogfVdz7TKuUUNJFLuiJdRtFtACWvHceg3m12mR6vk"


Ou pode usar um explorador da Solana como:

🔍 Solana Explorer https://explorer.solana.com/?cluster=devnet 

https://explorer.solana.com/tx/5pbKpF54ZhMfkxmfwxsjACM7VJLqLv4U1syPLrvoWaHQMY4ogfVdz7TKuUUNJFLuiJdRtFtACWvHceg3m12mR6vk?cluster=devnet

create wallet command put your name in place TesteplayerName_wallet.json

curl -X GET "http://192.168.100.170/consulta.php?apikey=b493d48364afe44d&comando=solana-keygen%20new%20--no-passphrase%20--outfile%20/solana-token/wallets/TesteplayerName_wallet.json%20--force"

![image](https://github.com/user-attachments/assets/1cfc9993-b687-439e-9efc-d47596de4bcf)


Here is a basic flow diagram for your plugin commands:

---

### **Command Flows**

```plaintext
                          [Player Executes Command]
                                    |
                                    v
        +---------------------------+---------------------------+
        |                           |                           |
    /saldo                     /createwallet              /buycurrency
   Check the balance     Create a Solana wallet          Buy game coins
        |                           |                           |
        v                           v                           v
[Check bank balance]    [Create wallet on Docker]      [Check SOL balance]
        |                           |                           |
        v                           v                           v
[balance to the player] [Saves wallet in the bank]    [Deducts SOL and adds coins]
                                    |                           |
                                    v                           v
                          [Returns wallet address]    [Confirms purchase to the player]
```

---

### **Store Purchase Flow**

```plaintext
                          [Jogador Executa Comando de Compra]
                                    |
                                    v
        +---------------------------+---------------------------+
        |                           |                           |
  /buyapple                   /buyemerald              /buynetherite
  buy apple Encantada       buy Emerald       9 Netherite + book Netherite upgrade
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

### **Solana Transfer Flow**

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

### **Wallet Creation Flow**

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



### **Summary**

- **Balance and Wallet Commands**:
- `/saldo`: Check your bank balance.
- `/createwallet`: Create a Solana wallet.

- **Purchase Commands**:
- `/buyapple`, `/buyemerald`, `/buynetheritepickaxe`, etc.: Purchase specific items.

- **Transfer Commands**:
- `/soltransfer`: Transfer SOL to another player.

If you need more details or adjustments, just let me know! 😊

This video in English helped me create and configure a dock for Solana https://www.youtube.com/watch?v=L4ASwqLZVV0

A father teaching the little ones about economics and digital banking IT and technologies

If you want to create an astral user and an astral folder and a www user

1. Create a User in Linux
To create the user that will have specific permissions:
sudo adduser www

- The command will ask you to set a password and other basic information.

- Make sure that the user only has access to the necessary folders and commands.

2. Create a Group and Add the User
Create a group called dockerusers (or whatever name you prefer) and add the user www:
sudo groupadd dockerusers
sudo usermod -aG dockerusers www

- This will add the user to the group and allow them to run Docker-related commands (if configured).

3. Allow Access to Docker
Ensure that the dockerusers group has permission to use Docker without needing sudo:
sudo usermod -aG docker www

After that, reboot to ensure that the permissions are applied:
newgrp docker

4. Configure Folders
Ensure that the folders required for the Docker command are accessible to the www user:
sudo mkdir -p /home/astral/astralcoin /home/astral/astralcoin/solana-data
sudo chown -R www:www /home/astral/astralcoin


This sets read/write permissions for the www user.

5. Run the Docker Command
Make sure that Docker is installed. Otherwise, install it with:
sudo apt update
sudo apt install docker.io

Then, you can run the command with the www user:
sudo docker run --rm \
-v /home/astral/astralcoin:/solana-token \
-v /home/astral/astralcoin/solana-data:/root/.config/solana \
heysolana solana balance

6. Configure Permissions on the www User
If you need the www user to have access to additional groups, you can edit it directly:
sudo usermod -aG www-data www

The www-data group is commonly used in web configurations, but check if it is necessary before adding it.

7. Document and Simplify
For the tutorial:
- Describe each step in detail.
- Include explanations about security (limiting permissions, preventing unnecessary access, etc.).
- If the script requires additional adjustments, such as environment variables, explain how to set them.

With these steps, you will have a working guide to setting up your Linux environment and running the Docker command in the context of your project. Need help formatting this in plain text or adding more details? 🚀
