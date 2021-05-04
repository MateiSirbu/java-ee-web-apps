/*
  (C) 2020 Matei Sîrbu.
 */
package eu.msirbu.tw.tema1.server;

import eu.msirbu.tw.tema1.server.services.MultiServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    /**
     * The port the server is listening to.
     */
    private static final int PORT = 8080;

    /**
     * The main thread. All the juicy action happens here.
     * The infinite loop warning is suppressed (false positive:
     * that's PRECISELY what I want to do, IntelliJ!)
     * @param args The command line arguments; none required.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args){
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("JForums server is up and running.\nListening for incoming connections.");

            while (true) {
                executor.execute(new MultiServerThread(serverSocket.accept()));
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
            System.exit(-1);
        }
    }
}
