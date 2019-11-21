package capturemate.net;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ImageSendServer {
    private ServerSocket serverSocket;
    public void start(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        while (true)
            new EchoClientHandler(serverSocket.accept()).start();
    }

    public void stop() throws Exception {
        serverSocket.close();
    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
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
                    ImageIO.write(image, "jpg", new File("output" + i++ + ".jpg"));

                    out.println("GOTCHA");

                }

                out.close();
                clientSocket.close();
            } catch(Exception e) {
                System.out.println("server" + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ImageSendServer a = new ImageSendServer();
        try {
            a.start(8965);

        } catch(Exception e) {

        }
    }

}