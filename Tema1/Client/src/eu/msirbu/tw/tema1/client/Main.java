package eu.msirbu.tw.tema1.client;

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
    private static final String TOKEN_PATH = System.getProperty("user.home") + "/.jforums/token.dat";

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(HOSTNAME, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String request;
            Response response;

            while ((response = (Response) (in.readObject())) != null) {
                String statusMessage = response.getStatusMessage();
                ServerState currentState = response.getCurrentState();
                String payload = response.getPayload();

                System.out.println(statusMessage);

                if (response.getPayload().equals("EXIT"))
                    break;

                switch (currentState) {
                    case REGISTERING:

                    case REGISTERED:
                        File tokenFile = new File(TOKEN_PATH);
                        tokenFile.getParentFile().mkdirs();
                        tokenFile.createNewFile();
                        FileWriter writer = new FileWriter(TOKEN_PATH);
                        writer.write(payload);
                        writer.close();
                        System.out.print("> ");
                        request = stdIn.readLine();
                        break;
                    case LOGGING:
                        try {
                            Scanner tokenReader = new Scanner(new File(TOKEN_PATH));
                            StringBuilder token = new StringBuilder();
                            while (tokenReader.hasNextLine())
                                token.append(tokenReader.nextLine());
                            request = token.toString();
                        } catch (IOException e) {
                            System.out.println("Authentication token not found.");
                            request = "register";
                        }
                        break;
                    default:
                        System.out.print("> ");
                        request = stdIn.readLine();
                }

                if (request != null) {
                    out.println(request);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Cannot find " + HOSTNAME + " on the network.");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("Response not formatted properly.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
