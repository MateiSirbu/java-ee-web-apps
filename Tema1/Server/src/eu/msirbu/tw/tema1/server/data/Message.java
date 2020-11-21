package eu.msirbu.tw.tema1.server.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Message {

    private String author, text;
    private Date timestamp;
    private ArrayList<Message> replies = new ArrayList<>();

    /**
     * Constructor
     *
     * @param author The author of the message.
     * @param text The text of the message.
     *
     * **/
    public Message(String author, Date timestamp, String text) {
        this.author = author;
        this.timestamp = timestamp;
        this.text = text;
    }

    /** Adds a reply to the message. **/
    public void addReply(Message message) {
        replies.add(message);
    }

    // Getters and setters

    public ArrayList<Message> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<Message> replies) {
        this.replies = replies;
    }

    public String getAuthor() {
        return author;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        return author + "@ " + timestamp + "\n " + text;
    }
}
