package eu.msirbu.tw.tema1.server.services;

import com.sun.tools.javac.util.Pair;
import eu.msirbu.tw.tema1.server.constants.CRUDStatus;
import eu.msirbu.tw.tema1.server.data.Message;
import eu.msirbu.tw.tema1.server.data.Request;
import eu.msirbu.tw.tema1.server.data.Response;
import eu.msirbu.tw.tema1.server.data.ServerState;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;

public class Protocol {

    private ServerState currentState = ServerState.WAITING;
    private static final Authenticator auth = new Authenticator();
    private static final DataProvider db = new DataProvider();
    private String currentTopic = null;
    private int currentMessageID = 0;

    public Protocol() {
    }

    /**
     * Specifies the message to be displayed when accessing the first menu.
     *
     * @param firstTime Whether the first menu is first accessed or not.
     * @return The string to be displayed on the client command line.
     */
    public String mainMenu(boolean firstTime) {
        return (firstTime) ?
                "Please type:\n" +
                        "• ls          | to see your subscription list\n" +
                        "• ls --all    | for all available topics\n" +
                        "• sub         | to subscribe to a topic\n" +
                        "• fetch       | to view discussions in the topics you follow\n" +
                        "• new --topic | to create a new topic\n" +
                        "• new --convo | to create a new conversation inside topic\n" +
                        "• reply       | to reply to a message inside topic"
                : "Back to the main menu (ls [--all]/sub/fetch/reply/new --topic or --convo)";
    }

