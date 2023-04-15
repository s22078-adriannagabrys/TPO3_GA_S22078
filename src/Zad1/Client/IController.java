package Zad1.Client;

public interface IController {
    public void addTopic (String topic);
    public void removeTopic (String topic);
    public void writeMessage (String topic, String message);
}
