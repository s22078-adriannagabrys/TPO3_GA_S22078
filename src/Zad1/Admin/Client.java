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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ADDTOPIC");
        stringBuilder.append(" ");
        stringBuilder.append(topic);
        stringBuilder.append("\n");
        ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(stringBuilder));
        adminChannel.write(byteBuffer);
    }

    public void removeTopic (String topic) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("REMOVETOPIC");
        stringBuilder.append(" ");
        stringBuilder.append(topic);
        stringBuilder.append("\n");
        ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(stringBuilder));
        adminChannel.write(byteBuffer);

    }
    public void sendMessage (String topic, String message) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SENDMESSAGE");
        stringBuilder.append(" ");
        stringBuilder.append(topic);
        stringBuilder.append(" ");
        stringBuilder.append(message);
        stringBuilder.append("\n");
        ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(stringBuilder));
        adminChannel.write(byteBuffer);

    }


}
