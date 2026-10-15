package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuController {

    @FXML private Button btnCitas;
    @FXML private Button btnClientes;
    @FXML private Button btnSalir;

    @FXML
    void onIrClientes(ActionEvent event) {
        cambiarEscena(event, "/ni/edu/uam/evaluacion_javafx_citastecnicas/fxml/cliente-view.fxml", "Gestión de Clientes");
    }

    @FXML
    void onIrCitas(ActionEvent event) {
        cambiarEscena(event, "/ni/edu/uam/evaluacion_javafx_citastecnicas/fxml/cita-view.fxml", "Gestión de Citas");
    }

    @FXML
    void onSalir(ActionEvent event) {
        Platform.exit();
    }

    private void cambiarEscena(ActionEvent event, String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("TecnoAsistencia - " + titulo);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de navegación", "No se pudo cargar la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}