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

    public ImageSendServer(ImageView view, int port) {
        this.imageView = view;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true)
                new EchoClientHandler(serverSocket.accept(), imageView).start();
        } catch(Exception e) {

        }

    }

    public void stopServer() throws Exception {
        serverSocket.close();
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
                out = new PrintWriter(clientSocket.getOutputStream(), true);
//                in = new BufferedReader(
//                        new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                int i =0;
                while (true) {
                    InputStream inputStream = clientSocket.getInputStream();

                    System.out.println("Reading: " + System.currentTimeMillis());

                    byte[] sizeAr = new byte[4];
                    inputStream.read(sizeAr);
                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                    byte[] imageAr = new byte[size];
                    inputStream.read(imageAr);

                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
                    if(image == null) {
                        break;
                    }
                    Image imageFX = SwingFXUtils.toFXImage(image, null);

                    Platform.runLater(() -> {
                        this.imageView.setImage(imageFX);
                    });


                }

                out.close();
                clientSocket.close();
            } catch(Exception e) {
                System.out.println("server" + e.getMessage());
            }
        }
    }

}