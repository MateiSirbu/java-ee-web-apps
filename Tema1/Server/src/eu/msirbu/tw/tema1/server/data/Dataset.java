package eu.msirbu.tw.tema1.server.data;

import com.sun.tools.javac.util.Pair;
import eu.msirbu.tw.tema1.server.constants.CRUDStatus;

import java.util.ArrayList;
import java.util.Objects;

public class Dataset {
    ArrayList<Topic> topics = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();

    /**
     * Attempts adding an user to the registered user list.
     * @param userName The name of the user to be added.
     * @return Whether the user was added or not. It won't if the user name is duplicate.
     */
    public CRUDStatus addUser(String userName) {
        try {
            if (users.contains(userName))
                return CRUDStatus.FAIL_USER_IS_DUPLICATE;
            else if (userName.equals(""))
                return CRUDStatus.FAIL_EMPTY_ITEM;
            users.add(new User(userName));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return CRUDStatus.FAIL_MISCELLANEOUS_ERROR;
        }
        return CRUDStatus.SUCCESS;
    }

    /**
     * Attempts adding a topic to the topic list.
     * @param topicName The name of the topic to be added.
     * @return Whether the topic was added or not. It won't if the topic name is duplicate.
     */
    public CRUDStatus addTopic(String topicName) {
        try {
            Topic topicToBeAdded = new Topic(topicName);
            final int existingTopicIndex = topics.indexOf(topicToBeAdded);
            if (existingTopicIndex == -1) {
                topics.add(topicToBeAdded);
                return CRUDStatus.SUCCESS;
            }
            return CRUDStatus.FAIL_DUPLICATE;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return CRUDStatus.FAIL_MISCELLANEOUS_ERROR;
        }
    }

    public CRUDStatus addSubscriptionToUser(String userName, String topic)
    {
        final int userIndex = users.indexOf(new User(userName));
        if (userIndex == -1)
            return CRUDStatus.FAIL_USER_NOT_FOUND;
        final int topicIndex = topics.indexOf(new Topic(topic));
        if (topicIndex == -1)
            return CRUDStatus.FAIL_ITEM_NOT_FOUND;
        final int topicIndexInSubscription = users.get(userIndex).getSubscriptions().indexOf(topic);
        if (topicIndexInSubscription != -1)
            return CRUDStatus.FAIL_DUPLICATE;
        try {
            users.get(userIndex).addSubscription(topic);
            return CRUDStatus.SUCCESS;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return CRUDStatus.FAIL_MISCELLANEOUS_ERROR;
        }
    }

    public Pair<CRUDStatus, ArrayList<String>> listUserSubscriptions(String userName) {
        CRUDStatus status = CRUDStatus.SUCCESS;
        ArrayList<String> subscriptions = new ArrayList<String>();
        final int userIndex = users.indexOf(new User(userName));
        if (userIndex == -1)
            status = CRUDStatus.FAIL_USER_NOT_FOUND;
        try {
            subscriptions = users.get(userIndex).getSubscriptions();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            status = CRUDStatus.FAIL_MISCELLANEOUS_ERROR;
        }
        return new Pair<>(status, subscriptions);
    }

    public Pair<CRUDStatus, ArrayList<String>> listAllTopics() {
        CRUDStatus status = CRUDStatus.SUCCESS;
        ArrayList<String> topicNames = new ArrayList<>();
        try {
            for (Topic topic : topics) {
                topicNames.add(topic.getName());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            status = CRUDStatus.FAIL_MISCELLANEOUS_ERROR;
        }
        return new Pair<>(status, topicNames);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dataset dataset = (Dataset) o;
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
