package eu.msirbu.tw.tema1.server;

import eu.msirbu.tw.tema1.server.services.Authenticator;
import eu.msirbu.tw.tema1.server.services.MultiServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args){
        boolean listening = true;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (listening) {
                executor.execute(new MultiServerThread(serverSocket.accept()));
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
            System.exit(-1);
        }
    }
}
