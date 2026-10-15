package ni.edu.uam.evaluacion_javafx_citastecnicas.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Usar la ruta absoluta desde resources con la barra inicial '/'
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/ni/edu/uam/evaluacion_javafx_citastecnicas/fxml/menu-view.fxml"));

        // 2. Quitar el 320, 240 para que respete los 1100x700 que definiste en Scene Builder
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("TecnoAsistencia Nicaragua S.A.");
        stage.setScene(scene);

        // Opcional: Centrar la ventana en la pantalla
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}