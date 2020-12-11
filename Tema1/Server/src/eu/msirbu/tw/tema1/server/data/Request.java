package eu.msirbu.tw.tema1.server.data;

import java.io.Serializable;
import java.util.Objects;

public class Request implements Serializable {

    private static final long serialVersionUID = 0L;

    private String message;
    private String payload;

    /**
     * The constructor.
     */
    public Request() {
        this.message = "";
        this.payload = "";
    }

    // Necessary getters and setters

    public String getPayload() {
        return payload;
    }

    public String getMessage() {
        return message;
    }

    @SuppressWarnings("unused") // false positive: in use inside the Client project
    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressWarnings("unused") // false positive: in use inside the Client project
    public void setPayload(String payload) {
        this.payload = payload;
    }

    // Overridden functions inherited from Object

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return message.equals(request.message) &&
                payload.equals(request.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, payload);
    }

    @Override
    public String toString() {
        return "Request{" +
                "text='" + message + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}