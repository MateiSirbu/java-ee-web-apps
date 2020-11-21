package eu.msirbu.tw.tema1.server.services;

import eu.msirbu.tw.tema1.common.Response;
import eu.msirbu.tw.tema1.common.ServerState;

import java.security.GeneralSecurityException;

public class Protocol {

    private ServerState currentState = ServerState.WAITING;
    private Authenticator auth;

    public Protocol(Authenticator auth) {
        this.auth = auth;
    }

    public Response processInput(String input) {

        String status = "Undefined status.", payload = "";

        switch (currentState) {
            case WAITING: {
                status = "*******************\n" +
                        "Welcome to JForums!\n" +
                        "*******************\n\n" +
                        "Please LOGIN, REGISTER or EXIT.";
                currentState = ServerState.NOTLOGGEDIN;
                break;
            }
            case REGISTERING: {
                status = "Registered successfully.";
                if (input.equals(""))
                {
                    status = "Please enter your username.";
                    currentState = ServerState.REGISTERING;
                }
                else {
                    try {
                        payload = auth.generateUserToken(input);
                        currentState = ServerState.REGISTERED;
                    } catch (GeneralSecurityException e) {
                        status = "Cannot register, please try again. Enter your username.";
                        currentState = ServerState.REGISTERING;
                    }
                }
                break;
            }
            case LOGGING: {
                if (input.toUpperCase().equals("REGISTER")) {
                    currentState = ServerState.REGISTERING;
                    status = "Please register by entering your username.";
                } else {
                    try {
                        status = "Hello, " + auth.getUserNameFromToken(input);
                        currentState = ServerState.LOGGEDIN;
                    } catch (GeneralSecurityException | IllegalArgumentException e) {
                        status = "Cannot log in, invalid authentication token.\nPlease LOGIN, REGISTER or EXIT.";
                        currentState = ServerState.NOTLOGGEDIN;
                    }
                }
                break;
            }
            case LOGGEDIN: {
                switch (input.toUpperCase()) {

                }
            }
            case NOTLOGGEDIN: {
                switch (input.toUpperCase()) {
                    case "EXIT": {
                        status = "Exiting...";
                        payload = "EXIT";
                        break;
                    }
                    case "LOGIN": {
                        status = "Logging you in...";
                        currentState = ServerState.LOGGING;
                        break;
                    }
                    case "REGISTER": {
                        status = "You must be new around here. Please enter your username.";
                        currentState = ServerState.REGISTERING;
                        break;
                    }
                    default:
                        status = "Invalid command, please try again.";
                        break;
                }
            }
        }

        return new Response(status, payload, currentState);
    }
}
