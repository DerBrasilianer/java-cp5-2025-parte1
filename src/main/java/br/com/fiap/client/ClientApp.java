package br.com.fiap.client;

import br.com.fiap.connection.Connection;
import br.com.fiap.rsa.KeyPairGeneratorRSA;
import br.com.fiap.rsa.RSAKey;
import br.com.fiap.rsa.RSAUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;

public class ClientApp {

    private static volatile BigInteger remoteE = null;
    private static volatile BigInteger remoteN = null;
    private static volatile boolean running = true;

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5000;
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }

        KeyPairGeneratorRSA keyGen = new KeyPairGeneratorRSA();
        RSAKey keyPair = keyGen.generateKeyPair();
        BigInteger eClient = keyPair.getE();
        BigInteger dClient = keyPair.getD();
        BigInteger nClient = keyPair.getN();

        System.out.println("Cliente RSA gerado:");
        System.out.println("p=" + keyGen.getP() + " q=" + keyGen.getQ());
        System.out.println("n=" + nClient);
        System.out.println("phi=" + keyGen.getPhi());
        System.out.println("e=" + eClient);
        System.out.println("d=" + dClient);

        Socket socket = new Socket(host, port);
        System.out.println("Conectado ao servidor " + host + ":" + port);
        Connection conn = new Connection(socket);

        String serverPubLine = conn.readLine();
        if (serverPubLine != null && serverPubLine.startsWith("PUB:")) {
            String[] parts = serverPubLine.split(":");
            if (parts.length >= 3) {
                remoteE = new BigInteger(parts[1]);
                remoteN = new BigInteger(parts[2]);
                System.out.println("Chave pública do servidor recebida: e=" + remoteE + " n=" + remoteN);
            }
        }

        String myPubLine = "PUB:" + eClient.toString() + ":" + nClient.toString();
        conn.sendLine(myPubLine);

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
                            System.out.println("Chave pública do servidor atualizada: e=" + remoteE + " n=" + remoteN);
                        }
                        continue;
                    }
                    if (line.startsWith("MSG:")) {
                        String payload = line.substring(4);
                        System.out.println("[DEBUG] Recebido cifrado: " + payload);
                        String plain = RSAUtil.decryptString(payload, dClient, nClient);
                        System.out.println("[DEBUG] Decifrado: " + plain);
                        System.out.println("Servidor: " + plain);
                        continue;
                    }
                    if (line.startsWith("CMD:")) {
                        String cmd = line.substring(4);
                        if ("EXIT".equalsIgnoreCase(cmd.trim())) {
                            System.out.println("Servidor solicitou encerramento.");
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
                        System.out.println("Aguardando chave pública do servidor...");
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
            socket.close();
        } catch (IOException ignored) {
        }
        System.out.println("Cliente finalizado.");
    }

}
