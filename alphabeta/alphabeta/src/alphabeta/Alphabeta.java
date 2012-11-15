/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author TheSpecialisT
 */
public class Alphabeta extends Application {
    
    public static void main(String[] args) {
        Application.launch(Alphabeta.class, args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
        
        stage.setScene(new Scene(root));
        stage.show();
    }
}
