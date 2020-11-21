package eu.msirbu.tw.tema1.server.services;

import eu.msirbu.tw.tema1.common.Response;
import eu.msirbu.tw.tema1.server.data.Dataset;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class MultiServerThread implements Runnable {

    private final Socket socket;
    private static final Authenticator auth = new Authenticator();
    private static Dataset dataset;

    public MultiServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String inputLine;
            Response outputLine;
            Protocol protocol = new Protocol(auth);

            System.out.println("Connection established.");
            outputLine = protocol.processInput(null);
            out.writeObject(outputLine);

            while((inputLine = in.readLine()) != null) {
                outputLine = protocol.processInput(inputLine);
                out.writeObject(outputLine);
                if (outputLine.getPayload().toUpperCase().equals("EXIT"))
                    break;
            }
            socket.close();
            System.out.println("Connection ended gracefully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
