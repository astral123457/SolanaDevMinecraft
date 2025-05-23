# SolanaDevMinecraft 1.19.x for 1.20.x for 1.21.x Paper, that is, it also works with modern server fabric and mods, has two Spigot or Paper.
This tutorial teaches you how to create a server on your PC as well as how to configure the plugin and the PHP Solana API that I made using Docker.

![image](https://github.com/user-attachments/assets/0c31614f-d56e-417b-9429-ad9055f86395)


# Installation Api WEB
    su -
#
    apt install curl
# 
     curl -s "https://raw.githubusercontent.com/astral123457/SolanaDevMinecraft/refs/heads/main/web_sol/ogp_install_openpanelgamer.sh" -o ogp_install_openpanelgamer.sh
#
     chmod +x ogp_install_openpanelgamer.sh
#
     ./ogp_install_openpanelgamer.sh
#
     curl -s "https://raw.githubusercontent.com/astral123457/SolanaDevMinecraft/refs/heads/main/web_sol/install.sh" -o install.sh
#
     chmod +x install.sh
#
     ./install.sh
#
     sudo usermod -aG docker www-data
#
     sudo chmod 666 /var/run/docker.sock
# SQL
     sudo mysql -u root -p -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY '0073007'; CREATE DATABASE banco; FLUSH PRIVILEGES;"

#
       sudo nano /etc/sudoers

# Allow members of group sudo to execute any command
 
     %sudo   ALL=(ALL:ALL) ALL
#
     www-data ALL=(ALL) NOPASSWD: /usr/bin/docker
#
![image](https://github.com/user-attachments/assets/73539364-0c14-4054-aa88-68e802fc0f54)


     sudo systemctl restart apache2
#
     

Example URL:
http://your_server/consulta.php?apikey=b493d48364afe44d&comando=your_command_here

consulta.php?apikey=b493d48364afe44d&comando=ls
# ls
http://your_server/consulta.php?apikey=b493d48364afe44d&ls
# create wallet command put your name in place TesteplayerName_wallet.json
http://your_server/consulta.php?apikey=b493d48364afe44d&comando=solana-keygen%20new%20--no-passphrase%20--outfile%20/solana-token/wallets/TesteplayerName_wallet.json%20--force

curl -X GET "http://192.168.100.170/consulta.php?apikey=b493d48364afe44d&comando=solana-keygen%20new%20--no-passphrase%20--outfile%20/solana-token/wallets/TesteplayerName_wallet.json%20--force"

Security Enhancements (Recommended):

Real API Key: Instead of deriving from "banana", generate a long, random API key (e.g. 32 or 64 random hexadecimal characters) and store it directly in the script or in a configuration file.
PHP


![image](https://github.com/user-attachments/assets/d4876677-67bd-47a1-bcdd-952afe291502)




debian-12.9.0-amd64-netinst.iso

![image](https://github.com/user-attachments/assets/32aacb40-5edd-4d85-b4c5-7a0501fe5be3)
#segure bot off 1
#connect 2
![image](https://github.com/user-attachments/assets/3587d163-78cb-4ace-8600-18619774d4f2)


Name: Debina 12 

Generation: Generation 2 

Memory: 4024 MB 

Network: Default Switch 

Hard Disk: C:ProgramDataMicrosoftWindowsVirtual Hard DisksDebina 12.vhdx (VHDX, dynamic expansion) 

Operating System: Will be installed from C:Usersbzm36Desktopdebian-12.9.0-amd64-netinst.iso

![image](https://github.com/user-attachments/assets/001add6f-b860-497d-b527-f3d1a14e6ecf)

![image](https://github.com/user-attachments/assets/880d12b2-a029-49a1-842c-7ed69e66008a)





# panel gamer Open Game Panel Php e Mysql config

https://github.com/astral123457/SolanaDevMinecraft/blob/main/web_sol/ogp.txt

# mysql Remot Control 

https://github.com/astral123457/SolanaDevMinecraft/blob/main/web_sol/maria_banco_de_dados.txt

# vpn for frendys tailscale 

![image](https://github.com/user-attachments/assets/ea73cc42-234a-47a7-811a-993053c5ca61)

    ssh name_user@ip
    yes
    password *****
    su -
    password

    apt install curl
    curl -fsSL https://tailscale.com/install.sh | sh
    tailscale up

![image](https://github.com/user-attachments/assets/212504f6-327d-467b-ae49-4e0428a5fca0)


# doc linux debian 12 solana console create and configure a dock

#
    sudo apt-get install ca-certificates curl
#
    sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
#
    sudo chmod a+r /etc/apt/keyrings/docker.asc
#
    sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 7EA0A9C3F273FCD8
#
    echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
    $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
    sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
#
    curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
#
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian bookworm stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
#
    sudo apt update
#
    sudo apt install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
#
    sudo systemctl start docker
#
    sudo systemctl enable docker
#
    sudo docker run hello-world    
#
🚢🐳 Nice Jobs Steve
    
root@debian:~#

    mkdir solana

root@debian:~#

    cd solana

root@debian:~/solana#

    nano Dockerfile

------------------------DockerfileBegin-----------------------------
    # Use a lightweight base image
    FROM debian:bullseye-slim

    # Set non-interactive frontend for apt
    ENV DEBIAN_FRONTEND=noninteractive

    # Install required dependencies and Rust
    RUN apt-get update && apt-get install -y \
    curl build-essential libssl-dev pkg-config nano \
    && curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

    # Add Rust to PATH
    ENV PATH="/root/.cargo/bin:$PATH"

    # Verify Rust installation
    RUN rustc --version

    # Install Solana CLI
    RUN curl -sSfL https://release.anza.xyz/stable/install | sh \
    && echo 'export PATH="$HOME/.local/share/solana/install/active_release/bin:$PATH"' >> ~/.bashrc

    # Add Solana CLI to PATH
    ENV PATH="/root/.local/share/solana/install/active_release/bin:$PATH"

    # Verify Solana CLI installation
    RUN solana --version

    # Set up Solana config for Devnet
    RUN solana config set -ud

    # Set working directory
    WORKDIR /solana-token

    # Default command to run a shell
    CMD ["/bin/bash"]

------------------------DockerfileEnd-----------------------------

https://blog.networkchuck.com/posts/create-a-solana-token/

This video in English helped me create and set up a dock for Solana https://www.youtube.com/watch?v=L4ASwqLZVV0 

A father teaching his daughters about economics and digital banking technologies.

# install CLi SOLANA pLUGI p SOL sOLANA

    docker build -t heysolana .
#
    docker run -it --rm -v $(pwd):/solana-token -v $(pwd)/solana-data:/root/.config/solana heysolana
#
    solana-keygen grind --starts-with dad:1
#


ls 

# dad-your-token-acount.json

OBS 

    solana config set --keypair dad-your-token-acount.json
    
#
    solana config set --url devnet
#
    solana config get
#
    solana address
#
    mkdir wallets
    
# https://faucet.solana.com/ to have SOL airdropped # INSS DEV SOLANA

because it is public, there is no need to identify yourself, just do the work of putting your wallet in every 8 hours every day, they will know if your wallet has already been received ?
# INSS = Institutional Network Solana Secret...

    solana balance

# apache2 config

   exit
#    
   nano /etc/apache2/apache2.conf

# ________Apache2.conf_________remove Require all denied 
    <Directory />
        Options FollowSymLinks
        AllowOverride None
        Require all granted                           
    </Directory>
# ________Apache2.conf_________ add Require all granted

#

       sudo usermod -aG docker www-data
#
       sudo nano /etc/sudoers

# Allow members of group sudo to execute any command
 
        %sudo   ALL=(ALL:ALL) ALL
#
        www-data ALL=(ALL) NOPASSWD: /usr/bin/docker
#
![image](https://github.com/user-attachments/assets/73539364-0c14-4054-aa88-68e802fc0f54)


        sudo systemctl restart apache2
#
    
# SolanaDevMinecraft

The Solana Dev token has no real value and is used only for fun and economic experimentation. It is not subject to regulations or financial laws,

functioning solely as a fictional asset within a testing environment and playful interactions. Its purpose is to provide entertainment and learning about economic concepts without involving real financial transactions. 
# INSS DEV SOLANA
this dev coin can be obtained every 8 hours, 2 drops of values from 1 to 5
https://faucet.solana.com/

because it is public, there is no need to identify yourself, just do the work of putting your wallet in every 8 hours every day, they will know if your wallet has already been received ?

# INSS = Institutional Network Solana Secret...


![image](https://github.com/user-attachments/assets/1a40a6c9-a8fa-44f8-bf96-59b5109b015e)


Example URL:
http://your_server/consulta.php?apikey=2f4164616e614e61&command=your_command_here

Security Enhancements (Recommended):

Real API Key: Instead of deriving from "banana", generate a long, random API key (e.g. 32 or 64 random hexadecimal characters) and store it directly in the script or in a configuration file.
PHP

// At the beginning of the script:
// $correctApiKey = 'your_random_very_long_and_secure_key_here';


![image](https://github.com/user-attachments/assets/3808718f-a04c-48f1-b4cf-7cde0e76b7f7)


        
# See sudoers(5) for more information on "@include" directives:

# if you want to set up a nice complete php and mysql panel for minecraft (I recommend it and it's just another command tutorial but you learn and configure everything yourself)

https://github.com/astral123457/SolanaDevMinecraft/blob/main/web_sol/ogp.txt

# then there are the configuration of the remote mysqlmaria access (I also recommend)

https://github.com/astral123457/SolanaDevMinecraft/blob/main/web_sol/maria_banco_de_dados.txt

![image](https://github.com/user-attachments/assets/133f8767-edc6-4081-8921-1c6d1cdf2fad)


    sudo apt -y install apache2 curl subversion php8.2 php8.2-gd php8.2-zip libapache2-mod-php8.2 php8.2-curl php8.2-mysql php8.2-xmlrpc php8.2-mbstring php8.2-xmlrpc php8.2-bcmath

#

    sudo a2enmod php8.2

#
    sudo systemctl restart apache2

#

Test 1 root@debian:

#

    cd /var/www/html

#

    nano shell.php

#

     
    <?php
    ini_set('display_errors', 1);
    ini_set('display_startup_errors', 1);
    error_reporting(E_ALL);

    $command = "sudo -u www-data docker run --rm -v /root/solana:/solana-token -v /root/solana/solana-data:/root/.config/solana heysolana ls";
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



# Api consulta.php:
        nano consulta.php

https://github.com/astral123457/SolanaDevMinecraft/blob/main/consulta.php

    CTRL + S (salve)
    CTRL + X (exite)

![image](https://github.com/user-attachments/assets/ef577acc-a11e-440b-b63a-da61738b7098)


# SQL
    mysql -u root -p
# SQL
    SHOW DATABASES;
# SQL
    CREATE DATABASE banco;
# SQL
    exit
#


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

/refundSolana <signature>


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

# Dependencia mod do Fabric server https://fabricmc.net/use/server/

          Cardboard-1.21.4.jar (Cardboard-1.19.x or 1.20.x or 1.21.x)	
          
          fabric-api-0.119.21.21.4.jar 	 
          
          iCommon-Fabric-bundle1.jar (1.19 a 1.21.5)

          antixray-fabric-1.4.9+1.21.4.jar

          ViaBackwards-5.3.2.jar

          ViaFabric-0.4.17+91-main.jar

          ViaVersion-5.3.2.jar
          
# Plugin Custom

          EssentialsX-2.21.0.jar (/back /sethome /tpa) Basic commads

          Vault 1.7.3.jar

          AuthMe-5.6.0-legacy.jar
          
          ProtocolLib.jar
          
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

# Recover wallet if optional needed

solana-keygen recover --force

will ask to type the secret phrase

root@f702df0e10ba:/solana-token# solana-keygen recover --force
[recover] seed phrase:
[recover] If this seed phrase has an associated passphrase, enter it now. Otherwise, press ENTER to continue:
Recovered pubkey `"7PJBFH7sRPDjmQk7n2qQzaFSn4oxsMv8BFS43vKKb3S2"`. Continue? (y/n):
y
Wrote recovered keypair to /root/.config/solana/id.json

and then you have to place it in the correct directory

mv /root/.config/solana/id.json /solana-token/wallets/007amauri_wallet.json

if you want to buy Solana and link to the real wallet
The official website of Solana Pay is solanapay.com. There you will find information on how to integrate low-cost decentralized payments using the Solana blockchain.
Sem problemas! Se você quiser configurar uma página para doações com Solana Pay, pode começar criando uma carteira Solana e gerando um link de pagamento. No site Solana Pay, há documentação detalhada sobre como fazer isso.
Se precisar de ajuda para dar os primeiros passos, posso te guiar! Quer começar criando uma carteira Solana? 🚀😊

# O comando 

     solana config set --url devnet

configura o ambiente Solana para a rede de desenvolvimento gratuita (Devnet), onde você pode testar transações sem custos reais.
Para configurar a rede paga, que envolve transações na blockchain real, você deve apontar para a Mainnet com o seguinte comando:

     solana config set --url mainnet-beta
     
Isso conectará sua configuração à rede principal do Solana, onde as transações envolvem SOL reais e são registradas na blockchain oficial

# onde compra Solana

https://solanapay.com/

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/H2H411P12P)

