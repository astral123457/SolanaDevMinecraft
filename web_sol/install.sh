# Comandos para instalar o Docker no Ubuntu
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 7EA0A9C3F273FCD8
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Comandos para instalar o Docker no Debian
curl -fsSL https://download.docker.com/linux/debian/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian bookworm stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Comandos gerais para instalação e inicialização do Docker
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl start docker
sudo systemctl enable docker
sudo docker run hello-world

# Comandos relacionados ao Solana
mkdir solana
cd solana
nano Dockerfile

# Conteúdo do Dockerfile
cat <<EOF > Dockerfile
# Use a lightweight base image
FROM debian:bullseye-slim

# Set non-interactive frontend for apt
ENV DEBIAN_FRONTEND=noninteractive

# Install required dependencies and Rust
RUN apt-get update && apt-get install -y \
    curl build-essential libssl-dev pkg-config nano && \
    curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Add Rust to PATH
ENV PATH="/root/.cargo/bin:\$PATH"

# Verify Rust installation
RUN rustc --version

# Install Solana CLI
RUN curl -sSfL https://release.anza.xyz/stable/install | sh && \
    echo 'export PATH="\$HOME/.local/share/solana/install/active_release/bin:\$PATH"' >> ~/.bashrc

# Add Solana CLI to PATH
ENV PATH="/root/.local/share/solana/install/active_release/bin:\$PATH"

# Verify Solana CLI installation
RUN solana --version

# Set up Solana config for Devnet
RUN solana config set -ud

# Set working directory
WORKDIR /solana-token

# Default command to run a shell
CMD ["/bin/bash"]
EOF

# Outro conteúdo relacionado ao Solana
docker build -t heysolana .
docker run -it --rm -v $(pwd):/solana-token -v $(pwd)/solana-data:/root/.config/solana heysolana
solana-keygen grind --starts-with dad:1
ls
dad-your-token-acount.json
solana config set --keypair dad-your-token-acount.json
solana config set --url devnet
solana config get
solana address
mkdir wallets
# Acesse https://faucet.solana.com/ para receber SOL na Devnet

# Informações sobre a Devnet do Solana (INSS DEV SOLANA)
echo "INSS DEV SOLANA: Esta dev coin pode ser obtida a cada 8 horas, com 2 drops de valores de 1 a 5 em https://faucet.solana.com/. Não é necessário se identificar, apenas coloque o endereço da sua carteira a cada 8 horas diariamente. O sistema identificará se a sua carteira já recebeu."
echo "INSS = Institutional Network Solana Secret..."

# Configuração do Apache2
apache2 config
exit
nano /etc/apache2/apache2.conf

# Conteúdo do arquivo de configuração do Apache2
cat <<EOF > /etc/apache2/apache2.conf
<Directory />
    Options FollowSymLinks
    AllowOverride None
    Require all granted
</Directory>
EOF

# Informações sobre o SolanaDevMinecraft Token
echo "SolanaDevMinecraft: O token Solana Dev não tem valor real e é usado apenas para diversão e experimentação econômica. Não está sujeito a regulamentações ou leis financeiras, funcionando unicamente como um ativo fictício dentro de um ambiente de teste e interações lúdicas. Seu propósito é proporcionar entretenimento e aprendizado sobre conceitos econômicos sem envolver transações financeiras reais."
echo "INSS DEV SOLANA: esta dev coin pode ser obtida a cada 8 horas, 2 drops de valores de 1 a 5 em https://faucet.solana.com/. Como é público, não há necessidade de se identificar, apenas coloque o endereço da sua carteira a cada 8 horas todos os dias, eles saberão se sua carteira já recebeu."
echo "INSS = Institutional Network Solana Secret..."

# Exemplo de URL de consulta
echo "Exemplo URL: http://your_server/consulta.php?apikey=2f4164616e614e61&command=your_command_here"

# Melhorias de segurança recomendadas para API Key em PHP
echo "Segurança Aprimorada (Recomendado):"
echo "Chave de API Real: Em vez de derivar de 'banana', gere uma chave de API longa e aleatória (por exemplo, 32 ou 64 caracteres hexadecimais aleatórios) e armazene-a diretamente no script ou em um arquivo de configuração."
echo "PHP:"
echo "// No início do script:"
echo "// \$correctApiKey = 'your_random_very_long_and_secure_key_here';"

# Comandos para adicionar o usuário www-data ao grupo docker e configurar sudo sem senha para docker
sudo usermod -aG docker www-data
sudo nano /etc/sudoers

# Conteúdo para adicionar ao arquivo /etc/sudoers
echo "# Allow members of group sudo to execute any command"
echo "%sudo ALL=(ALL:ALL) ALL"
echo ""
echo "www-data ALL=(ALL) NOPASSWD: /usr/bin/docker"

# Comandos para criar e editar o arquivo shell.php
cd /var/www/html
nano shell.php

# Conteúdo do arquivo shell.php (incompleto na sua mensagem)
echo "<?php"
echo "ini_set('display_errors', 1);"
echo "ini_set('display_startup_errors', 1);"
echo "error_reporting(E_"
