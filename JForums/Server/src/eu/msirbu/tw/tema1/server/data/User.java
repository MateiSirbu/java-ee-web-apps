package eu.msirbu.tw.tema1.server.data;

import java.util.ArrayList;
import java.util.Objects;

public class User {
    String username;
    ArrayList<String> subscriptions = new ArrayList<>();

    public User(String userName) {
        this.username = userName;
    }

    public ArrayList<String> getSubscriptions() {
        return subscriptions;
    }

    /**
     * Attempts adding a topic to this user's subscription list.
     * @param topicName The name of the topic the user is attempting to subscribe to.
     * @return Whether the user has successfully subscribed to the specified topic. Will fail if subscription is duplicate.
     */
    public boolean addSubscription(String topicName) {
        if (subscriptions.contains(topicName))
            return false;
        subscriptions.add(topicName);
        return true;
    }

    /**
     * Returns a String representation of an user.
     * @return The String representation.
     */
    @Override
    public String toString() {
        return username;
    }

    /**
     * Specifies the conditions under which two users are considered identical.
     * @param o The object the current user will compare with.
     * @return Whether the user and the object are identical.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    /**
     * Specifies the method by which an user will be given an associated hash code.
     * @return The hash code itself.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
