/*
  (C) 2020 Matei Sîrbu.
 */
package eu.msirbu.tw.tema1.client;

import eu.msirbu.tw.tema1.common.Request;
import eu.msirbu.tw.tema1.common.Response;
import eu.msirbu.tw.tema1.common.ServerState;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

    /**
     * The port the client will connect to.
     **/
    private static final int PORT = 8080;
    /**
     * The hostname of the computer the client will connect to.
     **/
    private static final String HOSTNAME = "localhost";
    /**
     * The location the client will store the token at.
     * Note that the path should be modified for each client when testing on the same machine.
     * In a production environment, each machine should store a single token, at ~/.jforums/token.dat.
     */
    private static final String TOKEN_PATH = System.getProperty("user.home") + "/.jforums/token.dat";

    /**
     * The main thread. All the juicy action happens here.
     *
     * @param args The command line arguments; none required.
     */
    public static void main(String[] args) {
        try (
                Socket socket = new Socket(HOSTNAME, PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            Response response;

            while ((response = (Response) (in.readObject())) != null) {
                Request request = new Request();
                String payload = response.getPayload();

                System.out.println(response.getMessage());

                if (response.getPayload().equals("EXIT"))
                    break;

                System.out.print("> ");
                if (response.getCurrentState() == ServerState.REGISTERED) {
                    File tokenFile = new File(TOKEN_PATH);

                    if (!tokenFile.getParentFile().exists()) {
                        if (!tokenFile.getParentFile().mkdirs()) {
                            System.err.println("FATAL: Cannot create directory. Make sure you have " +
                                    "adequate R/W permissions, then try again. Exiting.");
                            System.exit(1);
                        }
                    }
                    if (!tokenFile.exists()) {
                        if (!tokenFile.createNewFile()) {
                            System.err.println("FATAL: Cannot create token file. Make sure you have " +
                                    "adequate R/W permissions, then try again. Exiting.");
                            System.exit(1);
                        }
                    }

                    FileWriter writer = new FileWriter(TOKEN_PATH);
                    writer.write(payload);
                    writer.close();
                    request.setMessage(stdIn.readLine());
                    request.setPayload(payload);
                } else {
                    try {
                        Scanner tokenReader = new Scanner(new File(TOKEN_PATH));
                        StringBuilder token = new StringBuilder();
                        while (tokenReader.hasNextLine())
                            token.append(tokenReader.nextLine());
                        request.setPayload(token.toString());
                    } catch (IOException e) {
                        request.setPayload("");
                    } finally {
                        request.setMessage(stdIn.readLine());
                    }
                }
                out.writeObject(request);
            }
        } catch (UnknownHostException e) {
            System.err.println("FATAL: Cannot find " + HOSTNAME + " on the network.");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("FATAL: Malformed incoming response. Disconnecting.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("FATAL: " + e.getMessage());
            System.exit(1);
        }
    }
}
