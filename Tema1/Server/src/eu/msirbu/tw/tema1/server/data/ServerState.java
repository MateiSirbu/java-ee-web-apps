package eu.msirbu.tw.tema1.server.data;

/**
 * All the states the server can be in.
 */
public enum ServerState {
    WAITING,
    NOT_LOGGED_IN,
    REGISTERING,
    REGISTERED,
    LOGGED_IN,
    ADDING_TOPIC,
    SUBSCRIBING,
    CONVO_SELECTING_TOPIC,
    CONVO_COMPOSING,
    REPLY_SELECTING_TOPIC,
    REPLY_SELECTING_CONVO,
    REPLY_COMPOSING
}
