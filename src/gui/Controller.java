package gui;

import capturemate.InputParams;
import capturemate.net.ImageSendServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;


import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Controller {
    private boolean isHidden = false;
    Stage primaryStage;
    @FXML
    private Button closeButton;
    @FXML
    private Button resizeButton;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField ipTextField;
    @FXML
    private Pane connectContainer;

    private String DEFAULT_IP = "127.0.0.1";

    private ImageSendServer server;
    private static double xOffset = 0;
    private static double yOffset = 0;
    private InputParams params;
    private ScreenCapture screenCapture;
    String ip = "127.0.0.1";
    @FXML
    private BorderPane anchorPane;
    KeyCombination cntrlZ = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_ANY);

    public Controller(){

        this.params = InputParams.getInstance();

    }

    @FXML
    public void onConnect(){

        System.out.println(ip);
        screenCapture = new ScreenCapture(imageView, ip);
        screenCapture.setDaemon(true);
        screenCapture.start();
        connectContainer.setVisible(false);
    }


    public void setPrimaryStage(Stage s) {
        this.primaryStage = s;
    }

    @FXML
    public void initialize() {

        imageView.setFitHeight(params.getHeight());
        imageView.setFitWidth(params.getWidth());

        anchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = primaryStage.getX() - event.getScreenX();
                yOffset = primaryStage.getY() - event.getScreenY();
            }
        });

        anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() + xOffset);
                primaryStage.setY(event.getScreenY() + yOffset);
            }
        });

        anchorPane.setOnMouseEntered(new EventHandler<MouseEvent>
                () {

            @Override
            public void handle(MouseEvent t) {
                anchorPane.setStyle("-fx-effect: innershadow(gaussian, rgba( 221, 221, 0, 0.5 ), 2, 1.0, 0, 0);");
                closeButton.setOpacity(1);
                resizeButton.setOpacity(1);

            }
        });

        anchorPane.setOnMouseExited(t -> {
            anchorPane.setStyle("-fx-effect: innershadow(gaussian, rgba( 221, 221, 0, 0 ), 2, 1.0, 0, 0);");
            closeButton.setOpacity(0);
            resizeButton.setOpacity(0);


        });
        anchorPane.setOnKeyPressed(event -> {
            if(cntrlZ.match(event)) {
                primaryStage.setOpacity(isHidden ? 1.0 : 0.0);
                isHidden = !isHidden;
            }
        });

        server = new ImageSendServer(imageView,8965);
        try {
            server.setDaemon(true);
            server.start();
        } catch (Exception e) {

        }
        closeButton.setOpacity(0);
        resizeButton.setOpacity(0);
        ipTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.ip = newValue;
        });
    }

    public void clearThreads(){
        try {
            if (screenCapture !=  null)
                screenCapture.stopCapturing();

            server.stopServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void onCloseButton(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void onResizeButton(ActionEvent actionEvent) {
        boolean value = screenCapture.toggleScaleFactorChange();
        float scaleFactor = params.getScaleFactors()[value ? 0 : 1];

        primaryStage.setWidth(params.getWidth()*scaleFactor);
        primaryStage.setHeight(params.getHeight()*scaleFactor);
        imageView.setFitHeight(params.getHeight()*scaleFactor);
        imageView.setFitWidth(params.getWidth()*scaleFactor);

    }
}
