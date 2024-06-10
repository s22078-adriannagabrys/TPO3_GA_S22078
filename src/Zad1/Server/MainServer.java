package Zad1.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class MainServer {

    private Selector selector = null;
    private ServerSocketChannel serverSocketChannel = null;
    private List<SocketChannel> channelList = new LinkedList<>();
    private Map<String, List<SocketChannel>> addressMap = new HashMap<>();

    public MainServer(int port) {
        try{
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress("localhost", port));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        serviceConnections();
    }
    private void sendTopicList(SocketChannel cc) throws IOException {
        StringBuilder topics = new StringBuilder("TOPICLIST ");
        for (String topic : addressMap.keySet()) {
            topics.append(topic).append(",");
        }
        // Remove the trailing comma
        if (topics.length() > 10) {
            topics.setLength(topics.length() - 1);
        }
        topics.append("\n");

        ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(topics));
        cc.write(byteBuffer);
    }


    private void serviceConnections() {
        boolean serverIsRunning = true;
        while(serverIsRunning) {
            try {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while(iter.hasNext()){
                    SelectionKey key = iter.next();
                    iter.remove();
                    if(key.isAcceptable()){
                        System.out.println("Accepting connection");
                        SocketChannel cc = serverSocketChannel.accept();
                        cc.configureBlocking(false);
                        cc.register(selector, SelectionKey.OP_READ);
                        channelList.add(cc);
                    }
                    if(key.isReadable()){
                        System.out.println("Reading data");
                        SocketChannel cc = (SocketChannel) key.channel();
                        serviceRequest(cc);
                    }
                }
            }
            catch(Exception exc){
                exc.printStackTrace();
            }
        }
    }

    private void serviceRequest(SocketChannel cc) throws IOException {
        StringBuilder request = new StringBuilder();
        readLoop:
        try {
            while (true) {
                ByteBuffer bbuf = ByteBuffer.allocate(1024);
                int n = cc.read(bbuf);
                if (n > 0) {
                    bbuf.flip();
                    CharBuffer cbuf = Charset.forName("ISO-8859-2").decode(bbuf);
                    while (cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        if (c == '\r' || c == '\n') break readLoop;
                        request.append(c);
                    }
                }
            }
        }catch (IOException ex){ //channel closed
            channelList.remove(cc);
            for (List<SocketChannel> socketList: addressMap.values()){
                socketList.remove(cc);
            }
            cc.close();
        }
        System.out.println(request);

        String[] messageArray = request.toString().split(" ");
        String header = messageArray[0];
        if (header.equals("ADDTOPIC")){
            System.out.println(header);
            String topic = messageArray[1];
            addressMap.put(topic, new LinkedList<>());
            for (SocketChannel sc : channelList){
                ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(request + "\n"));
                sc.write(byteBuffer);
            }

        }else if (header.equals("REMOVETOPIC")){
            String topic = messageArray[1];
            addressMap.remove(topic);
            for (SocketChannel sc : channelList){
                ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(request + "\n"));
                sc.write(byteBuffer);
            }
        } else if (header.equals("SUBSCRIBE")){
            String topic = messageArray[1];
            addressMap.get(topic).add(cc);

        } else if (header.equals("UNSUBSCRIBE")){
            String topic = messageArray[1];
            addressMap.get(topic).remove(cc);
        } else if (header.equals("SENDMESSAGE")){
            String topic = messageArray[1];
            String message = messageArray[2];
            if (addressMap.containsKey(messageArray[1])){
                for (SocketChannel sc : addressMap.get(topic)){
                    ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(request + "\n"));
                    sc.write(byteBuffer);
                }
            }
        } else if (header.equals("REQUESTTOPICLIST")) {
        sendTopicList(cc);
        }
    }

    public static void main(String[] args) {
        MainServer mainServer = new MainServer(Integer.parseInt(args[0]));
    }
}
