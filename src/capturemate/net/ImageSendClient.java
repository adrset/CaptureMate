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

        private double MAX_FPS = 60;

        public void closeSocket() throws Exception{
            clientSocket.close();
        }


        public void startConnection(String ip, int port) throws Exception {
            clientSocket = new Socket(ip, port);
        }

        public String sendImage(BufferedImage imgstream) throws Exception {
            long time1 = System.nanoTime();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(imgstream, "jpeg", byteArrayOutputStream);
            try {
                OutputStream outputStream = clientSocket.getOutputStream();

                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

                byte[] data = byteArrayOutputStream.toByteArray();
                ByteArrayInputStream ins = new ByteArrayInputStream(data);
                byte[] buffer = new byte[1024]; // or 4096, or more
                int count;
                outputStream.write(size);
                outputStream.flush();

                int sum=0;
                while ((count = ins.read(buffer)) > 0) {
                    sum += count;
                    outputStream.write(buffer, 0, count);
                }

                outputStream.flush();
                long time2 = System.nanoTime();
                double elapsed = (time2 - time1) / Math.pow(10, 9);
                if (elapsed < 1/MAX_FPS) {
                    Thread.sleep((long) ((1.0/MAX_FPS - elapsed) * 1000.0));
                }
            } catch (Exception e){
               e.printStackTrace();
            }


            //String resp = in.readLine();
            return "";
        }

        public void stopConnection() throws Exception{

            clientSocket.close();
        }
    }
