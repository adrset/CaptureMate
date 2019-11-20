package gui;

import cgp.InputParams;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenCapture extends Thread {
    Robot robot;
    private InputParams params;
    private ImageView imageView;
    private volatile boolean stop = false;
    private volatile boolean bigSize = true;

    public synchronized void stopCapturing(){
        this.stop = true;
    }

    public synchronized boolean toggleScaleFactorChange(){
        bigSize = !bigSize;
        return bigSize;

    }


    @Override
    public void run() {
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
        } catch(Exception e) {
            //
        }
    }

    public void screenCapture() {
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

    }
}
