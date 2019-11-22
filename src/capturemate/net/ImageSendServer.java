package capturemate.net;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ImageSendServer extends Thread{
    private ImageView imageView;
    private int port;
    private ServerSocket serverSocket;
    private volatile boolean stop = false;
    private EchoClientHandler ev;
    public ImageSendServer(ImageView view, int port) {
        this.imageView = view;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (!stop){
                ev = new EchoClientHandler(serverSocket.accept(), imageView);
                ev.setDaemon(true);
                ev.start();
            }
            serverSocket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void stopServer() throws Exception {
        if(serverSocket != null){
            //
            stop = true;
            if (ev.clientSocket != null)
                ev.clientSocket.close();
        }
    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private ImageView imageView;
        public EchoClientHandler(Socket socket, ImageView view) {
            this.clientSocket = socket;
            this.imageView = view;
        }

        public void run() {
            try {

                while (true) {

                    InputStream inputStream = clientSocket.getInputStream();

                    byte[] sizeAr = new byte[4];

                    inputStream.read(sizeAr);

                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                    if (size < 0) {
                        continue;
                    }

                    byte[] byteArray = new byte[1024];
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int total = 0;
                    int readBytes = 0;

                    while (total < size && (readBytes = inputStream.read(byteArray)) != -1) {
                        bos.write(byteArray, 0, readBytes);
                        total += readBytes;
                    }

                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(bos.toByteArray()));
                    if (image == null) {
                        continue;
                    }

                    Image imageFX = SwingFXUtils.toFXImage(image, null);
                    Platform.runLater(() -> {
                        this.imageView.setImage(imageFX);
                    });
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}