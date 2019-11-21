package capturemate.net;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ImageSendClient {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public void closeSocket() throws Exception{
            clientSocket.close();
        }


        public void startConnection(String ip, int port) throws Exception {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public String sendImage(BufferedImage imgstream) throws Exception {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(imgstream, "jpg", byteArrayOutputStream);
            try {
                OutputStream outputStream = clientSocket.getOutputStream();
                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                outputStream.write(size);
                outputStream.write(byteArrayOutputStream.toByteArray());
                outputStream.flush();
            } catch (Exception e){

            }

            //String resp = in.readLine();
            return "";
        }

        public void stopConnection() throws Exception{
            in.close();
            out.close();
            clientSocket.close();
        }
    }
