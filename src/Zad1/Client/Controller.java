package Zad1.Client;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class Controller implements IController {

    public ListView<String> subscribedListView;
    public ListView<String> topicListView;
    public Button subscribeButton;
    public Button unsubscribeButton;
    public TextArea messageArea;
    public Client clientClient;

    @Override
    public void addTopic(String topic) {
        Platform.runLater(() -> {
            if (!topicListView.getItems().contains(topic)) {
                topicListView.getItems().add(topic);
            }
        });
    }

    @Override
    public void removeTopic(String topic) {
        Platform.runLater(() -> {
            topicListView.getItems().remove(topic);
            subscribedListView.getItems().remove(topic);
        });
    }

    @Override
    public void writeMessage(String topic, String message) {
        Platform.runLater(() -> {
            System.out.println("writeMessage called with topic: " + topic + " and message: " + message);
                messageArea.appendText("[" + topic + "] " + message + "\n");

        });
    }

    public void eventSubscribeTopic() {
        String selectedTopic = this.topicListView.getSelectionModel().getSelectedItem();
        if (selectedTopic != null && !subscribedListView.getItems().contains(selectedTopic)) {
            subscribedListView.getItems().add(selectedTopic);
            try {
                clientClient.subscribeThisTopic(selectedTopic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void eventUnsubscribeTopic() {
        String selectedTopic = this.subscribedListView.getSelectionModel().getSelectedItem();
        if (selectedTopic != null) {
            subscribedListView.getItems().remove(selectedTopic);
            try {
                clientClient.unsubscribeThisTopic(selectedTopic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}