    /**
     * The function that processed the input sent by the client.
     *
     * @param request The request to be processed, that contains the
     *                command line input and the auth token.
     * @return The adequate response to the request.
     */
    public Response processInput(Request request) {

        String input = request.getMessage().trim();
        String token = request.getPayload();

        StringBuilder output = new StringBuilder("Invalid command, please try again.");
        String payload = "";

        if (currentState == ServerState.REGISTERED)
            currentState = ServerState.LOGGED_IN;

        if (input.toUpperCase().equals("EXIT")) {
            output = new StringBuilder("Exiting...");
            payload = "EXIT";
        }

        switch (currentState) {
            case WAITING: {
                output = new StringBuilder(
                        "*********************************************\n\n" +
                                "Welcome to the                                  \n" +
                                "      _ ______                                  \n" +
                                "     | |  ____|                                 \n" +
                                "     | | |__ ___  _ __ _   _ _ __ ___  ___      \n" +
                                " _   | |  __/ _ \\| '__| | | | '_ ` _ \\/ __|   \n" +
                                "| |__| | | | (_) | |  | |_| | | | | | \\__ \\   \n" +
                                " \\____/|_|  \\___/|_|   \\__,_|_| |_| |_|___/\n\n" +
                                "*********************************************\n\n" +
                                "Please LOGIN, REGISTER or EXIT.");
                currentState = ServerState.NOT_LOGGED_IN;
                break;
            }
            case REGISTERING: {
                output = new StringBuilder("Registered successfully. Welcome aboard! " + mainMenu(true));
                if (input.equals("")) {
                    output = new StringBuilder("Please enter your username.");
                    currentState = ServerState.REGISTERING;
                } else {
                    try {
                        CRUDStatus addUserStatus = db.addUser(input.toUpperCase());
                        if (addUserStatus == CRUDStatus.SUCCESS) {
                            payload = auth.generateUserToken(input);
                            currentState = ServerState.REGISTERED;
                        } else if (addUserStatus == CRUDStatus.FAIL_USER_IS_DUPLICATE) {
                            output = new StringBuilder("This user has already been registered. Try another username below.");
                            currentState = ServerState.REGISTERING;
                        }
                    } catch (GeneralSecurityException | IllegalArgumentException e) {
                        output = new StringBuilder("Cannot register, please try again. Enter your username.");
                        currentState = ServerState.REGISTERING;
                    }
                }
                break;
            }
            case SUBSCRIBING: {
                try {
                    String userName = auth.getUserNameFromToken(token);
                    if (input.equals("..")) {
                        output = new StringBuilder(mainMenu(false));
                        currentState = ServerState.LOGGED_IN;
                    } else {
                        CRUDStatus addStatus = db.addSubscriptionToUser(userName.toUpperCase(), input.toUpperCase());
                        if (addStatus == CRUDStatus.SUCCESS) {
                            output = new StringBuilder("You subscribed to " + input.toUpperCase() +
                                    ".\n" + mainMenu(false));
                            currentState = ServerState.LOGGED_IN;
                        } else if (addStatus == CRUDStatus.FAIL_ITEM_NOT_FOUND) {
                            output = new StringBuilder("The topic " + input.toUpperCase() + " does not exist.\n" +
                                    "Please enter the name of an existing topic, or type .. to cancel.");
                        } else if (addStatus == CRUDStatus.FAIL_DUPLICATE) {
                            output = new StringBuilder("You are already subscribed to " + input.toUpperCase() +
                                    ".\nPlease enter the name of a topic you don't follow, " +
                                    "or type .. to cancel.");
                        }
                    }
                } catch (GeneralSecurityException | IllegalArgumentException e) {
                    output = new StringBuilder("Cannot subscribe, invalid authentication token."
                            + "\nPlease LOGIN, REGISTER or EXIT.");
                    currentState = ServerState.NOT_LOGGED_IN;
                }
                break;
            }
            case ADDING_TOPIC: {
                try {
                    auth.getUserNameFromToken(token);
                    if (input.equals("..")) {
                        output = new StringBuilder(mainMenu(false));
                        currentState = ServerState.LOGGED_IN;
                    } else {
                        CRUDStatus addStatus = db.addTopic(input.toUpperCase());
                        if (addStatus == CRUDStatus.FAIL_DUPLICATE) {
                            output = new StringBuilder("The topic " + input.toUpperCase() + " already exists." +
                                    "\nPlease enter a new topic, or type .. to cancel.");
                        } else if (addStatus == CRUDStatus.SUCCESS) {
                            output = new StringBuilder("Topic added successfully." +
                                    "\n" + mainMenu(false));
                            currentState = ServerState.LOGGED_IN;
                        }
                    }
                } catch (GeneralSecurityException | IllegalArgumentException e) {
                    output = new StringBuilder("Cannot create topic, invalid authentication token."
                            + "\nPlease LOGIN, REGISTER or EXIT.");
                    currentState = ServerState.NOT_LOGGED_IN;
                }
                break;
            }
            case CONVO_SELECTING_TOPIC: {
                try {
                    String userName = auth.getUserNameFromToken(token);
                    if (input.equals("..")) {
                        output = new StringBuilder(mainMenu(false));
                        currentState = ServerState.LOGGED_IN;
                    } else {
                        if (db.getAllTopicNames().snd.contains(input.toUpperCase())) {
                            if (!db.getUserSubscriptions(userName.toUpperCase()).snd.contains(input.toUpperCase())) {
                                output = new StringBuilder("You are not subscribed to this topic." +
                                        "\nPlease type the name of a topic you follow, or .. to cancel.");
                            } else {

                                currentTopic = input.toUpperCase();
                                ArrayList<Message> messages = db.getMessagesInTopic(currentTopic).snd;
                                output = new StringBuilder("Previewing conversations in " + currentTopic + ":\n\n");
                                if (messages.size() == 0) {
                                    output.append("The thread is empty. Be the first one to post!\n\n");
                                } else {
                                    for (Message message : messages) {
                                        output.append(message.toString()).append("\n\n");
                                    }
                                }
                                output.append("Please type your message, or .. to cancel.");
                                currentState = ServerState.CONVO_COMPOSING;
                            }
                        } else {
                            output = new StringBuilder("The topic you specified does not exist." +
                                    "\nPlease type the name of an existing topic or .. to cancel.");
                        }
                    }
                } catch (GeneralSecurityException | IllegalArgumentException e) {
                    output = new StringBuilder("Cannot create topic, invalid authentication token."
                            + "\nPlease LOGIN, REGISTER or EXIT.");
                    currentState = ServerState.NOT_LOGGED_IN;
                }
                break;
            }
            case CONVO_COMPOSING: {
                try {
                    String userName = auth.getUserNameFromToken(token);
                    if (input.equals("..")) {
                        output = new StringBuilder(mainMenu(false));
                        currentTopic = "";
                        currentState = ServerState.LOGGED_IN;
                    } else if (input.equals("")) {
                        output = new StringBuilder("This message is empty. Please try again:");
                    } else {
                        db.addMessageInTopic(new Message(userName, new Date(), input), currentTopic);
                        output = new StringBuilder("Message sent.\n" + mainMenu(false));
                        currentTopic = "";
                        currentState = ServerState.LOGGED_IN;
                    }
                } catch (GeneralSecurityException | IllegalArgumentException e) {
                    output = new StringBuilder("Cannot send message, invalid authentication token."
                            + "\nPlease LOGIN, REGISTER or EXIT.");
                    currentState = ServerState.NOT_LOGGED_IN;
                }
                break;
            }
            case REPLY_SELECTING_TOPIC: {
                try {
                    String userName = auth.getUserNameFromToken(token);
                    if (input.equals("..")) {
                        output = new StringBuilder(mainMenu(false));
                        currentState = ServerState.LOGGED_IN;
                    } else {
                        if (db.getAllTopicNames().snd.contains(input.toUpperCase())) {
                            if (!db.getUserSubscriptions(userName.toUpperCase()).snd.contains(input.toUpperCase())) {
                                output = new StringBuilder("You are not subscribed to this topic." +
                                        "\nPlease type the name of a topic you follow, or .. to cancel.");
                            } else {
                                currentTopic = input.toUpperCase();
                                ArrayList<Message> messages = db.getMessagesInTopic(currentTopic).snd;
                                if (messages.size() == 0) {
                                    output = new StringBuilder("The thread is empty. There's no message to reply to!" +
                                            "\n" + mainMenu(false));
                                    currentState = ServerState.LOGGED_IN;
                                } else {
                                    output = new StringBuilder("Previewing conversations in " + currentTopic + ":\n\n");
                                    int messageID = 1;
                                    for (Message message : messages) {
                                        output.append("ID ").append(messageID++).append(" -> ")
                                                .append(message.toString()).append("\n\n");
                                    }
                                    output.append("Type the ID of the message you'd like to reply to, or .. to cancel.");
                                    currentState = ServerState.REPLY_SELECTING_CONVO;
                                }
                            }
                        } else {
                            output = new StringBuilder("The topic you specified does not exist." +
                                    "\nPlease type the name of an existing topic or .. to cancel.");
                        }
                    }
                } catch (GeneralSecurityException | IllegalArgumentException e) {
                    output = new StringBuilder("Cannot select topic, invalid authentication token."
                            + "\nPlease LOGIN, REGISTER or EXIT.");
                    currentState = ServerState.NOT_LOGGED_IN;
                }
                break;
            }
            case REPLY_SELECTING_CONVO: {
                try {
                    auth.getUserNameFromToken(token);
                    if (input.equals("..")) {
                        output = new StringBuilder(mainMenu(false));
                        currentTopic = "";
                        currentState = ServerState.LOGGED_IN;
                    } else {
                        int messageID = Integer.parseInt(input);
                        ArrayList<Message> messages = db.getMessagesInTopic(currentTopic).snd;
                        if (messageID > messages.size()) {
                            output = new StringBuilder("A message with ID " + messageID + " does not exist."
                                    + "Please provide a valid message ID.");
                        } else {
                            currentMessageID = messageID;
                            output = new StringBuilder("You are replying to " +
                                    messages.get(currentMessageID - 1).toString() +
                                    "\n\nType your message, or .. to cancel.");
                            currentState = ServerState.REPLY_COMPOSING;
                        }
                    }
                } catch (NumberFormatException e) {
                    output = new StringBuilder("Your input is not a number. Please provide a valid message ID.");
                } catch (GeneralSecurityException | IllegalArgumentException e) {
                    output = new StringBuilder("Cannot select conversation, invalid authentication token."
                            + "\nPlease LOGIN, REGISTER or EXIT.");
                    currentState = ServerState.NOT_LOGGED_IN;
                }
                break;
            }
            case REPLY_COMPOSING: {
                try {
                    String userName = auth.getUserNameFromToken(token);
                    if (input.equals("..")) {
                        output = new StringBuilder(mainMenu(false));
                        currentTopic = "";
                        currentState = ServerState.LOGGED_IN;
                        currentMessageID = 0;
                    } else if (input.equals("")) {
                        output = new StringBuilder("This message is empty. Please try again:");
                    } else {
                        Message predecessor = db.getMessagesInTopic(currentTopic).snd.get(currentMessageID - 1);
                        CRUDStatus addStatus = db.addMessageInTopic(new Message(userName, new Date(), input, predecessor), currentTopic);
                        if (addStatus == CRUDStatus.SUCCESS) {
                            output = new StringBuilder("Reply sent.\n" + mainMenu(false));
                            currentTopic = "";
                            currentState = ServerState.LOGGED_IN;
                        } else if (addStatus == CRUDStatus.FAIL_ITEM_NOT_FOUND) {
                            output = new StringBuilder("Cannot send reply, cannot find topic." +
                                    "\n" + mainMenu(false));
                            currentState = ServerState.LOGGED_IN;
                        }
                    }
                } catch (GeneralSecurityException | IllegalArgumentException e) {
                    output = new StringBuilder("Cannot send reply, invalid authentication token."
                            + "\nPlease LOGIN, REGISTER or EXIT.");
                    currentState = ServerState.NOT_LOGGED_IN;
                }
                break;
            }
            case LOGGED_IN: {
                try {
                    String userName = auth.getUserNameFromToken(token);
                    switch (input.toUpperCase()) {
                        case "LS": {
                            Pair<CRUDStatus, ArrayList<String>> userSubscriptionsRequest
                                    = db.getUserSubscriptions(userName.toUpperCase());
                            if (userSubscriptionsRequest.fst == CRUDStatus.SUCCESS) {
                                if (userSubscriptionsRequest.snd.size() == 0)
                                    output = new StringBuilder("You are not subscribed to any topics.");
                                else {
                                    output = new StringBuilder("Topics you've subscribed to:");
                                    for (String topicName : userSubscriptionsRequest.snd) {
                                        output.append("\n").append("• ").append(topicName);
                                    }
                                }
                            }
                            break;
                        }
                        case "LS --ALL": {
                            Pair<CRUDStatus, ArrayList<String>> userSubscriptions
                                    = db.getUserSubscriptions(userName.toUpperCase());
                            Pair<CRUDStatus, ArrayList<String>> allTopics = db.getAllTopicNames();
                            if (allTopics.fst == CRUDStatus.SUCCESS) {
                                output = new StringBuilder("All available topics:");
                                for (String topicName : allTopics.snd) {
                                    output.append("\n").append("• ").append(topicName);
                                    if (userSubscriptions.snd.contains(topicName)) {
                                        output.append(" [subscribed]");
                                    }
                                }
                            }
                            break;
                        }
                        case "SUB": {
                            output = new StringBuilder("Which topic would you like to subscribe to?\nType .. to cancel.");
                            currentState = ServerState.SUBSCRIBING;
                            break;
                        }
                        case "NEW --TOPIC": {
                            output = new StringBuilder("Which topic would you like to create?\nType .. to cancel.");
                            currentState = ServerState.ADDING_TOPIC;
                            break;
                        }
                        case "NEW --CONVO": {
                            output = new StringBuilder("Which topic would you like to start a new conversation in?" +
                                    "\nType .. to cancel");
                            currentState = ServerState.CONVO_SELECTING_TOPIC;
                            break;
                        }
                        case "REPLY": {
                            output = new StringBuilder("In which topic is the message you'd like to reply to?" +
                                    "\nType .. to cancel");
                            currentState = ServerState.REPLY_SELECTING_TOPIC;
                            break;
                        }
                        case "FETCH": {
                            Pair<CRUDStatus, ArrayList<String>> userSubscriptions
                                    = db.getUserSubscriptions(userName.toUpperCase());
                            if (userSubscriptions.snd.size() == 0) {
                                output = new StringBuilder("To fetch messages, you must subscribe to a topic first.");
                            } else {
                                output = new StringBuilder("Fetching messages from all subscribed topics:\n\n");
                                for (String topic : userSubscriptions.snd) {
                                    output.append("======== ").append(topic).append(" ========\n\n");
                                    ArrayList<Message> messages = db.getMessagesInTopic(topic).snd;
                                    if (messages.size() == 0) {
                                        output.append("This topic has no messages.\n\n");
                                    } else {
                                        for (Message message : messages) {
                                            output.append(message.toString()).append("\n\n");
                                        }
                                    }
                                }
                                output.append("Done.");
                            }
                            break;
                        }
                    }
                } catch (GeneralSecurityException | IllegalArgumentException e) {
                    output = new StringBuilder("Cannot fetch subscription list, invalid authentication token."
                            + "\nPlease LOGIN, REGISTER or EXIT.");
                    currentState = ServerState.NOT_LOGGED_IN;
                }
                break;
            }
            case NOT_LOGGED_IN: {
                switch (input.toUpperCase()) {
                    case "EXIT": {
                        output = new StringBuilder("Exiting...");
                        payload = "EXIT";
                        break;
                    }
                    case "LOGIN": {
                        if (token.equals("")) {
                            output = new StringBuilder("Authentication token not found. Please register by entering an username below.");
                            currentState = ServerState.REGISTERING;
                        } else {
                            try {
                                output = new StringBuilder("Hello, " + auth.getUserNameFromToken(token) + "! " + mainMenu(false));
                                currentState = ServerState.LOGGED_IN;
                            } catch (GeneralSecurityException | IllegalArgumentException e) {
                                output = new StringBuilder("Cannot log in, invalid authentication token.\nPlease LOGIN, REGISTER or EXIT.");
                                currentState = ServerState.NOT_LOGGED_IN;
                            }
                        }
                        break;
                    }
                    case "REGISTER": {
                        output = new StringBuilder("You must be new around here. Please enter your username.");
                        currentState = ServerState.REGISTERING;
                        break;
                    }
                }
            }
        }

        return new Response("\n" + output + "\n", payload, currentState);
    }
}
