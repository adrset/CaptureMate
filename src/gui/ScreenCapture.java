package gui;

import capturemate.InputParams;
import capturemate.net.ImageSendClient;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ScreenCapture extends Thread {
    Robot robot;
    private InputParams params;
    private ImageView imageView;
    private volatile boolean stop = false;
    private volatile boolean bigSize = true;
    ImageSendClient client;
    private String ip;

    public synchronized void stopCapturing(){
        this.stop = true;
    }

    public synchronized boolean toggleScaleFactorChange(){
        bigSize = !bigSize;
        return bigSize;

    }


    @Override
    public void run() {
        try {
            client.startConnection("localhost", 8965);
        } catch (Exception e) {

        }
        while (!this.stop){
            screenCapture();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ScreenCapture(ImageView imageView, String ip){
        try {
            this.ip = ip;
            this.imageView = imageView;
            this.params = InputParams.getInstance();
            this.robot = new Robot();
            client = new ImageSendClient();
        } catch(Exception e) {
            //
        }
    }

    public int screenCapture() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle rectangle = new Rectangle(dimension);
        BufferedImage screen = robot.createScreenCapture(rectangle);
        float scaleFactor = params.getScaleFactors()[bigSize ? 0 : 1];
        int height = (int) (params.getHeight()*scaleFactor);
        int width = (int) (params.getWidth()*scaleFactor);
        BufferedImage resized = new BufferedImage(width, height, screen.getType());
        Graphics2D g = resized.createGraphics();


        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(screen, 0, 0, width, height, 0, 0, screen.getWidth(),
                screen.getHeight(), null);
        g.dispose();

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            client.sendImage(resized);
        } catch (Exception e) {
            System.out.println("what" + e.getMessage());
            e.printStackTrace();
            try {
                client.startConnection(ip, 8965);
            } catch(Exception ee) {
            }
        }
        return 1;
    }
}
