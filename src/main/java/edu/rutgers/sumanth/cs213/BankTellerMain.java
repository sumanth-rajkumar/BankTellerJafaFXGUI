/**
 * This class has the main function of the program. It starts the BankTellerView.fxml GUI by calling it
 * and sets the scene of the GUI with the specified dimensions and title.
 * @author Sumanth Rajkumar, Shantanu Jain
 */

package edu.rutgers.sumanth.cs213;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BankTellerMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(BankTellerMain.class.getResource("BankTellerView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);
        stage.setTitle("Welcome to Online Banking!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}