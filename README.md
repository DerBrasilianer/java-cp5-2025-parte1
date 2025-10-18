# Chat Criptografado RSA - Checkpoint 5 (Parte 1 - Criptografia)

**Grupo:** LTAKN
- Enzo Prado Soddano — RM557937
- Lucas Resende Lima — RM556564
- Vinicius Prates Altafini — RM559183

---

## 📌 Descrição
Este projeto é a **Parte 1** do Checkpoint 5 de Java Advanced, implementando um sistema de chat cliente-servidor com criptografia RSA.

- **Comunicação TCP/IP** usando Sockets Java
- **Criptografia RSA** para todas as mensagens
- **Troca automática de chaves públicas** entre cliente e servidor
- **Comunicação bidirecional** criptografada
- **Interface de linha de comando** para envio e recebimento de mensagens
- **Validação com simulador RSA** da Drexel University

---

## 🚀 Funcionalidades

- 🔒 **Geração automática de chaves RSA** (p, q, n, φ, e, d)
- 📡 **Troca segura de chaves públicas** entre cliente e servidor
- 💬 **Chat em tempo real** com mensagens criptografadas
- 🔄 **Comunicação bidirecional** (cliente ⇔ servidor)
- ⚡ **Threads separadas** para leitura e escrita
- 🛑 **Comando de saída** controlado (`exit`)

---

## 🛠️ Tecnologias

- **Java 21**
- **Maven**
- **Sockets TCP/IP**
- **Algoritmo RSA**
- **BigInteger** para operações criptográficas

---

## ⚙️ Como executar

### Pré-requisitos
- Java 21
- Maven

### 1. Clonar o repositório
```bash
git clone https://github.com/DerBrasilianer/java-cp5-2025-parte1.git
cd java-cp5-2025-parte1
```

### 2. Executando o Servidor
```bash
mvn compile
mvn exec:java -Dexec.mainClass="br.com.fiap.server.ServerApp"
```

### 3. Executando o Cliente (em outro terminal)
```bash
mvn exec:java -Dexec.mainClass="br.com.fiap.client.ClientApp"
```

*Ou especificando host e porta:*
```bash
mvn exec:java -Dexec.mainClass="br.com.fiap.client.ClientApp" -Dexec.args="localhost 5000"
```

---

## 🔐 Configuração RSA

### Parâmetros Utilizados (Planilha Excel)
- **P (primo)**: 19
- **Q (primo)**: 31
- **N (modulus)**: P × Q = 589
- **φ(N) (totiente)**: (P-1) × (Q-1) = 540
- **E (expoente público)**: 7
- **D (expoente privado)**: 463

### Validação com Simulador
Os valores foram validados usando o **RSA Express Encryption-Decryption Calculator** da Drexel University, confirmando que a criptografia/descriptografia funciona corretamente.

---

## 🏗️ Estrutura do Projeto

```
java-cp5-2025-parte1/
├── src/
│   └── main/
│       └── java/
│           └── br/com/fiap/
│               ├── server/
│               │   └── ServerApp.java
│               ├── client/
│               │   └── ClientApp.java
│               ├── connection/
│               │   └── Connection.java
│               └── rsa/
│                   ├── RSAKey.java
│                   ├── RSAUtil.java
│                   └── KeyPairGeneratorRSA.java
├── pom.xml
├── Integrantes.txt
└── Dados RSA LTAKN.xlsx
```

---

## 📡 Protocolo de Comunicação

### Troca de Chaves
- `PUB:e:n` - Envia chave pública (expoente e modulus)

### Mensagens
- `MSG:ciphertext` - Mensagem criptografada (valores BigInteger separados por vírgula)

### Comandos
- `CMD:EXIT` - Solicita encerramento da conexão

---

## 🔧 Classes Principais

### ServerApp
- Inicia servidor na porta 5000
- Gera par de chaves RSA
- Gerencia conexões com clientes
- Processa mensagens criptografadas

### ClientApp  
- Conecta ao servidor
- Gera próprio par de chaves RSA
- Interface para envio/recebimento de mensagens

### RSAUtil
- `encryptString()` - Criptografa string usando chave pública
- `decryptString()` - Descriptografa usando chave privada
- Opera caractere por caractere

### KeyPairGeneratorRSA
- Gera par de chaves com valores fixos (P=19, Q=31, E=7)
- Calcula D usando modInverse()

### Connection
- Gerencia comunicação de rede via Sockets
- BufferedReader e PrintWriter para I/O

---

## 🧪 Exemplo de Execução

### Servidor:
```
Servidor RSA gerado:
p=19 q=31
n=589
phi=540
e=7
d=463
Aguardando conexão na porta 5000...
Cliente conectado: /127.0.0.1:54321
Chave pública do cliente recebida: e=7 n=589
[DEBUG] Recebido cifrado: 123,456,789
[DEBUG] Decifrado: Ola
Cliente: Ola
```

### Cliente:
```
Cliente RSA gerado:
p=19 q=31
n=589
phi=540
e=7
d=463
Conectado ao servidor localhost:5000
Chave pública do servidor recebida: e=7 n=589
[DEBUG] Texto original: Ola
[DEBUG] Cifrado (RSA): 123,456,789
Servidor: Oi
Cliente: Ola
```

---

## 🔒 Processo de Criptografia

1. **Geração de Chaves**: Cada lado gera seu próprio par de chaves RSA
2. **Troca Pública**: Cliente e servidor trocam chaves públicas (E, N)
3. **Criptografia**: Mensagens são criptografadas com a chave pública do destinatário
4. **Transmissão**: Dados trafegam criptografados pela rede
5. **Descriptografia**: Destinatário descriptografa com sua chave privada

---

## 📊 Planilha de Dados RSA

A planilha `Dados RSA LTAKN.xlsx` contém todos os cálculos matemáticos:
- Cálculo do modulus N = P × Q
- Função totiente φ(N) = (P-1) × (Q-1)
- Definição do expoente público E
- Cálculo do expoente privado D (inverso multiplicativo)

---

## 🎯 IDE Utilizado

**IntelliJ IDEA** - Ambiente de desenvolvimento principal

---

## 📞 Contato

- **Repositório GitHub**: https://github.com/DerBrasilianer/java-cp5-2025-parte1.git

---

## 👥 Desenvolvido por

**Grupo LTAKN** - Java Advanced · FIAP · 2025