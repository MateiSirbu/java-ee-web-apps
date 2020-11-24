package eu.msirbu.tw.tema1.common;

/**
 * All the states the server can be in.
 * Warnings are suppressed as the "unused" states are all in use on the server's side, and cannot be deleted.
 */
@SuppressWarnings("unused")
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