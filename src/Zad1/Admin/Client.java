package Zad1.Admin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    SocketChannel adminChannel;
    private int port;

    public Client(int port) throws IOException {
        this.port = port;
        adminChannel = SocketChannel.open();
        adminChannel.connect(new InetSocketAddress("localhost", port));
    }

    public void addTopic (String topic) throws IOException {
        String message = "ADDTOPIC " + topic + "\n";
        sendMessageToChannel(message);
    }

    public void removeTopic (String topic) throws IOException {
        String message = "REMOVETOPIC " + topic + "\n";
        sendMessageToChannel(message);
    }

    public void sendMessage (String topic, String message) throws IOException {
        String fullMessage = "SENDMESSAGE " + topic + " " + message + "\n";
        sendMessageToChannel(fullMessage);
    }

    private void sendMessageToChannel(String message) throws IOException {
        ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(message));
        adminChannel.write(byteBuffer);
    }
}
