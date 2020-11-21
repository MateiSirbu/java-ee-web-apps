package eu.msirbu.tw.tema1.common;

import java.io.Serializable;
import java.util.Objects;

public class Response implements Serializable {

    private static final long serialVersionUID = 0L;

    private String statusMessage;
    private String payload;
    private ServerState currentState;

    public Response(String message, String payload, ServerState currentState) {
        this.statusMessage = message;
        this.payload = payload;
        this.currentState = currentState;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public ServerState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ServerState currentState) {
        this.currentState = currentState;
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusMessage='" + statusMessage + '\'' +
                ", payload='" + payload + '\'' +
                ", currentState=" + currentState +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return statusMessage.equals(response.statusMessage) &&
                payload.equals(response.payload) &&
                currentState == response.currentState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusMessage, payload, currentState);
    }
}
