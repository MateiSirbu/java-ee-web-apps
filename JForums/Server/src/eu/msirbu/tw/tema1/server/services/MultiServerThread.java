package eu.msirbu.tw.tema1.server.services;

import eu.msirbu.tw.tema1.server.data.Request;
import eu.msirbu.tw.tema1.server.data.Response;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MultiServerThread implements Runnable {

    private final Socket socket;

    public MultiServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            Request request;
            Protocol protocol = new Protocol();

            System.out.println("Connection established.");
            out.writeObject(protocol.processInput(new Request()));

            try {
                while ((request = (Request) (in.readObject())) != null) {
                    Response response = protocol.processInput(request);
                    out.writeObject(response);
                    if (response.getPayload().toUpperCase().equals("EXIT"))
                        break;
                }
                socket.close();
                System.out.println("Connection ended gracefully.");
            } catch (EOFException e) {
                System.out.println("Client terminated connection unexpectedly.");
            } catch (ClassNotFoundException e) {
                System.out.println("Malformed incoming packet. Disconnecting.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
