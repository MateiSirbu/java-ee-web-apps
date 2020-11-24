package eu.msirbu.tw.tema1.common;

import java.io.Serializable;
import java.util.Objects;

public class Response implements Serializable {

    private static final long serialVersionUID = 0L;

    private final String message;
    private final String payload;
    private final ServerState currentState;

    /**
     * The constructor.
     * @param message The message.
     * @param payload The payload, i.e. attached additional info.
     * @param currentState The state the server is in, mainly used for proper token management.
     */
    public Response(String message, String payload, ServerState currentState) {
        this.message = message;
        this.payload = payload;
        this.currentState = currentState;
    }

    // Necessary getters and setters

    public String getPayload() {
        return payload;
    }

    // Overridden functions inherited from Object

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return message.equals(response.message) &&
                payload.equals(response.payload) &&
                currentState == response.currentState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, payload, currentState);
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusMessage='" + message + '\'' +
                ", payload='" + payload + '\'' +
                ", currentState=" + currentState +
                '}';
    }
}
