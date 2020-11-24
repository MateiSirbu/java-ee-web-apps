package eu.msirbu.tw.tema1.common;

import java.io.Serializable;
import java.util.Objects;

public class Request implements Serializable {

    private static final long serialVersionUID = 0L;

    private final String message;
    private final String payload;

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
