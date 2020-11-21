package eu.msirbu.tw.tema1.server.data;

import java.util.ArrayList;
import java.util.Objects;

public class Topic {

    private String name;
    private ArrayList<Message> messages = new ArrayList<>();

    public Topic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Adds a message to the discussion thread.
     * @param message The message to be added.
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Returns a String representation of a topic.
     * @return The String representation.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Specifies the conditions under which two topics are considered identical.
     * @param o The object the current topic will compare with.
     * @return Whether the topic and the object are identical.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return name.equals(topic.name);
    }

    /**
     * Specifies the method by which a topic will be given an associated hash code.
     * @return The hash code itself.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
