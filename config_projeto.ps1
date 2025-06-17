# Solicitar o nome do projeto
$projectName = Read-Host "Digite o nome do projeto"

# Criar uma pasta para o projeto
if (-Not (Test-Path -Path $projectName)) {
    New-Item -ItemType Directory -Path $projectName
    Write-Host "Diretório $projectName criado com sucesso."
} else {
    Write-Host "Diretório $projectName já existe. Continuando..."
}

# Navegar para o diretório do projeto
Set-Location -Path $projectName

# Executar o comando Gradle Init com os parâmetros pré-configurados
Write-Host "Configurando projeto Gradle..."
& gradle init --type java-application --dsl groovy --package "com.$projectName" --test-framework junit-jupiter --project-name $projectName --incubating

Write-Host "Projeto criado com sucesso na pasta $projectName."



# Verificar se o diretório já existe
if (-Not (Test-Path -Path "app")) {
    # Criar a pasta do projeto e o subdiretório "app"
    New-Item -ItemType Directory -Path "app" -Force
    Write-Host "Diretório $projectName criado com sucesso."
} else {
    Write-Host "Diretório $projectName já existe. Continuando..."
}

# Navegar para o subdiretório "app"
Set-Location -Path "app"

# Remover o arquivo build.gradle, se existir
if (Test-Path "build.gradle") {
    Remove-Item "build.gradle"
    Write-Host "Arquivo build.gradle existente foi removido."
}

# Criar o diretório src/main/resources
if (-Not (Test-Path -Path "src/main/resources")) {
    New-Item -ItemType Directory -Path "src/main/resources" -Force
    Write-Host "Estrutura de diretórios src/main/resources criada."
} else {
    Write-Host "Diretório src/main/resources já existe."
}

# Criar o arquivo plugin.yml
$pluginContent = @"
name: $projectName
version: v2.7.2
main: com.$projectName.App
api-version: 1.21.5
author: Amauri Bueno dos Santos
description: Este é um plugin personalizado para Minecraft.
folia-supported: true
"@
$pluginContent | Out-File -FilePath "src/main/resources/plugin.yml" -Encoding UTF8
Write-Host "Arquivo plugin.yml criado com sucesso."

# Criar o arquivo build.gradle
$buildGradleContent = @"
plugins {
    id 'java'
	id 'com.github.johnrengelman.shadow' version '8.1.1' // For packaging dependencies
	id 'io.papermc.paperweight.patcher' version '2.0.0-beta.17'
}

repositories {
    mavenCentral()
    maven { url 'https://repo.papermc.io/repository/maven-public/' }
	maven { url 'https://jitpack.io' } // Adiciona o repositório JitPack
    maven { url 'https://hub.spigotmc.org/nexus/content/repositories/snapshots/' }
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.1'

    compileOnly 'dev.folia:folia-api:1.21.5-R0.1-SNAPSHOT'

    implementation 'com.google.guava:guava:31.1-jre'
    implementation 'mysql:mysql-connector-java:8.0.33'
    implementation 'org.json:json:20210307'
    implementation 'org.xerial:sqlite-jdbc:3.36.0.3'

}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
    options.release = 21
    options.fork = true
}

tasks.named('jar') {
    archiveBaseName.set('$projectName') // Nome personalizado
    archiveVersion.set('v2.7.2')
    archiveClassifier.set('')
    destinationDirectory.set(file("$buildDir/libs"))
}


tasks.test {
    enabled = false
}
"@
$buildGradleContent | Out-File -FilePath "build.gradle" -Encoding UTF8
Write-Host "Arquivo build.gradle criado com sucesso."

# Mensagem final
Write-Host "Projeto $projectName configurado com sucesso!"
pause