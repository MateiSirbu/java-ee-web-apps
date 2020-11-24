package eu.msirbu.tw.tema1.server.services;

import com.sun.tools.javac.util.Pair;
import eu.msirbu.tw.tema1.server.constants.CRUDStatus;
import eu.msirbu.tw.tema1.server.data.Message;
import eu.msirbu.tw.tema1.server.data.Topic;
import eu.msirbu.tw.tema1.server.data.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DataProvider {
    private final ArrayList<Topic> topics = new ArrayList<>(Arrays.asList(
            new Topic("SPORTS"),
            new Topic("MOVIES"),
            new Topic("MUSIC"),
            new Topic("MATH"),
            new Topic("SCIENCE"),
            new Topic("FUN FACTS")));
    private final ArrayList<User> users = new ArrayList<>();

    /**
     * Attempts adding an user to the registered user list.
     *
     * @param userName The name of the user to be added.
     * @return Whether the user was added or not. It won't if the user name is duplicate.
     */
    public CRUDStatus addUser(String userName) {
        if (users.contains(new User(userName)))
            return CRUDStatus.FAIL_USER_IS_DUPLICATE;
        else if (userName.equals(""))
            return CRUDStatus.FAIL_EMPTY_ITEM;
        users.add(new User(userName));
        return CRUDStatus.SUCCESS;
    }

    /**
     * Attempts adding a topic to the topic list.
     *
     * @param topicName The name of the topic to be added.
     * @return Whether the topic was added or not. It won't if the topic name is duplicate.
     */
    public CRUDStatus addTopic(String topicName) {
        Topic topicToBeAdded = new Topic(topicName);
        final int existingTopicIndex = topics.indexOf(topicToBeAdded);
        if (existingTopicIndex == -1) {
            topics.add(topicToBeAdded);
            return CRUDStatus.SUCCESS;
        }
        return CRUDStatus.FAIL_DUPLICATE;
    }

    public Pair<CRUDStatus, ArrayList<Message>> getMessagesInTopic(String topicName) {
        final int targetTopic = topics.indexOf(new Topic(topicName));
        if (targetTopic != -1) {
            return new Pair<>(CRUDStatus.SUCCESS, topics.get(targetTopic).getMessages());
        }
        return new Pair<>(CRUDStatus.FAIL_ITEM_NOT_FOUND, new ArrayList<>());
    }

    /**
     * Attempts adding a message in a topic.
     *
     * @param message The Message object to be added.
     * @param topicName The name of the topic the message will be added to.
     * @return Whether the message was added or not. It won't if the topic does not exist.
     */
    public CRUDStatus addMessageInTopic(Message message, String topicName) {
        final int targetTopic = topics.indexOf(new Topic(topicName));
        if (targetTopic != -1) {
            topics.get(targetTopic).addMessage(message);
            return CRUDStatus.SUCCESS;
        }
        return CRUDStatus.FAIL_ITEM_NOT_FOUND;
    }

    public CRUDStatus addSubscriptionToUser(String userName, String topic) {
        final int userIndex = users.indexOf(new User(userName));
        final int topicIndex = topics.indexOf(new Topic(topic));
        if (topicIndex == -1)
            return CRUDStatus.FAIL_ITEM_NOT_FOUND;
        final int topicIndexInSubscription = users.get(userIndex).getSubscriptions().indexOf(topic);
        if (topicIndexInSubscription != -1)
            return CRUDStatus.FAIL_DUPLICATE;
        users.get(userIndex).addSubscription(topic);
        return CRUDStatus.SUCCESS;
    }

    public Pair<CRUDStatus, ArrayList<String>> getUserSubscriptions(String userName) {
        CRUDStatus status = CRUDStatus.SUCCESS;
        final int userIndex = users.indexOf(new User(userName));
        ArrayList<String> subscriptions = users.get(userIndex).getSubscriptions();
        return new Pair<>(status, subscriptions);
    }

    public Pair<CRUDStatus, ArrayList<String>> getAllTopicNames() {
        CRUDStatus status = CRUDStatus.SUCCESS;
        ArrayList<String> topicNames = new ArrayList<>();
        for (Topic topic : topics) {
            topicNames.add(topic.getName());
        }
        return new Pair<>(status, topicNames);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataProvider dataset = (DataProvider) o;
        return topics.equals(dataset.topics) && users.equals(dataset.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topics, users);
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "topics=" + topics +
                ", registeredUsers=" + users +
                '}';
    }
}
