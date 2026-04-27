package org.hectora15.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryWindow) throws Exception {
        URL interfaceFile = getClass().getResource("/simulator_view.fxml");

        if (interfaceFile == null) {
            System.err.println("Critical Error: simulator_view.fxml file could not be found.");
            System.exit(1);
        }

        Parent root = FXMLLoader.load(interfaceFile);

        primaryWindow.setTitle("Network Traffic Simulator");
        primaryWindow.setScene(new Scene(root, 850, 650));
        primaryWindow.setMinWidth(850);
        primaryWindow.setMinHeight(650);
        primaryWindow.show();
    }


}