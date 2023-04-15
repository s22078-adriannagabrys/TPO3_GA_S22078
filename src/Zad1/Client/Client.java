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

    public void subscribeThisTopic(String topic) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SUBSCRIBE");
        stringBuilder.append(" ");
        stringBuilder.append(topic);
        stringBuilder.append("\n");
        ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(stringBuilder));
        clientChannel.write(byteBuffer);
    }

    public void unsubscribeThisTopic(String topic) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UNSUBSCRIBE");
        stringBuilder.append(" ");
        stringBuilder.append(topic);
        stringBuilder.append("\n");
        ByteBuffer byteBuffer = Charset.forName("ISO-8859-2").encode(CharBuffer.wrap(stringBuilder));
        clientChannel.write(byteBuffer);
    }

    public class Listener implements Runnable{

        @Override
        public void run() {
            Selector selector = null;
            try {
                selector = Selector.open();
                clientChannel.register(selector, SelectionKey.OP_READ);
                System.out.println("Starting listening...");
                while(go) {
                    try {
                        int selectionsNumber = selector.selectNow(); //operacja nieblokująca aby sprawdzic warunek pętli
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
            readLoop:
            while(true){
                ByteBuffer bbuf = ByteBuffer.allocate(1024);
                int n = clientChannel.read(bbuf);     // nie natrafimy na koniec wiersza
                if (n > 0) {
                    bbuf.flip();
                    CharBuffer cbuf = Charset.forName("ISO-8859-2").decode(bbuf);
                    while(cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        if (c == '\r' || c == '\n') break readLoop;
                        request.append(c);
                    }
                }
            }
            System.out.println(request);
            String[] messageArray = request.toString().split(" ");
            String header = messageArray[0];
            if (header.equals("ADDTOPIC")){
                String topic = messageArray[1];
                iController.addTopic(topic);
            }else if (header.equals("REMOVETOPIC")){
                String topic = messageArray[1];
                iController.removeTopic(topic);
            }else if (header.equals("SENDMESSAGE")) {
                String topic = messageArray[1];
                String message = messageArray[2];
                iController.writeMessage(topic, message);
            }
        }
    }
}
