package br.com.fiap.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendLine(String s) {
        out.println(s);
        out.flush();
    }

    public String readLine() throws IOException {
        return in.readLine();
    }

    public void close() {
        try {
            in.close();
        } catch (Exception ignored) {
        }
        try {
            out.close();
        } catch (Exception ignored) {
        }
        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }

}
