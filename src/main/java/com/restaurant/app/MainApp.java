package com.restaurant.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyCombination;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/fxml/login.fxml")
        );

        Parent root = loader.load();
        Scene scene = new Scene(root);

        // 🎨 CSS global
        scene.getStylesheets().add(
                getClass()
                        .getResource("/css/style.css")
                        .toExternalForm()
        );

        stage.setTitle("Gestion de Restaurant");

        // 🖥️ Plein écran
        stage.setScene(scene);
        stage.setMaximized(true);           // plein écran fenêtré
        stage.setFullScreen(true);          // vrai plein écran
        stage.setFullScreenExitHint("");    // cacher message
        stage.setFullScreenExitKeyCombination(
                KeyCombination.NO_MATCH
        );

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
