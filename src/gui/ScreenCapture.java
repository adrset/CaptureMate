package gui;

import capturemate.InputParams;
import capturemate.net.ImageSendClient;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenCapture extends Thread {
    Robot robot;
    private InputParams params;
    private ImageView imageView;
    private volatile boolean stop = false;
    private volatile boolean bigSize = true;
    ImageSendClient client;

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

    public ScreenCapture(ImageView imageView){
        try {
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
        int height = (int) (params.getWidth()*scaleFactor);
        int width = (int) (params.getHeight()*scaleFactor);
        BufferedImage resized = new BufferedImage(width, height, screen.getType());
        Graphics2D g = resized.createGraphics();


        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(screen, 0, 0, width, height, 0, 0, screen.getWidth(),
                screen.getHeight(), null);
        g.dispose();
        Image image = SwingFXUtils.toFXImage(resized, null);

        Platform.runLater(() -> {
            imageView.setImage(image);
        });

        try {
            String response = client.sendMessage("hello server");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            try {
                client.startConnection("localhost", 8965);
            } catch(Exception ee) {
                System.out.println("retryy" + e.getMessage());
            }
        }
        return 1;
    }
}
