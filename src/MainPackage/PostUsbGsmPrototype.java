/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainPackage;

import Utilities.Workers.GSMModemHandler;
import Utilities.Workers.SMSPicker;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jssc.SerialPortException;

/**
 *
 * @author MSSM
 */
public class PostUsbGsmPrototype extends Application {
    
    @Override
    public void start(Stage primaryStage) throws SerialPortException, IOException {        
        BorderPane root = FXMLLoader.load(getClass().getResource("/MVC/Fxmls/MainStage.fxml"));
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Post-Gsm-Usb-Prototype");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(event -> { System.exit(0); });
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
