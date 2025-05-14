# SolanaDevMinecraft 1.19.x to 1.20.x to 1.21.x Paper i.e. it also works with modern server fabric and mods



![image](https://github.com/user-attachments/assets/d4876677-67bd-47a1-bcdd-952afe291502)




debian-12.9.0-amd64-netinst.iso

![image](https://github.com/user-attachments/assets/32aacb40-5edd-4d85-b4c5-7a0501fe5be3)

Nome: Debina 12
Geração: Geração 2
Memória: 4024 MB
Rede: Default Switch
Disco Rígido: C:\ProgramData\Microsoft\Windows\Virtual Hard Disks\Debina 12.vhdx (VHDX, expansão dinâmica)
Sistema Operacional: Será instalado de C:\Users\bzm36\Desktop\debian-12.9.0-amd64-netinst.iso

![image](https://github.com/user-attachments/assets/001add6f-b860-497d-b527-f3d1a14e6ecf)

![image](https://github.com/user-attachments/assets/880d12b2-a029-49a1-842c-7ed69e66008a)





# panel gamer Open Game Panel Php e Mysql config

https://github.com/astral123457/SolanaDevMinecraft/blob/main/web_sol/ogp.txt

# mysql Remot Control 

https://github.com/astral123457/SolanaDevMinecraft/blob/main/web_sol/maria_banco_de_dados.txt

# vpn for frendys tailscale 

![image](https://github.com/user-attachments/assets/ea73cc42-234a-47a7-811a-993053c5ca61)


    curl -fsSL https://tailscale.com/install.sh | sh

# doc linux debian 12 solana console create and configure a dock

To configure everything first, I recommend watching this video in English, it helped me create and configure a dock for Solana https://www.youtube.com/watch?v=L4ASwqLZVV0

# Dependencia mod do Fabric server https://fabricmc.net/use/server/

          Cardboard-1.21.4.jar (Cardboard-1.19.x or 1.20.x or 1.21.x)	
          
          fabric-api-0.119.21.21.4.jar 	 
          
          iCommon-Fabric-bundle1.jar (1.19 a 1.21.5)

          antixray-fabric-1.4.9+1.21.4.jar
          
# Plugin Custom

          EssentialsX-2.21.0.jar (/back /sethome /tpa) Basic commads
          
          SolanaDevMinecraftAstraL-1.33-all.jar


# Option custom mod do Fabric user https://fabricmc.net/use/installer/

          fabric-api-0.119.2+1.21.4.jar
          Essential-1.21.4.jar
          iris-fabric-1.8.8+mc1.21.4.jar
          sodium-fabric-0.6.13+mc1.21.4.jar
          Xaeros_Minimap_25.2.0_Fabric_1.21.4.jar
          XaerosWorldMap_1.39.4_Fabric_1.21.4.jar

          entity_model_features_fabric_1.21.4-2.4.1.jar
          entity_texture_features_fabric_1.21.4-6.2.10.jar

# Option custom resourcepacks

          enchant icons 1.21.4 v1.3.zip






SolanaDevMinecraft

The Solana Dev token has no real value and is used only for fun and economic experimentation. It is not subject to regulations or financial laws,

functioning solely as a fictional asset within a testing environment and playful interactions. Its purpose is to provide entertainment and learning about economic concepts without involving real financial transactions.

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

       sudo usermod -aG docker www-data

       sudo nano /etc/sudoers

 # Allow members of group sudo to execute any command
 
        %sudo   ALL=(ALL:ALL) ALL

        www-data ALL=(ALL) NOPASSWD: /usr/bin/docker
# See sudoers(5) for more information on "@include" directives:

Test 1 root@debian:/var/www/html# 

        nano shell.php

        <?php
        $command = "sudo -u www-data docker run --rm -v /root/solana:/solana-token -v /root/solana/solana-data:/root/.config/so>$output = shell_exec($command);
        echo "<pre>Comando executado: $command</pre>";
        echo "<pre>Saída do comando:\n$output</pre>";
        ?>
or Test 2

         nano shell.php
         
        <?php
        $command = "sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana ls 2>&1";
        $output = shell_exec($command);
        echo "<pre>Comando executado: $command</pre>";
        echo "<pre>Saída do comando:\n$output</pre>";
        ?>

![image](https://github.com/user-attachments/assets/12b87ece-8d45-4990-86a6-a463f5c537a5)


rm shell.php (test)


       

![image](https://github.com/user-attachments/assets/454fedd8-840c-472e-a1d1-f19be016b9a6)
test dock option 1

       sudo -u www-data docker run --rm -v /home/astral/astralcoin:/solana-token -v /home/astral/astralcoin/solana-data:/root/.config/solana heysolana ls

test dock option 2
       
       sudo -u www-data docker run --rm -v /root/solana:/solana-token -v /root/solana/solana-data:/root/.config/solana heysolana ls

![image](https://github.com/user-attachments/assets/6d32ebfb-3f88-45f2-b143-b2ccf1c75c9e)

        sudo -u www-data docker run --rm -v /root/solana:/solana-token -v /root/solana/solana-data:/root/.config/solana heysolana mkdir wallets



Api consulta.php:


![image](https://github.com/user-attachments/assets/ef577acc-a11e-440b-b63a-da61738b7098)


         http://100.85.188.51/consulta.php?apikey=b493d48364afe44d&comando=ls


![image](https://github.com/user-attachments/assets/4740e2a3-0960-4603-b7b0-17b2d1ef135f)





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
  buy apple Enchanted       buy Emerald       9 Netherite + book Netherite upgrade
        |                           |                           |
        v                           v                           v
[Check balance in the bank]   [Check balance in the bank]   [Check balance in the bank]
        |                           |                           |
        v                           v                           v
[Deduct player's balance]   [Deduct player's balancer]   [Deduct player's balance]
        |                           |                           |
        v                           v                           v
[Add item to player]         [Add item to player]        [Add item to player]
        |                           |                           |
        v                           v                           v
[Confirm purchase to the player] [Confirm purchase to the player] [Confirm purchase to the player]



  buyapple:🍎
    description: Buy a magic apple.
    usage: /buyapple
  buyemerald:💎
    description: Buy an Emerald.
    usage: /buyemerald
  buydiamond:💎
    description: Compra um Diamante.
    usage: /buydiamond
  buygold:🏆
    description: Buy a gold bar.
    usage: /buygold
  buyiron:🔨
    description: Buy an Iron Bar.
    usage: /buyiron
  buynetherite:🥈
    description: Buy a block and a Netherite upgrade book.
    usage: /buynetherite
  buySpinningWand:🖍🖌
    description: Buy a Twirling Wand.
    usage: /buySpinningWand
  buyLapis:✏✒🖋🖊
    description: Buy a Lapis Lazuli.
    usage: /buyLapis
  buyQuartz:
    description: Buy a Quartz.
    usage: /buyQuartz
  buyRedstone:🧧
    description: Buy a Redstone.
    usage: /buyRedstone
  buyClay:🧥
    description: Buy a block of clay.
    usage: /buyClay
  buySandBlock:👝
    description: Buy a block of sand.
    usage: /buySandBlock
  buyAllTools:
    description: Buy all the tools.
    usage: /buyAllTools
  buyAllFood:
    description: Buy all the food.
    usage: /buyAllFood
  buySimpleBook:
    description: Buy a Simple Book.
    usage: /buySimpleBook
  buyEmeraldBlock:
    description: Buy an Emerald Block.
    usage: /buyEmeraldBlock
  buySimpleMap:
    description: Buy a Simple Map. 🎉🎇🎁🎃there is a special effect that enchants the pickaxe if you
 are holding it and any piece of armor must be held
    usage: /buySimpleMap
  buySimpleCompass:
    description: Buy a Simple Compass. ⌚🧿🔮 Here is a secret: if you are holding the sword at the time
 of purchasing the compass, it grants enchantments to the sword...🎇🎆✨🎉
    usage: /buySimpleCompass
  buySimpleFishingRod:
    description: Buy a simple fishing rod.
    usage: /buySimpleFishingRod
  buyAxolotlBucket:
    description: Bucket with simple fish.
    usage: /buyAxolotlBucket
transfertokengamer:
description: Transfers currency from the bank to the player.
usage: /transfertokengamer <amount>


```

---

### **Solana Transfer Flow**

```plaintext
                          [Player Executes /soltransfer]
                                    |
                                    v
                          [Check sender's wallet]
                                    |
                                    v
                        [Check the sender's SOL balance]
                                    |
                                    v
                        [Executes transfer via Docker]
                                    |
                                    v
                        [Records transaction in the bank]
                                    |
                                    v
                        [Confirm transfer to the player]
```

---

### **Wallet Creation Flow** 

The idea is to have a very visible wallet where you put a little money just to buy the game currency, it is not an invisible secret wallet.

```plaintext
                          [Player Executes /createwallet]
                                    |
                                    v
                        [Check if a wallet already exists]
                                    |
                                    v
                        [Create a wallet via Docker]
                                    |
                                    v
                        [Save wallet address in the bank]
                                    |
                                    v
                        [Returns the address to the player]
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

# config.yml

database:

      url: jdbc:mysql://debian.tail561849.ts.net:3306/banco

      user: root
      
      password: "0073007"
      
      database_solana_minecraft: "banco"

docker:

      host: debian.tail561849.ts.net
      
      base_path: /home/astral/astralcoin
      
      solana_command: heysolana
      
      wallet_bank_store_admin: "dadhcDXHiHDrWkT2Z4pSZyF6HWmHwQMG3HtGciwccVP"
      
      api_web_key: "b493d48364afe44d"

store:

  value_of_in-game_currency: "1000" # 1 SOL = 1000 moedas do jogo
  
  price:
        apple: 500
        
        emerald: 1000
        
        lapis: 1000
        
        quartz: 1000
        
        redstone: 1000
        
        clay: 1000
        
        buySandBlock: 1000
        
        buySpinningWand: 800
        
        buyEmeraldBlock: 10000
        
        buyDiamondBlock: 50000
        
        buyGoldBlock: 20000
        
        buyIronBlock: 10000
        
        buyNetheriteBlock: 100000
        
        buyAllTools: 5000
        
        buyAllEnchantmentBooks: 5000
        
        buyAllFood: 2000
        
        buySimpleBook: 50
        
        buySimpleMap: 100
        
        buySimpleCompass: 150
        
        buySimpleFishingRod: 200
        
        buyAxolotlBucket: 400

language:

  default: "pt-BR"
  
  supported:
  
    - "pt-BR"
    
    - "en-US"
    
    - "es-ES"
    
    - "fr-FR"  # Francês off
    
    - "de-DE"  # Alemão off 
    
    - "it-IT"  # Italiano off 
    
    - "ja-JP"  # Japonês off
    
    - "ko-KR"  # Coreano off
    
    - "zh-CN"  # Chinês simplificado off
    
    - "zh-TW"  # Chinês tradicional off
    
    - "ru-RU"  # Russo off
    
    - "ar-SA"  # Árabe off
    
    - "hi-IN"  # Hindi off
    
    - "pt-PT"  # Português de Portugal off
    
    - "es-MX"  # Espanhol do México off
    
    - "fr-CA"  # Francês canadense off
    
    - "de-AT"  # Alemão da Áustria off
    
    - "it-CH"  # Italiano da Suíça off
    
    - "ja-JP"  # Japonês do Japão off
    
    - "ko-KR"  # Coreano da Coreia do Sul off


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


               sudo usermod -aG docker www-data

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


root@debian:~/solana# bash carteira.sh

root@350bf931f092:/solana-token# solana-keygen new -o /root/.config/solana/id.json

Generating a new keypair

For added security, enter a BIP39 passphrase

NOTE! This passphrase improves security of the recovery seed phrase NOT the
keypair file itself, which is stored as insecure plain text

BIP39 Passphrase (empty for none):
Enter same passphrase again:

sudo docker run --rm \ -v /home/astral/astralcoin:/solana-token\ -v /home/astral/astralcoin/solana-data:/root/.config/solana \heysolana solana balance

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

![image](https://github.com/user-attachments/assets/99b53f7e-d58c-4877-bc74-994e947f7932)

