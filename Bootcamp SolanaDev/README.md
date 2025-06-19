# English
     The Solana Dev Bootcamp is a course designed for developers who want to learn how to build applications
     on the Solana blockchain. It covers everything from basic blockchain concepts to the development of complete applications, including smart contracts and API integration.
     Regarding Python and Solana, there is an SDK that allows interaction with the blockchain using this language. This can be useful for developing APIs for Java applications and managing transactions on Brok Chan, Solana's ledger.
     As for SHA-384 encryption, it is a more secure version of SHA-256, generating a 384-bit hash instead of 256 bits. This increases resistance against brute force attacks and collisions, making it an interesting choice for blockchain security.
     I use Solana's CLI with access to an isolated Docker 🚢🐳 environment separate from my main Linux system. In this setup, the system executes wallet commands. I may switch to an API in the future, but for now, I still use Docker to store and manage multiple temporary wallets, functioning like a bank for interaction.

     📌 Useful Links:
     🔗 Solana Java SDK [https://solana.com/pt/docs/clients/java]
     🔗 Solana Game SDKs[https://solana.com/pt/docs/clients/game-sdks]
     🔗 Solana Installation Guide [https://solana.com/pt/docs/intro/installation]

     I recommend setting up a primary wallet outside of Docker 🚢🐳 for increased security. The Docker environment can be used by clients to purchase assets using temporary game wallets as passes. That way, the client does not expose their main wallet, transferring funds to the game wallet only at the moment of the purchase. Still, I aim to keep this game wallet      secure, even if network traffic monitoring could expose my key.
     The game network is registered via VPN on a private Minecraft server, where each user must have a registered email, improving security with Tailscale. I'm still working on a system to monitor and register emails. I also considered implementing a password rotation system, where the wallet password changes automatically every hour, further enhancing security.           Wallet access would be controlled via a protected URL.

# Português

     O Bootcamp Solana Dev é um curso voltado para desenvolvedores que desejam aprender a construir aplicações na blockchain Solana. Ele abrange desde conceitos básicos de blockchain até o desenvolvimento de aplicações completas, incluindo contratos inteligentes e integração com APIs.
     Sobre Python e Solana, existe um SDK que permite interagir com a blockchain usando essa linguagem. Isso pode ser útil para desenvolver APIs para seus aplicativos Java e gerenciar transações na Brok Chan, o livro-caixa da Solana.
     Quanto à criptografia SHA-384, ela é uma versão mais segura do SHA-256, gerando um hash de 384 bits em vez de 256 bits. Isso oferece maior resistência contra ataques de força bruta e colisões, tornando-a uma opção interessante para segurança em blockchain.
     Eu utilizo o CLI da Solana com acesso a um Docker 🚢🐳 isolado do meu sistema Linux principal. Lá, o sistema executa comandos da carteira. Talvez eu migre para uma API, mas, por enquanto, ainda utilizo o ambiente Docker para armazenar e gerenciar múltiplas carteiras temporárias, funcionando como um banco para interação.

     
     📌 Links úteis:
     🔗 Solana Java SDK [https://solana.com/pt/docs/clients/java]
     🔗 Solana Game SDKs[https://solana.com/pt/docs/clients/game-sdks]
     🔗 Guia de instalação Solana  [https://solana.com/pt/docs/intro/installation]

     
     Recomendo criar uma carteira principal separada do Docker 🚢🐳, garantindo mais segurança. O Docker pode ser útil para permitir que clientes comprem ativos no jogo com carteiras temporárias, funcionando como um passe. Assim, o cliente não expõe sua carteira principal e transfere fundos para a carteira do jogo apenas no momento da compra da moeda do jogo.           Ainda assim, busco manter essa carteira segura, mesmo que haja monitoramento de tráfego da rede que possa expor minha chave.
     A rede do jogo está cadastrada via VPN em um servidor privado de Minecraft, exigindo que cada usuário tenha um e-mail cadastrado, aumentando a segurança com Tailscale. Ainda estou desenvolvendo um sistema para monitoramento e cadastro de e-mails. Também pensei em implementar um sistema onde a senha da carteira muda automaticamente a cada hora, reforçando a           segurança. O acesso à carteira seria feito via URL protegida.

# Español

     El Bootcamp Solana Dev es un curso diseñado para desarrolladores que desean aprender a construir aplicaciones en la blockchain de Solana. Cubre desde conceptos básicos de blockchain hasta el desarrollo de aplicaciones completas, incluyendo contratos inteligentes e integración con API.
     Sobre Python y Solana, existe un SDK que permite interactuar con la blockchain utilizando este lenguaje. Esto puede ser útil para desarrollar APIs para aplicaciones Java y gestionar transacciones en Brok Chan, el libro mayor de Solana.
     En cuanto a la cifrado SHA-384, es una versión más segura de SHA-256, generando un hash de 384 bits en lugar de 256 bits. Esto proporciona una mayor resistencia contra ataques de fuerza bruta y colisiones, lo que lo convierte en una opción interesante para la seguridad en blockchain.
     Utilizo el CLI de Solana con acceso a un Docker aislado 🚢🐳 separado de mi sistema Linux principal. En este entorno, el sistema ejecuta los comandos de la billetera. Puede que en el futuro cambie a una API, pero por ahora sigo utilizando Docker para almacenar y gestionar múltiples billeteras temporales, funcionando como un banco para la interacción.

     📌 Enlaces útiles:
     🔗 Solana Java SDK [https://solana.com/pt/docs/clients/java]
     🔗 Solana Game SDKs[https://solana.com/pt/docs/clients/game-sdks]
     🔗 Guía de instalación de Solana  [https://solana.com/pt/docs/intro/installation]

     Recomiendo configurar una billetera principal fuera de Docker 🚢🐳 para mayor seguridad. El entorno Docker puede ser utilizado por los clientes para comprar activos con billeteras temporales de juegos como pases. Así, el cliente no expone su billetera principal y transfiere fondos solo en el momento de la compra de la moneda del juego. Aun así, me esfuerzo      por mantener segura esta billetera del juego, incluso si el monitoreo del tráfico de la red pudiera exponer mi clave.
     La red del juego está registrada a través de VPN en un servidor privado de Minecraft, donde cada usuario debe tener un correo electrónico registrado, mejorando la seguridad con Tailscale. Todavía estoy desarrollando un sistema para monitorear y registrar correos electrónicos. También pensé en implementar un sistema de cambio de contraseña automático, donde la      contraseña de la billetera se renueva cada hora, lo que mejora aún más la seguridad. El acceso a la billetera se gestionaría a través de una URL protegida.


# cluster caseiro com dois PCs usando seu servidor com Debian 12 

     como base — e isso é uma ótima ideia se você quer redundância, alta disponibilidade ou até dividir tarefas entre as máquinas.
     Você pode configurar um cluster de alta disponibilidade com ferramentas como:
     - Pacemaker: gerencia os recursos do cluster.
     - Corosync: cuida da comunicação entre os nós.
     - pcs: ferramenta de linha de comando para configurar tudo isso.
     Um exemplo prático seria: você tem um servidor web rodando no Debian 12. Se ele cair, o outro PC assume automaticamente, mantendo o serviço no ar. Isso é feito com failover automático.
     Encontrei um guia em vídeo bem completo que mostra como montar esse tipo de cluster com Debian 12, usando Pacemaker e Corosync. Ele cobre desde a configuração de IP fixo até os testes de failover.
     Se preferir um passo a passo escrito, esse tutorial do Clube do Linux também é excelente — mostra como configurar a rede, instalar os pacotes e testar o cluster.

     

# 📁 Sincronização de arquivos entre os PC

     Sim, é possível! Para que os arquivos de um servidor sejam automaticamente replicados no outro, você pode usar:
     - rsync com cron: sincroniza pastas em intervalos regulares.
     - DRBD (Distributed Replicated Block Device): espelha discos entre servidores — ideal para alta disponibilidade.
     - GlusterFS ou Ceph: sistemas de arquivos distribuídos, ótimos para clusters.
     Se você quer que os dois compartilhem os mesmos arquivos em tempo real, GlusterFS pode ser uma solução bem legal.

# 🌐 Acesso via FTP em cluste

     Dá sim! Você pode configurar o vsftpd (Very Secure FTP Daemon) em ambos os servidores e usar um IP virtual com failover. Assim:
     - O IP flutuante aponta sempre para o servidor ativo.
     - O serviço FTP continua acessível mesmo se um dos PCs for desligado.
     - Os arquivos podem ser armazenados em um volume compartilhado (como o GlusterFS que mencionei).
     Tem um guia excelente de configuração do vsftpd no Debian 12 que mostra como deixar tudo seguro com SSL/TLS e controle de usuários.

     link https://pt.linux-console.net/?p=30685

# 🎮 Cluster para Minecraft (por diversão e ciência, claro 😄)

     Sim, senhor! Você pode rodar o Minecraft Server em um dos PCs e usar o outro como backup ou até para balancear carga (em setups mais avançados). Algumas ideias:
     - Failover simples: se o servidor principal cair, o outro assume com os mesmos arquivos do mundo.
     - Servidor proxy com BungeeCord ou Velocity: conecta vários servidores Minecraft (ideal para minigames ou mundos separados).
     - Backup automático: sincronize os mundos com rsync para não perder nada.
     Tem um tutorial completíssimo de instalação do Minecraft Server no Debian 12 que cobre desde o Java até o firewall e o EULA.

     Se quiser, posso montar um plano de ação com comandos e tudo, baseado no que você já tem aí. Bora transformar esse laboratório caseiro num datacenter gamer-acadêmico? 😎🖥️🎮✨

# Um cluster é basicamente um conjunto de computadores ou servidores que funciona juntos

     como se fossem uma única máquina. Eles são usados para aumentar a performance, a disponibilidade ou a capacidade de processamento de dados. Mas o interessante mesmo está nas possibilidades:
     - Alta performance: para rodar aplicações pesadas, como simulações científicas, modelagem 3D, ou renderização de vídeos.
     - Alta disponibilidade: em empresas que precisam que sistemas fiquem sempre online, mesmo que um servidor falhe, outro assume.
     - Big Data e análise de dados: ferramentas como Apache Hadoop e Spark rodam em clusters para processar grandes volumes de informações.
     - Hospedagem de sites e serviços: grandes portais ou aplicativos que recebem muitos acessos ao mesmo tempo.
     - Inteligência artificial: treinar modelos de IA exige muito poder de processamento — e clusters dão conta do recado.

     links https://www.youtube.com/watch?v=mgCDf-0Ovcg

     

     









