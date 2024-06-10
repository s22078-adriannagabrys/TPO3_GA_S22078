package Zad1.Client;

import Zad1.Client.Client;
import Zad1.Client.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {
    private Client client;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GUI.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);
        Controller controller = fxmlLoader.getController();
        client = new Client(
                Integer.parseInt(getParameters().getUnnamed().get(0)),
                controller);
        controller.clientClient = client;
        client.requestTopicList();
        primaryStage.show();
    }

//    @Override
//    public void stop() {
//        if (client != null) {
//            client.stopConnection();
//        }
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
