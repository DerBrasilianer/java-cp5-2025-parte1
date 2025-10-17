package br.com.fiap.server;

import br.com.fiap.connection.Connection;
import br.com.fiap.rsa.KeyPairGeneratorRSA;
import br.com.fiap.rsa.RSAKey;
import br.com.fiap.rsa.RSAUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {

    private static volatile BigInteger remoteE = null;
    private static volatile BigInteger remoteN = null;
    private static volatile boolean running = true;

    public static void main(String[] args) throws Exception {

        KeyPairGeneratorRSA keyGen = new KeyPairGeneratorRSA();
        RSAKey keyPair = keyGen.generateKeyPair();
        BigInteger eServer = keyPair.getE();
        BigInteger dServer = keyPair.getD();
        BigInteger nServer = keyPair.getN();

        System.out.println("Servidor RSA gerado:");
        System.out.println("p=" + keyGen.getP() + " q=" + keyGen.getQ());
        System.out.println("n=" + nServer);
        System.out.println("phi=" + keyGen.getPhi());
        System.out.println("e=" + eServer);
        System.out.println("d=" + dServer);

        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Aguardando conexão na porta 5000...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Cliente conectado: " + clientSocket.getRemoteSocketAddress());
        Connection conn = new Connection(clientSocket);

        String pubLine = "PUB:" + eServer.toString() + ":" + nServer.toString();
        conn.sendLine(pubLine);

        Thread reader = new Thread(() -> {
            try {
                while (running) {
                    String line = conn.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.startsWith("PUB:")) {
                        String[] parts = line.split(":");
                        if (parts.length >= 3) {
                            remoteE = new BigInteger(parts[1]);
                            remoteN = new BigInteger(parts[2]);
                            System.out.println("Chave pública do cliente recebida: e=" + remoteE + " n=" + remoteN);
                        }
                        continue;
                    }
                    if (line.startsWith("MSG:")) {
                        String payload = line.substring(4);
                        System.out.println("[DEBUG] Recebido cifrado: " + payload);
                        String plain = RSAUtil.decryptString(payload, dServer, nServer);
                        System.out.println("[DEBUG] Decifrado: " + plain);
                        System.out.println("Cliente: " + plain);
                        continue;
                    }
                    if (line.startsWith("CMD:")) {
                        String cmd = line.substring(4);
                        if ("EXIT".equalsIgnoreCase(cmd.trim())) {
                            System.out.println("Cliente solicitou encerramento.");
                            running = false;
                            break;
                        }
                    }
                }
            } catch (IOException e) {
            } finally {
                running = false;
            }
        });
        reader.start();

        Thread writer = new Thread(() -> {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            try {
                while (running) {
                    String text = console.readLine();
                    if (text == null) {
                        break;
                    }
                    if ("exit".equalsIgnoreCase(text.trim())) {
                        conn.sendLine("CMD:EXIT");
                        running = false;
                        break;
                    }
                    if (remoteE == null || remoteN == null) {
                        System.out.println("Aguardando chave pública do cliente...");
                        continue;
                    }
                    String cipher = RSAUtil.encryptString(text, remoteE, remoteN);
                    System.out.println("[DEBUG] Texto original: " + text);
                    System.out.println("[DEBUG] Cifrado (RSA): " + cipher);
                    conn.sendLine("MSG:" + cipher);
                }
            } catch (IOException e) {
            } finally {
                running = false;
            }
        });
        writer.start();

        try {
            reader.join();
        } catch (InterruptedException ignored) {
        }
        try {
            writer.join();
        } catch (InterruptedException ignored) {
        }
        conn.close();
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
        System.out.println("Servidor finalizado.");
    }

}
