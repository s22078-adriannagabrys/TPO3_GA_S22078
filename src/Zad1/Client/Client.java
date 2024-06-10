package Zad1.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class Client {
    SocketChannel clientChannel;
    private int port;
    Thread thread;
    boolean go = true;
    IController iController;

    public Client(int port, IController iController) throws IOException {
        this.port = port;
        this.iController = iController;
        clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress("localhost", port));
        clientChannel.configureBlocking(false);
        Listener listener = new Listener();
        thread = new Thread(listener);
        thread.start();
    }
    public void requestTopicList() throws IOException {
        sendCommand("REQUESTTOPICLIST", "");
    }

    public void subscribeThisTopic(String topic) throws IOException {
        sendCommand("SUBSCRIBE", topic);
    }

    public void unsubscribeThisTopic(String topic) throws IOException {
        sendCommand("UNSUBSCRIBE", topic);
    }

    private void sendCommand(String command, String argument) throws IOException {
        String message = command + " " + argument + "\n";
        ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(message));
        clientChannel.write(byteBuffer);
    }

//    public void stopConnection() {
//        go = false;
//        try {
//            clientChannel.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public class Listener implements Runnable {

        @Override
        public void run() {
            Selector selector = null;
            try {
                selector = Selector.open();
                clientChannel.register(selector, SelectionKey.OP_READ);
                System.out.println("Starting listening...");
                while (go) {
                    try {
                        int selectionsNumber = selector.selectNow(); // Non-blocking operation to check loop condition
                        if (selectionsNumber == 0) {
                            continue;
                        }
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> iter = keys.iterator();
                        while (iter.hasNext()) {
                            SelectionKey key = iter.next();
                            iter.remove();
                            if (key.isReadable()) {
                                System.out.println("Reading data");
                                SocketChannel cc = (SocketChannel) key.channel();
                                serviceRequest(cc);
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void serviceRequest(SocketChannel cc) throws IOException {
            System.out.println("Reading request...");
            StringBuilder request = new StringBuilder();
            ByteBuffer bbuf = ByteBuffer.allocate(1024);
            while (cc.read(bbuf) > 0) {
                bbuf.flip();
                CharBuffer cbuf = Charset.forName("ISO-8859-2").decode(bbuf);
                request.append(cbuf);
                bbuf.clear();
            }

            System.out.println(request);
            String[] lines = request.toString().split("\n");
            for (String line : lines) {
                if (!line.isEmpty()) {
                    handleMessage(line.trim());
                }
            }
        }

        private void handleMessage(String message) {
            String[] messageArray = message.split(" ", 3);
            String header = messageArray[0];
            if (header.equals("ADDTOPIC")) {
                String topic = messageArray[1];
                iController.addTopic(topic);
            } else if (header.equals("REMOVETOPIC")) {
                String topic = messageArray[1];
                iController.removeTopic(topic);
            } else if (header.equals("SENDMESSAGE")) {
                String topic = messageArray[1];
                String msg = messageArray.length > 2 ? messageArray[2] : "";
                iController.writeMessage(topic, msg);
            } else if (header.equals("TOPICLIST")) {
                String[] topics = messageArray[1].split(",");
                for (String topic : topics) {
                    iController.addTopic(topic);
                }
            }
        }
    }
}
