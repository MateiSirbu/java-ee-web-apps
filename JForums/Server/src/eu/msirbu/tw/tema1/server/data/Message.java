package eu.msirbu.tw.tema1.server.data;

import java.util.Date;
import java.util.Objects;

public class Message {

    private final String author;
    private final String text;
    private final Date timestamp;
    private final Message predecessor;

    /**
     * Constructor
     *
     * @param author The author of the message.
     * @param timestamp The date the message was posted.
     * @param text The text of the message.
     * **/
    public Message(String author, Date timestamp, String text) {
        this.author = author;
        this.timestamp = timestamp;
        this.text = text;
        this.predecessor = null;
    }

    /**
     * Constructor
     *
     * @param author The author of the message.
     * @param timestamp The date the message was posted.
     * @param text The text of the message.
     * @param predecessor The message this message is replying to.
     * **/
    public Message(String author, Date timestamp, String text, Message predecessor) {
        this.author = author;
        this.timestamp = timestamp;
        this.text = text;
        this.predecessor = predecessor;
    }

    // Overridden functions inherited from Object

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return author.equals(message.author) && text.equals(message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, text);
    }

    @Override
    public String toString() {
        String message = author + " @ " + timestamp + "\n[" + text + "]";
        if (predecessor != null) {
            message = message + "\n| Replying to " + predecessor.author + " @ " + predecessor.timestamp +
                    "\n| [" + predecessor.text + "]";
        }
        return message;
    }
}
