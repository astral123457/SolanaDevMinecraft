#!/bin/bash

# Tutorial de instalação Open Game Panel

echo "Atualizando lista de pacotes..."
sudo apt-get update -y

echo "Realizando upgrade dos pacotes..."
sudo apt-get upgrade -y

echo "Instalando dependências para o Agente OGP..."
sudo apt-get install -y libxml-parser-perl libpath-class-perl perl-modules screen rsync sudo e2fsprogs unzip libarchive-extract-perl pure-ftpd libarchive-zip-perl libc6 libgcc1 git curl

echo "Instalando dependências adicionais para o Agente OGP (arquiteturas 32 bits)..."
sudo apt-get install -y libc6-i386 libgcc1:i386 lib32gcc-s1

echo "Instalando dependência para o Agente OGP..."
sudo apt-get install -y libhttp-daemon-perl

echo "Verificando e instalando sudo (caso necessário)..."
sudo apt-get install -y sudo

echo "Adicionando usuário amauri ao grupo sudo..."
sudo usermod -aG sudo amauri

echo "Instalando dependência adicional para o Agente OGP..."
sudo apt-get install -y libarchive-extract-perl

echo "Baixando o instalador mais recente do Agente OGP..."
wget -N "https://github.com/OpenGamePanel/Easy-Installers/raw/master/Linux/Debian Ubuntu/ogp-agent-latest.deb" -O "ogp-agent-latest.deb"

echo "Instalando o Agente OGP..."
sudo dpkg -i "ogp-agent-latest.deb"

echo "Exibindo a senha padrão do usuário ogp_agent..."
sudo cat /root/ogp_user_password

echo "Instalando dependências adicionais (php-gettext, git, php-bcmath)..."
sudo apt -y install php-php-gettext
sudo apt -y install libc6-dbg gdb valgrind

echo "Instalando dependências para o Painel OGP..."
sudo apt -y install apache2 curl subversion php8.2 php8.2-gd php8.2-zip libapache2-mod-php8.2 php8.2-curl php8.2-mysql php8.2-xmlrpc php8.2-mbstring php8.2-xmlrpc php8.2-bcmath

echo "Instalando dependências adicionais do PHP..."
sudo apt -y install php8.2-xmlrpc
sudo apt -y install php8.2-bcmath
sudo apt -y install php-pear

echo "Instalando o servidor MariaDB..."
sudo apt install mariadb-server -y

echo "Executando a configuração de segurança do MariaDB..."
sudo mysql_secure_installation

echo "Criando o diretório fastdl..."
sudo mkdir /var/www/html/fastdl

echo "Criando o arquivo info.php para teste do Apache..."
sudo nano /var/www/html/fastdl/info.php <<EOL
<?php
phpinfo();
?>
EOL

echo "Reiniciando o serviço Apache..."
sudo service apache2 restart

echo "Criando o usuário e banco de dados para o OGP Panel no MariaDB..."
sudo mariadb <<EOL
CREATE USER 'ogpuser'@'%' IDENTIFIED BY '0073007';
REVOKE ALL PRIVILEGES ON *.* FROM 'ogpuser'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'ogpuser'@'%' REQUIRE NONE WITH GRANT OPTION MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;
CREATE DATABASE ogp_panel;
EOL

echo "Criando a tabela de exemplo todo_list no banco de dados ogp_panel..."
sudo mariadb ogp_panel <<EOL
CREATE TABLE todo_list (
    item_id INT AUTO_INCREMENT,
    content VARCHAR(255),
    PRIMARY KEY(item_id)
);
INSERT INTO todo_list (content) VALUES ("Ok meu banco de dados :) Amauri Mysql");
EOL

echo "Criando o arquivo todo_list.php para teste de conexão com o banco de dados..."
sudo nano /var/www/html/fastdl/todo_list.php <<EOL
<?php
\$user = "ogpuser";
\$password = "0073007";
\$database = "ogp_panel";
\$table = "todo_list";

try {
  \$db = new PDO("mysql:host=localhost;dbname=\$database", \$user, \$password);
  echo "<h2>TODO</h2><ol>";
  foreach(\$db->query("SELECT content FROM \$table") as \$row) {
    echo "<li>" . \$row['content'] . "</li>";
  }
  echo "</ol>";
} catch (PDOException \$e) {
    print "Error!: " . \$e->getMessage() . "<br/>";
    die();
}
?>
EOL

echo "Instalando o Painel PHP do Open Game Panel..."
wget -N "https://github.com/OpenGamePanel/Easy-Installers/raw/master/Linux/Debian Ubuntu/ogp-panel-latest.deb" -O "ogp-panel-latest.deb"
sudo dpkg -i "ogp-panel-latest.deb"

echo "Removendo o arquivo de instalação do Painel OGP..."
cd /var/www/html/
sudo rm -r install.php

echo "Exibindo informações de configuração do MySQL para o Painel OGP..."
sudo cat /root/ogp_panel_mysql_info

echo "Alterando as permissões do diretório fastdl..."
sudo chmod 777 /var/www/html/fastdl/

echo "Finalizado! Siga as instruções adicionais sobre a libgcc_s.so.1 e configuração do MariaDB."
echo "Lembre-se de acessar http://localhost/install.php para configurar o Painel OGP."
