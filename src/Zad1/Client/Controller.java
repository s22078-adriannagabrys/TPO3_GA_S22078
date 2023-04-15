package Zad1.Client;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class Controller implements IController{

    public ListView subscribedListView;
    public ListView topicListView;
    public Button subscribeButton;
    public Button unsubscribeButton;
    public Button sendButton;
    public TextArea messageArea;
    public Client clientClient;


    public void addTopic (String topic){
        Platform.runLater(() -> {
            if (!topicListView.getItems().contains(topic)) {
                topicListView.getItems().add(topic);
            }
        });
    }
    public void removeTopic (String topic) {
        Platform.runLater(() -> {
            topicListView.getItems().remove(topic);
            subscribedListView.getItems().remove(topic);
        });
    }
    public void writeMessage (String topic, String message) {
        Platform.runLater(() -> {
            messageArea.setText(message);
        });
    }


    public void eventSubscribeTopic (){
        subscribedListView.getItems().add(this.topicListView.getSelectionModel().getSelectedItem());
        try {
            clientClient.subscribeThisTopic((String) this.topicListView.getSelectionModel().getSelectedItem());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eventUnsubscribeTopic (){
        subscribedListView.getItems().remove(this.topicListView.getSelectionModel().getSelectedItem());
        try {
            clientClient.unsubscribeThisTopic((String) this.topicListView.getSelectionModel().getSelectedItem());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
