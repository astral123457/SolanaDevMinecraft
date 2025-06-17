# Solicitar o nome do projeto
$projectName = Read-Host "Digite o nome do projeto"

# Navegar para o diretório do projeto
Set-Location -Path $projectName

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
version: 1.0
main: $projectName.App
api-version: 1.19
author: Amauri
description: Este é um plugin personalizado para Minecraft.
"@
$pluginContent | Out-File -FilePath "src/main/resources/plugin.yml" -Encoding UTF8
Write-Host "Arquivo plugin.yml criado com sucesso."

# Criar o arquivo build.gradle
$buildGradleContent = @"
plugins {
    id 'java'
}

repositories {
    mavenCentral()
    maven { url 'https://repo.papermc.io/repository/maven-public/' }
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.1'
    compileOnly 'io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT'
    implementation 'com.google.guava:guava:31.1-jre'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
    options.compilerArgs << "-Xlint:deprecation"
}

tasks.named('jar') {
    archiveBaseName.set('$projectName') // Nome personalizado
    archiveVersion.set('1.33') // Versão opcional
}
"@
$buildGradleContent | Out-File -FilePath "build.gradle" -Encoding UTF8
Write-Host "Arquivo build.gradle criado com sucesso."

# Mensagem final
Write-Host "Projeto $projectName configurado com sucesso!"
pause