package Zad1.Admin;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Controller {

    public ListView topicListView;
    public Button addButton;
    public Button removeButton;
    public Button sendButton;
    public TextField messageField;
    public TextField topicField;
    public Client clientAdmin;

    public void eventAddTopic (){
        if(!topicField.getText().isEmpty()){
            String topic = topicField.getText();
            if(!topicListView.getItems().contains(topic)) {
                topicListView.getItems().add(topic);
                try {
                    clientAdmin.addTopic(topic);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void eventRemoveTopic (){
        if(topicListView.getItems().contains(this.topicListView.getSelectionModel().getSelectedItem())) {
            try {
                clientAdmin.removeTopic((String) this.topicListView.getSelectionModel().getSelectedItem());
            } catch (IOException e) {
                e.printStackTrace();
            }
            topicListView.getItems().remove(this.topicListView.getSelectionModel().getSelectedItem());
        }
    }
    public void eventSendMessage (){
        if(!messageField.getText().isEmpty()){
            String message = messageField.getText();
            try {
                clientAdmin.sendMessage((String) this.topicListView.getSelectionModel().getSelectedItem(), message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
